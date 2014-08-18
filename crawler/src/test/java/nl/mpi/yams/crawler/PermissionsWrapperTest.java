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
package nl.mpi.yams.crawler;

import junit.framework.TestCase;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.DataNodeType;

/**
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class PermissionsWrapperTest extends TestCase {

    private PermissionsWrapper getPermissionsWrapper() {
//        return new PermissionsWrapper("http://localhost:8080/yams-cs-connector/rest/node?id=", "hdl:11142/00-75BAA2D4-FBCF-4E0C-94BE-624808AA1959");
        return new PermissionsWrapper("https://lux16.mpi.nl/ds/yams-cs-connector/rest/node?id=", "hdl:11142/00-75BAA2D4-FBCF-4E0C-94BE-624808AA1959");
//00-75BAA2D4-FBCF-4E0C-94BE-624808AA1959        
//        00-75BAA2D4-FBCF-4E0C-94BE-624808AA1959
    }

    /**
     * Test of getLabel method, of class PermissionsWrapper.
     */
    public void testGetLabel() {
        System.out.println("getLabel");
        PermissionsWrapper instance = getPermissionsWrapper();
        String expResult = "testCollection";
        String result = instance.getLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class PermissionsWrapper.
     */
    public void testGetType() {
        System.out.println("getType");
        PermissionsWrapper instance = getPermissionsWrapper();
        DataNodeType result = instance.getType();
        assertEquals("cmdi", result.getFormat().name());
        assertEquals("", result.getID());
        assertEquals("", result.getLabel());
        assertEquals("application/cmdi", result.getMimeType());
    }

    /**
     * Test of getDataNodePermissions method, of class PermissionsWrapper.
     */
    public void testGetDataNodePermissions() {
        System.out.println("getDataNodePermissions");
        PermissionsWrapper instance = getPermissionsWrapper();
        DataNodePermissions result = instance.getDataNodePermissions();
        assertEquals("permission_needed", result.getAccessLevel().toString());
        assertEquals(null, result.getLabel());
    }
}
