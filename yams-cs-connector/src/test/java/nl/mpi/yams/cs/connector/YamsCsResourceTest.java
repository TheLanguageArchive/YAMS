/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.cs.connector;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.core.CorpusNodeType;
import nl.mpi.archiving.corpusstructure.core.FileInfo;
import nl.mpi.archiving.corpusstructure.provider.CorpusStructureProvider;
import nl.mpi.flap.model.SerialisableDataNode;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YamsCsResourceTest {

    private Mockery context = new JUnit4Mockery();
//    private ArchivePropertyDao archiveDao;
//    private ArchiveObjectDao aoDao;
//    private CorpusStructureDao csDao;
//    private VPathService vPathService;
    private CorpusStructureProvider corpusStructureProvider;

    @Before
    public void setUp() {
//        archiveDao = context.mock(ArchivePropertyDao.class);
//        aoDao = context.mock(ArchiveObjectDao.class);
//        csDao = context.mock(CorpusStructureDao.class);
//        vPathService = context.mock(VPathService.class);        
        corpusStructureProvider = context.mock(CorpusStructureProvider.class);
//        corpusStructureProviderImpl = new CorpusStructureProviderImpl(archiveDao, aoDao, csDao);
//        corpusStructureProviderImpl.initializeWithVPathService(vPathService);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getXml method, of class YamsCsResource.
     */
    @Test
    public void testGetXml() throws URISyntaxException {
        System.out.println("getXml");
        YamsCsResource instance = new YamsCsResource(null, null, null);
        instance.setCorpusStructureProvider(corpusStructureProvider);
        final String expectedUrl = "http://test/node";
        final String expectedHdl = "hdl:1234/5678";
        final String expectedName = "test node";
        final URI hdlUri = new URI(expectedHdl);
        final URI nodeUri = new URI(expectedUrl);

        // Underlying archive object to be returned by DAO
        final CorpusNode archiveObject = new CorpusNode() {

            @Override
            public URI getNodeURI() {
                return nodeUri;
            }

            @Override
            public URI getProfile() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FileInfo getFileInfo() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public CorpusNodeType getType() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Date getLastUpdate() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isOnSite() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getFormat() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getName() {
                return expectedName;
            }

            @Override
            public URI getPID() {
                return hdlUri;
            }

        };

        context.checking(new Expectations() {
            {
                oneOf(corpusStructureProvider).getNode(hdlUri);
                will(returnValue(archiveObject));
            }
        });
        SerialisableDataNode result = instance.getDataNode(expectedHdl);
        assertEquals(expectedName, result.getLabel());
        assertEquals(expectedName, result.getLabel());
        assertEquals(expectedHdl, result.getArchiveHandle());
    }
}