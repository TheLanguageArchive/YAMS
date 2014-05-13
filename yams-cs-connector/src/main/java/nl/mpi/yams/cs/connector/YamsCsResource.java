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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.core.database.dao.ArchiveObjectDao;
import nl.mpi.archiving.corpusstructure.core.database.dao.ArchivePropertyDao;
import nl.mpi.archiving.corpusstructure.core.database.dao.CorpusStructureDao;
import nl.mpi.archiving.corpusstructure.core.database.pojo.ArchiveObject;
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

    private final ArchiveObjectDao aoDao;
    private final ArchivePropertyDao archiveDao;
    private final CorpusStructureDao csDao;
//    private final CorpusStructureProvider corpusStructureProvider;
//    private final AccessInfoProvider accessInfoProvider;
    @Autowired
    private CorpusStructureProvider corpusStructureProvider;

    @Autowired
    public YamsCsResource(ArchiveObjectDao aoDao, ArchivePropertyDao archiveDao, CorpusStructureDao csDao) {
        this.aoDao = aoDao;
        this.archiveDao = archiveDao;
        this.csDao = csDao;
    }

    private final static Logger logger = LoggerFactory.getLogger(YamsCsResource.class);
//    private final ArchiveObjectDao aoDao;
//    private final ArchivePropertyDao archiveDao;
//    private final CorpusStructureDao csDao;
//
//
//    }

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
//    @Path("hdl{hdl}")
    public SerialisableDataNode getDataNode(@QueryParam("url") final String nodeUri) throws URISyntaxException {
        CorpusNode corpusNode = this.corpusStructureProvider.getNode(new URI(nodeUri));
        final ArchiveObject archiveObject = aoDao.select(corpusNode.getPID());
        if (archiveObject == null) {
            final SerialisableDataNode serialisableDataNode = new SerialisableDataNode();
            serialisableDataNode.setLabel("corpusNode == null");
            serialisableDataNode.setURI(nodeUri);
            return serialisableDataNode;
        }
//        final ArchiveObject selectedNode = aoDao.select(nodeUri);
//        selectedNode.
//        final SerialisableDataNode dataNode = new NodeWrapper(corpusNode);
//        return dataNode;
        return new ArchiveObjectWrapper(archiveObject);
    }
}
