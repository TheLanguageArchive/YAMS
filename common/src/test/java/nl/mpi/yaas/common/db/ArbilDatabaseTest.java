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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import junit.framework.TestCase;
import nl.mpi.arbil.ArbilDesktopInjector;
import nl.mpi.arbil.ArbilVersion;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.data.ArbilDataNodeContainer;
import nl.mpi.arbil.data.ArbilDataNodeLoader;
import nl.mpi.arbil.data.ArbilField;
import nl.mpi.arbil.data.ArbilTreeHelper;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.userstorage.ArbilSessionStorage;
import nl.mpi.arbil.util.ApplicationVersionManager;
import nl.mpi.arbil.util.ArbilMimeHashQueue;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.AbstractDataNode;
import nl.mpi.flap.plugin.PluginArbilDataNodeLoader;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.flap.plugin.PluginSessionStorage;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.QueryDataStructures.CriterionJoinType;
import nl.mpi.yaas.common.data.SearchParameters;

/**
 *
 * @author petwit2
 */
public class ArbilDatabaseTest extends TestCase {

    String projectDatabaseName = "unit-test-database";
//
//    public ArbilDatabaseTest(String testName) {
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
//     * Test of getDatabaseProjectDirectory method, of class ArbilDatabase.
//     */
//    public void testGetDatabaseProjectDirectory() {
//        System.out.println("getDatabaseProjectDirectory");
//        ArbilDatabase instance = null;
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
     * Test of createDatabase method, of class ArbilDatabase.
     */
    public void testCreateDatabase() throws Exception {
        System.out.println("createDatabase");
        final ArbilDatabase instance = new ArbilDatabase(AbstractDataNode.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
        instance.createDatabase();
    }

    /**
     * Test of insertIntoDatabase method by waling a tree of metadata and
     * inserting it into the database.
     */
    public void testInsertIntoDatabase() {
        System.out.println("walkTreeInsertingNodes");
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new ArbilVersion());
        final ArbilDesktopInjector injector = new ArbilDesktopInjector();
        injector.injectHandlers(versionManager);

        final ArbilWindowManager arbilWindowManager = injector.getWindowManager();
        final ArbilSessionStorage arbilSessionStorage = new ArbilSessionStorage();
        PluginArbilDataNodeLoader dataNodeLoader = new ArbilDataNodeLoader(arbilWindowManager, arbilSessionStorage, new ArbilMimeHashQueue(arbilWindowManager, arbilSessionStorage), new ArbilTreeHelper(arbilSessionStorage, arbilWindowManager));
        try {
            final ArbilDatabase instance = new ArbilDatabase(AbstractDataNode.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
            URI startURI = new URI("http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/cgn.imdi");
            ArbilDataNodeContainer nodeContainer = null; //new ArbilDataNodeContainer() {
//                public void dataNodeRemoved(ArbilNode dataNode) {
////                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                public void dataNodeIconCleared(ArbilNode dataNode) {
//                    if (dataNode.isDataLoaded()) {
//                        try {
//                            instance.insertIntoDatabase(dataNode);
//                        } catch (PluginException exception) {
//                            fail(exception.getMessage());
//                        }
//                    }
//                }
//
//                public void dataNodeChildAdded(ArbilNode destination, ArbilNode newChildNode) {
////                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                public boolean isFullyLoadedNodeRequired() {
//                    return true;
//                }
//            };
            ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, startURI);
            loadChildNodes(dataNodeLoader, dataNode);
            instance.insertIntoDatabase(dataNode, ArbilField.class);

            // TODO review the generated test code and remove the default call to fail.
//            fail("The test case is a prototype.");
        } catch (URISyntaxException exception) {
            fail(exception.getMessage());
        } catch (InterruptedException exception) {
            fail(exception.getMessage());
        } catch (PluginException exception) {
            fail(exception.getMessage());
        } catch (QueryException exception) {
            fail(exception.getMessage());
        }
    }

    /**
     * Test of getSearchResult method, of class ArbilDatabase.
     */
    public void testGetSearchResult() throws Exception {
        System.out.println("getSearchResult");
        CriterionJoinType criterionJoinType = CriterionJoinType.intersect;
        ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
        searchParametersList.add(new SearchParameters(new MetadataFileType(), new MetadataFileType(), QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.equals, ""));
        //todo: add various search parameters
//        searchParametersList.add(new SearchParameters(new MetadataFileType(), new MetadataFileType(), QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.equals, ""));
        ArbilDatabase instance = new ArbilDatabase(AbstractDataNode.class, MetadataFileType.class, getPluginSessionStorage(), projectDatabaseName);
        String expResult = "a resutl";
        AbstractDataNode result = (AbstractDataNode) instance.getSearchResult(criterionJoinType, searchParametersList);
        System.out.println("result:" + result.toString());
        assertEquals(expResult, result.getName());
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
//    /**
//     * Test of getPathMetadataTypes method, of class ArbilDatabase.
//     */
//    public void testGetPathMetadataTypes() throws Exception {
//        System.out.println("getPathMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        ArbilDatabase instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getPathMetadataTypes(metadataFileType);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getFieldMetadataTypes method, of class ArbilDatabase.
//     */
//    public void testGetFieldMetadataTypes() throws Exception {
//        System.out.println("getFieldMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        ArbilDatabase instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getFieldMetadataTypes(metadataFileType);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getMetadataTypes method, of class ArbilDatabase.
//     */
//    public void testGetMetadataTypes() throws Exception {
//        System.out.println("getMetadataTypes");
//        MetadataFileType metadataFileType = null;
//        ArbilDatabase instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getMetadataTypes(metadataFileType);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getTreeFieldTypes method, of class ArbilDatabase.
//     */
//    public void testGetTreeFieldTypes() throws Exception {
//        System.out.println("getTreeFieldTypes");
//        MetadataFileType metadataFileType = null;
//        boolean fastQuery = false;
//        ArbilDatabase instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.getTreeFieldTypes(metadataFileType, fastQuery);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of getTreeData method, of class ArbilDatabase.
//     */
//    public void testGetTreeData() throws Exception {
//        System.out.println("getTreeData");
//        ArrayList<MetadataFileType> treeBranchTypeList = null;
//        ArbilDatabase instance = null;
//        Object expResult = null;
//        Object result = instance.getTreeData(treeBranchTypeList);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    private int numberToLoad = 10;

    private void loadChildNodes(PluginArbilDataNodeLoader dataNodeLoader, ArbilDataNode dataNode) throws InterruptedException {
        System.out.println("Loading: " + numberToLoad);
        if (numberToLoad < 0) {
            return;
        }
        if (dataNode.getLoadingState() != ArbilDataNode.LoadingState.LOADED) {
            dataNode.reloadNode();
        }
        while (dataNode.getLoadingState() != ArbilDataNode.LoadingState.LOADED) {
            Thread.sleep(100);
        }
        numberToLoad--;
        for (ArbilDataNode childNode : dataNode.getChildArray()) {
            loadChildNodes(dataNodeLoader, childNode);
        }
    }
}
