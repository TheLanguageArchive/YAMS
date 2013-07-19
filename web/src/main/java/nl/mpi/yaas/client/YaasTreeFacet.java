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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import nl.mpi.yaas.common.data.MetadataFileType;

/**
 * Created on : Jul 18, 2013, 11:23:53 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YaasTreeFacet extends TreeItem {

    private final MetadataFileType metadataFileType;
    final private SearchOptionsServiceAsync searchOptionsService;

    public YaasTreeFacet(MetadataFileType metadataFileType, SearchOptionsServiceAsync searchOptionsService) {
        this.metadataFileType = metadataFileType;
        this.searchOptionsService = searchOptionsService;
        setText(metadataFileType.toString());
    }

    protected void loadChildFacets() {
        searchOptionsService.getPathOptions(metadataFileType, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    for (final MetadataFileType facetType : result) {
                        addItem(new YaasTreeFacet(facetType, searchOptionsService));
                    }
                }
            }
        });
    }
}
