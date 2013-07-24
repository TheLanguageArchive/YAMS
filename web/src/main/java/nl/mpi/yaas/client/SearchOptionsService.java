/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;
import java.util.List;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;
import nl.mpi.yaas.shared.WebQueryException;

/**
 * Created on : Jan 30, 2013, 5:21:01 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@RemoteServiceRelativePath("searchoptions")
public interface SearchOptionsService extends RemoteService {

    DatabaseStats getDatabaseStats() throws WebQueryException;

    MetadataFileType[] getTypeOptions(MetadataFileType metadataFileType) throws WebQueryException;

    MetadataFileType[] getPathOptions(MetadataFileType metadataFileType) throws WebQueryException;

    MetadataFileType[] getValueOptions(MetadataFileType metadataFileType) throws WebQueryException;

    MetadataFileType[] getTreeFacets(MetadataFileType[] metadataFileTypes) throws WebQueryException;

    SerialisableDataNode performSearch(QueryDataStructures.CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) throws WebQueryException;

    List<SerialisableDataNode> getDataNodes(ArrayList<DataNodeId> dataNodeIds) throws WebQueryException;

    IconTableBase64 getImageDataForTypes() throws WebQueryException;
}
