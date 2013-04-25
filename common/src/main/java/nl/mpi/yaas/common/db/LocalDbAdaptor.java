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
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Delete;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.Set;
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
    final private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    final private File dbPathDir;

    public LocalDbAdaptor(File dbPath) throws QueryException {
        dbPathDir = new File(dbPath, "BaseXData");
        System.out.println("dbPathDir: " + dbPathDir.toString());
        dbPathDir.mkdir();
        try {
            System.out.println("dbpath exists: " + dbPathDir.exists());
            synchronized (databaseLock) {
                new Set("DBPATH", dbPathDir).execute(context);
            }
        } catch (BaseXException baseXException2) {
            logger.error(baseXException2.getMessage());
            throw new QueryException(baseXException2.getMessage(), baseXException2);
        }
    }

    public void checkDbExists(String databaseName) throws QueryException {
        System.out.println("databaseName: " + databaseName);
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

    public void createIndexes(String databaseName) throws QueryException {
        String createIndexesQuery = "db:optimize(\"" + databaseName + "\")\n";
        String queryResult = executeQuery(createIndexesQuery);
    }
}
