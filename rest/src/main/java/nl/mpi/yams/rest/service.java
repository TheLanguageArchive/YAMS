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
package nl.mpi.yams.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.DatabaseList;
import nl.mpi.yams.common.data.DatabaseStats;
import nl.mpi.yams.common.data.HighlightableDataNode;
import nl.mpi.yams.common.data.MetadataFileType;
import nl.mpi.yams.common.data.QueryDataStructures;
import nl.mpi.yams.common.data.SearchParameters;
import nl.mpi.yams.common.db.DataBaseManager;
import nl.mpi.yams.common.db.DbAdaptor;
import nl.mpi.yams.common.db.RestDbAdaptor;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 *
 * @since Feb 17, 2014 3:41 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Path("")
public class service {

    final private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    @Context
    private UriInfo context;
    @Context
    ServletConfig servletConfig;
    @Context
    ServletContext servletContext;

    /**
     * Creates a new instance of service
     */
    public service() {
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    public String getHtmlListing() throws QueryException {
        StringBuilder stringBuilder = new StringBuilder();
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(DataBaseManager.defaultDataBase);
        stringBuilder.append("<a href=\"./dbinfo\">dbinfo</a><br>");
        for (String dbName : yamsDatabase.getDatabaseList()) {
            stringBuilder.append("<h3>");
            stringBuilder.append(dbName);
            stringBuilder.append("</h3><a href=\"./dbinfo/");
            stringBuilder.append(dbName);
            stringBuilder.append("\">dbinfo</a><br>");
            stringBuilder.append("<a href=\"./stats/");
            stringBuilder.append(dbName);
            stringBuilder.append("\">db stats</a><br>");
            stringBuilder.append("<a href=\"./data/");
            stringBuilder.append(dbName);
            stringBuilder.append("\">root nodes</a><br>");
            stringBuilder.append("<a href=\"./types/");
            stringBuilder.append(dbName);
            stringBuilder.append("\">types</a><br>");
            stringBuilder.append("<a href=\"./paths/");
            stringBuilder.append(dbName);
            stringBuilder.append("?type=\">paths</a><br>");
            stringBuilder.append("<a href=\"./hints/");
            stringBuilder.append(dbName);
            stringBuilder.append("?type=&path=&text=");
            stringBuilder.append("\">hints</a><br>");
            stringBuilder.append("<a href=\"./search/");
            stringBuilder.append(dbName);
            stringBuilder.append("/");
            stringBuilder.append(QueryDataStructures.CriterionJoinType.union.name());
            stringBuilder.append("?sn=");
            stringBuilder.append(QueryDataStructures.SearchNegator.is);
            stringBuilder.append("&st=");
            stringBuilder.append(QueryDataStructures.SearchType.contains);
            stringBuilder.append("&ft=&p=&s=Comic&sn=");
            stringBuilder.append(QueryDataStructures.SearchNegator.is);
            stringBuilder.append("&st=");
            stringBuilder.append(QueryDataStructures.SearchType.contains);
            stringBuilder.append("&ft=&p=&s=Books\">search</a><br>");
        }
        return stringBuilder.toString();
    }

    @GET
    @Path("/dbinfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDatabaseList() throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase("");
        DatabaseList databaseList = yamsDatabase.getDatabaseStatsList();
        return Response.ok(databaseList).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/dbinfo/{dbname}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDatabaseInfo(@PathParam("dbname") String dbName) throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        DatabaseStats databaseStats = yamsDatabase.getDatabaseStats();
        return Response.ok(databaseStats).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/data/{dbname}")
    public Response getRootNode(@PathParam("dbname") String dbName) throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        final SerialisableDataNode rootNodes = yamsDatabase.getRootNodes();
        return Response.ok(rootNodes.getChildList()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/data/{dbname}/linksof")
//    @Path("hdl{hdl}")
    public Response getChildDataNodes(@PathParam("dbname") String dbName, @QueryParam("id") final String identifier, @QueryParam("start") @DefaultValue("0") final int start, @QueryParam("end") @DefaultValue("30") final int end) throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        final SerialisableDataNode childNodes;
        if (identifier.startsWith("hdl:")) {
            childNodes = yamsDatabase.getChildNodesOfHdl(identifier, start, end);
        } else if (identifier.startsWith("http:") || identifier.startsWith("https:")) {
            childNodes = yamsDatabase.getChildNodesOfUrl(identifier, start, end);
        } else {
            childNodes = yamsDatabase.getChildNodesOfId(identifier, start, end);
        }
        return Response.ok(childNodes.getChildList()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/types/{dbname}")
    public Response getTypeOptions(@PathParam("dbname") String dbName) throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        final MetadataFileType[] metadataTypes = yamsDatabase.getMetadataTypes(null);
        return Response.ok(metadataTypes).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/paths/{dbname}")
    public Response getPathOptions(@PathParam("dbname") String dbName, @QueryParam("type") final String type) throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        final MetadataFileType[] metadataTypes = yamsDatabase.getMetadataPaths(new MetadataFileType(type, null, null));
        return Response.ok(metadataTypes).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/hints/{dbname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHints(@PathParam("dbname") String dbName, @QueryParam("type") final String type, @QueryParam("path") final String path, @QueryParam("text") final String text, @QueryParam("max") @DefaultValue("5") final int max) throws QueryException {
        final MetadataFileType options = new MetadataFileType(type, path, text);
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        MetadataFileType[] metadataFieldTypes = yamsDatabase.getMetadataFieldValues(options, max);
        return Response.ok(metadataFieldTypes).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/data/{dbname}/node")
//    @Path("hdl{hdl}")
    public Response getDataNode(@PathParam("dbname") String dbName, @QueryParam("id") final String identifier) throws QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        final SerialisableDataNode childNodes;
        //logger.info(identifier);
        if (identifier.startsWith("hdl:")) {
            List<String> identifierList = new ArrayList<String>();
            identifierList.add(identifier);
            childNodes = yamsDatabase.getNodeDatasByHdls(identifierList);
        } else if (identifier.startsWith("http:") || identifier.startsWith("https:")) {
            List<String> identifierList = new ArrayList<String>();
            identifierList.add(identifier);
            childNodes = yamsDatabase.getNodeDatasByUrls(identifierList);
        } else {
            List<DataNodeId> identifierList = new ArrayList<DataNodeId>();
            identifierList.add(new DataNodeId(identifier));
            childNodes = yamsDatabase.getNodeDatasByIDs(identifierList);
        }
        return Response.ok(childNodes.getChildList()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/search/{dbname}/{jointype}")
    public Response perfomSearch(@PathParam("dbname") String dbName,
            @PathParam("jointype") String joinType,
            @QueryParam("sn") @DefaultValue("is") final List<String> searchNegator,
            @QueryParam("st") @DefaultValue("equals") final List<String> searchType,
            @QueryParam("ft") @DefaultValue("") final List<String> type,
            @QueryParam("p") @DefaultValue("") final List<String> path,
            @QueryParam("s") @DefaultValue("") final List<String> text,
            @QueryParam("start") @DefaultValue("0") final int start,
            @QueryParam("end") @DefaultValue("30") final int end) throws QueryException {
        String basexRestUrl = getBasexRestUrl();
        try {
            final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), getBasexUser(), getBasexPass());
            DataBaseManager<HighlightableDataNode, DataField, MetadataFileType> yamsDatabase = new DataBaseManager(HighlightableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, dbName);
            final ArrayList<SearchParameters> parameters = new ArrayList<SearchParameters>();
            for (int index = 0; index < text.size(); index++) {
                final MetadataFileType metadataFileType = (type.size() < index || type.get(index).isEmpty()) ? new MetadataFileType(null, null, null) : new MetadataFileType(type.get(index), null, null);
                final MetadataFileType metadataPathType = (path.size() < index || path.get(index).isEmpty()) ? new MetadataFileType(null, null, null) : new MetadataFileType(null, path.get(index), null);
                final QueryDataStructures.SearchNegator currentNegator = (searchNegator.size() < index) ? QueryDataStructures.SearchNegator.valueOf(searchNegator.get(searchNegator.size() - 1)) : QueryDataStructures.SearchNegator.valueOf(searchNegator.get(index));
                final QueryDataStructures.SearchType currentType = (searchType.size() < index) ? QueryDataStructures.SearchType.valueOf(searchType.get(searchType.size() - 1)) : QueryDataStructures.SearchType.valueOf(searchType.get(index));
                parameters.add(new SearchParameters(metadataFileType, metadataPathType, currentNegator, currentType, text.get(index)));
            }
//            arrayList.add(new SearchParameters(new MetadataFileType(), new MetadataFileType(), QueryDataStructures.SearchNegator.is, QueryDataStructures.SearchType.contains, "Books"));
            final HighlightableDataNode foundNodes = yamsDatabase.getSearchResult(QueryDataStructures.CriterionJoinType.valueOf(joinType), parameters);
            return Response.ok(foundNodes).header("Access-Control-Allow-Origin", "*").build();
        } catch (MalformedURLException exception) {
            throw new QueryException("Failed to open the database connection at: " + basexRestUrl + " " + exception.getMessage());
        }
    }

    @GET
    @Path("/stats/{dbname}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getStats(@PathParam("dbname") String dbName) throws MalformedURLException, QueryException {
        DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(dbName);
        String jsonDataDetailed = "[[0,0,0,0]]";
        String queryStringDetailed = " ('[[0,0,0,0,0,0]',\n" //[''timestamp'', ''linkcount'', ''documentcount'', ''queryms'']',\n"
                + "let $dbName := '" + dbName + "'\n"
                + "for $crawlerStats in collection($dbName)/CrawlerStats\n"
                // this order by seems to cause problems which might depend on which basex version is being used: + "order by $crawlerStats/@timestamp/string()\n"
                + "let $dateTime := if (empty($crawlerStats/@timestamp)) then \n"
                + " '20130000000000'\n"
                + "else\n"
                + " $crawlerStats/@timestamp/string()\n"
                + "let $maxMemory := if (empty($crawlerStats/@maxMemory)) then \n"
                + " '0'\n"
                + "else\n"
                + " string(($crawlerStats/@maxMemory) div 1048576.0)\n"
                + "let $jsDateTime := string-join(('new Date(', substring($dateTime, 1, 4), ',', substring($dateTime, 5, 2), ',', substring($dateTime, 7, 2), ',', substring($dateTime, 9, 2), ',', substring($dateTime, 11, 2), ',', substring($dateTime, 13, 2),')'),'')\n"
                + "let $linkcount := $crawlerStats/@linkcount/string()\n"
                + "let $documentcount := $crawlerStats/@documentcount/string()\n"
                + "let $querytime := string($crawlerStats/@queryms)\n"
                + "let $freebytes := string(($crawlerStats/@freebytes) div 1048576.0)\n"
                + "let $totalbytes := string(($crawlerStats/@totalbytes) div 1048576.0)\n"
                + "return (',[',string-join(($jsDateTime,$linkcount,$documentcount,$querytime,$freebytes,$totalbytes,$maxMemory),','),']'),']')\n";
        final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(getBasexRestUrl()), getBasexUser(), getBasexPass());
        jsonDataDetailed = dbAdaptor.executeQuery(DataBaseManager.defaultDataBase, queryStringDetailed);
        return Response.ok(jsonDataDetailed).header("Access-Control-Allow-Origin", "*").build();
    }
    
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("/hdn")
//    public Response getHDN() throws QueryException {
//        final HighlightableDataNode dataNode = new HighlightableDataNode();
//        dataNode.setID("getHDN");
//        return Response.ok(dataNode).header("Access-Control-Allow-Origin", "*").build();
//    }
//
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("/sdn")
//    public Response getSDN() throws QueryException {
//        final SerialisableDataNode dataNode = new SerialisableDataNode();
//        dataNode.setID("getSDN");
//        return Response.ok(dataNode).header("Access-Control-Allow-Origin", "*").build();
//    }
//
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("/shdn")
//    public Response getSHDN() throws QueryException {
//        final SerialisableDataNode dataNode = new HighlightableDataNode();
//        dataNode.setID("getSHDN");
//        return Response.ok(dataNode).header("Access-Control-Allow-Origin", "*").build();
//    }
//
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("/cdn")
//    public Response getCDN() throws QueryException {
//        final SerialisableDataNode dataNode = new HighlightableDataNode();
//        dataNode.setID("getSHDN");
//        final SerialisableDataNode parentNode = new HighlightableDataNode();
//        List<SerialisableDataNode> childNodes = new ArrayList<SerialisableDataNode>();
//        childNodes.add(dataNode);
//        parentNode.setChildList(childNodes);
//        return Response.ok(parentNode.getChildList()).header("Access-Control-Allow-Origin", "*").build();
//    }
//
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("/pdn")
//    public Response getPDN() throws QueryException {
//        final SerialisableDataNode dataNode = new HighlightableDataNode();
//        dataNode.setID("getSHDN");
//        final SerialisableDataNode parentNode = new HighlightableDataNode();
//        List<SerialisableDataNode> childNodes = new ArrayList<SerialisableDataNode>();
//        childNodes.add(dataNode);
//        parentNode.setChildList(childNodes);
//        parentNode.setID("parentNode");
//        return Response.ok(parentNode).header("Access-Control-Allow-Origin", "*").build();
//    }
//
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    @Path("/ldn")
//    public Response getLDN() throws QueryException {
//        final HighlightableDataNode dataNode = new HighlightableDataNode();
//        dataNode.setID("getSHDN");
//        List<SerialisableDataNode> childNodes = new ArrayList<SerialisableDataNode>();
//        childNodes.add(dataNode);
//        return Response.ok(childNodes).header("Access-Control-Allow-Origin", "*").build();
//    }
    private String getBasexRestUrl() {
        final String initParameterRestUrl = servletContext.getInitParameter("basexRestUrl");
        return initParameterRestUrl;
    }

    private String getBasexUser() {
        final String initParameterUser = servletContext.getInitParameter("basexUser");
        return initParameterUser;
    }

    private String getBasexPass() {
        final String initParameterPass = servletContext.getInitParameter("basexPass");
        return initParameterPass;
    }

    private DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> getDatabase(String databaseName) throws QueryException {
        String basexRestUrl = getBasexRestUrl();
        try {
            final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), getBasexUser(), getBasexPass());
            return new DataBaseManager(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, databaseName);
        } catch (MalformedURLException exception) {
            throw new QueryException("Failed to open the database connection at: " + basexRestUrl + " " + exception.getMessage());
        }
    }
}
