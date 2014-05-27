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
        final DataNodeType dataNodeType = new DataNodeType();
        switch (accessInfoProvider.getAccessLevel(archiveObject.getNodeURI())) {
            case ACCESS_LEVEL_CLOSED:
                dataNodeType.setAccessLevel(DataNodeType.AccessLevel.closed);
                break;
            case ACCESS_LEVEL_EXTERNAL:
                dataNodeType.setAccessLevel(DataNodeType.AccessLevel.external);
                break;
            case ACCESS_LEVEL_OPEN_EVERYBODY:
                dataNodeType.setAccessLevel(DataNodeType.AccessLevel.open_everybody);
                break;
            case ACCESS_LEVEL_OPEN_REGISTERED_USERS:
                dataNodeType.setAccessLevel(DataNodeType.AccessLevel.open_registered_users);
                break;
            case ACCESS_LEVEL_PERMISSION_NEEDED:
                dataNodeType.setAccessLevel(DataNodeType.AccessLevel.permission_needed);
                break;
            case ACCESS_LEVEL_UNKNOWN:
                dataNodeType.setAccessLevel(DataNodeType.AccessLevel.unknown);
                break;
        }
        switch (archiveObject.getType()) {
            case COLLECTION:
                dataNodeType.setFormat(DataNodeType.FormatType.cmdi);
                break;
            case IMDICATALOGUE:
                dataNodeType.setFormat(DataNodeType.FormatType.imdi_catalogue);
                break;
            case IMDIINFO:
                dataNodeType.setFormat(DataNodeType.FormatType.imdi_info);
                break;
            case METADATA:
                dataNodeType.setFormat(DataNodeType.FormatType.cmdi);
                break;
            case RESOURCE_ANNOTATION:
                dataNodeType.setFormat(DataNodeType.FormatType.resource_annotation);
                break;
            case RESOURCE_AUDIO:
                dataNodeType.setFormat(DataNodeType.FormatType.resource_audio);
                break;
            case RESOURCE_LEXICAL:
                dataNodeType.setFormat(DataNodeType.FormatType.resource_lexical);
                break;
            case RESOURCE_OTHER:
                dataNodeType.setFormat(DataNodeType.FormatType.resource_other);
                break;
            case RESOURCE_VIDEO:
                dataNodeType.setFormat(DataNodeType.FormatType.resource_video);
                break;
        }
        dataNodeType.setMimeType(archiveObject.getFormat());
        return dataNodeType;
    }

    @Override
    public Integer getLinkCount() {
        // todo: it would be nice if corpus structure provides us with a child link count but instead we get a list
        return corpusStructureProvider.getChildNodeURIs(archiveObject.getNodeURI()).size();
    }
}
