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

import java.io.File;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.CreateIndex;
import org.basex.core.cmd.Delete;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.Get;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.OptimizeAll;
import org.basex.core.cmd.Set;
import org.basex.core.cmd.XQuery;
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

    public LocalDbAdaptor() throws QueryException {
        try {
            synchronized (databaseLock) {
                logger.info(new Get("dbpath").execute(context));
            }
        } catch (BaseXException baseXException2) {
            logger.error(baseXException2.getMessage());
            throw new QueryException(baseXException2.getMessage(), baseXException2);
        }
    }

    public void checkDbExists(String databaseName) throws QueryException {
        logger.debug("databaseName: " + databaseName);
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
//    logger.debug(new InfoDB().execute(context));
//    new DropIndex("text").execute(context);
//    new DropIndex("attribute").execute(context);
//    new DropIndex("fulltext").execute(context);
                new DropDB(databaseName).execute(context);
//                new Set("CREATEFILTER", suffixFilter).execute(context);
//                final File cacheDirectory = getDatabaseProjectDirectory(databaseName);
//                logger.debug("cacheDirectory: " + cacheDirectory);
//                new CreateDB(databaseName, cacheDirectory.toString()).execute(context);
                new CreateDB(databaseName).execute(context);
//                logger.debug("Create full text index");
//                new CreateIndex("fulltext").execute(context); // note that the indexes appear to be created by default, so this step might be redundant
                logger.debug(new InfoDB().execute(context));
            }
        } catch (BaseXException exception) {
            throw new QueryException(exception.getMessage(), exception);
        }
    }

    public void addDocument(String databaseName, String documentName, String documentContents) throws QueryException {
        try {
            synchronized (databaseLock) {
                new Open(databaseName).execute(context);
                new Delete(documentName).execute(context);
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
                new Delete(documentName).execute(context);
                new Close().execute(context);
            }
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error deleting document", exception);
        }
    }

    public String executeQuery(String databaseName, String queryString) throws QueryException {
        try {
            synchronized (databaseLock) {
//                logger.debug("queryString: " + queryString);
                return new XQuery(queryString).execute(context);
            }
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error executing query", exception);
        }
    }

    public void createIndexes(String databaseName) throws QueryException {
        try {
            synchronized (databaseLock) {
                new Open(databaseName).execute(context);
                new CreateIndex("text").execute(context);
                new CreateIndex("attribute").execute(context);
                new CreateIndex("fulltext").execute(context);
                new OptimizeAll().execute(context);
                new Close().execute(context);
            }
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error creating indexes", exception);
        }
    }
}
