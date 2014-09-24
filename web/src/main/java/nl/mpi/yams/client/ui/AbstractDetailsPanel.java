/**
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

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.PluginDataNode;

/**
 *
 * @author twan
 */
public abstract class AbstractDetailsPanel extends VerticalPanel {

    private int hiddenNodeCount = 0;

    public AbstractDetailsPanel() {
        this.setVisible(false);
    }

    public void setDataNode(PluginDataNode dataNode) {
        this.clear();
        this.setVisible(true);
        if (dataNode != null) {
            addDataNodePanel(dataNode);
        }
    }

    protected abstract void addDataNodePanel(PluginDataNode dataNode);

    protected final Panel createDataNodeDetailsPanel(PluginDataNode dataNode) {
        boolean hasFieldData = false;
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
                    final String fieldValue = field.getFieldValue();
                    if (fieldValue != null) {
                        HTML valueLabel = new HTML(new SafeHtmlBuilder().appendEscapedLines(fieldValue).toSafeHtml());
                        valueLabel.setStyleName("IMDI_value");
                        horizontalPanel.add(valueLabel);
                        hasFieldData = true;
                    }
                }
                verticalPanel.add(horizontalPanel);
            }
        }
        //        logger.info("children");
        final List<? extends PluginDataNode> childList = dataNode.getChildList();
        if (childList != null) {
            //            int childIndex = 0;
            //            int maxToLoad = 10;
            //            for (final SerialisableDataNode dataNodeChild : childList.subList(childIndex, childIndex + maxToLoad)) {
            //                verticalPanel.add(addDataNodePanel(dataNodeChild));
            //                childIndex++;
            for (final PluginDataNode dataNodeChild : childList) {
                boolean lazyLoad = dataNodeChild.getChildList() != null && dataNodeChild.getChildList().size() > 5;
                final Panel dataNodePanel = createDataNodeDetailsPanel(dataNodeChild);
                if (lazyLoad) {
                    final DisclosurePanel disclosurePanel = new DisclosurePanel(dataNodeChild.getLabel());
                    //                disclosurePanel.getHeader().setStyleName("IMDI_group_header_static");
                    verticalPanel.add(disclosurePanel);
                    disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
                        public void onOpen(OpenEvent<DisclosurePanel> event) {
                            disclosurePanel.setContent(dataNodePanel);
                        }
                    });
                } else {
                    verticalPanel.add(dataNodePanel);
                }
                if (dataNodePanel.isVisible()) {
                    hasFieldData = true;
                }
            }
        }
        if (hasFieldData) {
            hiddenNodeCount++;
        }
        simplePanel.setVisible(hasFieldData);
        return simplePanel;
    }

    /**
     * @return the hiddenNodeCount
     */
    protected int getHiddenNodeCount() {
        return hiddenNodeCount;
    }

    /**
     *
     * @param parent panel to add headered panel to
     * @param headerText header text
     * @return content panel of created header panel
     */
    protected Panel createDetailsGroup(Panel parent, String headerText) {
        final VerticalPanel panel = new VerticalPanel();
        panel.setStylePrimaryName("details-group");
        panel.setVisible(true);
        parent.add(panel);

        if (headerText != null) {
            final Label headerLabel = new Label(headerText);
            headerLabel.setStylePrimaryName("details-group-header");
            panel.add(headerLabel);
        }

        final VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setStylePrimaryName("details-group-content");
        panel.add(contentPanel);
        return contentPanel;
    }

}
