/*
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
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
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginArbilDataNodeLoader;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.DatabaseStats;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.db.DataBaseManager;
import nl.mpi.yaas.common.db.DataBaseManager.IterableResult;
import nl.mpi.yaas.common.db.DbAdaptor;
import nl.mpi.yaas.common.db.LocalDbAdaptor;

/**
 * Created on : Feb 6, 2013, 2:04:40 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class RemoteArchiveCrawler {

    final PluginArbilDataNodeLoader dataNodeLoader;
    final DataBaseManager<SerialisableDataNode, DataField, MetadataFileType> arbilDatabase;
    private int numberToInsert = 0;
    private int numberInserted = 0;
    private int totalLoaded = 0;
    public static final String HANDLE_SERVER_URI = "http://hdl.handle.net/";

    public enum DbType {

        TestDB,
        StandardDB
    }

    public RemoteArchiveCrawler(DbType dbType) throws QueryException {
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new ArbilVersion());
        final ArbilDesktopInjector injector = new ArbilDesktopInjector();
        injector.injectHandlers(versionManager, new ArbilLogConfigurer(versionManager.getApplicationVersion(), "yaas"));

        final ArbilWindowManager arbilWindowManager = injector.getWindowManager();
        final ArbilSessionStorage arbilSessionStorage = new ArbilSessionStorage();
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
        final DbAdaptor dbAdaptor = new LocalDbAdaptor(new File(System.getProperty("user.dir"), "yaas-data"));
        arbilDatabase = new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, dbAdaptor, dataBaseName);
        arbilDatabase.clearDatabaseStats();
    }

    private void clearAndCalculateDbStats() throws QueryException {
        System.out.println("Calculating the database statistics");
        arbilDatabase.clearDatabaseStats();
        final DatabaseStats databaseStats = arbilDatabase.getDatabaseStats();
        System.out.println("KnownDocumentsCount: " + databaseStats.getKnownDocumentsCount());
        System.out.println("MissingDocumentsCount: " + databaseStats.getMisingDocumentsCount());
        System.out.println("RootDocumentsCount: " + databaseStats.getRootDocumentsCount());
    }

    public void update(int numberToInsert) {
        this.numberToInsert = numberToInsert;
        numberInserted = 0;
        System.out.println("FindAndInsertMissingNodes");
        try {
            // todo: change this to a loop that gets more missing document URLs in blocks of 100 from the db until the max
            final IterableResult handlesOfMissing = arbilDatabase.getHandlesOfMissing();
            while (numberInserted < numberToInsert) {
//            for (String targetHandle = arbilDatabase.getFirstHandlesOfMissing(); !targetHandle.isEmpty();) {
                String targetHandle = handlesOfMissing.getNext();
                if (targetHandle == null) {
                    System.out.println("No more missing documents to crawl");
                    break;
                }
                System.out.println("targetHandle: " + targetHandle);
                URI targetUri = new URI(targetHandle.replace("hdl:", HANDLE_SERVER_URI));
                System.out.println("targetUri: " + targetUri);
                ArbilDataNodeContainer nodeContainer = null; //new ArbilDataNodeContainer() {
                ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, new URI(targetHandle));
                System.out.println("arbil url: " + dataNode.getUrlString());
                loadAndInsert(arbilDatabase, dataNode);

            }
            System.out.println("Update complete");
            clearAndCalculateDbStats();
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
        }
    }

    public void crawl(URI startURI, int numberToInsert) {
        this.numberToInsert = numberToInsert;
        numberInserted = 0;
        System.out.println("crawl");
        try {
            ArbilDataNodeContainer nodeContainer = null;
            ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, startURI);
            System.out.println("Dropping old DB and creating a new DB");
            arbilDatabase.dropAndRecreateDb(); // this will drop the old database
            loadAndInsert(arbilDatabase, dataNode);
            System.out.println("Crawl complete");
            clearAndCalculateDbStats();
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
        }
    }

    private void loadAndInsert(DataBaseManager arbilDatabase, ArbilDataNode dataNode) throws InterruptedException, PluginException, QueryException, CrawlerException {
        System.out.println("Loading: " + numberInserted);
        while (dataNode.getLoadingState() != ArbilDataNode.LoadingState.LOADED) {
            dataNode.reloadNode();
            dataNode.waitTillLoaded();
            Thread.sleep(100); // todo: when this sleep is not here there are regular concurrent modification exceptions in the get fields of arbil data node
        }
        totalLoaded++;
//        loadChildNodes(dataNode);
        if (!dataNode.fileNotFound && !dataNode.isChildNode()) {
            System.out.println("Inserting into the database");
            System.out.println("URL: " + dataNode.getUrlString());
            final ArbilDataNodeWrapper arbilDataNodeWrapper = new ArbilDataNodeWrapper(dataNode);
            //            arbilDataNodeWrapper.checkChildNodesLoaded();
            if (arbilDataNodeWrapper.getID() != null && !arbilDataNodeWrapper.getID().isEmpty()) {
                arbilDatabase.insertIntoDatabase(arbilDataNodeWrapper, true);
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
