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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ValueListBox;
import java.io.IOException;
import java.util.Arrays;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;

/**
 * Created on : Feb 5, 2013, 11:44:03 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SearchCriterionPanel extends HorizontalPanel {

    private final SearchOptionsServiceAsync searchOptionsService;
    final SearchPanel searchPanel;
    final ValueListBox<MetadataFileType> typesOptionsListBox;
    final ValueListBox<MetadataFileType> fieldsOptionsListBox;
    final ValueListBox<QueryDataStructures.SearchOption> searchOptionsListBox;
    final SuggestBox searchTextBox;

    public SearchCriterionPanel(final SearchPanel searchPanel, SearchOptionsServiceAsync searchOptionsService) {
        this.searchPanel = searchPanel;
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
        this.add(typesOptionsListBox);
        this.add(fieldsOptionsListBox);
        searchOptionsListBox = searchPanel.getSearchOptionsListBox();
        this.add(searchOptionsListBox);
        this.add(searchTextBox);
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
        searchOptionsService.getPathOptions(null, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    widget.setValue(result[0]);
                    widget.setAcceptableValues(Arrays.asList(result));
                }
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
        searchOptionsService.getTypeOptions(null, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    widget.setValue(result[0]);
                    widget.setAcceptableValues(Arrays.asList(result));
                }
            }
        });
        return widget;
    }

    private SuggestBox createTextBox() {
        final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        searchOptionsService.getValueOptions(null, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    oracle.clear();
                    for (MetadataFileType type : result) {
                        oracle.add(type.toString());
                    }
                }
            }
        });
        return new SuggestBox(oracle);
    }
}
