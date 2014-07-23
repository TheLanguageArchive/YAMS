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
package nl.mpi.yams.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import nl.mpi.yams.client.DatabaseInformation;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.client.HistoryListener;
import nl.mpi.yams.common.data.DatabaseStats;

/**
 * @since Nov 4, 2013 4:02:36 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseSelect extends VerticalPanel implements HistoryListener {

    private final ListBox databaseListBox = new ListBox();
    private static final String FAILED_TO_GET_THE_DATABASE_LIST = "Failed to get the database list, is the database running?";
    public static final String PLEASE_SELECT_A_DATABASE = "<please select a database>";
    private static final String LOADING_DATABASE_LIST = "Loading database list.";
    private static final String LOADING_DATABASE = "Loading database.";
    private static final Logger logger = Logger.getLogger("");
    private final HistoryController historyController;
    private final DatabaseInformation databaseInfo;
    private final Label databaseInfoLabel;
    final private Image loadingImage;

    public DatabaseSelect(final HistoryController historyController, DatabaseInformation databaseInfo) {
        add(databaseListBox);
        databaseInfoLabel = new Label(LOADING_DATABASE_LIST);
        add(databaseInfoLabel);
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
        loadingImage = new Image("./loader.gif");
        add(loadingImage);
        databaseListBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                final String itemText = databaseListBox.getItemText(databaseListBox.getSelectedIndex());
                if (PLEASE_SELECT_A_DATABASE.equals(itemText)) {
                    historyController.setDatabaseName(null);
                } else {
                    historyController.setDatabaseName(itemText);
                }
            }
        });
    }

    public void userSelectionChange() {
        if (databaseInfo.hasDatabaseError()) {
            showError();
        } else {
            setDatabaseNames(databaseInfo.getDatabaseList());
            showDatabaseInfo(databaseInfo.getDatabaseStats(historyController.getDatabaseName()));
        }
    }

    public void historyChange() {
        final String databaseName = historyController.getDatabaseName();
        if (databaseName != null) {
            if (databaseName.isEmpty()) {
                databaseListBox.setSelectedIndex(0);
            } else {
                for (int index = 0; index < databaseListBox.getItemCount(); index++) {
                    if (databaseListBox.getSelectedIndex() != index) {
                        if (databaseName.equals(databaseListBox.getItemText(index))) {
                            databaseListBox.setSelectedIndex(index);
//                        loadingImage.setVisible(true);
//                        databaseInfoLabel.setText(LOADING_DATABASE);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void setDatabaseInfoLabel(String databaseInfoText) {
        databaseInfoLabel.setText(databaseInfoText);
        loadingImage.setVisible(false);
    }

    private void showError() {
        databaseInfoLabel.setText(FAILED_TO_GET_THE_DATABASE_LIST);
        loadingImage.setVisible(false);
    }

    private void showDatabaseInfo(DatabaseStats result) {
        if (result != null) {
            final String knownDocumentsText = "Available Documents: " + result.getKnownDocumentsCount();
            final String missingDocumentsText = "Missing Documents: " + result.getMisingDocumentsCount();
            setDatabaseInfoLabel(knownDocumentsText + " " + missingDocumentsText);
        } else {
            databaseInfoLabel.setText("");
        }
    }

    private void setDatabaseNames(String[] databaseNames) {
        // only if our list does not match the server list do we do anything here because the database list will not change within the scope of a page
        if (databaseNames.length != databaseListBox.getItemCount() - 1) {
            loadingImage.setVisible(false);
            int selectedIndex = 0;
            databaseListBox.clear();
            databaseListBox.addItem(PLEASE_SELECT_A_DATABASE);
            for (String databaseNameItem : databaseNames) {
                databaseListBox.addItem(databaseNameItem);
                if (databaseNameItem.equals(historyController.getDatabaseName())) {
                    selectedIndex = databaseListBox.getItemCount() - 1;
                }
            }
            databaseListBox.setSelectedIndex(selectedIndex);
        }
    }
}
