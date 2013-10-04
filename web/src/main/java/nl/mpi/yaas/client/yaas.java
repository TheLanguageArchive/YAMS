package nl.mpi.yaas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
//import com.googlecode.gwtphonegap.client.PhoneGap;
//import com.googlecode.gwtphonegap.client.util.PhonegapUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.db.DataBaseManager;

public class yaas implements EntryPoint, DatabaseNameListener {

    private static final Logger logger = Logger.getLogger("");
    private static final String FAILED_TO_CONNECT_TO_THE_SERVER = "Failed to connect to the server.";
    private SearchOptionsServiceAsync searchOptionsService;
//    final PhoneGap phoneGap = GWT.create(PhoneGap.class);

    public void onModuleLoad() {
        searchOptionsService = GWT.create(SearchOptionsService.class);
        String databaseName = com.google.gwt.user.client.Window.Location.getParameter("databaseName");
        if (databaseName == null) {
            databaseName = DataBaseManager.defaultDataBase;
        }
        setupPage(databaseName);
    }

    private void setupPage(final String databaseName) {
//        final String moduleBaseURL = "http://tlatest03.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT/yaas/";
        final String dbStatsHref = (databaseName == null) ? "DatabaseStats.jsp" : "DatabaseStats.jsp?databaseName=" + databaseName;
        RootPanel.get("databaseStats").add(new Anchor("View Database Statistics", dbStatsHref));
        RootPanel.get("databaseStats").add(new Label(GWT.getModuleBaseURL()));
        ServiceDefTarget serviceDefTarget = (ServiceDefTarget) searchOptionsService;
        RootPanel.get("databaseStats").add(new Label(serviceDefTarget.getServiceEntryPoint()));
//        serviceDefTarget.setServiceEntryPoint("http://tlatest03.mpi.nl:8080/yaas-gwt-1.0-SNAPSHOT/yaas/searchoptions");
//        RootPanel.get("databaseStats").add(new Label(serviceDefTarget.getServiceEntryPoint()));
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
                    RootPanel.get("searchOptionsPanel").add(new Label(FAILED_TO_CONNECT_TO_THE_SERVER));
                    logger.log(Level.SEVERE, "setupPage", caught);
                }

                public void onSuccess(IconTableBase64 result) {
                    final DataNodeTable dataNodeTable = new DataNodeTable();
                    final DataNodeTree dataNodeTree = new DataNodeTree(dataNodeTable, searchOptionsService, result);
                    final SearchPanel searchOptionsPanel = new SearchPanel(searchOptionsService, databaseName, dataNodeTree, dataNodeTable);
                    RootPanel.get("databaseStats").add(new DatabaseStatsPanel(searchOptionsService, databaseName, dataNodeTree, yaas.this));
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

    public void setDataBaseName(String databaseName) {
        RootPanel.get("databaseStats").clear();
        RootPanel.get("searchOptionsPanel").clear();
        RootPanel.get("dataNodeTree").clear();
        RootPanel.get("dataNodeTable").clear();
        RootPanel.get("facetedTree").clear();
        setupPage(databaseName);
    }
}
