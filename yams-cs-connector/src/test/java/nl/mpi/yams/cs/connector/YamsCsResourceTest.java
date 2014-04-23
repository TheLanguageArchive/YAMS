/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.cs.connector;

import nl.mpi.archiving.corpusstructure.core.database.dao.ArchiveObjectDao;
import nl.mpi.archiving.corpusstructure.core.database.dao.ArchivePropertyDao;
import nl.mpi.archiving.corpusstructure.core.database.dao.CorpusStructureDao;
import nl.mpi.archiving.corpusstructure.core.database.pojo.ArchiveObject;
import nl.mpi.archiving.corpusstructure.provider.db.CorpusStructureProviderImpl;
import nl.mpi.archiving.corpusstructure.provider.db.service.VPathService;
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
    private ArchivePropertyDao archiveDao;
    private ArchiveObjectDao aoDao;
    private CorpusStructureDao csDao;
    private VPathService vPathService;
    private CorpusStructureProviderImpl corpusStructureProviderImpl;

    @Before
    public void setUp() {
        archiveDao = context.mock(ArchivePropertyDao.class);
        aoDao = context.mock(ArchiveObjectDao.class);
        csDao = context.mock(CorpusStructureDao.class);
        vPathService = context.mock(VPathService.class);
        corpusStructureProviderImpl = new CorpusStructureProviderImpl(archiveDao, aoDao, csDao);
        corpusStructureProviderImpl.initializeWithVPathService(vPathService);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getXml method, of class YamsCsResource.
     */
    @Test
    public void testGetXml() {
        System.out.println("getXml");
        YamsCsResource instance = new YamsCsResource(aoDao, archiveDao, csDao);
        final String expectedUrl = "http://test/node";
        final String expectedHdl = "hdl:1234/5678";
        final String expectedName = "test node";

        // Underlying archive object to be returned by DAO
        final ArchiveObject archiveObject = new ArchiveObject();
        archiveObject.setUri(expectedUrl);
        archiveObject.setPid(expectedHdl);
        archiveObject.setName(expectedName);

        context.checking(new Expectations() {
            {
                oneOf(aoDao).select(expectedHdl);
                will(returnValue(archiveObject));
            }
        });
        SerialisableDataNode result = instance.getDataNode(expectedHdl);
        assertEquals(expectedName, result.getLabel());
        assertEquals(expectedName, result.getLabel());
        assertEquals(expectedHdl, result.getArchiveHandle());
    }
}
