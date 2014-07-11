/*
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
package nl.mpi.yaas.client;

import java.util.ArrayList;
import nl.mpi.yams.common.data.QueryDataStructures;
import nl.mpi.yams.common.data.SearchParameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class HistoryDataTest {

    public HistoryDataTest() {
    }

//    public void parseSearchString(String searchString) {
        /*
         https://support.google.com/websearch/answer/136861?hl=en
         https://support.google.com/websearch/answer/2466433
         move db tree above the search parameters maybe with a reveal panel
         add the search within and add to table buttons to the popup panel
         add a collapse/reveal panel to the search parameters with a linked text input that takes google like search parameters + - “field:” 


         union,,,is,equals,semi-spontaneous,,,is,equals,Africa,,,is,equals,Ghana,,,is,equals,Gesture%20Project

         semi-spontaneous Africa Ghana “Gesture Project”*/
//    }
//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of getDatabaseName method, of class HistoryData.
//     */
//    @Test
//    public void testGetDatabaseName() {
//        System.out.println("getDatabaseName");
//        HistoryData instance = new HistoryData();
//        String expResult = "";
//        String result = instance.getDatabaseName();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDatabaseName method, of class HistoryData.
//     */
//    @Test
//    public void testSetDatabaseName() {
//        System.out.println("setDatabaseName");
//        String databaseName = "";
//        HistoryData instance = new HistoryData();
//        instance.setDatabaseName(databaseName);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parseSearchString method, of class HistoryData.
//     */
//    @Test
//    public void testParseSearchString() {
//        /*move db tree above the search parameters maybe with a reveal panel
//add the search within and add to table buttons to the popup panel
//add a collapse/reveal panel to the search parameters with a linked text input that takes google like search parameters + - “field:” 
//
//
//union,,,is,equals,semi-spontaneous,,,is,equals,Africa,,,is,equals,Ghana,,,is,equals,Gesture%20Project
//
//semi-spontaneous Africa Ghana “Gesture Project”*/
//        
//        System.out.println("parseSearchString");
//        String searchString = "";
//        HistoryData instance = new HistoryData();
//        instance.parseSearchString(searchString);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getHistoryToken method, of class HistoryData.
//     */
//    @Test
//    public void testGetHistoryToken() {
//        //http://127.0.0.1:8888/yaas.html?gwt.codesvr=127.0.0.1:9997#EWE-2013-11-13,union,,,is,equals,semi-spontaneous,,,is,equals,Africa,,,is,equals,Ghana,,,is,equals,Gesture%20Project
//        System.out.println("getHistoryToken");
//        HistoryData instance = new HistoryData();
//        String expResult = "";
//        String result = instance.getHistoryToken();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parseHistoryToken method, of class HistoryData.
//     */
//    @Test
//    public void testParseHistoryToken() {
//        //http://127.0.0.1:8888/yaas.html?gwt.codesvr=127.0.0.1:9997#EWE-2013-11-13,union,,,is,equals,semi-spontaneous,,,is,equals,Africa,,,is,equals,Ghana,,,is,equals,Gesture%20Project
//        System.out.println("parseHistoryToken");
//        String historyToken = "";
//        HistoryData instance = new HistoryData();
//        instance.parseHistoryToken(historyToken);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCriterionJoinType method, of class HistoryData.
//     */
//    @Test
//    public void testGetCriterionJoinType() {
//        System.out.println("getCriterionJoinType");
//        HistoryData instance = new HistoryData();
//        QueryDataStructures.CriterionJoinType expResult = null;
//        QueryDataStructures.CriterionJoinType result = instance.getCriterionJoinType();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCriterionJoinType method, of class HistoryData.
//     */
//    @Test
//    public void testSetCriterionJoinType() {
//        System.out.println("setCriterionJoinType");
//        QueryDataStructures.CriterionJoinType criterionJoinType = null;
//        HistoryData instance = new HistoryData();
//        instance.setCriterionJoinType(criterionJoinType);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSearchParametersList method, of class HistoryData.
//     */
//    @Test
//    public void testGetSearchParametersList() {
//        System.out.println("getSearchParametersList");
//        HistoryData instance = new HistoryData();
//        ArrayList<SearchParameters> expResult = null;
//        ArrayList<SearchParameters> result = instance.getSearchParametersList();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSearchParametersList method, of class HistoryData.
//     */
//    @Test
//    public void testSetSearchParametersList() {
//        System.out.println("setSearchParametersList");
//        ArrayList<SearchParameters> searchParametersList = null;
//        HistoryData instance = new HistoryData();
//        instance.setSearchParametersList(searchParametersList);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
