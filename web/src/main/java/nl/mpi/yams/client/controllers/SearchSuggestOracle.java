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
package nl.mpi.yams.client.controllers;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.client.SearchSuggestionsStorage;
import nl.mpi.yams.common.data.MetadataFileType;

/**
 * @since Jul 29, 2014 10:26:46 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public abstract class SearchSuggestOracle extends MultiWordSuggestOracle {

    private static final Logger logger = Logger.getLogger("");
    private final SearchSuggestionsStorage searchSuggestionsStorage;
    final private SearchOptionsServiceAsync searchOptionsService;
    static private boolean requestInProgress = false;

    public SearchSuggestOracle(final SearchOptionsServiceAsync searchOptionsService) {
        searchSuggestionsStorage = new SearchSuggestionsStorage();
        this.searchOptionsService = searchOptionsService;
    }

    @Override
    public void requestSuggestions(final SuggestOracle.Request request, final SuggestOracle.Callback callback) {
//        logger.info(request.getQuery());
        final String searchText = getSearchText();
        final String path = getPath();
        final String type = getType();
        updateSuggestOracle(request, callback);
        if (searchSuggestionsStorage.isDone(getDatabaseName(), type, path, searchText) || requestInProgress) {
//            logger.info("relying on old suggestions");
//            logger.info(Boolean.toString(requestInProgress));
        } else {
            requestInProgress = true;
//            logger.info("requesting new suggestions");
            searchSuggestionsStorage.setDone(getDatabaseName(), type, path, searchText);
            setHintRequestStatus(true, "");
            final MetadataFileType options = new MetadataFileType(getType(), getPath(), request.getQuery());
            new MetadataFileTypeLoader(searchOptionsService).loadValueOptions(getDatabaseName(), options, new MetadataFileTypeListener() {
                public void metadataFileTypesLoaded(MetadataFileType[] result) {
                    HashSet<String> suggestions = new HashSet();
                    if (result != null) {
//                        logger.info(result.length + "new suggestions");
                        for (final MetadataFileType type : result) {
//                            logger.info(type.getValue());
                            suggestions.add(type.getValue());
                        }
                        searchSuggestionsStorage.addValues(getDatabaseName(), type, path, suggestions);
                    } else {
//                        logger.info("no new suggestions");
                    }
                    updateSuggestOracle(request, callback);
                    setHintRequestStatus(false, "");
                    requestInProgress = false;
                }

                public void metadataFileTypesLoadFailed(Throwable caught) {
                    requestInProgress = false;
                    setHintRequestStatus(false, "hint: try specifying a type and or path before typing");
                }
            }, request.getLimit());
        }
    }

    private void updateSuggestOracle(final SuggestOracle.Request request, final SuggestOracle.Callback callback) {
//        oracle.clear();
        final String path = getPath();
        final String type = getType();
        final String searchText = getSearchText();
        int resultLimit = request.getLimit();
//        logger.info("loading stored suggestions");
        final String[] values = searchSuggestionsStorage.getValues(getDatabaseName(), type, path);
//        oracle.addAll(Arrays.asList(values));
        ArrayList<SuggestOracle.Suggestion> suggestionList = new ArrayList<SuggestOracle.Suggestion>();
        for (final String entry : values) {
            if (entry.toLowerCase().contains(searchText.toLowerCase())) {
                suggestionList.add(new SuggestOracle.Suggestion() {

                    public String getDisplayString() {
                        return entry;
                    }

                    public String getReplacementString() {
                        return "\"" + entry + "\"";
                    }
                });
//                logger.log(Level.INFO, entry);
                resultLimit--;
                if (resultLimit <= 0) {
                    break;
                }
            }
        }
        SuggestOracle.Response response = new SuggestOracle.Response(suggestionList);
        callback.onSuggestionsReady(request, response);
    }

    public abstract String getType();

    public abstract String getPath();

    public abstract String getSearchText();

    public abstract String getDatabaseName();

    public abstract void setHintRequestStatus(boolean requestInProgress, String hintMessage);
}
