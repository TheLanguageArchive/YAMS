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
import nl.mpi.flap.model.DataField;
import static nl.mpi.flap.model.DataNodePermissions.AccessLevel.open_everybody;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.SerialisableDataNode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class HighlightableDataNodeTest {

    /**
     * Test of serializing the AbstractDataNode with types.
     *
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void testSerializeDataNodeAndTypesForJaxB() throws JAXBException {

        JAXBContext jaxbContext3 = JAXBContext.newInstance(DataField.class);
        Unmarshaller unmarshaller3 = jaxbContext3.createUnmarshaller();

        JAXBContext jaxbContext1 = JAXBContext.newInstance(FieldGroup.class);
        Unmarshaller unmarshaller1 = jaxbContext1.createUnmarshaller();

        JAXBContext jaxbContext4 = JAXBContext.newInstance(DataNodeType.class);
        Unmarshaller unmarshaller4 = jaxbContext4.createUnmarshaller();

        JAXBContext jaxbContext5 = JAXBContext.newInstance(SerialisableDataNode.class);
        Unmarshaller unmarshaller5 = jaxbContext5.createUnmarshaller();

        JAXBContext jaxbContext2 = JAXBContext.newInstance(HighlightableDataNode.class);
        Unmarshaller unmarshaller2 = jaxbContext2.createUnmarshaller();

        JAXBContext jaxbContext = JAXBContext.newInstance(HighlightableDataNode.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        String dataXmlString = "<DataNode Label=\"Test Node\" ID=\"Test Group\">"
                + "<Type Label=\"a label\" MimeType=\"a test type\" ID=\"a test ID\" Format=\"cmdi\"/>"
                + "<Permissions Label=\"a permission\" AccessLevel=\"open_everybody\"/>"
                + "<DataNode Label=\"Child Node\" ID=\"Test Child\"/>"
                + "</DataNode>";
        System.out.println("dataXmlString: " + dataXmlString);
        HighlightableDataNode dataNode = (HighlightableDataNode) unmarshaller.unmarshal(new StreamSource(new StringReader(dataXmlString)), HighlightableDataNode.class).getValue();
        StringWriter stringWriter = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(dataNode, stringWriter);
        System.out.println("Marshaller Output:\n" + stringWriter.toString());
        assertEquals(dataNode.getType().getLabel(), "a label");
        assertEquals(dataNode.getType().getMimeType(), "a test type");
        assertEquals(dataNode.getType().getID(), "a test ID");
        assertEquals(dataNode.getPermissions().getLabel(), "a permission");
        assertEquals(dataNode.getPermissions().getAccessLevel(), open_everybody);
        assertEquals(dataNode.getType().getFormat(), DataNodeType.FormatType.cmdi);
    }
}
