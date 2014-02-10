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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ValueListBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;

/**
 * Created on : Feb 5, 2013, 11:44:03 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SearchCriterionPanel extends HorizontalPanel implements HistoryListener {

    private static final Logger logger = Logger.getLogger("");
    final private SearchOptionsServiceAsync searchOptionsService;
    final private SearchPanel searchPanel;
    final private ValueListBox<MetadataFileType> typesOptionsListBox;
    final private ValueListBox<MetadataFileType> fieldsOptionsListBox;
    final private ValueListBox<QueryDataStructures.SearchOption> searchOptionsListBox;
    final private SuggestBox searchTextBox;
    private MultiWordSuggestOracle oracle;
    private final HistoryController historyController;
    final private Image loadingTypesImage;
    final private Image loadingPathsImage;
    final private Image valuesPathsImage;
    final private Label hintLabel;
    private String lastUsedDatabase = null;

    public SearchCriterionPanel(HistoryController historyController, final SearchPanel searchPanel, SearchOptionsServiceAsync searchOptionsService) {
        this.searchPanel = searchPanel;
        this.historyController = historyController;
        this.searchOptionsService = searchOptionsService;
        Button removeRowButton = new Button("remove", new ClickHandler() {
            public void onClick(ClickEvent event) {
                searchPanel.removeSearchCriterionPanel(SearchCriterionPanel.this);
            }
        });
        this.add(removeRowButton);
        searchTextBox = getSearchTextBox(searchPanel.getSearchHandler());
        fieldsOptionsListBox = getFieldsOptionsListBox();
        typesOptionsListBox = getTypesOptionsListBox();
        loadingTypesImage = new Image("./loader.gif");
        loadingPathsImage = new Image("./loader.gif");
        hintLabel = new Label();
        valuesPathsImage = new Image("./loader.gif");
        this.add(typesOptionsListBox);
        add(loadingTypesImage);
        loadingTypesImage.setVisible(false);
        this.add(fieldsOptionsListBox);
        add(loadingPathsImage);
        loadingPathsImage.setVisible(false);
        searchOptionsListBox = searchPanel.getSearchOptionsListBox();
        this.add(searchOptionsListBox);
        this.add(searchTextBox);
        this.add(valuesPathsImage);
        this.add(hintLabel);
        valuesPathsImage.setVisible(false);
    }

    public void historyChange() {
        final String databaseName = historyController.getDatabaseName();
        if (databaseName != null && !databaseName.equals(lastUsedDatabase)) {
            lastUsedDatabase = databaseName;
            loadTypesOptions();
        }
    }

    public MetadataFileType getMetadataFileType() {
        return typesOptionsListBox.getValue();
    }

    public MetadataFileType getMetadataFieldType() {
        return fieldsOptionsListBox.getValue();
    }

    public QueryDataStructures.SearchOption getSearchOption() {
        return searchOptionsListBox.getValue();
    }

    public QueryDataStructures.SearchNegator getSearchNegator() {
        return getSearchOption().getSearchNegator();
    }

    public QueryDataStructures.SearchType getSearchType() {
        return getSearchOption().getSearchType();
    }

    private SuggestBox getSearchTextBox(SearchHandler searchHandler) {
        final SuggestBox suggestBox = createTextBox();
        suggestBox.addKeyUpHandler(searchHandler);
        return suggestBox;
    }

    public String getSearchText() {
        return searchTextBox.getText();
    }

    private void loadTypesOptions() {
        loadingTypesImage.setVisible(true);
        searchOptionsService.getTypeOptions(historyController.getDatabaseName(), null, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
                loadingTypesImage.setVisible(false);
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    typesOptionsListBox.setValue(result[0]);
                    typesOptionsListBox.setAcceptableValues(Arrays.asList(result));
                    loadPathsOptions(typesOptionsListBox.getValue());
                }
                loadingTypesImage.setVisible(false);
            }
        });

    }

    private void loadPathsOptions(MetadataFileType type) {
        loadingPathsImage.setVisible(true);
        searchOptionsService.getPathOptions(historyController.getDatabaseName(), type, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
                loadingPathsImage.setVisible(false);
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    fieldsOptionsListBox.setValue(result[0]);
                    fieldsOptionsListBox.setAcceptableValues(Arrays.asList(result));
                    loadingPathsImage.setVisible(false);
                }
            }
        });
    }

    private ValueListBox getFieldsOptionsListBox() {
        final ValueListBox<MetadataFileType> widget = new ValueListBox<MetadataFileType>(new Renderer<MetadataFileType>() {
            public String render(MetadataFileType object) {
                if (object == null) {
                    return "<no value>";
                } else {
                    return object.toString();
                }
            }

            public void render(MetadataFileType object, Appendable appendable) throws IOException {
                if (object != null) {
                    appendable.append(object.toString());
                }
            }
        });
        widget.addStyleName("demo-ListBox");
        widget.addValueChangeHandler(new ValueChangeHandler<MetadataFileType>() {
            public void onValueChange(ValueChangeEvent<MetadataFileType> event) {
                clearSuggestOracle();
            }
        });
        return widget;
    }

    private ValueListBox getTypesOptionsListBox() {
        final ValueListBox<MetadataFileType> widget = new ValueListBox<MetadataFileType>(new Renderer<MetadataFileType>() {
            public String render(MetadataFileType object) {
                if (object == null) {
                    return "<no value>";
                } else {
                    return object.toString();
                }
            }

            public void render(MetadataFileType object, Appendable appendable) throws IOException {
                if (object != null) {
                    appendable.append(object.toString());
                }
            }
        });
        widget.addStyleName("demo-ListBox");
        widget.addValueChangeHandler(new ValueChangeHandler<MetadataFileType>() {
            public void onValueChange(ValueChangeEvent<MetadataFileType> event) {
                loadPathsOptions(event.getValue());
            }
        });
        return widget;
    }

    private void clearSuggestOracle() {
        oracle.clear();
    }

    private SuggestBox createTextBox() {
        oracle = new MultiWordSuggestOracle() {
            @Override
            public void requestSuggestions(final Request request, final Callback callback) {
//                if (request.getQuery().length() < 3) {
//                    // ignore queries that are less that 2 letters long
//                    Response response = new Response(Collections.<Suggestion>emptyList());
//                    callback.onSuggestionsReady(request, response);
//                    return;
//                }
                valuesPathsImage.setVisible(true);
                final MetadataFileType typeSelection = fieldsOptionsListBox.getValue();
                final MetadataFileType options = new MetadataFileType(typeSelection.getType(), typeSelection.getPath(), request.getQuery());
                searchOptionsService.getValueOptions(historyController.getDatabaseName(), options, new AsyncCallback<MetadataFileType[]>() {
                    public void onFailure(Throwable caught) {
                        valuesPathsImage.setVisible(false);
                        logger.log(Level.SEVERE, caught.getMessage());
                        hintLabel.setText("hint: try specifying a type and or path before typing");
                    }

                    public void onSuccess(MetadataFileType[] result) {
                        hintLabel.setText("");
                        ArrayList<Suggestion> suggestionList = new ArrayList<Suggestion>();
                        if (result != null) {
                            for (final MetadataFileType type : result) {
                                suggestionList.add(new Suggestion() {

                                    public String getDisplayString() {
                                        return type.getValue();
                                    }

                                    public String getReplacementString() {
                                        return type.getValue();
                                    }
                                });
                                logger.log(Level.INFO, type.getValue());
                            }
                            Response response = new Response(suggestionList);
                            callback.onSuggestionsReady(request, response);
                        }
                        valuesPathsImage.setVisible(false);
                    }
                });
            }
        };
        final SuggestBox suggestBox = new SuggestBox(oracle);
        return suggestBox;
    }
}
