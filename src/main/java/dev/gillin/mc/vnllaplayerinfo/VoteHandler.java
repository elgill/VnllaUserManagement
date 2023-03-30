package dev.gillin.mc.vnllaplayerinfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;

public class VoteHandler {
    public void giveVote(VnllaPlayerInfo plugin, Player p, FileConfiguration config, int num) {
        String rank = config.getString("votes.rank");
        if (num <= 0)
            return;
        if (!config.isSet("votes.rank")) {
            rank = "default";
        }

        p.giveExpLevels(num * plugin.getConfig().getInt("votes.xplevels"));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, num * plugin.getConfig().getInt("votes.beef")));
        int total = config.getInt("votes.totalVotes") + num;
        int vip1 = config.getInt("votes.vip1Votes") + num;
        int vip2 = config.getInt("votes.vip2Votes") + num;
        long keeprank = 1000 * plugin.getConfig().getLong("votes.keeprank");
        long vip1expire = config.getLong("votes.vip1expire");
        long vip2expire = config.getLong("votes.vip2expire");

        //TODO: THESE CAUSE ASYNC ERRORS!
        while (vip1 >= plugin.getConfig().getInt("votes.vip1")) {
            //if expiration date is later
            vip1 -= plugin.getConfig().getInt("votes.vip1");

            if (vip1expire > System.currentTimeMillis()) {
                vip1expire += keeprank;
            } else if (vip1expire <= System.currentTimeMillis() && vip1expire >= 0) {
                vip1expire = System.currentTimeMillis() + keeprank;
            }
            if (rank.equalsIgnoreCase("default")) {
                rank = "vip";

                // massive problem
                if (!plugin.isStaff(config)) {
                    new Groups(plugin).votesRankChange(p, "vip", config);
                }
            }
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent removetemp vip");
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent addtemp vip " + vip1expire / 1000 + " replace");

        }
        while (vip2 >= plugin.getConfig().getInt("votes.vip2")) {
            //if expiration date is later
            vip2 -= plugin.getConfig().getInt("votes.vip2");

            if (vip2expire > System.currentTimeMillis()) {
                vip2expire += keeprank;
            } else if (vip2expire <= System.currentTimeMillis() && vip2expire >= 0) {
                vip2expire = System.currentTimeMillis() + keeprank;
            }
            if (rank.equalsIgnoreCase("default") || rank.equalsIgnoreCase("vip")) {
                rank = "vip2";
                if (!plugin.isStaff(config)) {
                    new Groups(plugin).votesRankChange(p, "vip2", config);
                }
            }
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent removetemp vip2");
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent addtemp vip2 " + vip2expire / 1000 + " replace");

        }


        config.set("votes.totalVotes", total);
        config.set("votes.vip1Votes", vip1);
        config.set("votes.vip2Votes", vip2);
        config.set("votes.rank", rank);
        config.set("votes.vip1expire", vip1expire);
        config.set("votes.vip2expire", vip2expire);

        plugin.savePlayerConfig(config, p.getUniqueId().toString());

        try {
            if (!plugin.isStaff(config)) {
                int vip2remain = plugin.getConfig().getInt("votes.vip2") - vip2;
                int vip1remain = plugin.getConfig().getInt("votes.vip1") - vip1;
                if (rank.equals("default")) {
                    p.sendMessage(ChatColor.DARK_GREEN + "You need " + ChatColor.GOLD + vip1remain + ChatColor.DARK_GREEN + " more votes to earn VIP, and " + ChatColor.GOLD + vip2remain + ChatColor.DARK_GREEN + " more votes for VIP2.");
                } else if (rank.equals("vip")) {
                    p.sendMessage(ChatColor.DARK_GREEN + "You need " + ChatColor.GOLD + vip2remain + ChatColor.DARK_GREEN + " more votes to VIP2.");
                }
            }
        } catch (Exception e) {
            plugin.broadcastOPs("Error sending remaining votes message to " + p.getName());
        }
    }
}
