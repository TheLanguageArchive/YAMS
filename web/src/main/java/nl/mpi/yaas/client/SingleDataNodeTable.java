/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Apr 5, 2013, 2:05:30 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SingleDataNodeTable extends VerticalPanel {

    private static final Logger logger = Logger.getLogger("");

    public SingleDataNodeTable(final SerialisableDataNode yaasDataNode, ClickHandler closeHandler) {
        final List<FieldGroup> fieldGroups = yaasDataNode.getFieldGroups();
        int rowCounter = 0;
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
        } catch (ModelException exception) {
            logger.log(Level.SEVERE, "ClickEvent", exception);
        }
        add(linksPanel);
        add(grid);
        for (FieldGroup fieldGroup : fieldGroups) {
            grid.setText(rowCounter, 0, fieldGroup.getFieldName());
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            for (DataField dataField : fieldGroup.getFields()) {
                horizontalPanel.add(new Label(dataField.getFieldValue()));
            }
            grid.setWidget(rowCounter, 1, horizontalPanel);
            rowCounter++;
        }
    }
}
