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
package nl.mpi.yams.crawler;

import java.util.ArrayList;
import java.util.List;
import nl.mpi.arbil.clarin.profiles.CmdiTemplate;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.templates.ArbilTemplate;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.DataNodeType;
import static nl.mpi.flap.model.DataNodeType.IMDI_RESOURCE;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Mar 20, 2013, 5:26:44 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ArbilDataNodeWrapper extends SerialisableDataNode {

    final private ArbilDataNode arbilDataNode;

    public ArbilDataNodeWrapper(ArbilDataNode arbilDataNode) {
        this.arbilDataNode = arbilDataNode;
    }

    public void checkChildNodesLoaded() throws CrawlerException {
        for (ArbilDataNode childNode : arbilDataNode.getChildArray()) {
            if (!childNode.isDataLoaded() && !childNode.isDataPartiallyLoaded()) {
                throw new CrawlerException("Child node not adequatly loaded, cannot continue.");
            }
        }
    }

    @Override
    public String getID() throws ModelException {
        if (!arbilDataNode.isChildNode()) {
            // not all documents have an archive handle, so we are making things simpler by using the URI as the ID
            // String iD = arbilDataNode.getID();
            String iD = new DataNodeLink(arbilDataNode.getURI().toString(), arbilDataNode.archiveHandle).getIdString();
//            if (iD.isEmpty()) {
//                iD = arbilDataNode.getUrlString();
//            }
            return iD;
        } else {
            return null;
        }
    }

    @Override
    public String getURI() throws ModelException {
        return arbilDataNode.getURI().toString();
    }

    @Override
    public String getArchiveHandle() {
        return arbilDataNode.archiveHandle;
    }

    @Override
    public String getLabel() {
        return arbilDataNode.refreshStringValue();
    }

    @Override
    public DataNodeType getType() {
        final DataNodeType dataNodeType = new DataNodeType();
        if (arbilDataNode.isCmdiMetaDataNode()) {
            dataNodeType.setFormat(DataNodeType.FormatType.cmdi);
            final ArbilTemplate template = arbilDataNode.getNodeTemplate();
            if (template instanceof CmdiTemplate) {
                final CmdiTemplate cmdiTemplate = (CmdiTemplate) template;
                dataNodeType.setLabel(cmdiTemplate.getTemplateName());
                dataNodeType.setID(cmdiTemplate.getTemplateName()); // todo: modify Arbil so that the ID and Name are available
            }
        } else {
            dataNodeType.setFormat(DataNodeType.FormatType.imdi_session);
            if (arbilDataNode.isCatalogue()) {
                dataNodeType.setLabel("Catalogue");
                dataNodeType.setID("imdi.catalogue");
            } else if (arbilDataNode.isCorpus()) {
                dataNodeType.setLabel("Corpus");
                dataNodeType.setID("imdi.corpus");
            } else if (arbilDataNode.isSession()) {
                dataNodeType.setLabel("Session");
                dataNodeType.setID("imdi.session");
            } else if (arbilDataNode.isContainerNode()) {
                dataNodeType.setLabel("Container");
                dataNodeType.setID("container");
            } else if (arbilDataNode.hasResource()) { // a resource node will always be a child node so this would do nothing and is not required
                String mimeTypeForNode = arbilDataNode.getAnyMimeType();
                if (mimeTypeForNode != null) {
                    dataNodeType.setLabel(mimeTypeForNode);
                    dataNodeType.setID(IMDI_RESOURCE);
                } else {
                    dataNodeType.setLabel("Resource");
                    dataNodeType.setID(IMDI_RESOURCE);
                }
            } else if (arbilDataNode.isChildNode()) {
                dataNodeType.setLabel("Subnode");
                dataNodeType.setID("subnode");
            } else if (arbilDataNode.isDirectory()) {
                dataNodeType.setLabel("Directory");
                dataNodeType.setID("directory");
            }
        }
        return dataNodeType;
    }

    @Override
    public List<FieldGroup> getFieldGroups() {
        return arbilDataNode.getFieldGroups();
    }

    @Override
    public List<DataNodeLink> getChildIds() throws ModelException {
        List<DataNodeLink> childIds = new ArrayList<DataNodeLink>();
        for (ArbilDataNode childNode : arbilDataNode.getAllChildren()) {
            if (!childNode.getUrlString().contains("#")) { //!childNode.isChildNode()/* && childNode.isMetaDataNode()*/) {
                // not all documents have an archive handle, however if it does exist then it will be used otherwise the URI is used, as the ID via a hash
                childIds.add(new DataNodeLink(childNode.getUrlString(), childNode.archiveHandle));
            }
        }
        if (arbilDataNode.hasResource()) {
            childIds.add(new DataNodeLink(arbilDataNode.getFullResourceURI().toString(), arbilDataNode.getResourceArchiveHandle()));
        }
        return childIds;
    }

    @Override
    public List<? extends SerialisableDataNode> getChildList() {
        List<SerialisableDataNode> childList = new ArrayList<SerialisableDataNode>();
        for (ArbilDataNode childNode : arbilDataNode.getChildArray()) {
            if (childNode.isChildNode()) {
                childList.add(new ArbilDataNodeWrapper(childNode));
            }
        }
        return childList;
    }

    @Override
    public void setID(String id) {
        throw new UnsupportedOperationException("This value cannot be modified");
    }

    @Override
    public void setLabel(String label) {
        throw new UnsupportedOperationException("This value cannot be modified");
    }

    @Override
    public void setType(DataNodeType dataNodeType) {
        throw new UnsupportedOperationException("This value cannot be modified");
    }

    @Override
    public void setFieldGroups(List<FieldGroup> fieldGroups) {
        throw new UnsupportedOperationException("This value cannot be modified");
    }

    @Override
    public void setChildIds(List<DataNodeLink> childIds) {
        throw new UnsupportedOperationException("This value cannot be modified");
    }

    @Override
    public void setChildList(List<? extends SerialisableDataNode> childNodes) {
        throw new UnsupportedOperationException("This value cannot be modified");
    }
}