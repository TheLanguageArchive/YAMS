/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.yaas.common.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.util.Base64;

/**
 * Created on : Apr 8, 2013, 10:48:43 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class RestDbAdaptor implements DbAdaptor {

    final private URL restUrl;
    final private String encodedPass;

    public RestDbAdaptor(URL restUrl, String userName, String userPass) {
        this.restUrl = restUrl;
        // Encode user name and password pair with a base64 implementation.
        encodedPass = Base64.encode(userName + ":" + userPass);
    }

    public void dropAndRecreateDb(String databaseName) throws QueryException {
        try {
            URL databaseUrl = new URL(restUrl, databaseName);
            System.out.println("dropAndRecreateDb DELETE: " + databaseUrl);
            HttpURLConnection conn = (HttpURLConnection) databaseUrl.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Basic " + encodedPass);
            final int responseCode = conn.getResponseCode();
//            System.out.println("HTTP response: " + responseCode);
            conn.disconnect();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new QueryException("HTTP response: " + responseCode);
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
        checkDbExists(databaseName);
    }

    public void checkDbExists(String databaseName) throws QueryException {
        try {
            URL databaseUrl = new URL(restUrl, databaseName);
            System.out.println("checkDbExists PUT: " + databaseUrl);
            HttpURLConnection conn = (HttpURLConnection) databaseUrl.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Basic " + encodedPass);
            final int responseCode = conn.getResponseCode();
//            System.out.println("HTTP response: " + responseCode);
            conn.disconnect();
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw new QueryException("HTTP response: " + responseCode);
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public void addDocument(String databaseName, String documentName, String documentContents) throws QueryException {
        try {
            URL documentUrl = new URL(restUrl, databaseName + "/" + documentName.replaceAll(":", "-").replaceAll("/", "-"));
            System.out.println("addDocument PUT: " + documentUrl);
            HttpURLConnection conn = (HttpURLConnection) documentUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Basic " + encodedPass);
            conn.setRequestProperty("Content-Type", "application/query+xml");
            OutputStream out = conn.getOutputStream();
            out.write(documentContents.getBytes("UTF-8"));
            out.close();
            final int responseCode = conn.getResponseCode();
//            System.out.println("HTTP response: " + responseCode);
            conn.disconnect();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                throw new QueryException("HTTP response: " + responseCode);
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public void deleteDocument(String databaseName, String documentName) throws QueryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String executeQuery(String queryString) throws QueryException {
        // todo: it would be better to consume the string as it becomes availalbe, however this will get complicated when one query depends on another such as the get missing ID list in the crawler.
        StringBuilder replaceMe = new StringBuilder();
        try {
//    String request =
//      "<query xmlns='http://basex.org/rest'>\n" +
//      "  <text>(//city/name)[position() le 3]</text>\n" +
//      "  <parameter name='wrap' value='yes'/>\n" +
//      "</query>";
            System.out.println("queryString: " + queryString);

            // Establish the connection to the URL
            HttpURLConnection conn = (HttpURLConnection) restUrl.openConnection();
            // Set an output connection
            conn.setDoOutput(true);
            // Set as PUT request
            conn.setRequestMethod("POST");
            // Specify content type
            conn.setRequestProperty("Content-Type", "application/query+xml");

            // Get and cache output stream
            OutputStream out = conn.getOutputStream();

            // Send UTF-8 encoded query to server
            out.write(queryString.getBytes("UTF-8"));
            out.close();

            // Print the HTTP response code
            int code = conn.getResponseCode();
            System.out.println("HTTP response: " + code + " (" + conn.getResponseMessage() + ')');

            // Check if request was successful
            if (code == HttpURLConnection.HTTP_OK) {
                // Print the received result to standard output (same as GET request)
                System.out.println("Result:");
//
                // Get and cache input as UTF-8 encoded stream
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                // Print all lines of the result
                for (String line; (line = br.readLine()) != null;) {
                    System.out.println(line);
                    replaceMe.append(line);
                    replaceMe.append("\n");
                }
                br.close();
            } else {
                throw new QueryException("Could not connect to the rest service: " + code + " (" + conn.getResponseMessage() + ')');
            }
            // Close connection
            conn.disconnect();
            return replaceMe.toString();
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public QueryProcessor getQueryProcessor(String queryString) throws QueryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
