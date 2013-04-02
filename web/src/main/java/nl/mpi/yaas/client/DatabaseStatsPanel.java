/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import nl.mpi.yaas.common.data.DatabaseStats;

/**
 * Created on : Apr 2, 2013, 11:38:02 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseStatsPanel extends VerticalPanel {

    final SearchOptionsServiceAsync searchOptionsService;
    private final DataNodeTree dataNodeTree;

    public DatabaseStatsPanel(SearchOptionsServiceAsync searchOptionsService, DataNodeTree dataNodeTree) {
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTree = dataNodeTree;
//        DatabaseStatsPanel.this.add(new Label("Getting Database Stats"));
        searchOptionsService.getDatabaseStats(new AsyncCallback<DatabaseStats>() {
            public void onFailure(Throwable caught) {
                DatabaseStatsPanel.this.add(new Label("Failure"));
                DatabaseStatsPanel.this.add(new Label(caught.getMessage()));
            }

            public void onSuccess(DatabaseStats result) {
                DatabaseStatsPanel.this.add(new Label("Known Documents Count: " + result.getKnownDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Root Documents Count: " + result.getRootDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Missing Documents Count: " + result.getMisingDocumentsCount()));
                DatabaseStatsPanel.this.add(new Label("Query time: " + result.getQueryTimeMS() + "ms"));
//                final YaasTreeItem yaasTreeItem = new YaasTreeItem(result.getRootDocumentsIDs());
//                dataNodeTree.addResultsToTree(yaasTreeItem);
            }
        });
    }
}
