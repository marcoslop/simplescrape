package de.dbsystems.simplescrape;

/**
 * Represents tags in HTML-files. There will be one object for every opening and closing
 * tag, each (and for unary tags, too).
 * Attributes are stored and parsed seperately and can be accessed through the
 * getAttributes()-method.
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

public class HTMLTag extends AbstractHTMLToken {
    private boolean closingTag;
    private boolean unaryTag;

    /**
     * Test the attribute sets of two tags. Note: The order of a and b is
     * not irrelevant! Depending on the ScrapeOptions provided a may contain
     * more attributes than b without failing the test.
     * 
     * @param a The tag under test.
     * @param b The reference to be tested against.
     * @param options Relevant options for this comparison are attributesStrict and ignoreCase. 
     * @return <code>true</code>: The attributes match, <code>false</code>: they don't match.
     */
    public boolean attributesMatch(final HTMLTag a, final HTMLTag b, final ScrapeOptions options) {
        HTMLTagAttributes attrA = a.getAttributes();
        HTMLTagAttributes attrB = b.getAttributes();
        
        // First: Check if one or both tags don't have any attributes
        if (attrA == null && attrB == null) {
            // automatic pass: Both have no attributes
            return true;
        }
        if (attrB == null) {
            // if strict: No HtmlTagAttributes in A allowed, also: pass.
            return options.attributesStrict ? attrA == null : true;
        }
        if (attrA == null) {
            // there are attributes in B that can not be in A: fail.
            return false;
        }
        
        return attrA.match(attrB, options);
    }

    
    public boolean match(AbstractHTMLToken other, ScrapeOptions options) {
        if (!(other instanceof HTMLTag)) {
            return false;
        }
        HTMLTag b = (HTMLTag)other;
        boolean tagNameEquals = options.ignoreCase ?
            (getName().equalsIgnoreCase(b.getName())) :
            (getName().equals(b.getName()));
        return tagNameEquals &&
            isEndTag() == b.isEndTag() &&
            attributesMatch(this, b, options);
    }

    private String name = "";

    private HTMLTagAttributes attributes = null;

    /**
     * Create an HTML tag. Pass in the content of the tag, i.e. for the tag "<body
     * bgcolor=#ffffff>" pass in "body bgcolor=#ffffff".
     */
    public HTMLTag(String tagContent) {
        super();
        tagContent = tagContent.trim();
        if (tagContent.startsWith("/")) {   //NOPMD
            closingTag = true;
            tagContent = tagContent.substring(1);
        }
        if (tagContent.endsWith("/")) {
            unaryTag = true;
            tagContent = tagContent.substring(0, tagContent.length() - 1).trim();
        }
        int length = tagContent.length();
        if (length > 0) {
            int pos = 0;
            // skip all whitespace at the start
            while (pos < length && tagContent.charAt(pos) <= ' ') {
                pos++;
            }
            int end;
            // now find the first whitespace after the start (or the end of the string)
            for (end = pos; end < length; ++end) {
                if (tagContent.charAt(end) <= ' ') {
                    break;
                }
            }
            name = tagContent.substring(pos, end);
            if (end < length) {
                attributes = new HTMLTagAttributes(tagContent.substring(end));
            }
        }
    }

    /**
     * Returns the attributes of this node.
     * 
     * @return The attributes, if there are any, or null otherwise
     */
    public HTMLTagAttributes getAttributes() {
        return attributes;
    }

    /**
     * Returns the name of this tag.
     * 
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Whether or not the tag is an end tag.
     */
    public boolean isEndTag() {
        return closingTag;
    }

    /**
     * Whether or not the tag is a unary tag. Example for a unary tag: <br/>
     */
    public boolean isUnaryTag() {
        return unaryTag;
    }

    /**
     * Returns an HTML-representation of this tag. The attributes are returned
     * the way they were originally provided (not normalized).
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('<');
        if (closingTag) {
            buf.append('/');
        }
        buf.append(name);
        if (attributes != null) {
            buf.append(' ');
            buf.append(attributes.toString());
        }
        if (unaryTag) {
            buf.append('/');
        }
        buf.append('>');
        return buf.toString();
    }
}
