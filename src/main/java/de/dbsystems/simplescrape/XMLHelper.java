package de.dbsystems.simplescrape;

import org.w3c.dom.*;

/**
 * Class for holding HTML-comments. These are all texts surrounded by <!-- and
 * -->
 * 
 * @author Ronald Bieber, DB Systems GmbH
 * @since 30.04.2007
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

public class XMLHelper {

    /**
     * Returns the text-content of the first element in a document with a given tag name.
     * This is useful for well structured documents when it is known that there is only
     * one such element.
     * 
     * @param document The document to search within.
     * @param tagname The name of the element to retrieve.
     * @return The text content of the element. Text nodes therein are appended for the result, but
     * no further descendants are included. If the element could not be found, the empty string is
     * returned.
     */
    public static String getNodeContent(Document document, String tagname) {
        NodeList list = document.getElementsByTagName(tagname);
        if (list.getLength() < 1) {
//            log.debug("Not found: " + tagname);
            return "";
        }
        Element tag = (Element)list.item(0);
        NodeList content = tag.getChildNodes();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < content.getLength(); i++) {
            Node node = content.item(i);
            if (node instanceof Text) {
                buf.append(((Text)node).getNodeValue());
            }
        }
        String textcontent = buf.toString().trim();
//        log.debug("getNodeContent: " + tagname + " = [" + textcontent + "]");
        return textcontent;
    }
    
    /**
     * Returns the value of an attribute in the first element in a document with a given tag name.
     * This is useful for well structured documents when it is known that there is only
     * one such element and that it is has that attribute.
     * 
     * @param document The document to search within.
     * @param tagname The name of the element to access.
     * @param attributename The attribute's name.
     * @return The value of the attribute of the first respective element, or the empty string, if
     * the element of the attribute could not be found.
     */
    public static String getAttribute(Document document, String tagname, String attributename) {
        NodeList list = document.getElementsByTagName(tagname);
        if (list.getLength() < 1) {
            return "";
        }
        Element tag = (Element)list.item(0);
        return tag.getAttribute(attributename);
    }
    
}
