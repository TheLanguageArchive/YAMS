package nl.mpi.yams.client.controllers;

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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yams.client.DataNodeLoader;
import nl.mpi.yams.client.DataNodeLoaderJson;
import nl.mpi.yams.client.DataNodeLoaderRpc;
import nl.mpi.yams.client.DataNodeSearchListener;
import nl.mpi.yams.client.DatabaseInformation;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.client.ui.ResultsPanel;
import nl.mpi.yams.common.data.HighlightableDataNode;

/**
 * Created on : Feb 4, 2013, 11:07:09 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public abstract class SearchHandler implements ClickHandler, KeyUpHandler {

    protected static final Logger logger = Logger.getLogger("");
    protected final HistoryController historyController;
    boolean searchInProgress = false;
    protected final Object searchLockObject = new Object();
    protected final SearchOptionsServiceAsync searchOptionsService;
    protected final ResultsPanel resultsPanel;
    protected final DatabaseInformation databaseInfo;
    private DataNodeLoader dataNodeLoader;

    public SearchHandler(HistoryController historyController, DatabaseInformation databaseInfo, SearchOptionsServiceAsync searchOptionsService, ResultsPanel resultsPanel) {
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
        this.searchOptionsService = searchOptionsService;
        this.resultsPanel = resultsPanel;
    }

    public void updateDbName() {
        final String databaseName = historyController.getDatabaseName();
        if (searchOptionsService != null) {
            dataNodeLoader = new DataNodeLoaderRpc(searchOptionsService, databaseInfo.getDatabaseIcons(databaseName), databaseName);
        } else {
            dataNodeLoader = new DataNodeLoaderJson(databaseName);
        }
    }

    public void onClick(ClickEvent event) {
        initiateSearch();
    }

    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            initiateSearch();
        }
    }

    private void initiateSearch() {
//        logger.info("initiateSearch");
        synchronized (searchLockObject) {
            if (!searchInProgress) {
                searchInProgress = true;
//                logger.info("prepareSearch");
                prepareSearch();
//                logger.info("performSearch");
                if (searchOptionsService != null) {
                    performSearchRpc();
                } else {
                    performSearchJson();
                }
            }
        }
    }

    public void signalSearchDone() {
        searchInProgress = false;
    }

    protected abstract void prepareSearch();

    protected abstract void finaliseSearch();

//    protected abstract void performSearch();
    protected void performSearchJson() {
        logger.info("performSearchJson");
        final long startTime = System.currentTimeMillis();
        dataNodeLoader.performSearch(historyController.getDatabaseName(), historyController.getCriterionJoinType(), historyController.getSearchParametersList(), new DataNodeSearchListener() {

            public void dataNodeLoaded(List<HighlightableDataNode> dataNodeList) {
                long responseMils = System.currentTimeMillis() - startTime;
                final String searchTimeMessage = "PerformSearch response time: " + responseMils + " ms";
                logger.log(Level.INFO, searchTimeMessage);
                final String databaseName = historyController.getDatabaseName();
                for (HighlightableDataNode result : dataNodeList) {
                    resultsPanel.addResultsTree(databaseName, databaseInfo.getDatabaseIcons(databaseName), result, responseMils);
                }
                signalSearchDone();
                finaliseSearch();
                historyController.updateHistory(false);
            }

            public void dataNodeLoadFailed(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
                signalSearchDone();
                finaliseSearch();
            }
        });
    }

    protected void performSearchRpc() {
        logger.info("performSearchRpc");
        final long startTime = System.currentTimeMillis();
        searchOptionsService.performSearch(historyController.getDatabaseName(), historyController.getCriterionJoinType(), historyController.getSearchParametersList(), new AsyncCallback<HighlightableDataNode>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
                signalSearchDone();
                finaliseSearch();
            }

            public void onSuccess(HighlightableDataNode result) {
                long responseMils = System.currentTimeMillis() - startTime;
                final String searchTimeMessage = "PerformSearch response time: " + responseMils + " ms";
                logger.log(Level.INFO, searchTimeMessage);
                final String databaseName = historyController.getDatabaseName();
                resultsPanel.addResultsTree(databaseName, databaseInfo.getDatabaseIcons(databaseName), result, responseMils);
                signalSearchDone();
                finaliseSearch();
                historyController.updateHistory(false);
            }
        });
    }
}
