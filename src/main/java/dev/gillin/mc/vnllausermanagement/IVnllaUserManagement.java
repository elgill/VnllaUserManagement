package dev.gillin.mc.vnllausermanagement;

import org.bukkit.plugin.Plugin;

import java.io.File;

public interface IVnllaUserManagement extends Plugin {
    File getDataFolder();

    void createPlayerDataDirectory();


}
