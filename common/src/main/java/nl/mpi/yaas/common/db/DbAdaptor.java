/**
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
package nl.mpi.yaas.common.db;

import nl.mpi.flap.kinnate.entityindexer.QueryException;

/**
 * Created on : Apr 8, 2013, 10:49:05 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public interface DbAdaptor {

    void checkDbExists(String databaseName) throws QueryException;

    void dropAndRecreateDb(String databaseName) throws QueryException;

    void addDocument(String databaseName, String documentName, String documentContents) throws QueryException;

    void deleteDocument(String databaseName, String documentName) throws QueryException;

    String executeQuery(String databaseName, String queryString) throws QueryException;

    void createIndexes(String databaseName) throws QueryException;
    // todo: the use of QueryProcessor is not compatable with the rest interface so will have to go
//    public QueryProcessor getQueryProcessor(String queryString) throws QueryException;
}
