/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.cs.connector;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.provider.CorpusStructureProvider;
import nl.mpi.flap.model.SerialisableDataNode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST Web Service
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Path("yamscs")
public class YamsCsResource {

    final private CorpusStructureProvider corpusStructureProvider;
//    private final static Logger logger = LoggerFactory.getLogger(YamsCsResource.class);
//    private final ArchiveObjectDao aoDao;
//    private final ArchivePropertyDao archiveDao;
//    private final CorpusStructureDao csDao;
//

    @Autowired
    public YamsCsResource(CorpusStructureProvider corpusStructureProvider) {
        this.corpusStructureProvider = corpusStructureProvider;
//        logger.debug("Creating cs provider factory with {}, {}, {}", aoDao, archiveDao, csDao);
//        this.aoDao = aoDao;
//        this.archiveDao = archiveDao;
//        this.csDao = csDao;
    }

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
    @Produces("application/xml")
    @Path("hdl{hdl}")
    public SerialisableDataNode getDataNode(@PathParam("hdl") String hdl) throws URISyntaxException {
        CorpusNode corpusNode = this.corpusStructureProvider.getNode(new URI(hdl));
//        final ArchiveObject selectedNode = aoDao.select(hdl);
        final SerialisableDataNode dataNode = new NodeWrapper(corpusNode);
        return dataNode;
//        return null;
    }

    @GET
    @Produces(value = "application/xml")
    public String getXml() {
        return "<a>b</a>";
    }
}
