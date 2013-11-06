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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Document : MetadataFileType Created on : Aug 6, 2012, 1:37:47 PM
 *
 * @author Peter Withers
 */
@XmlRootElement(name = "MetadataFileType")
public class MetadataFileType implements Serializable {

    @XmlElement(name = "Type")
    private String type = null;
    @XmlElement(name = "Path")
    private String path = null;
    @XmlElement(name = "Label")
    private String label = null;
    @XmlElement(name = "Value")
    private String value = null;
    @XmlElement(name = "Count")
    private int recordCount = 0;
    @XmlElement(name = "MetadataFileType")
    private MetadataFileType[] childMetadataTypes = null;

    public MetadataFileType() {
    }

    public MetadataFileType[] getChildMetadataTypes() {
        return childMetadataTypes;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getRecordCount() {
        return recordCount;
    }

    @Override
    public String toString() {
        return label + " (" + recordCount + ")";
    }
}
