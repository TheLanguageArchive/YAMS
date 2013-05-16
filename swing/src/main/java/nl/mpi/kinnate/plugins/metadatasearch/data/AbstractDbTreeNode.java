package nl.mpi.kinnate.plugins.metadatasearch.data;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import nl.mpi.yaas.common.db.DataBaseManager;

/**
 * Document : AbstractDbTreeNode <br> Created on Sep 6, 2012, 4:20:23 PM <br>
 *
 * @author Peter Withers <br>
 */
abstract public class AbstractDbTreeNode implements TreeNode {

    protected DbTreeNode parentDbTreeNode = null;
    protected DefaultTreeModel defaultTreeModel = null;
    protected DataBaseManager yaasDatabase;

    public void setParentDbTreeNode(DbTreeNode parentDbTreeNode, DefaultTreeModel defaultTreeModel, DataBaseManager yaasDatabase) {
        this.parentDbTreeNode = parentDbTreeNode;
        this.defaultTreeModel = defaultTreeModel;
        this.yaasDatabase = yaasDatabase;
    }
}
