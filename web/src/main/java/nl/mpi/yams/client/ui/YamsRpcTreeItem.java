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
package nl.mpi.yams.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import static nl.mpi.flap.model.DataNodeType.IMDI_RESOURCE;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.client.DataNodeLoader;
import nl.mpi.yams.client.DataNodeLoaderListener;
import nl.mpi.yams.client.TreeNodeCheckboxListener;
import nl.mpi.yams.client.TreeNodeClickListener;
import nl.mpi.yams.client.TreeTableHeader;
import nl.mpi.yams.client.YamsTreeItemLoadedListener;
import static nl.mpi.yams.client.ui.YamsTreeItem.logger;
import nl.mpi.yams.common.data.DataNodeHighlight;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.HighlightableDataNode;

/**
 * @since May 23, 2014 10:22:08 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YamsRpcTreeItem extends YamsTreeItem {

    private DataNodeId dataNodeId = null;
    private final String databaseName;
    private final List<DataNodeHighlight> highlighedLinks = new ArrayList<DataNodeHighlight>();
    private final TreeTableHeader treeTableHeader;
    private final boolean displayFlatNodes;

    public YamsRpcTreeItem(String databaseName, DataNodeId dataNodeId, DataNodeLoader dataNodeLoader, TreeTableHeader treeTableHeader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener, boolean displayFlatNodes, final YamsTreeItemLoadedListener itemLoadedListener) {
        super(dataNodeLoader, popupPanel, checkboxListener, clickListener);
        this.dataNodeId = dataNodeId;
        this.databaseName = databaseName;
        this.treeTableHeader = treeTableHeader;
        this.displayFlatNodes = displayFlatNodes;
        // todo: continue working on the json version of the data loader
//        loadDataNodeJson();
        loadDataNode(itemLoadedListener);
    }

    public YamsRpcTreeItem(String databaseName, SerialisableDataNode yamsDataNode, DataNodeLoader dataNodeLoader, TreeTableHeader treeTableHeader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener, boolean displayFlatNodes) {
        super(dataNodeLoader, popupPanel, checkboxListener, clickListener);
        this.yamsDataNode = yamsDataNode;
        this.treeTableHeader = treeTableHeader;
        this.databaseName = databaseName;
        this.displayFlatNodes = displayFlatNodes;
        setLabel();
        try {
            if (yamsDataNode.getType() == null || !IMDI_RESOURCE.equals(yamsDataNode.getType().getID())) { // do not show child links of imdi resource nodes
                if (getFilteredChildNodes() != null || !getFlatChildIds(yamsDataNode, new ArrayList<DataNodeLink>()).isEmpty()) {
                    addItem(loadingTreeItem);
                }
            }
        } catch (ModelException exception) {
            errorTreeItem.setText(ERROR_GETTING_CHILD_NODES);
            addItem(errorTreeItem);
            logger.log(Level.SEVERE, ERROR_GETTING_CHILD_NODES, exception);
        }
        setNodeIconData(dataNodeLoader.getNodeIcon(yamsDataNode));
        // todo: add a click handler so that clicking anywhere will open the branch
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
//                    label.setStyleName("yams-treeNode-result-" + dataField.getPath());
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
        if (yamsDataNode != null) {
            for (DataNodeHighlight highlight : highlighedLinks) {
                addColumnForHighlight(horizontalPanel, yamsDataNode, highlight);
            }
        }
    }

    protected Widget getPopupWidget() {
        return new SingleDataNodeTable(yamsDataNode, highlighedLinks);
    }

    public void setHighlights(HighlightableDataNode dataNode) {
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
            nodeLabel.setStyleName("yams-treeNode-highlighted");
            nodeDetailsAnchor.setStyleName("yams-treeNode-highlighted");
        } else if (childHighlighted) {
            nodeLabel.setStyleName("yams-childNode-highlighted");
            nodeDetailsAnchor.setStyleName("yams-childNode-highlighted");
        } else {
            nodeLabel.setStyleName("yams-treeNode");
            nodeDetailsAnchor.setStyleName("yams-treeNode");
        }
        addColumnsForHighlights();
    }

    public void setHighlights(List<DataNodeHighlight> highlighedLinks) {
        boolean isHighlighted = false;
        boolean childHighlighted = false;
        if (yamsDataNode == null) {
            logger.warning("Data node is not loaded when applying tree highlights.");
        } else {
            try {
                final String uri = yamsDataNode.getURI();
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
            nodeLabel.setStyleName("yams-treeNode-highlighted");
            nodeDetailsAnchor.setStyleName("yams-treeNode-highlighted");
        } else if (childHighlighted) {
            nodeLabel.setStyleName("yams-childNode-highlighted");
            nodeDetailsAnchor.setStyleName("yams-childNode-highlighted");
        } else {
            nodeLabel.setStyleName("yams-treeNode");
            nodeDetailsAnchor.setStyleName("yams-treeNode");
        }
    }

    public void loadChildNodes() {
        removeItem(loadNextTreeItem);
        removeItem(errorTreeItem);
        if (yamsDataNode != null) {
            final List<? extends SerialisableDataNode> childList = getFilteredChildNodes();
            if (childList != null) {
                removeItem(loadingTreeItem);
                if (childList.size() > loadedCount) // add the meta child nodes
                {
                    for (SerialisableDataNode childDataNode : childList) {
                        insertLoadedChildNode(childDataNode);
                        loadedCount++;
                    }
                }
            } else {
                addItem(loadingTreeItem);
                try {
                    final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
                    final List<DataNodeLink> flatChildIds = getFlatChildIds(yamsDataNode, new ArrayList<DataNodeLink>());
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
                            resourceDataNode.setType(new DataNodeType("resource", nodeUriLowerCase.substring(nodeUriLowerCase.lastIndexOf(".") + 1), null, null));
                            final ArrayList<DataField> urlFields = new ArrayList<DataField>();
                            final DataField urlField = new DataField();
                            urlField.setFieldValue(childId.getNodeUriString());
                            urlFields.add(urlField);
                            final ArrayList<FieldGroup> fieldGroup = new ArrayList<FieldGroup>();
                            fieldGroup.add(new FieldGroup("URL", urlFields));
                            resourceDataNode.setFieldGroups(fieldGroup);
                            YamsTreeItem yamsTreeItem = new YamsRpcTreeItem(databaseName, resourceDataNode, dataNodeLoader, treeTableHeader, popupPanel, checkboxListener, clickListener, displayFlatNodes);
                            addItem(yamsTreeItem);
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
                                        insertLoadedChildNode(childDataNode);
                                        loadedCount++;
                                    }
                                }
//                                while (lastToGet > loadedCount) {
//                                    // when nodes are missing these "not found" nodes are added to keep the paging of the child node array in sync
//                                    addItem(new Label("node not found"));
//                                    loadedCount++;
//                                }
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

    private void loadDataNode(final YamsTreeItemLoadedListener itemLoadedListener) {
        if (loadAttempted == false) {
            loadAttempted = true;
            setText("loading...");
            addItem(loadingTreeItem);
            final ArrayList<DataNodeId> dataNodeIdList = new ArrayList<DataNodeId>();
            dataNodeIdList.add(dataNodeId);
            dataNodeLoader.requestLoad(dataNodeIdList, new DataNodeLoaderListener() {

                public void dataNodeLoaded(List<SerialisableDataNode> dataNodeList) {
                    yamsDataNode = dataNodeList.get(0);
                    setLabel();
                    removeItem(loadingTreeItem);
                    try {
                        if (getFilteredChildNodes() != null || !getFlatChildIds(yamsDataNode, new ArrayList<DataNodeLink>()).isEmpty()) {
                            addItem(loadingTreeItem);
                        }
                    } catch (ModelException exception) {
                        setText("failed to show node loaded");
                        logger.log(Level.SEVERE, "failed to show node loaded", exception);
                    }
                    setNodeIconData(dataNodeLoader.getNodeIcon(yamsDataNode));
                    hideShowExpandButton();
                    addColumnsForHighlights();
                    if (itemLoadedListener != null) {
                        itemLoadedListener.yamsTreeItemLoaded(YamsRpcTreeItem.this);
                    }
                }

                public void dataNodeLoadFailed(Throwable caught) {
                    setText(FAILED_TO_LOAD + " via RPC");
                    removeItem(loadingTreeItem);
                    logger.log(Level.SEVERE, FAILED_TO_LOAD + " via RPC", caught);
                    logger.log(Level.SEVERE, caught.getMessage(), caught);
                }
            });
        }
    }

    @Override
    protected void setLabel() {
        if (yamsDataNode != null) {
            int childCountsize = -1;
            try {
                final List<DataNodeLink> flatChildIds = getFlatChildIds(yamsDataNode, new ArrayList<DataNodeLink>());
                if (!flatChildIds.isEmpty()) {
                    childCountsize = flatChildIds.size();
                } else if (yamsDataNode.getChildList() != null) {
                    // get the reduced children here
                    final List<? extends SerialisableDataNode> filteredChildNodes = getFilteredChildNodes();
                    if (filteredChildNodes != null) {
                        childCountsize = filteredChildNodes.size();
                    } else {
                        childCountsize = 0;
                    }
                }
                if (childCountsize > 0) {
                    setText(yamsDataNode.getLabel() + "[" + childCountsize + "]");
                } else {
                    setText(yamsDataNode.getLabel());
                }
            } catch (ModelException exception) {
                setText(yamsDataNode.getLabel() + "[" + exception.getMessage() + "]");
            }
        } else {
            setText("not loaded");
        }
    }

    protected void insertLoadedChildNode(SerialisableDataNode childDataNode) {
        YamsRpcTreeItem yamsTreeItem = new YamsRpcTreeItem(databaseName, childDataNode, dataNodeLoader, treeTableHeader, popupPanel, checkboxListener, clickListener, displayFlatNodes);
        yamsTreeItem.setHighlights(highlighedLinks);
        addItem(yamsTreeItem);

    }

    private List<? extends SerialisableDataNode> getFilteredChildNodes() {
        final List<? extends SerialisableDataNode> childList = yamsDataNode.getChildList();
        if (childList == null) {
            return null;
        }
        if (displayFlatNodes) {
            final ArrayList flatNodes = new ArrayList<SerialisableDataNode>();
            getFlatNodes(yamsDataNode, flatNodes);
            if (flatNodes.isEmpty()) {
//                // if the list is empty then return null
                return null;
            } else {
                return flatNodes;
            }
        } else {
            return yamsDataNode.getChildList();
        }
    }

    private List<? extends SerialisableDataNode> getFlatNodes(SerialisableDataNode currentDataNode, List<SerialisableDataNode> flatNodes) {
        // this filtering should only be relevant to IMDI nodes because CMDI nodes will have all resouces as links
        final List<? extends SerialisableDataNode> childList = currentDataNode.getChildList();
        if (childList != null) // this filtering should only be relevant to IMDI nodes because CMDI nodes will have all resouces as links
        {
            for (SerialisableDataNode childDataNode : childList) {
                final DataNodeType nodeType = childDataNode.getType();
                if ((nodeType != null && IMDI_RESOURCE.equals(nodeType.getID()))) {
//                if (childDataNode.getArchiveHandle() != null) // archive handle is not the best thing to detect resource nodes
                    flatNodes.add(childDataNode);
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
