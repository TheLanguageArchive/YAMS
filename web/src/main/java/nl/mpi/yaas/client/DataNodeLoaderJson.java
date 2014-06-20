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
package nl.mpi.yaas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeId;
import nl.mpi.yaas.shared.JsonDataNode;
import nl.mpi.yaas.shared.WebQueryException;

/**
 * @since Mar 26, 2014 1:28:03 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeLoaderJson implements DataNodeLoader {

    private static final Logger logger = Logger.getLogger("");
    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);

    public DataNodeLoaderJson() {
    }

    public void requestLoadRoot(final DataNodeLoaderListener dataNodeLoaderListener) {
        // Send request to server and catch any errors.
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceLocations.jsonUrl());
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.warning(e.getMessage());
        }
    }

    public void requestLoadChildrenOf(DataNodeId dataNodeId, int first, int last, DataNodeLoaderListener dataNodeLoaderListener) {
        // Send request to server and catch any errors.
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, serviceLocations.jsonLinksOfUrl() + dataNodeId.getIdString());
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.warning(e.getMessage());
        }
    }

    public void requestLoad(List<DataNodeId> dataNodeIdList, final DataNodeLoaderListener dataNodeLoaderListener) {
        throw new UnsupportedOperationException("Not supported yet.");
//        searchOptionsService.getDataNodes(databaseName, dataNodeIdList, new AsyncCallback<List<SerialisableDataNode>>() {
//            public void onFailure(Throwable caught) {
//                dataNodeLoaderListener.dataNodeLoadFailed(caught);
//            }
//
//            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
//                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
//            }
//        });
    }

    public void requestLoadHdl(List<String> dataNodeHdlList, final DataNodeLoaderListener dataNodeLoaderListener) {
        throw new UnsupportedOperationException("Not supported yet.");
//        searchOptionsService.getDataNodesByHdl(databaseName, dataNodeHdlList, new AsyncCallback<List<SerialisableDataNode>>() {
//            public void onFailure(Throwable caught) {
//                dataNodeLoaderListener.dataNodeLoadFailed(caught);
//            }
//
//            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
//                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
//            }
//        });
    }

    public void requestLoadUri(List<String> dataNodeUriList, final DataNodeLoaderListener dataNodeLoaderListener) {
        throw new UnsupportedOperationException("Not supported yet.");
//        searchOptionsService.getDataNodesByUrl(databaseName, dataNodeUriList, new AsyncCallback<List<SerialisableDataNode>>() {
//            public void onFailure(Throwable caught) {
//                dataNodeLoaderListener.dataNodeLoadFailed(caught);
//            }
//
//            public void onSuccess(List<SerialisableDataNode> dataNodeList) {
//                dataNodeLoaderListener.dataNodeLoaded(dataNodeList);
//            }
//        });
    }

    public String getNodeIcon(SerialisableDataNode yaasDataNode) {
        return yaasDataNode.getType().getID();
    }

    private RequestCallback geRequestBuilder(final RequestBuilder builder, final DataNodeLoaderListener dataNodeLoaderListener) {
        return new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                dataNodeLoaderListener.dataNodeLoadFailed(exception);
                logger.warning("Couldn't retrieve JSON from: ");
                logger.warning(builder.getUrl());
            }

            public void onResponseReceived(Request request, Response response) {
                if (200 == response.getStatusCode()) {
                    final String text = response.getText();
                    logger.info(text);
                    final JsArray<JsonDataNode> jsonArray = JsonUtils.safeEval(text);
                    List<SerialisableDataNode> dataNodes = new ArrayList<SerialisableDataNode>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        final JsonDataNode jsonDataNode = (JsonDataNode) jsonArray.get(index);
                        dataNodes.add(new SerialisableDataNode() {

                            @Override
                            public DataNodeType getType() {
                                final DataNodeType dataNodeType = new DataNodeType(jsonDataNode.getLabel(), jsonDataNode.getTypeName(), jsonDataNode.getTypeID(), DataNodeType.FormatType.valueOf(jsonDataNode.getTypeFormat()));
                                return dataNodeType;
                            }

                            @Override
                            public DataNodePermissions getPermissions() {
                                final DataNodePermissions dataNodePermissions = new DataNodePermissions();
                                dataNodePermissions.setAccessLevel(DataNodePermissions.AccessLevel.valueOf(jsonDataNode.getTypeAccessLevel()));
                                return dataNodePermissions;
                            }

                            @Override
                            public String getLabel() {
                                return jsonDataNode.getLabel();
                            }

                            @Override
                            public String getArchiveHandle() {
                                return jsonDataNode.getArchiveHandle();
                            }

                            @Override
                            public String getURI() throws ModelException {
                                return jsonDataNode.getURI();
                            }

                            @Override
                            public String getID() throws ModelException {
                                return jsonDataNode.getID();
                            }

                            @Override
                            public Integer getLinkCount() {
                                return jsonDataNode.getLinkCount();
                            }

                        });
                    }
                    dataNodeLoaderListener.dataNodeLoaded(dataNodes);
                } else {
                    dataNodeLoaderListener.dataNodeLoadFailed(new WebQueryException("Couldn't retrieve JSON: " + response.getStatusCode()));
                    logger.warning("Couldn't retrieve JSON");
                    logger.warning(response.getStatusText());
                }
            }
        };
    }
}
