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
package nl.mpi.yams.cs.connector;

import java.net.URI;
import java.util.Date;
import java.util.List;
import nl.mpi.archiving.corpusstructure.core.database.pojo.ArchiveObject;
import nl.mpi.archiving.corpusstructure.core.database.pojo.CorpusStructure;
import nl.mpi.archiving.corpusstructure.core.database.pojo.UserGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 22, 2014 3:51:30 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ArchiveObjectWrapper extends SerialisableDataNode {

    private final ArchiveObject archiveObject;

    public ArchiveObjectWrapper(ArchiveObject archiveObject) {
        this.archiveObject = archiveObject;
    }

    @Override
    public String getLabel() {
        return archiveObject.getName();
    }

    @Override
    public String getArchiveHandle() {
        return archiveObject.getPid();
    }

    @Override
    public String getURI() throws ModelException {
        return archiveObject.getUri();
    }

    public ArchiveObject getNewer() {
        return archiveObject.getNewer(); //To change body of generated methods, choose Tools | Templates.
    }

    public ArchiveObject getOlder() {
        return archiveObject.getOlder(); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getObject_state() {
        return archiveObject.getObject_state(); //To change body of generated methods, choose Tools | Templates.
    }

    public List<CorpusStructure> getPaths() {
        return archiveObject.getPaths(); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getAccesslevel() {
        return archiveObject.getAccesslevel(); //To change body of generated methods, choose Tools | Templates.
    }

    public UserGroup getWriterights() {
        return archiveObject.getWriterights(); //To change body of generated methods, choose Tools | Templates.
    }

    public UserGroup getReadrights() {
        return archiveObject.getReadrights(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getName() {
        return archiveObject.getName(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getFormat() {
        return archiveObject.getFormat(); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getNodetype() {
        return archiveObject.getNodetype(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getOwner() {
        return archiveObject.getOwner(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getCreator() {
        return archiveObject.getCreator(); //To change body of generated methods, choose Tools | Templates.
    }

    public Date getFiletime() {
        return archiveObject.getFiletime(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getChecksum() {
        return archiveObject.getChecksum(); //To change body of generated methods, choose Tools | Templates.
    }

    public Long getFilesize() {
        return archiveObject.getFilesize(); //To change body of generated methods, choose Tools | Templates.
    }

    public Date getLast_update() {
        return archiveObject.getLast_update(); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean isOnsite() {
        return archiveObject.isOnsite(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getPid() {
        return archiveObject.getPid(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getProfile_uri() {
        return archiveObject.getProfile_uri(); //To change body of generated methods, choose Tools | Templates.
    }

//    public String getUri() {
//        return archiveObject.getUri(); //To change body of generated methods, choose Tools | Templates.
//    }

    public Long getId() {
        return archiveObject.getId(); //To change body of generated methods, choose Tools | Templates.
    }

    public URI getCanonicalUri() throws NullPointerException {
        return archiveObject.getCanonicalUri(); //To change body of generated methods, choose Tools | Templates.
    }

    public CorpusStructure getCanonicalPath() {
        return archiveObject.getCanonicalPath(); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean hasPath(CorpusStructure path) {
        return archiveObject.hasPath(path); //To change body of generated methods, choose Tools | Templates.
    }
}
