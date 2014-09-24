/*
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.yams.client.ui;

import java.util.logging.Logger;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;
import nl.mpi.yams.client.HistoryData;
import nl.mpi.yams.client.TreeNodeClickListener;
import nl.mpi.yams.client.controllers.HistoryController;
import nl.mpi.yams.common.data.DataNodeId;

/**
 * @since Aug 19, 2014 2:18:06 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class TreeActionPanelClickListener implements TreeNodeClickListener {

    private static final Logger logger = Logger.getLogger(ArchiveTreePanel.class.getName());
    private final HistoryController historyController;

    public TreeActionPanelClickListener(HistoryController historyController) {
        this.historyController = historyController;
    }

    public void clickEvent(PluginDataNode dataNode) {
        logger.info("TreeNodeClickListener");
        try {
            String id = dataNode.getArchiveHandle();
            if (id == null) {
                id = dataNode.getURI();//new DataNodeLink(dataNode.getURI(), dataNode.getArchiveHandle()).getIdString();
            }
            final HistoryData.NodeActionType nodeAction;
            switch (dataNode.getType().getFormat()) {
                //TODO: details for resources, too: <https://trac.mpi.nl/ticket/4202>
                case cmdi:
                case imdi_catalogue:
                case imdi_corpus:
                case imdi_info:
                case imdi_session:
                    nodeAction = HistoryData.NodeActionType.details;
                    break;
                case resource_annotation:
                case resource_lexical:
                case resource_audio:
                case resource_video:
                case resource_other:
                    nodeAction = HistoryData.NodeActionType.resourceDetails;
                    break;
                default:
                    nodeAction = HistoryData.NodeActionType.view;
            }
            logger.info(id);
            historyController.setBranchSelection(new DataNodeId(id), nodeAction);
        } catch (ModelException exception) {
            logger.warning(exception.getMessage());
        }
    }
}
