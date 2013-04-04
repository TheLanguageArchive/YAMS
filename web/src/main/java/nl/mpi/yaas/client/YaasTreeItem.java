/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
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
    private DataNodeId dataNodeId = null;
    final private SearchOptionsServiceAsync searchOptionsService;
    private boolean loadAttempted = false;
    final Label labelChildrenNotLoaded = new Label("child nodes not loaded");

    public YaasTreeItem(DataNodeId dataNodeId, SearchOptionsServiceAsync searchOptionsService) {
        this.dataNodeId = dataNodeId;
        this.searchOptionsService = searchOptionsService;
        loadDataNode();
    }

    public YaasTreeItem(SerialisableDataNode yaasDataNode, SearchOptionsServiceAsync searchOptionsService) {
        this.yaasDataNode = yaasDataNode;
        this.searchOptionsService = searchOptionsService;
        setLabel();
    }

    private void loadDataNode() {
        if (loadAttempted == false) {
            loadAttempted = true;
            setText("loading...");
            final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
            dataNodeIdList.add(dataNodeId);
            searchOptionsService.getDataNodes(dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
                public void onFailure(Throwable caught) {
                    setText("Failure: " + caught.getMessage());
                }

                public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                    yaasDataNode = dataNodeList.get(0);
                    setLabel();
                    addItem(labelChildrenNotLoaded);
                }
            });
        }
    }

    public void loadChildNodes() {
        final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
        for (String childId : yaasDataNode.getChildIds()) {
            dataNodeIdList.add(new DataNodeId(childId));
        }
        searchOptionsService.getDataNodes(dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
            public void onFailure(Throwable caught) {
                remove();
                setText("Loading child nodes failed: " + caught.getMessage());
            }

            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                remove();
                for (SerialisableDataNode childDataNode : dataNodeList) {
                    YaasTreeItem yaasTreeItem = new YaasTreeItem(childDataNode, searchOptionsService);
                    addItem(yaasTreeItem);;
                }
            }
        });
    }

    private void setLabel() {
        if (yaasDataNode != null) {
            setText(yaasDataNode.getLabel() + "[" + yaasDataNode.getChildIds().size() + "]");
        } else {
            setText("not loaded");
        }
    }

    public SerialisableDataNode getYaasDataNode() {
        return yaasDataNode;
    }
}
