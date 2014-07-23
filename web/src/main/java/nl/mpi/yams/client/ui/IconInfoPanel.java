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
package nl.mpi.yams.client.ui;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import nl.mpi.yams.client.DatabaseInformation;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.client.HistoryListener;
import nl.mpi.yams.common.data.IconTableBase64;
import nl.mpi.yams.common.data.NodeTypeImageBase64;

/**
 * Created on : May 27, 2013, 15:35 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class IconInfoPanel extends VerticalPanel implements HistoryListener {

    private final HistoryController historyController;
    private final DatabaseInformation databaseInfo;

    public IconInfoPanel(HistoryController historyController, DatabaseInformation databaseInfo) {
        this.historyController = historyController;
        this.databaseInfo = databaseInfo;
    }

    public void historyChange() {
        this.setIconInfo(databaseInfo.getDatabaseIcons(historyController.getDatabaseName()));
    }

    public void userSelectionChange() {
        historyChange();
    }

    public void setIconInfo(IconTableBase64 imageDataForTypes) {
        this.clear();
        if (imageDataForTypes != null) {
            IconInfoPanel.this.add(new Label("Icon Table Size: " + imageDataForTypes.getNodeTypeImageSet().size()));
            for (NodeTypeImageBase64 typeImageBase64 : imageDataForTypes.getNodeTypeImageSet()) {
                Image image = new Image();
                image.setUrl(typeImageBase64.getInlineImageDataString());
//                    Label testDataLabel = new Label(typeImageBase64.getInlineImageDataString());
                Label idLabel = new Label(typeImageBase64.getID());
                Label nameLabel = new Label(typeImageBase64.getLabel());
                final HorizontalPanel horizontalPanel = new HorizontalPanel();
                horizontalPanel.add(nameLabel);
                horizontalPanel.add(image);
                horizontalPanel.add(idLabel);
//                    horizontalPanel.add(testDataLabel);
                IconInfoPanel.this.add(horizontalPanel);
            }
        }
    }
}
