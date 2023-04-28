package dev.gillin.mc.vnllausermanagement.events.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerQuitHandler {

    private final VnllaUserManagement plugin;

    public PlayerQuitHandler(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    public void handleQuit(Player leavingPlayer) {
        handleLeaving(leavingPlayer.getUniqueId().toString(), true);
    }

    public void handleLeaving(String uuid, boolean async) {
        Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
        if(player == null){
            return;
        }
        Location loc = player.getLocation();
        if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    doLeavingTasks(uuid, loc);
                }
            }.runTaskAsynchronously(plugin);
        } else {
            doLeavingTasks(uuid, loc);
        }
    }

    private void doLeavingTasks(String uuid, Location loc) {
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);
        long current = System.currentTimeMillis();
        long lastLogin = playerConfigModel.getLastLogin();

        playerConfigModel.setLastLogout(current);
        long currentSessionLength = (current - lastLogin);
        if (currentSessionLength < (1000 * 60 * 60 * 12)) {
            playerConfigModel.setTotalPlaytime(playerConfigModel.getTotalPlaytime() + currentSessionLength);
        }
        //TODO: Investigate why I did LastLocation manually- Location object would be better
        playerConfigModel.setLastLocationX(loc.getX());
        playerConfigModel.setLastLocationY(loc.getY());
        playerConfigModel.setLastLocationZ(loc.getZ());
        World world = loc.getWorld();
        if(world != null){
            playerConfigModel.setLastLocationWorld(loc.getWorld().getName());
        }

        playerConfigModel.saveConfig(plugin);
    }
}
