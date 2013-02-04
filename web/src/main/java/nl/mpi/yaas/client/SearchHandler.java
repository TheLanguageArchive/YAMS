/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
