/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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

    private final ListBox databaseListBox = new ListBox();
    private final SearchOptionsServiceAsync searchOptionsService;
    private final DataNodeTree dataNodeTree;
    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_GET_THE_DATABASE_LIST = "Failed to get the database list";
    private static final String FAILED_TO_GET_THE_DATABASE_STATISTICS = "Failed to get the database statistics";
//    final private SearchOptionsServiceAsync searchOptionsService;
//    final private DataNodeTree dataNodeTree;

    public DatabaseStatsPanel(SearchOptionsServiceAsync searchOptionsService, final DataNodeTree dataNodeTree) {
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTree = dataNodeTree;
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
                for (String databaseName : result) {
                    databaseListBox.addItem(databaseName);
                }
            }
        });
    }

    private void updateDbStats() {
        //        this.searchOptionsService = searchOptionsService;
        //        this.dataNodeTree = dataNodeTree;
        //        DatabaseStatsPanel.this.add(new Label("Getting Database Stats"));
        searchOptionsService.getDatabaseStats(new AsyncCallback<DatabaseStats>() {
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
                dataNodeTree.addResultsToTree(result.getRootDocumentsIDs());
            }
        });
    }
}
