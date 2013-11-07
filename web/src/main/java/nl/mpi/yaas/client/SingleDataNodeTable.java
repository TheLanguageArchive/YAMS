/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.yaas.common.data.DataNodeHighlight;

/**
 * Created on : Apr 5, 2013, 2:05:30 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SingleDataNodeTable extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");

    public SingleDataNodeTable(final SerialisableDataNode yaasDataNode, ClickHandler closeHandler, List<DataNodeHighlight> nodeHighlights) {
        final List<FieldGroup> fieldGroups = yaasDataNode.getFieldGroups();
        int rowCounter = 0;
        Set<String> highlightPaths = new HashSet<String>();
        for (DataNodeHighlight highlight : nodeHighlights) {
            highlightPaths.add(highlight.getHighlightPath());
        }
        final HorizontalPanel linksPanel = new HorizontalPanel();
        final Grid grid = new Grid(yaasDataNode.getFieldGroups().size() + 1, 2);
        final Button closeButton = new Button("x", closeHandler);
        closeButton.setStyleName("yaas-closeButton");
        linksPanel.add(closeButton);
        try {
            final String uri = yaasDataNode.getURI();
            if (uri != null && uri.length() > 0) {
                Anchor anchor = new Anchor("view source", uri);
                linksPanel.add(anchor);
            }
            //final String id = yaasDataNode.getID();
//            Anchor entryAanchor = new Anchor("view entry", "http://tlatest03:8984/rest/"+dbUri);
//                linksPanel.add(entryAanchor);
            //linksPanel.add(new Label("DB ID: " + id));
        } catch (ModelException exception) {
            logger.log(Level.SEVERE, "ClickEvent", exception);
        }
        add(linksPanel);
        add(grid);
        for (FieldGroup fieldGroup : fieldGroups) {
            grid.setText(rowCounter, 0, fieldGroup.getFieldName());
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            for (DataField dataField : fieldGroup.getFields()) {
                final Label label = new Label(dataField.getFieldValue());
                horizontalPanel.add(label);
                if (highlightPaths.contains(dataField.getPath())) {
                    label.setStyleName("yaas-treeNode-highlighted");
                } else {
                    label.setStyleName("yaas-treeNode");
                }
            }
            grid.setWidget(rowCounter, 1, horizontalPanel);
            rowCounter++;
        }
    }
}
