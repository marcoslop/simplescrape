package de.dbsystems.simplescrape;

import java.util.Enumeration;
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

public class TestHTMLTagAttributes {
	private final static String MAIN_TEST_STRING = " size=\"1\"  noshade width=1 other Depth=2 ";

	@Test
	public void testExists() {
		HTMLTagAttributes attrs = new HTMLTagAttributes(null);
		assertFalse(attrs.exists("xyz"));
		attrs = new HTMLTagAttributes("");
		assertFalse(attrs.exists("xyz"));
		assertFalse(attrs.exists(""));
		attrs = new HTMLTagAttributes(MAIN_TEST_STRING);
		assertTrue(attrs.exists("size"));
		assertTrue(attrs.exists("noshade"));
		assertTrue(attrs.exists("other"));
		assertTrue(attrs.exists("Depth"));
	}

	@Test
	public void testGet() {
		HTMLTagAttributes attrs = new HTMLTagAttributes(null);
		assertNull(attrs.get("xyz"));
		attrs = new HTMLTagAttributes("");
		assertNull(attrs.get("xyz"));
		assertNull(attrs.get(""));
		attrs = new HTMLTagAttributes(MAIN_TEST_STRING);
		assertEquals(attrs.get("size"), "1");
		assertEquals(attrs.get("noshade"), "");
		assertEquals(attrs.get("other"), "");
        assertEquals(attrs.get("Depth"), "2");
        assertEquals(attrs.get("width"), "1");

		attrs = new HTMLTagAttributes("some");
		assertEquals(attrs.get("some"), "");
		attrs = new HTMLTagAttributes("some=thing");
		assertEquals(attrs.get("some"), "thing");
		attrs = new HTMLTagAttributes("some='thing'");
		assertEquals(attrs.get("some"), "thing");
		attrs = new HTMLTagAttributes("some=\"thing\"");
		assertEquals(attrs.get("some"), "thing");
	}

	@Test
	public void testToString() {
		HTMLTagAttributes attrs = new HTMLTagAttributes(MAIN_TEST_STRING);
		assertEquals(attrs.toString(), MAIN_TEST_STRING.trim());
	}

	@Test
	public void testGetAttributeNames() {
		HTMLTagAttributes attrs = new HTMLTagAttributes(MAIN_TEST_STRING);
		Enumeration<String> names = attrs.getAttributeNames();
		for (int i = 0; i < attrs.getSize(); i++) {
			assertTrue(names.hasMoreElements());
			assertNotNull(names.nextElement());
		}
		
		attrs = new HTMLTagAttributes("");
		names = attrs.getAttributeNames();
		assertFalse(names.hasMoreElements());
	}

	@Test
	public void testGetSize() {
		HTMLTagAttributes attrs = new HTMLTagAttributes(MAIN_TEST_STRING);
		assertEquals(attrs.getSize(), 5);
		attrs = new HTMLTagAttributes("");
		assertEquals(attrs.getSize(), 0);
		attrs = new HTMLTagAttributes(" ");
		assertEquals(attrs.getSize(), 0);
		attrs = new HTMLTagAttributes(null);
		assertEquals(attrs.getSize(), 0);
		attrs = new HTMLTagAttributes("temp");
		assertEquals(attrs.getSize(), 1);

		attrs = new HTMLTagAttributes("some");
		assertEquals(attrs.getSize(), 1);
		attrs = new HTMLTagAttributes("some=thing");
		assertEquals(attrs.getSize(), 1);
		attrs = new HTMLTagAttributes("some='thing'");
		assertEquals(attrs.getSize(), 1);
		attrs = new HTMLTagAttributes("some=\"thing\"");
		assertEquals(attrs.getSize(), 1);
	
        attrs = new HTMLTagAttributes("some==thing");
        assertEquals(attrs.getSize(), 1);
        attrs = new HTMLTagAttributes("some=====thing");
        assertEquals(attrs.getSize(), 1);
        attrs = new HTMLTagAttributes("=");
        assertEquals(attrs.getSize(), 0);
        attrs = new HTMLTagAttributes("=some=thing");
        assertEquals(attrs.getSize(), 1);
        attrs = new HTMLTagAttributes("=====");
        assertEquals(attrs.getSize(), 0);
        attrs = new HTMLTagAttributes("some = thing");
        assertEquals(attrs.getSize(), 1);
        attrs = new HTMLTagAttributes("some = 'thing'");
        assertEquals(attrs.getSize(), 1);
		attrs = new HTMLTagAttributes("===");
		assertEquals(attrs.getSize(), 0);
		attrs = new HTMLTagAttributes("some=thing=");
		assertEquals(attrs.getSize(), 1);
	}

}
