/**
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
package nl.mpi.yaas.client;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yaas.common.data.MetadataFileType;

/**
 * Created on : Jul 16, 2013, 11:21:39 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class FacetedTree extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");
    final private SearchOptionsServiceAsync searchOptionsService;
    final private ArrayList<MetadataFileType[]> selectedFacets = new ArrayList<MetadataFileType[]>();
    final private MenuBar menuBar;
    final private Tree facetTree;
    private final String databaseName;

    public FacetedTree(SearchOptionsServiceAsync searchOptionsService, String databaseName) {
        this.searchOptionsService = searchOptionsService;
        this.databaseName = databaseName;
        menuBar = new MenuBar();
        facetTree = new Tree();
        add(menuBar);
        add(facetTree);
        updateRootMenus(menuBar);
        facetTree.addOpenHandler(new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> event) {
                final TreeItem selectedItem = event.getTarget();
                for (int childCount = 0; childCount < selectedItem.getChildCount(); childCount++) {
                    final TreeItem child = selectedItem.getChild(childCount);
                    if (child instanceof YaasTreeFacet) {
                        YaasTreeFacet treeFacet = (YaasTreeFacet) child;
                        treeFacet.loadChildFacetsOnce(selectedFacets);
                    }
                }
            }
        });
    }

    private void updateRootMenus(MenuBar menuBar) {
        menuBar.clearItems();
        facetTree.clear();
        int menuIndex = 0;
        for (MetadataFileType[] type : selectedFacets) {
            String labelString = "";
            if (type[0].getType() != null && type[1].getPath() != null) {
                labelString = type[0].getType() + " / " + type[1].toString();
            } else {
                labelString += type[1].toString();
            }
            menuBar.addItem(labelString, getMenuItems(menuIndex++));
        }
        if (!selectedFacets.isEmpty()) {
            final YaasTreeFacet yaasTreeFacet = new YaasTreeFacet(databaseName, null, searchOptionsService, null, -1);
            facetTree.addItem(yaasTreeFacet);
            yaasTreeFacet.loadChildFacets(selectedFacets);
        }
        if (selectedFacets.isEmpty()) {
            menuBar.addItem("<please select a facet>", getMenuItems(menuIndex++));
        } else {
            menuBar.addItem("<add another facet>", getMenuItems(menuIndex++));
        }
    }

    private MenuBar getMenuItems(int menuIndex) {
        MenuBar facetMenu = new MenuBar(true);
        loadTypeOptions(facetMenu, null, menuIndex);
        return facetMenu;
    }

    public void execute() {
        Window.alert("You selected a menu item!");
    }

    private void loadTypeOptions(final MenuBar typeMenu, final MetadataFileType type, final int menuIndex) {
        searchOptionsService.getTypeOptions(databaseName, type, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    for (MetadataFileType facetType : result) {
                        MenuBar menu = new MenuBar(true);
                        typeMenu.addItem(facetType.toString(), menu);
                        loadFacetOptions(menu, facetType, menuIndex);
                    }
                }
                if (selectedFacets.size() > menuIndex) {
                    typeMenu.addItem("<Remove>", new ScheduledCommand() {
                        public void execute() {
                            selectedFacets.remove(menuIndex);
                            updateRootMenus(menuBar);
                        }
                    });
                }
            }
        });
    }

    private void loadFacetOptions(final MenuBar facetMenu, final MetadataFileType type, final int menuIndex) {
        searchOptionsService.getPathOptions(databaseName, type, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
            }

            public void onSuccess(MetadataFileType[] result) {
                if (result != null && result.length > 0) {
                    for (final MetadataFileType facetType : result) {
                        facetMenu.addItem(facetType.toString(), new ScheduledCommand() {
                            public void execute() {
                                if (selectedFacets.size() <= menuIndex) {
                                    selectedFacets.add(new MetadataFileType[]{type, facetType});
                                } else {
                                    selectedFacets.set(menuIndex, new MetadataFileType[]{type, facetType});
                                }
                                updateRootMenus(menuBar);
                            }
                        });
                    }
                }
            }
        });
    }
}
