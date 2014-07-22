/*
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
package nl.mpi.yams.common.data;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class DatabaseListTest {

    /**
     * Test of getDatabaseList method, of class DatabaseList.
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void testGetDatabaseListForJaxB() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(DatabaseList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        String dataXmlString = "<DatabaseList>\n"
                + "  <DatabaseInfo>\n"
                + "    <DatabaseName>unit-test-database</DatabaseName>\n"
                + "  </DatabaseInfo>\n"
                + "  <DatabaseInfo>\n"
                + "    <DatabaseName>YAMS-DB</DatabaseName>\n"
                + "    <DatabaseStats>\n"
                + "      <KnownDocuments>1361</KnownDocuments>\n"
                + "      <MissingDocuments>392</MissingDocuments>\n"
                + "      <RootDocuments>1</RootDocuments>\n"
                + "      <Cached>true</Cached>\n"
                + "      <RootDocumentID>c1631a46f14d962e7a1b3f90a16dc19e</RootDocumentID>\n"
                + "    </DatabaseStats>\n"
                + "  </DatabaseInfo>\n"
                + "</DatabaseList>";
        System.out.println("dataXmlString: " + dataXmlString);
        DatabaseList databaseList = unmarshaller.unmarshal(new StreamSource(new StringReader(dataXmlString)), DatabaseList.class).getValue();
        StringWriter stringWriter = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(databaseList, stringWriter);
        System.out.println("Marshaller Output:\n" + stringWriter.toString());
        assertEquals(2, databaseList.getDatabaseInfos().size());
        assertEquals("unit-test-database", databaseList.getDatabaseInfos().get(0).getDatabaseName());
        assertEquals("YAMS-DB", databaseList.getDatabaseInfos().get(1).getDatabaseName());
        assertEquals(1361, databaseList.getDatabaseInfos().get(1).getDatabaseStats().getKnownDocumentsCount());
        assertEquals(392, databaseList.getDatabaseInfos().get(1).getDatabaseStats().getMisingDocumentsCount());
        assertEquals(1, databaseList.getDatabaseInfos().get(1).getDatabaseStats().getRootDocumentsCount());
        assertEquals("c1631a46f14d962e7a1b3f90a16dc19e", databaseList.getDatabaseInfos().get(1).getDatabaseStats().getRootDocumentsIDs()[0].getIdString());
    }
}
