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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.HighlighableDataNode;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.db.DataBaseManager;
import nl.mpi.yaas.common.db.DbAdaptor;
import nl.mpi.yaas.common.db.RestDbAdaptor;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 *
 * @since Feb 17, 2014 3:41 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Path("yams")
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
        DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(DataBaseManager.defaultDataBase);
        for (String dbName : yaasDatabase.getDatabaseList()) {
            stringBuilder.append(dbName);
            stringBuilder.append("&nbsp;<a href=\"dbinfo/");
            stringBuilder.append(dbName);
            stringBuilder.append("\">dbinfo</a>&nbsp;");
            stringBuilder.append("<a href=\"hints/");
            stringBuilder.append(dbName);
            stringBuilder.append("\">hints</a><br>");
        }
        return stringBuilder.toString();
    }

    @GET
    @Path("/dbinfo/{dbname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatabaseInfo(@PathParam("dbname") String dbName) throws QueryException {
        DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(dbName);
        DatabaseStats databaseStats = yaasDatabase.getDatabaseStats();
        return Response.ok(databaseStats).build();
    }

    @GET
    @Path("/hints/{dbname}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getHints(@PathParam("dbname") String dbName) throws QueryException {
        final MetadataFileType options = new MetadataFileType(null, null, "Dutch");
        DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(dbName);
        MetadataFileType[] metadataFieldTypes = yaasDatabase.getMetadataFieldValues(options);
        return Response.ok(metadataFieldTypes).build();
    }

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

    private DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> getDatabase(String databaseName) throws QueryException {
        String basexRestUrl = getBasexRestUrl();
        try {
            final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), getBasexUser(), getBasexPass());
            return new DataBaseManager(HighlighableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, databaseName);
        } catch (MalformedURLException exception) {
            throw new QueryException("Failed to open the database connection at: " + basexRestUrl + " " + exception.getMessage());
        }
    }
}
