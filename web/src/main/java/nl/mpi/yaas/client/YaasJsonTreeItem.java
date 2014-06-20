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
package nl.mpi.yaas.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import static nl.mpi.yaas.client.YaasTreeItem.FAILURE;
import static nl.mpi.yaas.client.YaasTreeItem.logger;
import nl.mpi.yaas.common.data.DataNodeId;

/**
 * @since May 23, 2014 10:22:42 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YaasJsonTreeItem extends YaasTreeItem {

    final private YaasTreeItemLoadedListener itemLoadedListener;

    public YaasJsonTreeItem(SerialisableDataNode childDataNode, DataNodeLoader dataNodeLoader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener, final YaasTreeItemLoadedListener itemLoadedListener) {
        super(dataNodeLoader, popupPanel, checkboxListener, clickListener);
        this.itemLoadedListener = itemLoadedListener;
        this.yaasDataNode = childDataNode;
        setLabel();
        removeItem(loadingTreeItem);
        setNodeIconStye(this.yaasDataNode.getType(), this.yaasDataNode.getPermissions());
        hideShowExpandButton();
        if (itemLoadedListener != null) {
            itemLoadedListener.yaasTreeItemLoaded(YaasJsonTreeItem.this);
        }
        if (yaasDataNode.getLinkCount() > 0) {
            addItem(loadingTreeItem);
        }
    }

    public YaasJsonTreeItem(DataNodeLoader dataNodeLoader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener, final YaasTreeItemLoadedListener itemLoadedListener) {
        super(dataNodeLoader, popupPanel, checkboxListener, clickListener);
        this.itemLoadedListener = itemLoadedListener;
        loadDataNode(itemLoadedListener);
    }

    private void loadDataNode(final YaasTreeItemLoadedListener itemLoadedListener) {
        if (loadAttempted == false) {
            loadAttempted = true;
            setText("loading...");
            addItem(loadingTreeItem);
//            logger.info("requestLoadRoot");
            dataNodeLoader.requestLoadRoot(new DataNodeLoaderListener() {
//
                public void dataNodeLoaded(List<SerialisableDataNode> dataNodeList) {
//                    logger.info("dataNodeLoaded");
                    yaasDataNode = dataNodeList.get(0);
                    setLabel();
                    removeItem(loadingTreeItem);
//                    try {
                    if (yaasDataNode.getLinkCount() > 0) {
                        addItem(loadingTreeItem);
                    }
//                    } catch (ModelException exception) {
//                        setText(FAILURE);
//                        logger.log(Level.SEVERE, FAILURE, exception);
//                    }                   
                    setNodeIconStye(yaasDataNode.getType(), yaasDataNode.getPermissions());
                    hideShowExpandButton();
                    if (itemLoadedListener != null) {
                        itemLoadedListener.yaasTreeItemLoaded(YaasJsonTreeItem.this);
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

    @Override
    Widget getPopupWidget() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadChildNodes() {
        removeItem(loadNextTreeItem);
        removeItem(errorTreeItem);
        if (yaasDataNode != null) {
            addItem(loadingTreeItem);
            final int maxToGet = yaasDataNode.getLinkCount();
            if (maxToGet <= loadedCount) {
                // all child nodes should be visible so we can just return
                removeItem(loadingTreeItem);
                return;
            }
            try {
                final int numberToGet = 20;
                final int firstToGet = (loadedCount == 0) ? loadedCount : loadedCount + 1;
                final int lastToGet = (maxToGet < firstToGet + numberToGet) ? maxToGet : firstToGet + numberToGet;
//                    logger.log(Level.INFO, "loadedCount: " + loadedCount + ", numberToGet: " + numberToGet + ", firstToGet: " + firstToGet + ", lastToGet: " + lastToGet + ", maxToGet: " + maxToGet);                    
                dataNodeLoader.requestLoadChildrenOf(new DataNodeId(yaasDataNode.getURI()), firstToGet, lastToGet, new DataNodeLoaderListener() {

                    public void dataNodeLoaded(List<SerialisableDataNode> dataNodeList) {
//                        setText("Loaded " + dataNodeList.size() + " child nodes");
                        removeItem(loadingTreeItem);
                        if (dataNodeList != null) {
                            for (SerialisableDataNode childDataNode : dataNodeList) {
                                insertLoadedChildNode(childDataNode);
                                loadedCount++;
                            }
                        }
//                        while (lastToGet > loadedCount) {
//                            // when nodes are missing these "not found" nodes are added to keep the paging of the child node array in sync
//                            addItem(new Label("node not found"));
//                            loadedCount++;
//                        }
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
            } catch (ModelException exception) {
                removeItem(loadingTreeItem);
                errorTreeItem.setText(ERROR_GETTING_CHILD_NODES);
                addItem(errorTreeItem);
                logger.log(Level.SEVERE, ERROR_GETTING_CHILD_NODES, exception);
            }
        } else {
            errorTreeItem.setText("Data nod loaded");
            addItem(errorTreeItem);
        }
    }

    @Override
    void insertLoadedChildNode(SerialisableDataNode childDataNode) {
        YaasJsonTreeItem yaasTreeItem = new YaasJsonTreeItem(childDataNode, dataNodeLoader, popupPanel, checkboxListener, clickListener, itemLoadedListener);
        addItem(yaasTreeItem);
    }

    @Override
    void setLabel() {
        if (yaasDataNode != null) {
            int childCountsize = -1;
            if (childCountsize > 0) {
                setText(yaasDataNode.getLabel() + "[" + childCountsize + "]");
            } else {
                setText(yaasDataNode.getLabel());
            }
        } else {
            setText("not loaded");
        }
    }
}
