/*
 * Copyright (C) 2014 The Language Archive, Max Planck Institute for Psycholinguistics
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
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.NodeTypeImageBase64;

/**
 * @since Mar 26, 2014 1:28:03 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeLoader {

    final private SearchOptionsServiceAsync searchOptionsService;
    private final IconTableBase64 iconTableBase64;
    private final String databaseName;

    public DataNodeLoader(SearchOptionsServiceAsync searchOptionsService, IconTableBase64 iconTableBase64, String databaseName) {
        this.searchOptionsService = searchOptionsService;
        this.iconTableBase64 = iconTableBase64;
        this.databaseName = databaseName;
    }

    protected void requestLoad(List<DataNodeId> dataNodeIdList, final DataNodeLoaderListener dataNodeLoaderListener) {
        searchOptionsService.getDataNodes(databaseName, dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
            public void onFailure(Throwable caught) {
                dataNodeLoaderListener.dataNodeLoadFailed(caught);
            }

            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
            }
        });
    }

    protected String getNodeIcon(SerialisableDataNode yaasDataNode) {
        final NodeTypeImageBase64 typeIcon = iconTableBase64.getByType(yaasDataNode.getType());
        if (typeIcon != null) {
            return typeIcon.getInlineImageDataString();
        }
        return ""; // todo: we could return an error or loading icon here
    }
}