/*
 * Copyright (C) 2013 Peter Withers <peter.withers@mpi.nl>
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
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;
import nl.mpi.yaas.common.data.MetadataFileType;

/**
 * Created on : Jul 18, 2013, 11:23:53 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YaasTreeFacet extends TreeItem {

    private final MetadataFileType selectedFacet;
    final private SearchOptionsServiceAsync searchOptionsService;
    final private int levelIndex;
    final private YaasTreeFacet parentFacet;

    public YaasTreeFacet(MetadataFileType selectedFacet, SearchOptionsServiceAsync searchOptionsService, YaasTreeFacet parentFacet, int levelIndex) {
        this.selectedFacet = selectedFacet;
        this.searchOptionsService = searchOptionsService;
        this.parentFacet = parentFacet;
        this.levelIndex = levelIndex;
        setText(selectedFacet.toString());
    }

    private void getParentFacets(MetadataFileType[] parentFacets) {
        parentFacets[levelIndex] = selectedFacet;
        parentFacet.getParentFacets(parentFacets);
    }

    protected void loadChildFacetsOnce(ArrayList<MetadataFileType[]> selectedFacets) {
        if (getChildCount() == 0) {
            loadChildFacets(selectedFacets);
        }
    }

    protected void loadChildFacets(ArrayList<MetadataFileType[]> selectedFacets) {
        this.addItem(new Image("./loader.gif"));
        MetadataFileType[] parentFacets = new MetadataFileType[levelIndex + 1];
        if (parentFacet != null) {
            parentFacet.getParentFacets(parentFacets);
        }
        parentFacets[levelIndex] = selectedFacets.get(selectedFacets.size() - 1)[1];
        searchOptionsService.getTreeFacets(parentFacets, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                removeItems();
                setText(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                removeItems();
                if (result != null && result.length > 0) {
                    for (final MetadataFileType facetType : result) {
                        addItem(new YaasTreeFacet(facetType, searchOptionsService, YaasTreeFacet.this, levelIndex + 1));
                    }
                }
            }
        });
    }
}
