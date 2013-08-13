/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.client.SearchOptionsService;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;
import nl.mpi.yaas.common.db.DataBaseManager;
import nl.mpi.yaas.common.db.DbAdaptor;
import nl.mpi.yaas.common.db.RestDbAdaptor;
import nl.mpi.yaas.shared.WebQueryException;

/**
 * Created on : Jan 30, 2013, 5:23:13 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@SuppressWarnings("serial")
//@RemoteServiceRelativePath("SearchOptionsService")
public class SearchOptionsServiceImpl extends RemoteServiceServlet implements SearchOptionsService {

    public DatabaseStats getDatabaseStats() throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            DatabaseStats databaseStats = yaasDatabase.getDatabaseStats();
            return databaseStats;
        } catch (QueryException exception) {
            throw new WebQueryException(exception);
        }
    }

    private DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> getDatabase() throws QueryException {
        // the LocalDbAdaptor version of the Arbil database is not intended to multi entry and has be replaced by a REST version
//        final DbAdaptor dbAdaptor = new LocalDbAdaptor(new File(System.getProperty("user.dir"), "yaas-data"));
        try {
            final String basexRestUrl = getInitParameter("basexRestUrl");
//            final String basexRestUrl = "http://tlatest03:8984/rest/";
            System.out.println("basexRestUrl: " + basexRestUrl);
            final DbAdaptor dbAdaptor = new RestDbAdaptor(new URL(basexRestUrl), DataBaseManager.guestUser, DataBaseManager.guestUserPass);
            return new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, DataBaseManager.defaultDataBase);
        } catch (MalformedURLException exception) {
            throw new QueryException(exception);
        }
    }

    public MetadataFileType[] getTypeOptions(MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            MetadataFileType[] metadataPathTypes = yaasDatabase.getMetadataTypes(metadataFileType);
            return metadataPathTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getPathOptions(MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            MetadataFileType[] metadataFieldTypes = yaasDatabase.getMetadataPaths(metadataFileType);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getValueOptions(MetadataFileType metadataFileType) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            MetadataFileType[] metadataFieldTypes = yaasDatabase.getMetadataFieldValues(metadataFileType);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getTreeFacets(MetadataFileType[] metadataFileTypes) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            MetadataFileType[] metadataFieldTypes = yaasDatabase.getTreeFacetTypes(metadataFileTypes);
            return metadataFieldTypes;
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public SerialisableDataNode performSearch(QueryDataStructures.CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) throws WebQueryException {
//        return new YaasDataNode(criterionJoinType.name());
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            SerialisableDataNode yaasDataNode = yaasDatabase.getSearchResult(criterionJoinType, searchParametersList);
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

    public List<SerialisableDataNode> getDataNodes(ArrayList<DataNodeId> dataNodeIds) throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            SerialisableDataNode yaasDataNode = yaasDatabase.getNodeDatasByIDs(dataNodeIds);
            return (List<SerialisableDataNode>) yaasDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public IconTableBase64 getImageDataForTypes() throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase = getDatabase();
            final IconTableBase64 nodeIcons = yaasDatabase.getNodeIconsBase64();
            return nodeIcons;
        } catch (PluginException exception) {
            throw new WebQueryException(exception.getMessage());
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }
}
