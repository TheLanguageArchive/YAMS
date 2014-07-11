/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.yams.cs.connector;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.mpi.archiving.corpusstructure.core.CorpusNode;
import nl.mpi.archiving.corpusstructure.core.CorpusNodeType;
import nl.mpi.archiving.corpusstructure.core.FileInfo;
import nl.mpi.archiving.corpusstructure.provider.AccessInfoProvider;
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
    private CorpusStructureProvider corpusStructureProvider;
    private AccessInfoProvider accessInfoProvider;
    private HttpServletRequest request;

    @Before
    public void setUp() {    
        corpusStructureProvider = context.mock(CorpusStructureProvider.class);
        accessInfoProvider = context.mock(AccessInfoProvider.class);
        request = context.mock(HttpServletRequest.class);
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
        YamsCsResource instance = new YamsCsResource();
        instance.setCorpusStructureProvider(corpusStructureProvider);
        instance.setAccessInfoProvider(accessInfoProvider);
        final String expectedUrl = "http://test/node";
        final String expectedHdl = "hdl:1234/5678";
        final String expectedName = "test node";
        final String userName = "someone";
        final URI hdlUri = new URI(expectedHdl);
        final URI nodeUri = new URI(expectedUrl);

        // Underlying archive object to be returned by DAO
        final CorpusNode corpusNode = new CorpusNode() {

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
        final List<CorpusNode> corpusNodes = new ArrayList<CorpusNode>();
        corpusNodes.add(corpusNode);
        context.checking(new Expectations() {
            {
                oneOf(corpusStructureProvider).getChildNodes(hdlUri);
                will(returnValue(corpusNodes));
                oneOf(request).getRemoteUser();
                will(returnValue(userName));
            }
        });

        List<SerialisableDataNode> result = (List<SerialisableDataNode>) instance.getChildDataNodes(request, expectedHdl, 0, 1).getEntity();
        assertEquals(expectedName, result.get(0).getLabel());
        assertEquals(expectedName, result.get(0).getLabel());
        assertEquals(expectedHdl, result.get(0).getArchiveHandle());
    }
}
