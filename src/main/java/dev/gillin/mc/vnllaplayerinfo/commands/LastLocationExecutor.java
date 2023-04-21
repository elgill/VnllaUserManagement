package dev.gillin.mc.vnllaplayerinfo.commands;

import dev.gillin.mc.vnllaplayerinfo.CommonUtilities;
import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import dev.gillin.mc.vnllaplayerinfo.player.PlayerConfigModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class LastLocationExecutor implements CommandExecutor {
    private final VnllaPlayerInfo plugin;

    public LastLocationExecutor(VnllaPlayerInfo plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        // lastlocation <uuid>
        if (command.getName().equalsIgnoreCase("lastlocation") && args.length == 1) {
            String uuid = args[0];
            if(!CommonUtilities.isValidUUID(uuid) ){
                plugin.getLogger().log(Level.SEVERE, "Invalid UUID entered by player");
                return false;
            }
            //get location and tp player to it
            PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin,uuid);
            if (playerConfigModel.getLastLocationWorld() == null) {
                sender.sendMessage(ChatColor.GREEN + "This is the player's first session so they don't have one :)");
                return true;
            }
            Location loc = new Location(Bukkit.getWorld(playerConfigModel.getLastLocationWorld()),
                    playerConfigModel.getLastLocationX(), playerConfigModel.getLastLocationY(), playerConfigModel.getLastLocationZ());
            plugin.getServer().getPlayer(sender.getName()).teleport(loc);

            return true;
        }
        return false;
    }
}
