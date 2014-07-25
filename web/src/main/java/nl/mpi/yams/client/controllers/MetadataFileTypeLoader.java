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
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.yams.client.SearchOptionsServiceAsync;
import nl.mpi.yams.client.ServiceLocations;
import nl.mpi.yams.common.data.MetadataFileType;
import nl.mpi.yams.shared.JsonMetadataFileType;
import nl.mpi.yams.shared.WebQueryException;

/**
 * @since Jul 25, 2014 11:34:06 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class MetadataFileTypeLoader {

    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);
    private static final Logger logger = Logger.getLogger("");
    private final SearchOptionsServiceAsync searchOptionsService;

    public MetadataFileTypeLoader(SearchOptionsServiceAsync searchOptionsService) {
        this.searchOptionsService = searchOptionsService;
    }

    public void loadTypesOptions(final String databaseName, final MetadataFileTypeListener listener) {
        if (searchOptionsService != null) {
            loadTypesOptionsRpc(databaseName, listener);
        } else {
            loadTypesOptionsJson(serviceLocations.jsonMetadataTypesUrl(serviceLocations.jsonBasexAdaptorUrl(), databaseName), listener);
        }
    }

    public void loadPathOptions(final String databaseName, MetadataFileType type, final MetadataFileTypeListener listener) {
        if (searchOptionsService != null) {
            loadPathOptionsRpc(databaseName, type, listener);
        } else {
            loadTypesOptionsJson(serviceLocations.jsonMetadataPathsUrl(serviceLocations.jsonBasexAdaptorUrl(), databaseName, type.getType()), listener);
        }
    }

    private void loadPathOptionsRpc(final String databaseName, MetadataFileType type, final MetadataFileTypeListener listener) {
        searchOptionsService.getPathOptions(databaseName, type, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.getMessage());
                listener.metadataFileTypesLoadFailed(caught);
            }

            public void onSuccess(MetadataFileType[] result) {
                listener.metadataFileTypesLoaded(result);
            }
        });
    }

    private void loadTypesOptionsRpc(final String databaseName, final MetadataFileTypeListener listener) {
        searchOptionsService.getTypeOptions(databaseName, null, new AsyncCallback<MetadataFileType[]>() {
            public void onFailure(Throwable caught) {
                logger.info("loadTypesOptions");
                logger.info(databaseName);
                logger.log(Level.SEVERE, caught.getMessage());
                listener.metadataFileTypesLoadFailed(caught);
            }

            public void onSuccess(MetadataFileType[] result) {
                listener.metadataFileTypesLoaded(result);
            }
        });
    }

    private void loadTypesOptionsJson(final String jsonDbTypesUrl, final MetadataFileTypeListener listener) {
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, jsonDbTypesUrl);
        try {
            final Request request = builder.sendRequest(null, new RequestCallback() {
//
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        final String text = response.getText();
                        logger.info("onResponseReceived");
                        logger.info(jsonDbTypesUrl);
                        logger.info(text);
                        logger.info("onResponseReceivedEnd");
                        final JsArray<JsonMetadataFileType> jsonArray = JsonUtils.safeEval(text);
                        MetadataFileType[] results = new MetadataFileType[jsonArray.length()];

                        for (int index = 0; index < jsonArray.length(); index++) {
                            final JsonMetadataFileType fileType = (JsonMetadataFileType) jsonArray.get(index);
                            results[index] = new MetadataFileType(fileType.getType(), fileType.getPath(), fileType.getLabel(), fileType.getCount());
                        }
                        listener.metadataFileTypesLoaded(results);
                    } else {
                        logger.warning("Couldn't retrieve JSON: non 200");
                        logger.warning(jsonDbTypesUrl);
                        logger.warning(response.getStatusText());
                        logger.warning("StatusCode: " + Integer.toString(response.getStatusCode()));
                        listener.metadataFileTypesLoadFailed(new WebQueryException("Couldn't retrieve JSON: " + Integer.toString(response.getStatusCode())));
                    }
                }

                public void onError(Request request, Throwable exception) {
                    logger.log(Level.SEVERE, exception.getMessage());
                    listener.metadataFileTypesLoadFailed(exception);
                }
            });
        } catch (RequestException exception) {
            logger.log(Level.SEVERE, exception.getMessage());
            listener.metadataFileTypesLoadFailed(exception);
        }
    }
}
