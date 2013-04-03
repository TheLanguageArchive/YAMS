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
import nl.mpi.flap.plugin.PluginSessionStorage;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.MetadataFileType;
import org.junit.Assert;

/**
 *
 * @author petwit2
 */
public class DataBaseManagerTest extends TestCase {

    String projectDatabaseName = "unit-test-database";
//
//    public DataBaseManagerTest(String testName) {
//        super(testName);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    /**
//     * Test of getDatabaseProjectDirectory method, of class DataBaseManager.
//     */
//    public void testGetDatabaseProjectDirectory() {
//        System.out.println("getDatabaseProjectDirectory");
//        DataBaseManager instance = null;
//        File expResult = null;
//        File result = instance.getDatabaseProjectDirectory(projectDatabaseName);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    private PluginSessionStorage getPluginSessionStorage() {
        return new PluginSessionStorage() {
            private File tempWorkingDir;

            public File getApplicationSettingsDirectory() {
                if (tempWorkingDir == null) {
                    try {
                        tempWorkingDir = File.createTempFile("yaas-db", Long.toString(System.nanoTime()));
                        if (tempWorkingDir.exists()) {
                            if (!tempWorkingDir.delete()) {
                                throw new RuntimeException("Cannot create temp dir!");
                            }
                        }
                        if (tempWorkingDir.mkdir()) {
                            tempWorkingDir.deleteOnExit();
                        } else {
                            fail("Cannot create temp dir!");
                        }
                        System.out.println("Using working directory: " + tempWorkingDir.getAbsolutePath());
                    } catch (IOException exception) {
                        fail(exception.getMessage());
                    }
                }
                return tempWorkingDir;
            }

            public File getProjectDirectory() {
                return getApplicationSettingsDirectory();
            }

            public File getProjectWorkingDirectory() {
                return new File(getApplicationSettingsDirectory(), "WorkingFiles");
            }
        };
    }

    /**
     * Test of createDatabase method, of class DataBaseManager.
     */
    public void testCreateDatabase() throws Exception {
        System.out.println("createDatabase");
        final DataBaseManager instance = new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
        instance.createDatabase();
    }

    /**
     * Test of insertIntoDatabase method by waling a tree of metadata and
     * inserting it into the database.
     */
    public void testInsertIntoDatabase() throws JAXBException, PluginException, QueryException {
        System.out.println("walkTreeInsertingNodes");
        final PluginSessionStorage pluginSessionStorage = getPluginSessionStorage();
        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, pluginSessionStorage, projectDatabaseName);
        JAXBContext jaxbContext = JAXBContext.newInstance(SerialisableDataNode.class, DataField.class, DataField.class, DataNodeType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        for (String dataXmlString : TestData.testData) {
            System.out.println("dataXmlString: " + dataXmlString);
            SerialisableDataNode dataNode = (SerialisableDataNode) unmarshaller.unmarshal(new StreamSource(new StringReader(dataXmlString)), SerialisableDataNode.class).getValue();
            instance.insertIntoDatabase(dataNode);
        }
        DatabaseStats databaseStats = instance.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(databaseStats.getKnownDocumentsCount(), 55);
        assertEquals(databaseStats.getMisingDocumentsCount(), 0);
        assertEquals(databaseStats.getRootDocumentsCount(), 16);
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
        SerialisableDataNode dataNode = (SerialisableDataNode) instance.getNodeDatasByIDs(nodeIDs);
        assertEquals(12, dataNode.getChildList().size());
        assertTrue("Query took too long:" + databaseStats.getQueryTimeMS() + "ms", databaseStats.getQueryTimeMS() < 310);
    }

    public void testGetDatabaseStats() throws JAXBException, PluginException, QueryException {
        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
        DatabaseStats databaseStats = instance.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(databaseStats.getKnownDocumentsCount(), 0);
        assertEquals(databaseStats.getMisingDocumentsCount(), 0);
        assertEquals(databaseStats.getRootDocumentsCount(), 0);
        Assert.assertArrayEquals(databaseStats.getRootDocumentsIDs(), new String[0]);
    }

    public void testGetNodeDatasByIDs() throws QueryException {
        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"));
        SerialisableDataNode dataNode = (SerialisableDataNode) instance.getNodeDatasByIDs(nodeIDs);
        assertEquals(dataNode.getChildList(), null);
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
//        DataBaseManager instance = new DataBaseManager(AbstractDataNode.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
//        String expResult = "a resutl";
//        AbstractDataNode result = (AbstractDataNode) instance.getSearchResult(criterionJoinType, searchParametersList);
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
//        DataBaseManager instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getPathMetadataTypes(metadataFileType);
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
//        DataBaseManager instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getFieldMetadataTypes(metadataFileType);
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
//        DataBaseManager instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getMetadataTypes(metadataFileType);
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
//        DataBaseManager instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getTreeFieldTypes(metadataFileType, fastQuery);
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
//        DataBaseManager instance = null;
//        Object expResult = null;
//        Object result = instance.getTreeData(treeBranchTypeList);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
