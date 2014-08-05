/*
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.yams.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import java.util.logging.Logger;
import nl.mpi.flap.model.PluginDataNode;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.IconTableBase64;
import nl.mpi.yams.common.data.NodeTypeImageBase64;
import nl.mpi.yams.common.data.QueryDataStructures;
import nl.mpi.yams.common.data.SearchParameters;

/**
 * @since Mar 26, 2014 1:28:03 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeLoaderRpc implements DataNodeLoader {

    private static final Logger logger = Logger.getLogger("");
    final private SearchOptionsServiceAsync searchOptionsService;
    private final IconTableBase64 iconTableBase64;
    private final String databaseName;

    public DataNodeLoaderRpc(SearchOptionsServiceAsync searchOptionsService, IconTableBase64 iconTableBase64, String databaseName) {
        this.searchOptionsService = searchOptionsService;
        this.iconTableBase64 = iconTableBase64;
        this.databaseName = databaseName;
    }

    public void requestLoadRoot(DataNodeLoaderListener dataNodeLoaderListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void requestLoadChildrenOf(DataNodeId dataNodeId, int first, int last, DataNodeLoaderListener dataNodeLoaderListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void requestLoad(List<DataNodeId> dataNodeIdList, final DataNodeLoaderListener dataNodeLoaderListener) {
        searchOptionsService.getDataNodes(databaseName, dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
            public void onFailure(Throwable caught) {
                dataNodeLoaderListener.dataNodeLoadFailed(caught);
            }

            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
            }
        });
    }

    public void requestLoadHdl(List<String> dataNodeHdlList, final DataNodeLoaderListener dataNodeLoaderListener) {
        searchOptionsService.getDataNodesByHdl(databaseName, dataNodeHdlList, new AsyncCallback<List<SerialisableDataNode>>() {
            public void onFailure(Throwable caught) {
                dataNodeLoaderListener.dataNodeLoadFailed(caught);
            }

            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
            }
        });
    }

    public void requestLoadUri(List<String> dataNodeUriList, final DataNodeLoaderListener dataNodeLoaderListener) {
        searchOptionsService.getDataNodesByUrl(databaseName, dataNodeUriList, new AsyncCallback<List<SerialisableDataNode>>() {
            public void onFailure(Throwable caught) {
                dataNodeLoaderListener.dataNodeLoadFailed(caught);
            }

            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
            }
        });
    }

    public String getNodeIcon(PluginDataNode yamsDataNode) {
        final NodeTypeImageBase64 typeIcon = iconTableBase64.getByType(yamsDataNode.getType());
        if (typeIcon != null) {
            return typeIcon.getInlineImageDataString();
        }
        return ""; // todo: we could return an error or loading icon here
    }

    public void performSearch(String databaseName, QueryDataStructures.CriterionJoinType criterionJoinType, List<SearchParameters> searchParametersList, DataNodeSearchListener dataNodeSearchListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
