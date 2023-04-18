package dev.gillin.mc.vnllaplayerinfo.groups;

import dev.gillin.mc.vnllaplayerinfo.CommonUtilities;
import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import dev.gillin.mc.vnllaplayerinfo.groups.GroupModel;
import dev.gillin.mc.vnllaplayerinfo.groups.GroupSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Groups implements TabExecutor{
	private final VnllaPlayerInfo plugin;
	private final List<GroupModel> groupModels;
	private final List<GroupModel> voteGroupModels;

	public Groups(VnllaPlayerInfo p) {
		plugin=p;
		groupModels = GroupSerializer.deserializeGroups(p.getConfig().getConfigurationSection("groups"));
		voteGroupModels= new ArrayList<>();
		for(GroupModel groupModel: groupModels){
			if(groupModel.isVoteAchievable()){
				voteGroupModels.add(groupModel);
			}
		}
	}

	public List<GroupModel> getGroupModels() {
		return groupModels;
	}

	public List<GroupModel> getVoteGroupModels() {
		return voteGroupModels;
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
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
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("group")) {
			// /group ExamplePlayer add mod
			if(args.length==3) {
				OfflinePlayer player= CommonUtilities.getOfflinePlayerByString(args[0]);
				String addOrRemove = args[1];
				GroupModel groupModel = getGroupModelByKey(args[2].toLowerCase());
				//Only online players for now- should fix this
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

				return false;
			}
		}
		
		return false;
	}

	
	/*public boolean switchGroups(OfflinePlayer p, String group, boolean ownerperm, FileConfiguration config) {
		group=group.toLowerCase();
		//FileConfiguration config=plugin.getPlayerConfig(p.getUniqueId().toString());		
		if(group.equalsIgnoreCase("default")) {
			config.set("group", group);
			if(config.isSet("votes.viprank")&&!config.getString("votes.viprank").equals("default")) {
				config.set("group", config.getString("votes.viprank"));
				//TODO advance viprank
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" "+config.getString("votes.viprank").toUpperCase()+"!");
			}
			else {
				//TODO advance default
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" DEFAULT!");
			}
			plugin.savePlayerConfig(config, p.getUniqueId().toString());
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove owner");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove admin");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove mod");
			return true;
		}
		else if(group.equalsIgnoreCase("vip")) {
			config.set("group", group);
			if(config.isSet("votes.viprank")&&config.getString("votes.viprank").equals("vip2")) {
				config.set("group", config.getString("votes.viprank"));
				//TODO advance vip2
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" VIP2!");
			}
			else {
				//TODO advance vip
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" VIP!");
			}
			plugin.savePlayerConfig(config, p.getUniqueId().toString());
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove owner");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove admin");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove mod");
			return true;
		}
		else if(group.equalsIgnoreCase("vip2")) {
			config.set("group", group);
			//TODO advance vip2
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" VIP2!");
			plugin.savePlayerConfig(config, p.getUniqueId().toString());
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove owner");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove admin");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove mod");
			return true;
		}
		else if(group.equalsIgnoreCase("mod")) {
			config.set("group", group);
			//TODO advance mod
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" MOD!");
			plugin.savePlayerConfig(config, p.getUniqueId().toString());
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove owner");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove admin");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent add mod");
			return true;
		}
		else if(group.equalsIgnoreCase("admin")) {
			config.set("group", group);
			//TODO advance admin
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" ADMIN!");
			plugin.savePlayerConfig(config, p.getUniqueId().toString());
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent remove owner");
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent add admin");
			return true;
		}
		else if(group.equalsIgnoreCase("owner")&&ownerperm) {
			config.set("group", group);
			//TODO advance owner
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "advancemessage "+p.getName()+" OWNER!");
			plugin.savePlayerConfig(config, p.getUniqueId().toString());
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+p.getName()+" parent add owner");
			return true;
		}
		
		
		return false;
	}*/

	public void earnGroup(Player p, GroupModel groupModel){
		//List<String> perms = groupModel.getPermissions();
		//TODO: Grant perms
		List<String> earnRankCommands = groupModel.getEarnRankCommands();
		for(String cmd: earnRankCommands){
			cmd.replaceAll("%PLAYER%", p.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}
	public void loseGroup(Player p, GroupModel groupModel){
		//List<String> perms = groupModel.getPermissions();
		//TODO: Revoke perms
		List<String> loseRankCommands = groupModel.getLoseRankCommands();
		for(String cmd: loseRankCommands){
			cmd.replaceAll("%PLAYER%", p.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

}
