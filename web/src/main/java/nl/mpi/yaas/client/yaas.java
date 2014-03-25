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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
//import com.googlecode.gwtphonegap.client.PhoneGap;
//import com.googlecode.gwtphonegap.client.util.PhonegapUtil;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.IconTableBase64;

public class yaas implements EntryPoint, HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_CONNECT_TO_THE_SERVER = "Failed to connect to the server.";
    private static final String NO__DATABASE__SELECTED = "No Database Selected";
    private SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController = new HistoryController();
//    final PhoneGap phoneGap = GWT.create(PhoneGap.class);
    private boolean debugMode = false;
    private final FlowPanel loggerPanel = new FlowPanel();
    private SearchPanel searchOptionsPanel;
    private IconInfoPanel iconInfoPanel;
    private ResultsPanel resultsPanel;
    final Label noDatabaseLabel = new Label(NO__DATABASE__SELECTED);
    final private Image loadingImage = new Image("./loader.gif");
    private String lastUsedDatabase = null;
    private Anchor statisticsPageAnchor;

    public void onModuleLoad() {
        searchOptionsService = GWT.create(SearchOptionsService.class);
        String searchHandle = com.google.gwt.user.client.Window.Location.getParameter("hdl");
        if (searchHandle != null) {
            historyController.addSearchHandle(searchHandle);
        }
        setupPage(historyController);
        History.addValueChangeHandler(historyController);
        logger.addHandler(new HasWidgetsLogHandler(loggerPanel));
        RootPanel.get("loggerPanel").add(loggerPanel);
        History.fireCurrentHistoryState();
    }

    private void setStatisticsLink(String databaseName) {
        final String dbStatsHref = (databaseName == null || databaseName.isEmpty()) ? "DatabaseStats.jsp" : "DatabaseStats.jsp?databaseName=" + databaseName;
        statisticsPageAnchor.setHref(dbStatsHref);
    }

    private void setupPage(final HistoryController historyController) {
//        final String moduleBaseURL = "http://tlatest03.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT/yaas/";
        final String databaseName = historyController.getDatabaseName();
        statisticsPageAnchor = new Anchor("View Database Statistics");
        setStatisticsLink(databaseName);
        RootPanel.get("linksPanel").add(statisticsPageAnchor);
        CheckBox debugCheckBox = new CheckBox("debug");
        debugCheckBox.setValue(debugMode);
        RootPanel.get("optionsPanel").add(debugCheckBox);
        debugCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setDebugMode(event.getValue());
            }
        });
        setDebugMode(debugMode);
        RootPanel.get("databaseStats").add(new Label(GWT.getModuleBaseURL()));
        DisclosurePanel disclosurePanel = new DisclosurePanel("Search Options");
        final DatabaseSelect databaseSelectBox = new DatabaseSelect(searchOptionsService, historyController);
        historyController.addHistoryListener(this);
        historyController.addHistoryListener(databaseSelectBox);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(databaseSelectBox);
        final DataNodeTable dataNodeTable = new DataNodeTable();
        resultsPanel = new ResultsPanel(dataNodeTable, searchOptionsService, historyController);
        final ConciseSearchBox conciseSearchBox = new ConciseSearchBox(searchOptionsService, historyController, resultsPanel);
        historyController.addHistoryListener(conciseSearchBox);
        RootPanel.get("searchOptionsPanel").add(conciseSearchBox);
        databaseSelectBox.getDbList();
        final ServiceDefTarget serviceDefTarget = (ServiceDefTarget) searchOptionsService;
        RootPanel.get("databaseStats").add(new Label(serviceDefTarget.getServiceEntryPoint()));
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
        searchOptionsPanel = new SearchPanel(searchOptionsService, historyController, resultsPanel, dataNodeTable);
        historyController.addHistoryListener(searchOptionsPanel);
        searchOptionsPanel.setVisible(false);
        loadingImage.setVisible(false);
        final DatabaseStatsPanel databaseStatsPanel = new DatabaseStatsPanel(searchOptionsService, resultsPanel, databaseSelectBox, historyController);
        historyController.addHistoryListener(databaseStatsPanel);
        RootPanel.get("databaseStats").add(databaseStatsPanel);
        RootPanel.get("databaseStats").add(iconInfoPanel);
//                RootPanel.get("databaseStats").add(new QueryStatsPanel());
        verticalPanel.add(noDatabaseLabel);
        RootPanel.get("searchOptionsPanel").add(loadingImage);
        verticalPanel.add(searchOptionsPanel);
        disclosurePanel.setContent(verticalPanel);
        RootPanel.get("searchOptionsPanel").add(disclosurePanel);
        RootPanel.get("resultsPanel").add(resultsPanel);
//        RootPanel.get("dataNodeTable").add(dataNodeTable);
        RootPanel.get("facetedTree").add(new FacetedTree(searchOptionsService, databaseName));
    }

    private void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        RootPanel.get("databaseStats").setVisible(debugMode);
        RootPanel.get("facetedTree").setVisible(debugMode);
        RootPanel.get("loggerPanel").setVisible(debugMode);
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
            searchOptionsService.getImageDataForTypes(databaseName, new AsyncCallback<IconTableBase64>() {
                public void onFailure(Throwable caught) {
                    noDatabaseLabel.setVisible(true);
                    loadingImage.setVisible(false);
                    iconInfoPanel.setIconInfo(null);
                }

                public void onSuccess(IconTableBase64 result) {
                    resultsPanel.setIconTableBase64(result);
                    noDatabaseLabel.setVisible(false);
                    loadingImage.setVisible(false);
                    searchOptionsPanel.setVisible(true);
                    iconInfoPanel.setIconInfo(result);
                }
            });
        }
    }

    public void userSelectionChange() {
        // nothing needs to be done in this class
    }
}
