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
package nl.mpi.yams.common.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @since Feb 17, 2014 3:28:13 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@XmlRootElement(name = "DatabaseList")
public class DatabaseList {

//    @XmlElement(name = "QueryTimeMS")
    protected long queryTimeMS = -1;
//    @XmlElementWrapper
    @XmlElement(name = "Databases")
    protected List<DatabaseStats> databaseList = new ArrayList<DatabaseStats>();
//    @XmlElement(name = "NodeIcons")
//    protected IconTable iconTable;

    public DatabaseList() {
    }

    public void setQueryTimeMS(long queryTimeMS) {
        this.queryTimeMS = queryTimeMS;
    }

    public long getQueryTimeMS() {
        return queryTimeMS;
    }

    public List<DatabaseStats> getDatabaseList() {
        return databaseList;
    }
//
//    public void setDatabaseList(List<DatabaseStats> databaseList) {
//        this.databaseList = databaseList;
//    }
//
//    public void addDatabase(DatabaseStats databaseStats) {
//        this.databaseList.add(databaseStats);
//    }
}
