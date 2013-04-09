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
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.plugin.PluginSessionStorage;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Delete;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.XQuery;
import org.basex.query.QueryProcessor;
import org.slf4j.LoggerFactory;

/**
 * Created on : Apr 8, 2013, 10:48:27 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class LocalDbAdaptor implements DbAdaptor {

    static final private Context context = new Context();
    static final private Object databaseLock = new Object();
    final private PluginSessionStorage sessionStorage;
    final private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public LocalDbAdaptor(PluginSessionStorage sessionStorage) throws QueryException {
//        try {
        this.sessionStorage = sessionStorage;
//            final File dbPathFile = new File(sessionStorage.getApplicationSettingsDirectory(), "BaseXData");
////            dbPathFile.mkdir();
//            System.out.println("dbpath: " + dbPathFile.toString());
//            System.out.println("dbpath exists: " + dbPathFile.exists());
//            synchronized (databaseLock) {
        // it seems that setting the db path to a temp file has not been working for some time if ever
//                new Set("dbpath", dbPathFile).execute(context);
//            }
//        } catch (BaseXException baseXException2) {
//            logger.error(baseXException2.getMessage());
//            throw new QueryException(baseXException2.getMessage(), baseXException2);
//        }
    }

    public void checkDbExists(String databaseName) throws QueryException {
        try {
            synchronized (databaseLock) {
                new Open(databaseName).execute(context);
                new Close().execute(context);
            }
        } catch (BaseXException baseXException) {
            try {
                synchronized (databaseLock) {
                    new CreateDB(databaseName).execute(context);
                }
            } catch (BaseXException baseXException2) {
                logger.error(baseXException2.getMessage());
                throw new QueryException(baseXException2.getMessage(), baseXException2);
            }
        }
    }

    public File getDatabaseProjectDirectory(String projectDatabaseName) {
        return sessionStorage.getProjectWorkingDirectory();
    }

    public void dropAndRecreateDb(String databaseName) throws QueryException {
//        String suffixFilter = "*.*mdi";
        try {
            synchronized (databaseLock) {
//    System.out.print(new InfoDB().execute(context));
//    new DropIndex("text").execute(context);
//    new DropIndex("attribute").execute(context);
//    new DropIndex("fulltext").execute(context);
                new DropDB(databaseName).execute(context);
//                new Set("CREATEFILTER", suffixFilter).execute(context);
//                final File cacheDirectory = getDatabaseProjectDirectory(databaseName);
//                System.out.println("cacheDirectory: " + cacheDirectory);
//                new CreateDB(databaseName, cacheDirectory.toString()).execute(context);
                new CreateDB(databaseName).execute(context);
//                System.out.println("Create full text index");
//                new CreateIndex("fulltext").execute(context); // note that the indexes appear to be created by default, so this step might be redundant
                System.out.print(new InfoDB().execute(context));
            }
        } catch (BaseXException exception) {
            throw new QueryException(exception.getMessage(), exception);
        }
    }

    public void addDocument(String databaseName, String documentName, String documentContents) throws QueryException {
        try {
            synchronized (databaseLock) {
                new Open(databaseName).execute(context);
                new Add(documentName, documentContents).execute(context);
                new Close().execute(context);
            }
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error adding document: " + documentName, exception);
        }
    }

    public void deleteDocument(String databaseName, String documentName) throws QueryException {
        try {
            synchronized (databaseLock) {
                new Open(databaseName).execute(context);
                new Delete("DbStatsDocument").execute(context);
                new Close().execute(context);
            }
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error deleting document", exception);
        }
    }

    public String executeQuery(String queryString) throws QueryException {
        try {
            synchronized (databaseLock) {
                // try getting the cached stats                
                System.out.println("queryString: " + queryString);
                return new XQuery(queryString).execute(context);
            }
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error executing query", exception);
        }
    }

    public QueryProcessor getQueryProcessor(String queryString) throws QueryException {
        // todo: the use of QueryProcessor is not compatable with the rest interface so will have to go
        synchronized (databaseLock) {
            return new QueryProcessor(queryString, context);
        }
    }
}
