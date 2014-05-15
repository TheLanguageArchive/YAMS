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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 8, 2014 5:36:56 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class CitationPanel extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");
    private SerialisableDataNode dataNode;

//    private static final CitationTemplate CITATION_TEMPLATE = GWT.create(CitationTemplate.class);
    final CitationStrings citationStrings = GWT.create(CitationStrings.class);

    public CitationPanel() {
        this.setVisible(false);
    }

    public void setDataNode(SerialisableDataNode dataNode) {
//        logger.info("MetadataDetailsPanel");
        this.clear();
//        logger.info("a-MetadataDetailsPanel");
        this.setVisible(true);
//        logger.info("b-MetadataDetailsPanel");
        this.dataNode = dataNode;
//        logger.info("c-MetadataDetailsPanel");
//        try {
        this.add(getPanel(dataNode));
//            this.add(new HTML(CITATION_TEMPLATE.citationBody(dataNode.getURI(), dataNode.getID(), dataNode.getURI(), dataNode.getLabel(), dataNode.getType().getFormat().name(), "", dataNode.getArchiveHandle())));
//        } catch (ModelException exception) {
//            logger.warning(exception.toString());
//        }
//        logger.info("end-MetadataDetailsPanel");
    }

    public Panel getLabelPanel(String label, String value, String target) {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new Label(label));
        if (target == null) {
            horizontalPanel.add(new Label(value));
        } else {
            horizontalPanel.add(new Anchor(value, target));
        }
        return horizontalPanel;
    }

    public Panel getPanel(SerialisableDataNode dataNode) {
        final VerticalPanel simplePanel = new VerticalPanel();
//        logger.info(dataNode.getLabel());
        simplePanel.add(new Label(citationStrings.panelTitle()));
        try {
            simplePanel.add(getLabelPanel(citationStrings.citationLabel(dataNode.getLabel()), dataNode.getArchiveHandle(), dataNode.getURI()));

            final Label label = new Label(citationStrings.citationDescription());
            simplePanel.setStyleName("IMDI_group");
            simplePanel.add(label);
            final DisclosurePanel disclosurePanel = new DisclosurePanel(citationStrings.details());
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.add(getLabelPanel(citationStrings.internalId(), dataNode.getID(), null));
            verticalPanel.add(getLabelPanel(citationStrings.title(), dataNode.getLabel(), null));//"Resource \""++"\" from \"\""
//            verticalPanel.add(getLabelPanel("Archive Name:", "", null));
            if (dataNode.getType() != null) {
                verticalPanel.add(getLabelPanel(citationStrings.formatId(), dataNode.getType().getID(), null));
                verticalPanel.add(getLabelPanel(citationStrings.formatName(), dataNode.getType().getName(), null));
                if (dataNode.getType().getFormat() != null) {
                    verticalPanel.add(getLabelPanel(citationStrings.format(), dataNode.getType().getFormat().name(), null));
                }
            }
            verticalPanel.add(getLabelPanel(citationStrings.published(), "", null));
            verticalPanel.add(getLabelPanel(citationStrings.handle(), dataNode.getArchiveHandle(), null));
            verticalPanel.add(getLabelPanel(citationStrings.link(), new HandleFormatter().getUrlFromHandle(dataNode.getArchiveHandle()), null));
            verticalPanel.add(getLabelPanel(citationStrings.url(), dataNode.getURI(), null));
            disclosurePanel.add(verticalPanel);
            simplePanel.add(disclosurePanel);
        } catch (ModelException exception) {
            logger.warning(exception.toString());
        }
//        logger.info("groups");
        return simplePanel;
    }
}
