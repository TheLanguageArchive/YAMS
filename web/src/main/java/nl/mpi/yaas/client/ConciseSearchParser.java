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
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;

/**
 * @since Mar 19, 2014 5:28:19 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ConciseSearchParser {

    private static final String QUOTE_CHAR = "\"";
    private static final String SPLIT_CHAR = " ";
    private static final String NEGATOR_CHAR = "-";
    private static final QueryDataStructures.SearchNegator DEFAULT_NEGATOR = QueryDataStructures.SearchNegator.is;
    private static final String COMMAND_STRING = ":";
    private static final String JOIN_TYPE_INTERSECT = "all";
    private static final String JOIN_TYPE_UNION = "any";
    private static final String DB_COMMAND = "db" + COMMAND_STRING;
    private static final String FILE_COMMAND = "type" + COMMAND_STRING;
    private static final String FIELD_COMMAND = "path" + COMMAND_STRING;
    private static final String MATCH_COMMAND = "match" + COMMAND_STRING;

    private static final Logger logger = Logger.getLogger("");

    public HistoryData parseConciseSearch(String searchString) {
        // while we would prefer to use StringTokenzier we cannot because this section will be used in javascript via GWT
        final HistoryData historyData = new HistoryData();
        MetadataFileType type = new MetadataFileType(null, "", "");
        MetadataFileType path = new MetadataFileType("", null, "");
        QueryDataStructures.SearchNegator searchNegator = DEFAULT_NEGATOR;
        QueryDataStructures.SearchType searchType = QueryDataStructures.SearchType.equals;
        final ArrayList<SearchParameters> searchParametersList = historyData.getSearchParametersList();
        boolean withinQuote = false;
        boolean isConsumedCommand = false;
        StringBuilder searchTerm = new StringBuilder();
        for (String parameter : searchString.split(SPLIT_CHAR)) {
            // handle the search negator
            if (!withinQuote && parameter.startsWith(NEGATOR_CHAR)) {
                searchNegator = QueryDataStructures.SearchNegator.not;
                // remove the negator char
                parameter = parameter.substring(1);
            }
            // handle parameter quotes
            if (!withinQuote && parameter.startsWith(QUOTE_CHAR)) {
                withinQuote = true;
                // remove the start quote char
                parameter = parameter.substring(1);
            }
            if (withinQuote && parameter.endsWith(QUOTE_CHAR)) {
                withinQuote = false;
                // remove the start quote char
                parameter = parameter.substring(0, parameter.length() - 1);
            }
            if (searchTerm.length() > 0) {
                // if we are within quotes then reassemple the string
                searchTerm.append(SPLIT_CHAR);
            }
            if (!parameter.isEmpty()) {
                if (!isConsumedCommand && !withinQuote) {
                    for (QueryDataStructures.SearchType currentType : QueryDataStructures.SearchType.values()) {
                        if (currentType.name().equals(parameter)) {
                            searchType = currentType;
                            isConsumedCommand = true;
                        }
                    }
                }
                if (!isConsumedCommand && !withinQuote && parameter.startsWith(MATCH_COMMAND)) {
                    final String commandString = parameter.substring(MATCH_COMMAND.length());
                    if (JOIN_TYPE_UNION.equals(commandString)) {
                        historyData.setCriterionJoinType(QueryDataStructures.CriterionJoinType.union);
                    }
                    if (JOIN_TYPE_INTERSECT.equals(commandString)) {
                        historyData.setCriterionJoinType(QueryDataStructures.CriterionJoinType.intersect);
                    }
//                    for (QueryDataStructures.CriterionJoinType currentType : QueryDataStructures.CriterionJoinType.values()) {
//                        if (currentType.name().equals(commandString)) {
//                            historyData.setCriterionJoinType(currentType);
//                        }
//                    }
                    isConsumedCommand = true;
                }
                if (!isConsumedCommand && !withinQuote && parameter.startsWith(DB_COMMAND)) {
                    historyData.setDatabaseName(parameter.substring(DB_COMMAND.length()));
                    isConsumedCommand = true;
                }
                if (!isConsumedCommand && !withinQuote && parameter.startsWith(FILE_COMMAND)) {
                    type = new MetadataFileType(parameter.substring(FILE_COMMAND.length()), "", "");
                    isConsumedCommand = true;
                }
                if (!isConsumedCommand && !withinQuote && parameter.startsWith(FIELD_COMMAND)) {
                    path = new MetadataFileType("", parameter.substring(FIELD_COMMAND.length()), "");
                    isConsumedCommand = true;
                }
                if (!isConsumedCommand && !withinQuote && parameter.contains(COMMAND_STRING)) {
                    logger.info("unused command found:");
                    logger.info(parameter);
                    isConsumedCommand = true;
                }
                if (!isConsumedCommand) {
                    searchTerm.append(parameter);
                }
                isConsumedCommand = false;
            }
            if (!withinQuote && searchTerm.length() > 0) {
                // todo: process all the possible values on the search string like db: type: path: contains equals fuzzy + - etc...
                searchParametersList.add(new SearchParameters(type, path, searchNegator, searchType, searchTerm.toString()));
                searchTerm = new StringBuilder();
                searchNegator = DEFAULT_NEGATOR;
            }
        }
        // add any remaining parts if the final quote is missing
        if (searchTerm.length() > 0) {
            searchParametersList.add(new SearchParameters(type, path, searchNegator, searchType, searchTerm.toString()));
        }
        return historyData;
    }

    public String getConciseString(HistoryController historyController) {
        final HistoryData historyData = historyController.getHistoryData();
        StringBuilder searchStringBuilder = new StringBuilder();
//        if (historyController.getDatabaseName().equals("EWE-2013-11-13")) {
        if (historyData.getDatabaseName() != null && !historyData.getDatabaseName().isEmpty() && !historyController.getDefaultDatabase().equals(historyData.getDatabaseName())) {
            searchStringBuilder.append(DB_COMMAND);
            searchStringBuilder.append(historyData.getDatabaseName());
            searchStringBuilder.append(SPLIT_CHAR);
        }
        if (historyData.getCriterionJoinType().equals(QueryDataStructures.CriterionJoinType.intersect)) {
            searchStringBuilder.append(MATCH_COMMAND + JOIN_TYPE_INTERSECT + SPLIT_CHAR);
        }
        for (SearchParameters searchParameter : historyData.getSearchParametersList()) {
            if (searchParameter.getFileType().getType() != null && !searchParameter.getFileType().getType().isEmpty()) {
                searchStringBuilder.append(FILE_COMMAND);
                searchStringBuilder.append(searchParameter.getFileType().getType());
                searchStringBuilder.append(SPLIT_CHAR);
            }
            if (searchParameter.getFieldType().getPath() != null && !searchParameter.getFieldType().getPath().isEmpty()) {
                searchStringBuilder.append(FIELD_COMMAND);
                searchStringBuilder.append(searchParameter.getFieldType().getPath());
                searchStringBuilder.append(SPLIT_CHAR);
            }
            if (!searchParameter.getSearchType().equals(QueryDataStructures.SearchType.equals)) {
                searchStringBuilder.append(SPLIT_CHAR);
                searchStringBuilder.append(searchParameter.getSearchType());
                searchStringBuilder.append(SPLIT_CHAR);
            }
            // the + parameter is by default
//            if (searchParameter.getSearchNegator().equals(SearchNegator.is)) {
//                searchStringBuilder.append("+");
//            }
            if (searchParameter.getSearchNegator().equals(QueryDataStructures.SearchNegator.not)) {
                searchStringBuilder.append(NEGATOR_CHAR);
            }
            searchStringBuilder.append(QUOTE_CHAR);
            searchStringBuilder.append(searchParameter.getSearchString());
            searchStringBuilder.append(QUOTE_CHAR + SPLIT_CHAR);
        }
        return searchStringBuilder.toString();
    }
}
