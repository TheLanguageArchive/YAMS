/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
    final private DataNodeTable dataNodeTable;
    private boolean loadAttempted = false;
    final Label labelChildrenNotLoaded = new Label("child nodes not loaded");
    final HorizontalPanel outerPanel;
    final CheckBox checkBox;
    private SingleDataNodeTable singleDataNodeTable = null;

    public YaasTreeItem(DataNodeId dataNodeId, SearchOptionsServiceAsync searchOptionsService, DataNodeTable dataNodeTable) {
        super(new HorizontalPanel());
        this.dataNodeTable = dataNodeTable;
        this.dataNodeId = dataNodeId;
        this.searchOptionsService = searchOptionsService;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        setupWidgets();
        loadDataNode();

    }

    public YaasTreeItem(SerialisableDataNode yaasDataNode, SearchOptionsServiceAsync searchOptionsService, DataNodeTable dataNodeTable) {
        super(new HorizontalPanel());
        this.yaasDataNode = yaasDataNode;
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTable = dataNodeTable;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        setupWidgets();
        setLabel();
        addItem(labelChildrenNotLoaded);
    }

    private void setupWidgets() {
        outerPanel.add(checkBox);
        final Button expandButton = new Button(">");
        outerPanel.add(expandButton);
        checkBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (checkBox.getValue()) {
                    dataNodeTable.addDataNode(yaasDataNode);
                } else {
                    dataNodeTable.removeDataNode(yaasDataNode);
                }
            }
        });
        expandButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (singleDataNodeTable == null) {
                    singleDataNodeTable = new SingleDataNodeTable(yaasDataNode);
                    outerPanel.add(singleDataNodeTable);
//                                 expandButton.setText("<<");
                } else {
                    outerPanel.remove(singleDataNodeTable);
                    singleDataNodeTable = null;
                }
            }
        });
    }

    @Override
    public void setText(String text) {
        checkBox.setText(text);
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
        if (yaasDataNode != null) {
            if (yaasDataNode.getChildList() != null) {
                // add the meta child nodes
                for (SerialisableDataNode childDataNode : yaasDataNode.getChildList()) {
                    YaasTreeItem yaasTreeItem = new YaasTreeItem(childDataNode, searchOptionsService, dataNodeTable);
                    addItem(yaasTreeItem);;
                }
            } else {

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
//                        setText("Loaded " + dataNodeList.size() + " child nodes");
                        removeItems();
                        if (dataNodeList == null) {
                            addItem(new Label("child nodes failed to load"));
                        } else {
                            for (SerialisableDataNode childDataNode : dataNodeList) {
                                YaasTreeItem yaasTreeItem = new YaasTreeItem(childDataNode, searchOptionsService, dataNodeTable);
                                addItem(yaasTreeItem);
                            }
                        }
                    }
                });
            }
        } else {
            addItem(labelChildrenNotLoaded);
        }
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
