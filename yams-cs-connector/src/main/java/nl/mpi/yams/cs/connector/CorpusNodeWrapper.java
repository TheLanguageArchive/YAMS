/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
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
package nl.mpi.yams.cs.connector;

import java.net.URI;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.core.NodeNotFoundException;
import nl.mpi.archiving.corpusstructure.core.service.NodeResolver;
import nl.mpi.archiving.corpusstructure.provider.AccessInfoProvider;
import nl.mpi.archiving.corpusstructure.provider.CorpusStructureProvider;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * @since Apr 22, 2014 3:51:30 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class CorpusNodeWrapper extends SerialisableDataNode {

    private final CorpusNode corpusNode;
    private final AccessInfoProvider accessInfoProvider;
    private final CorpusStructureProvider corpusStructureProvider;
    private final NodeResolver nodeResolver;
    private final String userId;

    public CorpusNodeWrapper(CorpusStructureProvider corpusStructureProvider, AccessInfoProvider accessInfoProvider, NodeResolver nodeResolver, CorpusNode archiveObject, final String userId) {
        this.corpusStructureProvider = corpusStructureProvider;
        this.accessInfoProvider = accessInfoProvider;
        this.corpusNode = archiveObject;
        this.nodeResolver = nodeResolver;
        this.userId = (userId == null) ? "anonymous" : userId;
    }

    @Override
    public String getLabel() {
        return corpusNode.getName();
    }

    @Override
    public String getArchiveHandle() {
        final URI pid = nodeResolver.getPID(corpusNode);
        if (pid == null) {
            // for some reason archive objects do not always have persistent identifiers, even foreign archive nodes should have some sort of PID so this is possibly an issue in Corpus Structure
            return null;
        }
        return pid.toString();
    }

    @Override
    public String getURI() throws ModelException {
        // apparently for some reason getNodeUri has been designed to return only the identifier used to retrieve this node NOT the uri of the actual node!
        return corpusNode.getNodeURI().toString();
    }

    @Override
    public DataNodeType getType() {
        final DataNodeType dataNodeType = new DataNodeType();
        switch (corpusNode.getType()) {
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
        dataNodeType.setMimeType(corpusNode.getFormat());
        return dataNodeType;
    }

    @Override
    public DataNodePermissions getPermissions() {
        final DataNodePermissions permissions = new DataNodePermissions();

        try {
            switch (accessInfoProvider.getAccessLevel(corpusNode.getNodeURI())) {
                case ACCESS_LEVEL_CLOSED:
                    permissions.setAccessLevel(DataNodePermissions.AccessLevel.closed);
                    break;
                case ACCESS_LEVEL_EXTERNAL:
                    permissions.setAccessLevel(DataNodePermissions.AccessLevel.external);
                    break;
                case ACCESS_LEVEL_OPEN_EVERYBODY:
                    permissions.setAccessLevel(DataNodePermissions.AccessLevel.open_everybody);
                    break;
                case ACCESS_LEVEL_OPEN_REGISTERED_USERS:
                    permissions.setAccessLevel(DataNodePermissions.AccessLevel.open_registered_users);
                    break;
                case ACCESS_LEVEL_PERMISSION_NEEDED:
                    permissions.setAccessLevel(DataNodePermissions.AccessLevel.permission_needed);
                    break;
                case ACCESS_LEVEL_UNKNOWN:
                    permissions.setAccessLevel(DataNodePermissions.AccessLevel.unknown);
                    break;
            }
            return permissions;
        } catch (NodeNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Integer getLinkCount() {
        // todo: it would be nice if corpus structure provides us with a child link count but instead we get a list
        return corpusStructureProvider.getChildNodeURIs(corpusNode.getNodeURI()).size();
    }
}
