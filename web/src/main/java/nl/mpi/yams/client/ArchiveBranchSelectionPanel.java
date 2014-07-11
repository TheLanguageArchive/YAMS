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
package nl.mpi.yams.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.DatabaseStats;
import nl.mpi.yams.common.data.IconTableBase64;

/**
 * @since Mar 25, 2014 2:57:45 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ArchiveBranchSelectionPanel extends HorizontalPanel implements HistoryListener {

    private String dataNodeTreeDb = null;
    private DataNodeTree dataNodeTree = null;
    private static final Logger logger = Logger.getLogger("");
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;
    private final DatabaseInfo databaseInfo;
    private final PopupPanel popupPanel = new PopupPanel(true);
    private final VerticalPanel selectedBranchesPanel = new VerticalPanel();
    HashMap<SerialisableDataNode, HorizontalPanel> nodePanels = new HashMap<SerialisableDataNode, HorizontalPanel>();
    private final ArrayList<HorizontalPanel> rootNodePanels = new ArrayList<HorizontalPanel>();
    private final List<String> windowParamHdls;
    private final List<String> windowParamUrls;

    public ArchiveBranchSelectionPanel(SearchOptionsServiceAsync searchOptionsService, HistoryController historyController, DatabaseInfo databaseInfo, List<String> windowParamHdls, List<String> windowParamUrls) {
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
        this.windowParamHdls = windowParamHdls;
        this.windowParamUrls = windowParamUrls;
        this.add(selectedBranchesPanel);
        final Button browseButton = new Button("Browse");
        browseButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (dataNodeTree != null) {
                    popupPanel.showRelativeTo(browseButton);
                }
            }
        });
        this.add(browseButton);
    }

    public void historyChange() {
        userSelectionChange();
    }

    public void userSelectionChange() {
        final String databaseName = historyController.getDatabaseName();
        if (dataNodeTreeDb == null || !dataNodeTreeDb.equals(databaseName)) {
            if (dataNodeTree != null) {
                popupPanel.remove(dataNodeTree);
                dataNodeTree = null;
                selectedBranchesPanel.clear();
            }
//            logger.info("ArchiveTreePanel");
//            logger.info(dataNodeTreeDb);
            final DatabaseStats databaseStats = databaseInfo.getDatabaseStats(databaseName);
            final IconTableBase64 databaseIcons = databaseInfo.getDatabaseIcons(databaseName);
            if (databaseStats != null && databaseIcons != null && databaseStats.getRootDocumentsIDs() != null) {
                addDatabaseTree(databaseName, databaseStats.getRootDocumentsIDs(), databaseIcons);
            }
        }
    }

    public void addDatabaseTree(String databaseName, DataNodeId[] dataNodeIds, IconTableBase64 databaseIcons) {
//        logger.info("addDatabaseTree");
        // todo: move this db tree to a node select in the search criterior panel
        // todo: this could end up being a threading issue with iconTableBase64 being set from the wrong database
        //logger.info(databaseName);
        dataNodeTreeDb = databaseName;
        if (databaseName != null) {
            final DataNodeLoader dataNodeLoader = new DataNodeLoaderRpc(searchOptionsService, databaseIcons, databaseName);
//            final DataNodeLoader dataNodeLoader = new DataNodeLoaderJson(databaseName);
            final DataNodeLoaderListener dataNodeLoaderListener = new DataNodeLoaderListener() {

                public void dataNodeLoaded(List<SerialisableDataNode> dataNodeList) {
//                    logger.info("addDatabaseTree:dataNodeLoaded");
                    for (SerialisableDataNode dataNode : dataNodeList) {
                        rootNodePanels.add(addRootNode(dataNodeLoader, dataNode));
                    }
                }

                public void dataNodeLoadFailed(Throwable caught) {
                    logger.log(Level.WARNING, "addDatabaseTree:dataNodeLoadFailed", caught);
                    ArchiveBranchSelectionPanel.this.add(new Label("Failed to load"));
                }
            };
            if (!windowParamHdls.isEmpty()) {
//                logger.info("windowParamHdls");
                dataNodeLoader.requestLoadHdl(windowParamHdls, dataNodeLoaderListener);
            } else if (!windowParamUrls.isEmpty()) {
//                logger.info("windowParamUrls");
                dataNodeLoader.requestLoadUri(windowParamUrls, dataNodeLoaderListener);
            } else {
                dataNodeLoader.requestLoad(Arrays.asList(dataNodeIds), dataNodeLoaderListener);
            }

            dataNodeTree = new DataNodeTree(new TreeNodeCheckboxListener() {

                public void stateChanged(boolean selected, SerialisableDataNode dataNode, CheckBox checkBox) {
                    if (selected) {
                        addSearchBranch(dataNodeLoader, dataNode, checkBox);
                    } else {
                        removeSearchBranch(dataNode);
                    }
                }
            }, null, searchOptionsService, databaseIcons, true);
            dataNodeTree.addResultsToTree(databaseName, dataNodeIds, true);
            popupPanel.add(dataNodeTree);
        }
    }

    private void addRootNodePanels() {
        for (HorizontalPanel panel : rootNodePanels) {
            selectedBranchesPanel.add(panel);
        }
    }

    private void removeRootNodePanels() {
        for (HorizontalPanel panel : rootNodePanels) {
            selectedBranchesPanel.remove(panel);
        }

    }

    private void removeSearchBranch(SerialisableDataNode dataNode) {
        final HorizontalPanel nodePanel = nodePanels.remove(dataNode);
        if (nodePanel != null) {
            selectedBranchesPanel.remove(nodePanel);
        }
        if (nodePanels.isEmpty()) {
            addRootNodePanels();
        }
    }

    private HorizontalPanel addRootNode(DataNodeLoader dataNodeLoader, SerialisableDataNode dataNode) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        Image iconImage1 = new Image();
        Image iconImage2 = new Image();
        final DataNodePermissions.AccessLevel accessLevel = dataNode.getPermissions().getAccessLevel();
        if (accessLevel != null) {
            iconImage1.setStyleName("access_level_" + accessLevel.name());
        }
        iconImage2.setStyleName("format_" + dataNode.getType().getFormat().name());
        horizontalPanel.add(new Image(dataNodeLoader.getNodeIcon(dataNode)));
        horizontalPanel.add(iconImage1);
        horizontalPanel.add(iconImage2);
        horizontalPanel.add(new Label(dataNode.getLabel()));
        selectedBranchesPanel.add(horizontalPanel);
        return horizontalPanel;
    }

    private void addSearchBranch(DataNodeLoader dataNodeLoader, final SerialisableDataNode dataNode, final CheckBox checkBox) {
        if (nodePanels.isEmpty()) {
            removeRootNodePanels();
        }
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Button("x", new ClickHandler() {

            public void onClick(ClickEvent event) {
                removeSearchBranch(dataNode);
                checkBox.setValue(false);
            }
        }));
        horizontalPanel.add(new Image(dataNodeLoader.getNodeIcon(dataNode)));
        horizontalPanel.add(new Label(dataNode.getLabel()));
        selectedBranchesPanel.add(horizontalPanel);
        nodePanels.put(dataNode, horizontalPanel);
    }
}
