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

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlValue;
import nl.mpi.flap.model.DataNodeType;

/**
 * Created on: May 10, 2013 11:51:35 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class NodeTypeImage {

    private DataNodeType dataNodeType;
    private Image imageData;

    public NodeTypeImage() {
        dataNodeType = new DataNodeType();
    }

    public NodeTypeImage(DataNodeType dataNodeType, ImageIcon imageIcon) {
        this.dataNodeType = dataNodeType;
        this.imageData = imageIcon.getImage();
    }

    public DataNodeType getDataNodeType() {
        return dataNodeType;
    }

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

    public Image getImageData() {
        return imageData;
    }

    @XmlValue
    @XmlMimeType("image/jpeg")
    public void setImageData(Image imageData) {
        this.imageData = imageData;
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
        final NodeTypeImage other = (NodeTypeImage) obj;
        if (this.dataNodeType != other.dataNodeType && (this.dataNodeType == null || !this.dataNodeType.equals(other.dataNodeType))) {
            return false;
        }
        return true;
    }
}
