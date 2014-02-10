/*
 * Copyright (C) 2014 The Language Archive, Max Planck Institute for Psycholinguistics
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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since Feb 07, 2014 16:01 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class HistoryController implements ValueChangeHandler<String> {

    private String databaseName = "";
    ArrayList<HistoryListener> historyListeners = new ArrayList<HistoryListener>();
    private static final Logger logger = Logger.getLogger("");

    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue().toString();
//        logger.log(Level.INFO, historyToken);
        setStateFromHistory(historyToken);
    }

    public void addHistoryListener(HistoryListener historyListener) {
        historyListeners.add(historyListener);
    }

    public void removeHistoryListener(HistoryListener historyListener) {
        historyListeners.remove(historyListener);
    }

    private void setStateFromHistory(String historyToken) {
//        if (historyToken.substring(0, 3).equals("db:")) {.substring(2)
        final String[] historyParts = historyToken.split(",");
        if (historyParts != null && historyParts.length > 0) {
            databaseName = historyParts[0];
//            logger.log(Level.INFO, databaseName);
            for (HistoryListener historyListener : historyListeners) {
                historyListener.historyChange();
            }
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        if (databaseName == null) {
            History.newItem("");
        } else {
            History.newItem(/*"db:" +*/databaseName + ",");
        }
    }

}
