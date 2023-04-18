package dev.gillin.mc.vnllaplayerinfo.handlers;

import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import dev.gillin.mc.vnllaplayerinfo.groups.GroupModel;
import dev.gillin.mc.vnllaplayerinfo.player.GroupInfo;
import dev.gillin.mc.vnllaplayerinfo.player.PlayerConfigModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


public class VoteHandler {
    public void giveVote(VnllaPlayerInfo plugin, Player p, PlayerConfigModel playerConfigModel, int numVotes) {
        if (numVotes <= 0)
            return;

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

        /* These are messages if we want to add back expiration messages
        p.sendMessage(ChatColor.DARK_GREEN + "You need " + ChatColor.GOLD + vip1remain + ChatColor.DARK_GREEN +
                " more votes to earn VIP, and " + ChatColor.GOLD + vip2remain + ChatColor.DARK_GREEN + " more votes for VIP2.");
        p.sendMessage(ChatColor.DARK_GREEN + "You need " + ChatColor.GOLD + vip2remain + ChatColor.DARK_GREEN + " more votes to VIP2.");

        END BLOCK*/

    }
}
