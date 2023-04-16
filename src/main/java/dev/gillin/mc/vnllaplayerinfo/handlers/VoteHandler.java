package dev.gillin.mc.vnllaplayerinfo.handlers;

import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import dev.gillin.mc.vnllaplayerinfo.groups.GroupModel;
import dev.gillin.mc.vnllaplayerinfo.groups.Groups;
import dev.gillin.mc.vnllaplayerinfo.player.GroupInfo;
import dev.gillin.mc.vnllaplayerinfo.player.PlayerConfigModel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


public class VoteHandler {
    public void giveVote(VnllaPlayerInfo plugin, Player p, PlayerConfigModel playerConfigModel, int numVotes) {
        String rank = playerConfigModel.getRank();
        if (numVotes <= 0)
            return;
        if (rank==null) {
            rank = "default";
        }

        //TODO: Move this to ServerConfigModel
        p.giveExpLevels(numVotes * plugin.getConfig().getInt("votes.xplevels"));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, numVotes * plugin.getConfig().getInt("votes.beef")));

        playerConfigModel.setTotalVotes(playerConfigModel.getTotalVotes() + numVotes);

        HashMap<String, GroupInfo> groupInfoHashMap =playerConfigModel.getGroupInfos();
        for(GroupModel groupModel: plugin.getGroups().getVoteGroupModels()){
            GroupInfo groupInfo;
            if(groupInfoHashMap.containsKey(groupModel.getGroupKey())){
                groupInfo = groupInfoHashMap.get(groupModel.getGroupKey());
            } else {
                groupInfo = new GroupInfo(groupModel.getGroupKey());
            }
            groupInfo.setCurrentVotes(groupInfo.getCurrentVotes() + numVotes);
            if(groupInfo.getCurrentVotes() >= groupModel.getVotesRequired()){
                if(groupInfo.isActive()){
                    groupInfo.setExpiration(groupInfo.getExpiration() + groupModel.getRankLength());
                } else {
                    groupInfo.setActive(true);
                    int numTimesEarned = groupInfo.getCurrentVotes() / groupModel.getVotesRequired();
                    groupInfo.setExpiration(System.currentTimeMillis() + (groupModel.getRankLength()*numTimesEarned));
                    groupInfo.setCurrentVotes(groupInfo.getCurrentVotes() % groupModel.getVotesRequired());
                    plugin.getGroups().earnGroup(p, groupModel);
                }
            }
        }

        playerConfigModel.saveConfig(plugin);


        /*try { TODO:Determine if we want this
            if (true/* !plugin.isStaff(config) TODO: fix ) {
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
        }*/
    }
}
