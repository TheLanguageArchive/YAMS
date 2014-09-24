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

import com.google.gwt.user.client.ui.Label;
import nl.mpi.flap.model.PluginDataNode;

/**
 * @since Apr 8, 2014 5:36:56 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class MetadataDetailsPanel extends AbstractDetailsPanel {

    protected void addDataNodePanel(PluginDataNode dataNode) {
        this.add(createDataNodeDetailsPanel(dataNode));
        if (getHiddenNodeCount() > 0) {
            this.add(new Label("Hidden " + getHiddenNodeCount() + " nodes that have no data to show."));
        }
    }

}
