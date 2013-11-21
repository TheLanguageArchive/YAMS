/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import static nl.mpi.flap.model.DataNodeType.IMDI_RESOURCE;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeHighlight;

/**
 * Created on : Apr 5, 2013, 2:05:30 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SingleDataNodeTable extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");

    public SingleDataNodeTable(final SerialisableDataNode yaasDataNode, ClickHandler closeHandler, List<DataNodeHighlight> nodeHighlights) {
        final List<FieldGroup> fieldGroups = yaasDataNode.getFieldGroups();
        int rowCounter = 0;
        Set<String> highlightPaths = new HashSet<String>();
        for (DataNodeHighlight highlight : nodeHighlights) {
            highlightPaths.add(highlight.getHighlightPath());
        }
        final HorizontalPanel buttonPanel = new HorizontalPanel();
        final VerticalPanel linksPanel = new VerticalPanel();
        final Grid grid = new Grid(yaasDataNode.getFieldGroups().size() + 1, 2);
        final Button closeButton = new Button("x", closeHandler);
        closeButton.setStyleName("yaas-closeButton");
        buttonPanel.add(closeButton);
        buttonPanel.add(linksPanel);
        try {
            final String uri = yaasDataNode.getURI();
            if (uri != null && uri.length() > 0) {
                Anchor anchor = new Anchor("Metadata Link", uri);
                linksPanel.add(anchor);
            }
            linksPanel.add(new Label());
            final String handle = yaasDataNode.getArchiveHandle();
            if (handle != null && handle.length() > 0) {
                addHandleLink(handle, linksPanel);
            }
            linksPanel.add(new Label());
//            final String id = yaasDataNode.getID();
//            if (id != null && id.length() > 0) {
//                Anchor anchor = new Anchor(id, id);
//                linksPanel.add(anchor);
//            }
//            linksPanel.add(new Label());
            final DataNodeType nodeType = yaasDataNode.getType();
//            if (nodeType != null) {
//                Label typeLabel = new Label(nodeType.getID());
//                linksPanel.add(typeLabel);
//            }
            if (yaasDataNode.getType() != null && IMDI_RESOURCE.equals(nodeType.getID())) {
                for (DataNodeLink childLink : yaasDataNode.getChildIds()) {
                    final String resourceHandle = childLink.getArchiveHandle();
                    if (resourceHandle != null && resourceHandle.length() > 0) {
                        addHandleLink(resourceHandle, linksPanel);
                        // get the resource permissions
//                        String url = "http://127.0.0.1:8888/yaas.html";//http://lux17.mpi.nl/ds/accessinfo/rest/info/" + resourceHandle.replace(":", "%3A");
//                        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
//                        jsonp.requestString(url,
//                                new AsyncCallback<String>() {
//                                    public void onFailure(Throwable throwable) {
//                                        logger.log(Level.SEVERE, throwable.getMessage());
//                                    }
//
//                                    public void onSuccess(String response) {
//                                        logger.log(Level.INFO, "A " + response);
//                                    }
//                                });
//                        String url = "http://localhost:8984/rest/EWE-2013-11-12-S?method=jsonml";
//                        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
//                        jsonp.requestString(url,
//                                new AsyncCallback<String>() {
//                                    public void onFailure(Throwable throwable) {
//                                        logger.log(Level.SEVERE, throwable.getMessage());
//                                    }
//
//                                    public void onSuccess(String feed) {
//                                        logger.log(Level.INFO, "A " + feed);
////                                        JsArray<Entry> entries = feed.getEntries();
////                                        for (int i = 0; i < entries.length(); i++) {
////                                            Entry entry = entries.get(i);
////                                            logger.log(Level.INFO, "A " + entry.getTitle()
////                                                    + " (" + entry.getWhere() + "): "
////                                                    + entry.getStartTime() + " -> "
////                                                    + entry.getEndTime());
////                                        }
//                                    }
//                                });

//                        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
//
//                        try {
//                            Request request = builder.sendRequest(null, new RequestCallback() {
//                                public void onError(Request request, Throwable exception) {
//                                    logger.log(Level.SEVERE, exception.getMessage());
//                                }
//
//                                public void onResponseReceived(Request request, Response response) {
//                                    if (200 == response.getStatusCode()) {
//                                        logger.log(Level.INFO, "B" + response.getText());
//                                    } else {
//                                        logger.log(Level.INFO, "Code" + response.getStatusCode());
//                                        logger.log(Level.INFO, "C" + response.getStatusText());
//                                        logger.log(Level.INFO, "D" + response.getText());
//                                    }
//                                }
//                            });
//                        } catch (RequestException e) {
//                            logger.log(Level.SEVERE, "Couldn't retrieve JSON");
//                        }
                    }
                }
            }
            //final String id = yaasDataNode.getID();
//            Anchor entryAanchor = new Anchor("view entry", "http://tlatest03:8984/rest/"+dbUri);
//                linksPanel.add(entryAanchor);
            //linksPanel.add(new Label("DB ID: " + id));
        } catch (ModelException exception) {
            logger.log(Level.SEVERE, "ClickEvent", exception);
        }
        add(buttonPanel);
        add(grid);
        for (FieldGroup fieldGroup : fieldGroups) {
            grid.setText(rowCounter, 0, fieldGroup.getFieldName());
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            for (DataField dataField : fieldGroup.getFields()) {
                final Label label = new Label(dataField.getFieldValue());
                horizontalPanel.add(label);
                if (highlightPaths.contains(dataField.getPath())) {
                    label.setStyleName("yaas-treeNode-highlighted");
                } else {
                    label.setStyleName("yaas-treeNode");
                }
            }
            grid.setWidget(rowCounter, 1, horizontalPanel);
            rowCounter++;
        }
    }

    private void addHandleLink(final String resourceHandle, final VerticalPanel linksPanel) {
        String viewLink = resourceHandle.replace("hdl:", "http://hdl.handle.net/");
        linksPanel.add(new Anchor("IMDI Browser Link", viewLink + "@view"));
        linksPanel.add(new Anchor("Download Link", viewLink));
    }
}
