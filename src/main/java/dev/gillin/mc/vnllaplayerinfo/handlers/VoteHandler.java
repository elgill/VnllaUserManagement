package dev.gillin.mc.vnllaplayerinfo.handlers;

import dev.gillin.mc.vnllaplayerinfo.Groups;
import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import dev.gillin.mc.vnllaplayerinfo.player.PlayerConfigModel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;

public class VoteHandler {
    public void giveVote(VnllaPlayerInfo plugin, Player p, PlayerConfigModel playerConfigModel, int numVotes) {
        String rank = playerConfigModel.getRank();
        if (numVotes <= 0)
            return;
        if (rank==null) {
            rank = "default";
        }

        p.giveExpLevels(numVotes * plugin.getConfig().getInt("votes.xplevels"));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, numVotes * plugin.getConfig().getInt("votes.beef")));
        int total = playerConfigModel.getTotalVotes() + numVotes;
        int vip1 = playerConfigModel.getVip1Votes() + numVotes;
        int vip2 = playerConfigModel.getVip2Votes() + numVotes;
        long keeprank = 1000 * plugin.getConfig().getLong("votes.keeprank");
        long vip1expire = playerConfigModel.getVip1Expire();
        long vip2expire = playerConfigModel.getVip2Expire();

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
                /*if (!plugin.isStaff(config)) {
                    new Groups(plugin).votesRankChange(p, "vip", config);
                }*/ //TODO: Fix correctly
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
                /*if (!plugin.isStaff(config)) {
                    new Groups(plugin).votesRankChange(p, "vip2", config);
                }*/ //TODO: Fix Correctly
            }
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent removetemp vip2");
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent addtemp vip2 " + vip2expire / 1000 + " replace");

        }

        playerConfigModel.setTotalVotes(total);
        playerConfigModel.setVip1Votes(vip1);
        playerConfigModel.setVip2Votes(vip2);
        playerConfigModel.setRank(rank);
        playerConfigModel.setVip1Expire(vip1expire);
        playerConfigModel.setVip2Expire(vip2expire);

        playerConfigModel.saveConfig(plugin);


        try {
            if (true/* !plugin.isStaff(config) TODO: fix */) {
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
