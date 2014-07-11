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
package nl.mpi.yams.client;

import com.google.gwt.user.client.ui.CheckBox;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Mar 26, 2014 2:36:19 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public interface TreeNodeCheckboxListener {

    void stateChanged(boolean selected, SerialisableDataNode dataNode, CheckBox checkBox);
}
