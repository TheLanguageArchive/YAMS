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

import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.IconTableBase64;

/**
 * @since Mar 25, 2014 2:57:45 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ArchiveTreePanel extends VerticalPanel {

    private DataNodeId[] dataNodeTreeRootIds = null;
    private String dataNodeTreeDb = null;
    private IconTableBase64 iconTableBase64;
    private DataNodeTree dataNodeTree;
    private static final Logger logger = Logger.getLogger("");
    private final DataNodeTable dataNodeTable;
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;

    public ArchiveTreePanel(DataNodeTable dataNodeTable, SearchOptionsServiceAsync searchOptionsService, HistoryController historyController) {
        this.dataNodeTable = dataNodeTable;
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
    }

    public void setIconTableBase64(IconTableBase64 iconTableBase64) {
        this.iconTableBase64 = iconTableBase64;
        if (dataNodeTreeRootIds != null) {
            // todo: replace this overly complicated reload process by updating the web service provide all the required information (db, root nodes, icons, stats) in one connection
            addDatabaseTree(dataNodeTreeDb, dataNodeTreeRootIds);
        }
    }

    public void addDatabaseTree(String databaseName, DataNodeId[] dataNodeIds) {
        // todo: move this db tree to a node select in the search criterior panel
        // todo: this could end up being a threading issue with iconTableBase64 being set from the wrong database
        remove(dataNodeTree);
        dataNodeTreeRootIds = dataNodeIds;
        dataNodeTreeDb = databaseName;
        dataNodeTree = new DataNodeTree(dataNodeTable, searchOptionsService, iconTableBase64);
        dataNodeTree.addResultsToTree(databaseName, dataNodeIds);
        this.add(dataNodeTree);
        this.setVisible(true);
    }

    public void removeDatabaseTree() {
        remove(dataNodeTree);
//        this.setVisible(this.getTabBar().getTabCount() > 0);
    }
}
