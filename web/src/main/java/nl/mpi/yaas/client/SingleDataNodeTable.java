/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import java.util.List;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Apr 5, 2013, 2:05:30 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SingleDataNodeTable extends Grid {

    public SingleDataNodeTable(final SerialisableDataNode yaasDataNode) {
        super(yaasDataNode.getFieldGroups().size(), 2);
        final List<FieldGroup> fieldGroups = yaasDataNode.getFieldGroups();
        int rowCounter = 0;
        for (FieldGroup fieldGroup : fieldGroups) {
            setText(rowCounter, 0, fieldGroup.getFieldName());
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            for (DataField dataField : fieldGroup.getFields()) {
                horizontalPanel.add(new Label(dataField.getFieldValue()));
            }
            setWidget(rowCounter, 1, horizontalPanel);
            rowCounter++;
        }
    }
}
