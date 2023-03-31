package dev.gillin.mc.vnllaplayerinfo;

import org.bukkit.plugin.Plugin;

import java.io.File;

public interface IVnllaPlayerInfo extends Plugin {
    File getDataFolder();

    void createPlayerDataDirectory();


}
