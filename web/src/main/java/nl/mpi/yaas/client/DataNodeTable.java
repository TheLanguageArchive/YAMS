/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
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
//extends CellTable<YaasDataNode> {
//

    public void addDataNode(SerialisableDataNode yaasDataNode) {
        clear();
        add(new Label("addDataNode"));
        for (FieldGroup fieldGroup : yaasDataNode.getFieldGroups()) {
            final HorizontalPanel horizontalPanel = new HorizontalPanel();
            horizontalPanel.add(new Label(fieldGroup.getFieldName()));
            final VerticalPanel verticalPanel = new VerticalPanel();
            for (DataField dataField : fieldGroup.getFields()) {
                verticalPanel.add(new Label(dataField.getFieldValue()));
//                for (FieldGroup fieldGroup : yaasDataNode.getFieldGroups()) {
//                        addItem(new Label(fieldGroup.getFieldName()));
//                        for (DataField dataField : fieldGroup.getFields()) {
//                            addItem(new Label(dataField.getFieldValue()));
//                        }
//                    }
            }
            horizontalPanel.add(verticalPanel);
            add(horizontalPanel);
        }
    }
//
//        addColumn(new Column(new TextCell()) {
//            @Override
//            public Object getValue(Object object) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        });
//        add
//    }
}
