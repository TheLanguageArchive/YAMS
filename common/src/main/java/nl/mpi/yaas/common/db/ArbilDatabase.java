/**
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.AbstractDataNode;
import nl.mpi.flap.model.AbstractField;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.flap.plugin.PluginSessionStorage;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures.CriterionJoinType;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchNegator;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchType;
import nl.mpi.yaas.common.data.SearchParameters;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.Set;
import org.basex.core.cmd.XQuery;
import org.slf4j.LoggerFactory;

/**
 * Document : ArbilDatabase Created on : Aug 6, 2012, 11:39:33 AM
 *
 * @author Peter Withers
 */
public class ArbilDatabase<D, M> {

    final private Class<D> dClass;
    final private Class<M> mClass;
    static Context context = new Context();
    static final Object databaseLock = new Object();
    final private String databaseName;
    final private PluginSessionStorage sessionStorage;
    final private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public ArbilDatabase(Class<D> dClass, Class<M> mClass, PluginSessionStorage sessionStorage, String databaseName) throws QueryException {
        this.dClass = dClass;
        this.mClass = mClass;
        this.databaseName = databaseName;
        this.sessionStorage = sessionStorage;
        try {
            synchronized (databaseLock) {
                final File dbPathFile = new File(sessionStorage.getApplicationSettingsDirectory(), "BaseXData");
                System.out.println("dbpath: " + dbPathFile.toString());
                new Set("dbpath", dbPathFile).execute(context);
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
                throw new QueryException(baseXException2.getMessage());
            }
        }
    }

    public File getDatabaseProjectDirectory(String projectDatabaseName) {
        return sessionStorage.getProjectWorkingDirectory();
    }

    public void createDatabase() throws QueryException {
        String suffixFilter = "*.*mdi";
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
            }
        } catch (BaseXException exception) {
            throw new QueryException(exception.getMessage());
        }
    }

    public void insertIntoDatabase(AbstractDataNode dataNode, Class fieldClass) throws PluginException {
        // use JAXB to serialise and insert the data node into the database
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AbstractDataNode.class, AbstractField.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(dataNode, stringWriter);
            System.out.println("stringWriter:\n" + stringWriter.toString());
        } catch (JAXBException exception) {
            System.err.println("jaxb error:" + exception.getMessage());
            throw new PluginException(exception.getMessage());
        }
    }

    private String getTypeConstraint(MetadataFileType fileType) {
        String typeConstraint = "";
        if (fileType != null) {
            final String imdiType = fileType.getImdiType();
            final String profileId = fileType.getProfileIdString();
            if (imdiType != null) {
                typeConstraint = "[/*:METATRANSCRIPT/count(" + imdiType + ") > 0]";
            } else if (profileId != null) {
                typeConstraint = "[*:CMD/@*:schemaLocation contains text '" + profileId + "']/*:CMD/*:Components/*";
            }
        }
        return typeConstraint;
    }

    private String getFieldConstraint(MetadataFileType fieldType) {
        String fieldConstraint = "";
        if (fieldType != null) {
            final String fieldNameString = fieldType.getFieldName();
            if (fieldNameString != null) {
                fieldConstraint = "name() = '" + fieldNameString + "' and ";
            }
        }
        return fieldConstraint;
    }

    private String getSearchTextConstraint(SearchNegator searchNegator, SearchType searchType, String searchString) {
        final String escapedSearchString = escapeBadChars(searchString);
        String returnString = "";
        switch (searchType) {
            case contains:
                if (escapedSearchString.isEmpty()) {
                    // when the user has not entered any string then return all, but allow the negator to still be used
                    returnString = "1=1";
                } else {
                    returnString = "text() contains text '" + escapedSearchString + "'";
                }
                break;
            case equals:
                returnString = "text() = '" + escapedSearchString + "'";
                break;
            case fuzzy:
                returnString = "text() contains text '" + escapedSearchString + "' using fuzzy";
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
     * let $elementSet0 := for $nameString0 in collection('ArbilDatabase')//*:Address[count(*) = 0] order by $nameString0 return $nameString0
     let $elementSet1 := for $nameString0 in collection('ArbilDatabase')//*:Region[count(*) = 0] order by $nameString0 return $nameString0
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
            String currentFieldName = treeBranchType.getFieldName();
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

    private String getTreeQuery(ArrayList<MetadataFileType> treeBranchTypeList) {
//        String branchConstraint = "//treeBranchType.getFieldName()";

        return "<TreeNode><DisplayString>All</DisplayString>\n"
                + getTreeSubQuery(treeBranchTypeList, "", "", "", 0)
                + "</TreeNode>";


        /*
         for $d in distinct-values(doc("order.xml")//item/@dept)
         let $items := doc("order.xml")//item[@dept = $d]
         order by $d
         return <department code="{$d}">{
         for $i in $items
         order by $i/@num
         return $i
         }</department>

         */
    }

    private String getTreeFieldNames(MetadataFileType fileType, boolean fastQuery) {
        // todo: note that this does not filter the sub lists. While this loss is worth the speed gain, it may be possible to filter based on the same file type or similar.
//        String countClause;
//        if (fastQuery) {
//            countClause = "";
//        } else {
//            countClause = "<RecordCount>{count(distinct-values(collection('ArbilDatabase')/descendant-or-self::*[name() = $nameString]/text()))}</RecordCount>";
//        }
//        String typeConstraint = getTypeConstraint(fileType);
//        String noChildClause = "[count(*) = 0]";
//        String hasTextClause = "[text() != '']";
        return "<MetadataFileType>\n"
                + "{\n"
                //                + "for $nameString in distinct-values(collection('" + databaseName + "')" + typeConstraint + "/descendant-or-self::*" + noChildClause + hasTextClause + "/name()\n"
                //                + ")\n"
                //                + "order by $nameString\n"
                //                + "return\n"
                //                + "<MetadataFileType>"
                //                + "<fieldName>{$nameString}</fieldName>"
                //                + countClause
                //                + "</MetadataFileType>\n"
                /*
                 * optimised this query 2012-10-17
                 * the fast version of query above takes:
                 * 2586.19 ms
                 * the slow version of query above takes:
                 * 48998.5 ms
                 * the query below takes:
                 * 9.82 ms (varies per run)
                 */
                + "for $facetEntry in index:facets('ArbilDatabase', 'flat')//element[entry/text() != '']\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<fieldName>{string($facetEntry/@name)}</fieldName>\n"
                //                + "<RecordCount>{string($facetEntry/@count)}</RecordCount>\n"
                //                + "<ValueCount>{count($facetEntry/entry)}</ValueCount>\n"
                + "<RecordCount>{count($facetEntry/entry)}</RecordCount>\n"
                + "</MetadataFileType>\n"
                + "}</MetadataFileType>";
    }

    private String getSearchFieldConstraint(SearchParameters searchParameters) {
        String fieldConstraint = getFieldConstraint(searchParameters.getFieldType());
        String searchTextConstraint = getSearchTextConstraint(searchParameters.getSearchNegator(), searchParameters.getSearchType(), searchParameters.getSearchString());
        return fieldConstraint + searchTextConstraint;
    }

    private String getSearchConstraint(SearchParameters searchParameters) {
        String typeConstraint = getTypeConstraint(searchParameters.getFileType());
        String fieldConstraint = getFieldConstraint(searchParameters.getFieldType());
        // todo: add to query: boolean searchNot, SearchType searchType, String searchString
        String searchTextConstraint = getSearchTextConstraint(searchParameters.getSearchNegator(), searchParameters.getSearchType(), searchParameters.getSearchString());

        return //"for $nameString in distinct-values(\n"
                "collection('" + databaseName + "')[" + typeConstraint + "//" + fieldConstraint + searchTextConstraint + "]\n";
//                + "return\n"
//                + "<MetadataTreeNode>\n"
//                + "<FileUri>{base-uri($entityNode)}</FileUri>\n"
//                + "<FileUriPath>{path($entityNode)}</FileUriPath>\n"
//                + "</MetadataTreeNode>\n";
//                + "return concat(base-uri($entityNode), path($entityNode))\n"
//                + ")\n"
        //                + "order by $nameString\n"
//                + "return\n"
//                + "<MetadataTreeNode><arbilPathString>{$nameString}</arbilPathString></MetadataTreeNode>\n";
    }

    private String getPopulatedFieldNames(MetadataFileType fileType) {
        String typeConstraint = getTypeConstraint(fileType);
        return "let $allFieldNames := index:facets('ArbilDatabase')//element[text/@type = 'text']\n"
                + "return <MetadataFileType>\n"
                + "<MetadataFileType><displayString>All Fields</displayString>"
                //                + "<RecordCount>{sum($allFieldNames/text/@count)}</RecordCount>\n"
                + "</MetadataFileType>\n"
                //                + "for $nameString in distinct-values(\n"
                //                + "for $entityNode in collection('" + databaseName + "')" + typeConstraint + "/descendant-or-self::*[count(*) = 0]\n"
                //                + "return $entityNode/name()\n"
                //                + ")\n"
                //                + "order by $nameString\n"
                //                + "return\n"
                //                + "<MetadataFileType>"
                //                + "<fieldName>{$nameString}</fieldName>"
                //                + "<RecordCount>{count(collection('ArbilDatabase')/descendant-or-self::*[name() = $nameString]/text())}</RecordCount>"
                //                + "</MetadataFileType>\n"
                /*
                 * optimised this query 2012-10-17
                 * the query above takes:
                 * 66932.06 ms
                 * the query below takes:
                 * 12.39 ms (varies per run)
                 */
                + "{\nfor $facetEntry in $allFieldNames\n"
                + "let $nameString := $facetEntry/@name\n"
                + "group by $nameString\n"
                + "order by $nameString\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<fieldName>{$nameString}</fieldName>\n"
                //                + "<RecordCount>{string($facetEntry/@count)}</RecordCount>\n"
                //                + "<ValueCount>{count($facetEntry/entry)}</ValueCount>\n"
                + "<RecordCount>{sum($facetEntry/text/@count)}</RecordCount>\n"
                + "</MetadataFileType>\n"
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
                + "<displayString>All Types</displayString>\n"
                + "<RecordCount>{count(collection('ArbilDatabase'))}</RecordCount>\n"
                + "</MetadataFileType>\n"
                + "{\n"
                //                + "for $imdiType in distinct-values(collection('ArbilDatabase')/*:METATRANSCRIPT/*/name())\n"
                //                + "order by $imdiType\n"
                //                + "return\n"
                //                + "<MetadataFileType>\n"
                //                + "<ImdiType>{$imdiType}</ImdiType>\n"
                //                + "<RecordCount>{count(collection('ArbilDatabase')/*:METATRANSCRIPT/*[name()=$imdiType])}</RecordCount>\n"
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
                + "for $profileInfo in index:facets('ArbilDatabase')/document-node/element[@name='METATRANSCRIPT']/element[@name!='History']\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<fieldName>{string($profileInfo/@name)}</fieldName>\n"
                + "<RecordCount>{string($profileInfo/@count)}</RecordCount>\n"
                + "</MetadataFileType>"
                + "},{"
                + "for $profileInfo in index:facets('ArbilDatabase')/document-node/element[@name='CMD']/element[@name='Header']/element[@name='MdProfile']/text/entry\n"
                + "return\n"
                + "<MetadataFileType>\n"
                + "<fieldName>{string($profileInfo)}</fieldName>\n"
                + "<RecordCount>{string($profileInfo/@count)}</RecordCount>\n"
                //                + "<ValueCount>{count($profileInfo/entry)}</ValueCount>\n"
                + "</MetadataFileType>\n"
                //                + "},{"
                + "}</MetadataFileType>";
    }

    private String getPopulatedPaths() {
//        return "for $xpathString in distinct-values(\n"
//                + "for $entityNode in collection('" + databaseName + "')/*\n"
//                + "return path($entityNode)\n"
//                + ")\n"
//                + "return"
//                + "$xpathString";
        return "<MetadataFileType>\n"
                + "<MetadataFileType><displayString>All Types</displayString></MetadataFileType>\n"
                + "{\n"
                + "for $xpathString in distinct-values(\n"
                + "for $entityNode in collection('" + databaseName + "')/*\n"
                + "return path($entityNode)\n"
                + ")\n"
                + "order by $xpathString\n"
                + "return\n"
                + "<MetadataFileType><rootXpath>{$xpathString}</rootXpath></MetadataFileType>\n"
                + "}</MetadataFileType>";
    }

    public D getSearchResult(CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) throws QueryException {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("<DataNode> {\n");
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
                + "<DataNode NodeURI=\"{base-uri($documentNode)}\" Label=\"a resutA\">\n"
                + "{\n"
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
                 for $fieldNode in collection('ArbilDatabase')//.[(text() contains text 'pu6') or (name() = 'Name' and text() contains text 'pu8')]
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
                + "for $entityNode in $documentNode//*[");
        boolean firstConstraint = true;
        for (SearchParameters searchParameters : searchParametersList) {
            if (firstConstraint) {
                firstConstraint = false;
            } else {
                queryStringBuilder.append(" or ");
            }
            queryStringBuilder.append(getSearchFieldConstraint(searchParameters));
        }
        queryStringBuilder.append("]\n"
                + "return <DataNode NodeURI=\"{path($entityNode)}\" Label=\"a resutB\"/>\n"
                + "}</DataNode>\n"
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
    public M[] getPathMetadataTypes(MetadataFileType metadataFileType) throws QueryException {
        final String queryString = getPopulatedPaths();
        return getMetadataTypes(queryString);
    }

    public M[] getFieldMetadataTypes(MetadataFileType metadataFileType) throws QueryException {
        final String queryString = getPopulatedFieldNames(metadataFileType);
        return getMetadataTypes(queryString);
    }

    public M[] getMetadataTypes(MetadataFileType metadataFileType) throws QueryException {
        final String queryString = getMetadataTypes();
        return getMetadataTypes(queryString);
    }

    public M[] getTreeFieldTypes(MetadataFileType metadataFileType, boolean fastQuery) throws QueryException {
        final String queryString = getTreeFieldNames(metadataFileType, fastQuery);
        return getMetadataTypes(queryString);
    }

//    public DbTreeNode getSearchTreeData() {
//        final String queryString = getTreeQuery(treeBranchTypeList);
//        return getDbTreeNode(queryString);
//    }
    public D getTreeData(final ArrayList<MetadataFileType> treeBranchTypeList) throws QueryException {
        final String queryString = getTreeQuery(treeBranchTypeList);
        return getDbTreeNode(queryString);
    }

    private D getDbTreeNode(String queryString) throws QueryException {
        long startTime = System.currentTimeMillis();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(dClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String queryResult;
            synchronized (databaseLock) {
                System.out.println("queryString: " + queryString);
                queryResult = new XQuery(queryString).execute(context);
            }
            System.out.println("queryResult: " + queryResult);
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
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error getting search options");
        }
    }

    private M[] getMetadataTypes(final String queryString) throws QueryException {
        long startTime = System.currentTimeMillis();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(mClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String queryResult;
            synchronized (databaseLock) {
                System.out.println("queryString: " + queryString);
                queryResult = new XQuery(queryString).execute(context);
            }
            System.out.println("queryResult: " + queryResult);
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
        } catch (BaseXException exception) {
            logger.debug(exception.getMessage());
            throw new QueryException("Error getting search options");
        }
    }
}
