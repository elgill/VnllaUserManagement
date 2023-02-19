package dev.gillin.mc.vnllaplayerinfo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Groups implements TabExecutor{
	private final VnllaPlayerInfo plugin;
	static final String[] groups= {"default","vip","vip2","mod","admin","owner"};
	
	public Groups(VnllaPlayerInfo p) {
		plugin=p;
	}
	
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> possibiles = new ArrayList<>();
        if (args.length == 1) {	
        	for(Player player : plugin.getServer().getOnlinePlayers()) {
        		possibiles.add(player.getName());
        	}
            StringUtil.copyPartialMatches(args[0], possibiles, completions);
        } else if (args.length == 2) {
        	for(String group:groups) {
				possibiles = Arrays.asList(group);
        	}
            StringUtil.copyPartialMatches(args[1], possibiles, completions);
        } 

        
        Collections.sort(completions);
        return completions;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("group")) {
			if(args.length==2) {
				String group=args[1].toLowerCase();
				OfflinePlayer player=Bukkit.getOfflinePlayer(args[0]);
				return switchGroups(player, group, sender.hasPermission("groups.owner"), plugin.getPlayerConfig(player.getUniqueId().toString()));
			}
		}
		
		return false;
	}
	
	public void votesRankChange(OfflinePlayer p, String newRank, FileConfiguration config) {
		String group=config.getString("group");
		if(group.equals("default")||group.equals("vip")||group.equals("vip2")) {
			switchGroups(p,newRank,false, config);
		}
	}
	
	public boolean switchGroups(OfflinePlayer p, String group, boolean ownerperm, FileConfiguration config) {
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
	}

}
