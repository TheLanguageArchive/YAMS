/**
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
public class DatabaseStatsPanel extends VerticalPanel {

    private final SearchOptionsServiceAsync searchOptionsService;
    private final DataNodeTree dataNodeTree;
    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_GET_THE_DATABASE_STATISTICS = "Failed to get the database statistics";
//    final private SearchOptionsServiceAsync searchOptionsService;
//    final private DataNodeTree dataNodeTree;
    private final String databaseName;
    private final DatabaseSelect databaseSelect;

    public DatabaseStatsPanel(SearchOptionsServiceAsync searchOptionsService, String databaseName, final DataNodeTree dataNodeTree, DatabaseSelect databaseSelect) {
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTree = dataNodeTree;
        this.databaseName = databaseName;
        this.databaseSelect = databaseSelect;
        updateDbStats();
    }

    private void updateDbStats() {
        DatabaseStatsPanel.this.add(new Label("Current Database: " + databaseName));
        //        this.searchOptionsService = searchOptionsService;
        //        this.dataNodeTree = dataNodeTree;
        //        DatabaseStatsPanel.this.add(new Label("Getting Database Stats"));
        searchOptionsService.getDatabaseStats(databaseName, new AsyncCallback<DatabaseStats>() {
            public void onFailure(Throwable caught) {
                DatabaseStatsPanel.this.add(new Label(FAILED_TO_GET_THE_DATABASE_STATISTICS));
                logger.log(Level.SEVERE, FAILED_TO_GET_THE_DATABASE_STATISTICS, caught);
            }

            public void onSuccess(DatabaseStats result) {
                final String knownDocumentsText = "Available Documents: " + result.getKnownDocumentsCount();
                final String missingDocumentsText = "Missing Documents: " + result.getMisingDocumentsCount();
                databaseSelect.setDatabaseInfoLabel(knownDocumentsText + " " + missingDocumentsText);
                DatabaseStatsPanel.this.add(new Label(knownDocumentsText));
                DatabaseStatsPanel.this.add(new Label("Root Documents Count: " + result.getRootDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label(missingDocumentsText));
                DatabaseStatsPanel.this.add(new Label("Duplicate Documents Count: " + result.getDuplicateDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Query time: " + result.getQueryTimeMS() + "ms"));
                //                final YaasTreeItem yaasTreeItem = new YaasTreeItem();
                dataNodeTree.addResultsToTree(databaseName, result.getRootDocumentsIDs());
            }
        });
    }
}
