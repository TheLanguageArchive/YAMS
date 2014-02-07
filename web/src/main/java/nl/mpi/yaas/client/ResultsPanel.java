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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import static nl.mpi.yaas.client.YaasTreeItem.FAILURE;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.common.data.HighlighableDataNode;
import nl.mpi.yaas.common.data.IconTableBase64;

/**
 * @since Feb 06, 2014 11:37 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ResultsPanel extends TabPanel {

    private static final Logger logger = Logger.getLogger("");
    final DataNodeTable dataNodeTable;
    final SearchOptionsServiceAsync searchOptionsService;
    private final IconTableBase64 iconTableBase64;

    public ResultsPanel(final DataNodeTable dataNodeTable, SearchOptionsServiceAsync searchOptionsService, IconTableBase64 iconTableBase64) {
        this.dataNodeTable = dataNodeTable;
        this.searchOptionsService = searchOptionsService;
        this.iconTableBase64 = iconTableBase64;
    }

    public void addDatabaseTree(String databaseName, DataNodeId[] dataNodeIds) {
        final DataNodeTree dataNodeTree = new DataNodeTree(dataNodeTable, searchOptionsService, iconTableBase64);
        for (DataNodeId dataNodeId : dataNodeIds) {
            dataNodeTree.addResultsToTree(databaseName, dataNodeId);
        }
        this.add(dataNodeTree, databaseName);
        this.selectTab(this.getWidgetIndex(dataNodeTree));
    }

    public void addResultsTree(String databaseName, HighlighableDataNode dataNode) {
        try {
            if (dataNode.getChildIds() != null) {
                final DataNodeTree dataNodeTree = new DataNodeTree(dataNodeTable, searchOptionsService, iconTableBase64);
                for (DataNodeLink childId : dataNode.getChildIds()) {
                    dataNodeTree.addResultsToTree(databaseName, childId, dataNode);
                }
                this.addClosableTab(dataNodeTree, dataNode.getLabel());
                this.selectTab(this.getWidgetIndex(dataNodeTree));
            } else {
                final Label label = new Label("No results found");
                this.addClosableTab(label, dataNode.getLabel());
                this.selectTab(this.getWidgetIndex(label));
            }
        } catch (ModelException exception) {
            logger.log(Level.SEVERE, FAILURE, exception);
        }

    }

    private void addClosableTab(final Widget widget, String text) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        final Label label = new Label(text);
        Button button = new Button("X");
        button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                ResultsPanel.this.remove(widget);
            }
        });
        horizontalPanel.add(label);
        horizontalPanel.add(new HTML("&nbsp&nbsp&nbsp"));
        horizontalPanel.add(button);
        this.add(widget, horizontalPanel);
    }
}
