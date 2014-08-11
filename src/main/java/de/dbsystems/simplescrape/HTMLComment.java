package de.dbsystems.simplescrape;

/**
 * Class for holding HTML-comments. These are all texts surrounded by <!-- and
 * -->
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

public class HTMLComment extends AbstractHTMLToken {
    public boolean match(AbstractHTMLToken other, ScrapeOptions options) {
        if (!(other instanceof HTMLComment)) {
            return false;
        }
        String otherComment = ((HTMLComment)other).getComment();
        return options.ignoreCase ?
                   getComment().equalsIgnoreCase(otherComment) :
                   getComment().equals(otherComment);
    }

    private String content;

    /**
     * Constructor for a comment
     * @param text The content of the comment, without the surrounding markers.
     * Example: "This is a comment", not "<!-- this is a comment -->"
     */
    public HTMLComment(String text) {
        super();
        this.content = text;
    }

    /**
     * The content of this comment, without the surrounding markers.
     * @return The content of this comment.
     */
    public String getComment() {
        return content;
    }

    /**
     * Returns the HTML-representation of this comment, including the
     * comment-markers.
     * @return The comment as HTML.
     */
    public String toString() {
        return "<!--" + getComment() + "-->";
    }
}
