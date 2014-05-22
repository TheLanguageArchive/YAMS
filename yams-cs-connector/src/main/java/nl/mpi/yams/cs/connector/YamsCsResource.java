/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.provider.AccessInfoProvider;
import nl.mpi.archiving.corpusstructure.provider.CorpusStructureProvider;
import nl.mpi.flap.model.SerialisableDataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * REST Web Service
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Component
@Path("yamscs")
public class YamsCsResource {

    @Autowired
    private CorpusStructureProvider corpusStructureProvider;
//    @Autowired
//    private CorpusStructureTreeModelProvider corpusStructureTreeModelProvider;
    @Autowired
    private AccessInfoProvider accessInfoProvider;

    public YamsCsResource() {
    }

    private final static Logger logger = LoggerFactory.getLogger(YamsCsResource.class);

//    private final ArchiveObjectDao aoDao;
//    private final ArchivePropertyDao archiveDao;
//    private final CorpusStructureDao csDao;
//
//
//    }
    public void setAccessInfoProvider(AccessInfoProvider accessInfoProvider) {
        this.accessInfoProvider = accessInfoProvider;
    }

    public void setCorpusStructureProvider(CorpusStructureProvider corpusStructureProvider) {
        this.corpusStructureProvider = corpusStructureProvider;
    }

//    @Autowired
//    public YamsCsResource(ArchiveObjectDao aoDao, ArchivePropertyDao archiveDao, CorpusStructureDao csDao) {
//        logger.debug("Creating YamsCsResource with {}, {}, {}", aoDao, archiveDao, csDao);
//        this.aoDao = aoDao;
//        this.archiveDao = archiveDao;
//        this.csDao = csDao;
//        this.corpusStructureProvider = new CorpusStructureProviderImpl(archiveDao, aoDao, csDao);
//        this.accessInfoProvider = new AccessInfoProviderImpl(aoDao);
//    }
//
//    public YamsCsResource(CorpusStructureProvider corpusStructureProvider, AccessInfoProvider accessInfoProvider) {
//        logger.debug("Creating YamsCsResource with {}, {}", corpusStructureProvider, accessInfoProvider);
//        this.aoDao = null;
//        this.archiveDao = null;
//        this.csDao = null;
//        this.corpusStructureProvider = corpusStructureProvider;
//        this.accessInfoProvider = accessInfoProvider;
//    }
//    public YamsCsResource() {
//        aoDao = null;
//    }
//    /**
//     * Retrieves a SerialisableDataNode wrapping the response from corpus
//     * structure 2
//     *
//     * @param hdl the archive handle for the desired node
//     * @return an instance of SerialisableDataNode
//     */
    @GET
    @Produces("application/json")
    @Path("root")
    public SerialisableDataNode getRootNode(@Context HttpServletRequest request) throws URISyntaxException {
//        LinkedTreeNode linkedTreeNode = this.corpusStructureTreeModelProvider.getRoot();
        final URI rootNodeURI = this.corpusStructureProvider.getRootNodeURI();
        CorpusNode corpusNode = this.corpusStructureProvider.getNode(rootNodeURI);
        if (corpusNode == null) {
            throw new URISyntaxException(rootNodeURI.toString(), "Could not retrieve the corpus root node.");
        }
        return new CorpusNodeWrapper(accessInfoProvider, corpusNode, request.getRemoteUser());
    }

    @GET
    @Produces("application/json")
//    @Path("hdl{hdl}")
    public List<SerialisableDataNode> getChildDataNodes(@Context HttpServletRequest request, @QueryParam("url") final String nodeUri, @QueryParam("start") @DefaultValue("0") final int start, @QueryParam("end") @DefaultValue("30") final int end) throws URISyntaxException {
        final List<SerialisableDataNode> serialisableDataNodes = new ArrayList<SerialisableDataNode>();
        for (CorpusNode corpusNode : this.corpusStructureProvider.getChildNodes(new URI(nodeUri)).subList(start, end)) {
            serialisableDataNodes.add(new CorpusNodeWrapper(accessInfoProvider, corpusNode, request.getRemoteUser()));
        }
        return serialisableDataNodes;
    }
}
