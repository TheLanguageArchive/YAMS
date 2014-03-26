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
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.IconTableBase64;

/**
 * @since Mar 25, 2014 2:57:45 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ArchiveTreePanel extends VerticalPanel implements HistoryListener {

    private String dataNodeTreeDb = null;
    private DataNodeTree dataNodeTree = null;
    private static final Logger logger = Logger.getLogger("");
    private final DataNodeTable dataNodeTable;
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;
    private final DatabaseInfo databaseInfo;
    private final PopupPanel popupPanel = new PopupPanel();

    public ArchiveTreePanel(DataNodeTable dataNodeTable, SearchOptionsServiceAsync searchOptionsService, HistoryController historyController, DatabaseInfo databaseInfo) {
        this.dataNodeTable = dataNodeTable;
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
    }

    public void historyChange() {
        userSelectionChange();
    }

    public void userSelectionChange() {
        final String databaseName = historyController.getDatabaseName();
        if (dataNodeTreeDb == null || !dataNodeTreeDb.equals(databaseName)) {
            if (dataNodeTree != null) {
                popupPanel.remove(dataNodeTree);
                dataNodeTree = null;
                this.clear();
            }
//            logger.info("ArchiveTreePanel");
//            logger.info(dataNodeTreeDb);
            final DatabaseStats databaseStats = databaseInfo.getDatabaseStats(databaseName);
            final IconTableBase64 databaseIcons = databaseInfo.getDatabaseIcons(databaseName);
            if (databaseStats != null && databaseIcons != null && databaseStats.getRootDocumentsIDs() != null) {
                addDatabaseTree(databaseName, databaseStats.getRootDocumentsIDs(), databaseIcons);
            }
        }
    }

    public void addDatabaseTree(String databaseName, DataNodeId[] dataNodeIds, IconTableBase64 databaseIcons) {
        //logger.info("addDatabaseTree");
        // todo: move this db tree to a node select in the search criterior panel
        // todo: this could end up being a threading issue with iconTableBase64 being set from the wrong database
        //logger.info(databaseName);
        dataNodeTreeDb = databaseName;
        if (databaseName != null) {
            for (DataNodeId nodeId : dataNodeIds) {
                addSearchBranch(databaseName, nodeId, databaseIcons);
            }
            dataNodeTree = new DataNodeTree(dataNodeTable, searchOptionsService, databaseIcons);
            dataNodeTree.addResultsToTree(databaseName, dataNodeIds);
            popupPanel.add(dataNodeTree);
        }
    }

    private void addSearchBranch(String databaseName, DataNodeId nodeId, IconTableBase64 databaseIcons) {
        add(new YaasTreeItem(databaseName, nodeId, searchOptionsService, dataNodeTable, databaseIcons, null, popupPanel).outerPanel);
    }
}
