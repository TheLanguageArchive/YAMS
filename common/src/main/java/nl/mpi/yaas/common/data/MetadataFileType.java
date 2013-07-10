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

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
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
    @XmlElement(name = "fieldName")
    private String fieldName = null;
    @XmlElement(name = "Label")
    private String displayString = null;
    @XmlElement(name = "profileString")
    private String profileString = null;
    @XmlElement(name = "arbilPathString")
    private String arbilPathString = null;
    @XmlElement(name = "Count")
    private int recordCount = 0;
//    @XmlElementWrapper(name = "childMetadataTypes")
    @XmlElement(name = "MetadataFileType")
    private MetadataFileType[] childMetadataTypes = null;

    public MetadataFileType() {
    }

//    public MetadataFileType(String rootXpath, String pathPart, String displayString) {
//        this.rootXpath = rootXpath;
//        this.pathPart = pathPart;
//        this.displayString = displayString;
//    }
    public MetadataFileType[] getChildMetadataTypes() {
        return childMetadataTypes;
    }

    public String getType() {
        return type;
    }

    public String getArbilPathString() {
        return arbilPathString.replaceAll("\"[^\"]*\":", "*:").replaceAll("\\[\\d*\\]", "");
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getProfileIdString() {
        if (profileString != null) {
//            Pattern regexPattern = Pattern.compile(".*(clarin.eu:cr1:p_[0-9]+).*");
//            Matcher matcher = regexPattern.matcher(profileString);
//            while (matcher.find()) {
//                return matcher.group(1);
//            }
            return profileString;
        }
        return null;
    }

    @Override
    public String toString() {
        // todo: we really don't want to be putting this view code in this data object
        if (displayString == null) {
            if (type != null) {
                displayString = type;
            } else if (fieldName != null) {
                displayString = fieldName;
            } else if (profileString != null) {
                displayString = getProfileIdString();
            }
        }
        return displayString + " (" + recordCount + ")";
    }
}
