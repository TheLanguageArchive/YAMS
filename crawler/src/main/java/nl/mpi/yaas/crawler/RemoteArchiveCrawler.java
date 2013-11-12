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
package nl.mpi.yaas.crawler;

import nl.mpi.yaas.common.data.DatabaseLinks;
import nl.mpi.yaas.common.data.IconTable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import nl.mpi.arbil.ArbilDesktopInjector;
import nl.mpi.arbil.ArbilVersion;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.data.ArbilDataNodeContainer;
import nl.mpi.arbil.data.ArbilDataNodeLoader;
import nl.mpi.arbil.data.ArbilTreeHelper;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.userstorage.ArbilSessionStorage;
import nl.mpi.arbil.util.ApplicationVersionManager;
import nl.mpi.arbil.util.ArbilLogConfigurer;
import nl.mpi.arbil.util.MimeHashQueue;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.DataNodeLink;
import nl.mpi.flap.model.ModelException;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginArbilDataNodeLoader;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.db.DataBaseManager;
import nl.mpi.yaas.common.db.DbAdaptor;
import nl.mpi.yaas.common.db.LocalDbAdaptor;
import nl.mpi.yaas.common.db.RestDbAdaptor;

/**
 * Created on : Feb 6, 2013, 2:04:40 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class RemoteArchiveCrawler {

    final PluginArbilDataNodeLoader dataNodeLoader;
    final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> yaasDatabase;
    final IconTable iconTable;
    private int numberToInsert;
    private int numberInserted = 0;
    private int totalLoaded = 0;
    public static final String HANDLE_SERVER_URI = "http://hdl.handle.net/";

    public enum DbType {

        TestDB,
        StandardDB
    }

    public RemoteArchiveCrawler(DbType dbType, int numberToInsert, String databaseUrl, String databaseUser, String databasePassword) throws QueryException {
        this.numberToInsert = numberToInsert;
        iconTable = new IconTable();
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new ArbilVersion());
        final ArbilDesktopInjector injector = new ArbilDesktopInjector();
        final ArbilSessionStorage arbilSessionStorage = new ArbilSessionStorage();
        injector.injectHandlers(arbilSessionStorage, versionManager, new ArbilLogConfigurer(versionManager.getApplicationVersion(), "yaas"));

        final ArbilWindowManager arbilWindowManager = injector.getWindowManager();
        final MimeHashQueue mockMimeHashQueue = new MimeHashQueue() {
            public void addToQueue(ArbilDataNode dataNode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void forceInQueue(ArbilDataNode dataNode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean isCheckResourcePermissions() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void setCheckResourcePermissions(boolean checkResourcePermissions) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void startMimeHashQueueThread() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void stopMimeHashQueueThread() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public String[] getMimeType(URI fileUri) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void terminateQueue() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        dataNodeLoader = new ArbilDataNodeLoader(arbilWindowManager, arbilSessionStorage, mockMimeHashQueue, new ArbilTreeHelper(arbilSessionStorage, arbilWindowManager));
        String dataBaseName;
        switch (dbType) {
            case StandardDB:
                dataBaseName = DataBaseManager.defaultDataBase;
                break;
            default:
                dataBaseName = DataBaseManager.testDataBase;
                break;
        }
        try {
            DbAdaptor dbAdaptor;
            if (databaseUrl.startsWith("http://")) {
                dbAdaptor = new RestDbAdaptor(new URL(databaseUrl), databaseUser, databasePassword);
            } else {
                dbAdaptor = new LocalDbAdaptor(new File(databaseUrl));
            }
//        final DbAdaptor dbAdaptor = new LocalDbAdaptor(new File());
            yaasDatabase = new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, dataBaseName);
//            yaasDatabase.clearDatabaseStats();
        } catch (MalformedURLException exception) {
            throw new QueryException(exception);
        }
    }

    public void clearAndCalculateDbStats() throws QueryException {
        System.out.println("Removing the old database statistics");
        yaasDatabase.clearDatabaseStats();
        System.out.println("Creating the database indexes");
        yaasDatabase.createIndexes();
        System.out.println("Calculating the database statistics");
        final DatabaseStats databaseStats = yaasDatabase.getDatabaseStats();
        System.out.println("KnownDocumentsCount: " + databaseStats.getKnownDocumentsCount());
        System.out.println("MissingDocumentsCount: " + databaseStats.getMisingDocumentsCount());
        System.out.println("RootDocumentsCount: " + databaseStats.getRootDocumentsCount());
    }

    public void insertKnowIcons() throws PluginException, QueryException {
        System.out.println("Inserting the known icons");
        yaasDatabase.insertNodeIconsIntoDatabase(iconTable);
    }

    public void preloadFacets() throws QueryException {
        System.out.println("Preloading facets");
        for (MetadataFileType metadataType : yaasDatabase.getMetadataTypes(null)) {
            System.out.println("File type: " + metadataType.getLabel());
            for (MetadataFileType metadataPath : yaasDatabase.getMetadataPaths(metadataType)) {
                if (metadataPath.getPath() != null || metadataType.getType() != null) {
                    System.out.println("Path type: " + metadataPath.getLabel());
                    // we are no longer getting the node values for each facet and will replace this with granular value requests based on user input and cookie storage
//                    try {
//                        final MetadataFileType[] metadataFieldValues = yaasDatabase.getMetadataFieldValues(metadataPath);
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
//        for (MetadataFileType metadataType : yaasDatabase.getMetadataTypes(null)) {
//            System.out.println("File type: " + metadataType.getLabel());
//            for (MetadataFileType metadataPath : yaasDatabase.getMetadataPaths(metadataType)) {
//                System.out.println("Path type: " + metadataPath.getLabel());
//                final MetadataFileType[] metadataFieldValues = yaasDatabase.getMetadataFieldValues(metadataPath);
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
                System.out.println("Links read: " + databaseLinks.getRecentLinks());
                System.out.println("Links found: " + databaseLinks.getChildLinks());
                final Set<DataNodeLink> handlesOfMissing = yaasDatabase.getHandlesOfMissing(databaseLinks, 1000);
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
                    loadAndInsert(yaasDatabase, dataNode, databaseLinks);
                }
            }
            // store the current state
            yaasDatabase.getHandlesOfMissing(databaseLinks, 0);
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
//            final IterableResult handlesOfMissing = yaasDatabase.getHandlesOfMissing();
            boolean continueGetting = true;
            StringTokenizer stringTokenizer = null;
            while (continueGetting) {
                if (stringTokenizer == null) {
                    String handlesOfMissing = yaasDatabase.getHandlesOfMissing();
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
                    loadAndInsert(yaasDatabase, dataNode, new DatabaseLinks());
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
            yaasDatabase.checkDbExists();
        } catch (QueryException exception) {
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
    }

    public void dropAllRecords() {
        try {
            System.out.println("Dropping old crawled data");
            yaasDatabase.dropAllRecords(); // this will drop the old data and may drop the database depending on the database module used
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
            databaseLinks.insertRootLink(new DataNodeLink(startURI.toString(), null));
            ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, startURI);
            loadAndInsert(yaasDatabase, dataNode, databaseLinks);
            // store the current state
            yaasDatabase.getHandlesOfMissing(databaseLinks, 0);
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
        System.out.print("Loading: " + numberInserted + " URL: " + dataNode.getUrlString() + "                                                           \r");
        while (dataNode.getLoadingState() != ArbilDataNode.LoadingState.LOADED) {
            dataNode.reloadNode();
            dataNode.waitTillLoaded();
//            Thread.sleep(100); // the issue in arbil seems to now be resolved that required this sleep to be here without which there were regular concurrent modification exceptions in the get fields of arbil data node
        }
        totalLoaded++;
//        loadChildNodes(dataNode);
        if (!dataNode.fileNotFound && !dataNode.isChildNode()) {
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
