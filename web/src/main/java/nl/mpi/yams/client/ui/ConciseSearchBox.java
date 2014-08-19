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
package nl.mpi.yams.client.ui;

import com.google.gwt.core.client.GWT;
import nl.mpi.yams.client.controllers.SearchSuggestOracle;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;
import java.util.logging.Logger;
import nl.mpi.yams.client.ConciseSearchParser;
import nl.mpi.yams.client.DatabaseInformation;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.client.HistoryData;
import nl.mpi.yams.client.HistoryListener;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.client.ServiceLocations;
import nl.mpi.yams.client.controllers.SearchHandler;

/**
 * @since Mar 18, 2014 1:27:32 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ConciseSearchBox extends HorizontalPanel implements HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);
    private final HistoryController historyController;
    private final SuggestBox searchBox;
    private final Button searchButton;
    final private Image valuesPathsImage;
    private final SearchHandler searchHandler;

    public ConciseSearchBox(SearchOptionsServiceAsync searchOptionsService, final HistoryController historyController, DatabaseInformation databaseInfo, ResultsPanel resultsPanel) {
        this.setStyleName("yams-ConciseSearchBox");
        this.historyController = historyController;
        searchBox = new SuggestBox(new SearchSuggestOracle(searchOptionsService) {

            @Override
            public String getType() {
                return null;
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public String getSearchText() {
                return searchBox.getText();
            }

            @Override
            public String getDatabaseName() {
                return historyController.getDatabaseName();
            }

            @Override
            public void setHintRequestStatus(boolean requestInProgress, String hintMessage) {
                valuesPathsImage.setVisible(requestInProgress);
//                hintLabel.setText(hintMessage);
            }
        });
        searchBox.setLimit(20);
        searchBox.setAutoSelectEnabled(false);
        searchButton = new Button("search");
        searchHandler = new SearchHandler(historyController, databaseInfo, searchOptionsService, resultsPanel) {
            @Override
            protected void prepareSearch() {
                searchButton.setEnabled(false);
                final HistoryData searchParameters = new ConciseSearchParser().parseConciseSearch(historyController, searchBox.getText());
                historyController.setHistoryData(searchParameters);
//                searchButton.setHTML(SEARCHING_LABEL);
            }

            @Override
            protected void finaliseSearch() {
                searchButton.setEnabled(true);
//                searchButton.setHTML(SEARCH_LABEL);
            }
        };
        valuesPathsImage = new Image("./loader.gif");
        searchButton.addClickHandler(searchHandler);
        searchBox.addKeyUpHandler(searchHandler);
        this.add(searchBox);
        this.add(valuesPathsImage);
        valuesPathsImage.setVisible(false);
        this.add(searchButton);
        // todo: add an oracal for commands and values
    }

    public void historyChange() {
//        logger.info("historyChange");
        searchHandler.updateDbName();
        searchBox.setText(new ConciseSearchParser().getConciseString(historyController));
    }

    public void userSelectionChange() {
//        logger.info("userSelectionChange");
        // in this case we need to do the same thing on a user selection change as for a history change event
        historyChange();
    }

    public void performSearch() {
        searchHandler.onClick(null);
    }
}
