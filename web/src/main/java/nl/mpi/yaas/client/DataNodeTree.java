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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.List;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.HighlighableDataNode;
import nl.mpi.yaas.common.data.IconTableBase64;

/**
 * Created on : Jan 29, 2013, 2:27:32 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeTree extends Tree {

    final DataNodeTable dataNodeTable;
    final SearchOptionsServiceAsync searchOptionsService;
    private final IconTableBase64 iconTableBase64;
    private static final Logger logger = Logger.getLogger("");

    public DataNodeTree(final DataNodeTable dataNodeTable, SearchOptionsServiceAsync searchOptionsService, IconTableBase64 iconTableBase64) {
        this.dataNodeTable = dataNodeTable;
        this.searchOptionsService = searchOptionsService;
        this.iconTableBase64 = iconTableBase64;
//        // Create a tree with a few items in it.
//        TreeItem root = new TreeItem();
//        root.setText("root");
//        root.addTextItem("item0");
//        root.addTextItem("item1");
//        root.addTextItem("item2");
//
//        // Add a CheckBox to the tree
//        TreeItem item = new TreeItem(new CheckBox("item3"));
//        root.addItem(item);
//
//        this.addItem(root);
        addOpenHandler(new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> event) {
                final Object selectedItem = event.getTarget();
                if (selectedItem instanceof YaasTreeItem) {
                    YaasTreeItem yaasTreeItem = (YaasTreeItem) selectedItem;
                    yaasTreeItem.loadChildNodes();
//                    History.newItem(yaasTreeItem.getYaasDataNode().getArchiveHandle());
                }
            }
        });
//        addCloseHandler(new CloseHandler<TreeItem>() {
//
//            public void onClose(CloseEvent<TreeItem> event) {
////                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        });
//        addSelectionHandler(new SelectionHandler<TreeItem>() {
//            public void onSelection(SelectionEvent event) {
//                final Object selectedItem = event.getSelectedItem();
//                if (selectedItem instanceof YaasTreeItem) {
//                    YaasTreeItem yaasTreeItem = (YaasTreeItem) selectedItem;
//                    dataNodeTable.addDataNode(yaasTreeItem.getYaasDataNode());
//                }
//            }
//        });
    }

    public void addResultsToTree(final String databaseName, final DataNodeId[] dataNodeIds) {
        addPagingButton(new Pageable() {
            public void addYaasTreeItem(int index) {
                final YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, dataNodeIds[index], searchOptionsService, dataNodeTable, iconTableBase64);
                DataNodeTree.this.addItem(yaasTreeItem);
            }

            public int getCount() {
                return dataNodeIds.length;
            }
        });
    }

    public void addResultsToTree(final String databaseName, final List<DataNodeLink> rootIds, final HighlighableDataNode dataNode) {
        addPagingButton(new Pageable() {

            public void addYaasTreeItem(int index) {
                final YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, new DataNodeId(rootIds.get(index).getIdString()), searchOptionsService, dataNodeTable, iconTableBase64);
                yaasTreeItem.setHighlights(dataNode);
                DataNodeTree.this.addItem(yaasTreeItem);
            }

            public int getCount() {
                return rootIds.size();
            }
        });
    }

    private void addPagingButton(final Pageable pageable) {
        final Button loadNextButton = new Button("Load More");
        final TreeItem loadNextTreeItem = new TreeItem(loadNextButton);
        final ClickHandler clickHandler = new ClickHandler() {
            private int loadedCount = 0;

            public void onClick(ClickEvent event) {
                DataNodeTree.this.removeItem(loadNextTreeItem);
                int numberToLoad = 10;
                while (loadedCount < pageable.getCount() && numberToLoad > 0) {
                    pageable.addYaasTreeItem(loadedCount);
                    numberToLoad--;
                    loadedCount++;
                }
                if (loadedCount < pageable.getCount()) {
                    DataNodeTree.this.addItem(loadNextTreeItem);
                }
            }
        };
        clickHandler.onClick(null);
        loadNextButton.addClickHandler(clickHandler);
    }
}
