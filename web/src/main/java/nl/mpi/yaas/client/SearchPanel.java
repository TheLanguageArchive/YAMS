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
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.HighlighableDataNode;
import nl.mpi.yaas.common.data.QueryDataStructures.CriterionJoinType;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchOption;
import nl.mpi.yaas.common.data.SearchParameters;

/**
 * Created on : Jan 29, 2013, 2:50:44 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SearchPanel extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");
    private static final String SEARCH_LABEL = "Search";
    private static final String SEARCHING_LABEL = "<img src='./loader.gif'/>&nbsp;Searching";
    private static final String ADD_SEARCH_TERM = "add search term";
    private static final String sendButtonStyle = "sendButton";
    private static final String NO_VALUE = "<no value>";
    private static final String DEMO_LIST_BOX_STYLE = "demo-ListBox";
    private final SearchOptionsServiceAsync searchOptionsService;
    private final DataNodeTable dataNodeTable;
    private Button searchButton;
    private SearchHandler searchHandler;
    private final ResultsPanel resultsPanel;
    private final ValueListBox<CriterionJoinType> joinTypeListBox;
    private final VerticalPanel verticalPanel;
    private final ArrayList<SearchCriterionPanel> criterionPanelList = new ArrayList<SearchCriterionPanel>();
    private final String databaseName;

    public SearchPanel(SearchOptionsServiceAsync searchOptionsService, final String databaseName, ResultsPanel resultsPanel, DataNodeTable dataNodeTable) {
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTable = dataNodeTable;
        this.resultsPanel = resultsPanel;
        this.databaseName = databaseName;
        verticalPanel = new VerticalPanel();
        initSearchHandler();
        final SearchCriterionPanel searchCriterionPanel = new SearchCriterionPanel(databaseName, SearchPanel.this, searchOptionsService);
        verticalPanel.add(searchCriterionPanel);
        criterionPanelList.add(searchCriterionPanel);
        Button addRowButton = new Button(ADD_SEARCH_TERM, new ClickHandler() {
            public void onClick(ClickEvent event) {
                addSearchCriterionPanel(new SearchCriterionPanel(databaseName, SearchPanel.this, SearchPanel.this.searchOptionsService));
            }
        });
        this.add(verticalPanel);
        final HorizontalPanel buttonsPanel = new HorizontalPanel();
        this.add(addRowButton);
        joinTypeListBox = getJoinTypeListBox();
        buttonsPanel.add(joinTypeListBox);
        buttonsPanel.add(searchButton);
        this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        this.add(buttonsPanel);
    }

    protected void addSearchCriterionPanel(SearchCriterionPanel criterionPanel) {
        criterionPanelList.add(criterionPanel);
        verticalPanel.add(criterionPanel);
    }

    protected void removeSearchCriterionPanel(SearchCriterionPanel criterionPanel) {
        criterionPanelList.remove(criterionPanel);
        verticalPanel.remove(criterionPanel);
    }

    private void initSearchHandler() {
        searchButton = new Button(SEARCH_LABEL);
        searchButton.addStyleName(sendButtonStyle);

        searchHandler = new SearchHandler() {
            @Override
            void performSearch() {
                searchButton.setEnabled(false);
                searchButton.setHTML(SEARCHING_LABEL);
                final long startTime = System.currentTimeMillis();
                ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
                for (SearchCriterionPanel eventCriterionPanel : criterionPanelList) {
                    searchParametersList.add(new SearchParameters(eventCriterionPanel.getMetadataFileType(), eventCriterionPanel.getMetadataFieldType(), eventCriterionPanel.getSearchNegator(), eventCriterionPanel.getSearchType(), eventCriterionPanel.getSearchText()));
                }
                searchOptionsService.performSearch(databaseName, joinTypeListBox.getValue(), searchParametersList, new AsyncCallback<HighlighableDataNode>() {
                    public void onFailure(Throwable caught) {
                        logger.log(Level.SEVERE, caught.getMessage());
                        searchHandler.signalSearchDone();
                        searchButton.setEnabled(true);
                        searchButton.setHTML(SEARCH_LABEL);
                    }

                    public void onSuccess(HighlighableDataNode result) {
                        long responseMils = System.currentTimeMillis() - startTime;
                        final String searchTimeMessage = "PerformSearch response time: " + responseMils + " ms";
                        logger.log(Level.INFO, searchTimeMessage);
                        resultsPanel.addResultsTree(databaseName, result, responseMils);
                        searchHandler.signalSearchDone();
                        searchButton.setEnabled(true);
                        searchButton.setHTML(SEARCH_LABEL);
                    }
                });
            }
        };
        searchButton.addClickHandler(searchHandler);
    }

    protected ValueListBox getSearchOptionsListBox() {
        final ValueListBox<SearchOption> widget = new ValueListBox<SearchOption>(new Renderer<SearchOption>() {
            public String render(SearchOption object) {
                if (object == null) {
                    return NO_VALUE;
                } else {
                    return object.toString();
                }
            }

            public void render(SearchOption object, Appendable appendable) throws IOException {
                if (object != null) {
                    appendable.append(object.toString());
                }
            }
        });
        widget.addStyleName(DEMO_LIST_BOX_STYLE);
        widget.setValue(SearchOption.equals);
        widget.setAcceptableValues(Arrays.asList(SearchOption.values()));
        return widget;
    }

    private ValueListBox getJoinTypeListBox() {
        final ValueListBox<CriterionJoinType> widget = new ValueListBox<CriterionJoinType>(new Renderer<CriterionJoinType>() {
            public String render(CriterionJoinType object) {
                if (object == null) {
                    return NO_VALUE;
                } else {
                    return object.toString();
                }
            }

            public void render(CriterionJoinType object, Appendable appendable) throws IOException {
                if (object != null) {
                    appendable.append(object.toString());
                }
            }
        });
        widget.addStyleName(DEMO_LIST_BOX_STYLE);
        widget.setValue(CriterionJoinType.intersect);
        widget.setAcceptableValues(Arrays.asList(CriterionJoinType.values()));
        return widget;
    }

    public SearchHandler getSearchHandler() {
        return searchHandler;
    }
}
