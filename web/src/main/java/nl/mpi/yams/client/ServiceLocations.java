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

    @Key("nl.mpi.rrsUrl")
    String rrsUrl();

    @Key("nl.mpi.amsUrl")
    String amsUrl(String nodeIdentifier);

    @Key("nl.mpi.annexUrl")
    String annexUrl(String nodeIdentifier);

    @Key("nl.mpi.imdiSearchUrl")
    String imdiSearchUrl();

    @Key("nl.mpi.trovaUrl")
    String trovaUrl();

    @Key("nl.mpi.imdiBrowserManualUrl")
    String imdiBrowserManualUrl();

    @Key("nl.mpi.yamsUrl")
    String yamsUrl(String nodeId);

    @Key("nl.mpi.loginUrl")
    String loginUrl();

    @Key("nl.mpi.logoutUrl")
    String logoutUrl();

    @Key("nl.mpi.statusUrl")
    String statusUrl();

    @Key("nl.mpi.jsonCsAdaptorUrl")
    String jsonCsAdaptorUrl();

    @Key("nl.mpi.jsonYamsRestUrl")
    String jsonYamsRestUrl(String databaseName);

    @Key("nl.mpi.jsonRootNode")
    String jsonRootNode(String serviceUrl);

    @Key("nl.mpi.jsonNodeOfUrl")
    String jsonNodeOfUrl(String serviceUrl);

    @Key("nl.mpi.jsonLinksOfUrl")
    String jsonLinksOfUrl(String serviceUrl, String nodeIdentifier);

    @Key("nl.mpi.jsonNodeGetVar")
    String jsonNodeGetVar();
}
