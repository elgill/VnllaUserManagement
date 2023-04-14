package dev.gillin.mc.vnllaplayerinfo;

import dev.gillin.mc.vnllaplayerinfo.Database.Database;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

class VnllaPlayerInfoTest {

    private IVnllaPlayerInfo vnllaPlayerInfo;

    @BeforeEach
    public void setUp() {
        vnllaPlayerInfo = Mockito.mock(VnllaPlayerInfo.class);
        when(vnllaPlayerInfo.getLogger()).thenReturn(Logger.getLogger("TestLogger"));
    }






}

