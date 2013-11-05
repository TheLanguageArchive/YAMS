package nl.mpi.yaas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
//import com.googlecode.gwtphonegap.client.PhoneGap;
//import com.googlecode.gwtphonegap.client.util.PhonegapUtil;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.db.DataBaseManager;

public class yaas implements EntryPoint, DatabaseNameListener {

    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_CONNECT_TO_THE_SERVER = "Failed to connect to the server.";
    private static final String NO__DATABASE__SELECTED = "No Database Selected";
    private SearchOptionsServiceAsync searchOptionsService;
//    final PhoneGap phoneGap = GWT.create(PhoneGap.class);
    private boolean debugMode = false;
    private final FlowPanel loggerPanel = new FlowPanel();

    public void onModuleLoad() {
        searchOptionsService = GWT.create(SearchOptionsService.class);
        String databaseName = com.google.gwt.user.client.Window.Location.getParameter("databaseName");
        if (databaseName == null) {
            databaseName = DataBaseManager.defaultDataBase;
        }
        setupPage(databaseName);
        logger.addHandler(new HasWidgetsLogHandler(loggerPanel));
        RootPanel.get("loggerPanel").add(loggerPanel);
    }

    private void setupPage(final String databaseName) {
//        final String moduleBaseURL = "http://tlatest03.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT/yaas/";
        final String dbStatsHref = (databaseName == null || DatabaseSelect.PLEASE_SELECT_A_DATABASE.equals(databaseName)) ? "DatabaseStats.jsp" : "DatabaseStats.jsp?databaseName=" + databaseName;
        RootPanel.get("linksPanel").add(new Anchor("View Database Statistics", dbStatsHref));
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
        final DatabaseSelect databaseSelectBox = new DatabaseSelect(searchOptionsService, databaseName, this);
        RootPanel.get("searchOptionsPanel").add(databaseSelectBox);
        databaseSelectBox.getDbList();
        final ServiceDefTarget serviceDefTarget = (ServiceDefTarget) searchOptionsService;
        RootPanel.get("databaseStats").add(new Label(serviceDefTarget.getServiceEntryPoint()));
        if (GWT.getHostPageBaseURL().startsWith("file://")) {
            RootPanel.get("databaseStats").add(new Label("Changing Service Target"));
            final String baseUrl = GWT.getModuleBaseURL().replace("file:///android_asset/www", "http://tlatest06.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT");
            serviceDefTarget.setServiceEntryPoint(baseUrl + serviceDefTarget.getServiceEntryPoint().replace(GWT.getModuleBaseURL(), ""));
            serviceDefTarget.setRpcRequestBuilder(new RpcRequestBuilder() {
                @Override
                protected void doFinish(RequestBuilder requestBuilder) {
                    super.doFinish(requestBuilder);
                    requestBuilder.setHeader(MODULE_BASE_HEADER, baseUrl);
                }
            });
            RootPanel.get("databaseStats").add(new Label(GWT.getModuleBaseURL()));
            RootPanel.get("databaseStats").add(new Label(serviceDefTarget.getServiceEntryPoint()));
        }
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
        if (databaseName != null) {
            searchOptionsService.getImageDataForTypes(databaseName, new AsyncCallback<IconTableBase64>() {
                public void onFailure(Throwable caught) {
                    RootPanel.get("searchOptionsPanel").add(new Label(NO__DATABASE__SELECTED));
                }

                public void onSuccess(IconTableBase64 result) {
                    final DataNodeTable dataNodeTable = new DataNodeTable();
                    final DataNodeTree dataNodeTree = new DataNodeTree(dataNodeTable, searchOptionsService, result);
                    final SearchPanel searchOptionsPanel = new SearchPanel(searchOptionsService, databaseName, dataNodeTree, dataNodeTable);
                    RootPanel.get("databaseStats").add(new DatabaseStatsPanel(searchOptionsService, databaseName, dataNodeTree, databaseSelectBox));
                    RootPanel.get("databaseStats").add(new IconInfoPanel(result));
//                RootPanel.get("databaseStats").add(new QueryStatsPanel());
                    RootPanel.get("searchOptionsPanel").add(searchOptionsPanel);
                    RootPanel.get("dataNodeTree").add(dataNodeTree);
                    RootPanel.get("dataNodeTable").add(dataNodeTable);
                    RootPanel.get("facetedTree").add(new FacetedTree(searchOptionsService, databaseName));
                }
            });
        }
    }

    private void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        RootPanel.get("databaseStats").setVisible(debugMode);
        RootPanel.get("facetedTree").setVisible(debugMode);
    }

    public void setDataBaseName(String databaseName) {
        RootPanel.get("databaseStats").clear();
        RootPanel.get("searchOptionsPanel").clear();
        RootPanel.get("dataNodeTree").clear();
        RootPanel.get("dataNodeTable").clear();
        RootPanel.get("facetedTree").clear();
        RootPanel.get("linksPanel").clear();
        RootPanel.get("optionsPanel").clear();
        setupPage(databaseName);
    }
}
