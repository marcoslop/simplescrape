package de.dbsystems.simplescrape;


import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Vector;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
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

public class TestScraper {
    static Scraper scraper = null;
    static ScrapeOptions options = null;
    public final static String INPUT_FILE = "test.html";

    @Before
    public void setUp() throws Exception {
        try {
            InputStream responseStream = getClass().getClassLoader().getResourceAsStream(INPUT_FILE);
            Tokenizer tokenizer = new Tokenizer(responseStream);
            scraper = new Scraper();
            scraper.setTokenizerAndParse(tokenizer);
            responseStream.close();

        } catch (IOException e) {
            System.err.println("IOException:" + e.getMessage());
        }

        options = new ScrapeOptions();
        scraper.reset();
    }

    @Test
    public void testGetCurrentMarker() {
        scraper.setPosition(10);
        assertEquals(scraper.getPosition(), 10);
    }

    @Test
    public void testSetCurrentMarker() {
        scraper.setPosition(10);
        assertEquals(scraper.getPosition(), 10);
    }

    @Test
    public void testSetMarkerStart() {
        scraper.setPosition(10);
        assertTrue(scraper.getPosition() != 0);
        scraper.reset();
        assertEquals(scraper.getPosition(), 0);
    }

    @Test
    public void testGetTokenizer() {
        assertTrue(scraper.getTokenizer() instanceof Tokenizer);
   		assertNotNull(scraper.getTokenizer());
    }

    @Test
    public void testSearchToken() {
        int position = scraper.indexOf(new HTMLTag("div id=\"header\""), options);
        assertTrue(position >= 0);
    }

    @Test
    public void testSearchTokenChainVectorOfElementScrapeOptions() {
        Vector<AbstractHTMLToken> searchElements = new Vector<AbstractHTMLToken>();
        
        // 1. Suche nach einem beliebigen HtmlTag
        searchElements.add(new HTMLTag("div id=\"header\""));
        int position = scraper.searchTokens(searchElements, options);
        assertTrue(position >= 0);

        // 2. Search for a couple tags directly adjacent
        searchElements.clear();
        searchElements.add(new HTMLTag("div class=\"hddSkipLink\""));
        searchElements.add(new HTMLTag("a href=\"#start\" accesskey=\"0\""));
        searchElements.add(new HTMLTag("/a"));
        searchElements.add(new HTMLTag("/div"));
        scraper.reset();
        position = scraper.searchTokens(searchElements, options);
        assertTrue(position >= 0);
        
        // 3. Search for something with a regular expression
        searchElements.clear();
        searchElements.add(new HTMLTag("td rowspan=\"2\""));
        searchElements.add(new HTMLTag("label for=\"time\""));
        searchElements.add(new RegExTextToken("U[a-z]+"));
        searchElements.add(new HTMLTag("/label"));
        searchElements.add(new HTMLTag("/td"));
        scraper.reset();
        position = scraper.searchTokens(searchElements, options);
        assertTrue(position >= 0);
        assertEquals(((TextToken)scraper.get(position - 3)).getText(), "Uhrzeit");

        // 4. Search for tags with whitespace-tokens inbetween. Do this twice with different search options.
        // This test is particularly difficult, as in our test-data the tag [div class="appFrame"] appears
        // multiple times and we are not searching for the first occurrence.
        searchElements.clear();
        searchElements.add(new HTMLTag("div class=\"appFrame\""));
        searchElements.add(new HTMLTag("div class=\"fahrplan\""));
        scraper.reset();
        options.elementOrder = ScrapeOptions.ELEMENT_ORDER_WHITESPACE_ALLOWED;
        position = scraper.searchTokens(searchElements, options);
        assertTrue(position >= 0);
        scraper.reset();
        options.elementOrder = ScrapeOptions.ELEMENT_ORDER_STRICT;
        position = scraper.searchTokens(searchElements, options);
        assertFalse(position >= 0);
        
        // 5. Search for tags with whitespace-tokens and comments inbetween. Do this twice with different
		// search options.
        searchElements.clear();
        searchElements.add(new HTMLTag("script language=\"JavaScript\" type=\"text/javascript\""));
        searchElements.add(new HTMLTag("/script"));
        scraper.reset();
        options.elementOrder = ScrapeOptions.ELEMENT_ORDER_COMMENTS_ALLOWED;
        position = scraper.searchTokens(searchElements, options);
        assertTrue(position >= 0);
        scraper.reset();
        options.elementOrder = ScrapeOptions.ELEMENT_ORDER_STRICT;
        position = scraper.searchTokens(searchElements, options);
        assertFalse(position >= 0);

    }

    /** Hier nur kurzer Test, ob gezieltes Positionssetzen funktioniert.
     * Sonstige Tests werden in testSearchTokenChainVectorOfElementScrapeOptions
     * gemacht.
     */
    @Test
    public void testSearchTokenChainMarkerVectorOfElementScrapeOptions() {
        Vector<AbstractHTMLToken> searchElements = new Vector<AbstractHTMLToken>();
        searchElements.add(new HTMLTag("html lang=\"de\""));
        int position = scraper.searchTokens(searchElements, options);
        assertTrue(position >= 0);
        position = scraper.searchTokens(100, searchElements, options);
        assertTrue(position < 0);
    }

    
    @Test
    public void testGetNextTextElement() {
        Vector<AbstractHTMLToken> searchElements = new Vector<AbstractHTMLToken>();
        String tagContent = "div id=\"footerlogos\"";
        searchElements.add(new HTMLTag(tagContent));
        int position = scraper.searchTokens(searchElements, options);
        assertEquals(scraper.get(position - 1).toString(), "<" + tagContent + ">");
        position -= 9;

        scraper.setPosition(position);
        TextToken text = scraper.getNextText(false);
        assertEquals(text.getText(), "\n\n\t\n\n\t\t");
 
        scraper.setPosition(position);
        text = scraper.getNextText(true);
        assertEquals(text.getText(), "&nbsp;");
    }
    
    @Test
    public void testGetNextTag() {
        Vector<AbstractHTMLToken> searchElements = new Vector<AbstractHTMLToken>();
        String tagContent = "div id=\"footerlogos\"";
        searchElements.add(new HTMLTag(tagContent));
        int position = scraper.searchTokens(searchElements, options);
        assertEquals(scraper.get(position - 1).toString(), "<" + tagContent + ">");
        position -= 6;

        scraper.setPosition(position);
        HTMLTag tag = scraper.getNextTag();
        assertEquals(tag.toString(), "<div class=\"breaker\">");

        scraper.setPosition(position-20);
        tag = scraper.getNextTag(position);
        assertEquals(tag.toString(), "<div class=\"breaker\">");
    }
}
