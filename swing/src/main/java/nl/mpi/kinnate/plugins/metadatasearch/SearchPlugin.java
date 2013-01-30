package nl.mpi.kinnate.plugins.metadatasearch;

import javax.swing.JPanel;
import nl.mpi.flap.module.AbstractBaseModule;
import nl.mpi.flap.plugin.ArbilWindowPlugin;
import nl.mpi.flap.plugin.PluginArbilDataNodeLoader;
import nl.mpi.flap.plugin.PluginBugCatcher;
import nl.mpi.flap.plugin.PluginDialogHandler;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.flap.plugin.PluginSessionStorage;
import nl.mpi.flap.plugin.PluginWidgetFactory;
import nl.mpi.kinnate.plugins.metadatasearch.ui.SearchPanel;

/**
 * Document : SearchPlugin <br> Created on Sep 10, 2012, 5:14:23 PM <br>
 *
 * @author Peter Withers <br>
 */
public class SearchPlugin extends AbstractBaseModule implements ArbilWindowPlugin {

    public SearchPlugin() throws PluginException {
        super("XML DB Search Plugin", "A plugin for Arbil that provides a XML DB search.", "nl.mpi.kinnate.plugins.metadatasearch");
    }

    public JPanel getUiPanel(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage, PluginBugCatcher bugCatcher, PluginArbilDataNodeLoader arbilDataNodeLoader, PluginWidgetFactory pluginWidgetFactory) throws PluginException {
        SearchPanel searchPanel = new SearchPanel(arbilDataNodeLoader, dialogHandler, bugCatcher, sessionStorage, pluginWidgetFactory);
        searchPanel.initOptions();
        return searchPanel;
    }
}
