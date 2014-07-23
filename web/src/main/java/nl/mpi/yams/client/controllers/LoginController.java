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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import java.util.logging.Logger;
import nl.mpi.yams.client.ServiceLocations;
import nl.mpi.yams.shared.LoginStatus;

/**
 * @since May 12, 2014 11:43:43 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class LoginController {

    private static final Logger logger = Logger.getLogger("");
    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);
    final private int timeTillNextCheck = 10 * 60 * 1000; // the timer will be set for 10 minutes
    final private Timer timer;
    private final ActionsPanelController actionsPanelController;

    public LoginController(ActionsPanelController actionsPanelController) {
        this.actionsPanelController = actionsPanelController;
        timer = new Timer() {
            public void run() {
                checkLoginState();
                timer.schedule(timeTillNextCheck);
            }
        };
    }

    public void startStatusTimer() {
        if (!timer.isRunning()) {
            timer.schedule(timeTillNextCheck);
        }
    }

    public native void exportCheckLoginState(LoginController loginController) /*-{
     $wnd.checkLoginState = function (){ loginController.@nl.mpi.yams.client.controllers.LoginController::checkLoginState()(); }
     }-*/;

    protected void checkLoginState() {
        // Send request to server and catch any errors.
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceLocations.statusUrl());

        try {
            Request request = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    logger.warning("Couldn't retrieve JSON");
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        final String text = response.getText();
                        logger.info(text);
                        final JsArray<LoginStatus> jsonData = JsonUtils.safeEval(text);
                        logger.info(Integer.toString(jsonData.length()));
                        logger.info(jsonData.get(0).getRemoteUser());
                        actionsPanelController.setLoginState(jsonData.get(0).getRemoteUser(), jsonData.get(0).isAnonymous());
                    } else {
                        logger.warning("Couldn't retrieve JSON");
                        logger.warning(response.getStatusText());
                    }
                }
            });
        } catch (RequestException e) {
            logger.warning("Couldn't retrieve JSON");
            logger.warning(e.getMessage());
        }
    }
}
