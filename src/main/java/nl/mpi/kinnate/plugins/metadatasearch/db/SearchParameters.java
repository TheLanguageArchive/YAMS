package nl.mpi.kinnate.plugins.metadatasearch.db;

import nl.mpi.kinnate.plugins.metadatasearch.data.MetadataFileType;

/**
 * Document : SearchParameters <br> Created on Sep 11, 2012, 4:55:09 PM <br>
 *
 * @author Peter Withers <br>
 */
public class SearchParameters {

    MetadataFileType fileType;
    MetadataFileType fieldType;
    ArbilDatabase.SearchNegator searchNegator;
    ArbilDatabase.SearchType searchType;
    String searchString;

    public SearchParameters(MetadataFileType fileType, MetadataFileType fieldType, ArbilDatabase.SearchNegator searchNegator, ArbilDatabase.SearchType searchType, String searchString) {
        this.fileType = fileType;
        this.fieldType = fieldType;
        this.searchNegator = searchNegator;
        this.searchType = searchType;
        this.searchString = searchString;
    }
}
