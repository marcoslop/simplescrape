package de.dbsystems.simplescrape;

import java.util.*;

/**
 * Defines a class for parsing and storing the
 * attributes of an HTML tag. Note that the
 * attributes are not parsed until they are
 * actually needed.
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

public class HTMLTagAttributes {
	public final static String NO_VALUE = "";
	private String unparsedAttributes;

    private String unparsedAttributesLowerCase;

    private Hashtable<String, String> attributes = null;
    
    public final static String QUOTATION_CHARS = "'\"";

    /**
     *  Constructs new attributes from a string.
     *  The attributes are not parsed right away. Instead, they are only parsed
     *  when needed. 
     *  
     *  @param attributesString The attribute string from inside the
     *  tag. Example: if the tag is "<div class=frame>", use " class=frame"
     *  as attributesString.
     */
    public HTMLTagAttributes(String attributesString) {
        if (attributesString == null) {
            attributesString = "";
        }
        unparsedAttributes = attributesString.trim();
        unparsedAttributesLowerCase = unparsedAttributes.toLowerCase();
    }

    /**
     * Returns, whether an attribute was defined within this tag.
     * @param name The name of the attribute to be checked.
     * @return Whether it exists or not.
     */
    public boolean exists(String name) {
        return (get(name) != null);
    }

    /**
     * Get the value of a given attribute.
     * @param name The attribute name (not case-sensitive).
     * @return the value of that attribute, or null, if that attribute was not present in this tag. For unary attributes
     * an empty string is returned. The result is not quoted, regardless of the quoting originally used.
     */
    public String get(String name) {
        String nameLowerCase = name.toLowerCase();

        // Delay the parsing by checking if the attribute
        // name is in the attribute set.

        if (attributes == null) {
            if (unparsedAttributesLowerCase.indexOf(nameLowerCase) == -1) {
                return null;
            }

            parse(unparsedAttributes);
        }

        return attributes.get(nameLowerCase);
    }

    /**
     *  Parse the attributes. Unlike XML, HTML attributes
     *  may be unary and if a value is specified, it may or may not be quoted. This algorithm
     *  respect these factors.
     *  
     *  Note: Parsing takes place in a lazy way. Only if details about the attributes are requested,
     *  parsing is performed.
     *  @param input The string containing all the names and values in the way used in the HTML-code.
     */
    private void parse(String input) {
        ArrayList<String> tokens = new ArrayList<String>();
        int length = input.length();
        // Split the attribute string into tokens
        int i = 0;
        while (i < length) {
            char c = input.charAt(i);
            if (QUOTATION_CHARS.indexOf(c) >= 0) {
                i = storeValue(input, tokens, i);
            } else if (c == '=') {
                // avoid leading equal signs and multiple equals signs
                if (tokens.size() > 0 && !tokens.get(tokens.size() - 1).equals("=")) {
                    tokens.add("=");
                }
            } else if (c > ' ') {
                i = storeName(input, tokens, i);
            }
            i++;
        }
        // remove trailing equal signs
        for (i = tokens.size() - 1; i >= 0 && tokens.get(i).equals("="); i--) {
            tokens.remove(i);
        }
        
        attributes = storeAttributes(tokens);
    }

    /**
     * Reads a value from an input string, stores it in a list of tokens
     * and returns the position to continue from.
     * 
     * @param input The input string to operate upon.
     * @param tokens The list of tokens, where the value is to be appended.
     * @param start The index, where the value starts within input.
     * @return The index, where the value ends. This is usually the index of the
     * closing quotation mark, but can also be the length of input, if no further
     * quotation mark could be found.
     */
    private int storeValue(String input, ArrayList<String> tokens, int start) {
        char c = input.charAt(start);
        int end = input.indexOf(c, start + 1);
        if (end < 0) {
            end = input.length();
        }
        tokens.add(input.substring(start + 1, end));
        return end;
    }

    /**
     * Reads an attribute name from an input string, stores it in a list of tokens
     * and returns the position to continue from.
     * 
     * @param input The input string to operate upon.
     * @param tokens The list of tokens, where the name is to be appended.
     * @param start The index, where the name starts within input.
     * @return The index, where the name ends. This is the position of the last
     * character of the attribute name.
     */
    private int storeName(String input, ArrayList<String> tokens, int start) {
        int end1 = input.indexOf(' ', start);
        int end2 = input.indexOf('=', start);
        int end = -1;
        if (end1 > 0 && end2 > 0) {
            end = Math.min(end1, end2);
        } else if (end1 < 0 && end2 < 0){
            end = input.length();
        } else {
            // This works as one of them is -1
            end = Math.max(end1, end2);
        }
        tokens.add(input.substring(start, end));
        return end - 1;
    }
    
    /**
     * Stores the attributes as name-value pairs
     * in a hashtable. If an attribute does not have
     * a value, the empty string is used as its value.
     * Attribute names will be stored in lower case.
     * 
     * @param tokens The parsed list of tokens.
     */
    private Hashtable<String, String> storeAttributes(ArrayList<String> tokens) {
        int index = 0;
        Hashtable<String, String> result = new Hashtable<String, String>();
        int tokencount = tokens.size();
        while (index < tokencount) {
        	if (index < tokencount - 2 && tokens.get(index + 1).equals("=")) {
        		// since we have normalized before, we know that there will be a value, too.
        		result.put(tokens.get(index).toLowerCase(), tokens.get(index + 2));
                index += 3;
        	} else {
        		result.put(tokens.get(index).toLowerCase(), NO_VALUE);
                index++;
        	}
        }
        return result;
    }

    /**
     * Returns the attribute string. This string is identical to the original format and has
     * not been normalized in any way. 
     */
    public String toString() {
        return unparsedAttributes;
    }
    
    /**
     * Returns an enumeration of all the attribute names found. 
     * @return The attribute names as an enumeration of strings. This can be null,
     * if no attributes are present.
     */
    public Enumeration<String> getAttributeNames() {
        if (attributes == null) {
      		parse(unparsedAttributes);
        }
   		return attributes.keys();
    }
    
    /**
     * Return the number of attributes stored herein.
     * @return The number of attributes.
     */
    public int getSize() {
    	if (attributes == null && unparsedAttributes.length() > 0) {
    		parse(unparsedAttributes);
    	}
        return attributes == null ? 0 : attributes.size();
    }

    public boolean match(HTMLTagAttributes other, ScrapeOptions options) {
        // Iff strict checking is desired: Check now if there are no more
        // HtmlTagAttributes in A than in B. This can be done with a simple size
        // comparison. Since this is so simple, we do it upfront to save
        // some time instead of running through all attributes first.
        if (options.attributesStrict) {
            if (getSize() != other.getSize()) {
                return false;
            }
        }

        // Now: Check for every attribute in B if it occurs in A likewise
        Enumeration<String> namesB = other.getAttributeNames();
        boolean result = true;
        while (result && namesB.hasMoreElements()) {
            String nameB = namesB.nextElement();
            String valueA = get(nameB);
            String valueB = other.get(nameB);
            if (valueA == null) {
                if (valueB != null) {
                    result = false;
                }
            } else if (valueB == null) {
                result = false;
            } else {
                result = options.ignoreCase ? valueA.equalsIgnoreCase(valueB) : valueA.equals(valueB);
            }
        }
        return result;
        
    }

}
