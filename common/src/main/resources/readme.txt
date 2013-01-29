Plugins HelloWorld Application

This is a sample plugin that does nothing other than be loaded into a host application.
When the ApplicationSample in tests package is run, all correctly configured plugins will be shown in the plugin loader menu.
To be a valid plugin a class must implement nl.mpi.kinnate.plugin.BasePlugin and its class name must be listed in the file of the same name in META-INF.services. Any number of plugins can be listed in this file.