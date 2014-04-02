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
package nl.mpi.yaas.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.client.SearchOptionsService;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.HighlighableDataNode;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;
import nl.mpi.yaas.common.db.DataBaseManager;
import nl.mpi.yaas.common.db.DbAdaptor;
import nl.mpi.yaas.common.db.RestDbAdaptor;
import nl.mpi.yaas.shared.WebQueryException;
import org.slf4j.LoggerFactory;

/**
 * Created on : Jan 30, 2013, 5:23:13 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@SuppressWarnings("serial")
//@RemoteServiceRelativePath("SearchOptionsService")
public class SearchOptionsServiceImpl extends RemoteServiceServlet implements SearchOptionsService {

    final private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private String getBasexRestUrl() {
        final String initParameterRestUrl = getServletContext().getInitParameter("basexRestUrl");
        return initParameterRestUrl;
//        return (initParameterRestUrl != null && !initParameterRestUrl.isEmpty()) ? initParameterRestUrl : "http://localhost:8984/rest/";
//        return "http://tlatest06:8984/rest/";
    }

    private String getBasexUser() {
        final String initParameterUser = getServletContext().getInitParameter("basexUser");
        return initParameterUser;
//        return (initParameterUser != null && !initParameterUser.isEmpty()) ? initParameterUser : DataBaseManager.guestUser;
    }

    private String getBasexPass() {
        final String initParameterPass = getServletContext().getInitParameter("basexPass");
        return initParameterPass;
//        return (initParameterPass != null && !initParameterPass.isEmpty()) ? initParameterPass : DataBaseManager.guestUser;
    }

    public String[] getDatabaseList() throws WebQueryException {
//        logger.info("getDatabaseList");
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(DataBaseManager.defaultDataBase);
            return yaasDatabase.getDatabaseList();
        } catch (QueryException exception) {
            throw new WebQueryException(exception);
        }
    }

    public DatabaseStats getDatabaseStats(String databaseName) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            DatabaseStats databaseStats = yaasDatabase.getDatabaseStats();
            return databaseStats;
        } catch (QueryException exception) {
            throw new WebQueryException(exception);
        }
    }

    private DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> getDatabase(String databaseName) throws QueryException {
        // the LocalDbAdaptor version of the Arbil database is not intended to multi entry and has be replaced by a REST version
//        final DbAdaptor dbAdaptor = new LocalDbAdaptor(new File(System.getProperty("user.dir"), "yaas-data"));
        String basexRestUrl = getBasexRestUrl();
//        System.out.println("basexRestUrl: " + basexRestUrl);
        try {
            final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), getBasexUser(), getBasexPass());
            return new DataBaseManager<HighlighableDataNode, DataField, MetadataFileType>(HighlighableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, databaseName);
        } catch (MalformedURLException exception) {
            throw new QueryException("Failed to open the database connection at: " + basexRestUrl + " " + exception.getMessage());
        }
    }

    public MetadataFileType[] getTypeOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataPathTypes = yaasDatabase.getMetadataTypes(metadataFileType);
            return metadataPathTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getPathOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataFieldTypes = yaasDatabase.getMetadataPaths(metadataFileType);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getValueOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataFieldTypes = yaasDatabase.getMetadataFieldValues(metadataFileType, 5);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getTreeFacets(String databaseName, MetadataFileType[] metadataFileTypes) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataFieldTypes = yaasDatabase.getTreeFacetTypes(metadataFileTypes);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public HighlighableDataNode performSearch(String databaseName, QueryDataStructures.CriterionJoinType criterionJoinType, List<SearchParameters> searchParametersList) throws WebQueryException {
//        return new YaasDataNode(criterionJoinType.name());
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            HighlighableDataNode yaasDataNode = yaasDatabase.getSearchResult(criterionJoinType, searchParametersList);
            return yaasDataNode;
//            ArrayList<String> returnList = new ArrayList<String>();
//            for (WebMetadataFileType metadataFileType : metadataFieldTypes) {
//                returnList.add(metadataFileType.getFieldName());
//            };
//            return returnList.toArray(new String[0]);
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public List<SerialisableDataNode> getDataNodesByHdl(String databaseName, List<String> hdlList) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            SerialisableDataNode yaasDataNode = yaasDatabase.getNodeDatasByHdls(hdlList);
            return (List<SerialisableDataNode>) yaasDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public List<SerialisableDataNode> getDataNodesByUrl(String databaseName, List<String> urlList) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            SerialisableDataNode yaasDataNode = yaasDatabase.getNodeDatasByUrls(urlList);
            return (List<SerialisableDataNode>) yaasDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public List<SerialisableDataNode> getDataNodes(String databaseName, List<DataNodeId> dataNodeIds) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            SerialisableDataNode yaasDataNode = yaasDatabase.getNodeDatasByIDs(dataNodeIds);
            return (List<SerialisableDataNode>) yaasDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public IconTableBase64 getImageDataForTypes(String databaseName) throws WebQueryException {
        try {
            DataBaseManager<HighlighableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase(databaseName);
            final IconTableBase64 nodeIcons = yaasDatabase.getNodeIconsBase64();
            return nodeIcons;
        } catch (PluginException exception) {
            throw new WebQueryException(exception.getMessage());
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }
}
