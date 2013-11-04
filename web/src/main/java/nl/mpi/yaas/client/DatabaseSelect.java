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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since Nov 4, 2013 4:02:36 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseSelect extends VerticalPanel {

    private final ListBox databaseListBox = new ListBox();
    private final SearchOptionsServiceAsync searchOptionsService;
    private static final String FAILED_TO_GET_THE_DATABASE_LIST = "Failed to get the database list";
    private static final Logger logger = Logger.getLogger("");
    private final ArrayList<DatabaseNameListener> databaseNameListeners = new ArrayList<DatabaseNameListener>();
    private final String databaseName;

    public DatabaseSelect(SearchOptionsServiceAsync searchOptionsService, String databaseName, DatabaseNameListener databaseNameListener) {
        this.searchOptionsService = searchOptionsService;
        add(databaseListBox);
        databaseNameListeners.add(databaseNameListener);
        this.databaseName = databaseName;
    }

    public void getDbList() {
        searchOptionsService.getDatabaseList(new AsyncCallback<String[]>() {
            public void onFailure(Throwable caught) {
                add(new Label(FAILED_TO_GET_THE_DATABASE_LIST));
                logger.log(Level.SEVERE, FAILED_TO_GET_THE_DATABASE_LIST, caught);
            }

            public void onSuccess(String[] result) {
                int selectedIndex = 0;
                databaseListBox.addItem("<please select a database>");
                for (String databaseNameItem : result) {
                    databaseListBox.addItem(databaseNameItem);
                    if (databaseNameItem.equals(databaseName)) {
                        selectedIndex = databaseListBox.getItemCount() - 1;
                    }
                }
                databaseListBox.setSelectedIndex(selectedIndex);
                databaseListBox.addChangeHandler(new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        for (DatabaseNameListener databaseNameListener : databaseNameListeners) {
                            databaseNameListener.setDataBaseName(databaseListBox.getItemText(databaseListBox.getSelectedIndex()));
                        }
                    }
                });
            }
        });
    }
}
