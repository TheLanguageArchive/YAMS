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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Feb 5, 2013, 1:24:35 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public abstract class YaasTreeItem extends TreeItem {

    public static final String ERROR_GETTING_CHILD_NODES = "Error getting child nodes";
    public static final String LOADING_CHILD_NODES_FAILED = "Loading child nodes failed";
    public static final String FAILURE = "Failure";
    protected SerialisableDataNode yaasDataNode = null;
    final protected DataNodeLoader dataNodeLoader;
    protected boolean loadAttempted = false;
    final HorizontalPanel outerPanel;
    final private CheckBox checkBox;
    final protected Label nodeLabel;
    final protected Anchor nodeDetailsAnchor;
    protected final VerticalPanel verticalPanel = new VerticalPanel();
    private final Image iconImage1 = new Image();
    private final Image iconImage2 = new Image();
    protected final TreeItem loadingTreeItem;
    protected final TreeItem errorTreeItem;
    protected final TreeItem loadNextTreeItem;
    protected static final Logger logger = Logger.getLogger("");
    protected int loadedCount = 0;
    protected final PopupPanel popupPanel;
    protected final TreeNodeCheckboxListener checkboxListener;
    protected final TreeNodeClickListener clickListener;

    public YaasTreeItem(DataNodeLoader dataNodeLoader, PopupPanel popupPanel, TreeNodeCheckboxListener checkboxListener, TreeNodeClickListener clickListener) {
        super(new HorizontalPanel());
        this.dataNodeLoader = dataNodeLoader;
        this.popupPanel = popupPanel;
        this.checkboxListener = checkboxListener;
        this.clickListener = clickListener;
        loadingTreeItem = getLoadingItem();
        errorTreeItem = new TreeItem();
        outerPanel = (HorizontalPanel) this.getWidget();
        checkBox = new CheckBox();
        nodeLabel = new Label();
        nodeDetailsAnchor = new Anchor();
        loadNextTreeItem = getLoadNextTreeItem();
        setupWidgets();
    }

    private TreeItem getLoadNextTreeItem() {
        final Label loadNextButton = new Label("Load More");
        loadNextButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                loadChildNodes();
            }
        });
        loadNextButton.setStylePrimaryName("load-more-btn");
        final TreeItem treeItem = new TreeItem(loadNextButton);
//        treeItem.setStyleDependentName("selected", false);
//        treeItem.getElement().addClassName("load-more");
//        treeItem.set
        return treeItem;
    }

    private TreeItem getLoadingItem() {
        HorizontalPanel loadingItem = new HorizontalPanel();
        loadingItem.add(new Image("./loader.gif"));
        loadingItem.add(new Label("loading..."));
        return new TreeItem(loadingItem);

    }

    protected void setNodeIconStye(DataNodeType dataNodeType, DataNodePermissions dataNodePermissions) {
        iconImage1.setStyleName("access_level_" + dataNodePermissions.getAccessLevel().name());
        iconImage2.setStyleName("format_" + dataNodeType.getFormat().name());
    }

    protected void setNodeIconData(String iconData) {
        iconImage1.setUrl(iconData);
    }

    protected void hideShowExpandButton() {
        final boolean hasFields = yaasDataNode != null && yaasDataNode.getFieldGroups() != null;
        nodeLabel.setVisible(!hasFields);
        if (checkboxListener != null) {
            checkBox.setVisible(hasFields);
        }
        nodeDetailsAnchor.setVisible(hasFields);
    }

    private void setupWidgets() {
        setStylePrimaryName("yaas-treeNode");
        final Style style = outerPanel.getElement().getStyle();
        style.setLeft(0, Style.Unit.PX);
        style.setPosition(Style.Position.RELATIVE);
        outerPanel.add(iconImage1);
        outerPanel.add(iconImage2);
        if (checkboxListener != null) {
            outerPanel.add(checkBox);
        }
        outerPanel.add(nodeLabel);
        outerPanel.add(nodeDetailsAnchor);
//        expandButton = new Button(">");
        outerPanel.add(verticalPanel);
//        outerPanel.add(expandButton);
        if (checkboxListener != null) {
            checkBox.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    checkboxListener.stateChanged(checkBox.getValue(), yaasDataNode, checkBox);
                }
            });
        }
//        nodeDetailsAnchor.addMouseOutHandler(new MouseOutHandler() {
//
//            public void onMouseOut(MouseOutEvent event) {
//                if (singleDataNodeTable != null) {
////                    if (!event.getRelatedTarget().equals(singleDataNodeTable.)) {
//                        verticalPanel.remove(singleDataNodeTable);
//                        singleDataNodeTable = null;
////                    }
//                }
//            }
//        });
        //logger.info("clickListener");
        if (clickListener != null) {
            //logger.info(clickListener.toString());
            nodeDetailsAnchor.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    //logger.info(yaasDataNode.getLabel());
                    clickListener.clickEvent(yaasDataNode);
                }
            });
        }
        if (popupPanel != null) {
            nodeDetailsAnchor.addMouseOverHandler(new MouseOverHandler() {

                public void onMouseOver(MouseOverEvent event) {
//                if (singleDataNodeTable == null) {
//                    singleDataNodeTable = ;
//                    singleDataNodeTable.setStyleName("yaas-treeNodeDetails");                    
                    popupPanel.setWidget(getPopupWidget());
                    popupPanel.setPopupPosition(nodeDetailsAnchor.getAbsoluteLeft() + nodeDetailsAnchor.getOffsetWidth() / 2, nodeDetailsAnchor.getAbsoluteTop() + nodeDetailsAnchor.getOffsetHeight());
                    popupPanel.show();
//                                 expandButton.setText("<<");
//                }
                }
            });
        }
        hideShowExpandButton();
    }

    abstract Widget getPopupWidget();

    @Override
    public void setText(String text) {
        nodeLabel.setText(text);
        nodeDetailsAnchor.setText(text);
    }

//    private void loadDataNodeJson() {
//        // todo: continue working on the json version of the data loader
//        // todo: create a json datanode for use here
//        // The RequestBuilder code is replaced by a call to the getJson method. So you no longer need the following code in the refreshWatchList method: http://stackoverflow.com/questions/11121374/gwt-requestbuilder-cross-site-requests
//        // also the current configuration probalby needs on the server: Response.setHeader("Access-Control-Allow-Origin","http://192.168.56.101:8080/BaseX76/rest/");
//        final String requestUrl = "http://192.168.56.101:8080/BaseX76/rest/yaas-data/" + dataNodeId.getIdString() + "?method=jsonml";
//        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(requestUrl));
//        try {
//            Request request = builder.sendRequest(null, new RequestCallback() {
//                public void onError(Request request, Throwable exception) {
//                    setText(FAILURE);
//                    logger.log(Level.SEVERE, FAILURE, exception);
//                }
//
//                public void onResponseReceived(Request request, Response response) {
//                    if (200 == response.getStatusCode()) {
//                        setText(response.getText());
//                    } else {
//                        // if the document does not exist this error will occur
//                        setText(FAILURE);
//                        logger.log(Level.SEVERE, FAILURE, new Throwable(response.getStatusText() + response.getStatusCode() + " " + " " + response.getText() + " " + requestUrl));
//                    }
//                }
//            });
//        } catch (RequestException exception) {
//            setText(FAILURE);
//            logger.log(Level.SEVERE, FAILURE, exception);
//        }
//    }
    public abstract void loadChildNodes();

    abstract void insertLoadedChildNode(SerialisableDataNode childDataNode);

    abstract void setLabel();

    public SerialisableDataNode getYaasDataNode() {
        return yaasDataNode;
    }
}
