package dev.gillin.mc.vnllausermanagement;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface IVnllaUserManagement extends Plugin {
    @NotNull File getDataFolder();

    void createPlayerDataDirectory();


}
