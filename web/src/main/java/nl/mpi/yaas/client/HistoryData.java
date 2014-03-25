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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;

/**
 * @since Feb 11, 2014 10:08:14 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class HistoryData {

    private static final Logger logger = Logger.getLogger("");
    private String databaseName = "";
    private final HashSet<String> searchHandles = new HashSet<String>();
    private QueryDataStructures.CriterionJoinType criterionJoinType = QueryDataStructures.CriterionJoinType.union;
    private ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
    public final QueryDataStructures.CriterionJoinType defaultCriterionJoinType = QueryDataStructures.CriterionJoinType.union;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getHistoryToken() {
        StringBuilder historyToken = new StringBuilder();
        if (databaseName != null) {
            historyToken.append(/*"db:" +*/databaseName);
            if (criterionJoinType != null) {
                historyToken.append(",");
                historyToken.append(criterionJoinType.name());
                if (searchParametersList != null) {
                    for (SearchParameters searchParameters : searchParametersList) {
                        historyToken.append(",");
                        if (searchParameters.getFileType().getType() != null) {
                            historyToken.append(searchParameters.getFileType().getType());
                        }
                        historyToken.append(",");
                        if (searchParameters.getFieldType().getPath() != null) {
                            historyToken.append(searchParameters.getFieldType().getPath());
                        }
                        historyToken.append(",");
                        if (searchParameters.getSearchNegator() != null) {
                            historyToken.append(searchParameters.getSearchNegator());
                        }
                        historyToken.append(",");
                        if (searchParameters.getSearchType() != null) {
                            historyToken.append(searchParameters.getSearchType());
                        }
                        historyToken.append(",");
                        final String searchString = searchParameters.getSearchString();
                        if (searchString != null) {
                            final String encodedSearchString = searchString.replaceAll(",", "%2C"); //this gets encoded twice but this is so we can decode it after splitting the other parameters on "," later
                            historyToken.append(encodedSearchString);
//                            historyToken.append(History.encodeHistoryToken(searchString));
                        }
//                        historyToken.append(")");
                    }
                }
            }
        }
        return historyToken.toString();
    }

    public void parseHistoryToken(String historyToken) {
        logger.log(Level.INFO, historyToken);
        ArrayList<SearchParameters> searchParametersListTemp = new ArrayList<SearchParameters>();
        final String[] historyParts = historyToken.split(",");
        if (historyParts != null && historyParts.length > 0) {
            databaseName = historyParts[0];
        } else {
            databaseName = null;
        }
        if (historyParts != null && historyParts.length > 1) {
            criterionJoinType = QueryDataStructures.CriterionJoinType.valueOf(historyParts[1]);
            // continue reading the token to get the remaining search parameters
            int searchParametersIndex = 0;
            final int paramStartIndex = 2;
            final int paramCount = 5;
            while (historyParts.length > paramStartIndex + (searchParametersIndex * paramCount)) {
                final int currentParamIndex = paramStartIndex + searchParametersIndex * paramCount;
                final String fileType = historyParts[currentParamIndex];
                final String fieldType = historyParts[currentParamIndex + 1];
                final String negatorType = historyParts[currentParamIndex + 2];
                final String searchType = historyParts[currentParamIndex + 3];
                // if the user is searching for "" then the last element will not exist
                final String searchText = (historyParts.length < currentParamIndex + 5) ? "" : historyParts[currentParamIndex + 4].replaceAll("%2C", ",");
                searchParametersListTemp.add(new SearchParameters(new MetadataFileType(fileType, null, null), new MetadataFileType(null, fieldType, null), QueryDataStructures.SearchNegator.valueOf(negatorType), QueryDataStructures.SearchType.valueOf(searchType), searchText));
                searchParametersIndex++;
            }
        } else {
            criterionJoinType = defaultCriterionJoinType;
        }
        searchParametersList = searchParametersListTemp;
    }

    public QueryDataStructures.CriterionJoinType getCriterionJoinType() {
        return criterionJoinType;
    }

    public void setCriterionJoinType(QueryDataStructures.CriterionJoinType criterionJoinType) {
        this.criterionJoinType = criterionJoinType;
    }

    public ArrayList<SearchParameters> getSearchParametersList() {
        return searchParametersList;
    }

    public void setSearchParametersList(ArrayList<SearchParameters> searchParametersList) {
        this.searchParametersList = searchParametersList;
    }

    public void addSearchHandle(String handleString) {
        searchHandles.add(handleString);
    }
}
