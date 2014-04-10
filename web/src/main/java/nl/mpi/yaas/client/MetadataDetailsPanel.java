/*
 * Copyright (C) 2014 The Language Archive, Max Planck Institute for Psycholinguistics
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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 8, 2014 5:36:56 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class MetadataDetailsPanel extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");
    private SerialisableDataNode dataNode;
    
    public MetadataDetailsPanel() {
        this.setStyleName("metadataDetailsPanel");
        this.setVisible(false);
    }

    public void setDataNode(SerialisableDataNode dataNode) {
        logger.info("setDataNode");
        this.setVisible(true);
        this.dataNode = dataNode;
        this.add(addDataNodePanel(dataNode));
    }

    public Panel addDataNodePanel(SerialisableDataNode dataNode) {
        final VerticalPanel simplePanel = new VerticalPanel();
        logger.info("label");
        final Label label = new Label(dataNode.getLabel());
        simplePanel.setStyleName("IMDI_group");
        simplePanel.add(label);
        label.setStyleName("IMDI_group_header_static");
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setStyleName("IMDI_group_static");
        simplePanel.add(verticalPanel);
        logger.info("groups");
        final List<FieldGroup> fieldGroups = dataNode.getFieldGroups();
        if (fieldGroups != null) {
            for (FieldGroup fieldGroup : fieldGroups) {
                HorizontalPanel horizontalPanel = new HorizontalPanel();
                horizontalPanel.setStyleName("IMDI_name_value");
                final Label groupLabel = new Label(fieldGroup.getFieldName());
                groupLabel.setStyleName("IMDI_label");
                horizontalPanel.add(groupLabel);
                logger.info("fields");
                for (DataField field : fieldGroup.getFields()) {
                    final Label valueLabel = new Label(field.getFieldValue());
                    valueLabel.setStyleName("IMDI_value");
                    horizontalPanel.add(valueLabel);
                }
                verticalPanel.add(horizontalPanel);
            }
        }
        logger.info("children");
        final List<? extends SerialisableDataNode> childList = dataNode.getChildList();
        if (childList != null) {
            for (SerialisableDataNode dataNodeChild : childList) {
                verticalPanel.add(addDataNodePanel(dataNodeChild));
            }
        }
        return simplePanel;
    }
}
