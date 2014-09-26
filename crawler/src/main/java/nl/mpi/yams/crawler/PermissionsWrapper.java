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
package nl.mpi.yams.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import nl.mpi.flap.model.DataNodePermissions;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.PluginDataNodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since Aug 13, 2014 1:42:04 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class PermissionsWrapper {

    private final static Logger logger = LoggerFactory.getLogger(PermissionsWrapper.class);
    final String archiveHandle;
    final private JsonNode jsonNode;

    public PermissionsWrapper(String permissionsServiceUri, String archiveHandle) {
        this.archiveHandle = archiveHandle;
        JsonNode jsonNodeInner = null;
        try {
//            URL url = new URL("https://lux17.mpi.nl/lat/yams-cs-connector/rest?id=" + archiveHandle);
            URL url = new URL(permissionsServiceUri + archiveHandle);
            ObjectMapper mapper = new ObjectMapper();
            jsonNodeInner = mapper.readTree(url);
        } catch (FileNotFoundException ex) {
            logger.warn("File not found: {}", ex.getMessage());
        } catch (IOException exception) {
            logger.error(archiveHandle, exception);
        } catch (IllegalArgumentException exception) {
            logger.error(archiveHandle, exception);
        }
        jsonNode = jsonNodeInner;
    }

    public String getLabel() {
        if (jsonNode != null) {
            return jsonNode.get("Label").getTextValue();
        } else {
            return "";
        }
    }

    public DataNodeType getType() {
        final DataNodeType dataNodeType = new DataNodeType();
        if (jsonNode != null) {
            try {
                dataNodeType.setID(jsonNode.get("Type").get("ID").getTextValue());
                dataNodeType.setLabel(jsonNode.get("Type").get("Label").getTextValue());
                dataNodeType.setMimeType(jsonNode.get("Type").get("MimeType").getTextValue());
                dataNodeType.setFormat(PluginDataNodeType.FormatType.valueOf(jsonNode.get("Type").get("Format").getTextValue()));
            } catch (IllegalArgumentException exception) {
                logger.info("Invalid FormatType", exception);
            }
        }
        return dataNodeType;
    }

    public DataNodePermissions getDataNodePermissions() {
        final DataNodePermissions dataNodePermissions = new DataNodePermissions();
        if (jsonNode != null) {
            dataNodePermissions.setLabel(jsonNode.get("Permissions").get("Label").getTextValue());
            final JsonNode accessLevel = jsonNode.get("Permissions").get("AccessLevel");
            if (accessLevel != null) {
                try {
                    dataNodePermissions.setAccessLevel(DataNodePermissions.AccessLevel.valueOf(accessLevel.getTextValue()));
                } catch (IllegalArgumentException exception) {
                    logger.info("Invalid AccessLevel", exception);
                }
            }
        }
        return dataNodePermissions;
    }

    public static void main(String[] args) {
        final DataNodePermissions dataNodePermissions = new PermissionsWrapper("https://lux16.mpi.nl/ds/yams-cs-connector/rest?id=", "hdl:11142/00-D01D1D74-9EF2-42E2-BF9B-33CF9300A343").getDataNodePermissions();
        System.out.println(dataNodePermissions.getLabel());
        System.out.println(dataNodePermissions.getAccessLevel());
        final DataNodePermissions dataNodePermissions1 = new PermissionsWrapper("https://lux16.mpi.nl/ds/yams-cs-connector/rest?id=", "hdl:11142/00-D01D1D74-9EF2-42E2-BF9B-").getDataNodePermissions();
        System.out.println(dataNodePermissions1.getLabel());
        System.out.println(dataNodePermissions1.getAccessLevel());
    }
}
