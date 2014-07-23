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
package nl.mpi.yams.client.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.common.data.MetadataFileType;

/**
 * Created on : Jul 18, 2013, 11:23:53 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YamsTreeFacet extends TreeItem {

    private static final Logger logger = Logger.getLogger("");
    private final MetadataFileType selectedFacet;
    final private SearchOptionsServiceAsync searchOptionsService;
    final private int levelIndex;
    final private YamsTreeFacet parentFacet;
    private boolean loadRequestMade = false;
    private final String databaseName;

    public YamsTreeFacet(String databaseName, MetadataFileType selectedFacet, SearchOptionsServiceAsync searchOptionsService, YamsTreeFacet parentFacet, int levelIndex) {
        this.databaseName = databaseName;
        this.selectedFacet = selectedFacet;
        this.searchOptionsService = searchOptionsService;
        this.parentFacet = parentFacet;
        this.levelIndex = levelIndex;
        if (selectedFacet == null) {
            setText("Selected Facets");
        } else {
            setText(selectedFacet.toString());
        }
    }

    private void getParentFacets(MetadataFileType[] parentFacets) {
        if (levelIndex > 0) {
            logger.log(Level.INFO, "levelIndex: " + levelIndex + " : " + selectedFacet.toString());
            parentFacets[levelIndex] = selectedFacet;
            parentFacet.getParentFacets(parentFacets);
        }
    }

    protected synchronized void loadChildFacetsOnce(ArrayList<MetadataFileType[]> selectedFacets) {
        if (!loadRequestMade) {
            loadRequestMade = true;
            loadChildFacets(selectedFacets);
        }
    }

    protected void loadChildFacets(ArrayList<MetadataFileType[]> selectedFacets) {
        this.addItem(new Image("./loader.gif"));
        MetadataFileType[] parentFacets = new MetadataFileType[levelIndex + 2];
        if (parentFacet != null) {
            getParentFacets(parentFacets);
        }
        parentFacets[levelIndex + 1] = selectedFacets.get(selectedFacets.size() - 1)[1];
        searchOptionsService.getTreeFacets(databaseName, parentFacets, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                removeItems();
                setText(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                removeItems();
                if (result != null && result.length > 0) {
                    for (final MetadataFileType facetType : result) {
                        addItem(new YamsTreeFacet(databaseName, facetType, searchOptionsService, YamsTreeFacet.this, levelIndex + 1));
                    }
                }
            }
        });
    }
}
