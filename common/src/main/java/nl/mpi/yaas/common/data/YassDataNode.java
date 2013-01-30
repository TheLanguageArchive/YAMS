/*
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
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
package nl.mpi.yaas.common.data;

import java.net.URI;
import java.util.List;
import javax.swing.ImageIcon;
import nl.mpi.flap.model.AbstractDataNode;
import nl.mpi.flap.model.FieldGroup;

/**
 * Created on : Jan 29, 2013, 5:58:41 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YassDataNode extends AbstractDataNode {

    @Override
    public String getID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URI getURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImageIcon getIcon() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FieldGroup> getFieldGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AbstractDataNode[] getChildArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
