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
package nl.mpi.yams.client;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import java.util.ArrayList;
import java.util.HashSet;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Apr 2, 2013, 3:44:06 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeTable extends DialogBox {

    final private ArrayList<SerialisableDataNode> dataNodes;
    final private CellTable<SerialisableDataNode> cellTable;
    final private ArrayList<String> currentColumns;
    final private DisclosurePanel disclosurePanel;
    final private ScrollPanel scrollPanel;
    final String tableTitle = "Table";

    public DataNodeTable() {
        disclosurePanel = new DisclosurePanel(tableTitle);
//        add(new Label("Table"));
        cellTable = new CellTable<SerialisableDataNode>();
        dataNodes = new ArrayList<SerialisableDataNode>();
        currentColumns = new ArrayList<String>();
        scrollPanel = new ScrollPanel(cellTable);
//        scrollPanel.setSize("100%", "100%");
//        setHeight("100%");
//        setWidth("50%");
        disclosurePanel.add(scrollPanel);
        add(disclosurePanel);
        this.setModal(false);
    }

    public void removeDataNode(SerialisableDataNode yaasDataNode) {
        dataNodes.remove(yaasDataNode);
        updateTable();
        if (dataNodes.isEmpty()) {
            this.hide();
        } else {
            this.center();
        }
    }

    public void addDataNode(SerialisableDataNode yaasDataNode) {
        dataNodes.add(yaasDataNode);
        updateTable();
        this.center();
    }

    private HashSet<String> getColumnLabels() {
        HashSet<String> columnLabels = new HashSet<String>();
        for (SerialisableDataNode dataNode : dataNodes) {
            for (FieldGroup fieldGroup : dataNode.getFieldGroups()) {
                columnLabels.add(fieldGroup.getFieldName());
            }
        }
        return columnLabels;
    }

    private void updateTable() {
        HashSet<String> columnLabels = getColumnLabels();
        for (final String currentName : columnLabels) {
            if (!currentColumns.contains(currentName)) {
                currentColumns.add(currentName);
                cellTable.addColumn(new TextColumn<SerialisableDataNode>() {
                    @Override
                    public String getValue(SerialisableDataNode dataNode) {
                        for (FieldGroup fieldGroup : dataNode.getFieldGroups()) {
                            if (fieldGroup.getFieldName().equals(currentName)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (DataField dataField : fieldGroup.getFields()) {
                                    stringBuilder.append(dataField.getFieldValue());
                                    stringBuilder.append(" - ");
                                };
                                return stringBuilder.toString();
                            }
                        }
                        return "<no value>";
                    }
                }, currentName);
            }
        }
        cellTable.setRowCount(dataNodes.size(), true);
        cellTable.setRowData(dataNodes);
        disclosurePanel.setTitle(tableTitle + " (" + dataNodes.size() + ")");
        disclosurePanel.setOpen(dataNodes.size() > 0);
    }
}
