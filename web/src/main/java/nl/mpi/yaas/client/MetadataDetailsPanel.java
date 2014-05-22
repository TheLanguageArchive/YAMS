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

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
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
        this.add(addDataNodePanel(dataNode));
//        logger.info("end-MetadataDetailsPanel");
    }

    public Panel addDataNodePanel(SerialisableDataNode dataNode) {
        final VerticalPanel simplePanel = new VerticalPanel();
       // logger.info(dataNode.getLabel());
        final Label label = new Label(dataNode.getLabel());
        simplePanel.setStyleName("IMDI_group");
        simplePanel.add(label);
        label.setStyleName("IMDI_group_header_static");
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setStyleName("IMDI_group_static");
        simplePanel.add(verticalPanel);
//        logger.info("groups");
        final List<FieldGroup> fieldGroups = dataNode.getFieldGroups();
        if (fieldGroups != null) {
            for (FieldGroup fieldGroup : fieldGroups) {
                HorizontalPanel horizontalPanel = new HorizontalPanel();
                horizontalPanel.setStyleName("IMDI_name_value");
                final Label groupLabel = new Label(fieldGroup.getFieldName());
                groupLabel.setStyleName("IMDI_label");
                horizontalPanel.add(groupLabel);
//                logger.info("fields");
                for (DataField field : fieldGroup.getFields()) {
                    final Label valueLabel = new Label(field.getFieldValue());
                    valueLabel.setStyleName("IMDI_value");
                    horizontalPanel.add(valueLabel);
                }
                verticalPanel.add(horizontalPanel);
            }
        }
//        logger.info("children");
        final List<? extends SerialisableDataNode> childList = dataNode.getChildList();
        if (childList != null) {
//            int childIndex = 0;
//            int maxToLoad = 10;
//            for (final SerialisableDataNode dataNodeChild : childList.subList(childIndex, childIndex + maxToLoad)) {
//                verticalPanel.add(addDataNodePanel(dataNodeChild));
//                childIndex++;
            for (final SerialisableDataNode dataNodeChild : childList) {
                boolean lazyLoad = dataNodeChild.getChildList() != null && dataNodeChild.getChildList().size() > 5;
                if (lazyLoad) {
                    final DisclosurePanel disclosurePanel = new DisclosurePanel(dataNodeChild.getLabel());
//                disclosurePanel.getHeader().setStyleName("IMDI_group_header_static");
                    verticalPanel.add(disclosurePanel);
                    disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

                        public void onOpen(OpenEvent<DisclosurePanel> event) {
                            disclosurePanel.setContent(addDataNodePanel(dataNodeChild));
                        }
                    });
                } else {
                    verticalPanel.add(addDataNodePanel(dataNodeChild));
                }
            }
        }
        return simplePanel;
    }
}