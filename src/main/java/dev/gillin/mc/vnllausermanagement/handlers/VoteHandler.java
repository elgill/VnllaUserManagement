package dev.gillin.mc.vnllausermanagement.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.groups.GroupModel;
import dev.gillin.mc.vnllausermanagement.player.GroupInfo;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class VoteHandler {
    public void giveVote(VnllaUserManagement plugin, Player p, PlayerConfigModel playerConfigModel, int numVotes) {
        if (numVotes <= 0)
            return;

        //TODO: Move this to ServerConfigModel
        p.giveExpLevels(numVotes * plugin.getConfig().getInt("votes.xplevels"));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, numVotes * plugin.getConfig().getInt("votes.beef")));

        playerConfigModel.setTotalVotes(playerConfigModel.getTotalVotes() + numVotes);

        Map<String, GroupInfo> groupInfoHashMap = playerConfigModel.getGroupInfos();
        for(GroupModel groupModel: plugin.getGroups().getVoteGroupModels()){
            GroupInfo groupInfo;
            if(groupModel.getVotesRequired() <= 0){
                continue;
            }
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
        /*
            These are messages if we want to add back expiration messages
            Two remain: p.sendMessage(ChatColor.DARK_GREEN + "You need " + ChatColor.GOLD + vip1remain + ChatColor.DARK_GREEN +" more votes to earn VIP, and " + ChatColor.GOLD + vip2remain + ChatColor.DARK_GREEN + " more votes for VIP2.");
            One Remain: p.sendMessage(ChatColor.DARK_GREEN + "You need " + ChatColor.GOLD + vip2remain + ChatColor.DARK_GREEN + " more votes to VIP2.");
        */

    }
}
