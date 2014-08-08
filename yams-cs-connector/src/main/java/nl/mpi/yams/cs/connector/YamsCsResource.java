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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.provider.AccessInfoProvider;
import nl.mpi.archiving.corpusstructure.provider.CorpusStructureProvider;
import nl.mpi.flap.model.SerialisableDataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * YAMS REST Web Service
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Component
@Path("")
public class YamsCsResource {

    @Autowired
    private CorpusStructureProvider corpusStructureProvider;
    @Autowired
    private AccessInfoProvider accessInfoProvider;
    private final static Logger logger = LoggerFactory.getLogger(YamsCsResource.class);

    public void setAccessInfoProvider(AccessInfoProvider accessInfoProvider) {
        this.accessInfoProvider = accessInfoProvider;
    }

    public void setCorpusStructureProvider(CorpusStructureProvider corpusStructureProvider) {
        this.corpusStructureProvider = corpusStructureProvider;
    }

    /**
     * Retrieves a SerialisableDataNode wrapping the response from corpus
     * structure 2
     *
     * @param hdl the archive handle for the desired node
     * @return an instance of SerialisableDataNode
     */
    @GET
    @Produces("application/json")
//    @Path("root")
    public Response getRootNode(@Context HttpServletRequest request) throws URISyntaxException {
//        LinkedTreeNode linkedTreeNode = this.corpusStructureTreeModelProvider.getRoot();
        final List<SerialisableDataNode> nodeWrappers = new ArrayList<SerialisableDataNode>();
        final URI rootNodeURI = this.corpusStructureProvider.getRootNodeURI();
        CorpusNode corpusNode = this.corpusStructureProvider.getNode(rootNodeURI);
        if (corpusNode == null) {
            throw new URISyntaxException(rootNodeURI.toString(), "Could not retrieve the corpus root node.");
        }
        final CorpusNodeWrapper corpusNodeWrapper = new CorpusNodeWrapper(corpusStructureProvider, accessInfoProvider, corpusNode, request.getRemoteUser());
        nodeWrappers.add(corpusNodeWrapper);
        return Response.ok(nodeWrappers).header("Access-Control-Allow-Origin", "*").build();

    }

    @GET
    @Produces("application/json")
    @Path("linksof")
//    @Path("hdl{hdl}")
    public Response getChildDataNodes(@Context HttpServletRequest request, @QueryParam("id") final String nodeUri, @QueryParam("start") @DefaultValue("0") final int start, @QueryParam("end") @DefaultValue("30") final int end) throws URISyntaxException {
        final List<SerialisableDataNode> nodeWrappers = new ArrayList<SerialisableDataNode>();
        final List<CorpusNode> childNodes = this.corpusStructureProvider.getChildNodes(new URI(nodeUri));
        final int lastToGet = (childNodes.size() > end) ? end : childNodes.size();
        for (CorpusNode corpusNode : childNodes.subList(start, lastToGet)) {
            nodeWrappers.add(new CorpusNodeWrapper(corpusStructureProvider, accessInfoProvider, corpusNode, request.getRemoteUser()));
        }
        return Response.ok(nodeWrappers).header("Access-Control-Allow-Origin", "*").build();
    }

//    @GET
//    @Produces("application/json")
//    @Path("versions")
////    @Path("hdl{hdl}")
//    public Response getDataNodeVersions(@Context HttpServletRequest request, @QueryParam("id") final String nodeUri) throws URISyntaxException {        
//todo: this will require the archive objects and related spring things to be configured
//        final CorpusNode corpusNode = this.corpusStructureProvider.getNode(new URI(nodeUri));        
//        return Response.ok(corpusNode.getFileInfo().).header("Access-Control-Allow-Origin", "*").build();
//    }
}
