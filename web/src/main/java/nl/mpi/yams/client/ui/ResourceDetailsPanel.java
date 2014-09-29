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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;
import nl.mpi.yams.client.DataNodeLoaderJson;
import nl.mpi.yams.client.DataNodeLoaderListener;
import static nl.mpi.yams.client.ui.LabelPanel.addLabel;

/**
 * @author Peter Withers <peter.withers@mpi.nl>
 * @author twan.goosen@mpi.nl
 */
public class ResourceDetailsPanel extends AbstractDetailsPanel {

    private final static Logger logger = Logger.getLogger(ResourceDetailsPanel.class.getName());
    private final DataNodeLoaderJson csLoader;

    public ResourceDetailsPanel() {
        this(null);
    }

    public ResourceDetailsPanel(DataNodeLoaderJson csLoader) {
        this.csLoader = csLoader;
    }

    protected void addDataNodePanel(final PluginDataNode dataNode) {
        if (csLoader == null) {
            addPanels(dataNode, dataNode);
        } else {
            loadCsNode(dataNode);
        }
    }

    private void loadCsNode(final PluginDataNode dataNode) {
        try {
            csLoader.requestLoadHdl(Collections.singletonList(dataNode.getURI()), new DataNodeLoaderListener() {

                public void dataNodeLoaded(List<? extends PluginDataNode> dataNodeList) {
                    if (dataNodeList.size() != 1) {
                        dataNodeLoadFailed(new Exception("Unexpected number of nodes returned by corpus structure:" + dataNodeList.size()));
                    } else {
                        final PluginDataNode csNode = dataNodeList.get(0);
                        addPanels(dataNode, csNode);
                    }
                }

                public void dataNodeLoadFailed(Throwable caught) {
                    // failed to get matching node from CS service
                    logger.log(Level.WARNING, "failed to retrieve node from corpus structure, using provided node", caught);
                    // fall back to database datanode
                    addPanels(dataNode, dataNode);
                }
            });

        } catch (ModelException ex) {
            logger.log(Level.SEVERE, "error showing resource info", ex);
        }
    }

    private void addPanels(final PluginDataNode dataNode, final PluginDataNode csDataNode) {
        addGlobalInfoPanel(dataNode);
        addAccessInfoPanel(csDataNode);
        addLicenseInfoPanel(csDataNode);
        addRightsInfoPanel();
        addDetailsPanel(dataNode);
    }

    private void addGlobalInfoPanel(PluginDataNode dataNode) {
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
    }

    private void addAccessInfoPanel(PluginDataNode dataNode) {
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
    }

    private void addLicenseInfoPanel(PluginDataNode dataNode) {
        /*
         Applicable License Agreement(s)
        
         No licenses required for this resource.
         */
        final Panel licenseInfoPanel = createDetailsGroup(this, "Applicable License Agreement(s)");
        licenseInfoPanel.add(new Label("Information not available at this moment"));
        //TODO: Add license info
    }

    private void addRightsInfoPanel() {
        /*
         Rights
        
         All resources in this archive are Copyright © of their respective holders. Re-distribution of the content in any form, by any means, without the prior permission of the copyright holder is prohibited, unless explicitly stated otherwise in a license. See the metadata descriptions of the resources for contact information or contact corpman@mpi.nl.
         */
        final Panel rightsPanel = createDetailsGroup(this, "Rights");
        rightsPanel.add(new Label("All resources in this archive are Copyright © of their respective holders. Re-distribution of the content in any form, by any means, without the prior permission of the copyright holder is prohibited, unless explicitly stated otherwise in a license. See the metadata descriptions of the resources for contact information or contact corpman@mpi.nl"));
    }

    private void addDetailsPanel(PluginDataNode dataNode) {
        final Panel detailsPanel = createDetailsGroup(this, "More details");
        add(detailsPanel);
        final DisclosurePanel detailsDisclouserPanel = new DisclosurePanel("Click to see more detailed information about this resource");
        detailsPanel.add(detailsDisclouserPanel);
        detailsDisclouserPanel.add(createDataNodeDetailsPanel(dataNode));
    }

}
