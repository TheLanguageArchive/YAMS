package nl.mpi.kinnate.plugins.metadatasearch;

import javax.swing.JPanel;
import nl.mpi.arbil.plugin.ArbilWindowPlugin;
import nl.mpi.arbil.plugin.PluginArbilDataNodeLoader;
import nl.mpi.arbil.plugin.PluginBugCatcher;
import nl.mpi.arbil.plugin.PluginDialogHandler;
import nl.mpi.arbil.plugin.PluginException;
import nl.mpi.arbil.plugin.PluginSessionStorage;
import nl.mpi.arbil.plugin.PluginWidgetFactory;
import nl.mpi.kinnate.plugin.AbstractBasePlugin;
import nl.mpi.kinnate.plugins.metadatasearch.ui.FacetedTreePanel;

/**
 * Document : FacetedPlugin <br> Created on Sep 10, 2012, 5:13:47 PM <br>
 *
 * @author Peter Withers <br>
 */
public class FacetedPlugin extends AbstractBasePlugin implements ArbilWindowPlugin {

    public FacetedPlugin() throws PluginException {
        super("Faceted Tree Plugin", "A plugin for Arbil that provides a faceted tree via a XML DB.", "nl.mpi.kinnate.plugins.metadatasearch");
    }

    public JPanel getUiPanel(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage, PluginBugCatcher bugCatcher, PluginArbilDataNodeLoader arbilDataNodeLoader, PluginWidgetFactory pluginWidgetFactory) throws PluginException {
        final FacetedTreePanel facetedTreePanel = new FacetedTreePanel(arbilDataNodeLoader, dialogHandler, bugCatcher, sessionStorage, pluginWidgetFactory);
        // trigger the facets to load
//        new Thread(facetedTreePanel.getRunnable("add")).start();
        new Thread(facetedTreePanel.getRunnable("options")).start();
        return facetedTreePanel;
    }
}
