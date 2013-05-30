/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
public class DataNodeTable extends VerticalPanel {

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
    }

    public void removeDataNode(SerialisableDataNode yaasDataNode) {
        dataNodes.remove(yaasDataNode);
        updateTable();
    }

    public void addDataNode(SerialisableDataNode yaasDataNode) {
        dataNodes.add(yaasDataNode);
        updateTable();
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
                                    stringBuilder.append("<hr>");
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
