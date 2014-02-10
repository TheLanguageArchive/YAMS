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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.DatabaseStats;

/**
 * Created on : Apr 2, 2013, 11:38:02 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseStatsPanel extends VerticalPanel implements HistoryListener {

    private final SearchOptionsServiceAsync searchOptionsService;
    private final ResultsPanel resultsPanel;
    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_GET_THE_DATABASE_STATISTICS = "Failed to get the database statistics";
//    final private SearchOptionsServiceAsync searchOptionsService;
//    final private DataNodeTree dataNodeTree;
    private final DatabaseSelect databaseSelect;
    private final HistoryController historyController;

    public DatabaseStatsPanel(SearchOptionsServiceAsync searchOptionsService, final ResultsPanel resultsPanel, DatabaseSelect databaseSelect, HistoryController historyController) {
        this.searchOptionsService = searchOptionsService;
        this.resultsPanel = resultsPanel;
        this.databaseSelect = databaseSelect;
        this.historyController = historyController;
    }

    public void historyChange() {
        updateDbStats();
    }

    private void updateDbStats() {
        DatabaseStatsPanel.this.clear();
        final String databaseName = historyController.getDatabaseName();
        if (databaseName != null && !databaseName.isEmpty()) {
            DatabaseStatsPanel.this.add(new Label("Current Database: " + databaseName));
            //        this.searchOptionsService = searchOptionsService;
            //        this.dataNodeTree = dataNodeTree;
            //        DatabaseStatsPanel.this.add(new Label("Getting Database Stats"));
            searchOptionsService.getDatabaseStats(databaseName, new AsyncCallback<DatabaseStats>() {
                public void onFailure(Throwable caught) {
                    DatabaseStatsPanel.this.add(new Label(FAILED_TO_GET_THE_DATABASE_STATISTICS));
                    logger.log(Level.SEVERE, FAILED_TO_GET_THE_DATABASE_STATISTICS);
                }

                public void onSuccess(DatabaseStats result) {
                    final String knownDocumentsText = "Available Documents: " + result.getKnownDocumentsCount();
                    final String missingDocumentsText = "Missing Documents: " + result.getMisingDocumentsCount();
                    DatabaseStatsPanel.this.add(new Label(knownDocumentsText));
                    DatabaseStatsPanel.this.add(new Label("Root Documents Count: " + result.getRootDocumentsCount()));
                    DatabaseStatsPanel.this.add(new Label(missingDocumentsText));
                    DatabaseStatsPanel.this.add(new Label("Duplicate Documents Count: " + result.getDuplicateDocumentsCount()));
                    DatabaseStatsPanel.this.add(new Label("Query time: " + result.getQueryTimeMS() + "ms"));
                    //                final YaasTreeItem yaasTreeItem = new YaasTreeItem();
                    resultsPanel.addDatabaseTree(databaseName, result.getRootDocumentsIDs());
                    databaseSelect.setDatabaseInfoLabel(knownDocumentsText + " " + missingDocumentsText);
                }
            });
        }
    }
}
