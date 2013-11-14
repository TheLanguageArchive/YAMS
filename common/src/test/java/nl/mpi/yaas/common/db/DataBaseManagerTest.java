/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.yaas.common.db;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseLinks;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.HighlighableDataNode;
import nl.mpi.yaas.common.data.IconTable;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.NodeTypeImage;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Peter Withers
 */
public abstract class DataBaseManagerTest {

    static String testDatabaseName = "unit-test-database";
    static String restUrl = "http://localhost:8984/rest/";
//    static String restUrl = "http://tlatest03:8984/rest";
    static String restUser = "admin";
    static String restPass = "admin";

    abstract DbAdaptor getDbAdaptor() throws IOException, QueryException;

    public DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> getDataBaseManager(boolean insertData) throws IOException, QueryException, JAXBException, PluginException, ModelException {
        DbAdaptor dbAdaptor = getDbAdaptor();
        final DataBaseManager dataBaseManager = new DataBaseManager(HighlighableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, testDatabaseName);
        dataBaseManager.dropAllRecords();
        DatabaseLinks databaseLinks = new DatabaseLinks();
        if (insertData) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SerialisableDataNode.class, DataField.class, DataField.class, DataNodeType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            for (String testFileName : TestData.testFiles) {
                System.out.println("testFileName: " + testFileName);
                SerialisableDataNode dataNode = (SerialisableDataNode) unmarshaller.unmarshal(new StreamSource(DataBaseManagerTest.class.getResourceAsStream("/testdata/" + testFileName)), SerialisableDataNode.class).getValue();
                dataBaseManager.insertIntoDatabase(dataNode, true);
                final DataNodeLink dataNodeLink = new DataNodeLink();
                dataNodeLink.setIdString(dataNode.getID());
                databaseLinks.insertRootLink(dataNodeLink);
                databaseLinks.insertLinks(dataNodeLink, dataNode);
            }
        }
        dataBaseManager.getHandlesOfMissing(databaseLinks, 0);
        dataBaseManager.createIndexes();
        return dataBaseManager;
    }

    /**
     * Test of insertIntoDatabase method by waling a tree of metadata and
     * inserting it into the database.
     */
    @Test
    public void testSampleData() throws JAXBException, PluginException, QueryException, IOException, ModelException {
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);

        DatabaseStats databaseStats = dbManager.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(28, databaseStats.getKnownDocumentsCount());
        assertEquals(34, databaseStats.getMisingDocumentsCount());
//        assertEquals(0, databaseStats.getDuplicateDocumentsCount()); // calculating duplicates is very time consuming and is no longer done
        assertEquals(28, databaseStats.getRootDocumentsCount());
        final DataNodeId[] expectedArray = new DataNodeId[]{
            new DataNodeId("0b7899a7cd5875b653920820f680da43"),
            new DataNodeId("0bda78423e1c7619a1e7cd1104aa7e02"),
            new DataNodeId("0c8a62939fc0a5f90aed345641b96379"),
            new DataNodeId("0a03166921f32c2d1e6ef4806d09055e"),
            new DataNodeId("0e6f686abcef53f99f68a445c77068b9"),
            new DataNodeId("0b0601223681aafdbbdd704f18534a8a"),
            new DataNodeId("0d296020ab87454caa485073686b4c8c"),
            new DataNodeId("0d2f15fb77b1ad020a2b710724588118"),
            new DataNodeId("0b92e5b845d81837447c154dd49d2728"),
            new DataNodeId("1b8a54ac164c0d79f679546f28a7fd2e"),
            new DataNodeId("0a5d5d2b11a9e6d9f9f84cb6973b56d6"),
            new DataNodeId("0a4f1304c0f5d6a5a3c2f0076f68af1d"),
            new DataNodeId("0b3f141698c5c983daa1ab52ae12e310"),
            new DataNodeId("0aa7135ea9760514fee9d400ceb66109"),
            new DataNodeId("0ddef5d81b873c5fb8648ee44b8bb2a4"),
            new DataNodeId("0a5f98482990d8b5c2d2eff7a53fa326"),
            new DataNodeId("0f49b8cb9286bb8ca41d40480c0ca6b0"),
            new DataNodeId("0f4d9cdcd07a1d0c642bb11a0dd1cf2e"),
            new DataNodeId("0bffddf7dce6209f5b989a39b1392cea"),
            new DataNodeId("0b01e50850ee4032de418aac477b9d13"),
            new DataNodeId("1b3964d207c486edc5a3565e2b3eaa51"),
            new DataNodeId("0ba62767c0ea1bc1bdfd3581462ea2bc"),
            new DataNodeId("0a646a555c394adf97f10100490dd7f4"),
            new DataNodeId("0ac15a1d88ec87cf22c086e6861d892a"),
            new DataNodeId("0ec2bc0633f964958527d1bd3e366f3a"),
            new DataNodeId("0c13f64dcabfd367e87769de60881f1e"),
            new DataNodeId("1b0e86d0d3eda6c5981ad31232dd4dbb"),
            new DataNodeId("0e6a4c84769cebf23a65f2072a895fe8")
        };
        final List<DataNodeId> expected = Arrays.<DataNodeId>asList(expectedArray);
        final List<DataNodeId> actual = Arrays.asList(databaseStats.getRootDocumentsIDs());
        assertEquals(expected.size(), actual.size());
        for (DataNodeId dataNodeId : databaseStats.getRootDocumentsIDs()) {
            System.out.println("new DataNodeId(\"" + dataNodeId.getIdString() + "\"),");
        }
        assertArrayEquals(databaseStats.getRootDocumentsIDs(), expectedArray);
//        assertThat(actual, (Matcher) hasItems(expected));
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("0a646a555c394adf97f10100490dd7f4"));
        SerialisableDataNode dataNode = (SerialisableDataNode) dbManager.getNodeDatasByIDs(nodeIDs);
        assertEquals(1, dataNode.getChildList().size());
        assertTrue("Query took too long:" + databaseStats.getQueryTimeMS() + "ms", databaseStats.getQueryTimeMS() < 420);
    }

    @Test
    public void testGetDatabaseStats() throws JAXBException, PluginException, QueryException, IOException, ModelException {
//        dbAdaptor.dropAllRecords(testDatabaseName);
//        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, testDatabaseName);

        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(false);
        //dbManager.clearDatabaseStats();
        DatabaseStats databaseStats = dbManager.getDatabaseStats();
        System.out.println("DatabaseStats Query Time: " + databaseStats.getQueryTimeMS() + "ms");
        assertEquals(0, databaseStats.getKnownDocumentsCount());
        assertEquals(0, databaseStats.getMisingDocumentsCount());
//        assertEquals(0, databaseStats.getDuplicateDocumentsCount());
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
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
//        final DataBaseManager instance = new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, testDatabaseName);
        final ArrayList<DataNodeId> nodeIDs = new ArrayList<DataNodeId>();
        nodeIDs.add(new DataNodeId("0a4f1304c0f5d6a5a3c2f0076f68af1d"));
        SerialisableDataNode dataNode = (SerialisableDataNode) dbManager.getNodeDatasByIDs(nodeIDs);
        assertEquals(1, dataNode.getChildList().size());
    }

    /**
     * Test of getMetadataTypes method, of class DataBaseManager.
     */
    @Test
    public void testGetMetadataTypes() throws Exception {
        System.out.println("getMetadataTypes");
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        MetadataFileType metadataFileType = null;
        MetadataFileType[] result = dbManager.getMetadataTypes(metadataFileType);
        assertEquals("All Types", result[0].getLabel());
        assertEquals(28, result[0].getRecordCount());
        assertEquals("Corpus", result[1].getLabel());
        assertEquals(2, result[1].getRecordCount());
        assertEquals("Session", result[2].getLabel());
        assertEquals(26, result[2].getRecordCount());
    }

    /**
     * Test of getPathMetadataTypes method, of class DataBaseManager.
     */
    @Test
    public void testGetMetadataPaths() throws Exception {
        System.out.println("getMetadataPaths");
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        MetadataFileType[] result1 = dbManager.getMetadataPaths(null);
        assertEquals("All Paths", result1[0].getLabel());
        assertEquals(4464, result1[0].getRecordCount());
        assertEquals("Access.Date", result1[1].getLabel());
        assertEquals(195, result1[1].getRecordCount());
        assertEquals("CommunicationContext.EventStructure", result1[10].getLabel());
        assertEquals(26, result1[10].getRecordCount());
        MetadataFileType metadataFileType = new MetadataFileType() {
            @Override
            public String getType() {
                return "Corpus";
            }
        };
        MetadataFileType[] result2 = dbManager.getMetadataPaths(metadataFileType);
        assertEquals("All Paths", result2[0].getLabel());
        assertEquals(6, result2[0].getRecordCount());
        assertEquals("Description", result2[1].getLabel());
        assertEquals(2, result2[1].getRecordCount());
        assertEquals("Title", result2[3].getLabel());
        assertEquals(2, result2[2].getRecordCount());
    }

    /**
     * Test of getFieldMetadataTypes method, of class DataBaseManager.
     */
    @Test
    public void testGetMetadataFieldValues() throws Exception {
        System.out.println("getFieldMetadataTypes");
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        MetadataFileType[] result1 = dbManager.getMetadataFieldValues(null);
        assertEquals("", result1[0].getLabel());
        assertEquals(null, result1[0].getType());
        assertEquals(null, result1[0].getPath());
        assertEquals(2929, result1[0].getRecordCount());
        assertEquals(" 	Author: not applicable      	Title: radio: Radio 1 - Nieuws      	Publisher: not applicable      	Place: not applicable      	Date: not applicable      	Recording date: 2000 	Recording time: Unspecified 	From: national radio     	Produced from: Vlaamse Radio- en Televisieomroep    ", result1[9].getLabel());
        assertEquals(" 	Author: not applicable      	Title: radio: Radio 1 - Nieuws      	Publisher: not applicable      	Place: not applicable      	Date: not applicable      	Recording date: 2000 	Recording time: Unspecified 	From: national radio     	Produced from: Vlaamse Radio- en Televisieomroep    ", result1[9].getValue());
        assertEquals(1, result1[9].getRecordCount());
        MetadataFileType metadataFileType1 = new MetadataFileType() {
            @Override
            public String getType() {
                return "Corpus";
            }
        };
        MetadataFileType[] result2 = dbManager.getMetadataFieldValues(metadataFileType1);
        assertEquals("", result1[0].getLabel());
        assertEquals(1, result2[0].getRecordCount());
        assertEquals(" 	Author: not applicable      	Title: radio: Radio 1 - Nieuws      	Publisher: not applicable      	Place: not applicable      	Date: not applicable      	Recording date: 2000 	Recording time: Unspecified 	From: national radio     	Produced from: Vlaamse Radio- en Televisieomroep    ", result1[9].getLabel());
        assertEquals("Corpus", result2[1].getType());
        assertEquals(null, result2[1].getPath());
        assertEquals(2, result2[1].getRecordCount());
        MetadataFileType metadataFileType2 = new MetadataFileType() {
            @Override
            public String getType() {
                return "Corpus";
            }

            @Override
            public String getPath() {
                return "Description";
            }
        };
        MetadataFileType[] result3 = dbManager.getMetadataFieldValues(metadataFileType2);
        assertEquals("Sessions containing subjects of less than 17 years", result3[0].getLabel());
        assertEquals(1, result3[0].getRecordCount());
        assertEquals(2, result3.length);
    }

    /**
     * Test of getSearchResult method, of class DataBaseManager.
     */
    @Test
    public void testGetSearchResult() throws Exception {
        System.out.println("getSearchResult");
        MetadataFileType metadataFileType1 = new MetadataFileType() {
            @Override
            public String getType() {
                return "Session";
            }

            @Override
            public String getPath() {
                return "Description";
            }
        };
        ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
        searchParametersList.add(new SearchParameters(metadataFileType1, metadataFileType1, QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "Author: not applicable"));
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        HighlighableDataNode result1 = dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.intersect, searchParametersList);
        assertEquals("Search Results", result1.getID());
        assertEquals(19, result1.getChildIds().size());
        assertEquals(19, result1.getHighlights().size());
        searchParametersList.add(new SearchParameters(metadataFileType1, metadataFileType1, QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "LEMMATISATION"));
        HighlighableDataNode result2 = dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.intersect, searchParametersList);
        assertEquals(null, result2.getChildList());
        HighlighableDataNode result3 = dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.union, searchParametersList);
        assertEquals(26, result3.getChildIds().size());
        assertEquals(45, result3.getHighlights().size());
//        assertEquals(2, result3.getHighlightsForNode(result3.getChildIds().get(0).getIdString()).size());
//        assertEquals(".METATRANSCRIPT.Corpus.Name", result3.getHighlightsForNode(result3.getChildIds().get(0).getIdString()).get(0).getHighlightPath());
        // todo: the not clause is not excluding nodes but including them and needs to have a separate set for excluded nodes
        ArrayList<SearchParameters> searchParametersList2 = new ArrayList<SearchParameters>();
        searchParametersList2.add(new SearchParameters(metadataFileType1, metadataFileType1, QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "ESAT-KUL"));
        final HighlighableDataNode searchResult1 = dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.union, searchParametersList2);
        // count of ESAT-KUL = 12 fields 7 files
        assertEquals(7, searchResult1.getChildIds().size());
        assertEquals(12, searchResult1.getHighlights().size());
        searchParametersList2.clear();
        searchParametersList2.add(new SearchParameters(metadataFileType1, metadataFileType1, QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "CCL-KUL"));
        final HighlighableDataNode searchResult2 = dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.union, searchParametersList2);
        // count of CCL-KUL = 32 fields 10 files
        assertEquals(10, searchResult2.getChildIds().size());
        assertEquals(32, searchResult2.getHighlights().size());
        searchParametersList2.clear();

        searchParametersList2.add(new SearchParameters(metadataFileType1, metadataFileType1, QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "ESAT-KUL"));
        searchParametersList2.add(new SearchParameters(metadataFileType1, metadataFileType1, QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "CCL-KUL"));
        final HighlighableDataNode searchResult3 = dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.union, searchParametersList2);
        // there are some files which contain both terms, so this should not be correct
        assertEquals(10, searchResult3.getChildIds().size());
        assertEquals(44, searchResult3.getHighlights().size());

        assertEquals(7, dbManager.getSearchResult(QueryDataStructures.CriterionJoinType.intersect, searchParametersList2).getChildIds().size());
    }

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
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
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

    /**
     * Test of getTreeFacetTypes method, of class DataBaseManager.
     */
    @Test
    @Ignore
    public void testGetTreeFacetTypes() throws Exception {
        System.out.println("getTreeFacetTypes");
        MetadataFileType[] metadataFileTypes = null;
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        Object[] expResult = null;
        final MetadataFileType[] result = dbManager.getTreeFacetTypes(metadataFileTypes);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getHandlesOfMissing method, of class DataBaseManager.
     */
    @Test
    public void testGetHandlesOfMissing() throws Exception {
        System.out.println("getHandlesOfMissing");
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        String result = dbManager.getHandlesOfMissing();
        System.out.println("result:" + result);
        assertEquals(2719, result.length());
    }

    /**
     * Test of getDatabaseList method, of class DataBaseManager.
     */
    @Test
    public void testGetDatabaseList() throws Exception {
        System.out.println("getDatabaseList");
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        String[] result = dbManager.getDatabaseList();
        assertTrue("Unexpected db names length: " + result.length, result.length > 0);
    }

    /**
     * Test of getHandlesOfMissing method, of class DataBaseManager.
     */
    @Test
    public void testGetHandlesOfMissing_0args() throws Exception {
        System.out.println("getHandlesOfMissing");
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);
        String expResult = "http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/age0_male.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/age0_female.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_13.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_14.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_17.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_28.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_31.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_32.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_12.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_30.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_08.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_25.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_16.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_07.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_29.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_09.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_19.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_04.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_18.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_01.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_23.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_26.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_11.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_21.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_27.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_10.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_02.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_06.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_22.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_05.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_20.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_03.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_24.imdi http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/CGN_WAV_15.imdi";
        String result = dbManager.getHandlesOfMissing();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHandlesOfMissing method, of class DataBaseManager.
     */
    @Test
    public void testGetHandlesOfMissing_DatabaseLinks() throws Exception {
        System.out.println("getHandlesOfMissing");
        DatabaseLinks databaseLinks1 = new DatabaseLinks();
        final DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> dbManager = getDataBaseManager(true);

        Set<DataNodeLink> result0 = dbManager.getHandlesOfMissing(databaseLinks1, 10);
        assertEquals(10, result0.size());

        databaseLinks1.insertRootLink(new DataNodeLink("one", null));
        databaseLinks1.insertRootLink(new DataNodeLink("two", null));

        databaseLinks1.insertChildLink(new DataNodeLink("a", null));
        databaseLinks1.insertChildLink(new DataNodeLink("b", null));
        databaseLinks1.insertChildLink(new DataNodeLink("c", null));
        databaseLinks1.insertChildLink(new DataNodeLink("d", null));
        databaseLinks1.insertChildLink(new DataNodeLink("e", null));
        databaseLinks1.insertChildLink(new DataNodeLink("f", null));
        databaseLinks1.insertChildLink(new DataNodeLink("f", null));// duplicate

        Set<DataNodeLink> result1 = dbManager.getHandlesOfMissing(databaseLinks1, 10);
        assertEquals(10, result1.size());

        DatabaseLinks databaseLinks2 = new DatabaseLinks();
        databaseLinks2.insertRootLink(new DataNodeLink("one", null));// duplicate
        databaseLinks2.insertRootLink(new DataNodeLink("three", null));
        databaseLinks2.insertChildLink(new DataNodeLink("f", null));// duplicate
        databaseLinks2.insertChildLink(new DataNodeLink("g", null));
        databaseLinks2.insertChildLink(new DataNodeLink("h", null));
        databaseLinks2.insertChildLink(new DataNodeLink("i", null));
        databaseLinks2.insertChildLink(new DataNodeLink("j", null));
        databaseLinks2.insertChildLink(new DataNodeLink("k", null));
        databaseLinks2.insertChildLink(new DataNodeLink("l", null));
        final DataNodeLink dataNodeLink = new DataNodeLink();
        dataNodeLink.setIdString("0132fd35d7d2fd68faa904613c1bf6ad");
        dataNodeLink.setNodeUriString("Speaker Ages");
        databaseLinks2.insertChildLink(dataNodeLink); // this link is to a document that already exists in the database, so it must be removed making the end count 12
        databaseLinks2.insertRecentLink(dataNodeLink);
        int numberToGet = 3;
        Set<DataNodeLink> result2 = dbManager.getHandlesOfMissing(databaseLinks2, numberToGet);
        assertEquals("31", dbManager.dbAdaptor.executeQuery(testDatabaseName, "count(collection(\"unit-test-database\")/DatabaseLinks/RootDocumentLinks)"));
        assertEquals("46", dbManager.dbAdaptor.executeQuery(testDatabaseName, "count(collection(\"unit-test-database\")/DatabaseLinks/MissingDocumentLinks)"));
        assertEquals(numberToGet, result2.size());
    }
}
