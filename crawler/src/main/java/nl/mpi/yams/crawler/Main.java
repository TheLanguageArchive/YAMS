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

import java.net.URI;
import java.net.URISyntaxException;
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import nl.mpi.flap.plugin.PluginException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Created on : Feb 6, 2013, 2:02:48 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class Main {

    static public void main(String[] args) {
//        if (System.getProperty("java.util.logging.config.file") == null) {
//            try {
//                LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging-initial.properties"));
//            } catch (IOException exception) {
//                System.out.println("Could not configure initial logging");
//                System.out.println(exception.getMessage());
//                System.exit(-1);
//            } catch (SecurityException exception) {
//                System.out.println("Could not configure initial logging");
//                System.out.println(exception.getMessage());
//                System.exit(-1);
//            }
//        }
        int defaultNumberToCrawl = 90;

//        http://lux16.mpi.nl/cmdi_test/new_leslla/Discourse/Moroccan.cmdi
//        http://childes.talkbank.org/data-cmdi/childes.cmdi
//        http://talkbank.org/data-cmdi/talkbank.cmdi
//        String databaseUrl = "http://lux16.mpi.nl:8984/rest/";
//        String databaseUrl = "http://192.168.56.101:8080/BaseX76/rest/";
//        String databaseUrl = "http://localhost:8984/rest/";
        String databaseUrl = "";
        String databaseName = "YAMS-DB";
        String databaseUser = "admin";
        String databasePassword = "admin";
        // the crawlFilter limits to the domain (string prefix)that can be crawled 
        String crawlFilter = "http://lux16.mpi.nl/";
//        String defaultStartUrl = "http://corpus1.mpi.nl/ds/TranslationService/translate?in=1839/00-0000-0000-0001-53A5-2&outFormat=cmdi";
//        String defaultStartUrl = "http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/cgn.imdi";
//        String defaultStartUrl = "http://lux16.mpi.nl/cmdi_test/leslla/Discourse/Turkish/Ozlem/Cycle1/d_t_o_1.cmdi";
//            startUrl = "http://lux16.mpi.nl/cmdi_test/leslla/Discourse/Turkish/Ozlem/Cycle1/d_t_o_1.cmdi";
        String defaultStartUrl = "http://hdl.handle.net/11142/00-74BB450B-4E5E-4EC7-B043-F444C62DB5C0";
        // create the command line parser
        CommandLineParser parser = new BasicParser(); //DefaultParser();
        // create the Options
        Options options = new Options();
        options.addOption("d", "drop", false, "Drop the existing data and recrawl. This option implies the c option.");
        options.addOption("f", "facets", false, "Preload the facets from the existing crawled data.");
        options.addOption("c", "crawl", false, "Crawl the provided url or the default url if not otherwise specified.");
        options.addOption("a", "append", false, "Restart crawling adding missing documents.");
        options.addOption("n", "number", true, "Number of documents to insert (default: " + defaultNumberToCrawl + ").");
        options.addOption("t", "target", true, "Target URL of the start documents to crawl (default: " + defaultStartUrl + "). This option implies the c option.");
        options.addOption("s", "server", true, "Data base server URL or file path (when a file path is provided it is used as the local basex directory via the java bindings rather than the REST interface), default is to use the un mondified local basex directory");
        options.addOption("d", "dbname", true, "Name of the database to use (default: " + databaseName + ").");
        options.addOption("u", "user", true, "Data base user name, (default: " + databaseUser + ").");
        options.addOption("p", "password", true, "Data base password, (default: " + databasePassword + ").");
        options.addOption("l", "limit", true, "Limit crawling to URLs which contain the provided string (default: " + crawlFilter + ").");
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            // check for valid actions and show help if none found
            if (!line.hasOption("d") && !line.hasOption("c") && !line.hasOption("a") && !line.hasOption("f")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("yams-crawler", options);
                System.exit(-1);
            }

            String startUrl = defaultStartUrl;
            int numberToCrawl = defaultNumberToCrawl;
            boolean crawlOption = line.hasOption("c");
            if (line.hasOption("t")) {
                startUrl = line.getOptionValue("t");
                crawlOption = true;
            }
            if (line.hasOption("n")) {
                numberToCrawl = Integer.parseInt(line.getOptionValue("n"));
            }
            if (line.hasOption("s")) {
                databaseUrl = line.getOptionValue("s");
            }
            if (line.hasOption("d")) {
                databaseName = line.getOptionValue("d");
            }
            if (line.hasOption("u")) {
                databaseUser = line.getOptionValue("u");
            }
            if (line.hasOption("p")) {
                databasePassword = line.getOptionValue("p");
            }
            if (line.hasOption("l")) {
                crawlFilter = line.getOptionValue("l");
            }
            try {
                RemoteArchiveCrawler archiveCrawler = new RemoteArchiveCrawler(numberToCrawl, crawlFilter, databaseUrl, databaseName, databaseUser, databasePassword);
                URI startURI = new URI(startUrl);
//            URI startURI = new URI("file:///Users/petwit2/.arbil/ArbilWorkingFiles/http/corpus1.mpi.nl/qfs1/media-archive/silang_data/Corpusstructure/1.imdi");                
                if (line.hasOption("d")) {
                    System.out.println("Dropping and crawing from scratch");
                    archiveCrawler.dropAllRecords();
                    crawlOption = true;
                } else {
                    // make sure the db exists
                    archiveCrawler.checkDbExists();
                }
                if (crawlOption) {
                    System.out.println("Crawling the start URL: " + startURI);
                    archiveCrawler.crawl(startURI);
                }
                if (line.hasOption("a")) {
                    System.out.println("Looking for and appending missing documents");
                    archiveCrawler.updateFast();
                }
                archiveCrawler.insertKnowIcons();
                if (line.hasOption("f")) {
                    archiveCrawler.clearAndCalculateDbStats();
                    archiveCrawler.preloadFacets();
                }
                System.exit(0); // arbil threads might be requiring this to terminate
            } catch (URISyntaxException exception) {
                System.out.println(exception.getMessage());
                System.exit(-1);
            } catch (QueryException exception) {
                System.out.println(exception.getMessage());
                System.exit(-1);
            } catch (PluginException exception) {
                System.out.println(exception.getMessage());
                System.exit(-1);
            }
        } catch (ParseException exp) {
            System.out.println("Cannot parse the command line input:" + exp.getMessage());
        }
    }
}
