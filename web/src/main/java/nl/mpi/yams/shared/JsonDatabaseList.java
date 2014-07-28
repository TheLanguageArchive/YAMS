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
 * @since Jul 23, 2014 1:29:51 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class JsonDatabaseList extends JavaScriptObject {

    protected JsonDatabaseList() {
    }

    public final native int getSize() /*-{ return this.DatabaseInfo.length; }-*/;

    public final native String getDatabaseName(int index) /*-{ return this.DatabaseInfo[index].DatabaseName; }-*/;

    public final native int getKnownDocumentsCount(int index) /*-{ return parseInt(this.DatabaseInfo[index].DatabaseStats.KnownDocuments); }-*/;

    public final native int getMissingDocumentsCount(int index) /*-{ return parseInt(this.DatabaseInfo[index].DatabaseStats.MissingDocuments); }-*/;

    public final native int getDuplicateDocumentsCount(int index) /*-{ return parseInt(this.DatabaseInfo[index].DatabaseStats.DuplicateDocuments); }-*/;

    public final native int getRootDocumentsCount(int index) /*-{ return parseInt(this.DatabaseInfo[index].DatabaseStats.RootDocuments); }-*/;

    public final native String getRootDocumentsIDs(int index, int idIndex) /*-{ return this.DatabaseInfo[index].DatabaseStats.RootDocumentID[idIndex].idString; }-*/;
}
