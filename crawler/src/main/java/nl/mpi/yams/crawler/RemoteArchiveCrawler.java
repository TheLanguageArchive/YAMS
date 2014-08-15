/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.yams.crawler;

import nl.mpi.yams.common.data.DatabaseLinks;
import nl.mpi.yams.common.data.IconTable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.data.ArbilDataNodeContainer;
import nl.mpi.arbil.data.BlockingDataNodeLoader;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginArbilDataNodeLoader;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yams.common.data.DatabaseStats;
import nl.mpi.yams.common.data.MetadataFileType;
import nl.mpi.yams.common.db.DataBaseManager;
import nl.mpi.yams.common.db.DbAdaptor;
import nl.mpi.yams.common.db.LocalDbAdaptor;
import nl.mpi.yams.common.db.RestDbAdaptor;

/**
 * Created on : Feb 6, 2013, 2:04:40 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class RemoteArchiveCrawler {

    final PluginArbilDataNodeLoader dataNodeLoader;
    final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yamsDatabase;
    final IconTable iconTable;
    final private int numberToInsert;
    private int numberInserted = 0;
    private int totalLoaded = 0;
    public static final String HANDLE_SERVER_URI = "http://hdl.handle.net/";
    private String crawlFilter;

    public RemoteArchiveCrawler(int numberToInsert, String crawlFilter, String databaseUrl, String databaseName, String databaseUser, String databasePassword) throws QueryException {
        System.out.println("numberToInsert:" + numberToInsert);
        System.out.println("numberToInsert:" + databaseUrl);
        System.out.println("numberToInsert:" + databaseName);
        System.out.println("crawlFilter:" + crawlFilter);
        this.numberToInsert = numberToInsert;
        if (databaseName == null || databaseName.length() < 5) {
            throw new QueryException("Database name must be more than 5 letters long.");
        }
        iconTable = new IconTable();

        final String cacheDirectory = System.getProperty("user.dir");
        dataNodeLoader = BlockingDataNodeLoader.getBlockingDataNodeLoader(cacheDirectory);
        try {
            DbAdaptor dbAdaptor;
            if (databaseUrl.startsWith("http://")) {
                dbAdaptor = new RestDbAdaptor(new URL(databaseUrl), databaseUser, databasePassword);
            } else {
                if (!databaseUrl.isEmpty()) {
                    final File databaseDirectory = new File(databaseUrl);
                    new File(databaseDirectory, ".basexhome").createNewFile();
                    Properties props = System.getProperties();
                    props.setProperty("org.basex.path", databaseDirectory.getAbsolutePath());
                }
                dbAdaptor = new LocalDbAdaptor();
            }
//        final DbAdaptor dbAdaptor = new LocalDbAdaptor(new File());
            yamsDatabase = new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, databaseName);
//            yamsDatabase.clearDatabaseStats();
        } catch (MalformedURLException exception) {
            throw new QueryException(exception);
        } catch (IOException exception) {
            throw new QueryException(exception);
        }
    }

    public void clearAndCalculateDbStats() throws QueryException {
        System.out.println("Removing the old database statistics");
        yamsDatabase.clearDatabaseStats();
        System.out.println("Creating the database indexes");
        yamsDatabase.createIndexes();
        System.out.println("Calculating the database statistics");
        final DatabaseStats databaseStats = yamsDatabase.getDatabaseStats();
        System.out.println("KnownDocumentsCount: " + databaseStats.getKnownDocumentsCount());
        System.out.println("MissingDocumentsCount: " + databaseStats.getMisingDocumentsCount());
        System.out.println("RootDocumentsCount: " + databaseStats.getRootDocumentsCount());
    }

    public void insertKnowIcons() throws PluginException, QueryException {
        System.out.println("Inserting the known icons");
        yamsDatabase.insertNodeIconsIntoDatabase(iconTable);
    }

    public void preloadFacets() throws QueryException {
        System.out.println("Preloading facets");
        for (MetadataFileType metadataType : yamsDatabase.getMetadataTypes(null)) {
            System.out.println("File type: " + metadataType.getLabel());
            for (MetadataFileType metadataPath : yamsDatabase.getMetadataPaths(metadataType)) {
                if (metadataPath.getPath() != null || metadataType.getType() != null) {
                    System.out.println("Path type: " + metadataPath.getLabel());
                    // we are no longer getting the node values for each facet and will replace this with granular value requests based on user input and cookie storage
//                    try {
//                        final MetadataFileType[] metadataFieldValues = yamsDatabase.getMetadataFieldValues(metadataPath);
//                        if (metadataFieldValues != null) {
//                            System.out.println("Values: " + metadataFieldValues.length);
//                        } else {
//                            System.out.println("Values: none");
//                        }
//                    } catch (QueryException exception) {
//                        System.out.println("Failed to get metadata field values: " + exception.getMessage());
//                    }
                }
            }
        }
    }

    // todo: preload the faceted tree data
//    public void preloadFacetedTreeData() throws QueryException {
//        System.out.println("Preloading faceted tree data");
//        for (MetadataFileType metadataType : yamsDatabase.getMetadataTypes(null)) {
//            System.out.println("File type: " + metadataType.getLabel());
//            for (MetadataFileType metadataPath : yamsDatabase.getMetadataPaths(metadataType)) {
//                System.out.println("Path type: " + metadataPath.getLabel());
//                final MetadataFileType[] metadataFieldValues = yamsDatabase.getMetadataFieldValues(metadataPath);
//                if (metadataFieldValues != null) {
//                    System.out.println("Values: " + metadataFieldValues.length);
//                } else {
//                    System.out.println("Values: none");
//                }
//            }
//        }
//    }
    public void updateFast() {
        DatabaseLinks databaseLinks = new DatabaseLinks();
        try {
            boolean continueGetting = true;
            while (continueGetting) {
                System.out.println("Links read: " + databaseLinks.getRecentLinks().size());
                System.out.println("Links found: " + databaseLinks.getChildLinks().size());
                final Set<DataNodeLink> handlesOfMissing = yamsDatabase.getHandlesOfMissing(databaseLinks, 1000, crawlFilter);
                if (handlesOfMissing.isEmpty()) {
                    continueGetting = false;
                }
                databaseLinks = new DatabaseLinks();
                for (DataNodeLink dataNodeLink : handlesOfMissing) {
                    if (numberInserted >= numberToInsert) {
                        continueGetting = false;
                        break;
                    }
                    String targetHandle = dataNodeLink.getNodeUriString();
//                    System.out.println("targetHandle: " + targetHandle);
                    ArbilDataNodeContainer nodeContainer = null;
                    ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, new URI(targetHandle));
//                    System.out.println("arbil url: " + dataNode.getUrlString());
                    loadAndInsert(yamsDatabase, dataNode, databaseLinks);
                }
            }
            // store the current state
            yamsDatabase.getHandlesOfMissing(databaseLinks, 0, crawlFilter);
            System.out.println("Update complete");
        } catch (URISyntaxException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (PluginException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (QueryException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (CrawlerException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (ModelException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
    }

    public void update() {
        System.out.println("FindAndInsertMissingNodes");
        try {
            // todo: change this to a loop that gets more missing document URLs in blocks of 100 from the db until the max
//            final IterableResult handlesOfMissing = yamsDatabase.getHandlesOfMissing();
            boolean continueGetting = true;
            StringTokenizer stringTokenizer = null;
            while (continueGetting) {
                if (stringTokenizer == null) {
                    String handlesOfMissing = yamsDatabase.getHandlesOfMissing();
                    System.out.println("Nodes to get length: " + handlesOfMissing.length());
                    stringTokenizer = new StringTokenizer(handlesOfMissing);
                    continueGetting = stringTokenizer.hasMoreTokens();
                }
                try {
                    String targetHandle = stringTokenizer.nextToken(" ");
                    if (numberInserted >= numberToInsert) {
                        continueGetting = false;
                        break;
                    }
                    if (targetHandle == null) {
                        System.out.println("No more missing documents to crawl");
                        break;
                    }
//                    System.out.println("targetHandle: " + targetHandle);
                    URI targetUri = new URI(targetHandle.replace("hdl:", HANDLE_SERVER_URI));
//                    System.out.println("targetUri: " + targetUri);
                    ArbilDataNodeContainer nodeContainer = null; //new ArbilDataNodeContainer() {
                    ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, new URI(targetHandle));
//                    System.out.println("arbil url: " + dataNode.getUrlString());
                    loadAndInsert(yamsDatabase, dataNode, new DatabaseLinks());
                } catch (NoSuchElementException exception) {
                    stringTokenizer = null;
                }
            }
            System.out.println("Update complete");
        } catch (URISyntaxException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (PluginException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (QueryException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (CrawlerException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (ModelException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
    }

    public void checkDbExists() {
        try {
            System.out.println("Checking the database exists");
            yamsDatabase.checkDbExists();
        } catch (QueryException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
    }

    public void dropAllRecords() {
        try {
            System.out.println("Dropping old crawled data");
            yamsDatabase.dropAllRecords(); // this will drop the old data and may drop the database depending on the database module used
        } catch (QueryException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
    }

    public void crawl(URI startURI) {
        System.out.println("crawl");
        DatabaseLinks databaseLinks = new DatabaseLinks();
        try {
            ArbilDataNodeContainer nodeContainer = null;
            ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, startURI);
            // because we add the root link before the node is loaded the archive handle is not known
            databaseLinks.insertRootLink(new DataNodeLink(dataNode.getUrlString(), dataNode.archiveHandle));
            loadAndInsert(yamsDatabase, dataNode, databaseLinks);
            // store the current state
            yamsDatabase.getHandlesOfMissing(databaseLinks, 0, crawlFilter);
            System.out.println("Crawl complete");
        } catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (PluginException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (QueryException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (CrawlerException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        } catch (ModelException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
    }

    private void insertNodeIcons(ArbilDataNode dataNode) {
        final ArbilDataNodeWrapper arbilDataNodeWrapper = new ArbilDataNodeWrapper(dataNode);
        iconTable.addTypeIcon(arbilDataNodeWrapper.getType(), dataNode.getIcon().getImage());
        for (ArbilDataNode childNode : dataNode.getChildArray()) {
            if (childNode.isChildNode()) {
                insertNodeIcons(childNode);
            }
        }
    }

    private void loadAndInsert(DataBaseManager arbilDatabase, ArbilDataNode dataNode, DatabaseLinks databaseLinks) throws InterruptedException, PluginException, QueryException, CrawlerException, ModelException {
//        if (dataNode.getUrlString().startsWith("http://hdl.handle.net/") && dataNode.getUrlString().charAt(21) != '/') {
//            System.out.println("bad link: " + dataNode.getUrlString());
//        } else {
        System.out.print("Loading: " + numberInserted + " URL: " + dataNode.getUrlString() + "                                                           \r");
//        }
        while (dataNode.getLoadingState() != ArbilDataNode.LoadingState.LOADED && dataNode.isMetaDataNode()) {
            dataNode.reloadNode();
            dataNode.waitTillLoaded();
//            Thread.sleep(100); // the issue in arbil seems to now be resolved that required this sleep to be here without which there were regular concurrent modification exceptions in the get fields of arbil data node
        }
        totalLoaded++;
//        loadChildNodes(dataNode);
        if (/*!dataNode.fileNotFound &&*/!dataNode.isChildNode()) {
//            System.out.println("Inserting into the database");
            final ArbilDataNodeWrapper arbilDataNodeWrapper = new ArbilDataNodeWrapper(dataNode);
            insertNodeIcons(dataNode);
            databaseLinks.insertLinks(new DataNodeLink(dataNode.getUrlString(), dataNode.archiveHandle), arbilDataNodeWrapper);
            //            arbilDataNodeWrapper.checkChildNodesLoaded();
            if (arbilDataNodeWrapper.getID() != null && !arbilDataNodeWrapper.getID().isEmpty()) {
                arbilDatabase.insertIntoDatabase(arbilDataNodeWrapper, false);
                numberInserted++;
            } else {
                throw new CrawlerException("No ID found");
            }
        }
    }

    private void loadChildNodes(ArbilDataNode dataNode) throws InterruptedException {
        for (ArbilDataNode childNode : dataNode.getChildArray()) {
            if (childNode.getLoadingState() == ArbilDataNode.LoadingState.UNLOADED) {
                System.out.println("loading child node for: " + numberInserted + ". total loaded: " + totalLoaded);
                System.out.println("Child URL: " + childNode.getUrlString());
                childNode.reloadNodeShallowly();
            }
            while (childNode.getLoadingState() == ArbilDataNode.LoadingState.UNLOADED) {
                Thread.sleep(100);
            }
            System.out.println(childNode.getLoadingState().name());
            totalLoaded++;
        }
    }
}
