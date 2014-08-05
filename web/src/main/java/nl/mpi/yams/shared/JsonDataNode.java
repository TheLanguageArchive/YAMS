/*
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
package nl.mpi.yams.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;
import java.util.List;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.PluginDataNode;

/**
 * @since May 23, 2014 1:42:18 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public final class JsonDataNode extends JavaScriptObject implements PluginDataNode {

    protected JsonDataNode() {
    }

    public final native String getLabel() /*-{ return this.Label; }-*/;

    public final native String getArchiveHandle() /*-{ return this.ArchiveHandle; }-*/;

    public final native String getURI() /*-{ return this.URI; }-*/;

    public final native int getLinkCountInt() /*-{ if (this.ChildLink != null) return parseInt(this.ChildLink.length); else if (this.LinkCount != null) return parseInt(this.LinkCount); else return 0; }-*/;

    public final native String getID() /*-{ return this.ID; }-*/;

    public final native String getTypeName() /*-{ return this.Type.Name; }-*/;

    public final native String getTypeID() /*-{ return this.Type.ID; }-*/;

    public final native String getTypeFormat() /*-{ return this.Type.Format; }-*/;

    public final native String getTypeAccessLevel() /*-{ if (this.Permissions != null) return this.Permissions.AccessLevel; else return null;}-*/;

    public final native int getHighlightCount() /*-{ return this.Highlight.length; }-*/;

    public final native String getHighlightId(int index) /*-{ return this.Highlight[index].ID; }-*/;

    public final native String getHighlightPath(int index) /*-{ return this.Highlight[index].Path; }-*/;

    public final native String getChildLinkId(int index) /*-{ return this.ChildLink[index].ID; }-*/;

    public final native String getURI(int index) /*-{ return this.ChildLink[index].URI; }-*/;

    public final native String getChildLinkArchiveHandle(int index) /*-{ return this.ChildLink[index].ArchiveHandle; }-*/;

    public final native int getFieldGroupCount() /*-{ if (this.FieldGroup != null) return parseInt(this.FieldGroup.length); else return 0; }-*/;

    public final native String getFieldGroupLabel(int index) /*-{ return this.FieldGroup[index].Label; }-*/;

    public final native int getFieldDataCount(int index) /*-{ if (this.FieldGroup[index].FieldData != null) return parseInt(this.FieldGroup[index].FieldData.length); else return 0; }-*/;

    public final native String getFieldValue(int index, int fieldIndex) /*-{ return this.FieldGroup[index].FieldData[fieldIndex].FieldValue; }-*/;

    public final native String getFieldPath(int index, int fieldIndex) /*-{ return this.FieldGroup[index].FieldData[fieldIndex].Path; }-*/;

    public final native String getFieldLanguageId(int index, int fieldIndex) /*-{ return this.FieldGroup[index].FieldData[fieldIndex].LanguageId; }-*/;

    public final native String getFieldKeyName(int index, int fieldIndex) /*-{ return this.FieldGroup[index].FieldData[fieldIndex].KeyName; }-*/;

    public final native int getChildCount() /*-{ if (this.DataNode != null) return parseInt(this.DataNode.length); else return 0; }-*/;

    public final native JsArray<JsonDataNode> getChild(int index) /*-{ return this.DataNode[index]; }-*/;

    public DataNodeType getType() {
        final DataNodeType dataNodeType = new DataNodeType(this.getLabel(), this.getTypeName(), this.getTypeID(), DataNodeType.FormatType.valueOf(this.getTypeFormat()));
        return dataNodeType;
    }

    public List<FieldGroup> getFieldGroups() {
        final ArrayList<FieldGroup> fieldGroups = new ArrayList<FieldGroup>();
        for (int groupIndex = 0; groupIndex < this.getFieldGroupCount(); groupIndex++) {
            final ArrayList dataFieldList = new ArrayList<DataField>();
            for (int fieldIndex = 0; fieldIndex < this.getFieldDataCount(groupIndex); fieldIndex++) {
                final DataField dataField = new DataField();
                dataField.setFieldValue(this.getFieldValue(groupIndex, fieldIndex));
                dataField.setKeyName(this.getFieldKeyName(groupIndex, fieldIndex));
                dataField.setLanguageId(this.getFieldLanguageId(groupIndex, fieldIndex));
                dataField.setPath(this.getFieldPath(groupIndex, fieldIndex));
                dataFieldList.add(dataField);
            }
            fieldGroups.add(new FieldGroup(this.getFieldGroupLabel(groupIndex), dataFieldList));
        }
        return fieldGroups;
    }

    public List<JsonDataNode> getChildList() {
        final ArrayList childList = new ArrayList<JsonDataNode>();
        for (int index = 0; index < this.getChildCount(); index++) {
            childList.add(this.getChild(index));
        }
        return childList;
    }

    public DataNodePermissions getPermissions() {
        final DataNodePermissions dataNodePermissions = new DataNodePermissions();
        final String typeAccessLevel = this.getTypeAccessLevel();
        if (typeAccessLevel != null) {
            dataNodePermissions.setAccessLevel(DataNodePermissions.AccessLevel.valueOf(typeAccessLevel));
        }
        return dataNodePermissions;
    }

    public List<DataNodeLink> getChildIds() throws ModelException {
        List<DataNodeLink> links = new ArrayList<DataNodeLink>();
        for (int index = 0; index < this.getLinkCount(); index++) {
            final DataNodeLink dataNodeLink = new DataNodeLink();
            dataNodeLink.setIdString(this.getChildLinkId(index));
            links.add(dataNodeLink);
        }
        return links;
    }

    public Integer getLinkCount() {
        return this.getLinkCountInt();
    }
}
