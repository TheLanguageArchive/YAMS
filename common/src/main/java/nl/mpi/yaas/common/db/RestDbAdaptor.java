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
import java.net.MalformedURLException;
import java.net.URL;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
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
//                throw new QueryException("HTTP response: " + responseCode);
                // this is not exceptional and will happen on the first run when the db does not exist
                System.out.println("Failed to delete old database, maybe it does not exist");
            }
        } catch (IOException exception) {
            // this is not exceptional and will happen on the first run when the db does not exist
            System.out.println("Failed to delete old database, maybe it does not exist: " + exception.getMessage());
        }
        checkDbExists(databaseName);
    }

    public void deleteAllFromDb(String databaseName) throws QueryException {
        String deleteAllQuery = "for $document in collection('" + databaseName + "') "
                // + "return fn:delete('" + databaseName + "', fn:substring-after(base-uri($document), '/'))";
                + "return fn:delete('" + databaseName + "', base-uri($document))";
        System.out.println("deleteAllQuery: " + deleteAllQuery);
        executeQuery(databaseName, deleteAllQuery);
    }

    public void checkDbExists(String databaseName) throws QueryException {
        try {
            URL databaseUrl = new URL(restUrl, databaseName);
            HttpURLConnection connectionGet = (HttpURLConnection) databaseUrl.openConnection();
            connectionGet.setRequestMethod("GET");
            connectionGet.setRequestProperty("Authorization", "Basic " + encodedPass);
            final int getResponseCode = connectionGet.getResponseCode();
//            System.out.println("HTTP response: " + responseCode);
            connectionGet.disconnect();
            if (getResponseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("checkDbExists PUT: " + databaseUrl);
                HttpURLConnection connectionPut = (HttpURLConnection) databaseUrl.openConnection();
                connectionPut.setRequestMethod("PUT");
                connectionPut.setRequestProperty("Authorization", "Basic " + encodedPass);
                final int putResponseCode = connectionPut.getResponseCode();
//            System.out.println("HTTP response: " + responseCode);
                connectionPut.disconnect();
                if (putResponseCode != HttpURLConnection.HTTP_CREATED) {
                    throw new QueryException("HTTP response: " + putResponseCode);
                }
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    private void runCommand(String commandString) throws QueryException {
        try {
            HttpURLConnection conn = (HttpURLConnection) restUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + encodedPass);
            conn.setRequestProperty("Content-Type", "application/xml");
            OutputStream out = conn.getOutputStream();
//            System.out.println("executeQuery POST: " + restUrl + " : " + commandString);
            out.write(commandString.getBytes("UTF-8"));
            out.close();
            final int responseCode = conn.getResponseCode();
            final String responseMessage = conn.getResponseMessage();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("command ok");
            }
            // output the response which is only for debugging
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            for (String line; (line = bufferedReader.readLine()) != null;) {
                System.out.println("response: " + line);
            }
            bufferedReader.close();
            // end output
            conn.disconnect();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new QueryException("HTTP response: " + responseCode + " " + responseMessage);
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public void checkUserPermissions(String databaseName) throws QueryException {
        // just run a test query
        // commands should probably be disabled on the production database
        runCommand("<command xmlns=\"http://basex.org/rest\">\n"
                + "  <text>show users</text>\n"
                + "  <parameter name='encoding' value='ISO-8859-1'/>\n"
                + "</command>");
    }

    public void addDocument(String databaseName, String documentName, String documentContents) throws QueryException {
        try {
            URL documentUrl = new URL(restUrl, databaseName + "/" + documentName); //.replaceAll(":", "-").replaceAll("/", "-"));
            System.out.println("addDocument PUT: " + documentUrl);
            HttpURLConnection conn = (HttpURLConnection) documentUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Basic " + encodedPass);
            conn.setRequestProperty("Content-Type", "application/xml");
            OutputStream out = conn.getOutputStream();
            out.write(documentContents.getBytes("UTF-8"));
            out.close();
            final int responseCode = conn.getResponseCode();
            final String responseMessage = conn.getResponseMessage();
//            System.out.println("HTTP response: " + responseCode);
            conn.disconnect();
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw new QueryException("HTTP response: " + responseCode + " : responseMessage: " + responseMessage + " : documentUrl: " + documentUrl);
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public void deleteDocument(String databaseName, String documentName) throws QueryException {
        try {
            URL documentUrl = new URL(restUrl, databaseName + "/" + documentName); //.replaceAll(":", "-").replaceAll("/", "-"));
            System.out.println("deleteDocument DELETE: " + documentUrl);
            HttpURLConnection conn = (HttpURLConnection) documentUrl.openConnection();
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
    }

    public String executeQuery(String databaseName, String queryString) throws QueryException {
        // todo: it would be better to consume the string as it becomes available, however this will get complicated when one query depends on another such as the get missing ID list in the crawler.
        StringBuilder replaceMe = new StringBuilder();
        try {
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) restUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + encodedPass);
            conn.setRequestProperty("Content-Type", "application/xml");
            OutputStream out = conn.getOutputStream();
            String bodyString = "<query xmlns=\"http://basex.org/rest\">\n"
                    + "  <text><![CDATA[" + queryString + "]]></text>\n"
                    + "</query>";
//            System.out.println("executeQuery POST: " + restUrl + " : " + bodyString);
            out.write(bodyString.getBytes("UTF-8"));
            out.close();
            final int responseCode = conn.getResponseCode();
            final String responseMessage = conn.getResponseMessage();
            long responseMils = System.currentTimeMillis() - startTime;
            String queryTimeString = "response time: " + responseMils + "ms";
            System.out.println(queryTimeString);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                System.out.println("Reading response");
                for (String line; (line = bufferedReader.readLine()) != null;) {
//                    System.out.println("response: " + line);
//                    System.out.print(".");
                    replaceMe.append(line);
                }
                bufferedReader.close();
            }
            System.out.println(".");
            conn.disconnect();
            long totalMils = System.currentTimeMillis() - startTime;
            String totalTimeString = "total time: " + totalMils + "ms";
            System.out.println(totalTimeString);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new QueryException("HTTP response: " + responseCode + " " + responseMessage);
            }
            return replaceMe.toString();
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public void createIndexes(String databaseName) throws QueryException {
        try {
            for (String command : new String[]{"CREATE+INDEX+TEXT", "CREATE+INDEX+ATTRIBUTE", "CREATE+INDEX+FULLTEXT", "optimize+all"}) {
                URL databaseUrl = new URL(restUrl, databaseName + "?command=" + command);
                System.out.println("createIndexes GET: " + databaseUrl);
                HttpURLConnection conn = (HttpURLConnection) databaseUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + encodedPass);
                final int responseCode = conn.getResponseCode();
//            System.out.println("HTTP response: " + responseCode);
                conn.disconnect();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new QueryException("HTTP response: " + responseCode);
                }
            }
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
        checkDbExists(databaseName);
    }

    public static void main(String[] args) throws MalformedURLException, QueryException {
        final RestDbAdaptor restDbAdaptor = new RestDbAdaptor(new URL("http://192.168.56.101:8080/BaseX76/rest/"), "admin", "admin");
        System.out.println(restDbAdaptor.executeQuery(DataBaseManager.defaultDataBase, "collection('unit-test-database/CrawledData')/DataNode/Type"));
        System.out.println(restDbAdaptor.executeQuery(DataBaseManager.defaultDataBase, "<MetadataFileType>\n"
                + "<MetadataFileType>\n"
                + "<displayString>All Types</displayString>\n"
                + "<RecordCount>{count(collection('unit-test-database/CrawledData'))}</RecordCount>\n"
                + "</MetadataFileType>\n"
                + "{\n"
                + "let $allNodeTypes := collection('unit-test-database/CrawledData')/DataNode/Type/@Name\n"
                + "for $nodeType in distinct-values($allNodeTypes)\n"
                + "order by $nodeType\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<Type>{$nodeType}</Type>\n"
                + "<Count>{count($allNodeTypes[$nodeType])}</Count>\n"
                + "</MetadataFileType>\n"
                + "},{for $profileString in distinct-values(collection('unit-test-database')/*:CMD/@*:schemaLocation)\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<profileString>{$profileString}</profileString>\n"
                + "<RecordCount>{count(collection('unit-test-database')/*:CMD[@*:schemaLocation = $profileString])}</RecordCount></MetadataFileType>\n"
                + "}</MetadataFileType>"));
    }
}
