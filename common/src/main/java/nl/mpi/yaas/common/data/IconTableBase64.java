/**
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
package nl.mpi.yaas.common.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import nl.mpi.flap.model.DataNodeType;

/**
 * Created on: May 28, 2013 14:00 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@XmlRootElement(name = "NodeIcons")
public class IconTableBase64 implements Serializable {

    @XmlElement(name = "TypeIcon")
    private Set<NodeTypeImageBase64> nodeTypeIconSet;

    public IconTableBase64() {
        nodeTypeIconSet = new HashSet<NodeTypeImageBase64>();
    }

    public Set<NodeTypeImageBase64> getNodeTypeImageSet() {
        return Collections.unmodifiableSet(nodeTypeIconSet);
    }

    /**
     * Finds the first NodeTypeImageBase64 that matches the supplied DataNodeType
     *
     * @param the type information of the data node in the form of a
     * DataNodeType
     * @return a NodeTypeImageBase64 that contains the icon for the supplied
     * node type or null if no matching type image is found
     */
    public NodeTypeImageBase64 getByType(DataNodeType dataNodeType) {
        for (NodeTypeImageBase64 nodeTypeImage : nodeTypeIconSet) {
            if (nodeTypeImage.getDataNodeType().equals(dataNodeType)) {
                return nodeTypeImage;
            }
        }
        return null;
    }
}
