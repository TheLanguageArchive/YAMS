/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Aug 28, 2013, 5:38:42 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class HighlighableDataNode extends SerialisableDataNode implements Serializable {

    private List<DataNodeHighlight> highlights;

    public List<DataNodeHighlight> getHighlights() {
        return highlights;
    }

    @XmlElement(name = "Highlight")
    public void setHighlights(List<DataNodeHighlight> highlighedLinks) {
        this.highlights = highlighedLinks;
    }

    public List<DataNodeHighlight> getHighlightsForNode(SerialisableDataNode dataNode) throws ModelException {
        final String id = dataNode.getID();
        return getHighlightsForNode(id);
    }

    public List<DataNodeHighlight> getHighlightsForNode(String id) throws ModelException {
        List<DataNodeHighlight> returnList = new ArrayList<DataNodeHighlight>();
        for (DataNodeHighlight highlight : highlights) {
            if (highlight.getDataNodeId().equals(id)) {
                returnList.add(highlight);
            }
        }
        return returnList;
    }
}
