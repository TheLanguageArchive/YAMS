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
import nl.mpi.arbil.util.ArbilMimeHashQueue;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.model.DataField;
import nl.mpi.flap.model.SerialisableDataNode;
import nl.mpi.flap.plugin.PluginArbilDataNodeLoader;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.yaas.common.data.MetadataFileType;
import nl.mpi.yaas.common.db.DataBaseManager;

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

    public RemoteArchiveCrawler() throws QueryException {
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new ArbilVersion());
        final ArbilDesktopInjector injector = new ArbilDesktopInjector();
        injector.injectHandlers(versionManager, new ArbilLogConfigurer(versionManager.getApplicationVersion(), "yaas"));

        final ArbilWindowManager arbilWindowManager = injector.getWindowManager();
        final ArbilSessionStorage arbilSessionStorage = new ArbilSessionStorage();
        dataNodeLoader = new ArbilDataNodeLoader(arbilWindowManager, arbilSessionStorage, new ArbilMimeHashQueue(arbilWindowManager, arbilSessionStorage), new ArbilTreeHelper(arbilSessionStorage, arbilWindowManager));

        arbilDatabase = new DataBaseManager<SerialisableDataNode, DataField, MetadataFileType>(SerialisableDataNode.class, DataField.class, MetadataFileType.class, arbilSessionStorage, DataBaseManager.defaultDataBase);
    }

    public void update(int numberToInsert) {
        this.numberToInsert = numberToInsert;
        numberInserted = 0;
        System.out.println("FindAndInsertMissingNodes");
        try {
            for (String targetHandle : arbilDatabase.getHandlesOfMissing()) {
                if (numberInserted >= numberToInsert) {
                    break;
                }
                System.out.println("targetHandle: " + targetHandle);
                URI targetUri = new URI(targetHandle.replace("hdl:", HANDLE_SERVER_URI));
                System.out.println("targetUri: " + targetUri);
                ArbilDataNodeContainer nodeContainer = null; //new ArbilDataNodeContainer() {
                ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, new URI(targetHandle));
                loadAndInsert(arbilDatabase, dataNode);

            }
            System.out.println("Update complete");
            // TODO review the generated test code and remove the default call to fail.
//            fail("The test case is a prototype.");
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
        System.out.println("walkTreeInsertingNodes");
        try {
//            final DataBaseManager dataBaseManager = new DataBaseManager();
            ArbilDataNodeContainer nodeContainer = null; //new ArbilDataNodeContainer() {
//                public void dataNodeRemoved(ArbilNode dataNode) {
////                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                public void dataNodeIconCleared(ArbilNode dataNode) {
//                    if (dataNode.isDataLoaded()) {
//                        try {
//                            instance.insertIntoDatabase(dataNode);
//                        } catch (PluginException exception) {
//                            fail(exception.getMessage());
//                        }
//                    }
//                }
//
//                public void dataNodeChildAdded(ArbilNode destination, ArbilNode newChildNode) {
////                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                public boolean isFullyLoadedNodeRequired() {
//                    return true;
//                }
//            };
            ArbilDataNode dataNode = (ArbilDataNode) dataNodeLoader.getPluginArbilDataNode(nodeContainer, startURI);
            System.out.println("Dropping old DB and creating a new DB");
            arbilDatabase.createDatabase(); // this will drop the old database
            loadChildNodes(arbilDatabase, dataNode);
            System.out.println("Crawl complete");
            // TODO review the generated test code and remove the default call to fail.
//            fail("The test case is a prototype.");
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

    private void loadChildNodes(DataBaseManager arbilDatabase, ArbilDataNode dataNode) throws InterruptedException, PluginException, QueryException, CrawlerException {
        if (numberInserted >= numberToInsert) {
            return;
        }
        loadAndInsert(arbilDatabase, dataNode);
        for (ArbilDataNode childNode : dataNode.getChildArray()) {
            loadChildNodes(arbilDatabase, childNode);
        }
    }

    private void loadAndInsert(DataBaseManager arbilDatabase, ArbilDataNode dataNode) throws InterruptedException, PluginException, QueryException, CrawlerException {
        System.out.println("Loading: " + numberInserted);
        dataNode.reloadNode();
        while (dataNode.getLoadingState() != ArbilDataNode.LoadingState.LOADED) {
            Thread.sleep(100);
        }
        totalLoaded++;
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
        if (!dataNode.fileNotFound && !dataNode.isChildNode()) {
            System.out.println("Inserting into the database");
            System.out.println("URL: " + dataNode.getUrlString());
            final ArbilDataNodeWrapper arbilDataNodeWrapper = new ArbilDataNodeWrapper(dataNode);
            arbilDataNodeWrapper.checkChildNodesLoaded();
            if (arbilDataNodeWrapper.getID() != null && !arbilDataNodeWrapper.getID().isEmpty()) {
                arbilDatabase.insertIntoDatabase(arbilDataNodeWrapper);
                numberInserted++;
            } else {
                throw new CrawlerException("No ID found");
            }
        }
    }
}
