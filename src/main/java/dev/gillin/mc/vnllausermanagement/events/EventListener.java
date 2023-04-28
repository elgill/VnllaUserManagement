package dev.gillin.mc.vnllausermanagement.events;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.events.handlers.PlayerJoinHandler;
import dev.gillin.mc.vnllausermanagement.events.handlers.PlayerQuitHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private final PlayerJoinHandler playerJoinHandler;
    private final PlayerQuitHandler playerQuitHandler;
    public EventListener(VnllaUserManagement vnllaUserManagement) {
        playerJoinHandler = new PlayerJoinHandler(vnllaUserManagement);
        playerQuitHandler = new PlayerQuitHandler(vnllaUserManagement);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerJoinHandler.handleJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerQuitHandler.handleQuit(event.getPlayer());
    }
}
