/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static final String ERROR_GETTING_CHILD_NODES = "Error getting child nodes";
    public static final String LOADING_CHILD_NODES_FAILED = "Loading child nodes failed";
    public static final String FAILURE = "Failure";
    private SerialisableDataNode yaasDataNode = null;
    private DataNodeId dataNodeId = null;
    final private SearchOptionsServiceAsync searchOptionsService;
    final private DataNodeTable dataNodeTable;
    private boolean loadAttempted = false;
    final HorizontalPanel outerPanel;
    final private CheckBox checkBox;
    final private Label nodeLabel;
    final private Anchor nodeDetailsAnchor;
    private SingleDataNodeTable singleDataNodeTable = null;
    private final IconTableBase64 iconTableBase64;
    private final Image iconImage = new Image();
    private final TreeItem loadingTreeItem;
    private final TreeItem errorTreeItem;
    private final TreeItem loadNextTreeItem;
    private static final Logger logger = Logger.getLogger("");
    private final String databaseName;
    private int loadedCount = 0;

    public YaasTreeItem(String databaseName, DataNodeId dataNodeId, SearchOptionsServiceAsync searchOptionsService, DataNodeTable dataNodeTable, IconTableBase64 iconTableBase64) {
        super(new HorizontalPanel());
        loadingTreeItem = getLoadingItem();
        errorTreeItem = new TreeItem();
        this.dataNodeTable = dataNodeTable;
        this.dataNodeId = dataNodeId;
        this.databaseName = databaseName;
        this.searchOptionsService = searchOptionsService;
        this.iconTableBase64 = iconTableBase64;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        loadNextTreeItem = getLoadNextTreeItem();
        setupWidgets();
        // todo: continue working on the json version of the data loader
//        loadDataNodeJson();
        loadDataNode();
    }

    public YaasTreeItem(String databaseName, SerialisableDataNode yaasDataNode, SearchOptionsServiceAsync searchOptionsService, DataNodeTable dataNodeTable, IconTableBase64 iconTableBase64) {
        super(new HorizontalPanel());
        loadingTreeItem = getLoadingItem();
        errorTreeItem = new TreeItem();
        this.yaasDataNode = yaasDataNode;
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTable = dataNodeTable;
        this.iconTableBase64 = iconTableBase64;
        this.databaseName = databaseName;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        loadNextTreeItem = getLoadNextTreeItem();
        setupWidgets();
        setLabel();
        try {
            if (yaasDataNode.getChildList() != null || yaasDataNode.getChildIds() != null) {
                addItem(loadingTreeItem);
            }
        } catch (ModelException exception) {
            errorTreeItem.setText(ERROR_GETTING_CHILD_NODES);
            addItem(errorTreeItem);
            logger.log(Level.SEVERE, ERROR_GETTING_CHILD_NODES, exception);
        }
        setNodeIcon();
    }

    private TreeItem getLoadNextTreeItem() {
        final Button loadNextButton = new Button("Load More");
        loadNextButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                loadChildNodes();
            }
        });
        return new TreeItem(loadNextButton);
    }

    private TreeItem getLoadingItem() {
        HorizontalPanel loadingItem = new HorizontalPanel();
        loadingItem.add(new Image("./loader.gif"));
        loadingItem.add(new Label("loading..."));
        return new TreeItem(loadingItem);

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
        checkBox.setVisible(hasFields);
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
                    singleDataNodeTable = new SingleDataNodeTable(yaasDataNode, this);
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

    private void loadDataNodeJson() {
        // todo: continue working on the json version of the data loader
        // todo: create a json datanode for use here
        // The RequestBuilder code is replaced by a call to the getJson method. So you no longer need the following code in the refreshWatchList method: http://stackoverflow.com/questions/11121374/gwt-requestbuilder-cross-site-requests
        // also the current configuration probalby needs on the server: Response.setHeader("Access-Control-Allow-Origin","http://192.168.56.101:8080/BaseX76/rest/");
        final String requestUrl = "http://192.168.56.101:8080/BaseX76/rest/yaas-data/" + dataNodeId.getIdString() + "?method=jsonml";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(requestUrl));
        try {
            Request request = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    setText(FAILURE);
                    logger.log(Level.SEVERE, FAILURE, exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        setText(response.getText());
                    } else {
                        // if the document does not exist this error will occur
                        setText(FAILURE);
                        logger.log(Level.SEVERE, FAILURE, new Throwable(response.getStatusText() + response.getStatusCode() + " " + " " + response.getText() + " " + requestUrl));
                    }
                }
            });
        } catch (RequestException exception) {
            setText(FAILURE);
            logger.log(Level.SEVERE, FAILURE, exception);
        }
    }

    private void loadDataNode() {
        if (loadAttempted == false) {
            loadAttempted = true;
            setText("loading...");
            final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
            dataNodeIdList.add(dataNodeId);
            searchOptionsService.getDataNodes(databaseName, dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
                public void onFailure(Throwable caught) {
                    setText(FAILURE);
                    logger.log(Level.SEVERE, FAILURE, caught);
                }

                public void onSuccess(List<SerialisableDataNode> dataNodeList) {
                    yaasDataNode = dataNodeList.get(0);
                    setLabel();
                    try {
                        if (yaasDataNode.getChildList() != null || yaasDataNode.getChildIds() != null) {
                            addItem(loadingTreeItem);
                        }
                    } catch (ModelException exception) {
                        setText(FAILURE);
                        logger.log(Level.SEVERE, FAILURE, exception);
                    }
                    setNodeIcon();
                    hideShowExpandButton();
                }
            });
        }
    }

    public void loadChildNodes() {
        removeItem(loadNextTreeItem);
        removeItem(errorTreeItem);
        if (yaasDataNode != null) {
            if (yaasDataNode.getChildList() != null) {
                removeItem(loadingTreeItem);
                // add the meta child nodes
                for (SerialisableDataNode childDataNode : yaasDataNode.getChildList()) {
                    YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, childDataNode, searchOptionsService, dataNodeTable, iconTableBase64);
                    addItem(yaasTreeItem);;
                }
            } else {
                addItem(loadingTreeItem);
                try {
                    final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
                    final int maxToGet = yaasDataNode.getChildIds().size();
                    if (maxToGet <= loadedCount) {
                        // all child nodes should be visible so we can just return
                        removeItem(loadingTreeItem);
                        return;
                    }
                    final int numberToGet = 20;
                    final int firstToGet = (loadedCount == 0) ? loadedCount : loadedCount + 1;
                    final int lastToGet = (maxToGet < firstToGet + numberToGet) ? maxToGet : firstToGet + numberToGet;
//                    logger.log(Level.INFO, "loadedCount: " + loadedCount + ", numberToGet: " + numberToGet + ", firstToGet: " + firstToGet + ", lastToGet: " + lastToGet + ", maxToGet: " + maxToGet);
                    for (DataNodeLink childId : yaasDataNode.getChildIds().subList(firstToGet, lastToGet)) {
                        dataNodeIdList.add(new DataNodeId(childId.getIdString()));
                    }
                    searchOptionsService.getDataNodes(databaseName, dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
                        public void onFailure(Throwable exception) {
                            removeItem(loadingTreeItem);
                            errorTreeItem.setText(LOADING_CHILD_NODES_FAILED);
                            addItem(errorTreeItem);
                            logger.log(Level.SEVERE, LOADING_CHILD_NODES_FAILED, exception);
                        }

                        public void onSuccess(List<SerialisableDataNode> dataNodeList) {
//                        setText("Loaded " + dataNodeList.size() + " child nodes");
                            removeItem(loadingTreeItem);
                            if (dataNodeList != null) {
                                for (SerialisableDataNode childDataNode : dataNodeList) {
                                    YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, childDataNode, searchOptionsService, dataNodeTable, iconTableBase64);
                                    addItem(yaasTreeItem);
                                    loadedCount++;
                                }
                            }
                            while (lastToGet > loadedCount) {
                                // when nodes are missing these "not found" nodes are added to keep the paging of the child node array in sync
                                addItem(new Label("node not found"));
                                loadedCount++;
                            }
                            if (loadedCount < maxToGet) {
                                addItem(loadNextTreeItem);
                            }
                        }
                    });
                } catch (ModelException exception) {
                    removeItem(loadingTreeItem);
                    errorTreeItem.setText(ERROR_GETTING_CHILD_NODES);
                    addItem(errorTreeItem);
                    logger.log(Level.SEVERE, ERROR_GETTING_CHILD_NODES, exception);
                }
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
