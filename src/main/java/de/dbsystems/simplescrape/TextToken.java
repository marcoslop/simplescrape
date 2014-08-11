package de.dbsystems.simplescrape;

import java.util.regex.Pattern;

/**
 * Represents tokens containing text data in an HTML-file. This is all data
 * outside of tags and comments. Tokens can span multiple words, sentences and
 * lines.
 * 
 * @author Ronald Bieber, DB Systems GmbH
 * @since 04.04.2007
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

public class TextToken extends AbstractHTMLToken {
	private String text;

	/**
	 * Creates a new TextToken, initializing it with the provided text.
	 * @param text The text this token is supposed to hold.
	 */
	public TextToken(String text) {
        super();
		this.text = text;
	}

	/**
	 * Returns the text-content of this token.
	 * 
	 * @return The text token, or null, if none has been set.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the text-content of this token. Unlike than with other
	 * children of HtmlToken, this is the same as calling
	 * 
	 * @see #getText()
	 * 
	 * @return The text token, or null, if none has been set.
	 */
	public String toString() {
		return getText();
	}

    public boolean match(AbstractHTMLToken other, ScrapeOptions options) {
        if (!(other instanceof TextToken)) {
            return false;
        }
        if (other instanceof RegExTextToken) {
            return Pattern.matches(((RegExTextToken)other).getExpression(), getText());
        } else {
            if (options.ignoreCase) {
                if (options.trimText) {
                    return getText().trim().equalsIgnoreCase(((TextToken)other).getText().trim());
                } else {
                    return getText().equalsIgnoreCase(((TextToken)other).getText());
                }
            } else {
                if (options.trimText) {
                    return getText().trim().equals(((TextToken)other).getText().trim());
                } else {
                    return getText().equals(((TextToken)other).getText());
                }
            }
        }
    }
}
