package dev.gillin.mc.vnllausermanagement.events.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PlayerJoinHandler implements Listener {

    private final VnllaUserManagement plugin;

    public PlayerJoinHandler(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    public void handleJoin(Player joiningPlayer) {
        String uuid = joiningPlayer.getUniqueId().toString();
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);

        handleVotesOwed(joiningPlayer, playerConfigModel);

        new BukkitRunnable() {
            @Override
            public void run() {
                String ip = getPlayerIp(joiningPlayer);
                updatePlayerConfigModel(ip, joiningPlayer, playerConfigModel);
                plugin.getPlayerData().insertPlayerIP(uuid, ip);

                //is one of their alts banned?!
                List<OfflinePlayer> banned = plugin.getBannedAlts(ip, uuid);

                plugin.sendAltWarning(banned, joiningPlayer);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updatePlayerConfigModel(String ip, Player joiningPlayer, PlayerConfigModel playerConfigModel) {
        String name = joiningPlayer.getName();

        //lose vip if applicable
        plugin.checkLoseGroup(joiningPlayer, playerConfigModel);

        for(String earnGroupKey: playerConfigModel.getPendingEarnedGroups()){
            plugin.getGroups().earnGroup(joiningPlayer, plugin.getGroups().getGroupModelByKey(earnGroupKey));
        }
        for(String loseGroupKey: playerConfigModel.getPendingLostGroups()){
            plugin.getGroups().loseGroup(joiningPlayer, plugin.getGroups().getGroupModelByKey(loseGroupKey));
        }
        playerConfigModel.setPendingEarnedGroups(new ArrayList<>());
        playerConfigModel.setPendingLostGroups(new ArrayList<>());

        playerConfigModel.setLastPlayerName(name);
        List<String> ips = playerConfigModel.getIpAddresses();
        if (!ips.contains(ip)) {
            ips.add(ip);
        }
        playerConfigModel.setLastLogin(System.currentTimeMillis());
        //save changes
        playerConfigModel.saveConfig(plugin);
    }

    public void handleVotesOwed(Player joiningPlayer, PlayerConfigModel playerConfigModel) {
        if (playerConfigModel.getVotesOwed() > 0) {
            Bukkit.getLogger().log(Level.FINE, "Applying Votes owed to {0}", joiningPlayer.getName());
            plugin.getVoteHandler().giveVote(plugin, joiningPlayer, playerConfigModel, playerConfigModel.getVotesOwed());
            playerConfigModel.setVotesOwed(0);
            playerConfigModel.saveConfig(plugin);
        }
    }

    public String getPlayerIp(Player player){
        String ip = player.spigot().getRawAddress().toString();
        //trim to ip from e.g. /127.0.0.1:32673
        try {
            ip = ip.substring(ip.indexOf('/') + 1, ip.indexOf(':'));
        } catch (StringIndexOutOfBoundsException e) {
            ip = "ERROR";
            Bukkit.getLogger().log(Level.SEVERE, "Failed to Parse IP", e);
        }
        return ip;
    }
}
