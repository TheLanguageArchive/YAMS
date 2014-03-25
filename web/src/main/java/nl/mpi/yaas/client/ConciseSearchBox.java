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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import java.util.logging.Logger;

/**
 * @since Mar 18, 2014 1:27:32 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ConciseSearchBox extends HorizontalPanel implements HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    private final HistoryController historyController;
    private final TextBox searchBox;
    private final Button searchButton;

    public ConciseSearchBox(SearchOptionsServiceAsync searchOptionsService, final HistoryController historyController, DatabaseInfo databaseInfo, ResultsPanel resultsPanel) {
        this.setStyleName("yams-ConciseSearchBox");
        this.historyController = historyController;
        searchBox = new TextBox();
        searchButton = new Button("search");
        SearchHandler searchHandler = new SearchHandler(historyController, databaseInfo, searchOptionsService, resultsPanel) {
            @Override
            void prepareSearch() {
                searchButton.setEnabled(false);
                final HistoryData searchParameters = new ConciseSearchParser().parseConciseSearch(searchBox.getText());
                historyController.setHistoryData(searchParameters);
//                searchButton.setHTML(SEARCHING_LABEL);
            }

            @Override
            void finaliseSearch() {
                searchButton.setEnabled(true);
//                searchButton.setHTML(SEARCH_LABEL);
            }
        };
        searchButton.addClickHandler(searchHandler);
        searchBox.addKeyUpHandler(searchHandler);
        this.add(searchBox);
        this.add(searchButton);
        // todo: add an oracal for commands and values
    }

    public void historyChange() {
//        logger.info("historyChange");
        searchBox.setText(new ConciseSearchParser().getConciseString(historyController));
    }

    public void userSelectionChange() {
//        logger.info("userSelectionChange");
        // in this case we need to do the same thing on a user selection change as for a history change event
        historyChange();
    }
}
