/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ValueListBox;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures;

/**
 * Created on : Feb 5, 2013, 11:44:03 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SearchCriterionPanel extends HorizontalPanel {

    final SearchPanel searchPanel;
    final ValueListBox<MetadataFileType> typesOptionsListBox;
    final ValueListBox<MetadataFileType> fieldsOptionsListBox;
    final ValueListBox<QueryDataStructures.SearchOption> searchOptionsListBox;
    final SuggestBox searchStringbox;

    public SearchCriterionPanel(final SearchPanel searchPanel) {
        this.searchPanel = searchPanel;
        Button removeRowButton = new Button("remove", new ClickHandler() {
            public void onClick(ClickEvent event) {
                searchPanel.removeSearchCriterionPanel(SearchCriterionPanel.this);
            }
        });
        this.add(removeRowButton);
        searchStringbox = new SuggestBox(searchPanel.createCountriesOracle());
        typesOptionsListBox = searchPanel.getTypesOptionsListBox();
        this.add(typesOptionsListBox);
        fieldsOptionsListBox = searchPanel.getFieldsOptionsListBox();
        this.add(fieldsOptionsListBox);
        searchOptionsListBox = searchPanel.getSearchOptionsListBox();
        this.add(searchOptionsListBox);
        this.add(searchStringbox);
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

    public String getSearchText() {
        return searchStringbox.getText();
    }
}
