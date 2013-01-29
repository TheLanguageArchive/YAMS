package nl.mpi.kinnate.plugins.metadatasearch;

import nl.mpi.flap.kinnate.KinOathEntity;
import nl.mpi.flap.plugin.AbstractBasePlugin;
import nl.mpi.flap.plugin.KinOathEntityProviderPlugin;
import nl.mpi.flap.plugin.PluginBugCatcher;
import nl.mpi.flap.plugin.PluginDialogHandler;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.flap.plugin.PluginSessionStorage;

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
