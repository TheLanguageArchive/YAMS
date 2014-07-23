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

import com.google.gwt.user.client.ui.HorizontalPanel;
import java.util.HashMap;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.client.controllers.ActionsPanelController;
import nl.mpi.yams.client.DatabaseInformation;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.client.HistoryData;
import nl.mpi.yams.client.HistoryListener;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.client.TreeNodeClickListener;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.DatabaseStats;
import nl.mpi.yams.common.data.IconTableBase64;

/**
 * @since Mar 25, 2014 2:57:45 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ArchiveTreePanel extends HorizontalPanel implements HistoryListener {

    private String dataNodeTreeDb = null;
    private final boolean useCorpusStructureDb;
    private DataNodeTree dataNodeTree = null;
    private static final Logger logger = Logger.getLogger("");
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;
    private final DatabaseInformation databaseInfo;
    private final ActionsPanelController actionsPanelController;
    HashMap<SerialisableDataNode, HorizontalPanel> nodePanels = new HashMap<SerialisableDataNode, HorizontalPanel>();

    public ArchiveTreePanel(DataNodeTable dataNodeTable, SearchOptionsServiceAsync searchOptionsService, HistoryController historyController, DatabaseInformation databaseInfo, ActionsPanelController actionsPanelController, boolean useCorpusStructureDb) {
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
        this.actionsPanelController = actionsPanelController;
        this.useCorpusStructureDb = useCorpusStructureDb;
    }

    public void historyChange() {
        userSelectionChange();
    }

    public void userSelectionChange() {
        if (!useCorpusStructureDb) {
            final String databaseName = historyController.getDatabaseName();
//            logger.info(databaseName);
            if (dataNodeTreeDb == null || !dataNodeTreeDb.equals(databaseName)) {
                if (dataNodeTree != null) {
                    ArchiveTreePanel.this.remove(dataNodeTree);
                    dataNodeTree = null;
                }
//                logger.info("ArchiveTreePanel");
//                logger.info(dataNodeTreeDb);
                final DatabaseStats databaseStats = databaseInfo.getDatabaseStats(databaseName);
                final IconTableBase64 databaseIcons = databaseInfo.getDatabaseIcons(databaseName);
                if (databaseStats != null && databaseIcons != null && databaseStats.getRootDocumentsIDs() != null) {
                    addDatabaseTree(databaseName, databaseStats.getRootDocumentsIDs(), databaseIcons);
                }
            }
        } else {
            addCsDatabaseTree();
        }
    }

    public void addDatabaseTree(String databaseName, DataNodeId[] dataNodeIds, IconTableBase64 databaseIcons) {
//        logger.info("addDatabaseTree");
//        logger.info(databaseName);
//        logger.info("dataNodeIds:" + dataNodeIds.length);
//        logger.info(dataNodeIds[0].getIdString());
        dataNodeTreeDb = databaseName;
        if (databaseName != null) {
            final TreeNodeClickListener treeNodeClickListener = new TreeNodeClickListener() {

                public void clickEvent(SerialisableDataNode dataNode) {
//                    logger.info("TreeNodeClickListener");
//                    actionsPanelController.setDataNode(dataNode);
//                    detailsPanel.setDataNode(dataNode);
                    try {
                        historyController.setBranchSelection(new DataNodeId(dataNode.getID()), HistoryData.NodeActionType.details);
                    } catch (ModelException exception) {
                        logger.warning(exception.getMessage());
                    }
                }
            };
            dataNodeTree = new DataNodeTree(null, treeNodeClickListener, searchOptionsService, databaseIcons, true);
            dataNodeTree.addResultsToTree(databaseName, dataNodeIds, true);
            ArchiveTreePanel.this.add(dataNodeTree);
        }
    }

    public void addCsDatabaseTree() {
        if (dataNodeTree == null) {
//            logger.info("addCsDatabaseTree");
            final TreeNodeClickListener treeNodeClickListener = new TreeNodeClickListener() {

                public void clickEvent(SerialisableDataNode dataNode) {
//                    logger.info("TreeNodeClickListener");
                    try {
                        String id = dataNode.getArchiveHandle();
                        if (id == null) {
                            id = new DataNodeLink(dataNode.getURI(), dataNode.getArchiveHandle()).getIdString();
                        }
//                        logger.info(id);
                        historyController.setBranchSelection(new DataNodeId(id), HistoryData.NodeActionType.details);
                    } catch (ModelException exception) {
                        logger.warning(exception.getMessage());
                    }
                }
            };
            dataNodeTree = new DataNodeTree(null, treeNodeClickListener, searchOptionsService, null, true);
            dataNodeTree.addCsRootToTree();
            ArchiveTreePanel.this.add(dataNodeTree);
        }
    }
}
