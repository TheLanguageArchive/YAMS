/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Created on : Jan 29, 2013, 2:27:32 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeTree extends Tree {

    public DataNodeTree() {
        // Create a tree with a few items in it.
        TreeItem root = new TreeItem();
        root.setText("root");
        root.addTextItem("item0");
        root.addTextItem("item1");
        root.addTextItem("item2");

        // Add a CheckBox to the tree
        TreeItem item = new TreeItem(new CheckBox("item3"));
        root.addItem(item);

        this.addItem(root);
    }

    public void setRootNode(TreeItem treeItem) {
        this.addItem(treeItem);
    }
}
