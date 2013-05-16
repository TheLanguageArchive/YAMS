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
import nl.mpi.kinnate.plugins.metadatasearch.ui.FacetedTreePanel;

/**
 * Document : FacetedPlugin <br> Created on Sep 10, 2012, 5:13:47 PM <br>
 *
 * @author Peter Withers <br>
 */
public class FacetedPlugin extends AbstractBaseModule implements ArbilWindowPlugin {

    public FacetedPlugin() throws PluginException {
        super("Faceted Tree Plugin", "A plugin for Arbil that provides a faceted tree via a XML DB.", "nl.mpi.kinnate.plugins.metadatasearch");
    }

    public JPanel getUiPanel(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage, PluginBugCatcher bugCatcher, PluginArbilDataNodeLoader arbilDataNodeLoader, PluginWidgetFactory pluginWidgetFactory) throws PluginException {
        final FacetedTreePanel facetedTreePanel = new FacetedTreePanel(dialogHandler, bugCatcher, sessionStorage, pluginWidgetFactory);
        // trigger the facets to load
//        new Thread(facetedTreePanel.getRunnable("add")).start();
        new Thread(facetedTreePanel.getRunnable("options")).start();
        return facetedTreePanel;
    }
}
