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
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.HighlighableDataNode;
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

    DatabaseStats getDatabaseStats(String databaseName) throws WebQueryException;

    String[] getDatabaseList() throws WebQueryException;

    MetadataFileType[] getTypeOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException;

    MetadataFileType[] getPathOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException;

    MetadataFileType[] getValueOptions(String databaseName, MetadataFileType metadataFileType) throws WebQueryException;

    MetadataFileType[] getTreeFacets(String databaseName, MetadataFileType[] metadataFileTypes) throws WebQueryException;

    HighlighableDataNode performSearch(String databaseName, QueryDataStructures.CriterionJoinType criterionJoinType, List<SearchParameters> searchParametersList) throws WebQueryException;

    List<SerialisableDataNode> getDataNodes(String databaseName, List<DataNodeId> dataNodeIds) throws WebQueryException;

    IconTableBase64 getImageDataForTypes(String databaseName) throws WebQueryException;
}
