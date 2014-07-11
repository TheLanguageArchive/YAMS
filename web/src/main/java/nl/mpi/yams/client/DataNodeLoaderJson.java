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
package nl.mpi.yams.client;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.shared.JsonDataNode;
import nl.mpi.yams.shared.WebQueryException;

/**
 * @since Mar 26, 2014 1:28:03 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeLoaderJson implements DataNodeLoader {

    private static final Logger logger = Logger.getLogger("");
    final private ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);
    final String jsonUrl;

    public DataNodeLoaderJson() {
        jsonUrl = serviceLocations.jsonCsAdaptorUrl();
    }

    public DataNodeLoaderJson(String databaseName) {
        jsonUrl = serviceLocations.jsonYamsRestUrl(databaseName);
    }

    public void requestLoadRoot(final DataNodeLoaderListener dataNodeLoaderListener) {
        final String jsonRootUrl = serviceLocations.jsonRootNode(jsonUrl);
        // Send request to server and catch any errors.
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, jsonRootUrl);
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener, jsonRootUrl));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.log(Level.SEVERE, "requestLoadRoot", e);
        }
    }

    public void requestLoadChildrenOf(DataNodeId dataNodeId, int first, int last, DataNodeLoaderListener dataNodeLoaderListener) {
//        logger.info("requestLoadChildrenOf");
        final String jsonLinksOfUrl = serviceLocations.jsonLinksOfUrl(jsonUrl, dataNodeId.getIdString());
//        logger.info(jsonLinksOfUrl);
        // Send request to server and catch any errors.
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, jsonLinksOfUrl);
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener, jsonLinksOfUrl));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.log(Level.SEVERE, "requestLoadChildrenOf", e);
        }
    }

    public void requestLoad(List<DataNodeId> dataNodeIdList, final DataNodeLoaderListener dataNodeLoaderListener) {
//        logger.info("requestLoad");
// Send request to server and catch any errors.
        StringBuilder stringBuilder = new StringBuilder();
        final String jsonNodesOfUrl = serviceLocations.jsonNodeOfUrl(jsonUrl);
        stringBuilder.append(jsonNodesOfUrl);
        stringBuilder.append("?");
        for (DataNodeId dataNodeId : dataNodeIdList) {
            stringBuilder.append(serviceLocations.jsonNodeGetVar());
            stringBuilder.append(dataNodeId.getIdString());
            stringBuilder.append("&");
        }
//        final String jsonLinksOfUrl = serviceLocations.jsonYamsDataUrl(jsonUrl, stringBuilder.toString());
        final String restNodeUrl = stringBuilder.toString();
//        logger.warning(restNodeUrl);
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, restNodeUrl);
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener, restNodeUrl));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.log(Level.SEVERE, "requestLoad", e);
        }
    }

    public void requestLoadHdl(List<String> dataNodeHdlList, final DataNodeLoaderListener dataNodeLoaderListener) {
//        logger.info("requestLoadHdl");
        // Send request to server and catch any errors.
        StringBuilder stringBuilder = new StringBuilder();
        final String jsonNodesOfUrl = serviceLocations.jsonNodeOfUrl(jsonUrl);
        stringBuilder.append(jsonNodesOfUrl);
        stringBuilder.append("?");
        for (String dataNodeHdl : dataNodeHdlList) {
            stringBuilder.append(serviceLocations.jsonNodeGetVar());
            stringBuilder.append(dataNodeHdl);
            stringBuilder.append("&");
        }
        final String restNodeUrl = stringBuilder.toString();
//        logger.warning(restNodeUrl);
//        final String jsonLinksOfUrl = serviceLocations.jsonLinksOfUrl(jsonUrl, stringBuilder.toString());
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, restNodeUrl);
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener, restNodeUrl));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.log(Level.SEVERE, "requestLoadHdl", e);
        }
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

    private RequestCallback geRequestBuilder(final RequestBuilder builder, final DataNodeLoaderListener dataNodeLoaderListener, final String targetUri) {
        return new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                dataNodeLoaderListener.dataNodeLoadFailed(exception);
                logger.warning("Couldn't retrieve JSON from: ");
                logger.warning(builder.getUrl());
            }

            public void onResponseReceived(Request request, Response response) {
                if (200 == response.getStatusCode()) {
                    final String text = response.getText();
//                    logger.info("onResponseReceived");
//                    logger.info(targetUri);
//                    logger.info(text);
//                    logger.info("onResponseReceivedEnd");
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
                                final String typeAccessLevel = jsonDataNode.getTypeAccessLevel();
                                if (typeAccessLevel != null) {
                                    dataNodePermissions.setAccessLevel(DataNodePermissions.AccessLevel.valueOf(typeAccessLevel));
                                }
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

                            @Override
                            public List<FieldGroup> getFieldGroups() {
                                final ArrayList<FieldGroup> fieldGroups = new ArrayList<FieldGroup>();
//                                final ArrayList<DataField> dataFieldList = new ArrayList<DataField>();
//                                final DataField dataField = new DataField(); 
//                                dataField.setFieldValue("");
//                                dataFieldList.add(dataField);
//                                fieldGroups.add(new FieldGroup("text", dataFieldList));
                                return fieldGroups;
                            }
                        });
                    }
                    dataNodeLoaderListener.dataNodeLoaded(dataNodes);
                } else {
                    dataNodeLoaderListener.dataNodeLoadFailed(new WebQueryException("Couldn't retrieve JSON: " + response.getStatusCode()));
                    logger.warning("Couldn't retrieve JSON");
                    logger.warning(targetUri);
                    logger.warning(response.getStatusText());
                }
            }
        };
    }
}
