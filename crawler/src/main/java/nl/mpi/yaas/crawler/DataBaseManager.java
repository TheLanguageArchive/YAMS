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
package nl.mpi.yaas.crawler;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginException;

/**
 * Created on : Feb 6, 2013, 2:07:30 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Deprecated
public class DataBaseManager {

    public void insertIntoDatabase(SerialisableDataNode dataNode, Class fieldClass) throws PluginException {
        // use JAXB to serialise and insert the data node into the database
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SerialisableDataNode.class, DataField.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(dataNode, stringWriter);
            System.out.println("stringWriter:\n" + stringWriter.toString());
        } catch (JAXBException exception) {
            System.err.println("jaxb error:" + exception.getMessage());
            throw new PluginException(exception.getMessage());
        }
    }
}
