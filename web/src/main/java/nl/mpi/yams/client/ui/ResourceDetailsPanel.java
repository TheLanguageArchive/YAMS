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
package nl.mpi.yams.client.ui;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;
import static nl.mpi.yams.client.ui.LabelPanel.addLabel;

/**
 * @author Peter Withers <peter.withers@mpi.nl>
 * @author twan.goosen@mpi.nl
 */
public class ResourceDetailsPanel extends AbstractDetailsPanel {

    private final static Logger logger = Logger.getLogger(ResourceDetailsPanel.class.getName());

    protected void addDataNodePanel(PluginDataNode dataNode) {
        final Panel infoPanel = createDetailsGroup(this, null);
        /*
         URL:	https://corpus1.mpi.nl/media-archive/IFA_corpus/SLspeech/chunks/F20N/F20N1FPA1.aifc
         Handle URI:	http://hdl.handle.net/1839/00-0000-0000-0004-3540-E
         Internal Node ID:	MPI275776#
         Node type:	Media Resource
         Format:	audio/x-aiff
         File size:	17 MB
         Last modified:	Thu Apr 26 11:22:52 CEST 2001
         MD5 Check sum:	c9eecd66fcc77fe2b7cd8d4213097fd9
         */
        try {
            addLabel(infoPanel, "URL: ", dataNode.getURI());
        } catch (ModelException ex) {
            logger.severe(ex.getMessage());
        }
        addLabel(infoPanel, "Handle: ", dataNode.getArchiveHandle());
        addLabel(infoPanel, "Node type: ", dataNode.getType().getFormat().toString());
        addLabel(infoPanel, "Format: ", dataNode.getType().getMimeType());

        /*
         Access Info

         The resource can be viewed by clicking the "view" button or downloaded with the "download" button, provided that you have sufficient access permissions. 

         General Accessibility:	This resource is openly available
         Currently accessible to user anonymous:	yes
         */
        final Panel accessInfoPanel = createDetailsGroup(this, "Access Info");
        accessInfoPanel.add(new Label("The resource can be viewed by clicking the \"view\" button or downloaded with the \"download\" button, provided that you have sufficient access permissions."));
        addLabel(accessInfoPanel, "General Accessibility: ", dataNode.getPermissions().getLabel());
        addLabel(accessInfoPanel, "Currently accessible to user anonymous: ", dataNode.getPermissions().getAccessLevel() == DataNodePermissions.AccessLevel.open_everybody ? "yes" : "no");

        /*
         Applicable License Agreement(s) 

         No licenses required for this resource. 
         */
        final Panel licenseInfoPanel = createDetailsGroup(this, "Applicable License Agreement(s)");
        licenseInfoPanel.add(new Label("Information not available at this moment"));
        //TODO: Add license info

        /*
         Rights 

         All resources in this archive are Copyright © of their respective holders. Re-distribution of the content in any form, by any means, without the prior permission of the copyright holder is prohibited, unless explicitly stated otherwise in a license. See the metadata descriptions of the resources for contact information or contact corpman@mpi.nl.
         */
        final Panel rightsPanel = createDetailsGroup(this, "Rights");
        rightsPanel.add(new Label("All resources in this archive are Copyright © of their respective holders. Re-distribution of the content in any form, by any means, without the prior permission of the copyright holder is prohibited, unless explicitly stated otherwise in a license. See the metadata descriptions of the resources for contact information or contact corpman@mpi.nl"));
        
        final Panel detailsPanel = createDetailsGroup(this, "More details");
        add(detailsPanel);
        final DisclosurePanel detailsDisclouserPanel = new DisclosurePanel("Click to see more detailed information about this resource");
        detailsPanel.add(detailsDisclouserPanel);
        detailsDisclouserPanel.add(createDataNodeDetailsPanel(dataNode));
    }

}
