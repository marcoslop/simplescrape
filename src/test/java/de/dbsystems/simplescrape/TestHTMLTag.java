package de.dbsystems.simplescrape;

import static org.junit.Assert.*;
import org.junit.Test;

/* This software is provided "AS IS," without a warranty of any kind.
 * 
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.
 * DB Systems GmbH AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * IN NO EVENT WILL DB Systems GmbH OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
 * AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE
 * OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF DB Systems GmbH HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

public class TestHTMLTag {

    @Test
    public void testTag() {
        HTMLTag tag = new HTMLTag("p");
        assertEquals(tag.getName(), "p");
        tag = new HTMLTag("/p");
        assertEquals(tag.getName(), "p");
        tag = new HTMLTag("!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
        assertEquals(tag.getName(), "!DOCTYPE");
        tag = new HTMLTag("");
        assertEquals(tag.getName(), "");
        tag = new HTMLTag("    ");
        assertEquals(tag.getName(), "");
        tag = new HTMLTag("/");
        assertEquals(tag.getName(), "");
        tag = new HTMLTag("//");
        assertEquals(tag.getName(), "");
        tag = new HTMLTag("/ p");
        assertEquals(tag.getName(), "p");
        tag = new HTMLTag("meta  /");
        assertEquals(tag.getName(), "meta");
    }

    @Test
    public void testUnaryAndClosingTags() {
        HTMLTag tag = new HTMLTag("br");
        assertFalse(tag.isUnaryTag());
        assertFalse(tag.isEndTag());

        tag = new HTMLTag("/p");
        assertFalse(tag.isUnaryTag());
        assertTrue(tag.isEndTag());

        tag = new HTMLTag("br/");
        assertTrue(tag.isUnaryTag());
        assertFalse(tag.isEndTag());

        tag = new HTMLTag("meta /");
        assertTrue(tag.isUnaryTag());
        assertFalse(tag.isEndTag());

        tag = new HTMLTag("/ image");
        assertTrue(tag.isEndTag());
        assertFalse(tag.isUnaryTag());
    }

    @Test
    public void testToString() {
        HTMLTag tag = new HTMLTag("p");
        assertEquals(tag.toString(), "<p>");
        tag = new HTMLTag("/p");
        assertEquals(tag.toString(), "</p>");
        tag = new HTMLTag("!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
        assertEquals(tag.toString(), "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    }

    @Test
    public void testGetAttributes() {
        HTMLTag tag = new HTMLTag("body bgcolor=\"#ffffff\"");
        assertNotNull(tag.getAttributes());
        tag = new HTMLTag("hr size=1 noshade");
        assertNotNull(tag.getAttributes());
        tag = new HTMLTag("br");
        assertNull(tag.getAttributes());
    }

}
