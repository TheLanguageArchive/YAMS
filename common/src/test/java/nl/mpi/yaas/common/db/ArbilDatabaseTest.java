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
import java.util.ArrayList;
import junit.framework.TestCase;
import nl.mpi.arbil.userstorage.ArbilSessionStorage;
import nl.mpi.flap.model.AbstractDataNode;
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

//    String projectDatabaseName = "unit-test-database";
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
            // todo: remove this user home directory for a temp directory and copy some metadata files from the test resources into the temp directory
            public File getApplicationSettingsDirectory() {
                return new File("/Users/petwit2/.arbil/");
            }

            public File getProjectDirectory() {
                return new File("/Users/petwit2/.arbil/");
            }

            public File getProjectWorkingDirectory() {
                return new File("/Users/petwit2/.arbil/ArbilWorkingFiles/");
            }
        };
    }

//    /**
//     * Test of createDatabase method, of class ArbilDatabase.
//     */
//    public void testCreateDatabase() throws Exception {
//        System.out.println("createDatabase");
//        ArbilDatabase instance = new ArbilDatabase(AbstractDataNode.class, null, getPluginSessionStorage());
//        instance.createDatabase();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
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
        ArbilDatabase instance = new ArbilDatabase(AbstractDataNode.class, MetadataFileType.class, getPluginSessionStorage());
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
}
