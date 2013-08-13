/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
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
import nl.mpi.flap.model.SerialisableDataNode;
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
    private final SearchOptionsServiceAsync searchOptionsService;
    private final DataNodeTable dataNodeTable;
    private Button searchButton;
    private SearchHandler searchHandler;
    private final DataNodeTree dataNodeTree;
    private final ValueListBox<CriterionJoinType> joinTypeListBox;
    private final VerticalPanel verticalPanel;
    private final ArrayList<SearchCriterionPanel> criterionPanelList = new ArrayList<SearchCriterionPanel>();

    public SearchPanel(SearchOptionsServiceAsync searchOptionsService, DataNodeTree dataNodeTree, DataNodeTable dataNodeTable) {
        this.searchOptionsService = searchOptionsService;
        this.dataNodeTable = dataNodeTable;
        this.dataNodeTree = dataNodeTree;
        verticalPanel = new VerticalPanel();
        initSearchHandler();
        final SearchCriterionPanel searchCriterionPanel = new SearchCriterionPanel(SearchPanel.this, searchOptionsService);
        verticalPanel.add(searchCriterionPanel);
        criterionPanelList.add(searchCriterionPanel);
        Button addRowButton = new Button("add search term", new ClickHandler() {
            public void onClick(ClickEvent event) {
                addSearchCriterionPanel(new SearchCriterionPanel(SearchPanel.this, SearchPanel.this.searchOptionsService));
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
        searchButton = new Button("Search");
        searchButton.addStyleName("sendButton");

        searchHandler = new SearchHandler() {
            @Override
            void performSearch() {
                searchButton.setEnabled(false);
                final long startTime = System.currentTimeMillis();
                ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
                for (SearchCriterionPanel eventCriterionPanel : criterionPanelList) {
                    searchParametersList.add(new SearchParameters(eventCriterionPanel.getMetadataFileType(), eventCriterionPanel.getMetadataFieldType(), eventCriterionPanel.getSearchNegator(), eventCriterionPanel.getSearchType(), eventCriterionPanel.getSearchText()));
                }
                searchOptionsService.performSearch(joinTypeListBox.getValue(), searchParametersList, new AsyncCallback<SerialisableDataNode>() {
                    public void onFailure(Throwable caught) {
                        logger.log(Level.SEVERE, "PerformSearch", caught);
                        searchHandler.signalSearchDone();
                        searchButton.setEnabled(true);
                    }

                    public void onSuccess(SerialisableDataNode result) {
                        long responseMils = System.currentTimeMillis() - startTime;
                        final String searchTimeMessage = "PerformSearch response time: " + responseMils + " ms";
                        logger.log(Level.INFO, searchTimeMessage);
                        dataNodeTree.addResultsToTree(result);
                        searchHandler.signalSearchDone();
                        searchButton.setEnabled(true);
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
                    return "<no value>";
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
        widget.addStyleName("demo-ListBox");
        widget.setValue(SearchOption.equals);
        widget.setAcceptableValues(Arrays.asList(SearchOption.values()));
        return widget;
    }

    private ValueListBox getJoinTypeListBox() {
        final ValueListBox<CriterionJoinType> widget = new ValueListBox<CriterionJoinType>(new Renderer<CriterionJoinType>() {
            public String render(CriterionJoinType object) {
                if (object == null) {
                    return "<no value>";
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
        widget.addStyleName("demo-ListBox");
        widget.setValue(CriterionJoinType.intersect);
        widget.setAcceptableValues(Arrays.asList(CriterionJoinType.values()));
        return widget;
    }

    public SearchHandler getSearchHandler() {
        return searchHandler;
    }
}
