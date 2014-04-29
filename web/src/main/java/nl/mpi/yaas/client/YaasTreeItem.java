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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import static nl.mpi.flap.model.DataNodeType.IMDI_RESOURCE;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeHighlight;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.HighlighableDataNode;

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
    final private DataNodeLoader dataNodeLoader;
    private boolean loadAttempted = false;
    final HorizontalPanel outerPanel;
    final private CheckBox checkBox;
    final private Label nodeLabel;
    final private Anchor nodeDetailsAnchor;
    private final VerticalPanel verticalPanel = new VerticalPanel();
    private final Image iconImage = new Image();
    private final TreeItem loadingTreeItem;
    private final TreeItem errorTreeItem;
    private final TreeItem loadNextTreeItem;
    private static final Logger logger = Logger.getLogger("");
    private final String databaseName;
    private int loadedCount = 0;
    private final List<DataNodeHighlight> highlighedLinks = new ArrayList<DataNodeHighlight>();
    private final TreeTableHeader treeTableHeader;
    private final PopupPanel popupPanel;
    private final TreeNodeCheckboxListener checkboxListener;
    private final TreeNodeClickListener clickListener;
    private final boolean displayFlatNodes;

    public YaasTreeItem(String databaseName, DataNodeId dataNodeId, DataNodeLoader dataNodeLoader, TreeTableHeader treeTableHeader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener, boolean displayFlatNodes, final YaasTreeItemLoadedListener itemLoadedListener) {
        super(new HorizontalPanel());
        loadingTreeItem = getLoadingItem();
        errorTreeItem = new TreeItem();
        this.dataNodeId = dataNodeId;
        this.databaseName = databaseName;
        this.dataNodeLoader = dataNodeLoader;
        this.treeTableHeader = treeTableHeader;
        this.popupPanel = popupPanel;
        this.checkboxListener = checkboxListener;
        this.clickListener = clickListener;
        this.displayFlatNodes = displayFlatNodes;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        loadNextTreeItem = getLoadNextTreeItem();
        setupWidgets();
        // todo: continue working on the json version of the data loader
//        loadDataNodeJson();
        loadDataNode(itemLoadedListener);
    }

    public YaasTreeItem(String databaseName, SerialisableDataNode yaasDataNode, DataNodeLoader dataNodeLoader, TreeTableHeader treeTableHeader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener, boolean displayFlatNodes) {
        super(new HorizontalPanel());
        loadingTreeItem = getLoadingItem();
        errorTreeItem = new TreeItem();
        this.yaasDataNode = yaasDataNode;
        this.dataNodeLoader = dataNodeLoader;
        this.treeTableHeader = treeTableHeader;
        this.databaseName = databaseName;
        this.popupPanel = popupPanel;
        this.checkboxListener = checkboxListener;
        this.clickListener = clickListener;
        this.displayFlatNodes = displayFlatNodes;
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        loadNextTreeItem = getLoadNextTreeItem();
        setupWidgets();
        setLabel();
        try {
            if (yaasDataNode.getType() == null || !IMDI_RESOURCE.equals(yaasDataNode.getType().getID())) { // do not show child links of imdi resource nodes
                if (getFilteredChildNodes() != null || !getFlatChildIds(yaasDataNode, new ArrayList<DataNodeLink>()).isEmpty()) {
                    addItem(loadingTreeItem);
                }
            }
        } catch (ModelException exception) {
            errorTreeItem.setText(ERROR_GETTING_CHILD_NODES);
            addItem(errorTreeItem);
            logger.log(Level.SEVERE, ERROR_GETTING_CHILD_NODES, exception);
        }
        setNodeIcon();
        // todo: add a click handler so that clicking anywhere will open the branch
    }

    private TreeItem getLoadNextTreeItem() {
        final Button loadNextButton = new Button("Load More");
        loadNextButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                loadChildNodes();
            }
        });
        loadNextButton.setStyleName("load-more-btn");
        return new TreeItem(loadNextButton);
    }

    private TreeItem getLoadingItem() {
        HorizontalPanel loadingItem = new HorizontalPanel();
        loadingItem.add(new Image("./loader.gif"));
        loadingItem.add(new Label("loading..."));
        return new TreeItem(loadingItem);

    }

    private void addColumnForHighlight(HorizontalPanel horizontalPanel, SerialisableDataNode dataNode, DataNodeHighlight highlight) {
        if (dataNode != null && treeTableHeader != null) {
            logger.info(dataNode.getLabel());
            final List<FieldGroup> fieldGroups = dataNode.getFieldGroups();
            if (fieldGroups != null) {
                for (FieldGroup fieldGroup : fieldGroups) {
                    for (DataField dataField : fieldGroup.getFields()) {
                        if (highlight.getHighlightPath().equals(dataField.getPath())) {
                            logger.info(dataField.getFieldValue());
                            final HTML label = new HTML(dataField.getFieldValue());
                            horizontalPanel.add(label);
                            final Style style = label.getElement().getStyle();
                            style.setLeft(treeTableHeader.getLeftForColumn(fieldGroup.getFieldName()), Style.Unit.PX);//"position:relative;left:110px;width:200px;"
                            style.setPosition(Style.Position.ABSOLUTE);
//                    label.setStyleName("yaas-treeNode-result-" + dataField.getPath());
                        }
                    }
                }
            }
            final List<? extends SerialisableDataNode> childList = dataNode.getChildList();
            if (childList != null) {
                for (SerialisableDataNode childDataNode : childList) {
                    addColumnForHighlight(horizontalPanel, childDataNode, highlight);
//                    if (dataNode != null) {
                }
            }
        }
    }

    private void addColumnsForHighlights() {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        verticalPanel.add(horizontalPanel);
        if (yaasDataNode != null) {
            for (DataNodeHighlight highlight : highlighedLinks) {
                addColumnForHighlight(horizontalPanel, yaasDataNode, highlight);
            }
        }
    }

    public void setHighlights(HighlighableDataNode dataNode) {
        boolean isHighlighted = false;
        boolean childHighlighted = false;
//        root nodes of a search result always need to be highlighted    
        for (DataNodeHighlight highlight : dataNode.getHighlights()) {
            if (highlight.getDataNodeId().equals(dataNodeId.getIdString())) {
                this.highlighedLinks.add(highlight);
                if (highlight.getHighlightPath().contains("(")) {
                    childHighlighted = true;
                } else {
                    isHighlighted = true;
                    break; // no need to look further
                };
            }
        }
        if (isHighlighted) {
            nodeLabel.setStyleName("yaas-treeNode-highlighted");
            nodeDetailsAnchor.setStyleName("yaas-treeNode-highlighted");
        } else if (childHighlighted) {
            nodeLabel.setStyleName("yaas-childNode-highlighted");
            nodeDetailsAnchor.setStyleName("yaas-childNode-highlighted");
        } else {
            nodeLabel.setStyleName("yaas-treeNode");
            nodeDetailsAnchor.setStyleName("yaas-treeNode");
        }
        addColumnsForHighlights();
    }

    public void setHighlights(List<DataNodeHighlight> highlighedLinks) {
        boolean isHighlighted = false;
        boolean childHighlighted = false;
        if (yaasDataNode == null) {
            logger.warning("Data node is not loaded when applying tree highlights.");
        } else {
            try {
                final String uri = yaasDataNode.getURI();
                if (uri != null) {
                    final String[] uriParts = uri.split("#");
                    if (uriParts != null && uriParts.length >= 2) {
                        final String fragment = uriParts[1];
                        for (DataNodeHighlight highlight : highlighedLinks) {
                            final String highlightPath = highlight.getHighlightPath();
                            if (highlightPath.startsWith(fragment)) {
                                this.highlighedLinks.add(highlight);
                                final String remainder = highlightPath.substring(fragment.length());
                                if (remainder.contains("(")) {
                                    childHighlighted = true;
                                } else {
                                    isHighlighted = true;
                                    break; // no need to look further
                                };
                            }
                        }
                    }
                }
            } catch (ModelException exception) {
                // nothing to do here if there is no URI
                logger.warning(exception.getMessage());
            }
        }
        if (isHighlighted) {
            nodeLabel.setStyleName("yaas-treeNode-highlighted");
            nodeDetailsAnchor.setStyleName("yaas-treeNode-highlighted");
        } else if (childHighlighted) {
            nodeLabel.setStyleName("yaas-childNode-highlighted");
            nodeDetailsAnchor.setStyleName("yaas-childNode-highlighted");
        } else {
            nodeLabel.setStyleName("yaas-treeNode");
            nodeDetailsAnchor.setStyleName("yaas-treeNode");
        }
    }

    private void setNodeIcon() {
        // todo: replace this with CSS and modify the crawler to make space separated strings that select multiply CSS classes for each icon
        iconImage.setUrl(dataNodeLoader.getNodeIcon(yaasDataNode));
    }

    private void hideShowExpandButton() {
        final boolean hasFields = yaasDataNode != null && yaasDataNode.getFieldGroups() != null;
        nodeLabel.setVisible(!hasFields);
        if (checkboxListener != null) {
            checkBox.setVisible(hasFields);
        }
        nodeDetailsAnchor.setVisible(hasFields);
    }

    private void setupWidgets() {
        setStyleName("yaas-treeNode");
        final Style style = outerPanel.getElement().getStyle();
        style.setLeft(0, Style.Unit.PX);
        style.setPosition(Style.Position.RELATIVE);
        outerPanel.add(iconImage);
        if (checkboxListener != null) {
            outerPanel.add(checkBox);
        }
        outerPanel.add(nodeLabel);
        outerPanel.add(nodeDetailsAnchor);
//        expandButton = new Button(">");
        outerPanel.add(verticalPanel);
//        outerPanel.add(expandButton);
        if (checkboxListener != null) {
            checkBox.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    checkboxListener.stateChanged(checkBox.getValue(), yaasDataNode, checkBox);
                }
            });
        }
//        nodeDetailsAnchor.addMouseOutHandler(new MouseOutHandler() {
//
//            public void onMouseOut(MouseOutEvent event) {
//                if (singleDataNodeTable != null) {
////                    if (!event.getRelatedTarget().equals(singleDataNodeTable.)) {
//                        verticalPanel.remove(singleDataNodeTable);
//                        singleDataNodeTable = null;
////                    }
//                }
//            }
//        });
        if (clickListener != null) {
            nodeDetailsAnchor.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    clickListener.clickEvent(yaasDataNode);
                }
            });
        }
        if (popupPanel != null) {
            nodeDetailsAnchor.addMouseOverHandler(new MouseOverHandler() {

                public void onMouseOver(MouseOverEvent event) {
//                if (singleDataNodeTable == null) {
//                    singleDataNodeTable = ;
//                    singleDataNodeTable.setStyleName("yaas-treeNodeDetails");                    
                    popupPanel.setWidget(new SingleDataNodeTable(yaasDataNode, highlighedLinks));
                    popupPanel.setPopupPosition(nodeDetailsAnchor.getAbsoluteLeft() + nodeDetailsAnchor.getOffsetWidth() / 2, nodeDetailsAnchor.getAbsoluteTop() + nodeDetailsAnchor.getOffsetHeight());
                    popupPanel.show();
//                                 expandButton.setText("<<");
//                }
                }
            });
        }
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

    private void loadDataNode(final YaasTreeItemLoadedListener itemLoadedListener) {
        if (loadAttempted == false) {
            loadAttempted = true;
            setText("loading...");
            addItem(loadingTreeItem);
            final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
            dataNodeIdList.add(dataNodeId);
            dataNodeLoader.requestLoad(dataNodeIdList, new DataNodeLoaderListener() {

                public void dataNodeLoaded(List<SerialisableDataNode> dataNodeList) {
                    yaasDataNode = dataNodeList.get(0);
                    setLabel();
                    removeItem(loadingTreeItem);
                    try {
                        if (getFilteredChildNodes() != null || !getFlatChildIds(yaasDataNode, new ArrayList<DataNodeLink>()).isEmpty()) {
                            addItem(loadingTreeItem);
                        }
                    } catch (ModelException exception) {
                        setText(FAILURE);
                        logger.log(Level.SEVERE, FAILURE, exception);
                    }
                    setNodeIcon();
                    hideShowExpandButton();
                    addColumnsForHighlights();
                    if (itemLoadedListener != null) {
                        itemLoadedListener.yaasTreeItemLoaded(YaasTreeItem.this);
                    }
                }

                public void dataNodeLoadFailed(Throwable caught) {
                    setText(FAILURE);
                    removeItem(loadingTreeItem);
                    logger.log(Level.SEVERE, FAILURE, caught);
                }
            });
        }
    }

    public void loadChildNodes() {
        removeItem(loadNextTreeItem);
        removeItem(errorTreeItem);
        if (yaasDataNode != null) {
            final List<? extends SerialisableDataNode> childList = getFilteredChildNodes();
            if (childList != null) {
                removeItem(loadingTreeItem);
                if (childList.size() > loadedCount) // add the meta child nodes
                {
                    for (SerialisableDataNode childDataNode : childList) {
                        YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, childDataNode, dataNodeLoader, treeTableHeader, popupPanel, checkboxListener, clickListener, displayFlatNodes);
                        yaasTreeItem.setHighlights(highlighedLinks);
                        addItem(yaasTreeItem);
                        loadedCount++;
                    }
                }
            } else {
                addItem(loadingTreeItem);
                try {
                    final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
                    final List<DataNodeLink> flatChildIds = getFlatChildIds(yaasDataNode, new ArrayList<DataNodeLink>());
                    final int maxToGet = flatChildIds.size();
                    if (maxToGet <= loadedCount) {
                        // all child nodes should be visible so we can just return
                        removeItem(loadingTreeItem);
                        return;
                    }
                    final int numberToGet = 20;
                    final int firstToGet = (loadedCount == 0) ? loadedCount : loadedCount + 1;
                    final int lastToGet = (maxToGet < firstToGet + numberToGet) ? maxToGet : firstToGet + numberToGet;
//                    logger.log(Level.INFO, "loadedCount: " + loadedCount + ", numberToGet: " + numberToGet + ", firstToGet: " + firstToGet + ", lastToGet: " + lastToGet + ", maxToGet: " + maxToGet);
                    for (DataNodeLink childId : flatChildIds.subList(firstToGet, lastToGet)) {
                        final String nodeUriLowerCase = childId.getNodeUriString().toLowerCase();
                        if (nodeUriLowerCase.endsWith(".cmdi") || nodeUriLowerCase.endsWith(".imdi")) {
                            dataNodeIdList.add(new DataNodeId(childId.getIdString()));
                        } else {
                            // todo: this will be replaced with CS2 and or YAMS crawler including data files in the database.
                            final SerialisableDataNode resourceDataNode = new SerialisableDataNode();
                            resourceDataNode.setLabel(childId.getNodeUriString().substring(nodeUriLowerCase.lastIndexOf("/") + 1));
                            resourceDataNode.setURI(childId.getNodeUriString());
                            resourceDataNode.setType(new DataNodeType("resource", nodeUriLowerCase.substring(nodeUriLowerCase.lastIndexOf(".") + 1), null));
                            final ArrayList<DataField> urlFields = new ArrayList<DataField>();
                            final DataField urlField = new DataField();
                            urlField.setFieldValue(childId.getNodeUriString());
                            urlFields.add(urlField);
                            final ArrayList<FieldGroup> fieldGroup = new ArrayList<FieldGroup>();
                            fieldGroup.add(new FieldGroup("URL", urlFields));
                            resourceDataNode.setFieldGroups(fieldGroup);
                            YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, resourceDataNode, dataNodeLoader, treeTableHeader, popupPanel, checkboxListener, clickListener, displayFlatNodes);
                            addItem(yaasTreeItem);
                            loadedCount++;
                        }
                    }
                    if (dataNodeIdList.isEmpty()) {
                        removeItem(loadingTreeItem);
                        if (loadedCount < maxToGet - 1) {
                            addItem(loadNextTreeItem);
                        }
                    } else {
                        dataNodeLoader.requestLoad(dataNodeIdList, new DataNodeLoaderListener() {

                            public void dataNodeLoaded(List<SerialisableDataNode> dataNodeList) {
//                        setText("Loaded " + dataNodeList.size() + " child nodes");
                                removeItem(loadingTreeItem);
                                if (dataNodeList != null) {
                                    for (SerialisableDataNode childDataNode : dataNodeList) {
                                        YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, childDataNode, dataNodeLoader, treeTableHeader, popupPanel, checkboxListener, clickListener, displayFlatNodes);
                                        yaasTreeItem.setHighlights(highlighedLinks);
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

                            public void dataNodeLoadFailed(Throwable caught) {
                                removeItem(loadingTreeItem);
                                errorTreeItem.setText(LOADING_CHILD_NODES_FAILED);
                                addItem(errorTreeItem);
                                logger.log(Level.SEVERE, LOADING_CHILD_NODES_FAILED, caught);
                            }
                        });
                    }
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
                final List<DataNodeLink> flatChildIds = getFlatChildIds(yaasDataNode, new ArrayList<DataNodeLink>());
                if (!flatChildIds.isEmpty()) {
                    childCountsize = flatChildIds.size();
                } else if (yaasDataNode.getChildList() != null) {
                    // get the reduced children here
                    final List<? extends SerialisableDataNode> filteredChildNodes = getFilteredChildNodes();
                    if (filteredChildNodes != null) {
                        childCountsize = filteredChildNodes.size();
                    } else {
                        childCountsize = 0;
                    }
                }
                if (childCountsize > 0) {
                    setText(yaasDataNode.getLabel() + "[" + childCountsize + "]");
                } else {
                    setText(yaasDataNode.getLabel());
                }
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

    private List<? extends SerialisableDataNode> getFilteredChildNodes() {
        final List<? extends SerialisableDataNode> childList = yaasDataNode.getChildList();
        if (childList == null) {
            return null;
        }
        if (displayFlatNodes) {
            final ArrayList flatNodes = new ArrayList<SerialisableDataNode>();
            getFlatNodes(yaasDataNode, flatNodes);
            if (flatNodes.isEmpty()) {
//                // if the list is empty then return null
                return null;
            } else {
                return flatNodes;
            }
        } else {
            return yaasDataNode.getChildList();
        }
    }

    private List<? extends SerialisableDataNode> getFlatNodes(SerialisableDataNode currentDataNode, List<SerialisableDataNode> flatNodes) {
        // this filtering should only be relevant to IMDI nodes because CMDI nodes will have all resouces as links
        final List<? extends SerialisableDataNode> childList = currentDataNode.getChildList();
        if (childList != null) // this filtering should only be relevant to IMDI nodes because CMDI nodes will have all resouces as links
        {
            for (SerialisableDataNode childDataNode : childList) {
                final DataNodeType nodeType = childDataNode.getType();
                try {
                    // todo: this check for # might be reduntant and was added for the cmdi resources only
                    if ((nodeType != null && IMDI_RESOURCE.equals(nodeType.getID())) || !childDataNode.getURI().contains("#")) {
//                if (childDataNode.getArchiveHandle() != null) // archive handle is not the best thing to detect resource nodes
                        flatNodes.add(childDataNode);
                    }
                } catch (ModelException exception) {
                    logger.warning(exception.getMessage());
                }
                getFlatNodes(childDataNode, flatNodes);
            }
        }
        return flatNodes;
    }

    private List<DataNodeLink> getFlatChildIds(SerialisableDataNode currentDataNode, List<DataNodeLink> dataNodeLinks) throws ModelException {
        final List<DataNodeLink> childIds = currentDataNode.getChildIds();
        if (childIds != null) {
            dataNodeLinks.addAll(childIds);
        }
        if (displayFlatNodes) {
            final List<? extends SerialisableDataNode> childList = currentDataNode.getChildList();
            if (childList != null) // this filtering should only be relevant to IMDI nodes because CMDI nodes will have all resouces as links
            {
                for (SerialisableDataNode childDataNode : childList) {
                    getFlatChildIds(childDataNode, dataNodeLinks);
                }
            }
        }
        return dataNodeLinks;
    }
}
