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
import nl.mpi.flap.kinnate.entityindexer.QueryException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
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
        int defaultNumberToCrawl = 10000;
        String defaultStartUrl = "http://corpus1.mpi.nl/CGN/COREX6/data/meta/imdi_3.0_eaf/corpora/cgn.imdi";
        // create the command line parser
        CommandLineParser parser = new BasicParser(); //DefaultParser();
        // create the Options
        Options options = new Options();
        options.addOption("d", "drop", false, "drop the existing database and recrawl");
        options.addOption("a", "append", false, "recrawl adding missing documents (this is the default behaviour)");
        options.addOption("n", "number", true, "number of documents to insert (default is " + defaultNumberToCrawl + ")");
        options.addOption("u", "url", true, "url of the start documents to crawl (default is " + defaultStartUrl + ")");
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            String startUrl = defaultStartUrl;
            int numberToCrawl = defaultNumberToCrawl;
            if (line.hasOption("u")) {
                startUrl = line.getOptionValue("u");
            }
            if (line.hasOption("n")) {
                numberToCrawl = Integer.parseInt(line.getOptionValue("n"));
            }
            try {
                RemoteArchiveCrawler archiveCrawler = new RemoteArchiveCrawler(RemoteArchiveCrawler.DbType.StandardDB);
                URI startURI = new URI(startUrl);
//            URI startURI = new URI("file:///Users/petwit2/.arbil/ArbilWorkingFiles/http/corpus1.mpi.nl/qfs1/media-archive/silang_data/Corpusstructure/1.imdi");
                if (line.hasOption("d")) {
                    System.out.println("Dropping and crawing from scratch");
                    archiveCrawler.dropDataBase();
                }
                archiveCrawler.crawl(startURI, numberToCrawl);
//                if (line.hasOption("a")) {
                System.out.println("Restarting crawl appending new documents");
                archiveCrawler.update(numberToCrawl);
//                }
                System.exit(0); // arbil threads might be requiring this to terminate
            } catch (URISyntaxException exception) {
                System.out.println(exception.getMessage());
                System.exit(-1);
            } catch (QueryException exception) {
                System.out.println(exception.getMessage());
                System.exit(-1);
            }
        } catch (ParseException exp) {
            System.out.println("Cannot parse the command line input:" + exp.getMessage());
        }
    }
}
