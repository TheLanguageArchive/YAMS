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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import nl.mpi.yams.common.data.DatabaseStats;

/**
 * Created on : Apr 2, 2013, 11:38:02 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseStatsPanel extends VerticalPanel implements HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_GET_THE_DATABASE_STATISTICS = "Failed to get the database statistics";
    private final HistoryController historyController;
    private final DatabaseInfo databaseInfo;
    private String databaseName = null;

    public DatabaseStatsPanel(DatabaseInfo databaseInfo, HistoryController historyController) {
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
    }

    public void userSelectionChange() {
        if (!databaseInfo.ready()) {
            DatabaseStatsPanel.this.add(new Label("Getting Database Stats"));
        } else {
            final String currentDatabaseName = historyController.getDatabaseName();
            if (databaseName == null || !databaseName.equals(currentDatabaseName)) {
                databaseName = currentDatabaseName;
                DatabaseStatsPanel.this.clear();
                if (databaseInfo.hasDatabaseError()) {
                    showError();
                }
                showDatabaseInfo(databaseInfo.getDatabaseStats(currentDatabaseName));
            }
        }
    }

    public void historyChange() {
        userSelectionChange();
    }

    private void showError() {
        DatabaseStatsPanel.this.add(new Label(FAILED_TO_GET_THE_DATABASE_STATISTICS));
    }

    private void showDatabaseInfo(DatabaseStats result) {
        if (result != null) {
            DatabaseStatsPanel.this.add(new Label("Current Database: " + databaseName));
            final String knownDocumentsText = "Available Documents: " + result.getKnownDocumentsCount();
            final String missingDocumentsText = "Missing Documents: " + result.getMisingDocumentsCount();
            DatabaseStatsPanel.this.add(new Label(knownDocumentsText));
            DatabaseStatsPanel.this.add(new Label("Root Documents Count: " + result.getRootDocumentsCount()));
            DatabaseStatsPanel.this.add(new Label(missingDocumentsText));
            DatabaseStatsPanel.this.add(new Label("Duplicate Documents Count: " + result.getDuplicateDocumentsCount()));
            DatabaseStatsPanel.this.add(new Label("Query time: " + result.getQueryTimeMS() + "ms"));
        }
    }
}
