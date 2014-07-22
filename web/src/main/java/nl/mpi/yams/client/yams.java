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

public class yams implements EntryPoint {

    private static final Logger logger = Logger.getLogger("");
    private SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController = new HistoryController();
    private DatabaseInformation databaseInfo;
//    final PhoneGap phoneGap = GWT.create(PhoneGap.class);
    private boolean debugMode = false;
    private final FlowPanel loggerPanel = new FlowPanel();
    private final List<String> windowParamHdls = new ArrayList<String>();
    private final List<String> windowParamUrls = new ArrayList<String>();

    public void onModuleLoad() {
        searchOptionsService = GWT.create(SearchOptionsService.class);
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
//        final String moduleBaseURL = "http://tlatest03.mpi.nl:8080/yams-gwt-1.0-SNAPSHOT/yams/";
        final String databaseName = historyController.getDatabaseName();
        databaseInfo = new DatabaseInformation(searchOptionsService, historyController);
        final RootPanel linksPanelTag = RootPanel.get("linksPanel");
        if (linksPanelTag != null) {
            final StatisticsLink statisticsLink = new StatisticsLink(historyController);
            linksPanelTag.add(statisticsLink);
            historyController.addHistoryListener(statisticsLink);
        }
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
        setDebugMode(debugMode);
        final RootPanel databaseStatsTag = RootPanel.get("databaseStats");
        if (databaseStatsTag != null) {
            databaseStatsTag.add(new Label(GWT.getModuleBaseURL()));
        }
        final DataNodeTable dataNodeTable = new DataNodeTable();
        final RootPanel searchOptionsPanelTag = RootPanel.get("searchOptionsPanel");
        if (searchOptionsPanelTag != null) {
            ResultsPanel resultsPanel = new ResultsPanel(dataNodeTable, searchOptionsService, historyController);
            final RootPanel resultsPanelTag = RootPanel.get("resultsPanel");
            if (resultsPanelTag != null) {
                resultsPanelTag.add(resultsPanel);
            }
            final ConciseSearchBox conciseSearchBox = new ConciseSearchBox(searchOptionsService, historyController, databaseInfo, resultsPanel);
            historyController.addHistoryListener(conciseSearchBox);
            searchOptionsPanelTag.add(conciseSearchBox);
            final ArchiveBranchSelectionPanel archiveBranchSelectionPanel = new ArchiveBranchSelectionPanel(searchOptionsService, historyController, databaseInfo, windowParamHdls, windowParamUrls);
            searchOptionsPanelTag.add(new SearchPanel(historyController, databaseInfo, resultsPanel, searchOptionsService, dataNodeTable, archiveBranchSelectionPanel));
        }
        if (databaseStatsTag != null) {
            final ServiceDefTarget serviceDefTarget = (ServiceDefTarget) searchOptionsService;
            databaseStatsTag.add(new Label(serviceDefTarget.getServiceEntryPoint()));
        }
//        if (GWT.getHostPageBaseURL().startsWith("file://")) {
//            RootPanel.get("databaseStats").add(new Label("Changing Service Target"));
//            final String baseUrl = GWT.getModuleBaseURL().replace("file:///android_asset/www", "http://lux17.mpi.nl/ds/yams2");
//            serviceDefTarget.setServiceEntryPoint(baseUrl + serviceDefTarget.getServiceEntryPoint().replace(GWT.getModuleBaseURL(), ""));
//            serviceDefTarget.setRpcRequestBuilder(new RpcRequestBuilder() {
//                @Override
//                protected void doFinish(RequestBuilder requestBuilder) {
//                    super.doFinish(requestBuilder);
//                    requestBuilder.setHeader(MODULE_BASE_HEADER, baseUrl);
//                }
//            });
//            RootPanel.get("databaseStats").add(new Label(GWT.getModuleBaseURL()));
//            RootPanel.get("databaseStats").add(new Label(serviceDefTarget.getServiceEntryPoint()));
//
//            PhonegapUtil.prepareService(serviceDefTarget, "http://lux17.mpi.nl/", "ds/yams/yams/searchoptions");
////  HandlerManager eventBus = new HandlerManager(null);
////  AppController appViewer = new AppController(rpcService, eventBus);
////  appViewer.go(RootPanel.get());
//        }
        //        serviceDefTarget.setServiceEntryPoint("http://tlatest03.mpi.nl:8080/yams-gwt-1.0-SNAPSHOT/yams/searchoptions");
////        serviceDefTarget.setServiceEntryPoint(moduleUrl + relativeServiceUrl);
//
//        serviceDefTarget.setRpcRequestBuilder(new RpcRequestBuilder() {
//            @Override
//            protected void doFinish(RequestBuilder rb) {
//                super.doFinish(rb);
//
//                rb.setHeader(MODULE_BASE_HEADER, moduleBaseURL);
//            }
//        });
//        PhonegapUtil.prepareService(serviceDefTarget, moduleBaseURL, "searchoptions");
        if (databaseStatsTag != null) {
            IconInfoPanel iconInfoPanel = new IconInfoPanel(historyController, databaseInfo);
            final DatabaseStatsPanel databaseStatsPanel = new DatabaseStatsPanel(databaseInfo, historyController);
            historyController.addHistoryListener(databaseStatsPanel);
            databaseStatsTag.add(databaseStatsPanel);
            databaseStatsTag.add(iconInfoPanel);
            historyController.addHistoryListener(iconInfoPanel);
        }

//                RootPanel.get("databaseStats").add(new QueryStatsPanel());
//        if (searchOptionsPanelTag != null) {
//            searchOptionsPanelTag.add(loadingImage);
//        }
        final RootPanel facetedTreeTag = RootPanel.get("facetedTree");
//        RootPanel.get("dataNodeTable").add(dataNodeTable);
        if (facetedTreeTag != null) {
            final FacetedTree facetedTree = new FacetedTree(searchOptionsService, historyController);
            facetedTreeTag.add(facetedTree);
            historyController.addHistoryListener(facetedTree);
        }
        databaseInfo.getDbInfo();
        ActionsPanelController actionsPanelController = new ActionsPanelController(databaseInfo, searchOptionsService,
                historyController,
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
        final RootPanel corpusTreePanelTag = RootPanel.get("corpusTreePanel");
        final RootPanel corpusTreeCsDbPanelTag = RootPanel.get("corpusTreeCsDbPanel");
        if (corpusTreePanelTag != null || corpusTreeCsDbPanelTag != null) {
            final RootPanel detailsPanelTag = RootPanel.get("detailsPanel");
            if (detailsPanelTag != null) {
                if (corpusTreePanelTag != null) {
                    final ArchiveTreePanel archiveTreePanel = new ArchiveTreePanel(dataNodeTable, searchOptionsService, historyController, databaseInfo, actionsPanelController, false);
                    corpusTreePanelTag.add(archiveTreePanel);
                    historyController.addHistoryListener(archiveTreePanel);
                }
                if (corpusTreeCsDbPanelTag != null) {
                    final ArchiveTreePanel archiveTreeCsDbPanel = new ArchiveTreePanel(dataNodeTable, searchOptionsService, historyController, databaseInfo, actionsPanelController, true);
                    corpusTreeCsDbPanelTag.add(archiveTreeCsDbPanel);
                    historyController.addHistoryListener(archiveTreeCsDbPanel);
                }
                historyController.addHistoryListener(actionsPanelController);
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
