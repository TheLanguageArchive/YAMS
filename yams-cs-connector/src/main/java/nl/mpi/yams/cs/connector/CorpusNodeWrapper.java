/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.cs.connector;

import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.provider.AccessInfoProvider;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 22, 2014 3:51:30 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class CorpusNodeWrapper extends SerialisableDataNode {

    private final CorpusNode archiveObject;
    private final AccessInfoProvider accessInfoProvider;
    private final String userId;

    public CorpusNodeWrapper(AccessInfoProvider accessInfoProvider, CorpusNode archiveObject, final String userId) {
        this.accessInfoProvider = accessInfoProvider;
        this.archiveObject = archiveObject;
        this.userId = (userId == null) ? "anonymous" : userId;
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

    @Override
    public DataNodeType getType() {
        String iconsString = "";
//        if (accessInfoProvider.hasReadAccess(archiveObject.getNodeURI(), userId)) {
//            iconsString += "R";
//        }
//        if (accessInfoProvider.hasWriteAccess(archiveObject.getNodeURI(), userId)) {
//            iconsString += "W";
//        }
        return new DataNodeType(userId, iconsString, DataNodeType.FormatType.xml);
    }
}
