package de.dbsystems.simplescrape;

/**
 * Common superclass for all tokens that can be found in an HTML-file.
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

public abstract class AbstractHTMLToken {

    /**
     * Determines whether two tokens match.
     * @param other The search-HtmlToken to be tested against.
     * @param options A set of options. Relevant options are attributesStrict, trimText and
     * ignoreCase.
     * @return true: The two elements match, false: they don't (duh!)
     */
    public abstract boolean match(AbstractHTMLToken other, ScrapeOptions options);
}
