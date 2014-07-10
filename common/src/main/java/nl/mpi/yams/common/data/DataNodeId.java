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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created on : Apr 2, 2013, 5:44:48 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@XmlRootElement(name = "DataNodeId")
public class DataNodeId implements Serializable {

    @XmlValue
    String idString = null;

    protected DataNodeId() {
    }

    public DataNodeId(String idString) {
        this.idString = idString;
    }

    public String getIdString() {
        return idString;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.idString != null ? this.idString.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataNodeId other = (DataNodeId) obj;
        if ((this.idString == null) ? (other.idString != null) : !this.idString.equals(other.idString)) {
            return false;
        }
        return true;
    }
}
