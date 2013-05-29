/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import nl.mpi.yaas.common.data.IconTableBase64;
import nl.mpi.yaas.common.data.NodeTypeImageBase64;

/**
 * Created on : May 27, 2013, 15:35 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class IconInfoPanel extends VerticalPanel {

    final protected IconTableBase64 imageDataForTypes;

    public IconInfoPanel(IconTableBase64 imageDataForTypes) {
        this.imageDataForTypes = imageDataForTypes;

        IconInfoPanel.this.add(new Label("Icon Table Size: " + imageDataForTypes.getNodeTypeImageSet().size()));
        for (NodeTypeImageBase64 typeImageBase64 : imageDataForTypes.getNodeTypeImageSet()) {
            Image image = new Image();
            image.setUrl(typeImageBase64.getInlineImageDataString());
//                    Label testDataLabel = new Label(typeImageBase64.getInlineImageDataString());
            Label idLabel = new Label(typeImageBase64.getID());
            Label nameLabel = new Label(typeImageBase64.getName());
            final HorizontalPanel horizontalPanel = new HorizontalPanel();
            horizontalPanel.add(nameLabel);
            horizontalPanel.add(image);
            horizontalPanel.add(idLabel);
//                    horizontalPanel.add(testDataLabel);
            IconInfoPanel.this.add(horizontalPanel);
        }
    }
}
