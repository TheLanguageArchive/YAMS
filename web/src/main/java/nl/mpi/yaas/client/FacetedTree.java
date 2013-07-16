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

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import nl.mpi.yaas.common.data.MetadataFileType;

/**
 * Created on : Jul 16, 2013, 11:21:39 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class FacetedTree extends VerticalPanel implements ScheduledCommand {

    final private SearchOptionsServiceAsync searchOptionsService;
    final private ArrayList<MetadataFileType> selectedFacetets = new ArrayList<MetadataFileType>();

    public FacetedTree(SearchOptionsServiceAsync searchOptionsService) {
        this.searchOptionsService = searchOptionsService;
        MenuBar menu = new MenuBar();
        menu.addItem("<please select a facet>", getMenuItems());
        add(menu);
    }

    private MenuBar getMenuItems() {
        MenuBar facetMenu = new MenuBar(true);
        loadTypeOptions(facetMenu, null);
        return facetMenu;
    }

    public void execute() {
        Window.alert("You selected a menu item!");
    }

    private void loadTypeOptions(final MenuBar typeMenu, final MetadataFileType type) {
        searchOptionsService.getTypeOptions(type, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    for (MetadataFileType facetType : result) {
                        MenuBar menu = new MenuBar(true);
                        typeMenu.addItem(facetType.toString(), menu);
                        loadFacetOptions(menu, facetType);
                    }
                }
            }
        });
    }

    private void loadFacetOptions(final MenuBar facetMenu, final MetadataFileType type) {
        searchOptionsService.getPathOptions(type, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    for (MetadataFileType facetType : result) {
//                        MenuBar menu = new MenuBar(true);
                        facetMenu.addItem(facetType.toString(), FacetedTree.this);
//                        loadFacetOptions(facetMenu, facetType);
                    }
                }
            }
        });
    }
}
