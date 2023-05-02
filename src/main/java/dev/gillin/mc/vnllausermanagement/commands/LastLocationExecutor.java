package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class LastLocationExecutor implements CommandExecutor {
    private final VnllaUserManagement plugin;

    public LastLocationExecutor(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // lastlocation <uuid>
        if (args.length == 1) {
            String uuid = args[0];
            if(!CommonUtilities.isValidUUID(uuid) ){
                plugin.getLogger().log(Level.SEVERE, "Invalid UUID entered by player");
                return false;
            }
            //get location and tp player to it
            PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin,uuid);
            if (playerConfigModel.getLastLocation() == null) {
                sender.sendMessage(ChatColor.GREEN + "This is the player's first session so they don't have one :)");
                return true;
            }
            Location location = playerConfigModel.getLastLocation();
            if(location == null) {
                Bukkit.getLogger().log(Level.WARNING, "Last Location is null");
                sender.sendMessage(ChatColor.RED + "Could not retrieve Last Location!");
                return false;
            }
            Player senderPlayer = plugin.getServer().getPlayer(sender.getName());
            if(senderPlayer == null){
                Bukkit.getLogger().log(Level.WARNING, "Failed to get Player object for sender {0}", sender.getName());
                return false;
            }
            senderPlayer.teleport(location);

            return true;
        }
        return false;
    }
}
