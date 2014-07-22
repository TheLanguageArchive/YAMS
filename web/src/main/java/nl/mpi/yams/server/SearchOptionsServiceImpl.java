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
package nl.mpi.yams.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yams.client.SearchOptionsService;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.DatabaseList;
import nl.mpi.yams.common.data.DatabaseStats;
import nl.mpi.yams.common.data.HighlightableDataNode;
import nl.mpi.yams.common.data.IconTableBase64;
import nl.mpi.yams.common.data.MetadataFileType;
import nl.mpi.yams.common.data.QueryDataStructures;
import nl.mpi.yams.common.data.SearchParameters;
import nl.mpi.yams.common.db.DataBaseManager;
import nl.mpi.yams.common.db.DbAdaptor;
import nl.mpi.yams.common.db.RestDbAdaptor;
import nl.mpi.yams.shared.WebQueryException;
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
//        return "http://localhost:8984/rest/";
//        return "http://lux16.mpi.nl:8984/rest/";
    }

    private String getBasexUser() {
        final String initParameterUser = getServletContext().getInitParameter("basexUser");
        return initParameterUser;
//        return DataBaseManager.guestUser;
//        return "admin";
    }

    private String getBasexPass() {
        final String initParameterPass = getServletContext().getInitParameter("basexPass");
        return initParameterPass;
//        return DataBaseManager.guestUser;
//        return "admin";
    }

    public DatabaseList getDatabaseList() throws WebQueryException {
//        logger.info("getDatabaseList");
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(DataBaseManager.defaultDataBase);
            return yamsDatabase.getDatabaseStatsList();
        } catch (QueryException exception) {
            //exception.printStackTrace();
            //logger.error(exception.getMessage());
            throw new WebQueryException("getDatabaseList", exception);
        }
    }

    public DatabaseStats getDatabaseStats(String databaseName) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            DatabaseStats databaseStats = yamsDatabase.getDatabaseStats();
            return databaseStats;
        } catch (QueryException exception) {
            throw new WebQueryException("getDatabaseStats", exception);
        }
    }

    private DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> getDatabase(String databaseName) throws QueryException {
        // the LocalDbAdaptor version of the Arbil database is not intended to multi entry and has be replaced by a REST version
//        final DbAdaptor dbAdaptor = new LocalDbAdaptor(new File(System.getProperty("user.dir"), "yams-data"));
        String basexRestUrl = getBasexRestUrl();
//        System.out.println("basexRestUrl: " + basexRestUrl);
        //logger.info("getDatabase:" + databaseName);
        //System.out.println("getDatabase: " + databaseName);
        try {
            final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), getBasexUser(), getBasexPass());
            return new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, databaseName);
        } catch (MalformedURLException exception) {
            throw new QueryException("Failed to open the database connection at: " + basexRestUrl, exception);
        }
    }

    public MetadataFileType[] getTypeOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException {
        //logger.info("getTypeOptions:" + databaseName);
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataPathTypes = yamsDatabase.getMetadataTypes(metadataFileType);
            return metadataPathTypes;
        } catch (QueryException exception) {
            throw new WebQueryException("getTypeOptions", exception);
        }
    }

    public MetadataFileType[] getPathOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataFieldTypes = yamsDatabase.getMetadataPaths(metadataFileType);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException("getPathOptions", exception);
        }
    }

    public MetadataFileType[] getValueOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataFieldTypes = yamsDatabase.getMetadataFieldValues(metadataFileType, 5);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException("getValueOptions", exception);
        }
    }

    public MetadataFileType[] getTreeFacets(String databaseName, MetadataFileType[] metadataFileTypes) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            MetadataFileType[] metadataFieldTypes = yamsDatabase.getTreeFacetTypes(metadataFileTypes);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException("getTreeFacets", exception);
        }
    }

    public HighlightableDataNode performSearch(String databaseName, QueryDataStructures.CriterionJoinType criterionJoinType, List<SearchParameters> searchParametersList) throws WebQueryException {
//        return new YamsDataNode(criterionJoinType.name());
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            //logger.info("databaseName:" + databaseName);
            HighlightableDataNode yamsDataNode = (HighlightableDataNode) yamsDatabase.getSearchResult(criterionJoinType, searchParametersList);
            return yamsDataNode;
//            ArrayList<String> returnList = new ArrayList<String>();
//            for (WebMetadataFileType metadataFileType : metadataFieldTypes) {
//                returnList.add(metadataFileType.getFieldName());
//            };
//            return returnList.toArray(new String[0]);
        } catch (QueryException exception) {
            //exception.printStackTrace();
            throw new WebQueryException("performSearch:" + databaseName + ":" + criterionJoinType.name() + ":" + searchParametersList.size(), exception);
        }
    }

    public List<SerialisableDataNode> getDataNodesByHdl(String databaseName, List<String> hdlList) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            SerialisableDataNode yamsDataNode = yamsDatabase.getNodeDatasByHdls(hdlList);
            final List<SerialisableDataNode> name = (List<SerialisableDataNode>) yamsDataNode.getChildList();
            //logger.info("getDataNodesByHdl:done:" + yamsDataNode.getChildList().size());
            return name;
        } catch (QueryException exception) {
            throw new WebQueryException("getDataNodesByHdl", exception);
        }
    }

    public List<SerialisableDataNode> getDataNodesByUrl(String databaseName, List<String> urlList) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            SerialisableDataNode yamsDataNode = yamsDatabase.getNodeDatasByUrls(urlList);
            return (List<SerialisableDataNode>) yamsDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException("getDataNodesByUrl", exception);
        }
    }

    public List<SerialisableDataNode> getDataNodes(String databaseName, List<DataNodeId> dataNodeIds) throws WebQueryException {
        try {
            logger.info("getDataNodes");
            logger.info(databaseName);
            logger.info(Integer.toString(dataNodeIds.size()));
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            SerialisableDataNode yamsDataNode = yamsDatabase.getNodeDatasByIDs(dataNodeIds);
            return (List<SerialisableDataNode>) yamsDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException("getDataNodes", exception);
        }
    }

    public IconTableBase64 getImageDataForTypes(String databaseName) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            final IconTableBase64 nodeIcons = yamsDatabase.getNodeIconsBase64();
            return nodeIcons;
        } catch (PluginException exception) {
            throw new WebQueryException("getImageDataForTypes", exception);
        } catch (QueryException exception) {
            throw new WebQueryException("getImageDataForTypes", exception);
        }
    }

    public List<SerialisableDataNode> getRootDataNodes(String databaseName) throws WebQueryException {
        try {
            logger.info("getRootDataNodes");
            logger.info(databaseName);
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase = getDatabase(databaseName);
            SerialisableDataNode yamsDataNode = yamsDatabase.getRootNodes();
            return (List<SerialisableDataNode>) yamsDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException("getRootDataNodes", exception);
        }
    }
}
