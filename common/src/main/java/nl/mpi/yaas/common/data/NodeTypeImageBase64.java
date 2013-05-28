/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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
package nl.mpi.yaas.common.data;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import nl.mpi.flap.model.DataNodeType;

/**
 * Created on: May 28, 2013 14:00 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class NodeTypeImageBase64 implements Serializable {

    private DataNodeType dataNodeType = new DataNodeType();
    private String inlineImageData;

    public String getName() {
        return dataNodeType.getName();
    }

    @XmlAttribute(name = "Name")
    public void setName(String name) {
        dataNodeType.setName(name);
    }

    public String getID() {
        return dataNodeType.getID();
    }

    @XmlAttribute(name = "ID")
    public void setID(String ID) {
        dataNodeType.setID(ID);
    }

    public DataNodeType.FormatType getFormat() {
        return dataNodeType.getFormat();
    }

    @XmlAttribute(name = "Format")
    public void setFormat(DataNodeType.FormatType formatType) {
        dataNodeType.setFormat(formatType);
    }

    public String getInlineImageData() {
        return inlineImageData;
    }

    @XmlValue
    public void setInlineImageData(String inlineImageData) {
        this.inlineImageData = inlineImageData;
    }

    public String getInlineImageDataString() {
        return "data:" + NodeTypeImage.imageFormatString + ";base64," + getInlineImageData();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.dataNodeType != null ? this.dataNodeType.hashCode() : 0);
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
        final NodeTypeImageBase64 other = (NodeTypeImageBase64) obj;
        if (this.dataNodeType != other.dataNodeType && (this.dataNodeType == null || !this.dataNodeType.equals(other.dataNodeType))) {
            return false;
        }
        return true;
    }
}
