package dev.gillin.mc.vnllaplayerinfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Forge implements TabExecutor{
	static final String[] colors= {
				"aqua","black","blue","dark_aqua",
				"dark_blue", "dark_gray", "dark_green", "dark_purple",
				"dark_red","gold","gray","light_purple",
				"red","white","yellow"
			};
	static final ChatColor[] chatColors= {
				ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, 
				ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE,
				ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.LIGHT_PURPLE,
				ChatColor.RED, ChatColor.WHITE, ChatColor.YELLOW
			};
	VnllaPlayerInfo plugin;
	public Forge(VnllaPlayerInfo p) {
		plugin=p;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> list = new ArrayList<>();

        if (args.length == 1) {	
        	for(String a:colors) {
        		list.add(a);
        	}
            StringUtil.copyPartialMatches(args[0], list, completions);
        } 
        
        /*else if (args.length == 2) {
            if (args[0].equals("filter")) {
                    colors.add("set");
            }
            StringUtil.copyPartialMatches(args[1], colors, completions);
        }*/
        
        Collections.sort(completions);
        return completions;
    }
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(command.getName().equalsIgnoreCase("forge")) {
			if(args.length>=3) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED+"Only players can use this you silly, silly goose! :P");
					return true;
				}
				String color=args[0].toLowerCase();
				ChatColor c=ChatColor.MAGIC;
				for(int x=0;x<colors.length;x++) {
					if(color.equalsIgnoreCase(colors[x]))
						c=chatColors[x];
				}
				String parser="";
				for(int x=1;x<args.length;x++) {
					parser+=args[x]+" ";
				}
				parser=parser.trim();
				if(!(parser.length()>4&&parser.charAt(0)=='"'&&parser.charAt(parser.length()-1)=='"'))
					return false;
				
				String name=parser.substring(1,parser.indexOf('"', 1));
				String lore=parser.substring(parser.lastIndexOf('"', parser.length()-2)+1, parser.length()-1);
				Player p=(Player) sender;
				ItemStack item=p.getInventory().getItemInMainHand();
				ItemMeta meta=item.getItemMeta();
				
				meta.setDisplayName(c+name);
				ArrayList<String> lores=new ArrayList<String>();
				lores.add(lore.trim());
				meta.setLore(lores);
				
				item.setItemMeta(meta);
				
				return true;
			}
		}
		return false;
	}
}
