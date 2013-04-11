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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import junit.framework.TestCase;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.MetadataFileType;
import org.junit.Assert;
import org.junit.Before;

/**
 *
 * @author petwit2
 */
public class DataBaseManagerTest extends TestCase {

    String projectDatabaseName = "unit-test-database";
    final DbAdaptor dbAdaptor;
    final DataBaseManager dbManager;

    public DataBaseManagerTest() throws IOException, QueryException {
        dbAdaptor = new LocalDbAdaptor(getTempDirectory());
        dbManager = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);
    }

    private File getTempDirectory() throws IOException {

//        File tempWorkingDir = File.createTempFile("yaas-db", "-tmp");
//        File tempWorkingDir = File.createTempFile("yaas-db", Long.toString(System.nanoTime()), new File("./target"));
        File tempWorkingDir = new File(new File("target").getAbsoluteFile(), "yaas-db");
//        File tempWorkingDir = File.createTempFile("yaas-db", "", new File("./target"));
        // todo: resolve why basex cant read long file names and move back the the temp dir and delete the old directory
//        if (tempWorkingDir.exists()) {
//            if (!tempWorkingDir.delete()) {
//                throw new RuntimeException("Cannot create temp dir!");
//            }
//        }
//        if (tempWorkingDir.mkdir()) {
//            tempWorkingDir.deleteOnExit();
//        } else {
//            fail("Cannot create temp dir!");
//        }
        System.out.println("Using working directory: " + tempWorkingDir.getAbsolutePath());
        return tempWorkingDir;
    }

    /**
     * Test of createDatabase method, of class DataBaseManager.
     */
    public void testCreateDatabase() throws Exception {
        System.out.println("createDatabase");
        final DataBaseManager instance = new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);
//        dbManager.createDatabase();
    }

    /**
     * Test of insertIntoDatabase method by waling a tree of metadata and
     * inserting it into the database.
     */
    @Before
    public void testInsertIntoDatabase() throws JAXBException, PluginException, QueryException, IOException {
        System.out.println("walkTreeInsertingNodes");
        dbAdaptor.dropAndRecreateDb(projectDatabaseName);
        JAXBContext jaxbContext = JAXBContext.newInstance(SerialisableDataNode.class, DataField.class, DataField.class, DataNodeType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        for (String dataXmlString : TestData.testData) {
            System.out.println("dataXmlString: " + dataXmlString);
            SerialisableDataNode dataNode = (SerialisableDataNode) unmarshaller.unmarshal(new StreamSource(new StringReader(dataXmlString)), SerialisableDataNode.class).getValue();
            dbManager.insertIntoDatabase(dataNode, false);
        }
        DatabaseStats databaseStats = dbManager.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(55, databaseStats.getKnownDocumentsCount());
        assertEquals(0, databaseStats.getMisingDocumentsCount());
        assertEquals(39, databaseStats.getDuplicateDocumentsCount());
        assertEquals(16, databaseStats.getRootDocumentsCount());
        Assert.assertArrayEquals(databaseStats.getRootDocumentsIDs(), new DataNodeId[]{
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2A9B-9"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2AB1-4"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2FA3-5"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2FA4-B"),
                    new DataNodeId("hdl:1839/00-0000-0000-0008-CAD1-B"),
                    new DataNodeId("hdl:1839/00-0000-0000-0008-C805-D"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2AB4-0"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2C2D-F"),
                    new DataNodeId("hdl:1839/00-0000-0000-000D-B73D-9"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2AA2-6"),
                    new DataNodeId("hdl:1839/00-0000-0000-0004-D511-0"),
                    new DataNodeId("hdl:1839/00-0000-0000-0004-D512-F"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2E76-0"),
                    new DataNodeId("hdl:1839/00-0000-0000-000D-B743-0"),
                    new DataNodeId("hdl:1839/00-0000-0000-0001-2E77-E")
                });
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"));
        SerialisableDataNode dataNode = (SerialisableDataNode) dbManager.getNodeDatasByIDs(nodeIDs);
        assertEquals(12, dataNode.getChildList().size());
        assertTrue("Query took too long:" + databaseStats.getQueryTimeMS() + "ms", databaseStats.getQueryTimeMS() < 420);
    }

    public void testGetDatabaseStats() throws JAXBException, PluginException, QueryException, IOException {
        dbAdaptor.dropAndRecreateDb(projectDatabaseName);
        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);
        DatabaseStats databaseStats = instance.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(0, databaseStats.getKnownDocumentsCount());
        assertEquals(0, databaseStats.getMisingDocumentsCount());
        assertEquals(0, databaseStats.getDuplicateDocumentsCount());
        assertEquals(0, databaseStats.getRootDocumentsCount());
        Assert.assertArrayEquals(databaseStats.getRootDocumentsIDs(), new String[0]);

        assertFalse("Cached db stats should not exist at this point", databaseStats.isIsCachedResults());
        databaseStats = instance.getDatabaseStats();
        assertTrue("Failed to use the cached db stats", databaseStats.isIsCachedResults());
        instance.clearDatabaseStats();
        databaseStats = instance.getDatabaseStats();
        assertFalse("Failed to clear the db stats cache", databaseStats.isIsCachedResults());
    }

    public void testGetNodeDatasByIDs() throws QueryException, IOException {
        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"));
        SerialisableDataNode dataNode = (SerialisableDataNode) instance.getNodeDatasByIDs(nodeIDs);
        assertEquals(null, dataNode.getChildList());
    }
    /**
     * Test of getSearchResult method, of class DataBaseManager.
     */
//    public void testGetSearchResult() throws Exception {
//        System.out.println("getSearchResult");
//        CriterionJoinType criterionJoinType = CriterionJoinType.intersect;
//        ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
//        searchParametersList.add(new SearchParameters(new MetadataFileType(), new MetadataFileType(), QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.equals, ""));
//        //todo: add various search parameters
////        searchParametersList.add(new SearchParameters(new MetadataFileType(), new MetadataFileType(), QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.equals, ""));
//        DataBaseManager dbManager = new DataBaseManager(AbstractDataNode.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
//        String expResult = "a resutl";
//        AbstractDataNode result = (AbstractDataNode) dbManager.getSearchResult(criterionJoinType, searchParametersList);
//        System.out.println("result:" + result.toString());
//        assertEquals(expResult, result.getName());
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getPathMetadataTypes method, of class DataBaseManager.
//     */
//    public void testGetPathMetadataTypes() throws Exception {
//        System.out.println("getPathMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        DataBaseManager dbManager = null;
//        Object[] expResult = null;
//        Object[] result = dbManager.getPathMetadataTypes(metadataFileType);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getFieldMetadataTypes method, of class DataBaseManager.
//     */
//    public void testGetFieldMetadataTypes() throws Exception {
//        System.out.println("getFieldMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        DataBaseManager dbManager = null;
//        Object[] expResult = null;
//        Object[] result = dbManager.getFieldMetadataTypes(metadataFileType);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getMetadataTypes method, of class DataBaseManager.
//     */
//    public void testGetMetadataTypes() throws Exception {
//        System.out.println("getMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        DataBaseManager dbManager = null;
//        Object[] expResult = null;
//        Object[] result = dbManager.getMetadataTypes(metadataFileType);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getTreeFieldTypes method, of class DataBaseManager.
//     */
//    public void testGetTreeFieldTypes() throws Exception {
//        System.out.println("getTreeFieldTypes");
//        MetadataFileType metadataFileType = null;
//        boolean fastQuery = false;
//        DataBaseManager dbManager = null;
//        Object[] expResult = null;
//        Object[] result = dbManager.getTreeFieldTypes(metadataFileType, fastQuery);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getTreeData method, of class DataBaseManager.
//     */
//    public void testGetTreeData() throws Exception {
//        System.out.println("getTreeData");
//        ArrayList<MetadataFileType> treeBranchTypeList = null;
//        DataBaseManager dbManager = null;
//        Object expResult = null;
//        Object result = dbManager.getTreeData(treeBranchTypeList);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
