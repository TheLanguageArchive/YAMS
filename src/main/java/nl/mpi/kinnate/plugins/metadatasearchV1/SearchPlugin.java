package nl.mpi.kinnate.plugins.metadatasearchV1;

import javax.swing.JPanel;
import nl.mpi.arbil.plugin.ArbilWindowPlugin;
import nl.mpi.arbil.plugin.PluginArbilDataNodeLoader;
import nl.mpi.arbil.plugin.PluginBugCatcher;
import nl.mpi.arbil.plugin.PluginDialogHandler;
import nl.mpi.arbil.plugin.PluginException;
import nl.mpi.arbil.plugin.PluginSessionStorage;
import nl.mpi.kinnate.plugin.AbstractBasePlugin;
import nl.mpi.kinnate.plugins.metadatasearchV1.ui.SearchPanel;

/**
 * Document : SearchPlugin <br> Created on Sep 10, 2012, 5:14:23 PM <br>
 *
 * @author Peter Withers <br>
 */
public class SearchPlugin extends AbstractBasePlugin implements ArbilWindowPlugin {

    public SearchPlugin() throws PluginException {
        super("XML DB Search Plugin 0-1 (good-with-more-files)", "A plugin for Arbil that provides a XML DB search.", "nl.mpi.kinnate.plugins.metadatasearch");
    }

    public JPanel getUiPanel(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage, PluginBugCatcher bugCatcher, PluginArbilDataNodeLoader arbilDataNodeLoader) throws PluginException {
        SearchPanel searchPanel = new SearchPanel(arbilDataNodeLoader, dialogHandler);
        searchPanel.initOptions();
        return searchPanel;
    }
}
