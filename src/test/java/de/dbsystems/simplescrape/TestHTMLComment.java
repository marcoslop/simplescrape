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

public class TestHTMLComment {

    @Test
    public void testComment() {
        HTMLComment comment = new HTMLComment(" This is TextToken ");
        assertNotNull(comment.getComment());
    }

    @Test
    public void testGetComment() {
        HTMLComment comment = new HTMLComment("This is TextToken");
        assertEquals(comment.getComment(), "This is TextToken");
    }

    @Test
    public void testToString() {
        HTMLComment comment = new HTMLComment("This is TextToken");
        assertEquals(comment.toString(), "<!--This is TextToken-->");
    }

}
