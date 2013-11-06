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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Created on : Feb 4, 2013, 11:07:09 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public abstract class SearchHandler implements ClickHandler, KeyUpHandler {

    boolean searchInProgress = false;
    private final Object searchLockObject = new Object();

    public void onClick(ClickEvent event) {
        initiateSearch();
    }

    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            initiateSearch();
        }
    }

    private void initiateSearch() {
        synchronized (searchLockObject) {
            if (!searchInProgress) {
                searchInProgress = true;
                performSearch();
            }
        }
    }

    public void signalSearchDone() {
        searchInProgress = false;
    }

    abstract void performSearch();
}
