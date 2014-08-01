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

/**
 * @since May 23, 2014 1:42:18 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class JsonDataNode extends JavaScriptObject {

    protected JsonDataNode() {
    }

    public final native String getLabel() /*-{ return this.Label; }-*/;

    public final native String getArchiveHandle() /*-{ return this.ArchiveHandle; }-*/;

    public final native String getURI() /*-{ return this.URI; }-*/;

    public final native int getLinkCount() /*-{ if (this.ChildLink != null) return parseInt(this.ChildLink.length); else if (this.LinkCount != null) return parseInt(this.LinkCount); else return 0; }-*/;

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
}
