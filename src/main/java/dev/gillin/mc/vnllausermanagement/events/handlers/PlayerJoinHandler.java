package dev.gillin.mc.vnllausermanagement.events.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerJoinHandler implements Listener {

    private final VnllaUserManagement plugin;

    public PlayerJoinHandler(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    public void handleJoin(Player joiningPlayer) {
        String uuid = joiningPlayer.getUniqueId().toString();
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);

        plugin.handleVotesOwed(joiningPlayer, playerConfigModel);

        new BukkitRunnable() {
            @Override
            public void run() {
                String ip = plugin.getPlayerIp(joiningPlayer);
                plugin.updatePlayerConfigModel(ip, joiningPlayer, playerConfigModel);
                plugin.getPlayerData().insertPlayerIP(uuid, ip);

                //is one of their alts banned?!
                List<OfflinePlayer> banned = plugin.getBannedAlts(ip, uuid);

                plugin.sendAltWarning(banned, joiningPlayer);
            }
        }.runTaskAsynchronously(plugin);
    }
}
