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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 11, 2014 1:39:33 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ActionsPanelController {

    private SerialisableDataNode dataNode = null;
    final private RootPanel metadataSearchTag;
    final private RootPanel annotationContentSearchTag;
    final private RootPanel manageAccessRightsTag;
    final private RootPanel resourceAccessTag;
    final private RootPanel citationTag;
    final private RootPanel welcomePanelTag;
    final private RootPanel aboutTag;

    public ActionsPanelController(RootPanel welcomePanelTag, RootPanel metadataSearchTag, RootPanel annotationContentSearchTag, RootPanel manageAccessRightsTag, RootPanel resourceAccessTag, RootPanel citationTag, RootPanel aboutTag) {
        this.welcomePanelTag = welcomePanelTag;
        this.metadataSearchTag = metadataSearchTag;
        this.annotationContentSearchTag = annotationContentSearchTag;
        this.manageAccessRightsTag = manageAccessRightsTag;
        this.resourceAccessTag = resourceAccessTag;
        this.citationTag = citationTag;
        this.aboutTag = aboutTag;

        if (welcomePanelTag != null) {
            welcomePanelTag.setVisible(true);
        }
        if (metadataSearchTag != null) {
            metadataSearchTag.setVisible(false);
        }
        if (annotationContentSearchTag != null) {
            annotationContentSearchTag.setVisible(false);
        }
        if (manageAccessRightsTag != null) {
            manageAccessRightsTag.setVisible(false);
        }
        if (resourceAccessTag != null) {
            resourceAccessTag.setVisible(false);
        }
        if (citationTag != null) {
            citationTag.setVisible(false);
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
    }

    public void setDataNode(SerialisableDataNode dataNode) {
        this.dataNode = dataNode;
        if (welcomePanelTag != null) {
            welcomePanelTag.setVisible(false);
        }

        if (metadataSearchTag != null) {
            metadataSearchTag.setVisible(true);
        }
        if (annotationContentSearchTag != null) {
            annotationContentSearchTag.setVisible(true);
        }
        if (manageAccessRightsTag != null) {
            manageAccessRightsTag.setVisible(true);
        }
        if (resourceAccessTag != null) {
            resourceAccessTag.setVisible(true);
        }
        if (citationTag != null) {
            citationTag.setVisible(true);
        }
    }
}
