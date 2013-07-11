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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.IconTable;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.NodeTypeImage;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Peter Withers
 */
public abstract class DataBaseManagerTest {

    static String projectDatabaseName = "unit-test-database";

    abstract DbAdaptor getDbAdaptor() throws IOException, QueryException;

    public DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> getDataBaseManager(boolean insertData) throws IOException, QueryException, JAXBException, PluginException, ModelException {
        DbAdaptor dbAdaptor = getDbAdaptor();
        final DataBaseManager dataBaseManager = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);
        dataBaseManager.dropAllRecords();
        if (insertData) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SerialisableDataNode.class, DataField.class, DataField.class, DataNodeType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            for (String dataXmlString : TestData.testData) {
                System.out.println("dataXmlString: " + dataXmlString);
                SerialisableDataNode dataNode = (SerialisableDataNode) unmarshaller.unmarshal(new StreamSource(new StringReader(dataXmlString)), SerialisableDataNode.class).getValue();
                dataBaseManager.insertIntoDatabase(dataNode, false);
            }
        }
        dataBaseManager.createIndexes();
        return dataBaseManager;
    }

    /**
     * Test of insertIntoDatabase method by waling a tree of metadata and
     * inserting it into the database.
     */
    @Test
    public void testSampleData() throws JAXBException, PluginException, QueryException, IOException, ModelException {
        final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);

        DatabaseStats databaseStats = dbManager.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(17, databaseStats.getKnownDocumentsCount());
        assertEquals(7, databaseStats.getMisingDocumentsCount());
        assertEquals(0, databaseStats.getDuplicateDocumentsCount());
        assertEquals(17, databaseStats.getRootDocumentsCount());
        assertArrayEquals(databaseStats.getRootDocumentsIDs(), new DataNodeId[]{
            new DataNodeId("hdl:1839/00-0000-0000-0001-2AB1-4"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2FA3-5"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2FA4-B"),
            new DataNodeId("hdl:1839/00-0000-0000-0008-CAD1-B"),
            new DataNodeId("hdl:1839/00-0000-0000-0008-C805-D"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2C2D-F"),
            new DataNodeId("hdl:1839/00-0000-0000-000D-B73D-9"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2A9B-9"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2AA2-6"),
            new DataNodeId("hdl:1839/00-0000-0000-0004-D511-0"),
            new DataNodeId("hdl:1839/00-0000-0000-0004-D512-F"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2AB4-0"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2E76-0"),
            new DataNodeId("hdl:1839/00-0000-0000-000D-B743-0"),
            new DataNodeId("hdl:1839/00-0000-0000-0001-2E77-E"),
            new DataNodeId("0132fd35d7d2fd68faa904613c1bf6ad")
        });
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"));
        SerialisableDataNode dataNode = (SerialisableDataNode) dbManager.getNodeDatasByIDs(nodeIDs);
        assertEquals(1, dataNode.getChildList().size());
        assertTrue("Query took too long:" + databaseStats.getQueryTimeMS() + "ms", databaseStats.getQueryTimeMS() < 420);
    }

    @Test
    public void testGetDatabaseStats() throws JAXBException, PluginException, QueryException, IOException, ModelException {
//        dbAdaptor.dropAllRecords(projectDatabaseName);
//        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);

        final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(false);
        DatabaseStats databaseStats = dbManager.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(0, databaseStats.getKnownDocumentsCount());
        assertEquals(0, databaseStats.getMisingDocumentsCount());
        assertEquals(0, databaseStats.getDuplicateDocumentsCount());
        assertEquals(0, databaseStats.getRootDocumentsCount());
        Assert.assertArrayEquals(databaseStats.getRootDocumentsIDs(), new String[0]);

        assertFalse("Cached db stats should not exist at this point", databaseStats.isIsCachedResults());
        databaseStats = dbManager.getDatabaseStats();
        assertTrue("Failed to use the cached db stats", databaseStats.isIsCachedResults());
        dbManager.clearDatabaseStats();
        databaseStats = dbManager.getDatabaseStats();
        assertFalse("Failed to clear the db stats cache", databaseStats.isIsCachedResults());
    }

    @Test
    public void testGetNodeDatasByIDs() throws QueryException, IOException, JAXBException, PluginException, ModelException {
        final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
//        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, projectDatabaseName);
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("hdl:1839/00-0000-0000-0001-2A9A-4"));
        SerialisableDataNode dataNode = (SerialisableDataNode) dbManager.getNodeDatasByIDs(nodeIDs);
        assertEquals(1, dataNode.getChildList().size());
    }

    /**
     * Test of getMetadataTypes method, of class DataBaseManager.
     */
    @Test
    public void testGetMetadataTypes() throws Exception {
        System.out.println("getMetadataTypes");
        final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        MetadataFileType metadataFileType = null;
        MetadataFileType[] result = dbManager.getMetadataTypes(metadataFileType);
        assertEquals("All Types (17)", result[0].toString());
        assertEquals("Corpus (1)", result[1].toString());
        assertEquals("Test (4)", result[2].toString());
    }

    /**
     * Test of getPathMetadataTypes method, of class DataBaseManager.
     */
    @Test
    public void testGetMetadataPaths() throws Exception {
        System.out.println("getMetadataPaths");
        final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        MetadataFileType[] result1 = dbManager.getMetadataPaths(null);
        assertEquals("All Paths (41)", result1[0].toString());
        assertEquals("Access.Availability (2)", result1[1].toString());
        assertEquals("ContentType (2)", result1[10].toString());
        MetadataFileType metadataFileType = new MetadataFileType() {
            @Override
            public String getType() {
                return "Corpus";
            }
        };
        MetadataFileType[] result2 = dbManager.getMetadataPaths(metadataFileType);
        assertEquals("All Paths (3)", result2[0].toString());
        assertEquals("Description (1)", result2[1].toString());
        assertEquals("Title (1)", result2[3].toString());
    }

//    /**
//     * Test of getFieldMetadataTypes method, of class DataBaseManager.
//     */
//    @Test
//    public void testGetFieldMetadataTypes() throws Exception {
//        System.out.println("getFieldMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        DataBaseManager instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getFieldMetadataTypes(metadataFileType);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTreeFieldTypes method, of class DataBaseManager.
//     */
//    @Test
//    public void testGetTreeFieldTypes() throws Exception {
//        System.out.println("getTreeFieldTypes");
//        MetadataFileType metadataFileType = null;
//        boolean fastQuery = false;
//        DataBaseManager instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getTreeFieldTypes(metadataFileType, fastQuery);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of insertNodeIconsIntoDatabase method, of class DataBaseManager.
     */
    @Test
    public void testInsertNodeIconsIntoDatabase() throws Exception {
        System.out.println("insertNodeIconsIntoDatabase");
        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        IconTable iconTable = new IconTable();
        iconTable.addTypeIcon(new NodeTypeImage(new DataNodeType("a", "b", DataNodeType.FormatType.cmdi), new ImageIcon(bufferedImage).getImage()));
        iconTable.addTypeIcon(new NodeTypeImage(new DataNodeType("b", "c", DataNodeType.FormatType.cmdi), new ImageIcon(bufferedImage).getImage()));
        iconTable.addTypeIcon(new NodeTypeImage(new DataNodeType("b", "c", DataNodeType.FormatType.imdi), new ImageIcon(bufferedImage).getImage()));
        iconTable.addTypeIcon(new NodeTypeImage(new DataNodeType("b", "c", DataNodeType.FormatType.imdi), new ImageIcon(bufferedImage).getImage()));

        IconTable iconTable2 = new IconTable();
        iconTable2.addTypeIcon(new NodeTypeImage(new DataNodeType("a", "b", DataNodeType.FormatType.cmdi), new ImageIcon(bufferedImage).getImage()));
        iconTable2.addTypeIcon(new NodeTypeImage(new DataNodeType("b", "c", DataNodeType.FormatType.xml), new ImageIcon(bufferedImage).getImage()));
        iconTable2.addTypeIcon(new NodeTypeImage(new DataNodeType("s", "c", DataNodeType.FormatType.cmdi), new ImageIcon(bufferedImage).getImage()));
        iconTable2.addTypeIcon(new NodeTypeImage(new DataNodeType("b", "s", DataNodeType.FormatType.imdi), new ImageIcon(bufferedImage).getImage()));
        final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        final IconTable nodeIcons1 = dbManager.insertNodeIconsIntoDatabase(iconTable);
        // test that all three node dype icons have been added
        assertEquals(3, nodeIcons1.getNodeTypeImageSet().size());
        final IconTable nodeIcons2 = dbManager.insertNodeIconsIntoDatabase(iconTable2);
        // test that two additional node type icons have been added and the duplicates have not
        assertEquals(6, nodeIcons2.getNodeTypeImageSet().size());
        // test the base64 icons
        final IconTableBase64 iconTableBase64 = dbManager.getNodeIconsBase64();
        assertEquals(6, iconTableBase64.getNodeTypeImageSet().size());
    }
}
