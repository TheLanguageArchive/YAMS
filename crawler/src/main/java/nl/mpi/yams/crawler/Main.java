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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.plugin.PluginException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on : Feb 6, 2013, 2:02:48 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    // initialize some defaults
    private final static int DEFAULT_NUMBER_TO_CRAWL = 90;
    private final static String DEFAULT_DATABASE_URL = "";
    private final static String DEFAULT_DATABASE_NAME = "YAMS-DB";
    private final static String DEFAULT_DATABASE_USER = "admin";
    private final static String DEFAULT_DATABASE_PASSWORD = "admin";
    // thestatic  crawlFilter limits to the domain (string prefix)that can be crawled 
    private final static String DEFAULT_CRAWL_FILTER = "http://lux16.mpi.nl/";
    private final static String DEFAULT_PERMISSIONS_SERVICE_URI = "https://lux16.mpi.nl/ds/yams-cs-connector/rest/node?id=";
    private final static String DEFAULT_START_URL = "http://hdl.handle.net/11142/00-74BB450B-4E5E-4EC7-B043-F444C62DB5C0";

    static public void main(String[] args) {

        // parse command line options
        final Options options = getCommandLineOptions();
        try {
            // parse the command line arguments
            final CommandLine line = new BasicParser().parse(options, args);
            if (processOptions(line, options)) {
                System.exit(0);
            }
        } catch (ParseException exp) {
            System.out.println("Cannot parse the command line input:" + exp.getMessage());
        }
        System.err.println("\nCrawler did not finish as expected. See log  file for details.");
        System.exit(-1);
    }

    private static boolean processOptions(final CommandLine line, final Options options) throws SecurityException, NumberFormatException {
        // configure logging with verbosity depending on the parameters
        configureLogging(line);

        // check for valid actions and show help if none found
        if (!line.hasOption(OPTION_DROP) && !line.hasOption(OPTION_CRAWL) && !line.hasOption(OPTION_APPEND) && !line.hasOption(OPTION_FACETS)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("yams-crawler", options);
            System.exit(-1);
        }

        // set initial values to defaults
        String startUrl = DEFAULT_START_URL;
        int numberToCrawl = DEFAULT_NUMBER_TO_CRAWL;
        String databaseUrl = DEFAULT_DATABASE_URL;
        String databaseName = DEFAULT_DATABASE_NAME;
        String databaseUser = DEFAULT_DATABASE_USER;
        String databasePassword = DEFAULT_DATABASE_PASSWORD;
        String crawlFilter = DEFAULT_CRAWL_FILTER;
        String permissionsServiceUri = DEFAULT_PERMISSIONS_SERVICE_URI;

        boolean crawlOption = line.hasOption(OPTION_CRAWL);
        if (line.hasOption(OPTION_TARGET)) {
            startUrl = line.getOptionValue(OPTION_TARGET);
            crawlOption = true;
        }
        if (line.hasOption(OPTION_NUMBER)) {
            numberToCrawl = Integer.parseInt(line.getOptionValue(OPTION_NUMBER));
        }
        if (line.hasOption(OPTION_SERVER)) {
            databaseUrl = line.getOptionValue(OPTION_SERVER);
        }
        if (line.hasOption(OPTION_DBNAME)) {
            databaseName = line.getOptionValue(OPTION_DBNAME);
        }
        if (line.hasOption(OPTION_DBUSER)) {
            databaseUser = line.getOptionValue(OPTION_DBUSER);
        }
        if (line.hasOption(OPTION_DBPASSWD)) {
            databasePassword = line.getOptionValue(OPTION_DBPASSWD);
        }
        if (line.hasOption(OPTION_LIMIT)) {
            crawlFilter = line.getOptionValue(OPTION_LIMIT);
        }
        if (line.hasOption(OPTION_AMS)) {
            permissionsServiceUri = line.getOptionValue(OPTION_AMS);
        }

        try {
            final RemoteArchiveCrawler archiveCrawler = new RemoteArchiveCrawler(numberToCrawl, crawlFilter, databaseUrl, databaseName, databaseUser, databasePassword, permissionsServiceUri);
            runCrawler(archiveCrawler, crawlOption, startUrl, line);
            return true; // arbil threads might be requiring this to terminate

            // if any point below this line gets reached, the crawler will
            // terminate with an error state (-1)
        } catch (URISyntaxException exception) {
            logger.error("Invalid URI", exception);
            System.err.println(exception.getMessage());
        } catch (QueryException exception) {
            logger.error("Database error", exception);
            System.err.println(exception.getMessage());
        } catch (PluginException exception) {
            logger.error("Error inserting icons", exception);
            System.err.println(exception.getMessage());
        }
        return false;
    }

    private static void runCrawler(RemoteArchiveCrawler archiveCrawler, boolean doCrawl, String startUrl, final CommandLine cmdLineOptions) throws URISyntaxException, PluginException, QueryException {
        if (cmdLineOptions.hasOption(OPTION_DROP)) {
            System.out.println("Dropping and crawing from scratch");
            archiveCrawler.dropAllRecords();
            //drop implies crawl
            doCrawl = true;
        } else {
            // make sure the db exists
            archiveCrawler.checkDbExists();
        }

        if (doCrawl) {
            final URI startURI = new URI(startUrl);
            System.out.println("Crawling the start URL: " + startURI);
            archiveCrawler.crawl(startURI);
        }

        if (cmdLineOptions.hasOption(OPTION_APPEND)) {
            System.out.println("Looking for and appending missing documents");
            archiveCrawler.updateFast();
        }

        archiveCrawler.insertKnowIcons();

        if (cmdLineOptions.hasOption(OPTION_FACETS)) {
            archiveCrawler.clearAndCalculateDbStats();
            archiveCrawler.preloadFacets();
        }
        logger.info("Done");
    }

    private static void configureLogging(CommandLine line) throws SecurityException {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));

            java.util.logging.Logger mpiLogger = java.util.logging.Logger.getLogger("nl.mpi");
            if (line.hasOption(OPTION_DEBUG)) {
                java.util.logging.Logger.getGlobal().setLevel(Level.FINEST);
                mpiLogger.setLevel(Level.FINEST);

                // also log to console
                final ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.FINEST);
                mpiLogger.addHandler(consoleHandler);
            } else {
                // use configured handler(s), only log info level
                java.util.logging.Logger.getGlobal().setLevel(Level.INFO);
                mpiLogger.setLevel(Level.INFO);
            }

            logger.info("YAMS crawler running");
            logger.debug("Outputting debug information");
        } catch (IOException ex) {
            System.err.println("Could not configure logging:" + ex.getMessage());
        }
    }

    private static final String OPTION_DEBUG = "x";
    private static final String OPTION_AMS = "ams";
    private static final String OPTION_LIMIT = "l";
    private static final String OPTION_DBPASSWD = "p";
    private static final String OPTION_DBUSER = "u";
    private static final String OPTION_DBNAME = "db";
    private static final String OPTION_SERVER = "s";
    private static final String OPTION_TARGET = "t";
    private static final String OPTION_NUMBER = "n";
    private static final String OPTION_APPEND = "a";
    private static final String OPTION_CRAWL = "c";
    private static final String OPTION_FACETS = "f";
    private static final String OPTION_DROP = "d";

    private static Options getCommandLineOptions() {
        // create the command line parser
        // create the Options
        Options options = new Options();
        options.addOption(OPTION_DROP, "drop", false, "Drop the existing data and recrawl. This option implies the c option.");
        options.addOption(OPTION_FACETS, "facets", false, "Preload the facets from the existing crawled data.");
        options.addOption(OPTION_CRAWL, "crawl", false, "Crawl the provided url or the default url if not otherwise specified.");
        options.addOption(OPTION_APPEND, "append", false, "Restart crawling adding missing documents.");
        options.addOption(OPTION_NUMBER, "number", true, "Number of documents to insert (default: " + DEFAULT_NUMBER_TO_CRAWL + ").");
        options.addOption(OPTION_TARGET, "target", true, "Target URL of the start documents to crawl (default: " + DEFAULT_START_URL + "). This option implies the c option.");
        options.addOption(OPTION_SERVER, "server", true, "Data base server URL or file path (when a file path is provided it is used as the local basex directory via the java bindings rather than the REST interface), default is to use the un mondified local basex directory");
        options.addOption(OPTION_DBNAME, "dbname", true, "Name of the database to use (default: " + DEFAULT_DATABASE_NAME + ").");
        options.addOption(OPTION_DBUSER, "user", true, "Data base user name, (default: " + DEFAULT_DATABASE_USER + ").");
        options.addOption(OPTION_DBPASSWD, "password", true, "Data base password, (default: " + DEFAULT_DATABASE_PASSWORD + ").");
        options.addOption(OPTION_LIMIT, "limit", true, "Limit crawling to URLs which contain the provided string (default: " + DEFAULT_CRAWL_FILTER + ").");
        options.addOption(OPTION_AMS, "amspermissions", true, "REST service URL where permissions information from AMS can be obtained (default: " + DEFAULT_PERMISSIONS_SERVICE_URI + ").");
        options.addOption(OPTION_DEBUG, "debug", false, "Display debug output");
        return options;
    }
}
