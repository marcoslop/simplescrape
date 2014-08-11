package de.dbsystems.simplescrape;

/**
 * Preferences for scraping a file. Since there are many different options on how to
 * process a scraped file, these are grouped together here. This keeps the method
 * signatures lean and allows for using different "sets" of options to be used
 * during scraping.
 *
 * @author Ronald Bieber, DB Systems GmbH
 * @since 05.04.2007
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

public class ScrapeOptions {
    /**
     * There are no other elements allowed inbetween the provided elements.
     * This is a possible value for elementOrder.
     */
    public final static int
        ELEMENT_ORDER_STRICT = 0;
    /**
     * Between the provided elements whitspace-tokens may appear.
     * This includes line-breaks. This is a possible value for elementOrder.
     */
    public final static int
        ELEMENT_ORDER_WHITESPACE_ALLOWED = 1;
    /**
     * Between the provided elements whitspace-tokens and comments may appear.
     * This includes line-breaks. This is a possible value for elementOrder.
     */
    public final static int
        ELEMENT_ORDER_COMMENTS_ALLOWED = 2;
    /**
     * Any other elements, including tags, may appear inbetween the provided
     * elements. This is a possible value for elementOrder.
     */
    public final static int
        ELEMENT_ORDER_ELEMENTS_ALLOWED = 3;
    
    /**
     * Specified, how strict the elements to be searched for are to be treated.
     * This can range from absolutely strict to very
     * lenient.
     * Possible values are:
     * <ul>
     * <li>ELEMENT_ORDER_STRICT</li>
     * <li>ELEMENT_ORDER_WHITESPACE_ALLOWED</li>
     * <li>ELEMENT_ORDER_COMMENTS_ALLOWED</li>
     * <li>ELEMENT_ORDER_ELEMENTS_ALLOWED</li>
     * </ul>
     * Default value: ELEMENT_ORDER_COMMENTS_ALLOWED
     */
    public int elementOrder = ELEMENT_ORDER_COMMENTS_ALLOWED;

    /**
     * Specifies, whether the attributes of provided elements are to be
     * treated strict or lenient.
     * Possible values:
     * <ul>
     * <li>true: The attributes in the scraped file must match the provided
     * attributes exactly. Additional attributes (in the scraped file) lead to
     * a matching failure.</li>
     * <li>false: The elements in the scraped file may contain additional
     * attributes without causing a matching failure.</li>
     * </ul>
     * Default: false
     */
    public boolean attributesStrict = false;

    /**
     * Specifies, whether equality checks are to be performed ignoring or
     * respecting case. This will be used for tags, comments and text in
     * the same way.
     * Default: true
     */
    public boolean ignoreCase = true;

    /**
     * Specifies, whether checked text is trimmed first. If true, surrounding whitespace
     * will be ignored (both in search-tokens and analyzed data.
     * Default: true
     */
    public boolean trimText = true;

    /**
     * Specifies, whether the internal current marker is advanced during a
     * search operation. Default: true.
     */
    public boolean advance = true;
    
    /**
     * Specified, whether the search is performed forward or backwards.
     * Please note that a backward search also leads to the provided elements
     * to be processed backwards.
     * <p>Example (simplied): "a b c d e d c", searching backwards (from the end)
     * for "d e" returns a positive result (with the marker pointing to the
     * second "d" as it is the first token after the found "d e"), while
     * a search for "b a" fails, as these tokens do not appear in that order.</p>
     * <p>Default: true (forwards).</p>
     * @todo Not yet supported.
     */
    public boolean searchForward = true;
}
