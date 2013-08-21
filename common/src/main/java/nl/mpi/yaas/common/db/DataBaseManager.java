/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.IconTable;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.NodeTypeImage;
import nl.mpi.yaas.common.data.QueryDataStructures.CriterionJoinType;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchNegator;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchOption;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchType;
import nl.mpi.yaas.common.data.SearchParameters;
import org.slf4j.LoggerFactory;

/**
 * Document : DataBaseManager Created on : Aug 6, 2012, 11:39:33 AM
 *
 * @param <D> Concrete class of DataNode that will be used in the jaxb
 * deserialising process
 * @param <F> Concrete class of DataField that will be used in the jaxb
 * deserialising process
 * @param <M> Concrete class of MetadataFileType that is used as query
 * parameters and in some cases query results via the jaxb deserialising process
 * @author Peter Withers
 */
public class DataBaseManager<D, F, M> {

    final private Class<D> dClass;
    final private Class<F> fClass;
    final private Class<M> mClass;
    final private DbAdaptor dbAdaptor;
    final private String databaseName;
    final private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * these are two recommended database names, one for testing and the other
     * for production
     */
    final static public String defaultDataBase = "yaas-data";
    final static public String testDataBase = "yaas-test-data";
    final static public String facetsCollection = "Facets";
    final static public String dbStatsDocument = "DbStats";
    final static public String iconTableDocument = "IconTable";
    final private String crawledDataCollection = "CrawledData";
//    final static public String guestUser = "guestdbuser";
//    final static public String guestUserPass = "minfc8u4ng6s";
    final static public String guestUser = "admin"; // todo: the user name and password for admin and guest users needs to be determined and set
    final static public String guestUserPass = "admin";

    /**
     *
     * @param dClass Concrete class of DataNode that will be used in the jaxb
     * deserialising process
     * @param fClass Concrete class of DataField that will be used in the jaxb
     * deserialising process
     * @param mClass Concrete class of MetadataFileType that is used as query
     * parameters and in some cases query results via the jaxb deserialising
     * process
     * @param dbAdaptor an implementation of DbAdaptor which interfaces to
     * either the REST DB or local DB via java bindings
     * @param databaseName the name of the database that will be connected to
     * @throws QueryException
     */
    public DataBaseManager(Class<D> dClass, Class<F> fClass, Class<M> mClass, DbAdaptor dbAdaptor, String databaseName) throws QueryException {
        this.dbAdaptor = dbAdaptor;
        this.dClass = dClass;
        this.fClass = fClass;
        this.mClass = mClass;
        this.databaseName = databaseName;
//        dbAdaptor.checkDbExists(databaseName);
    }

    /**
     * Verifies that the database exists and create a new empty database if it
     * does not
     *
     * @throws QueryException
     */
    public void checkDbExists() throws QueryException {
        dbAdaptor.checkDbExists(databaseName);
    }

    /**
     * Drop the entire database if it exists and create a new empty database
     *
     * @throws QueryException
     */
    public void dropAllRecords() throws QueryException {
        dbAdaptor.dropAndRecreateDb(databaseName);
//        dbAdaptor.deleteDocument(databaseName, crawledDataCollection);
//        dbAdaptor.deleteDocument(databaseName, dbStatsDocument);
//        dbAdaptor.deleteDocument(databaseName, iconTableDocument);
//        dbAdaptor.deleteDocument(databaseName, facetsCollection);
    }

    /**
     * Remove the database statistics document that is generated after a crawl
     * by getDatabaseStats()
     *
     * @throws QueryException
     */
    public void clearDatabaseStats() throws QueryException {
        dbAdaptor.deleteDocument(databaseName, dbStatsDocument);
    }

    /**
     * Causes the database to reindex all of its files which is required after
     * an add or delete for instance
     *
     * @throws QueryException
     */
    public void createIndexes() throws QueryException {
        long startTime = System.currentTimeMillis();
        dbAdaptor.createIndexes(databaseName);
//        System.out.println("queryResult: " + queryResult);
        long queryMils = System.currentTimeMillis() - startTime;
        String queryTimeString = "Create indexes time: " + queryMils + "ms";
        System.out.println(queryTimeString);
    }

    private String getCachedVersion(String cachedDocument, String queryString) throws QueryException {
        String statsCachedQuery = "for $statsDoc in collection(\"" + databaseName + "\")\n"
                + "where matches(document-uri($statsDoc), '" + cachedDocument + "')\n"
                + "return $statsDoc";
        String queryResult;
        queryResult = dbAdaptor.executeQuery(databaseName, statsCachedQuery);
        if (queryResult.length() < 2) {
            // calculate the stats
            queryResult = dbAdaptor.executeQuery(databaseName, queryString);
            String resultCacheFlagged = queryResult.replaceFirst("<Cached>false</Cached>", "<Cached>true</Cached>");
            // insert the stats as a document
            dbAdaptor.addDocument(databaseName, cachedDocument, resultCacheFlagged);
        }
//        System.out.println("queryResult: " + queryResult);
        return queryResult;
    }

    /**
     * Gets a list of available databases
     *
     * @return a list of database names
     * @throws QueryException
     */
    public String[] getDatabaseList() throws QueryException {
//        String queryResult = dbAdaptor.executeQuery(databaseName, "for $databaseName in db:list()\n"
//                + "return <String>{$databaseName}</String>");
        String queryResult = dbAdaptor.executeQuery(databaseName, "db:list()");
        System.out.println("databaseList: " + queryResult);
        return queryResult.split(" ");
    }

    /**
     * Creates a document in the database that holds information on the contents
     * of the database such as document count and root nodes URLs
     *
     * @return an object of type DatabaseStats that contains information on the
     * contents of the database
     * @throws QueryException
     */
    public DatabaseStats getDatabaseStats() throws QueryException {
        long startTime = System.currentTimeMillis();
        String statsQuery = "let $knownIds := collection(\"" + databaseName + "\")/DataNode/@ID\n"
                + "let $duplicateDocumentCount := count($knownIds) - count(distinct-values($knownIds))\n"
                + "let $childIds := collection(\"" + databaseName + "\")/DataNode/ChildLink/@ID\n" // removing the "/text()" here reduced the query from 310ms to 290ms with 55 documents
                //                 + "let $missingIds := distinct-values(for $testId in $childIds where not ($knownIds = $testId) return $testId)\n"
                //                 + "let $rootNodes := distinct-values(for $testId in $knownIds where not ($childIds = $testId) return $testId)\n"
                // With 55 documents this change (for loop replaced by "[not(.=") decreased the query from 254ms to 237ms and with zero documents it made no difference, but this was doe with out updating the indexes and running the query only once
                + "let $missingIds := distinct-values($childIds[not(.=$knownIds)])"
                + "let $rootNodes := distinct-values($knownIds[not(.=$childIds)])"
                + "return <DatabaseStats>\n"
                + "<KnownDocuments>{count($knownIds)}</KnownDocuments>\n"
                + "<MissingDocuments>{count($missingIds)}</MissingDocuments>\n"
                + "<DuplicateDocuments>{$duplicateDocumentCount}</DuplicateDocuments>\n"
                + "<RootDocuments>{count($rootNodes)}</RootDocuments>\n"
                + "<Cached>false</Cached>\n"
                + "{for $rootDocId in $rootNodes return <RootDocumentID>{$rootDocId}</RootDocumentID>}\n"
                + "</DatabaseStats>\n";
        String queryResult = getCachedVersion(dbStatsDocument, statsQuery);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DatabaseStats.class, DataNodeId.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            DatabaseStats databaseStats = (DatabaseStats) unmarshaller.unmarshal(new StreamSource(new StringReader(queryResult)), DatabaseStats.class).getValue();
            long queryMils = System.currentTimeMillis() - startTime;
//            String queryTimeString = "DatabaseStats Query time: " + queryMils + "ms";
            databaseStats.setQueryTimeMS(queryMils);
//            System.out.println(queryTimeString);
            return databaseStats;
        } catch (JAXBException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error getting DatabaseStats");
        }
    }

    /**
     * Searches the database for missing child nodes for use when crawling
     * missing documents
     *
     * @return the URLs of the first N missing documents
     * @throws PluginException
     * @throws QueryException
     */
    public String getHandlesOfMissing() throws PluginException, QueryException {
        long startTime = System.currentTimeMillis();
        String queryString = "let $childIds := collection(\"" + databaseName + "\")/DataNode/ChildLink\n"
                + "let $knownIds := collection(\"" + databaseName + "\")/DataNode/@ID\n"
                + "let $missingIds := distinct-values($childIds[not(@ID=$knownIds)]/@URI)"
                + "return $missingIds[position() le 1000]\n"; // <DataNodeId> </DataNodeId>
//        System.out.println("getHandlesOfMissing: " + queryString);
        String queryResult = dbAdaptor.executeQuery(databaseName, queryString);
        long queryMils = System.currentTimeMillis() - startTime;
        String queryTimeString = "Query time: " + queryMils + "ms";
        final String sampleDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());;
        String statsQuery = "let $childIds := collection(\"" + databaseName + "\")/DataNode/ChildLink\n"
                + "let $knownIds := collection(\"" + databaseName + "\")/DataNode/@ID\n"
                + "return\n"
                + "<CrawlerStats linkcount='{count($childIds)}' documentcount='{count($knownIds)}' queryms='" + queryMils + "' timestamp='" + sampleDateTime + "'/>";
        String statsDoc = dbAdaptor.executeQuery(databaseName, statsQuery);
        System.out.println("stats:" + statsDoc);
        // insert the stats document
        dbAdaptor.addDocument(databaseName, "CrawlerStats/" + sampleDateTime, statsDoc);
        System.out.println(queryTimeString);
        return queryResult; // the results here need to be split on " ", but the string can be very long so it should not be done by String.split().
    }

    /**
     * Retrieves the document of all the known node types and the icons for each
     * type from the database in base 64 format
     *
     * @return IconTableBase64 a set of node types and their icons in base 64
     * format
     * @throws PluginException
     * @throws QueryException
     */
    public IconTableBase64 getNodeIconsBase64() throws PluginException, QueryException {
        String iconTableQuery = "for $statsDoc in collection(\"" + databaseName + "\")\n"
                + "where matches(document-uri($statsDoc), '" + iconTableDocument + "')\n"
                + "return $statsDoc";
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(IconTableBase64.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String queryResult;
            queryResult = dbAdaptor.executeQuery(databaseName, iconTableQuery);
//            System.out.println("queryResult: " + queryResult);
            return (IconTableBase64) unmarshaller.unmarshal(new StreamSource(new StringReader(queryResult)), IconTableBase64.class).getValue();
        } catch (JAXBException exception) {
            throw new PluginException(exception);
        }
    }

    /**
     * Retrieves the document of all the known node types and the icons for each
     * type from the database
     *
     * @return IconTable a set of node types and their icons
     * @throws PluginException
     * @throws QueryException
     */
    public IconTable getNodeIcons() throws PluginException, QueryException {
        String iconTableQuery = "for $statsDoc in collection(\"" + databaseName + "\")\n"
                + "where matches(document-uri($statsDoc), '" + iconTableDocument + "')\n"
                + "return $statsDoc";
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(IconTable.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String queryResult;
            queryResult = dbAdaptor.executeQuery(databaseName, iconTableQuery);
//            System.out.println("queryResult: " + queryResult);
            return (IconTable) unmarshaller.unmarshal(new StreamSource(new StringReader(queryResult)), IconTable.class).getValue();
        } catch (JAXBException exception) {
            throw new PluginException(exception);
        }
    }

    /**
     * Inserts a document of all the known node types and the icons for each
     * type into the database
     *
     * @param iconTable a set of node types and their icons to be inserted into
     * the database
     * @throws PluginException
     * @throws QueryException
     */
    public IconTable insertNodeIconsIntoDatabase(IconTable iconTable) throws PluginException, QueryException {
        try {
            IconTable databaseIconTable = getNodeIcons();
            for (NodeTypeImage nodeTypeImage : databaseIconTable.getNodeTypeImageSet()) {
                // add the known types to the new set
                iconTable.addTypeIcon(nodeTypeImage);
            }
        } catch (PluginException exception) {
            // if there is not icon document here that can be normal if it is the first run
            System.out.println("Error getting existing IconTableDocument (this is normal on the first run).");
        }
        dbAdaptor.deleteDocument(databaseName, iconTableDocument);
        // use JAXB to serialise and insert the IconTable into the database
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(IconTable.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(iconTable, stringWriter);
            //System.out.println("NodeIcons to be inserted:\n" + stringWriter.toString());
            dbAdaptor.addDocument(databaseName, iconTableDocument, stringWriter.toString());
        } catch (JAXBException exception) {
            System.err.println("jaxb error:" + exception.getMessage());
            throw new PluginException(exception);
        }
        return iconTable;
    }

    /**
     * Inserts a document into the database and optionally checks for existing
     * documents that would constitute a duplicate
     *
     * @param dataNode the data node to be inserted into the database
     * @param testForDuplicates if true the database will be searched for the
     * document before inserting
     * @throws PluginException
     * @throws QueryException
     */
    public void insertIntoDatabase(SerialisableDataNode dataNode, boolean testForDuplicates) throws PluginException, QueryException, ModelException {
        // test for existing documents with the same ID and throw if one is found
        if (testForDuplicates) {
            String existingDocumentQuery = "let $countValue := count(collection(\"" + databaseName + "\")/DataNode[@ID = \"" + dataNode.getID() + "\"])\nreturn $countValue";
            String existingDocumentResult = dbAdaptor.executeQuery(databaseName, existingDocumentQuery);
            if (!existingDocumentResult.equals("0")) {
                throw new QueryException("Existing document found, count: " + existingDocumentResult);
            }
        }
        // use JAXB to serialise and insert the data node into the database
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(dClass, fClass, mClass);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(dataNode, stringWriter);
//            System.out.println("Data to be inserted:\n" + stringWriter.toString());
            dbAdaptor.addDocument(databaseName, crawledDataCollection + "/" + dataNode.getID(), stringWriter.toString());
        } catch (JAXBException exception) {
            System.err.println("jaxb error:" + exception.getMessage());
            throw new PluginException(exception);
        }
    }

    private String getTypeClause(MetadataFileType metadataFileType) {
        String typeClause = "";
        if (metadataFileType != null) {
            if (metadataFileType.getType() != null) {
                typeClause += "[/DataNode/Type/@Name = '" + metadataFileType.getType() + "']";
            }
            if (metadataFileType.getPath() != null) {
                typeClause += "//DataNode/FieldGroup[@Label = '" + metadataFileType.getPath() + "']";
            }
        }
        return typeClause;
    }

    private String getTypeNodes(MetadataFileType metadataFileType) {
        String typeNodes = "";
        if (metadataFileType != null) {
            if (metadataFileType.getPath() != null) {
                typeNodes += "<Path>" + metadataFileType.getPath() + "</Path>";
            }
            if (metadataFileType.getType() != null) {
                typeNodes += "<Type>" + metadataFileType.getType() + "</Type>";
            }
        }
        return typeNodes;
    }

    private String getDocumentName(MetadataFileType metadataFileType, String queryType) {
        if (metadataFileType == null) {
            return facetsCollection + "/" + queryType + "/all";
        }
        final String type = (metadataFileType.getType() == null) ? "all" : metadataFileType.getType();
        final String path = (metadataFileType.getPath() == null) ? "all" : metadataFileType.getPath();
        return facetsCollection + "/" + queryType + "/" + type + "/" + path;
    }

    private String getDocumentName(MetadataFileType[] metadataFileTypes, String queryType) {
        String documentName = facetsCollection + "/" + queryType;
        for (MetadataFileType metadataFileType : metadataFileTypes) {
            if (metadataFileType == null) {
                documentName += "/all/all";
            } else {
                final String type = (metadataFileType.getType() == null) ? "all" : metadataFileType.getType();
                final String path = (metadataFileType.getPath() == null) ? "all" : metadataFileType.getPath();
                documentName += "/" + type + "/" + path;
            }
        }
        System.out.println("documentName: " + documentName);
        return documentName;
    }
//    private String getFieldConstraint(MetadataFileType fieldType) {
//        String fieldConstraint = "";
//        if (fieldType != null) {
//            final String fieldNameString = fieldType.getFieldName();
//            if (fieldNameString != null) {
//                fieldConstraint = "FieldGroup/@Label = '" + fieldNameString + "' and ";
//            }
//        }
//        return fieldConstraint;
//    }

    private String getSearchTextConstraint(SearchNegator searchNegator, SearchType searchType, String searchString, String nodeString) {
        final String escapedSearchString = escapeBadChars(searchString);
        String returnString = "";
        switch (searchType) {
            case contains:
                if (escapedSearchString.isEmpty()) {
                    // when the user has not entered any string then return all, but allow the negator to still be used
                    returnString = "1=1";
                } else {
                    returnString = nodeString + "@FieldValue contains text '" + escapedSearchString + "'";
                }
                break;
            case equals:
                returnString = nodeString + "@FieldValue = '" + escapedSearchString + "'";
                break;
            case fuzzy:
                returnString = nodeString + "@FieldValue contains text '" + escapedSearchString + "' using fuzzy";
                break;
        }
        switch (searchNegator) {
            case is:
//                returnString = returnString;
                break;
            case not:
                returnString = "not(" + returnString + ")";
                break;
        }
        return returnString;
    }

    static String escapeBadChars(String inputString) {
        // our queries use double quotes so single quotes are allowed
        // todo: could ; cause issues?
        return inputString.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;");
    }
    /*
     * let $elementSet0 := for $nameString0 in collection('" + databaseName + "')//*:Address[count(*) = 0] order by $nameString0 return $nameString0
     let $elementSet1 := for $nameString0 in collection('" + databaseName + "')//*:Region[count(*) = 0] order by $nameString0 return $nameString0
     return
     <TreeNode><DisplayString>All</DisplayString>
     {
     for $nameString0 in distinct-values($elementSet0/text())
     return
     <TreeNode><DisplayString>Address: {$nameString0}</DisplayString>
     {
     let $intersectionSet0 := $elementSet1[root()//*:Address = $nameString0]
     for $nameString1 in distinct-values($intersectionSet0/text())
     return
     <TreeNode><DisplayString>Region: {$nameString1}</DisplayString>
     </TreeNode>
     }
     </TreeNode>
     }
     </TreeNode>
     * */

    private String getTreeSubQuery(ArrayList<MetadataFileType> treeBranchTypeList, String whereClause, String selectClause, String trailingSelectClause, int levelCount) {
        final int maxMetadataFileCount = 100;
        if (!treeBranchTypeList.isEmpty()) {
            String separatorString = "";
//            if (whereClause.length() > 0) {
//                separatorString = ",\n";
//            }
            MetadataFileType treeBranchType = treeBranchTypeList.remove(0);
            String currentFieldName = treeBranchType.getPath();
            String nextWhereClause = whereClause + "[//*:" + currentFieldName + " = $nameString" + levelCount + "]";
            String nextSelectClause = selectClause + "[*:" + currentFieldName + " = $nameString" + levelCount + "]";
            String nextTrailingSelectClause = "[*:" + currentFieldName + " = $nameString" + levelCount + "]";
            return "{\n"
                    + "for $nameString" + levelCount + " in distinct-values(collection('" + databaseName + "')" + whereClause + "//*:" + currentFieldName + "[count(*) = 0]\n"
                    //                + "return concat(base-uri($entityNode), path($entityNode))\n"
                    + ")\n"
                    + "order by $nameString" + levelCount + "\n"
                    + "return\n"
                    + "<TreeNode><DisplayString>" + currentFieldName + ": {$nameString" + levelCount + "}</DisplayString>\n"
                    + getTreeSubQuery(treeBranchTypeList, nextWhereClause, nextSelectClause, nextTrailingSelectClause, levelCount + 1)
                    + "</TreeNode>\n}\n";
        } else {
            return "{"
                    //                    + " if (count(collection('" + databaseName + "')" + whereClause + "//.[count(*) = 0][text() != '']" + trailingSelectClause + ") < " + maxMetadataFileCount + ") then\n"
                    + "for $matchingNode in collection('" + databaseName + "')" + whereClause + "//." + trailingSelectClause + "\n"
                    + "return\n"
                    + "<MetadataTreeNode>\n"
                    + "<FileUri>{base-uri($matchingNode)}</FileUri>\n"
                    + "<FileUriPath>{path($matchingNode)}</FileUriPath>\n"
                    + "</MetadataTreeNode>\n"
                    //                    + "else \n"
                    //                    + "<DisplayString>&gt;more than " + maxMetadataFileCount + " results, please add more facets&lt;</DisplayString>"
                    + "\n}\n";
        }
    }

    private String getNodesByIdQuery(final ArrayList<DataNodeId> nodeIDs) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("<DataNode>\n");
        queryStringBuilder.append("{for $dataNode in collection('");
        queryStringBuilder.append(databaseName);
        queryStringBuilder.append("')/DataNode where $dataNode/@ID = (\n");
        boolean firstLoop = true;
        for (DataNodeId dataNodeId : nodeIDs) {
            if (!firstLoop) {
                queryStringBuilder.append(",");
            }
            firstLoop = false;
            queryStringBuilder.append("'");
            queryStringBuilder.append(dataNodeId.getIdString());
            queryStringBuilder.append("'");
        }
        queryStringBuilder.append(") return $dataNode}");
        queryStringBuilder.append("</DataNode>");
        return queryStringBuilder.toString();
    }

    private String getSearchFieldConstraint(SearchParameters searchParameters) {
        String fieldConstraint = getTypeClause(searchParameters.getFieldType());
        String searchTextConstraint = getSearchTextConstraint(searchParameters.getSearchNegator(), searchParameters.getSearchType(), searchParameters.getSearchString(), "//FieldGroup/FieldData/");
        return fieldConstraint + searchTextConstraint;
    }

    private String getSearchConstraint(SearchParameters searchParameters) {
        String typeClause = "/DataNode";
        String pathClause = "";
        if (searchParameters.getFileType() != null) {
            if (searchParameters.getFileType().getType() != null) {
                typeClause += "[Type/@Name = '" + searchParameters.getFileType().getType() + "']";
            }
            if (searchParameters.getFieldType().getPath() != null) {
                pathClause += "[@Label = '" + searchParameters.getFieldType().getPath() + "']";
            }
        }
        String fieldsQuery = "";
        if (searchParameters.getSearchNegator() == SearchNegator.is) {
            fieldsQuery = "{"
                    + "for $field in $foundNode"
                    + getSearchTextConstraint(searchParameters.getSearchNegator(), searchParameters.getSearchType(), searchParameters.getSearchString(), "//FieldGroup/FieldData[")
                    + "]\n"
                    + "return \n"
                    + "<FieldGroup>{$field}</FieldGroup>\n"
                    + "}\n";
        }
        return "for $foundNode in collection('" + databaseName + "/" + crawledDataCollection + "')" + typeClause + "[//DataNode/FieldGroup" + pathClause + "["
                + getSearchTextConstraint(searchParameters.getSearchNegator(), searchParameters.getSearchType(), searchParameters.getSearchString(), "FieldData/")
                + "]]\n"
                + "return\n"
                + "<DataNode>\n"
                + "{$foundNode/@ID}\n"
                + "{$foundNode/@Label}\n"
                + fieldsQuery
                + "<ChildLink ID=\"{$foundNode/@ID}\"/>\n"
                + "{$foundNode/Type}\n"
                + "</DataNode>";
    }

    private String getTreeFacetsQuery(MetadataFileType[] metadataFileTypes) {
        String typeClause = "";
        for (MetadataFileType type : metadataFileTypes) {
            typeClause += getTypeClause(type);
        }
        String typeNodes = getTypeNodes(metadataFileTypes[metadataFileTypes.length - 1]);
        return "let $fieldValues := collection('" + databaseName + "/" + crawledDataCollection + "')" + typeClause + "//FieldData/@FieldValue/string()\n"
                + "return <MetadataFileType>\n"
                + "{\n"
                + "for $label in distinct-values($fieldValues)\n"
                + "order by $label\n"
                + "return <MetadataFileType>"
                + "<Label>{$label}</Label>\n"
                + "<Value>{$label}</Value>\n"
                + typeNodes
                + "<Count>{count($fieldValues[. = $label])}</Count></MetadataFileType>\n"
                + "}</MetadataFileType>";
    }

    private String getMetadataFieldValuesQuery(MetadataFileType metadataFileType) {
        String typeClause = getTypeClause(metadataFileType);
        String typeNodes = getTypeNodes(metadataFileType);
        return "let $fieldValues := collection('" + databaseName + "/" + crawledDataCollection + "')" + typeClause + "//FieldData/@FieldValue/string()\n"
                + "return <MetadataFileType>\n"
                + "{\n"
                + "for $label in distinct-values($fieldValues)\n"
                + "order by $label\n"
                + "return <MetadataFileType>"
                + "<Label>{$label}</Label>\n"
                + "<Value>{$label}</Value>\n"
                + typeNodes
                + "<Count>{count($fieldValues[. = $label])}</Count></MetadataFileType>\n"
                + "}</MetadataFileType>";
    }

    private String getMetadataTypes() {
//        return "for $xpathString in distinct-values(\n"
//                + "for $entityNode in collection('" + databaseName + "')/*\n"
//                + "return path($entityNode)\n"
//                + ")\n"
//                + "return"
//                + "$xpathString";
        return "<MetadataFileType>\n"
                + "<MetadataFileType>\n"
                + "<Label>All Types</Label>\n"
                + "<Count>{count(collection('" + databaseName + "/" + crawledDataCollection + "'))}</Count>\n"
                + "</MetadataFileType>\n"
                + "{\n"
                //                + "for $imdiType in distinct-values(collection('" + databaseName + "')/*:METATRANSCRIPT/*/name())\n"
                //                + "order by $imdiType\n"
                //                + "return\n"
                //                + "<MetadataFileType>\n"
                //                + "<ImdiType>{$imdiType}</ImdiType>\n"
                //                + "<RecordCount>{count(collection('" + databaseName + "')/*:METATRANSCRIPT/*[name()=$imdiType])}</RecordCount>\n"
                //                + "</MetadataFileType>\n"
                //                + "},{"
                //                + "for $profileString in distinct-values(collection('" + databaseName + "')/*:CMD/@*:schemaLocation)\n"
                //                //                + "order by $profileString\n"
                //                + "return\n"
                //                + "<MetadataFileType>\n"
                //                + "<profileString>{$profileString}</profileString>\n"
                //                + "<RecordCount>{count(collection('" + databaseName + "')/*:CMD[@*:schemaLocation = $profileString])}</RecordCount>"
                //                + "</MetadataFileType>\n"
                /*
                 * optimised this query 2012-10-17
                 * the query above takes:
                 * 5014.03 ms
                 * the query below takes:
                 * 11.8 ms (varies per run)
                 */
                //                + "for $profileInfo in index:facets('" + databaseName + "/" + crawledDataCollection + "')/document-node/element[@name='DataNode']/element[@name='Type']/attribute[@name='Format']/entry\n"
                //                + "return\n"
                //                + "<MetadataFileType>\n"
                //                + "<fieldName>{string($profileInfo)}</fieldName>\n"
                //                + "<RecordCount>{string($profileInfo/@count)}</RecordCount>\n"
                //                + "</MetadataFileType>"
                //                + "}{"
                //                + "for $profileInfo in index:facets('" + databaseName + "')/document-node/element[@name='DataNode']/element[@name='Type']/attribute[@name='Name']/entry\n"
                //                + "return\n"
                //                + "<MetadataFileType>\n"
                //                + "<fieldName>{string($profileInfo)}</fieldName>\n"
                //                + "<RecordCount>{string($profileInfo/@count)}</RecordCount>\n"
                //                //                + "<ValueCount>{count($profileInfo/entry)}</ValueCount>\n"
                //                + "</MetadataFileType>\n"

                + "let $allNodeTypes := collection('" + databaseName + "/" + crawledDataCollection + "')/DataNode/Type/@Name/string()\n"
                + "for $nodeType in distinct-values($allNodeTypes)\n"
                + "order by $nodeType\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<Label>{$nodeType}</Label>\n"
                + "<Type>{$nodeType}</Type>\n"
                + "<Count>{count($allNodeTypes[. = $nodeType])}</Count>\n"
                + "</MetadataFileType>\n"
                + "}</MetadataFileType>";
    }

    private String getMetadataPathsQuery(MetadataFileType metadataFileType) {
        String typeClause = getTypeClause(metadataFileType);
        String typeNodes = getTypeNodes(metadataFileType);
        return "let $fieldLabels := collection('" + databaseName + "/" + crawledDataCollection + "')" + typeClause + "//FieldGroup[FieldData/@FieldValue != '']/@Label/string()\n"
                + "return <MetadataFileType>\n"
                + "<MetadataFileType><Label>All Paths</Label>"
                + typeNodes
                + "<Count>{count($fieldLabels)}</Count></MetadataFileType>\n"
                + "{\n"
                + "for $label in distinct-values($fieldLabels)\n"
                + "order by $label\n"
                + "return <MetadataFileType>"
                + "<Label>{$label}</Label>\n"
                + typeNodes
                + "<Path>{$label}</Path>\n"
                + "<Count>{count($fieldLabels[. = $label and $label != ''])}</Count></MetadataFileType>\n"
                + "}</MetadataFileType>";
    }

    /**
     * Searches the database
     *
     * @param criterionJoinType the type of join that the query will perform
     * @param searchParametersList the parameters of the search
     * @return A data node that the results as child nodes plus some query
     * information
     * @throws QueryException
     */
    public D getSearchResult(CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) throws QueryException {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("<DataNode ID=\"Search Results\" Label=\"Search Results: ");
        if (searchParametersList.size() > 1) {
            queryStringBuilder.append(criterionJoinType.name());
            queryStringBuilder.append(" ");
        }
        for (SearchParameters parameters : searchParametersList) {
            queryStringBuilder.append("(");
            queryStringBuilder.append(parameters.getFileType().getType());
            queryStringBuilder.append(" ");
            queryStringBuilder.append(parameters.getFieldType().getPath());
            queryStringBuilder.append(" ");
            for (SearchOption option : SearchOption.values()) {
                if (option.getSearchNegator() == parameters.getSearchNegator() && option.getSearchType() == parameters.getSearchType()) {
                    queryStringBuilder.append(option.toString());
                }
            }
            queryStringBuilder.append(" ");
            queryStringBuilder.append(parameters.getSearchString());
            queryStringBuilder.append(") ");
        }
        queryStringBuilder.append("\">");
        queryStringBuilder.append("{\n");
        int parameterCounter = 0;
        for (SearchParameters searchParameters : searchParametersList) {
            queryStringBuilder.append("let $documentSet");
            queryStringBuilder.append(parameterCounter);
            queryStringBuilder.append(" := ");
            parameterCounter++;
            queryStringBuilder.append(getSearchConstraint(searchParameters));
        }
        queryStringBuilder.append("let $returnSet := $documentSet0");
        for (int setCount = 1; setCount < parameterCounter; setCount++) {
            queryStringBuilder.append(" ");
            queryStringBuilder.append(criterionJoinType.name());
            queryStringBuilder.append(" $documentSet");
            queryStringBuilder.append(setCount);
        }
        queryStringBuilder.append("\n"
                + "for $documentNode in $returnSet\n"
                + "return\n"
                /*
                 * This query currently takes 18348.54 ms
                 * the loop over the return set takes 15000 ms or so
                 * With two search values and union it takes 13810.04ms
                 * With two search values and union and one field name specified it takes 9086.76ms
                 * 
                 */
                /*
                 * 15041
                 * <TreeNode>{
                 for $fieldNode in collection('" + databaseName + "')//.[(text() contains text 'pu6') or (name() = 'Name' and text() contains text 'pu8')]
                 let $documentFile := base-uri($fieldNode)
                 group by $documentFile
                 return
                 <MetadataTreeNode>
                 <FileUri>{$documentFile}</FileUri>
                 {
                 for $entityNode in $fieldNode
                 return <FileUriPath>{path($entityNode)}</FileUriPath>
                 }
                 </MetadataTreeNode>
                 }</TreeNode>
                 */
                // todo: add back in the set functions
                //                + "for $entityNode in $documentNode[");
                //        boolean firstConstraint = true;
                //        for (SearchParameters searchParameters : searchParametersList) {
                //            if (firstConstraint) {
                //                firstConstraint = false;
                //            } else {
                //                queryStringBuilder.append(" or ");
                //            }
                //            queryStringBuilder.append(getSearchFieldConstraint(searchParameters));
                //        }
                //        queryStringBuilder.append("]\n"
                //                + "return $entityNode\n"
                + "$returnSet"
                + "}</DataNode>\n");
        final D metadataTypesString = getDbTreeNode(queryStringBuilder.toString());
        return metadataTypesString;
    }

//    public DbTreeNode getSearchResultX(CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) {
//        StringBuilder queryStringBuilder = new StringBuilder();
//        StringBuilder joinStringBuilder = new StringBuilder();
//        StringBuilder fieldStringBuilder = new StringBuilder();
//        int parameterCounter = 0;
//        for (SearchParameters searchParameters : searchParametersList) {
//            fieldStringBuilder.append(getSearchFieldConstraint(searchParameters));
//            if (queryStringBuilder.length() > 0) {
//                fieldStringBuilder.append(" or ");
//                joinStringBuilder.append(" ");
//                joinStringBuilder.append(criterionJoinType.name());
//                joinStringBuilder.append(" ");
//            } else {
//                joinStringBuilder.append("let $returnSet := ");
//            }
//            joinStringBuilder.append("$set");
//            joinStringBuilder.append(parameterCounter);
//            queryStringBuilder.append("let $set");
//            queryStringBuilder.append(parameterCounter);
//            queryStringBuilder.append(" := ");
//            parameterCounter++;
//            queryStringBuilder.append(getSearchConstraint(searchParameters));
//        }
//        queryStringBuilder.append(joinStringBuilder);
//        queryStringBuilder.append("return <TreeNode>{"
//                + "for $documentNode in $returnSet\n"
//                + "return\n"
//                + "<MetadataTreeNode>\n"
//                + "<FileUri>{base-uri($entityNode)}</FileUri>\n"
//                + "for $entityNode in $documentNode//*");
//        queryStringBuilder.append(fieldStringBuilder.toString());
//        queryStringBuilder.append("\n"
//                + "return <FileUriPath>{path($entityNode)}</FileUriPath>\n"
//                + "</MetadataTreeNode>\n"
//                + "}</TreeNode>");
//
//        final DbTreeNode metadataTypesString = getDbTreeNode(queryStringBuilder.toString());
//        return metadataTypesString;
//    }
    public M[] getMetadataPaths(MetadataFileType metadataFileType) throws QueryException {
        final String queryString = getMetadataPathsQuery(metadataFileType);
        return getMetadataTypes(queryString, getDocumentName(metadataFileType, "paths"));
    }

    public M[] getMetadataFieldValues(MetadataFileType metadataFileType) throws QueryException {
        final String queryString = getMetadataFieldValuesQuery(metadataFileType);
        return getMetadataTypes(queryString, getDocumentName(metadataFileType, "values"));
    }

    public M[] getMetadataTypes(MetadataFileType metadataFileType) throws QueryException {
        final String queryString = getMetadataTypes();
        return getMetadataTypes(queryString, getDocumentName(metadataFileType, "types"));
    }

    public M[] getTreeFacetTypes(MetadataFileType[] metadataFileTypes) throws QueryException {
        for (MetadataFileType type : metadataFileTypes) {
            System.out.println("Type: " + type);
        }
        final String queryString = getTreeFacetsQuery(metadataFileTypes);
        return getMetadataTypes(queryString, getDocumentName(metadataFileTypes, "tree"));
    }

//    public DbTreeNode getSearchTreeData() {
//        final String queryString = getTreeQuery(treeBranchTypeList);
//        return getDbTreeNode(queryString);
//    }
    public D getNodeDatasByIDs(final ArrayList<DataNodeId> nodeIDs) throws QueryException {
        final String queryString = getNodesByIdQuery(nodeIDs);
        return getDbTreeNode(queryString);
    }

    private D getDbTreeNode(String queryString) throws QueryException {
        long startTime = System.currentTimeMillis();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(dClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String queryResult;
//                System.out.println("queryString: " + queryString);
            queryResult = dbAdaptor.executeQuery(databaseName, queryString);
//            System.out.println("queryResult: " + queryResult);
            D rootTreeNode = (D) unmarshaller.unmarshal(new StreamSource(new StringReader(queryResult)), dClass).getValue();
            long queryMils = System.currentTimeMillis() - startTime;
            int resultCount = 0;
            if (rootTreeNode != null) {
                resultCount = 1;
            }
            String queryTimeString = "Query time: " + queryMils + "ms for " + resultCount + " entities";
            System.out.println(queryTimeString);
            return rootTreeNode;
        } catch (JAXBException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error getting search options");
        }
    }

    private M[] getMetadataTypes(final String queryString, String documentName) throws QueryException {
        long startTime = System.currentTimeMillis();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(mClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String queryResult = getCachedVersion(documentName, queryString);
//            System.out.println("queryString: " + queryString);
//            queryResult = dbAdaptor.executeQuery(databaseName, queryString);
//            System.out.println("queryResult: " + queryResult);
            M foundEntities = (M) unmarshaller.unmarshal(new StreamSource(new StringReader(queryResult)), MetadataFileType.class).getValue();
            long queryMils = System.currentTimeMillis() - startTime;
            final M[] entityDataArray = (M[]) ((MetadataFileType) foundEntities).getChildMetadataTypes();
            int resultCount = 0;
            if (entityDataArray != null) {
                resultCount = entityDataArray.length;
            }
            String queryTimeString = "Query time: " + queryMils + "ms for " + resultCount + " entities";
            System.out.println(queryTimeString);
//            selectedEntity.appendTempLabel(queryTimeString);
            return (M[]) ((MetadataFileType) foundEntities).getChildMetadataTypes();
        } catch (JAXBException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error getting search options");
        }
    }
}
