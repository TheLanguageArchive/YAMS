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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.List;
//import com.googlecode.gwtphonegap.client.PhoneGap;
//import com.googlecode.gwtphonegap.client.util.PhonegapUtil;
import java.util.logging.Logger;

public class yaas implements EntryPoint, HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_CONNECT_TO_THE_SERVER = "Failed to connect to the server.";
    private static final String NO__DATABASE__SELECTED = "No Database Selected";
    private SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController = new HistoryController();
    private DatabaseInfo databaseInfo;
//    final PhoneGap phoneGap = GWT.create(PhoneGap.class);
    private boolean debugMode = false;
    private final FlowPanel loggerPanel = new FlowPanel();
    private SearchPanel searchOptionsPanel;
    private IconInfoPanel iconInfoPanel;
    private ResultsPanel resultsPanel;
    final Label noDatabaseLabel = new Label(NO__DATABASE__SELECTED);
    final private Image loadingImage = new Image("./loader.gif");
    private String lastUsedDatabase = null;
    private Anchor statisticsPageAnchor = null;
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

    private void setStatisticsLink(String databaseName) {
        if (statisticsPageAnchor != null) {
            final String dbStatsHref = (databaseName == null || databaseName.isEmpty()) ? "DatabaseStats.jsp" : "DatabaseStats.jsp?databaseName=" + databaseName;
            statisticsPageAnchor.setHref(dbStatsHref);
        }
    }

    private void setupPage(final HistoryController historyController) {
//        final String moduleBaseURL = "http://tlatest03.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT/yaas/";
        final String databaseName = historyController.getDatabaseName();
        databaseInfo = new DatabaseInfo(searchOptionsService, historyController);
        setStatisticsLink(databaseName);
        final RootPanel linksPanelTag = RootPanel.get("linksPanel");
        if (linksPanelTag != null) {
            statisticsPageAnchor = new Anchor("View Database Statistics");
            linksPanelTag.add(statisticsPageAnchor);
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
        DisclosurePanel disclosurePanel = new DisclosurePanel("Search Options");
        final DatabaseSelect databaseSelectBox = new DatabaseSelect(historyController, databaseInfo);
        historyController.addHistoryListener(this);
        historyController.addHistoryListener(databaseSelectBox);
        final DataNodeTable dataNodeTable = new DataNodeTable();
        final RootPanel searchOptionsPanelTag = RootPanel.get("searchOptionsPanel");
        if (searchOptionsPanelTag != null) {
            resultsPanel = new ResultsPanel(dataNodeTable, searchOptionsService, historyController);
            final ConciseSearchBox conciseSearchBox = new ConciseSearchBox(searchOptionsService, historyController, databaseInfo, resultsPanel);
            historyController.addHistoryListener(conciseSearchBox);
            searchOptionsPanelTag.add(conciseSearchBox);
        }
        if (databaseStatsTag != null) {
            final ServiceDefTarget serviceDefTarget = (ServiceDefTarget) searchOptionsService;
            databaseStatsTag.add(new Label(serviceDefTarget.getServiceEntryPoint()));
        }
//        if (GWT.getHostPageBaseURL().startsWith("file://")) {
//            RootPanel.get("databaseStats").add(new Label("Changing Service Target"));
//            final String baseUrl = GWT.getModuleBaseURL().replace("file:///android_asset/www", "http://lux17.mpi.nl/ds/yaas2");
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
//            PhonegapUtil.prepareService(serviceDefTarget, "http://lux17.mpi.nl/", "ds/yaas/yaas/searchoptions");
////  HandlerManager eventBus = new HandlerManager(null);
////  AppController appViewer = new AppController(rpcService, eventBus);
////  appViewer.go(RootPanel.get());
//        }
        //        serviceDefTarget.setServiceEntryPoint("http://tlatest03.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT/yaas/searchoptions");
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
        iconInfoPanel = new IconInfoPanel();
        final ArchiveBranchSelectionPanel archiveBranchSelectionPanel = new ArchiveBranchSelectionPanel(dataNodeTable, searchOptionsService, historyController, databaseInfo, windowParamHdls, windowParamUrls);
        searchOptionsPanel = new SearchPanel(searchOptionsService, historyController, databaseInfo, resultsPanel, dataNodeTable, archiveBranchSelectionPanel);
        historyController.addHistoryListener(searchOptionsPanel);
        searchOptionsPanel.setVisible(false);
        loadingImage.setVisible(false);
        if (databaseStatsTag != null) {
            final DatabaseStatsPanel databaseStatsPanel = new DatabaseStatsPanel(databaseInfo, historyController);
            historyController.addHistoryListener(databaseStatsPanel);
            databaseStatsTag.add(databaseStatsPanel);
            databaseStatsTag.add(iconInfoPanel);
        }

//                RootPanel.get("databaseStats").add(new QueryStatsPanel());
        if (searchOptionsPanelTag != null) {
            searchOptionsPanelTag.add(loadingImage);
        }
        if (searchOptionsPanelTag != null) {
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.add(databaseSelectBox);
            verticalPanel.add(noDatabaseLabel);
            verticalPanel.add(searchOptionsPanel);
            disclosurePanel.setContent(verticalPanel);
            searchOptionsPanelTag.add(disclosurePanel);
        }
        final RootPanel resultsPanelTag = RootPanel.get("resultsPanel");
        if (resultsPanelTag != null) {
            resultsPanelTag.add(resultsPanel);
        }
        final RootPanel facetedTreeTag = RootPanel.get("facetedTree");
//        RootPanel.get("dataNodeTable").add(dataNodeTable);
        if (facetedTreeTag != null) {
            facetedTreeTag.add(new FacetedTree(searchOptionsService, databaseName));
        }
        databaseInfo.getDbInfo();
        ActionsPanelController actionsPanelController = new ActionsPanelController(
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
                RootPanel.get("versionInfoTag"));

        final RootPanel corpusTreePanelTag = RootPanel.get("corpusTreePanel");
        if (corpusTreePanelTag != null) {
            final RootPanel detailsPanelTag = RootPanel.get("detailsPanel");
            if (detailsPanelTag != null) {
                final MetadataDetailsPanel metadataDetailsPanel = new MetadataDetailsPanel();
                detailsPanelTag.add(metadataDetailsPanel);
                final ArchiveTreePanel archiveTreePanel = new ArchiveTreePanel(dataNodeTable, searchOptionsService, historyController, databaseInfo, metadataDetailsPanel, actionsPanelController);
                corpusTreePanelTag.add(archiveTreePanel);
                historyController.addHistoryListener(archiveTreePanel);
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
        final RootPanel facetedTreeTag = RootPanel.get("facetedTree");
        if (facetedTreeTag != null) {
            facetedTreeTag.setVisible(debugMode);
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
    public void historyChange() {
        final String databaseName = historyController.getDatabaseName();
        setStatisticsLink(databaseName);
        if (databaseName != null && !databaseName.equals(lastUsedDatabase)) {
            lastUsedDatabase = databaseName;
            loadingImage.setVisible(true);
            searchOptionsPanel.setVisible(false);
            noDatabaseLabel.setVisible(true);
        }
    }

    public void userSelectionChange() {
        if (databaseInfo.hasDatabaseError()) {
            noDatabaseLabel.setVisible(true);
            loadingImage.setVisible(false);
        }
        if (databaseInfo.ready()) {
            noDatabaseLabel.setVisible(false);
            loadingImage.setVisible(false);
            searchOptionsPanel.setVisible(true);
        }
        iconInfoPanel.setIconInfo(databaseInfo.getDatabaseIcons(historyController.getDatabaseName()));
    }
}
