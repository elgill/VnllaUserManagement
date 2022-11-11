package dev.gillin.mc.vnllaplayerinfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Forge implements TabExecutor{

	public static final Map<String, ChatColor> colorMap;
	static {
		Map<String, ChatColor> map = new HashMap<>();
		map.put("aqua",ChatColor.AQUA);
		map.put("black",ChatColor.BLACK);
		map.put("blue",ChatColor.BLUE);
		map.put("dark_aqua",ChatColor.DARK_AQUA);
		map.put("dark_blue",ChatColor.DARK_BLUE);
		map.put("dark_gray",ChatColor.DARK_GRAY);
		map.put("dark_green",ChatColor.DARK_GREEN);
		map.put("dark_purple",ChatColor.DARK_PURPLE);
		map.put("dark_red",ChatColor.DARK_RED);
		map.put("gold",ChatColor.GOLD);
		map.put("gray",ChatColor.GRAY);
		map.put("light_purple",ChatColor.LIGHT_PURPLE);
		map.put("red",ChatColor.RED);
		map.put("white",ChatColor.WHITE);
		map.put("yellow",ChatColor.YELLOW);
		colorMap = Collections.unmodifiableMap(map);
	}
	VnllaPlayerInfo plugin;
	public Forge(VnllaPlayerInfo p) {
		plugin=p;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			List<String> list = new ArrayList<>(colorMap.keySet());
            StringUtil.copyPartialMatches(args[0], list, completions);
        }
        
        Collections.sort(completions);
        return completions;
    }
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String commandLabel, String[] args) {
		if(command.getName().equalsIgnoreCase("forge") && args.length>=3) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can use this you silly, silly goose! :P");
				return true;
			}

			ChatColor color=colorMap.getOrDefault(args[0].toLowerCase(), ChatColor.MAGIC);

			StringBuilder parser= new StringBuilder();
			for(int x=1; x<args.length; x++) {
				parser.append(args[x]).append(" ");
			}
			// eliminate extra space
			parser = new StringBuilder(parser.toString().trim());

			if(!(parser.length()>4 && parser.charAt(0)=='"' && parser.charAt(parser.length()-1) == '"'))
				return false;

			String name=parser.substring(1, parser.toString().indexOf('"', 1));
			String lore=parser.substring(parser.toString().lastIndexOf('"', parser.length()-2)+1, parser.length()-1);

			Player p=(Player) sender;
			ItemStack item=p.getInventory().getItemInMainHand();
			ItemMeta meta=item.getItemMeta();

			if (meta == null) {
				sender.sendMessage(ChatColor.RED + "Error: Item meta is null");
				return false;
			}

			ArrayList<String> loreStrings=new ArrayList<>();
			loreStrings.add(lore.trim());

			meta.setDisplayName(color + name);
			meta.setLore(loreStrings);
			item.setItemMeta(meta);

			return true;
		}
		return false;
	}
}
