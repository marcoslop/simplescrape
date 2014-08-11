package de.dbsystems.simplescrape;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;

/**
 * Central class for this package. This class supports more compact descriptions of things to find
 * in a given webpage. Instead of checking in code whether "this and this is followed by that and that"
 * one can provide expressions of things to watch for.
 * 
 * This class is not thread-safe.
 * 
 * @author Ronald Bieber, DB Systems GmbH
 * @since 04.04.2007
 */

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

public class Scraper implements Iterator {
    private Tokenizer tokenizer = null;

    private int current = 0;

    private ArrayList<AbstractHTMLToken> elements = null;

    /**
     * Empty constructor, does nothing.
     *
     */
    public Scraper() {
        
    }
    
    /**
     * Convenience-Constructor. Takes the input stream, wraps a Tokenizer around it and
     * parses the stream completely. Afterwards, input can be closed.
     * @param input An InputStream to be parsed.
     */
    public Scraper(InputStream input) {
        try {
            Tokenizer tokenizer = new Tokenizer(input);
            setTokenizerAndParse(tokenizer);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    /**
     * Returns the current position as an index into the list of tokens.
     * 
     * @return The current position.
     */
    public int getPosition() {
        return current;
    }

    /**
     * Set the current position to be used for subsequent searches.
     * 
     * @param current
     *            The new position.
     */
    public void setPosition(int current) {
        this.current = current;
    }

    /**
     * Sets the marker to the first token. Subsequent searches start there (unless specified otherwise).
     *
     */
    public void reset() {
        this.current = 0;
    }

    /**
     * Returns the currently used tokenizer.
     * 
     * @return The tokenizer.
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Sets the tokenizer to be used for this scraping experience. This leads to
     * an immediate reading of the complete HTML-file into elements. The input
     * stream can afterwards be closed.
     * 
     * @param tokenizer
     *            The tokenizer.
     */
    public void setTokenizerAndParse(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        if (elements == null) {
            elements = new ArrayList<AbstractHTMLToken>();
        } else {
            elements.clear();
        }
        long time = System.currentTimeMillis();
        try {
            AbstractHTMLToken next = tokenizer.readElement();
            while (next != null) {
                elements.add(next);
                next = tokenizer.readElement();
            }
            setPosition(0);
        } catch (IOException e) {
            System.err.println("couldn't parse input stream: "+e.getMessage());
        }
    }

    /**
     * Checks if a given HtmlToken is considered whitespace.
     * All elements are considered whitespace iff they are a text-token and
     * contain only blanks, carriage returns (0x0A and 0x0D) and tab-characters. 
     * @param here The element to be tested.
     * @return Whether this is whitespace or not.
     */
    private boolean isWhitespace(AbstractHTMLToken here) {
        if (!(here instanceof TextToken)) {
            return false;
        }
        String content = ((TextToken) here).getText();
        for (int i = 0; i < content.length(); i++) {
            switch (content.charAt(i)) {
            case ' ':
            case '\n':
            case '\r':
            case '\t':
                // do nothing, continue scan
                break;
            default:
                // anything else: check failed!
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the text contained in the next tag of a specified kind. The current marker
     * in the scraper gets pushed forward, if a result could be found. It will point
     * to the closing tag of the found container. Typical uses of
     * this will search of a table cell, div-container, paragraph or heading.
     * If other tags appear between the opening and closing tag, they will be ignored,
     * but text contained therein gets concatenated.
     * 
     * @param tagName The tag name to be searched for.
     * @return The text, or null, if no appropriate tag could be found.
     */
    public String getNextContent(String tagName) {
        ScrapeOptions options = new ScrapeOptions();
        options.attributesStrict = false;
        HTMLTag from = new HTMLTag(tagName);
        int start = indexOf(from, options);
        if (start < 0) {
            return null;
        }
        HTMLTag to = new HTMLTag("/" + tagName);
        int end = indexOf(to, options);
        if (end < 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        for (int i = start + 1; i < end; i++) {
            AbstractHTMLToken token = get(i);
            if (token instanceof TextToken) {
                buf.append(((TextToken)token).getText());
            }
        }
        return buf.toString();
    }
        

    private boolean canAdvanceAnyway(AbstractHTMLToken here, AbstractHTMLToken there,
            ScrapeOptions options) {
        if (!(there instanceof HTMLTag)) {
            return false;
        }
        switch (options.elementOrder) {
        case ScrapeOptions.ELEMENT_ORDER_WHITESPACE_ALLOWED:
            return isWhitespace(here);
        case ScrapeOptions.ELEMENT_ORDER_COMMENTS_ALLOWED:
            return ((here instanceof HTMLComment) || isWhitespace(here));
        case ScrapeOptions.ELEMENT_ORDER_ELEMENTS_ALLOWED:
            return true;
        default:
            return false;
        }
    }

    /**
     * Searches in the current data for a token as provided. The
     * search starts at the last used position as returned by
     * getCurrentMarker(). Regardless of options.advance, the current marker will
     * only be pushed forward, if the requested token could be found.
     * 
     * @param searchToken
     *            The token to be searched for. Must not be null.
     * @param options
     *            The options to be used for the search.
     * @see ScrapeOptions
     * @return An index pointing to the found token,
     * or -1, if the token could not be found.
     */
    public int indexOf(AbstractHTMLToken searchToken, ScrapeOptions options) {
        return indexOf(getPosition(), searchToken, options);
    }

    /**
     * Searches in the current data for a token as provided. The
     * search starts at the last used position as returned by
     * getCurrentMarker(). Regardless of options.advance, the current marker will
     * only be pushed forward, if the requested token could be found.
     * 
     * @param startHere
     *            The position from where on the search should be performed.
     * @param searchToken
     *            The token to be searched for. Must not be null.
     * @param options
     *            The options to be used for the search.
     * @see ScrapeOptions
     * @return An index pointing to the found token, or -1,
     *  if the sequence could not be found.
     */
    public int indexOf(int startHere, AbstractHTMLToken searchToken, ScrapeOptions options) {
        if (searchToken == null) {
            throw new IllegalArgumentException(
                    "searchToken must not be null");
        }
        int position = startHere;
        boolean found = false;
        while (!found && position < elements.size()) {
            AbstractHTMLToken here = elements.get(position);
            if (here.match(searchToken, options)) {
                found = true;
            } else {
                position++;
            }
        }
        if (found) {
            if (options.advance) {
                setPosition(position);
            }
            return position;
        } else {
            return -1;
        }
    }

    /**
     * Searches in the current data for a sequence of tokens as provided. The
     * search starts at the last used position as returned by
     * getCurrentMarker(). Regardless of options.advance, the current marker will
     * only be pushed forward, if the requested pattern could be found.
     * 
     * @param searchElements
     *            The sequence of elements to be searched for. Must not be null.
     * @param options
     *            The options to be used for the search.
     * @see ScrapeOptions
     * @return An index pointing to the first element <b>after</b> the found sequence
     *         of elements, or -1, if the sequence could not be found.
     */
    public int searchTokens(Vector<AbstractHTMLToken> searchElements,
            ScrapeOptions options) {
        return searchTokens(getPosition(), searchElements, options);
    }

    /**
     * Like searchTokenChain(Vector<HtmlToken>, ScrapeOptions), but with a
     * configurable starting-point for the search.
     * 
     * @param startHere
     *            The position from where on the search should be performed.
     * @param searchElements
     *            The sequence of elements to be searched for. Must not be null.
     * @param options
     *            The options to be used for the search.
     * @see ScrapeOptions
     * @return The index pointing to the first element <b>after</b> the found sequence
     *         of elements, or -1, if the sequence could not be found.
     */
    public int searchTokens(int startHere,
            Vector<AbstractHTMLToken> searchElements, ScrapeOptions options) {
        if (searchElements == null) {
            throw new IllegalArgumentException(
                    "searchElements must not be null");
        }
        int advancingStart = startHere;
        int position = advancingStart;
        int searchPosition = 0;
        boolean patternStarted = false;
        while (position < elements.size()) {
            if (searchPosition == searchElements.size()) {
                break;
            } else {
                AbstractHTMLToken here = elements.get(position);
                AbstractHTMLToken there = searchElements.get(searchPosition);
                if (here.match(there, options)) {
                    patternStarted = true;
                    // we remember this position so that we can later pick up search here,
                    // if we only have a partial match and want to continue further on.
                    advancingStart = position;
                    position++;
                    searchPosition++;
                } else if (!patternStarted || canAdvanceAnyway(here, there, options)) {
                    position++;
                } else {
                    advancingStart++;
                	position = advancingStart;
                	searchPosition = 0;
                	patternStarted = false;
                }
            }
        }
        if (searchPosition == searchElements.size()) {
            if (options.advance) {
                setPosition(position);
                return getPosition();
            } else {
                return position;
            }
        } else {
            return -1;
        }
    }
    
    /**
     * Returns whether more Elements can be retrieved using the next()-method.
     * Warning: Other methods like getNextTag or getNextText() may fail, even
     * though this method returns true!
     */
    public boolean hasNext() {
        return getPosition() < elements.size();
    }

    /**
     * Returns the total number of elements in the current file.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Returns, how many more elements are in the current file based on
     * the current position.
     */
    public int available() {
        return Math.max(elements.size() - getPosition(), 0);
    }

    /**
     * Returns the next HtmlToken in the current file. This can be any kind of
     * HtmlToken, including TextToken-Elements with only whitespace. The current
     * Marker is advanced by one when calling this method.
     * @return The next HtmlToken.
     */
    public Object next() {
        AbstractHTMLToken element = elements.get(getPosition());
        advance(1);
        return element;
    }

    /**
     *  Removes the element currently pointed at from the file.
     */
    public void remove() {
        int which = getPosition();
        if (which >= 0 && which < elements.size()) {
            elements.remove(which);
        }
        
    }

    /**
     * Advance within the current file. The position can advance beyond the
     * last element in the file.
     * @param howFar How many elements should be skipped.
     */
    public void advance(int howFar) {
        setPosition(getPosition() + howFar);
    }
    
    /**
     * Returns the next TextToken-HtmlToken in the current file.
     * The current position advances to behind the next element, if the search
     * was successful.
     * @param skipEmpty true: TextToken-Elements containing only whitespace and
     * linebreaks are skipped, false: the next TextToken-HtmlToken is returned regardless
     * of content.
     * @return The next TextToken-HtmlToken, or null, if none could be found.
     */
    public TextToken getNextText(boolean skipEmpty) {
        int pos = getPosition();
        while (pos < elements.size()) {
            AbstractHTMLToken element = elements.get(pos);
            if (element instanceof TextToken) {
                if (!skipEmpty || !isWhitespace(element)) {
                    setPosition(pos + 1);
                    return (TextToken)element;
                }
            }
            pos ++;
        }
        return null;
    }
    
    /**
     * Returns the next TextToken-HtmlToken in the current file starting from
     * a given location. The current position in the file does not change.
     * @param fromHere Starting index for the search.
     * @param skipEmpty true: TextToken-Elements containing only whitespace and
     * linebreaks are skipped, false: the next TextToken-HtmlToken is returned regardless
     * of content.
     * @return The next TextToken-HtmlToken, or null, if non could be found.
     */
    public TextToken getNextText(int fromHere, boolean skipEmpty) {
        int start = fromHere;
        while (start < elements.size()) {
            AbstractHTMLToken element = elements.get(start);
            if (element instanceof TextToken) {
                if (!skipEmpty || !isWhitespace(element)) {
                    return (TextToken)element;
                }
            }
            start ++;
        }
        return null;
    }
    
    /**
     * Returns the next HtmlTag in the current file.
     * The current position advances to behind the next element.
     * @return The next HtmlTag, or null, if none could be found.
     */
    public HTMLTag getNextTag() {
        int pos = getPosition();
        while (pos < elements.size()) {
            AbstractHTMLToken element = elements.get(pos);
            if (element instanceof HTMLTag) {
                setPosition(pos + 1);
                return (HTMLTag)element;
            }
            pos ++;
        }
        return null;
    }
    
    /**
     * Returns the next HtmlTag in the current file starting from
     * a given location. The current position in the file does not change.
     * @param fromHere Starting index for the search.
     * @return The next HtmlTag, or null, if non could be found.
     */
    public HTMLTag getNextTag(int fromHere) {
        int start = fromHere;
        while (start < elements.size()) {
            AbstractHTMLToken element = elements.get(start);
            if (element instanceof HTMLTag) {
                return (HTMLTag)element;
            }
            start ++;
        }
        return null;
    }
    
    /**
     * Returns the element at the given index.
     * @param index The index of the element to be retrieved.
     * @return The requested element, or null, if index is out of range.
     */
    public AbstractHTMLToken get(int index) {
        if (index >= 0 && index < size()) {
            return elements.get(index);
        } else {
            return null;
        }
    }
    
    /**
     * Returns all Elements in this document that are relevant to forms.
     * This includes the form-tags, all input-, select-, and option-tags.
     * The position in the file doesn't change.
     * @return The elemnts for all forms in the document.
     */
    public List<AbstractHTMLToken> getForms() {
    	List<AbstractHTMLToken> forms = new ArrayList<AbstractHTMLToken>();
    	for (int pos = 0; pos < elements.size(); pos++) {
    		AbstractHTMLToken element = elements.get(pos);
    		if (element instanceof HTMLTag) {
    			String tagName = ((HTMLTag)element).getName();
    			if (tagName.equalsIgnoreCase("form") ||
    					tagName.equalsIgnoreCase("input") ||
    					tagName.equalsIgnoreCase("select") ||
    					tagName.equalsIgnoreCase("option"))
    			{
    				forms.add(element);
    			}
    		}
    	}
    	return forms;
    }

    /**
     * Convenience method for printing HTML-content to a file.
     * 
     * @param filename The name (and path) of the file to write to.
     * @throws IOException
     */
    public void printToFile(String filename) throws IOException {
        printToFile(elements, filename);
    }

    /**
     * Convenience method for printing a list of tokens to a file.
     * 
     * @param filename The name (and path) of the file to write to.
     * @throws IOException
     */
    public void printToFile(List<AbstractHTMLToken> tokens, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
        for (int i = 0; i < tokens.size(); i++) {
            writer.write(tokens.get(i).toString());
        }
        writer.close();
    }

}
