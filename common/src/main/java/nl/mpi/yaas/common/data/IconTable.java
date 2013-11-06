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

import java.awt.Image;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import nl.mpi.flap.model.DataNodeType;

/**
 * Created on: May 10, 2013 11:43 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@XmlRootElement(name = "NodeIcons")
public class IconTable {

//    @XmlElementWrapper(name = "NodeIcons")
    @XmlElement(name = "TypeIcon")
    final Set<NodeTypeImage> nodeTypeIconSet;

    public IconTable() {
//        typeIconMap = new HashMap<DataNodeType, ImageIcon>();
        nodeTypeIconSet = new HashSet<NodeTypeImage>();
    }

    public void addTypeIcon(DataNodeType dataNodeType, Image imageData) {
        nodeTypeIconSet.add(new NodeTypeImage(dataNodeType, imageData));
    }

    public void addTypeIcon(NodeTypeImage nodeTypeImage) {
        nodeTypeIconSet.add(nodeTypeImage);
    }

    public Set<NodeTypeImage> getNodeTypeImageSet() {
        return Collections.unmodifiableSet(nodeTypeIconSet);
    }
//    public void serialiseTypeIcons() {
//        Image img = new_i.getImage();
//
//        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null, BufferedImage.TYPE_BYTE_ARGB);
//        Graphics2D g2 = bi.createGraphics();
//// Draw img into bi so we can write it to file.
//        g2.drawImage(img, 0, 0, null);
//        g2.dispose();
//// Now bi contains the img.
//// Note: img may have transparent pixels in it; if so, okay.
//// If not and you can use TYPE_INT_RGB you will get better
//// performance with it in the jvm.
//        ImageIO.write(bi, "jpg", new File("new_abc.jpg"));
//
//    }
}
