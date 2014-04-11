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

    public ActionsPanelController(RootPanel welcomePanelTag, RootPanel metadataSearchTag, RootPanel annotationContentSearchTag, RootPanel manageAccessRightsTag, RootPanel resourceAccessTag, RootPanel citationTag) {
        this.welcomePanelTag = welcomePanelTag;
        this.metadataSearchTag = metadataSearchTag;
        this.annotationContentSearchTag = annotationContentSearchTag;
        this.manageAccessRightsTag = manageAccessRightsTag;
        this.resourceAccessTag = resourceAccessTag;
        this.citationTag = citationTag;

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
