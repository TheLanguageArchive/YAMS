/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;
import java.util.List;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeId;

/**
 * Created on : Feb 5, 2013, 1:24:35 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YaasTreeItem extends TreeItem {

    private SerialisableDataNode yaasDataNode = null;
    final private DataNodeId dataNodeId;
    final private SearchOptionsServiceAsync searchOptionsService;

    public YaasTreeItem(SerialisableDataNode yaasDataNode, DataNodeId dataNodeId, SearchOptionsServiceAsync searchOptionsService) {
        this.yaasDataNode = yaasDataNode;
        this.dataNodeId = dataNodeId;
        this.searchOptionsService = searchOptionsService;

        if (yaasDataNode == null) {
            loadDataNode();
        } else {
            setLabel();
        }
    }

    private void loadDataNode() {
        setText("loading...");
        final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
        dataNodeIdList.add(dataNodeId);
        searchOptionsService.getDataNodes(dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
            public void onFailure(Throwable caught) {
                setText("Failure: " + caught.getMessage());
            }

            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                setText("setting label");
                yaasDataNode = dataNodeList.get(0);
                setLabel();
//                setText("label set");
            }
        });
    }

    private void setLabel() {
        if (yaasDataNode != null) {
            setText(yaasDataNode.getLabel() + "[" + yaasDataNode.getChildIds().size() + "]");
        } else {
            setText(dataNodeId.getIdString());
        }
    }

    @Override
    public int getChildCount() {
        return yaasDataNode.getChildIds().size();
    }

    public SerialisableDataNode getYaasDataNode() {
        return yaasDataNode;
    }
}
