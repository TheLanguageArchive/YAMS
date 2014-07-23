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

import nl.mpi.yams.client.controllers.HistoryController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.DatabaseInfo;
import nl.mpi.yams.common.data.DatabaseList;
import nl.mpi.yams.common.data.DatabaseStats;
import nl.mpi.yams.common.data.IconTableBase64;
import nl.mpi.yams.shared.JsonDatabaseList;

/**
 * @since Mar 25, 2014 3:07:25 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseInformation {

    private static final Logger logger = Logger.getLogger("");
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;
    private boolean databaseError = false; // todo: do we actually want an error state to be stored like this!
    private final HashMap<String, DatabaseStats> databaseStatsMap = new HashMap<String, DatabaseStats>();
    private final HashMap<String, IconTableBase64> iconTableBase64Map = new HashMap<String, IconTableBase64>();
    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);
    private int requestCounter = 0;

    public DatabaseInformation(HistoryController historyController) {
        this.searchOptionsService = null;
        this.historyController = historyController;
    }

    public DatabaseInformation(SearchOptionsServiceAsync searchOptionsService, HistoryController historyController) {
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
    }

    public void getDbInfo() {
        if (searchOptionsService != null) {
            getDbInfoRPC();
        } else {
            getDbInfoJson();
        }
    }

    public void getDbInfoRPC() {
        requestCounter++;
        searchOptionsService.getDatabaseList(new AsyncCallback<DatabaseList>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, "DatabaseInfo:getDbInfo", caught);
                ready();
                requestCounter--;
                logger.log(Level.SEVERE, caught.getMessage());
                databaseError = true;
                historyController.fireDatabaseInfoEvent();
            }

            public void onSuccess(DatabaseList result) {
                for (DatabaseInfo databaseInfo : result.getDatabaseInfos()) {
                    databaseStatsMap.put(databaseInfo.getDatabaseName(), databaseInfo.getDatabaseStats());
                }
//                logger.info("getDbInfo");
                ready();
                requestCounter--;
                if (databaseStatsMap.size() > 0) {
                    historyController.setDefaultDatabase();//databaseNames[0]
                }
                getDatabaseIcons();
                historyController.fireDatabaseInfoEvent();
            }
        });
    }

    public void getDbInfoJson() {
        requestCounter++;
        final String jsonDbinfoUrl = serviceLocations.jsonDbInfoListUrl(serviceLocations.jsonBasexAdaptorUrl());
        // Send request to server and catch any errors.
//        logger.info(jsonDbinfoUrl);
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, jsonDbinfoUrl);
        try {
            final Request request = builder.sendRequest(null, new RequestCallback() {

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        final String text = response.getText();
//                        logger.info("onResponseReceived");
//                        logger.info(jsonDbinfoUrl);
//                        logger.info(text);
//                        logger.info("onResponseReceivedEnd");
                        // we need this json object to be a json array at this point so we add square brackets
                        final JsArray<JsonDatabaseList> jsonArray = JsonUtils.safeEval("[" + text + "]");
                        final JsonDatabaseList jsonDatabaseList = (JsonDatabaseList) jsonArray.get(0);
                        for (int index = 0; index < jsonDatabaseList.getSize(); index++) {
                            final int rootDocumentsCount = jsonDatabaseList.getRootDocumentsCount(index);
                            final DataNodeId[] dataNodeIds;
                            if (rootDocumentsCount > 0) {
                                dataNodeIds = new DataNodeId[rootDocumentsCount];
                                for (int idIndex = 0; idIndex < rootDocumentsCount; idIndex++) {
                                    dataNodeIds[idIndex] = new DataNodeId(jsonDatabaseList.getRootDocumentsIDs(index, idIndex));
                                }
                            } else {
                                dataNodeIds = new DataNodeId[0];
                            }
                            final DatabaseStats databaseStats = new DatabaseStats(
                                    jsonDatabaseList.getKnownDocumentsCount(index),
                                    jsonDatabaseList.getMissingDocumentsCount(index),
                                    jsonDatabaseList.getDuplicateDocumentsCount(index),
                                    jsonDatabaseList.getRootDocumentsCount(index),
                                    dataNodeIds);
                            final String databaseName = jsonDatabaseList.getDatabaseName(index);
                            databaseStatsMap.put(databaseName, databaseStats);
                        }
                    } else {
                        logger.warning("Couldn't retrieve JSON: non 200");
                        logger.warning(jsonDbinfoUrl);
                        logger.warning(response.getStatusText());
                        logger.warning("StatusCode: " + Integer.toString(response.getStatusCode()));
                    }
                    ready();
                    requestCounter--;
                    if (databaseStatsMap.size() > 0) {
                        historyController.setDefaultDatabase();//databaseNames[0]
                    }
//                    getDatabaseIcons();
                    historyController.fireDatabaseInfoEvent();
                }

                public void onError(Request request, Throwable exception) {
                    logger.log(Level.SEVERE, "DatabaseInfo:getDbInfoJsonError", exception);
                    ready();
                    requestCounter--;
                    logger.log(Level.SEVERE, exception.getMessage());
                    databaseError = true;
                    historyController.fireDatabaseInfoEvent();
                }
            });
        } catch (RequestException exception) {
            logger.log(Level.SEVERE, "DatabaseInfo:getDbInfoJson", exception);
            ready();
            requestCounter--;
            logger.log(Level.SEVERE, exception.getMessage());
            databaseError = true;
            historyController.fireDatabaseInfoEvent();
        }
    }

    private void getDatabaseIcons() {
        for (final String databaseName : databaseStatsMap.keySet()) {
            requestCounter++;
            searchOptionsService.getImageDataForTypes(databaseName, new AsyncCallback<IconTableBase64>() {
                public void onFailure(Throwable caught) {
                    logger.info("failed to get DatabaseInfo:getImageDataForTypes:" + databaseName);
                    ready();
                    requestCounter--;
                    databaseError = true;
                    historyController.fireDatabaseInfoEvent();
                }

                public void onSuccess(IconTableBase64 result) {
                    //logger.info("getImageDataForTypes");
                    ready();
                    requestCounter--;
                    iconTableBase64Map.put(databaseName, result);
                    historyController.fireDatabaseInfoEvent();
                }
            });
        }
    }

    public boolean hasDatabaseError() {
        return databaseError;
    }

    public boolean ready() {
        //logger.info("ready state: " + requestCounter + " " + databaseError);
        return requestCounter <= 0;
    }

    public String[] getDatabaseList() {
        String[] databaseList = new String[databaseStatsMap.size()];
        int index = 0;
        for (final String databaseName : databaseStatsMap.keySet()) {
            databaseList[index] = databaseName;
            index++;
        }
        return databaseList;
    }

    public DatabaseStats getDatabaseStats(String databaseName) {
        return databaseStatsMap.get(databaseName);
    }

    public IconTableBase64 getDatabaseIcons(String databaseName) {
        return iconTableBase64Map.get(databaseName);
    }
}
