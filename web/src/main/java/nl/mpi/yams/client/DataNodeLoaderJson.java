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
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yams.common.data.DataNodeHighlight;
import nl.mpi.yams.common.data.DataNodeId;
import nl.mpi.yams.common.data.HighlightableDataNode;
import nl.mpi.yams.common.data.QueryDataStructures;
import nl.mpi.yams.common.data.SearchParameters;
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
        jsonUrl = serviceLocations.jsonRootNodeUrl(serviceLocations.jsonBasexAdaptorUrl(), databaseName);
    }

    public void requestLoadRoot(final DataNodeLoaderListener dataNodeLoaderListener) {
        //logger.info("requestLoadRoot");
        final String jsonRootUrl = jsonUrl;
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
        //logger.info("requestLoadChildrenOf");
        final String jsonLinksOfUrl = serviceLocations.jsonLinksOfUrl(jsonUrl, dataNodeId.getIdString(), first, last);
        //logger.info(jsonLinksOfUrl);
        // Send request to server and catch any errors.
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, jsonLinksOfUrl);
        try {
            final Request request = builder.sendRequest(null, geRequestBuilder(builder, dataNodeLoaderListener, jsonLinksOfUrl));
        } catch (RequestException e) {
            dataNodeLoaderListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.log(Level.SEVERE, "requestLoadChildrenOf", e);
            logger.warning(jsonLinksOfUrl);
        }
    }

    public void requestLoad(List<DataNodeId> dataNodeIdList, final DataNodeLoaderListener dataNodeLoaderListener) {
        //logger.info("requestLoad");
// Send request to server and catch any errors.
        StringBuilder stringBuilder = new StringBuilder();
        for (DataNodeId dataNodeId : dataNodeIdList) {
            stringBuilder.append(serviceLocations.jsonNodeGetVar());
            stringBuilder.append(dataNodeId.getIdString());
            stringBuilder.append("&");
        }
//        logger.info("jsonUrl:" + jsonUrl);
//        final String jsonLinksOfUrl = serviceLocations.jsonYamsDataUrl(jsonUrl, stringBuilder.toString());
        final String restNodeUrl = serviceLocations.jsonNodeOfUrl(jsonUrl, stringBuilder.toString());
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
        //logger.info("requestLoadHdl");
        // Send request to server and catch any errors.
        StringBuilder stringBuilder = new StringBuilder();
        for (String dataNodeHdl : dataNodeHdlList) {
            stringBuilder.append(serviceLocations.jsonNodeGetVar());
            stringBuilder.append(dataNodeHdl);
            stringBuilder.append("&");
        }
        final String restNodeUrl = serviceLocations.jsonNodeOfUrl(jsonUrl, stringBuilder.toString());
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

    public String getNodeIcon(PluginDataNode yamsDataNode) {
        return yamsDataNode.getType().getID();
    }

    public void performSearch(String databaseName, QueryDataStructures.CriterionJoinType criterionJoinType, List<SearchParameters> searchParametersList, final DataNodeSearchListener dataNodeSearchListener) {
        String searchUrl = serviceLocations.jsonSearchUrl(serviceLocations.jsonBasexAdaptorUrl(), databaseName, criterionJoinType.name());
        for (SearchParameters parameters : searchParametersList) {
            final String type = (parameters.getFileType().getType() == null) ? "" : parameters.getFileType().getType();
            final String path = (parameters.getFieldType().getPath() == null) ? "" : parameters.getFieldType().getPath();
            searchUrl = serviceLocations.jsonSearchParam(searchUrl, parameters.getSearchNegator().name(), parameters.getSearchType().name(), type, path, parameters.getSearchString());
        }
        final String searchParamUrl = searchUrl;
        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, searchUrl);
        try {
            final Request request = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    dataNodeSearchListener.dataNodeLoadFailed(exception);
                    logger.warning("Couldn't retrieve JSON from: ");
                    logger.warning(builder.getUrl());
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        final String text = response.getText();
//                        logger.info("onResponseReceived");
                        logger.info(searchParamUrl);
//                        logger.info(text);
                        final String textCleaned = (text.startsWith("[")) ? text : "[" + text + "]";
//                        logger.info(textCleaned);
//                        logger.info("onResponseReceivedEnd");
                        final JsArray<JsonDataNode> jsonArray = JsonUtils.safeEval(textCleaned);
                        List<HighlightableDataNode> dataNodes = new ArrayList<HighlightableDataNode>();
                        for (int index = 0; index < jsonArray.length(); index++) {
                            final JsonDataNode jsonDataNode = (JsonDataNode) jsonArray.get(index);
                            dataNodes.add(new HighlightableDataNode() {

                                @Override
                                public List<DataNodeHighlight> getHighlights() {
                                    List<DataNodeHighlight> highlights = new ArrayList<DataNodeHighlight>();
                                    for (int index = 0; index < jsonDataNode.getHighlightCount(); index++) {
                                        final DataNodeHighlight dataNodeHighlight = new DataNodeHighlight();
                                        dataNodeHighlight.setDataNodeId(jsonDataNode.getHighlightId(index));
                                        dataNodeHighlight.setHighlightPath(jsonDataNode.getHighlightPath(index));
                                        highlights.add(dataNodeHighlight);
                                    }
                                    return highlights;
                                }

                                @Override
                                public List<? extends SerialisableDataNode> getChildList() {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public Integer getLinkCount() {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public List<DataNodeLink> getChildIds() throws ModelException {
                                    return jsonDataNode.getChildIds();
                                }

                                @Override
                                public List<FieldGroup> getFieldGroups() {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public DataNodePermissions getPermissions() {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public DataNodeType getType() {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public String getLabel() {
                                    return jsonDataNode.getLabel();
                                }

                                @Override
                                public String getArchiveHandle() {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public String getURI() throws ModelException {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }

                                @Override
                                public String getID() throws ModelException {
                                    throw new UnsupportedOperationException("Not supported yet.");
                                }
                            });
                        }
                        dataNodeSearchListener.dataNodeLoaded(dataNodes);
                    } else {
                        dataNodeSearchListener.dataNodeLoadFailed(new WebQueryException("Couldn't retrieve JSON: " + response.getStatusCode()));
                        logger.warning("Couldn't retrieve JSON");
                        logger.warning(searchParamUrl);
                        logger.warning(response.getStatusText());
                    }
                }
            });
        } catch (RequestException e) {
            dataNodeSearchListener.dataNodeLoadFailed(e);
            logger.warning("Couldn't retrieve JSON");
            logger.log(Level.SEVERE, "requestLoadRoot", e);
        }
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
                    try {
                        final JsArray<JsonDataNode> jsonArray = JsonUtils.safeEval(text);
                        List<PluginDataNode> dataNodes = new ArrayList<PluginDataNode>();
                        for (int index = 0; index < jsonArray.length(); index++) {
                            final JsonDataNode jsonDataNode = (JsonDataNode) jsonArray.get(index);
                            dataNodes.add(jsonDataNode);
                        }
                        dataNodeLoaderListener.dataNodeLoaded(dataNodes);
                    } catch (IllegalArgumentException exception) {
                        dataNodeLoaderListener.dataNodeLoadFailed(new WebQueryException("Couldn't retrieve JSON: " + response.getStatusCode()));
                        logger.warning("Couldn't parse JSON");
                        logger.warning(exception.getMessage());
                        logger.warning(targetUri);
                        logger.warning(response.getStatusText());
                        logger.info(text);
                    }
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
