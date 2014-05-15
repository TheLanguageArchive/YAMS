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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.logging.Logger;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 11, 2014 1:39:33 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ActionsPanelController {

    private static final Logger logger = Logger.getLogger("");
    private SerialisableDataNode dataNode = null;
    final private RootPanel actionsTargetPanel;
    final private RootPanel detailsPanel;
    final private RootPanel homeLinkTag;
    final private RootPanel metadataSearchTag;
    final private RootPanel annotationContentSearchTag;
    final private RootPanel manageAccessRightsTag;
    final private RootPanel resourceAccessTag;
    final private RootPanel citationTag;
    final private RootPanel welcomePanelTag;
    final private RootPanel aboutTag;
    final private RootPanel viewTag;
    final private RootPanel downloadTag;
    final private RootPanel versionInfoTag;
    final private Label userLabel = new Label("User: unkown");
    final private LoginController loginController;
    final private RootPanel loginTag;
    final private RootPanel logoutTag;
    final ServiceLocations serviceLocations = GWT.create(ServiceLocations.class);

    private enum NodeActionType {

        citation, details, search
    }

    public ActionsPanelController(RootPanel welcomePanelTag, RootPanel actionsTargetPanel, RootPanel detailsPanel, RootPanel homeLinkTag, RootPanel metadataSearchTag, RootPanel annotationContentSearchTag, RootPanel manageAccessRightsTag, RootPanel resourceAccessTag, RootPanel citationTag, RootPanel aboutTag, RootPanel viewTag, RootPanel downloadTag, RootPanel versionInfoTag, RootPanel loginTag, RootPanel logoutTag, RootPanel userSpan) {
        this.welcomePanelTag = welcomePanelTag;
        this.actionsTargetPanel = actionsTargetPanel;
        this.detailsPanel = detailsPanel;
        this.homeLinkTag = homeLinkTag;
        this.metadataSearchTag = metadataSearchTag;
        this.annotationContentSearchTag = annotationContentSearchTag;
        this.manageAccessRightsTag = manageAccessRightsTag;
        this.resourceAccessTag = resourceAccessTag;
        this.citationTag = citationTag;
        this.aboutTag = aboutTag;
        this.viewTag = viewTag;
        this.downloadTag = downloadTag;
        this.versionInfoTag = versionInfoTag;
        this.loginTag = loginTag;
        this.logoutTag = logoutTag;
        loginController = new LoginController(this);
        userLabel.setStyleName("header");
        loginController.exportCheckLoginState(loginController);
        loginController.checkLoginState();
        loginController.startStatusTimer();
        if (userSpan != null) {
            userSpan.add(userLabel);
        }
        if (loginTag != null) {
            final Anchor loginAnchor = Anchor.wrap(loginTag.getElement());
            addPopupPanelAction(loginAnchor, serviceLocations.loginUrl());
        }
        if (logoutTag != null) {
            final Anchor loginAnchor = Anchor.wrap(logoutTag.getElement());
            addPopupPanelAction(loginAnchor, serviceLocations.logoutUrl());
        }
        if (metadataSearchTag != null) {
            addPanelAction(metadataSearchTag, serviceLocations.yamsUrl());
        }
        if (annotationContentSearchTag != null) {
            addPageAction(annotationContentSearchTag, serviceLocations.trovaUrl());
        }
        if (manageAccessRightsTag != null) {
            addPanelAction(manageAccessRightsTag, serviceLocations.amsUrl());
        }
        if (resourceAccessTag != null) {
            addPanelAction(resourceAccessTag, serviceLocations.rrsUrl());
        }
        if (citationTag != null) {
            addNodeAction(citationTag, NodeActionType.citation);
        }
        if (aboutTag != null) {
            final Anchor aboutAnchor = Anchor.wrap(aboutTag.getElement());
            aboutAnchor.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    final version versionProperties = GWT.create(version.class);
                    final DialogBox dialogBox = new DialogBox(true, true);
                    Grid grid = new Grid(5, 2);
                    dialogBox.setText("About YAMS Browser");
                    grid.setWidget(0, 0, new Label("Version:"));
                    grid.setWidget(0, 1, new Label(versionProperties.majorVersion()));
                    grid.setWidget(1, 0, new Label("Project Version:"));
                    grid.setWidget(1, 1, new Label(versionProperties.projectVersion()));
                    grid.setWidget(2, 0, new Label("Build:"));
                    grid.setWidget(2, 1, new Label(versionProperties.buildVersion()));
                    grid.setWidget(3, 0, new Label("Compile Date:"));
                    grid.setWidget(3, 1, new Label(versionProperties.compileDate()));
                    grid.setWidget(4, 0, new Label("Commit Date:"));
                    grid.setWidget(4, 1, new Label(versionProperties.lastCommitDate()));
                    dialogBox.setGlassEnabled(true);
                    dialogBox.setAnimationEnabled(true);
                    dialogBox.setWidget(grid);
                    dialogBox.center();
                }
            });
        }
        if (homeLinkTag != null) {
            final Anchor homeAnchor = Anchor.wrap(homeLinkTag.getElement());
            homeAnchor.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    setDataNode(null);
                }
            });
        }
        setDataNode(null);
    }

    private void addPopupPanelAction(final FocusWidget focusWidget, final String targetUrl) {
        focusWidget.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
//                final DialogBox.CaptionImpl caption = new DialogBox.CaptionImpl();
//                final DialogBox popupPanel = new DialogBox(true, true);
                PopupPanel popupPanel = new PopupPanel(true, true);
                popupPanel.setPixelSize(800, 600);
                popupPanel.setGlassEnabled(true);
                popupPanel.setAnimationEnabled(true);
//                final DialogBox.Caption caption = popupPanel.getCaption();
//                caption.addClickHandler(new ClickHandler() {
//                    public void onClick(ClickEvent event) {
//                        popupPanel.hide();
//                        loginController.checkLoginState();
//                    }
//                });
//                caption.setStylePrimaryName("load-more-btn");
//                caption.setText("close");
                try {
                    final Frame frame = new Frame(getFormattedHandleLink(targetUrl));
                    popupPanel.setWidget(frame);
                    popupPanel.center();
                } catch (ModelException exception) {
                    logger.warning(exception.getMessage());
                }
            }
        });
    }

    private void addNodeAction(RootPanel rootPanel, final NodeActionType actionType) {
        final Button button = Button.wrap(rootPanel.getElement());
        button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (detailsPanel != null) {
                    detailsPanel.setVisible(false);
                }
                if (welcomePanelTag != null) {
                    welcomePanelTag.setVisible(false);
                }
                actionsTargetPanel.clear();
                actionsTargetPanel.setVisible(true);
//                try {
                    switch (actionType) {
                        case citation:
                            final CitationPanel citationPanel = new CitationPanel();
                            citationPanel.setDataNode(dataNode);
                            actionsTargetPanel.add(citationPanel);
                            break;
                        case details:
                            actionsTargetPanel.add(new MetadataDetailsPanel());
                            break;
//                        case search:actionsTargetPanel.add(new SearchPanel());
//                            break;
                    }
//                } catch (ModelException exception) {
//                    logger.warning(exception.getMessage());
//                }
            }
        });
    }

    private void addPanelAction(RootPanel rootPanel, final String targetUrl) {
        final Button button = Button.wrap(rootPanel.getElement());
        button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (detailsPanel != null) {
                    detailsPanel.setVisible(false);
                }
                if (welcomePanelTag != null) {
                    welcomePanelTag.setVisible(false);
                }
                actionsTargetPanel.clear();
                actionsTargetPanel.setVisible(true);
                try {
                    final Frame frame = new Frame(getFormattedHandleLink(targetUrl));
                    frame.setSize("100%", "100%");
                    actionsTargetPanel.add(frame);
                } catch (ModelException exception) {
                    logger.warning(exception.getMessage());
                }
            }
        });
    }

    private void addPageAction(RootPanel rootPanel, final String targetUrl) {
        final Button button = Button.wrap(rootPanel.getElement());
        button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                try {
                    Window.open(getFormattedHandleLink(targetUrl), "targetUrl", "");
                } catch (ModelException exception) {
                    logger.warning(exception.getMessage());
                }
            }
        }
        );
    }

    private String getFormattedHandleLink(final String targetUrl) throws ModelException {
        if (dataNode != null) {
            return targetUrl.replace("{}", dataNode.getURI());
        } else {
            return targetUrl;
        }
    }

    public final void setDataNode(SerialisableDataNode dataNode) {
        this.dataNode = dataNode;
        if (detailsPanel != null) {
            detailsPanel.setVisible(dataNode != null);
        }
        if (actionsTargetPanel != null) {
            actionsTargetPanel.clear();
            actionsTargetPanel.setVisible(false);
        }
        if (welcomePanelTag != null) {
            welcomePanelTag.setVisible(dataNode == null);
        }

        if (metadataSearchTag != null) {
            metadataSearchTag.setVisible(dataNode != null);
        }
        if (annotationContentSearchTag != null) {
            annotationContentSearchTag.setVisible(dataNode != null);
        }
        if (manageAccessRightsTag != null) {
            manageAccessRightsTag.setVisible(dataNode != null);
        }
        if (resourceAccessTag != null) {
            resourceAccessTag.setVisible(dataNode != null);
        }
        if (citationTag != null) {
            citationTag.setVisible(dataNode != null);
        }
        boolean showResourceButtons = (dataNode != null && dataNode.getType() != null && dataNode.getType().getFormat() == null);
        if (viewTag != null) {
            viewTag.setVisible(showResourceButtons);
        }
        if (downloadTag != null) {
            downloadTag.setVisible(showResourceButtons);
        }
        if (versionInfoTag != null) {
            versionInfoTag.setVisible(showResourceButtons);
        }
    }

    public void setLoginState(String userName, boolean anonymous) {
        userLabel.setText("User: " + userName);
        if (loginTag != null) {
            loginTag.setVisible(anonymous);
        }
        if (logoutTag != null) {
            logoutTag.setVisible(!anonymous);
        }
    }
}
