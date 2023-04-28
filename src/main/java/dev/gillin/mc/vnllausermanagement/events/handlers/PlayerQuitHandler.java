package dev.gillin.mc.vnllausermanagement.events.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import org.bukkit.entity.Player;

public class PlayerQuitHandler {

    private final VnllaUserManagement plugin;

    public PlayerQuitHandler(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    public void handleQuit(Player leavingPlayer) {
        plugin.handleLeaving(leavingPlayer.getUniqueId().toString(), true);
    }
}
