package nl.mpi.kinnate.plugins.metadatasearchV1.ui;

import javax.swing.JComboBox;
import nl.mpi.kinnate.plugins.metadatasearchV1.db.MetadataFileType;

/**
 * Document : SearchOptionBox Created on : Aug 8, 2012, 4:34:14 PM
 * @author Peter Withers
 */
public class SearchOptionBox extends JComboBox {

    private MetadataFileType[] metadataFileTypes;

    public void setTypes(MetadataFileType[] metadataFileTypes) {
        this.metadataFileTypes = metadataFileTypes;
        this.removeAllItems();
        for (MetadataFileType currentType : metadataFileTypes) {
            this.addItem(currentType);
        }
    }
}
