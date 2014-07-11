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
package nl.mpi.yams.client;

import com.google.gwt.storage.client.Storage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @since Feb 20, 2014 1:55:13 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SearchSuggestionsStorage {

    private static final Logger logger = Logger.getLogger("");
    private static final String DONE_KEY = "|done";
    private static final String DONE_FLAG = "true";
    private String lastKey = null;
    private String[] lastResult = null;

    final private Storage suggestionsStorage;

    public SearchSuggestionsStorage() {
        suggestionsStorage = Storage.getLocalStorageIfSupported();
    }

    private String getKey(String database, String type, String path) {
        return database + "|" + type + "|" + path;
    }

    private String[] getRawValues(String key) {
        final String storedItem = suggestionsStorage.getItem(key);
        if (storedItem != null && storedItem.length() > 3) {
            logger.info(storedItem);
            return storedItem.substring(1, storedItem.length() - 1).split(", ");
        } else {
            return new String[0];
        }
    }

    public String[] getValues(String database, String type, String path) {
        if (suggestionsStorage != null) {
            String key = getKey(database, type, path);
            if (!key.equals(lastKey)) {
                lastResult = getRawValues(key);
            }
            return lastResult;
        } else {
            return new String[0];
        }
    }

    public void addValues(String database, String type, String path, HashSet<String> freshSuggestions) {
        if (suggestionsStorage != null) {
            String key = getKey(database, type, path);
            final List<String> suggestionsList = Arrays.asList(getRawValues(key));
            // make unique and sort
            freshSuggestions.addAll(suggestionsList);
            final List<String> sortedUniqueList = new ArrayList<String>(freshSuggestions);
            Collections.sort(sortedUniqueList, String.CASE_INSENSITIVE_ORDER);
            suggestionsStorage.setItem(key, sortedUniqueList.toString());
        }
    }

    public void setDone(String database, String type, String path, String value) {
        if (suggestionsStorage != null) {
            String key = getKey(database, type, path) + "|" + value + DONE_KEY;
            suggestionsStorage.setItem(key, DONE_FLAG);
        }
    }

    public boolean isDone(String database, String type, String path, String value) {
        if (suggestionsStorage != null) {
            String key = getKey(database, type, path) + "|" + value + DONE_KEY;
            final String doneIndicator = suggestionsStorage.getItem(key);
            if (doneIndicator != null) {
                return doneIndicator.equals(DONE_FLAG);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
