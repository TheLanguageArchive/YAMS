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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.client.HistoryListener;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.client.TreeNodeCheckboxListener;
import nl.mpi.yams.common.data.HighlightableDataNode;
import nl.mpi.yams.common.data.IconTableBase64;

/**
 * @since Feb 06, 2014 11:37 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ResultsPanel extends TabPanel implements HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;
    private final TreeNodeCheckboxListener checkboxListener;
    private final TreeActionPanelClickListener treeActionPanelClickListener;
//    final String lastDatabaseName;
    // todo: replace the variabls dataNodeTreeRootIds and dataNodeTreeDb by updating the web service provide all the required information (db, root nodes, icons, stats) in one connection

    public ResultsPanel(final DataNodeTable dataNodeTable, SearchOptionsServiceAsync searchOptionsService, HistoryController historyController, TreeActionPanelClickListener treeActionPanelClickListener) {
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
        this.treeActionPanelClickListener = treeActionPanelClickListener;
        this.setVisible(false);
        checkboxListener = new TreeNodeCheckboxListener() {

            public void stateChanged(boolean selected, PluginDataNode dataNode, CheckBox checkBox) {
                if (selected) {
                    dataNodeTable.addDataNode(dataNode);
                } else {
                    dataNodeTable.removeDataNode(dataNode);
                }
            }
        };
    }

    public void userSelectionChange() {
        // nothing needs to be done in this class
    }

    public void historyChange() {
//        final String databaseName = historyController.getDatabaseName();
//        if (databaseName != null&& !lastDatabaseName.equals(databaseName)) {
//            RootPanel.get("searchOptionsPanel").add(loadingImage);
//            searchOptionsService.getImageDataForTypes(databaseName, new AsyncCallback<IconTableBase64>() {
//                public void onFailure(Throwable caught) {
//                    RootPanel.get("searchOptionsPanel").add(new Label(NO__DATABASE__SELECTED));
//                    RootPanel.get("searchOptionsPanel").remove(loadingImage);
//                }
//
//                public void onSuccess(IconTableBase64 result) {
//
//                }
//            });
//        }
    }

    public void addResultsTree(String databaseName, IconTableBase64 iconTableBase64, HighlightableDataNode dataNode, long responseTimeMils) {
        try {
            VerticalPanel verticalPanel = new VerticalPanel();
            final List<DataNodeLink> childIds = dataNode.getChildIds();
            if (childIds != null) {
                final DataNodeTree dataNodeTree = new DataNodeTree(checkboxListener, treeActionPanelClickListener, searchOptionsService, iconTableBase64, false);
                dataNodeTree.addResultsToTree(databaseName, childIds, dataNode, false);
                // add a label showing the time taken by a search and the result count
                final Label timeLabel = new Label("found " + childIds.size() + " in " + responseTimeMils + "ms");
                verticalPanel.add(timeLabel);
                verticalPanel.add(dataNodeTree);
            } else {
                final Label label = new Label("No results found");
                verticalPanel.add(label);
                final Label timeLabel = new Label(responseTimeMils + "ms");
                verticalPanel.add(timeLabel);
            }
            this.addClosableTab(verticalPanel, dataNode.getLabel());
            this.selectTab(this.getWidgetIndex(verticalPanel));
        } catch (ModelException exception) {
            logger.log(Level.SEVERE, "Failed to get child IDs", exception);
        }
        this.setVisible(true);
    }

    private void addClosableTab(final Widget widget, String text) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        final Label label = new Label(text);
        Button button = new Button("X");
        button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final int widgetIndex = ResultsPanel.this.getWidgetIndex(widget);
                final int selectedTab = ResultsPanel.this.getTabBar().getSelectedTab();
                ResultsPanel.this.remove(widget);
                if (selectedTab == widgetIndex) {
                    ResultsPanel.this.selectTab(widgetIndex - 1);
                }
            }
        });
        horizontalPanel.add(label);
        horizontalPanel.add(new HTML("&nbsp&nbsp&nbsp"));
        horizontalPanel.add(button);
        this.add(widget, horizontalPanel);
    }
}
