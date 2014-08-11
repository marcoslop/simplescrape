package de.dbsystems.simplescrape;

import java.io.*;

/**
 * Split an input stream into HTML tokens. These tokens can be tags, comments
 * and text tokens.
 * 
 * @author Ronald Bieber, DB Systems GmbH
 * @since 03.04.2007
 * 
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

public class Tokenizer {
	private Reader reader;
	private final static int LOOKAHEAD_LENGTH = 4;

	/**
	 * Used for a simple lookahead
	 */
	private int[] input = new int[LOOKAHEAD_LENGTH];

	private final static int TYPE_TEXT_TOKEN = 0;

	private final static int TYPE_TAG = 1;

	private final static int TYPE_COMMENT = 2;

	/**
	 * Convenience method for parsing a string.
	 */
	public Tokenizer(String text) throws IOException {
		this(new ByteArrayInputStream(text.getBytes()));
	}

	/**
	 * Parse an input stream.
	 */
	public Tokenizer(InputStream in) throws IOException {
		reader = new InputStreamReader(in);
		for (int i = 0; i < input.length; i++) {
			input[i] = reader.read();
		}
	}

	/**
	 * Advance to the next character in the stream.
	 */
	private void next() throws IOException {
		if (input[0] != -1) {
			for (int i = 0; i < input.length - 1; i++) {
				input[i] = input[i + 1];
			}
			input[input.length - 1] = reader.read();
		}
	}

	/**
	 * Advance n characters into the stream.
	 * 
	 * @param n
	 *            The number of characters to advance.
	 */
	private void next(int n) throws IOException {
        for (int i = 0; i < n; i++) {
			next();
		}
	}

	private boolean match(String match) {
		if (match == null || match.length() > input.length) {
			return false;
		}
		for (int i = 0; i < match.length(); i++) {
			if (match.charAt(i) != (char)input[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
     * Determines the type of the next token. As a side effect, depending on the type, the
     * position within the input advances in an order such that the position will afterwards
     * point to the start of the actual content of that token.
     * 
     * @return The type of the token as represented by one of the constants TYPE_TEXT_TOKEN,
     * TYPE_COMMENT or TYPE_TAG
     * @throws IOException This shouldn't happen, as the possibly hazardous next() is only
     * called after checking that there is enough to gobble up.
	 */
    private int determineTokenType() throws IOException {
        int state = TYPE_TEXT_TOKEN;
        if (input[0] == '<') {
            if (match("<!--")) { 
                state = TYPE_COMMENT;
                next(4);
            } else {
                state = TYPE_TAG;
                next();
            }
        }
        return state;
    }
    
    /**
	 * Read the next HTML token from the input stream.
	 * To determine what kind of element this is (text, tag or comment), use
	 * the instanceof-operator.
	 *
	 * @return The next HTML token, or null, if the end has been reached.
	 */
	public AbstractHTMLToken readElement() throws IOException {
		StringBuffer buf = new StringBuffer();
        // Step 1: What do we have here?
		int state = determineTokenType();
		boolean end = false;

		// Step 2: Gobble up characters until we hit the end of the token
		while (input[0] != -1 && !end) {
			buf.append((char) input[0]);
			next();

			switch (state) {
			case TYPE_TEXT_TOKEN:
				if (input[0] == '<') {
					end = true;
				}
				break;
			case TYPE_TAG:
				if (input[0] == '>') {
					next();
					end = true;
				}
				break;
			case TYPE_COMMENT:
				if (match("-->")) { 
					next(3);
					end = true;
				}
				break;
            default:
                // This never happens!
                break;
			}
		}

        return createNewToken(state, buf.toString());
	}
    
    
	/**
     * Creates a new HtmlToken.
     * @param tokenType Determines the type of the token. Use TYPE_TEXT_TOKEN, TYPE_TAG or TYPE_COMMENT
     * @param content The content for the next token.
     * @return The new token. This may be a TextToken, an HtmlTag or an HtmlComment - or null, if there is
     * no content to be used.
	 */
    private AbstractHTMLToken createNewToken(int tokenType, String content) {
        AbstractHTMLToken result = null;
        switch (tokenType) {
        case TYPE_TAG:
            result = new HTMLTag(content);
            break;
        case TYPE_COMMENT:
            result = new HTMLComment(content);
            break;
        default:
            if (content != null && content.length() > 0) {
                result = new TextToken(content);
            }
            // else we will resturn null;
            break;
        }
        return result;
    }
}
