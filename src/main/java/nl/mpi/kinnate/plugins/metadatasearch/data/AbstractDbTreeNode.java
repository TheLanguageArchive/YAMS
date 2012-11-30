package nl.mpi.kinnate.plugins.metadatasearch.data;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import nl.mpi.arbil.plugin.PluginArbilDataNodeLoader;
import nl.mpi.kinnate.plugins.metadatasearch.db.ArbilDatabase;

/**
 * Document : AbstractDbTreeNode <br> Created on Sep 6, 2012, 4:20:23 PM <br>
 *
 * @author Peter Withers <br>
 */
abstract public class AbstractDbTreeNode implements TreeNode {

    protected DbTreeNode parentDbTreeNode = null;
    protected DefaultTreeModel defaultTreeModel = null;
    protected PluginArbilDataNodeLoader arbilDataNodeLoader;
    protected ArbilDatabase arbilDatabase;

    public void setParentDbTreeNode(DbTreeNode parentDbTreeNode, DefaultTreeModel defaultTreeModel, PluginArbilDataNodeLoader arbilDataNodeLoader, ArbilDatabase arbilDatabase) {
        this.parentDbTreeNode = parentDbTreeNode;
        this.defaultTreeModel = defaultTreeModel;
        this.arbilDataNodeLoader = arbilDataNodeLoader;
        this.arbilDatabase = arbilDatabase;
    }
}