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
import javax.xml.bind.JAXBException;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.plugin.PluginException;

/**
 * Document : LocalDataBaseManagerTest Created on : April 24, 2013, 17:22 PM
 *
 * @author Peter Withers
 */
public class LocalDataBaseManagerTest extends DataBaseManagerTest {

    @Override
    DbAdaptor getDbAdaptor() throws IOException, QueryException {
        return new LocalDbAdaptor(getTempDirectory());
    }

    private File getTempDirectory() throws IOException {

//        File tempWorkingDir = File.createTempFile("yaas-db", "-tmp");
//        File tempWorkingDir = File.createTempFile("yaas-db", Long.toString(System.nanoTime()), new File("./target"));
        File tempWorkingDir = new File(new File("target").getAbsoluteFile(), "yaas-test-db");
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

    @Override
    public void testSampleData() throws JAXBException, PluginException, QueryException, IOException {
        super.testSampleData();
    }

    @Override
    public void testGetDatabaseStats() throws JAXBException, PluginException, QueryException, IOException {
        super.testGetDatabaseStats();
    }

    @Override
    public void testGetNodeDatasByIDs() throws QueryException, IOException, JAXBException, PluginException {
        super.testGetNodeDatasByIDs();
    }

    @Override
    public void testGetMetadataTypes() throws Exception {
        super.testGetMetadataTypes();
    }
}
