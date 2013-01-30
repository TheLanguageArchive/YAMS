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
package nl.mpi.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import nl.mpi.flap.module.BaseModule;
import nl.mpi.flap.plugin.ActivatablePlugin;
import nl.mpi.flap.plugin.PluginException;
import nl.mpi.pluginloader.PluginManager;
import nl.mpi.pluginloader.PluginService;
import nl.mpi.pluginloader.ui.PluginMenu;

/**
 * Created on : Nov 07, 2012, 12:01:34 PM
 *
 * 
 * @author Peter Withers
 */
public class ApplicationSample {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        JMenuBar jMenuBar = new JMenuBar();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ApplicationSample.class.getResourceAsStream("/readme.txt"), "UTF-8"));
            String readString;
            while (null != (readString = bufferedReader.readLine())) {
                stringBuilder.append(readString);
                stringBuilder.append("\n");
            }
        } catch (IOException exception) {
            stringBuilder.append(exception.getMessage());
        }
        final JTextArea jTextArea = new JTextArea(stringBuilder.toString());
        PluginManager pluginManager = new PluginManager() {
            public boolean isActivated(BaseModule kinOathPlugin) {
                try {
                    if (kinOathPlugin instanceof ActivatablePlugin) {
                        return ((ActivatablePlugin) kinOathPlugin).getIsActivated();
                    }
                } catch (PluginException exception) {
                    System.err.println("error getting plugin state:" + exception.getMessage());
                }
                return false;
            }

            public void activatePlugin(BaseModule kinOathPlugin) {
                try {
                    if (kinOathPlugin instanceof ActivatablePlugin) {
                        ((ActivatablePlugin) kinOathPlugin).activatePlugin(null, null);
                        jTextArea.setText("activate: \n" + kinOathPlugin.getName() + "\n" + kinOathPlugin.getMajorVersionNumber() + "." + kinOathPlugin.getMinorVersionNumber() + "." + kinOathPlugin.getBuildVersionNumber() + "\n" + kinOathPlugin.getDescription());
                    } else {
                        jTextArea.setText("non activateable plugin: \n" + kinOathPlugin.getName() + "\n" + kinOathPlugin.getMajorVersionNumber() + "." + kinOathPlugin.getMinorVersionNumber() + "." + kinOathPlugin.getBuildVersionNumber() + "\n" + kinOathPlugin.getDescription());
                    }
                } catch (PluginException exception) {
                    jTextArea.setText("Error activating plugin: \n" + kinOathPlugin.getName() + "\n" + kinOathPlugin.getMajorVersionNumber() + "." + kinOathPlugin.getMinorVersionNumber() + "." + kinOathPlugin.getBuildVersionNumber() + "\n" + kinOathPlugin.getDescription());
                }
            }

            public void deactivatePlugin(BaseModule kinOathPlugin) {
                try {
                    if (kinOathPlugin instanceof ActivatablePlugin) {
                        ((ActivatablePlugin) kinOathPlugin).deactivatePlugin(null, null);
                        jTextArea.setText("deactivate: \n" + kinOathPlugin.getName() + "\n" + kinOathPlugin.getMajorVersionNumber() + "." + kinOathPlugin.getMinorVersionNumber() + "." + kinOathPlugin.getBuildVersionNumber() + "\n" + kinOathPlugin.getDescription());
                    } else {
                        jTextArea.setText("non deactivateable plugin: \n" + kinOathPlugin.getName() + "\n" + kinOathPlugin.getMajorVersionNumber() + "." + kinOathPlugin.getMinorVersionNumber() + "." + kinOathPlugin.getBuildVersionNumber() + "\n" + kinOathPlugin.getDescription());
                    }
                } catch (PluginException exception) {
                    jTextArea.setText("error deactivating plugin: \n" + kinOathPlugin.getName() + "\n" + kinOathPlugin.getMajorVersionNumber() + "." + kinOathPlugin.getMinorVersionNumber() + "." + kinOathPlugin.getBuildVersionNumber() + "\n" + kinOathPlugin.getDescription());
                }
            }
        };
        try {
            jMenuBar.add(new PluginMenu(new PluginService(new URL[]{new URL("file:///path-to-plugins/plugin-jar-name.jar")}), pluginManager, false));
        } catch (MalformedURLException exception) {
            jMenuBar.add(new JLabel(exception.getMessage()));
        }
        jFrame.setJMenuBar(jMenuBar);
        jFrame.setContentPane(new JScrollPane(jTextArea));
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
