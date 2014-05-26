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

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import nl.mpi.flap.model.SerialisableDataNode;
import static nl.mpi.yaas.client.YaasTreeItem.FAILURE;
import static nl.mpi.yaas.client.YaasTreeItem.logger;

/**
 * @since May 23, 2014 10:22:42 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YaasJsonTreeItem extends YaasTreeItem {

    final private YaasTreeItemLoadedListener itemLoadedListener;

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
                    setNodeIcon();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void insertLoadedChildNode(SerialisableDataNode childDataNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
