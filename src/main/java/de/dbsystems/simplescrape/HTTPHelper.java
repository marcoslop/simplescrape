package de.dbsystems.simplescrape;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.*;

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

public class HTTPHelper {
    private Proxy proxy = Proxy.NO_PROXY;
    
    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Acquires the content of a webpage.
     * 
     * @param urlString The URL to be retrieved from.
     * @return The content as a tokenized Scraper, or null, if an error occurred.
     */
    public Scraper getWebpageForScraping(String urlString) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection(getProxy());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("GET-connection unsuccessful (Response-Code: "
                    + connection.getResponseCode() + ", should be 200 OK): "
                    + url.toString());
                return null;
            }

            Scraper scraper = new Scraper(connection.getInputStream());
            connection.disconnect();
            return scraper;
        } catch (MalformedURLException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }
    
    /**
     * Acquires the content of a webpage with POST-data. The code is
     * specific to the Reiseauskunft, as a referer is expected while cookies are
     * not processed at all.
     * 
     * @param urlString The URL to retrieve from.
     * @param postData The complete (and encoded) POST-data
     * @param referer The referer to be transmitted (HTTP-Header)
     * @return The content as a tokenized Scraper, or null, if an error occurred.
     */
    public Scraper getWebpageForScraping(String urlString, String postData, String referer) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(getProxy());
            connection.setDoOutput(true);
            connection.setRequestProperty("Referer", referer);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512); // grows, if needed
            PrintWriter buffer = new PrintWriter(byteStream, true);
            buffer.print(postData);
            buffer.flush();

            connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");

            // POST requests are required to have Content-Length
            connection.setRequestProperty("Content-Length", "" + byteStream.size());

            // Write POST data to real output stream
            byteStream.writeTo(connection.getOutputStream());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Seite nicht gefunden ("
                    + connection.getResponseCode() + "): "
                    + url.toString());
                return null;
            }

            Scraper scraper = new Scraper(connection.getInputStream());
            connection.disconnect();
            return scraper;
        } catch (MalformedURLException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }
    
}
