/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.data.QueryDataStructures.CriterionJoinType;
import nl.mpi.yaas.common.data.QueryDataStructures.SearchOption;
import nl.mpi.yaas.common.data.SearchParameters;
import nl.mpi.yaas.shared.YaasDataNode;

/**
 * Created on : Jan 29, 2013, 2:50:44 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SearchPanel extends VerticalPanel {

    private final SearchOptionsServiceAsync searchOptionsService = GWT.create(SearchOptionsService.class);
    private Button searchButton;
    private SearchHandler searchHandler;
    private final DataNodeTree dataNodeTree;
    final ValueListBox<CriterionJoinType> joinTypeListBox;

    public SearchPanel(DataNodeTree dataNodeTree) {
        this.dataNodeTree = dataNodeTree;
        final VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(getSearchRow(verticalPanel));
        Button addRowButton = new Button("add search term", new ClickHandler() {
            public void onClick(ClickEvent event) {
                verticalPanel.add(getSearchRow(verticalPanel));
            }
        });
        this.add(verticalPanel);
        final HorizontalPanel buttonsPanel = new HorizontalPanel();
        this.add(addRowButton);
        joinTypeListBox = getJoinTypeListBox();
        buttonsPanel.add(joinTypeListBox);
        buttonsPanel.add(getSearchButton());
        this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        this.add(buttonsPanel);
    }

    private Widget getSearchButton() {
        searchButton = new Button("Search");
        searchButton.addStyleName("sendButton");

        searchHandler = new SearchHandler() {
            @Override
            void performSearch() {
                searchButton.setEnabled(false);


                ArrayList<SearchParameters> searchParametersList = new ArrayList<SearchParameters>();
//                    for (SearchCriterionPanel eventCriterionPanel : criterionPanelArray) {
//                        searchParametersList.add(new SearchParameters(eventCriterionPanel.getMetadataFileType(), eventCriterionPanel.getMetadataFieldType(), eventCriterionPanel.getSearchNegator(), eventCriterionPanel.getSearchType(), eventCriterionPanel.getSearchText()));
//                    }

                searchOptionsService.performSearch(joinTypeListBox.getValue(), null,
                        new AsyncCallback<YaasDataNode>() {
                            public void onFailure(Throwable caught) {
                                Window.alert(caught.getMessage());
                                searchHandler.signalSearchDone();
                                searchButton.setEnabled(true);
                            }

                            public void onSuccess(YaasDataNode result) {
                                final TreeItem treeItem = new TreeItem();
                                treeItem.setText(result.getName());
                                dataNodeTree.setRootNode(treeItem);
                                dataNodeTree.setRootNode(treeItem);
                                searchHandler.signalSearchDone();
                                searchButton.setEnabled(true);
                            }
                        });
            }
        };
        searchButton.addClickHandler(searchHandler);
        return searchButton;
    }

    private Widget getSearchRow(final VerticalPanel verticalPanel) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        Button removeRowButton = new Button("remove", new ClickHandler() {
            public void onClick(ClickEvent event) {
                verticalPanel.remove(horizontalPanel);
            }
        });
        horizontalPanel.add(removeRowButton);
        SuggestBox suggestbox = new SuggestBox(createCountriesOracle());
        horizontalPanel.add(getTypesOptionsListBox());
        horizontalPanel.add(getFieldsOptionsListBox());
        horizontalPanel.add(getSearchOptionsListBox());
        horizontalPanel.add(suggestbox);
        return horizontalPanel;
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
        searchOptionsService.getFieldOptions(new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                widget.setAcceptableValues(Arrays.asList(result));
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
        searchOptionsService.getTypeOptions(new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                widget.setAcceptableValues(Arrays.asList(result));
            }
        });
        return widget;
    }

    private ValueListBox getSearchOptionsListBox() {
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
        widget.setAcceptableValues(Arrays.asList(CriterionJoinType.values()));
        widget.setValue(CriterionJoinType.intersect);
        return widget;
    }

    private MultiWordSuggestOracle createCountriesOracle() {
        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

        oracle.add("Afghanistan");
        oracle.add("Albania");
        oracle.add("Algeria");
        oracle.add("American Samoa");
        oracle.add("Andorra");
        oracle.add("Angola");
        oracle.add("Anguilla");
        oracle.add("Antarctica");
        oracle.add("Antigua And Barbuda");
        oracle.add("Argentina");
        oracle.add("Armenia");
        oracle.add("Aruba");
        oracle.add("Australia");
        oracle.add("Austria");
        oracle.add("Azerbaijan");
        oracle.add("Bahamas");
        oracle.add("Bahrain");
        oracle.add("Bangladesh");
        oracle.add("Barbados");
        oracle.add("Belarus");
        oracle.add("Belgium");
        oracle.add("Belize");
        oracle.add("Benin");
        oracle.add("Bermuda");
        oracle.add("Bhutan");
        oracle.add("Bolivia");
        oracle.add("Bosnia And Herzegovina");
        oracle.add("Botswana");
        oracle.add("Bouvet Island");
        oracle.add("Brazil");
        oracle.add("British Indian Ocean Territory");
        oracle.add("Brunei Darussalam");
        oracle.add("Bulgaria");
        oracle.add("Burkina Faso");
        oracle.add("Burundi");
        oracle.add("Cambodia");
        oracle.add("Cameroon");
        oracle.add("Canada");
        oracle.add("Cape Verde");
        oracle.add("Cayman Islands");
        oracle.add("Central African Republic");
        oracle.add("Chad");
        oracle.add("Chile");
        oracle.add("China");
        oracle.add("Christmas Island");
        oracle.add("Cocos (Keeling) Islands");
        oracle.add("Colombia");
        oracle.add("Comoros");
        oracle.add("Congo, The Democratic Republic Of The");
        oracle.add("Congo");
        oracle.add("Cook Islands");
        oracle.add("Costa Rica");
        oracle.add("Cote D''ivoire");
        oracle.add("Croatia");
        oracle.add("Cuba");
        oracle.add("Cyprus");
        oracle.add("Czech Republic");
        oracle.add("Denmark");
        oracle.add("Djibouti");
        oracle.add("Dominica");
        oracle.add("Dominican Republic");
        oracle.add("East Timor");
        oracle.add("Ecuador");
        oracle.add("Egypt");
        oracle.add("El Salvador");
        oracle.add("Equatorial Guinea");
        oracle.add("Eritrea");
        oracle.add("Estonia");
        oracle.add("Ethiopia");
        oracle.add("Falkland Islands (Malvinas)");
        oracle.add("Faroe Islands");
        oracle.add("Fiji");
        oracle.add("Finland");
        oracle.add("France");
        oracle.add("French Guiana");
        oracle.add("French Polynesia");
        oracle.add("French Southern Territories");
        oracle.add("Gabon");
        oracle.add("Gambia");
        oracle.add("Georgia");
        oracle.add("Germany");
        oracle.add("Ghana");
        oracle.add("Gibraltar");
        oracle.add("Greece");
        oracle.add("Greenland");
        oracle.add("Grenada");
        oracle.add("Guadeloupe");
        oracle.add("Guam");
        oracle.add("Guatemala");
        oracle.add("Guinea-Bissau");
        oracle.add("Guinea");
        oracle.add("Guyana");
        oracle.add("Haiti");
        oracle.add("Heard Island And Mcdonald Islands");
        oracle.add("Holy See (Vatican City State)");
        oracle.add("Honduras");
        oracle.add("Hong Kong");
        oracle.add("Hungary");
        oracle.add("Iceland");
        oracle.add("India");
        oracle.add("Indonesia");
        oracle.add("Iran, Islamic Republic Of");
        oracle.add("Iraq");
        oracle.add("Ireland");
        oracle.add("Israel");
        oracle.add("Italy");
        oracle.add("Jamaica");
        oracle.add("Japan");
        oracle.add("Jordan");
        oracle.add("Kazakstan");
        oracle.add("Kenya");
        oracle.add("Kiribati");
        oracle.add("Korea, Democratic People''s Republic Of");
        oracle.add("Korea, Republic Of");
        oracle.add("Kuwait");
        oracle.add("Kyrgyzstan");
        oracle.add("Lao People''s Democratic Republic");
        oracle.add("Latvia");
        oracle.add("Lebanon");
        oracle.add("Lesotho");
        oracle.add("Liberia");
        oracle.add("Libyan Arab Jamahiriya");
        oracle.add("Liechtenstein");
        oracle.add("Lithuania");
        oracle.add("Luxembourg");
        oracle.add("Macau");
        oracle.add("Macedonia, The Former Yugoslav Republic Of");
        oracle.add("Madagascar");
        oracle.add("Malawi");
        oracle.add("Malaysia");
        oracle.add("Maldives");
        oracle.add("Mali");
        oracle.add("Malta");
        oracle.add("Marshall Islands");
        oracle.add("Martinique");
        oracle.add("Mauritania");
        oracle.add("Mauritius");
        oracle.add("Mayotte");
        oracle.add("Mexico");
        oracle.add("Micronesia, Federated States Of");
        oracle.add("Moldova, Republic Of");
        oracle.add("Monaco");
        oracle.add("Mongolia");
        oracle.add("Montserrat");
        oracle.add("Morocco");
        oracle.add("Mozambique");
        oracle.add("Myanmar");
        oracle.add("Namibia");
        oracle.add("Nauru");
        oracle.add("Nepal");
        oracle.add("Netherlands Antilles");
        oracle.add("Netherlands");
        oracle.add("New Caledonia");
        oracle.add("New Zealand");
        oracle.add("Nicaragua");
        oracle.add("Niger");
        oracle.add("Nigeria");
        oracle.add("Niue");
        oracle.add("Norfolk Island");
        oracle.add("Northern Mariana Islands");
        oracle.add("Norway");
        oracle.add("Oman");
        oracle.add("Pakistan");
        oracle.add("Palau");
        oracle.add("Palestinian Territory, Occupied");
        oracle.add("Panama");
        oracle.add("Papua New Guinea");
        oracle.add("Paraguay");
        oracle.add("Peru");
        oracle.add("Philippines");
        oracle.add("Pitcairn");
        oracle.add("Poland");
        oracle.add("Portugal");
        oracle.add("Puerto Rico");
        oracle.add("Qatar");
        oracle.add("Reunion");
        oracle.add("Romania");
        oracle.add("Russian Federation");
        oracle.add("Rwanda");
        oracle.add("Saint Helena");
        oracle.add("Saint Kitts And Nevis");
        oracle.add("Saint Lucia");
        oracle.add("Saint Pierre And Miquelon");
        oracle.add("Saint Vincent And The Grenadines");
        oracle.add("Samoa");
        oracle.add("San Marino");
        oracle.add("Sao Tome And Principe");
        oracle.add("Saudi Arabia");
        oracle.add("Senegal");
        oracle.add("Seychelles");
        oracle.add("Sierra Leone");
        oracle.add("Singapore");
        oracle.add("Slovakia");
        oracle.add("Slovenia");
        oracle.add("Solomon Islands");
        oracle.add("Somalia");
        oracle.add("South Africa");
        oracle.add("South Georgia And The South Sandwich Islands");
        oracle.add("Spain");
        oracle.add("Sri Lanka");
        oracle.add("Sudan");
        oracle.add("Suriname");
        oracle.add("Svalbard And Jan Mayen");
        oracle.add("Swaziland");
        oracle.add("Sweden");
        oracle.add("Switzerland");
        oracle.add("Syrian Arab Republic");
        oracle.add("Taiwan, Province Of China");
        oracle.add("Tajikistan");
        oracle.add("Tanzania, United Republic Of");
        oracle.add("Thailand");
        oracle.add("Togo");
        oracle.add("Tokelau");
        oracle.add("Tonga");
        oracle.add("Trinidad And Tobago");
        oracle.add("Tunisia");
        oracle.add("Turkey");
        oracle.add("Turkmenistan");
        oracle.add("Turks And Caicos Islands");
        oracle.add("Tuvalu");
        oracle.add("Uganda");
        oracle.add("Ukraine");
        oracle.add("United Arab Emirates");
        oracle.add("United Kingdom");
        oracle.add("United States Minor Outlying Islands");
        oracle.add("United States");
        oracle.add("Uruguay");
        oracle.add("Uzbekistan");
        oracle.add("Vanuatu");
        oracle.add("Venezuela");
        oracle.add("Viet Nam");
        oracle.add("Virgin Islands, British");
        oracle.add("Virgin Islands, U.S.");
        oracle.add("Wallis And Futuna");
        oracle.add("Western Sahara");
        oracle.add("Yemen");
        oracle.add("Yugoslavia");
        oracle.add("Zambia");
        oracle.add("Zimbabwe");

        return oracle;
    }
}
