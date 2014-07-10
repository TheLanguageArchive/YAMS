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
package nl.mpi.yams.common.data;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Aug 26, 2013, 1:44:49 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@XmlRootElement(name = "DatabaseLinks")
public class DatabaseLinks {

    @XmlElement(name = "RootDocumentLinks")
    protected final Set<DataNodeLink> rootLinks = new HashSet<DataNodeLink>();
    @XmlElement(name = "MissingDocumentLinks")
    protected final Set<DataNodeLink> childLinks = new HashSet<DataNodeLink>();
    @XmlElement(name = "RecentDocumentLinks")
    protected final Set<DataNodeLink> recentLinks = new HashSet<DataNodeLink>();

    public void insertRootLink(DataNodeLink dataNodeLink) {
        rootLinks.add(dataNodeLink);
    }

    public void insertChildLink(DataNodeLink dataNodeLink) {
        childLinks.add(dataNodeLink);
    }

    public void removeChildLink(DataNodeLink dataNodeLink) {
        childLinks.remove(dataNodeLink);
    }

    public void insertRecentLink(DataNodeLink dataNodeLink) {
        recentLinks.add(dataNodeLink);
        childLinks.remove(dataNodeLink);
    }

    public Set<DataNodeLink> getRootLinks() {
        return rootLinks;
    }

    public Set<DataNodeLink> getChildLinks() {
        return childLinks;
    }

    public Set<DataNodeLink> getRecentLinks() {
        // todo: recent links are used by the database to update the facets after each run. These recent link are removed via xquery when the facets are updated.
        return recentLinks;
    }

    public void mergeDatabaseLinks(DatabaseLinks databaseLinks) {
        rootLinks.addAll(databaseLinks.getRootLinks());
        childLinks.addAll(databaseLinks.getChildLinks());
        recentLinks.addAll(databaseLinks.recentLinks);
    }

    public void insertLinks(DataNodeLink dataNodeLink, SerialisableDataNode dataNode) throws ModelException {
        recentLinks.add(dataNodeLink);
        if (dataNode.getChildIds() != null) {
            for (DataNodeLink nodeLink : dataNode.getChildIds()) {
                insertChildLink(nodeLink);
            }
        }
    }
}
