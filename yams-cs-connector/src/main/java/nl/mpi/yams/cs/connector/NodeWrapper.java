/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.cs.connector;

import java.util.List;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.core.database.pojo.ArchiveObject;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 22, 2014 3:51:30 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class NodeWrapper extends SerialisableDataNode {

    private final CorpusNode archiveObject;

    public NodeWrapper(CorpusNode archiveObject) {
        this.archiveObject = archiveObject;
    }

    @Override
    public String getLabel() {
        return archiveObject.getName();
    }

    @Override
    public String getArchiveHandle() {
        return archiveObject.getPID().toString();
    }

    @Override
    public String getURI() throws ModelException {
        return archiveObject.getNodeURI().toString();
    }
}
