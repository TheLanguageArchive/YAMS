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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;
import nl.mpi.yams.client.CitationStrings;
import nl.mpi.yams.client.HandleFormatter;

/**
 * @since Apr 8, 2014 5:36:56 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class CitationPanel extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");
    private final CitationStrings citationStrings = GWT.create(CitationStrings.class);
    private final HandleFormatter handleFormatter = new HandleFormatter();

    public CitationPanel() {
        this.setVisible(false);
    }

    public void setDataNode(PluginDataNode dataNode) {
        this.clear();
        this.setVisible(true);
        this.add(getPanel(dataNode));
    }

    public Panel getPanel(PluginDataNode dataNode) {
        final VerticalPanel simplePanel = new VerticalPanel();
        final Label titleLabel = new Label(citationStrings.panelTitle());
        titleLabel.setStylePrimaryName("details-header");
        simplePanel.add(titleLabel);

        final VerticalPanel verticalPanel = new VerticalPanel();
        try {
            final String archiveHandle = dataNode.getArchiveHandle();
            

            final String citationLinkTarget;
            if (archiveHandle != null) {
                citationLinkTarget = handleFormatter.getUrlFromHandle(archiveHandle);
            } else {
                citationLinkTarget = dataNode.getURI();
            }
            
            final Panel linkPanel = new FlowPanel();
            linkPanel.add(new Label(citationStrings.citationLabel(dataNode.getLabel())));
            linkPanel.add(new Anchor(citationLinkTarget, citationLinkTarget));
            linkPanel.setStylePrimaryName("citation-link");
            simplePanel.add(linkPanel);

            final Label label = new Label(citationStrings.citationDescription());
            simplePanel.setStyleName("IMDI_group");
            simplePanel.add(label);
            final DisclosurePanel disclosurePanel = new DisclosurePanel(citationStrings.details());
            verticalPanel.add(new LabelPanel(citationStrings.handle(), archiveHandle, handleFormatter.getUrlFromHandle(archiveHandle)));
            verticalPanel.add(new LabelPanel(citationStrings.url(), dataNode.getURI(), dataNode.getURI()));
            verticalPanel.add(new LabelPanel(citationStrings.title(), dataNode.getLabel()));//"Resource \""++"\" from \"\""
//            verticalPanel.add(getLabelPanel("Archive Name:", "", null));
            if (dataNode.getType() != null) {
                verticalPanel.add(new LabelPanel(citationStrings.formatId(), dataNode.getType().getID(), null));
                verticalPanel.add(new LabelPanel(citationStrings.formatName(), dataNode.getType().getMimeType(), null));
                if (dataNode.getType().getFormat() != null) {
                    verticalPanel.add(new LabelPanel(citationStrings.format(), dataNode.getType().getFormat().name(), null));
                }
            }
//            verticalPanel.add(new LabelPanel(citationStrings.published(), "", null));
            disclosurePanel.add(verticalPanel);
            simplePanel.add(disclosurePanel);
        } catch (ModelException exception) {
            logger.warning(exception.toString());
            verticalPanel.add(new Label(citationStrings.citationError()));
        }
        return simplePanel;
    }
}
