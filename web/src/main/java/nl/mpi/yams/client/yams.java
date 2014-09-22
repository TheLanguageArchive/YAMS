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
package nl.mpi.yams.client;

import nl.mpi.yams.client.controllers.ActionsPanelController;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.client.ui.ArchiveBranchSelectionPanel;
import nl.mpi.yams.client.ui.DatabaseStatsPanel;
import nl.mpi.yams.client.ui.DataNodeTable;
import nl.mpi.yams.client.ui.ConciseSearchBox;
import nl.mpi.yams.client.ui.SearchPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.ArrayList;
import java.util.List;
//import com.googlecode.gwtphonegap.client.PhoneGap;
//import com.googlecode.gwtphonegap.client.util.PhonegapUtil;
import java.util.logging.Logger;
import nl.mpi.yams.client.ui.ArchiveTreePanel;
import nl.mpi.yams.client.ui.FacetedTree;
import nl.mpi.yams.client.ui.IconInfoPanel;
import nl.mpi.yams.client.ui.ResultsPanel;
import nl.mpi.yams.client.ui.StatisticsLink;

public class yams implements EntryPoint {

    private static final Logger logger = Logger.getLogger("");
    private SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController = new HistoryController();
    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);
    private DatabaseInformation databaseInfo;
//    final PhoneGap phoneGap = GWT.create(PhoneGap.class);
    private boolean debugMode = false;
    private final FlowPanel loggerPanel = new FlowPanel();
    private final List<String> windowParamHdls = new ArrayList<String>();
    private final List<String> windowParamUrls = new ArrayList<String>();

    public void onModuleLoad() {
        searchOptionsService = null;
//        searchOptionsService = GWT.create(SearchOptionsService.class);
        setSearchBranchFromWindowParameter();
        setupPage(historyController);
        History.addValueChangeHandler(historyController);
        logger.addHandler(new HasWidgetsLogHandler(loggerPanel));
        final RootPanel loggerPanelTag = RootPanel.get("loggerPanel");
        if (loggerPanelTag != null) {
            loggerPanelTag.add(loggerPanel);
        }
        History.fireCurrentHistoryState();
    }

    private void setSearchBranchFromWindowParameter() {
        final String searchBranchHandle = com.google.gwt.user.client.Window.Location.getParameter("hdl");
        if (searchBranchHandle != null) {
            windowParamHdls.add(searchBranchHandle);
        }
        String searchBranchUrl = com.google.gwt.user.client.Window.Location.getParameter("url");
        if (searchBranchUrl != null) {
            windowParamUrls.add(searchBranchUrl);
        }
    }

    private void setupPage(final HistoryController historyController) {
        setupLinksPanel(historyController);
        setupOptionsPanel();
        setDebugMode(debugMode);

        final DataNodeTable dataNodeTable = new DataNodeTable();
        setupStats();
        setupSearch(dataNodeTable, historyController);
        setupFacetedTree(historyController);

        databaseInfo = new DatabaseInformation(searchOptionsService, historyController);
        databaseInfo.collectDbInfo();

        ActionsPanelController actionsPanelController = new ActionsPanelController(databaseInfo, searchOptionsService,
                historyController,
                RootPanel.get("errorTargetPanel"),
                RootPanel.get("welcomePanel"),
                RootPanel.get("actionsTargetPanel"),
                RootPanel.get("detailsPanel"),
                RootPanel.get("homeLink"),
                RootPanel.get("metadataSearchTag"),
                RootPanel.get("annotationContentSearchTag"),
                RootPanel.get("manageAccessRightsTag"),
                RootPanel.get("resourceAccessTag"),
                RootPanel.get("citationTag"),
                RootPanel.get("aboutTag"),
                RootPanel.get("viewTag"),
                RootPanel.get("downloadTag"),
                RootPanel.get("versionInfoTag"),
                RootPanel.get("loginTag"),
                RootPanel.get("logoutTag"),
                RootPanel.get("userSpan"));

        setupCorpusTree(dataNodeTable, historyController, actionsPanelController);
    }

    private void setupLinksPanel(final HistoryController historyController1) {
        final RootPanel linksPanelTag = RootPanel.get("linksPanel");
        if (linksPanelTag != null) {
            final StatisticsLink statisticsLink = new StatisticsLink(historyController1);
            linksPanelTag.add(statisticsLink);
            historyController1.addHistoryListener(statisticsLink);
        }
    }
    
    private void setupOptionsPanel() {
        final RootPanel optionsPanelTag = RootPanel.get("optionsPanel");
        if (optionsPanelTag != null) {
            CheckBox debugCheckBox = new CheckBox("debug");
            debugCheckBox.setValue(debugMode);
            optionsPanelTag.add(debugCheckBox);
            debugCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    setDebugMode(event.getValue());
                }
            });
        }
    }
    
    private void setupSearch(final DataNodeTable dataNodeTable, final HistoryController histController) {
        final RootPanel searchOptionsPanelTag = RootPanel.get("searchOptionsPanel");
        if (searchOptionsPanelTag != null) {
            ResultsPanel resultsPanel = new ResultsPanel(dataNodeTable, searchOptionsService, histController, null);
            final RootPanel resultsPanelTag = RootPanel.get("resultsPanel");
            if (resultsPanelTag != null) {
                resultsPanelTag.add(resultsPanel);
            }
            final ConciseSearchBox conciseSearchBox = new ConciseSearchBox(searchOptionsService, histController, databaseInfo, resultsPanel);
            histController.addHistoryListener(conciseSearchBox);
            searchOptionsPanelTag.add(conciseSearchBox);
            final ArchiveBranchSelectionPanel archiveBranchSelectionPanel = new ArchiveBranchSelectionPanel(searchOptionsService, histController, databaseInfo, windowParamHdls, windowParamUrls);
            searchOptionsPanelTag.add(new SearchPanel(histController, databaseInfo, resultsPanel, searchOptionsService, dataNodeTable, archiveBranchSelectionPanel));
        }
    }

    private void setupStats() {
        final RootPanel databaseStatsTag = RootPanel.get("databaseStats");
        if (databaseStatsTag != null) {
            databaseStatsTag.add(new Label(GWT.getModuleBaseURL()));

            if (searchOptionsService != null) {
                final ServiceDefTarget serviceDefTarget = (ServiceDefTarget) searchOptionsService;
                databaseStatsTag.add(new Label("GWT RPS: " + serviceDefTarget.getServiceEntryPoint()));
            } else {
                databaseStatsTag.add(new Label("GWT RPS not available"));
            }
            databaseStatsTag.add(new Label("CsAdaptorUrl" + serviceLocations.jsonCsAdaptorUrl()));
            databaseStatsTag.add(new Label("BasexAdaptorUrl: " + serviceLocations.jsonBasexAdaptorUrl()));

            IconInfoPanel iconInfoPanel = new IconInfoPanel(historyController, databaseInfo);
            final DatabaseStatsPanel databaseStatsPanel = new DatabaseStatsPanel(databaseInfo, historyController);
            historyController.addHistoryListener(databaseStatsPanel);
            databaseStatsTag.add(databaseStatsPanel);
            databaseStatsTag.add(iconInfoPanel);
            historyController.addHistoryListener(iconInfoPanel);
        }
    }

    private void setupFacetedTree(final HistoryController histController) {
        final RootPanel facetedTreeTag = RootPanel.get("facetedTree");
//        RootPanel.get("dataNodeTable").add(dataNodeTable);
        if (facetedTreeTag != null) {
            final FacetedTree facetedTree = new FacetedTree(searchOptionsService, histController);
            facetedTreeTag.add(facetedTree);
            histController.addHistoryListener(facetedTree);
        }
    }

    private void setupCorpusTree(final DataNodeTable dataNodeTable, final HistoryController historyController1, ActionsPanelController actionsPanelController) {
        final RootPanel corpusTreePanelTag = RootPanel.get("corpusTreePanel");
        final RootPanel corpusTreeCsDbPanelTag = RootPanel.get("corpusTreeCsDbPanel");
        if (corpusTreePanelTag != null || corpusTreeCsDbPanelTag != null) {
            final RootPanel detailsPanelTag = RootPanel.get("detailsPanel");
            if (detailsPanelTag != null) {
                if (corpusTreePanelTag != null) {
                    final ArchiveTreePanel archiveTreePanel = new ArchiveTreePanel(dataNodeTable, searchOptionsService, historyController1, databaseInfo, actionsPanelController, false);
                    corpusTreePanelTag.add(archiveTreePanel);
                    historyController1.addHistoryListener(archiveTreePanel);
                }
                if (corpusTreeCsDbPanelTag != null) {
                    final ArchiveTreePanel archiveTreeCsDbPanel = new ArchiveTreePanel(dataNodeTable, searchOptionsService, historyController1, databaseInfo, actionsPanelController, true);
                    corpusTreeCsDbPanelTag.add(archiveTreeCsDbPanel);
                    historyController1.addHistoryListener(archiveTreeCsDbPanel);
                }
                historyController1.addHistoryListener(actionsPanelController);
            } else {
                logger.severe("Found corpusTreePanel but not detailsPanel, cannot not correclty show the tree.");
            }
        }
    }

    private void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        final RootPanel databaseStatsTag = RootPanel.get("databaseStats");
        if (databaseStatsTag != null) {
            databaseStatsTag.setVisible(debugMode);
        }
        final RootPanel loggerPanelTag = RootPanel.get("loggerPanel");
        if (loggerPanelTag != null) {
            loggerPanelTag.setVisible(debugMode);
        }
    }

//    public void setDataBaseName(String databaseName) {
//        RootPanel.get("databaseStats").clear();
//        RootPanel.get("searchOptionsPanel").clear();
//        RootPanel.get("resultsPanel").clear();
//        RootPanel.get("dataNodeTable").clear();
//        RootPanel.get("facetedTree").clear();
//        RootPanel.get("linksPanel").clear();
//        RootPanel.get("optionsPanel").clear();
//        setupPage(databaseName);
//    }
}
