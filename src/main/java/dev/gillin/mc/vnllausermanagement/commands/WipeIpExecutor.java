package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WipeIpExecutor implements CommandExecutor {
    private final VnllaUserManagement plugin;
    public WipeIpExecutor(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("wipeip") && args.length == 1) {
            OfflinePlayer p = CommonUtilities.getOfflinePlayerByString(args[0]);
            plugin.getPlayerData().deleteIPsByUUID(p.getUniqueId().toString());
            commandSender.sendMessage(ChatColor.GREEN + "IPs for " + p.getName() + " cleared from the alt detector db!");
            return true;
        }
        return false;
    }
}
