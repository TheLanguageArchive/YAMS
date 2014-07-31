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
package nl.mpi.yams.client;

/**
 * @since Apr 15, 2014 3:31:49 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public interface ServiceLocations extends Messages {

    @Key("nl.mpi.yams.rrsUrl")
    String rrsUrl();

    @Key("nl.mpi.yams.amsUrl")
    String amsUrl(String nodeIdentifier);

    @Key("nl.mpi.yams.annexUrl")
    String annexUrl(String nodeIdentifier);

    @Key("nl.mpi.yams.imdiSearchUrl")
    String imdiSearchUrl();

    @Key("nl.mpi.yams.trovaUrl")
    String trovaUrl();

    @Key("nl.mpi.yams.imdiBrowserManualUrl")
    String imdiBrowserManualUrl();

    @Key("nl.mpi.yams.yamsUrl")
    String yamsUrl(String nodeId);

    @Key("nl.mpi.yams.loginUrl")
    String loginUrl();

    @Key("nl.mpi.yams.logoutUrl")
    String logoutUrl();

    @Key("nl.mpi.yams.statusUrl")
    String statusUrl();

    @Key("nl.mpi.yams.jsonCsAdaptorUrl")
    String jsonCsAdaptorUrl();

    @Key("nl.mpi.yams.jsonBasexAdaptorUrl")
    String jsonBasexAdaptorUrl();

    @Key("nl.mpi.yams.jsonRootNodeUrl")
    String jsonRootNodeUrl(String serviceUrl, String databaseName);

    @Key("nl.mpi.yams.jsonMetadataTypesUrl")
    String jsonMetadataTypesUrl(String serviceUrl, String databaseName);

    @Key("nl.mpi.yams.jsonMetadataPathsUrl")
    String jsonMetadataPathsUrl(String serviceUrl, String databaseName, String type);

    @Key("nl.mpi.yams.jsonMetadataValuesUrl")
    String jsonMetadataValuesUrl(String serviceUrl, String databaseName, String type, String path, String text, int max);

    @Key("nl.mpi.yams.jsonDbInfoListUrl")
    String jsonDbInfoListUrl(String serviceUrl);

    @Key("nl.mpi.yams.jsonDbInfoUrl")
    String jsonDbInfoUrl(String serviceUrl, String databaseName);

    @Key("nl.mpi.yams.jsonDbInfoUrl")
    String jsonNodeOfUrl(String serviceUrl, String identifierGetPart);

    @Key("nl.mpi.yams.jsonLinksOfUrl")
    String jsonLinksOfUrl(String serviceUrl, String identifierGetPart);

    @Key("nl.mpi.yams.jsonNodeGetVar")
    String jsonNodeGetVar();

    @Key("nl.mpi.yams.defaultDatabaseName")
    String defaultDatabaseName();
}
