/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
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
package nl.mpi.yams.common.db;

import java.net.MalformedURLException;
import java.net.URL;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;

/**
 * Created on : Apr 26, 2013, 10:28 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class RestDbAdaptorTest {

    public RestDbAdaptorTest() {
    }

    /**
     * Test of dropAndRecreateDb method, of class RestDbAdaptor.
     */
    @Test
    @Before
    public void testDropAndRecreateDb() throws Exception {
        System.out.println("dropAndRecreateDb");
        RestDbAdaptor instance = new RestDbAdaptor(new URL(DataBaseManagerTest.restUrl), DataBaseManagerTest.restUser, DataBaseManagerTest.restPass);
        instance.dropAndRecreateDb(DataBaseManagerTest.testDatabaseName);
    }

    /**
     * Test of deleteAllFromDb method, of class RestDbAdaptor.
     */
    @Test
    @Ignore
    public void testDeleteAllFromDb() throws Exception {
        System.out.println("deleteAllFromDb");
        RestDbAdaptor instance = new RestDbAdaptor(new URL(DataBaseManagerTest.restUrl), DataBaseManagerTest.restUser, DataBaseManagerTest.restPass);
        instance.checkDbExists(DataBaseManagerTest.testDatabaseName);
        instance.addDocument(DataBaseManagerTest.testDatabaseName, "testdocument", "<document><contents>here</contents></document>");
        instance.deleteAllFromDb(DataBaseManagerTest.testDatabaseName);
    }

    /**
     * Test of checkDbExists method, of class RestDbAdaptor.
     */
    @Test
    @Before
    public void testCheckDbExists() throws MalformedURLException, QueryException {
        System.out.println("checkDbExists");
        RestDbAdaptor instance = new RestDbAdaptor(new URL(DataBaseManagerTest.restUrl), DataBaseManagerTest.restUser, DataBaseManagerTest.restPass);
        instance.checkDbExists(DataBaseManagerTest.testDatabaseName);
    }

    /**
     * Test of checkUserPermissions method, of class RestDbAdaptor.
     */
//    @Test
//    public void testCheckUserPermissions() throws Exception {
//        System.out.println("checkUserPermissions");
//        String databaseName = "";
//        RestDbAdaptor instance = null;
//        instance.checkUserPermissions(databaseName);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of addDocument method, of class RestDbAdaptor.
     */
    @Test
    public void testAddDocument() throws Exception {
        System.out.println("addDocument");
        RestDbAdaptor instance = new RestDbAdaptor(new URL(DataBaseManagerTest.restUrl), DataBaseManagerTest.restUser, DataBaseManagerTest.restPass);
        instance.addDocument(DataBaseManagerTest.testDatabaseName, "testdocument", "<document><contents>here</contents></document>");
    }

    /**
     * Test of deleteDocument method, of class RestDbAdaptor.
     */
    @Test
    public void testDeleteDocument() throws Exception {
        System.out.println("deleteDocument");

        RestDbAdaptor instance = new RestDbAdaptor(new URL(DataBaseManagerTest.restUrl), DataBaseManagerTest.restUser, DataBaseManagerTest.restPass);
        instance.addDocument(DataBaseManagerTest.testDatabaseName, "testdocument", "<document><contents>here</contents></document>");
        instance.deleteDocument(DataBaseManagerTest.testDatabaseName, "testdocument");
    }

    /**
     * Test of executeQuery method, of class RestDbAdaptor.
     */
    @Test
    public void testExecuteQuery() throws Exception {
        System.out.println("executeQuery");
//        String queryString = "10 * 1234";
        String queryString = "<data>{10 * 1234}</data>";
//        String queryString = "count(collection('unit-test-database'))\n";
//        String queryString = "<data>{count(collection('unit-test-database'))}</data>\n";
//        String queryString = "let $knownIds := collection(\"unit-test-database\")/DataNode/@ID\n"
//                + "let $duplicateDocumentCount := count($knownIds) - count(distinct-values($knownIds))\n"
//                + "let $childIds := collection(\"unit-test-database\")/DataNode/ChildId\n"
//                + "let $missingIds := distinct-values($childIds[not(.=$knownIds)])let $rootNodes := distinct-values($knownIds[not(.=$childIds)])return <DatabaseStats>\n"
//                + "<KnownDocuments>{count($knownIds)}</KnownDocuments>\n"
//                + "<MissingDocuments>{count($missingIds)}</MissingDocuments>\n"
//                + "<DuplicateDocuments>{$duplicateDocumentCount}</DuplicateDocuments>\n"
//                + "<RootDocuments>{count($rootNodes)}</RootDocuments>\n"
//                + "{for $rootDocId in $rootNodes return <RootDocumentID>{$rootDocId}</RootDocumentID>}\n"
//                + "</DatabaseStats>";
        RestDbAdaptor instance = new RestDbAdaptor(new URL(DataBaseManagerTest.restUrl), DataBaseManagerTest.restUser, DataBaseManagerTest.restPass);
        String expResult = "<data>12340</data>";
        String result = instance.executeQuery(DataBaseManagerTest.testDatabaseName, queryString);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}