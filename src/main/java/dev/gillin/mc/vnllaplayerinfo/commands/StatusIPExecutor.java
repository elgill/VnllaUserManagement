package dev.gillin.mc.vnllaplayerinfo.commands;

import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class StatusIPExecutor implements CommandExecutor {
    private final VnllaPlayerInfo plugin;

    public StatusIPExecutor(VnllaPlayerInfo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length==1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE+"IP: "+ChatColor.WHITE+args[0]);
                    ArrayList<String> ign=new ArrayList<>();
                    for(String s:plugin.getDB().getUUIDsByIP(args[0])) {
                        ign.add(Bukkit.getOfflinePlayer(UUID.fromString(s)).getName());
                    }
                    sender.sendMessage(ChatColor.LIGHT_PURPLE+"Accounts: "+ChatColor.WHITE+ ign);
                    BanList list=plugin.getServer().getBanList(BanList.Type.IP);
                    if(list.isBanned(args[0])){
                        BanEntry ban=list.getBanEntry(args[0]);
                        if (ban != null) {
                            sender.sendMessage(ChatColor.RED+"Banned: "+ChatColor.RESET+ ban.getCreated());
                            sender.sendMessage(ChatColor.RED+"Banned by: "+ChatColor.RESET+ban.getSource());
                            sender.sendMessage(ChatColor.RED+"Reason: "+ChatColor.RESET+ban.getReason());
                        }
                    }
                }
            }.runTaskAsynchronously(plugin);
            return true;
        }
        return false;
    }
}
