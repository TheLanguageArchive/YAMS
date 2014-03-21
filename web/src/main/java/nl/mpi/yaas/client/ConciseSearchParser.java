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

import java.util.logging.Logger;
import nl.mpi.yaas.common.data.QueryDataStructures;
import nl.mpi.yaas.common.data.SearchParameters;

/**
 * @since Mar 19, 2014 5:28:19 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ConciseSearchParser {

    private static final Logger logger = Logger.getLogger("");

    public HistoryData parseConciseSearch(String searchString) {
        // todo: parse the actual search string
        final HistoryData historyData = new HistoryData();
        return historyData;
    }

    public String getConciseString(HistoryController historyController) {
        final HistoryData historyData = historyController.getHistoryData();
        StringBuilder searchStringBuilder = new StringBuilder();
//        if (historyController.getDatabaseName().equals("EWE-2013-11-13")) {
        if (!historyData.getDatabaseName().isEmpty() && !historyController.getDefaultDatabase().equals(historyData.getDatabaseName())) {
            searchStringBuilder.append("db:");
            searchStringBuilder.append(historyData.getDatabaseName());
            searchStringBuilder.append(" ");
        }
        if (historyData.getCriterionJoinType().equals(QueryDataStructures.CriterionJoinType.intersect)) {
            searchStringBuilder.append("match:all ");
        }
        for (SearchParameters searchParameter : historyData.getSearchParametersList()) {
            if (searchParameter.getFileType().getType() != null && !searchParameter.getFileType().getType().isEmpty()) {
                searchStringBuilder.append("file:");
                searchStringBuilder.append(searchParameter.getFileType().getType());
                searchStringBuilder.append(" ");
            }
            if (searchParameter.getFieldType().getPath() != null && !searchParameter.getFieldType().getPath().isEmpty()) {
                searchStringBuilder.append("field:");
                searchStringBuilder.append(searchParameter.getFieldType().getPath());
                searchStringBuilder.append(" ");
            }
            if (!searchParameter.getSearchType().equals(QueryDataStructures.SearchType.equals)) {
                searchStringBuilder.append(" ");
                searchStringBuilder.append(searchParameter.getSearchType());
                searchStringBuilder.append(" ");
            }
            // the + parameter is by default
//            if (searchParameter.getSearchNegator().equals(SearchNegator.is)) {
//                searchStringBuilder.append("+");
//            }
            if (searchParameter.getSearchNegator().equals(QueryDataStructures.SearchNegator.not)) {
                searchStringBuilder.append("-");
            }
            searchStringBuilder.append("\"");
            searchStringBuilder.append(searchParameter.getSearchString());
            searchStringBuilder.append("\" ");
        }
        return searchStringBuilder.toString();
    }
}
