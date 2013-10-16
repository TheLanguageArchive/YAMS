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
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.plugin.PluginException;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Document : LocalDataBaseManagerTest Created on : April 24, 2013, 17:22 PM
 *
 * @author Peter Withers
 */
public class LocalDataBaseManagerTest extends DataBaseManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Override
    DbAdaptor getDbAdaptor() throws IOException, QueryException {
        final File databaseDirectory = folder.newFolder("DatabaseDirectory");
        return new LocalDbAdaptor(databaseDirectory);
    }

    @Override
    public void testSampleData() throws JAXBException, PluginException, QueryException, IOException, ModelException {
        super.testSampleData();
    }

    @Override
    public void testGetDatabaseStats() throws JAXBException, PluginException, QueryException, IOException, ModelException {
        super.testGetDatabaseStats();
    }

    @Override
    public void testGetNodeDatasByIDs() throws QueryException, IOException, JAXBException, PluginException, ModelException {
        super.testGetNodeDatasByIDs();
    }

    @Override
    public void testGetMetadataTypes() throws Exception {
        super.testGetMetadataTypes();
    }
}
