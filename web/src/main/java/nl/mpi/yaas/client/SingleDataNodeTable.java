/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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
public class SingleDataNodeTable extends Grid {

    private static final Logger logger = Logger.getLogger("");

    public SingleDataNodeTable(final SerialisableDataNode yaasDataNode, ClickHandler closeHandler) {
        super(yaasDataNode.getFieldGroups().size() + 1, 2);
        final List<FieldGroup> fieldGroups = yaasDataNode.getFieldGroups();
        int rowCounter = 0;
        final VerticalPanel linksPanel = new VerticalPanel();
        final Button closeButton = new Button("close", closeHandler);
        final Button browseButton = new Button("browse", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    Window.open(yaasDataNode.getURI(), "_blank", "");
                } catch (ModelException exception) {
                    logger.log(Level.SEVERE, "ClickEvent", exception);
                }
            }
        });
        linksPanel.add(closeButton);
        linksPanel.add(browseButton);
        setWidget(rowCounter, 0, linksPanel);
        rowCounter++;
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
