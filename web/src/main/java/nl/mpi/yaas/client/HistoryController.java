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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import java.util.ArrayList;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;

/**
 * @since Feb 07, 2014 16:01 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class HistoryController implements ValueChangeHandler<String> {

    ArrayList<HistoryListener> historyListeners = new ArrayList<HistoryListener>();
    private static final Logger logger = Logger.getLogger("");
    final private HistoryData historyData;
    private String defaultDatabase = "";

    public HistoryController() {
        this.historyData = new HistoryData();
    }

    public void setDefaultDatabase(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
        historyData.setDatabaseName(defaultDatabase);
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void onValueChange(ValueChangeEvent<String> event) {
        this.historyData.parseHistoryToken(event.getValue());
        notifyListeners();
    }

    public void addHistoryListener(HistoryListener historyListener) {
        historyListeners.add(historyListener);
    }

    public void removeHistoryListener(HistoryListener historyListener) {
        historyListeners.remove(historyListener);
    }

    private void notifyListeners() {
        for (HistoryListener historyListener : historyListeners) {
            historyListener.historyChange();
        }
    }

    public String getDatabaseName() {
        return historyData.getDatabaseName();
    }

    public void setDatabaseName(String databaseName) {
        historyData.setDatabaseName(databaseName);
        updateHistory(true); // todo: this should probably be a notify not history event
    }

    public QueryDataStructures.CriterionJoinType getCriterionJoinType() {
        return historyData.getCriterionJoinType();
    }

    public ArrayList<SearchParameters> getSearchParametersList() {
        return historyData.getSearchParametersList();
    }

    public void setSearchParameters(QueryDataStructures.CriterionJoinType criterionJoinType, ArrayList<SearchParameters> searchParametersList) {
        historyData.setCriterionJoinType(criterionJoinType);
        historyData.setSearchParametersList(searchParametersList);
        notifyListeners();
    }

    private void updateHistory(boolean issueEvent) {
        History.newItem(historyData.getHistoryToken(), issueEvent);
    }

    public HistoryData getHistoryData() {
        return historyData;
    }
}
