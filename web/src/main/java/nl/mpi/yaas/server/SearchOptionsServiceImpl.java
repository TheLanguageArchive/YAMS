/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginSessionStorage;
import nl.mpi.yaas.client.SearchOptionsService;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;
import nl.mpi.yaas.common.db.DataBaseManager;
import nl.mpi.yaas.shared.WebQueryException;
import nl.mpi.yaas.shared.YaasDataField;

/**
 * Created on : Jan 30, 2013, 5:23:13 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@SuppressWarnings("serial")
public class SearchOptionsServiceImpl extends RemoteServiceServlet implements SearchOptionsService {

    public DatabaseStats getDatabaseStats() throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType> arbilDatabase = getDatabase();
            DatabaseStats databaseStats = arbilDatabase.getDatabaseStats();
            return databaseStats;
        } catch (QueryException exception) {
            throw new WebQueryException(exception);
        }
    }

    private DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType> getDatabase() throws QueryException {
        // todo: this version of the Arbil database is not intended to multi entry and will be replaced by a rest version when it is written
        final PluginSessionStorage pluginSessionStorage = new PluginSessionStorage() {
            public File getApplicationSettingsDirectory() {
                return new File("/Users/petwit2/.arbil/");
            }

            public File getProjectDirectory() {
                return new File("/Users/petwit2/.arbil/");
            }

            public File getProjectWorkingDirectory() {
                return new File("/Users/petwit2/.arbil/ArbilWorkingFiles/");
            }
        };
        return new DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType>(SerialisableDataNode.class, YaasDataField.class, MetadataFileType.class, pluginSessionStorage, DataBaseManager.defaultDataBase);
    }

    public MetadataFileType[] getTypeOptions() throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType> arbilDatabase = getDatabase();
            MetadataFileType[] metadataPathTypes = arbilDatabase.getMetadataTypes(null);
            return metadataPathTypes;
//            ArrayList<String> returnList = new ArrayList<String>();
//            for (WebMetadataFileType metadataFileType : metadataPathTypes) {
//                returnList.add(metadataFileType.getFieldName());
//            };
//            return returnList.toArray(new String[0]);
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public MetadataFileType[] getFieldOptions() throws WebQueryException {
        try {
            DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType> arbilDatabase = getDatabase();
            MetadataFileType[] metadataFieldTypes = arbilDatabase.getFieldMetadataTypes(null);
            return metadataFieldTypes;
//            ArrayList<String> returnList = new ArrayList<String>();
//            for (WebMetadataFileType metadataFileType : metadataFieldTypes) {
//                returnList.add(metadataFileType.getFieldName());
//            };
//            return returnList.toArray(new String[0]);
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }

    public SerialisableDataNode performSearch(QueryDataStructures.CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) throws WebQueryException {
//        return new YaasDataNode(criterionJoinType.name());
        try {
            DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType> arbilDatabase = getDatabase();
            SerialisableDataNode yaasDataNode = arbilDatabase.getSearchResult(criterionJoinType, searchParametersList);
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
            DataBaseManager<SerialisableDataNode, YaasDataField, MetadataFileType> arbilDatabase = getDatabase();
            SerialisableDataNode yaasDataNode = arbilDatabase.getNodeDatasByIDs(dataNodeIds);
            return (List<SerialisableDataNode>) yaasDataNode.getChildList();
        } catch (QueryException exception) {
            throw new WebQueryException(exception.getMessage());
        }
    }
}
