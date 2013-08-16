/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.DatabaseStats;

/**
 * Created on : Apr 2, 2013, 11:38:02 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseStatsPanel extends VerticalPanel {

    private final ListBox databaseListBox = new ListBox();
    private final SearchOptionsServiceAsync searchOptionsService;
    private final DataNodeTree dataNodeTree;
    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_GET_THE_DATABASE_LIST = "Failed to get the database list";
    private static final String FAILED_TO_GET_THE_DATABASE_STATISTICS = "Failed to get the database statistics";
    private final ArrayList<DatabaseNameListener> databaseNameListeners = new ArrayList<DatabaseNameListener>();
//    final private SearchOptionsServiceAsync searchOptionsService;
//    final private DataNodeTree dataNodeTree;
    private final String databaseName;

    public DatabaseStatsPanel(SearchOptionsServiceAsync searchOptionsService, String databaseName, final DataNodeTree dataNodeTree, DatabaseNameListener databaseNameListener) {
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTree = dataNodeTree;
        this.databaseName = databaseName;
        databaseNameListeners.add(databaseNameListener);
        add(databaseListBox);
        getDbList();
        updateDbStats();
    }

    private void getDbList() {
        searchOptionsService.getDatabaseList(new AsyncCallback<String[]>() {
            public void onFailure(Throwable caught) {
                DatabaseStatsPanel.this.add(new Label(FAILED_TO_GET_THE_DATABASE_LIST));
                logger.log(Level.SEVERE, FAILED_TO_GET_THE_DATABASE_LIST, caught);
            }

            public void onSuccess(String[] result) {
                int selectedIndex = 0;
                for (String databaseNameItem : result) {
                    databaseListBox.addItem(databaseNameItem);
                    if (databaseNameItem.equals(databaseName)) {
                        selectedIndex = databaseListBox.getItemCount() - 1;
                    }
                }
                databaseListBox.setSelectedIndex(selectedIndex);
                databaseListBox.addChangeHandler(new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        for (DatabaseNameListener databaseNameListener : databaseNameListeners) {
                            databaseNameListener.setDataBaseName(databaseListBox.getItemText(databaseListBox.getSelectedIndex()));
                        }
                    }
                });
            }
        });
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
                DatabaseStatsPanel.this.add(new Label("Known Documents Count: " + result.getKnownDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Root Documents Count: " + result.getRootDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Missing Documents Count: " + result.getMisingDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Duplicate Documents Count: " + result.getDuplicateDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Query time: " + result.getQueryTimeMS() + "ms"));
                //                final YaasTreeItem yaasTreeItem = new YaasTreeItem();
                dataNodeTree.addResultsToTree(databaseName, result.getRootDocumentsIDs());
            }
        });
    }
}
