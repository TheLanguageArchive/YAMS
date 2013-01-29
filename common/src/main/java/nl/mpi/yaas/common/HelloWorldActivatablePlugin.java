/**
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.yaas.common;

import nl.mpi.arbil.plugin.ActivatablePlugin;
import nl.mpi.arbil.plugin.PluginDialogHandler;
import nl.mpi.arbil.plugin.PluginException;
import nl.mpi.arbil.plugin.PluginSessionStorage;
import nl.mpi.kinnate.plugin.BasePlugin;
import nl.mpi.pluginloader.PluginSettings;

/**
 * Hello world activatable plugin
 */
public class HelloWorldActivatablePlugin implements BasePlugin, PluginSettings, ActivatablePlugin {

    private boolean activated = false;

    public String getName() {
        return "Sample Activate Plugin Name (yaas-common)";
    }

    public String getDescription() {
        return "Sample Activate Plugin Description String\nnl.mpi\nyaas-common";
    }

    public int getBuildVersionNumber() {
        return 3;
    }

    public int getMajorVersionNumber() {
        return 1;
    }

    public int getMinorVersionNumber() {
        return 2;
    }

    public void activatePlugin(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage) throws PluginException {
        activated = true;
    }

    public void deactivatePlugin(PluginDialogHandler dialogHandler, PluginSessionStorage sessionStorage) throws PluginException {
        activated = false;
    }

    public boolean getIsActivated() throws PluginException {
        return activated;
    }
}
