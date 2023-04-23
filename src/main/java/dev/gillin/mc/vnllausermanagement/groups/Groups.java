package dev.gillin.mc.vnllausermanagement.groups;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Groups implements TabExecutor{
	private final VnllaUserManagement plugin;
	private final List<GroupModel> groupModels;
	private final List<GroupModel> voteGroupModels;

	public Groups(VnllaUserManagement p) {
		plugin=p;
		Bukkit.getLogger().log(Level.INFO,"Parsing GroupModels... ");
		groupModels = GroupSerializer.deserializeGroups(p.getConfig().getConfigurationSection("groups"));
		voteGroupModels= new ArrayList<>();
		for(GroupModel groupModel: groupModels){
			if(groupModel.isVoteAchievable()){
				Bukkit.getLogger().log(Level.FINE,"Vote Achievable GroupModel: {0}",groupModel);
				voteGroupModels.add(groupModel);
			} else {
				Bukkit.getLogger().log(Level.FINE,"Non Vote Achievable GroupModel: {0}",groupModel);
			}
		}
	}

	public List<GroupModel> getGroupModels() {
		return groupModels;
	}

	public List<GroupModel> getVoteGroupModels() {
		return voteGroupModels;
	}

	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> possibles = new ArrayList<>();
        if (args.length == 1) {	
        	for(Player player : plugin.getServer().getOnlinePlayers()) {
        		possibles.add(player.getName());
        	}
            StringUtil.copyPartialMatches(args[0], possibles, completions);
        } else if (args.length == 2) {
			possibles.add("add");
			possibles.add("remove");
			StringUtil.copyPartialMatches(args[1], possibles, completions);
		} else if (args.length == 3) {
			for(GroupModel groupModel:getGroupModels()){
				if(!groupModel.isVoteAchievable()){
					possibles.add(groupModel.getGroupKey());
				}

			}
            StringUtil.copyPartialMatches(args[2], possibles, completions);
        }

        Collections.sort(completions);
        return completions;
    }

	public GroupModel getGroupModelByKey(String groupKey) {
		for (GroupModel groupModel : groupModels) {
			if (groupModel.getGroupKey().equalsIgnoreCase(groupKey)) {
				return groupModel;
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("group")) {
			// /group ExamplePlayer add mod
			if(args.length==3) {
				OfflinePlayer player= CommonUtilities.getOfflinePlayerByString(args[0]);
				String addOrRemove = args[1];
				GroupModel groupModel = getGroupModelByKey(args[2].toLowerCase());
				//Only online players for now- should fix this TODO
				if(!player.isOnline() || groupModel == null){
					sender.sendMessage(ChatColor.RED + "");
					return false;
				}
				if (addOrRemove.equalsIgnoreCase("add")){
					earnGroup(player.getPlayer(),groupModel);
					return true;
				} else if (addOrRemove.equalsIgnoreCase("remove")) {
					loseGroup(player.getPlayer(),groupModel);
					return true;
				}
			} else {
				//Currently only support three arguments
				return false;
			}
		}
		
		return false;
	}

	public void earnGroup(Player p, GroupModel groupModel){
		//List<String> perms = groupModel.getPermissions();
		//TODO: Grant perms
		List<String> earnRankCommands = groupModel.getEarnRankCommands();
		for(String cmd: earnRankCommands){
			cmd = cmd.replace("%PLAYER%", p.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}
	public void loseGroup(Player p, GroupModel groupModel){
		//List<String> perms = groupModel.getPermissions();
		//TODO: Revoke perms
		List<String> loseRankCommands = groupModel.getLoseRankCommands();
		for(String cmd: loseRankCommands){
			cmd = cmd.replace("%PLAYER%", p.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

}
