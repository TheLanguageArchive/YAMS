/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Apr 2, 2013, 3:44:06 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DataNodeTable extends VerticalPanel {

    public DataNodeTable() {
        add(new Label("Table"));
    }

    public void removeDataNode(SerialisableDataNode yaasDataNode) {
    }

    public void addDataNode(SerialisableDataNode yaasDataNode) {
        CellTable<FieldGroup> cellTable;
        final List<FieldGroup> fieldGroups = yaasDataNode.getFieldGroups();
        cellTable = new CellTable<FieldGroup>();
        cellTable.addColumn(new TextColumn<FieldGroup>() {
            @Override
            public String getValue(FieldGroup fieldGroup) {
                return fieldGroup.getFieldName();
            }
        }, "Name");
        cellTable.addColumn(new TextColumn<FieldGroup>() {
            @Override
            public String getValue(FieldGroup fieldGroup) {
                StringBuilder stringBuilder = new StringBuilder();
                for (DataField dataField : fieldGroup.getFields()) {
                    stringBuilder.append(dataField.getFieldValue());
                    stringBuilder.append("<hr>");
                };
                return stringBuilder.toString();
            }
        }, "Value");
        cellTable.setRowCount(fieldGroups.size(), true);
        cellTable.setRowData(fieldGroups);
        add(cellTable);
    }
}
