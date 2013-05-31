/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;
import java.util.List;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.NodeTypeImageBase64;

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
    final private CheckBox checkBox;
    final private Label nodeLabel;
    final private Anchor nodeDetailsAnchor;
//    private Button expandButton;
    private SingleDataNodeTable singleDataNodeTable = null;
    private final IconTableBase64 iconTableBase64;
    private final Image iconImage = new Image();

    public YaasTreeItem(DataNodeId dataNodeId, SearchOptionsServiceAsync searchOptionsService, DataNodeTable dataNodeTable, IconTableBase64 iconTableBase64) {
        super(new HorizontalPanel());
        this.dataNodeTable = dataNodeTable;
        this.dataNodeId = dataNodeId;
        this.searchOptionsService = searchOptionsService;
        this.iconTableBase64 = iconTableBase64;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        setupWidgets();
        loadDataNode();

    }

    public YaasTreeItem(SerialisableDataNode yaasDataNode, SearchOptionsServiceAsync searchOptionsService, DataNodeTable dataNodeTable, IconTableBase64 iconTableBase64) {
        super(new HorizontalPanel());
        this.yaasDataNode = yaasDataNode;
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTable = dataNodeTable;
        this.iconTableBase64 = iconTableBase64;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        setupWidgets();
        setLabel();
        try {
            if (yaasDataNode.getChildList() != null || yaasDataNode.getChildIds() != null) {
                addItem(labelChildrenNotLoaded);
            }
        } catch (ModelException exception) {
            addItem(new Label("Error getting child nodes: " + exception.getMessage()));
        }
        setNodeIcon();
    }

    private void setNodeIcon() {
        final NodeTypeImageBase64 typeIcon = iconTableBase64.getByType(yaasDataNode.getType());
        if (typeIcon != null) {
            iconImage.setUrl(typeIcon.getInlineImageDataString());
        }
    }

    private void hideShowExpandButton() {
        final boolean hasFields = yaasDataNode != null && yaasDataNode.getFieldGroups() != null;
        nodeLabel.setVisible(!hasFields);
        nodeDetailsAnchor.setVisible(hasFields);
    }

    private void setupWidgets() {
        setStyleName("yaas-treeNode");
        outerPanel.add(iconImage);
        outerPanel.add(checkBox);
        outerPanel.add(nodeLabel);
        outerPanel.add(nodeDetailsAnchor);
//        expandButton = new Button(">");
//        outerPanel.add(expandButton);
        checkBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (checkBox.getValue()) {
                    dataNodeTable.addDataNode(yaasDataNode);
                } else {
                    dataNodeTable.removeDataNode(yaasDataNode);
                }
            }
        });
        nodeDetailsAnchor.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (singleDataNodeTable == null) {
                    singleDataNodeTable = new SingleDataNodeTable(yaasDataNode);
                    singleDataNodeTable.setStyleName("yaas-treeNodeDetails");
                    outerPanel.add(singleDataNodeTable);
//                                 expandButton.setText("<<");
                } else {
                    outerPanel.remove(singleDataNodeTable);
                    singleDataNodeTable = null;
                }
            }
        });
        hideShowExpandButton();
    }

    @Override
    public void setText(String text) {
        nodeLabel.setText(text);
        nodeDetailsAnchor.setText(text);
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
                    try {
                        if (yaasDataNode.getChildList() != null || yaasDataNode.getChildIds() != null) {
                            addItem(labelChildrenNotLoaded);
                        }
                    } catch (ModelException exception) {
                        setText("Failure: " + exception.getMessage());
                    }
                    setNodeIcon();
                    hideShowExpandButton();
                }
            });
        }
    }

    public void loadChildNodes() {
        if (yaasDataNode != null) {
            if (yaasDataNode.getChildList() != null) {
                removeItems();
                // add the meta child nodes
                for (SerialisableDataNode childDataNode : yaasDataNode.getChildList()) {
                    YaasTreeItem yaasTreeItem = new YaasTreeItem(childDataNode, searchOptionsService, dataNodeTable, iconTableBase64);
                    addItem(yaasTreeItem);;
                }
            } else {
                removeItems();
                addItem(new Label("loading..."));
                final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
                try {
                    for (DataNodeLink childId : yaasDataNode.getChildIds()) {
                        dataNodeIdList.add(new DataNodeId(childId.getIdString()));
                    }
                } catch (ModelException exception) {
                    addItem(new Label("Error getting child nodes: " + exception.getMessage()));
                }
                searchOptionsService.getDataNodes(dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
                    public void onFailure(Throwable caught) {
                        removeItems();
                        addItem(new Label("Loading child nodes failed: " + caught.getMessage()));
                    }

                    public void onSuccess(List<SerialisableDataNode> dataNodeList) {
//                        setText("Loaded " + dataNodeList.size() + " child nodes");
                        removeItems();
                        if (dataNodeList == null) {
                            addItem(new Label("no child nodes found"));
                        } else {
                            for (SerialisableDataNode childDataNode : dataNodeList) {
                                YaasTreeItem yaasTreeItem = new YaasTreeItem(childDataNode, searchOptionsService, dataNodeTable, iconTableBase64);
                                addItem(yaasTreeItem);
                            }
                        }
                    }
                });
            }
        } else {
//            addItem(labelChildrenNotLoaded);
        }
    }

    private void setLabel() {
        if (yaasDataNode != null) {
            int childCountsize = -1;
            try {
                if (yaasDataNode.getChildIds() != null) {
                    childCountsize = yaasDataNode.getChildIds().size();
                } else if (yaasDataNode.getChildList() != null) {
                    childCountsize = yaasDataNode.getChildList().size();
                }
                setText(yaasDataNode.getLabel() + "[" + childCountsize + "]");
            } catch (ModelException exception) {
                setText(yaasDataNode.getLabel() + "[" + exception.getMessage() + "]");
            }
        } else {
            setText("not loaded");
        }
    }

    public SerialisableDataNode getYaasDataNode() {
        return yaasDataNode;
    }
}
