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

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
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
                }
            }
        });
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

    public void addResultsToTree(String databaseName, DataNodeId[] dataNodeIds) {
        for (DataNodeId dataNodeId : dataNodeIds) {
            final YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, dataNodeId, searchOptionsService, dataNodeTable, iconTableBase64);
            this.addItem(yaasTreeItem);
        }
        // temp code to add a session
        //final YaasTreeItem yaasTreeItem = new YaasTreeItem(new DataNodeId("http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/sessions/fv800203.imdi"), searchOptionsService, dataNodeTable);
        //this.addItem(yaasTreeItem);
    }

    public void addResultsToTree(String databaseName, HighlighableDataNode dataNode) {
        final YaasTreeItem yaasTreeItem = new YaasTreeItem(databaseName, dataNode, searchOptionsService, dataNodeTable, iconTableBase64);
        yaasTreeItem.setHighlights(dataNode);
        this.addItem(yaasTreeItem);
    }
}
