package nl.mpi.kinnate.plugins.metadatasearch;

import nl.mpi.arbil.plugin.KinOathEntityProviderPlugin;
import nl.mpi.arbil.plugin.PluginBugCatcher;
import nl.mpi.arbil.plugin.PluginDialogHandler;
import nl.mpi.arbil.plugin.PluginException;
import nl.mpi.arbil.plugin.PluginSessionStorage;
import nl.mpi.arbil.plugin.data.KinOathEntity;
import nl.mpi.kinnate.plugin.AbstractBasePlugin;

/**
 * Document : FacetedPlugin <br> Created on Sep 10, 2012, 5:13:47 PM <br>
 *
 * @author Peter Withers <br>
 */
public class FacetedEntityProvider extends AbstractBasePlugin implements KinOathEntityProviderPlugin {

    public FacetedEntityProvider() throws PluginException {
        super("Faceted Entity Provider Plugin", "A plugin that provides a faceted tree of the current XML DB as a directed graph.", "nl.mpi.kinnate.plugins.metadatasearch");
    }

    public KinOathEntity[] getEntities(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage, PluginBugCatcher bugCatcher) throws PluginException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
