package nl.mpi.yaas.common.db;

import nl.mpi.yaas.common.data.MetadataFileType;

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
