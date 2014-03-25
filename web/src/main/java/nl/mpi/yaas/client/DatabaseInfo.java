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
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.IconTableBase64;

/**
 * @since Mar 25, 2014 3:07:25 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseInfo {

    private static final Logger logger = Logger.getLogger("");
    private final SearchOptionsServiceAsync searchOptionsService;
    private final HistoryController historyController;
    private String[] databaseNames = new String[0];
    private boolean databaseError = false;
    private final HashMap<String, DatabaseStats> databaseStatsMap = new HashMap<String, DatabaseStats>();
    private final HashMap<String, IconTableBase64> iconTableBase64Map = new HashMap<String, IconTableBase64>();

    private int requestCounter = 0;

    public DatabaseInfo(SearchOptionsServiceAsync searchOptionsService, HistoryController historyController) {
        this.searchOptionsService = searchOptionsService;
        this.historyController = historyController;
    }

    public void getDbInfo() {
        requestCounter++;
        searchOptionsService.getDatabaseList(new AsyncCallback<String[]>() {
            public void onFailure(Throwable caught) {
                //logger.warning(caught.getMessage());
                ready();
                requestCounter--;
                logger.log(Level.SEVERE, caught.getMessage());
                databaseError = true;
                historyController.fireDatabaseInfoEvent();
            }

            public void onSuccess(String[] result) {
                //logger.info("getDbInfo");
                ready();
                requestCounter--;
                databaseNames = result;
                if (databaseNames.length > 0) {
                    historyController.setDefaultDatabase(databaseNames[0]);
                }
                getDatabaseStats();
                historyController.fireDatabaseInfoEvent();
            }
        });
    }

    private void getDatabaseStats() {
        for (final String databaseName : databaseNames) {
            requestCounter++;
            searchOptionsService.getDatabaseStats(databaseName, new AsyncCallback<DatabaseStats>() {
                public void onFailure(Throwable caught) {
                    //logger.warning(caught.getMessage());
                    ready();
                    requestCounter--;
                    databaseError = true;
                    //logger.log(Level.SEVERE, caught.getMessage());
                    historyController.fireDatabaseInfoEvent();
                }

                public void onSuccess(DatabaseStats result) {
                    //logger.info("getDatabaseStats");
                    ready();
                    requestCounter--;
                    databaseStatsMap.put(databaseName, result);
                    historyController.fireDatabaseInfoEvent();
                }
            });
            requestCounter++;
            searchOptionsService.getImageDataForTypes(databaseName, new AsyncCallback<IconTableBase64>() {
                public void onFailure(Throwable caught) {
                    //logger.warning(caught.getMessage());
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
        return databaseNames;
    }

    public DatabaseStats getDatabaseStats(String databaseName) {
        return databaseStatsMap.get(databaseName);
    }

    public IconTableBase64 getDatabaseIcons(String databaseName) {
        return iconTableBase64Map.get(databaseName);
    }

}
