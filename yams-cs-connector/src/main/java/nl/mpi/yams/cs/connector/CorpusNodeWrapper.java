/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.cs.connector;

import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.provider.AccessInfoProvider;
import nl.mpi.archiving.corpusstructure.provider.CorpusStructureProvider;
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
    private final CorpusStructureProvider corpusStructureProvider;
    private final String userId;

    public CorpusNodeWrapper(CorpusStructureProvider corpusStructureProvider, AccessInfoProvider accessInfoProvider, CorpusNode archiveObject, final String userId) {
        this.corpusStructureProvider = corpusStructureProvider;
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
        switch (accessInfoProvider.getAccessLevel(archiveObject.getNodeURI())) {
            case ACCESS_LEVEL_CLOSED:
                iconsString += "C";
                break;
            case ACCESS_LEVEL_EXTERNAL:
                iconsString += "E";
                break;
            case ACCESS_LEVEL_OPEN_EVERYBODY:
                iconsString += "O";
                break;
            case ACCESS_LEVEL_OPEN_REGISTERED_USERS:
                iconsString += "R";
                break;
            case ACCESS_LEVEL_PERMISSION_NEEDED:
                iconsString += "P";
                break;
            case ACCESS_LEVEL_UNKNOWN:
                iconsString += "U";
                break;
            default:
                iconsString += "X";
                break;
        }
//        if (accessInfoProvider.hasReadAccess(archiveObject.getNodeURI(), userId)) {
//            iconsString += "R";
//        }
//        if (accessInfoProvider.hasWriteAccess(archiveObject.getNodeURI(), userId)) {
//            iconsString += "W";
//        }
        switch (archiveObject.getType()) {
            case COLLECTION:
                iconsString += "T";
                break;
            case IMDICATALOGUE:
                iconsString += "C";
                break;
            case IMDIINFO:
                iconsString += "I";
                break;
            case METADATA:
                iconsString += "M";
                break;
            case RESOURCE_ANNOTATION:
                iconsString += "N";
                break;
            case RESOURCE_AUDIO:
                iconsString += "A";
                break;
            case RESOURCE_LEXICAL:
                iconsString += "L";
                break;
            case RESOURCE_OTHER:
                iconsString += "O";
                break;
            case RESOURCE_VIDEO:
                iconsString += "V";
                break;
            default:
                iconsString += "X";
                break;
        }
        return new DataNodeType(archiveObject.getFormat(), iconsString, DataNodeType.FormatType.xml);
    }

    @Override
    public Integer getLinkCount() {
        // todo: it would be nice if corpus structure provides us with a child link count but instead we get a list
        return corpusStructureProvider.getChildNodeURIs(archiveObject.getNodeURI()).size();
    }
}
