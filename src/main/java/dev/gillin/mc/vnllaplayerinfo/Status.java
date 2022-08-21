package dev.gillin.mc.vnllaplayerinfo;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
/*
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
*/
public class Status implements CommandExecutor{
	private final VnllaPlayerInfo plugin;
	public Status(VnllaPlayerInfo plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(command.getName().equalsIgnoreCase("status")&&sender.hasPermission("VnllaPlayerInfo.seestatus")) {
			if(args.length==1) {
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	/*
		            	 * While this is "deprecated" It is the fastest method to get this done.
		            	 * It's only deprecated because they want you to use uuids as the parameter
		            	 * Since that would make no sense for this use case, this still makes most sense.
		            	*/
		            	@SuppressWarnings("deprecation")
						OfflinePlayer p=Bukkit.getOfflinePlayer(args[0]);
						String uuid=p.getUniqueId().toString();
						ArrayList<String> ips=plugin.getDB().getIPsByUUID(uuid);
						ArrayList<String> alts=new ArrayList<>();
						
						FileConfiguration config=plugin.getPlayerConfig(uuid);
						
						Scoreboard main=Bukkit.getScoreboardManager().getMainScoreboard();
						int su_points=main.getObjective("su_points").getScore(p.getName()).getScore();
						int timesSuspected=main.getObjective("timesSuspected").getScore(p.getName()).getScore();
						
						// only populated alts list if it will be needed
						if(p.isBanned() || sender.hasPermission("VnllaPlayerInfo.seestatusalt")) {
							for (String s:ips) {
								for(String x:plugin.getDB().getUUIDssByIP(s)) {
									String name=plugin.getServer().getOfflinePlayer(UUID.fromString(x)).getName();
									if(!x.equalsIgnoreCase(uuid)&&!alts.contains(name)) 
										alts.add(name);
								}
							}
						}
						
						sender.sendMessage(ChatColor.LIGHT_PURPLE+"Name: "+ChatColor.WHITE+p.getName());
						sender.sendMessage(ChatColor.LIGHT_PURPLE+"UUID: "+ChatColor.WHITE+uuid);
						
						
						
						//if player has ever logged in
						if(config.isSet("playtime.lastLogin")) {
							try {
							String rank=config.getString("votes.rank");
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"viprank: "+ChatColor.WHITE+rank);
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"group: "+ChatColor.WHITE+config.getString("group"));
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"IP: "+ChatColor.WHITE+ips.toString());
							
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Total Votes: "+ChatColor.WHITE+config.getInt("votes.totalVotes"));
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Vip1 Votes: "+ChatColor.WHITE+config.getInt("votes.vip1Votes"));
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Vip2 Votes: "+ChatColor.WHITE+config.getInt("votes.vip2Votes"));
							Date expire;
							if(rank!=null&&rank.equalsIgnoreCase("vip"))
								expire=new Date(config.getLong("votes.vip1expire"));
							else if(rank!=null&&rank.equalsIgnoreCase("vip2"))
								expire=new Date(config.getLong("votes.vip2expire"));
							else
								expire=null;
							if(expire!=null)
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Rank Expires: "+ChatColor.WHITE+ expire);
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"su_points: "+ChatColor.WHITE+su_points);
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Times Suspected: "+ChatColor.WHITE+timesSuspected);
							if(config.isSet("votes.forgeitem"))
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Forge items owed: "+ChatColor.WHITE+config.getInt("votes.forgeitem"));
							
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Player Kills: "+ChatColor.WHITE+p.getStatistic(Statistic.PLAYER_KILLS));
							
							if(sender.hasPermission("VnllaPlayerInfo.seestatusalt")) 
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Known Alts: "+ChatColor.WHITE+ alts);
							
							long time=0;
							//if online, add time since login
							if(p.isOnline())
								time+=System.currentTimeMillis()-config.getLong("playtime.lastLogin");
							//if player has been on before, add the total time recorded
							if(config.isSet("playtime.totalAllTime")) 
								time+=config.getLong("playtime.totalAllTime");
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Total Playtime: "+ChatColor.WHITE+plugin.makeTimeReadable(time, true));
							
		
							if(config.isSet("playtime.lastLogin")) 
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Login: "+ChatColor.WHITE+plugin.makeTimeReadable(System.currentTimeMillis()-config.getLong("playtime.lastLogin"), false)+" ago");
							
							
							if(p.isOnline()) 
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Online: "+ChatColor.WHITE+"Now");
							else if(config.isSet("playtime.lastLogout")) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Online: "+ChatColor.WHITE+plugin.makeTimeReadable(System.currentTimeMillis()-config.getLong("playtime.lastLogout"), false)+" ago");
							}
							//long code to make it execute /lastlocation [uuid] when the yellow here is pressed
							
							
							TextComponent lastLoc=new TextComponent("Last Location: ");
							lastLoc.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
							TextComponent click=new TextComponent("here");
							ClickEvent c=new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lastlocation "+uuid);
							click.setClickEvent(c);
							click.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
							sender.spigot().sendMessage(lastLoc,click);
							
							} 
							catch(Exception e) {
								sender.sendMessage("Something went wrong...");
							}
								
						}
						else {
							sender.sendMessage(ChatColor.RED+"This player has never logged on... or something is broken");
						}
						if(p.isBanned()) {
							BanEntry ban=plugin.getServer().getBanList(BanList.Type.NAME).getBanEntry(p.getName());
							sender.sendMessage(ChatColor.RED+"Banned: "+ChatColor.RESET+ ban.getCreated());
							sender.sendMessage(ChatColor.RED+"Banned by: "+ChatColor.RESET+ban.getSource());
							sender.sendMessage(ChatColor.RED+"Reason: "+ChatColor.RESET+ban.getReason());
							sender.sendMessage(ChatColor.RED+"Known Alts: "+ChatColor.WHITE+ alts);
						}
		            }
		            
		        }.runTaskAsynchronously(plugin);
		        return true;
			}
		}
		
		
		
		else if(command.getName().equalsIgnoreCase("statusip")) {
				if(args.length==1) {
					new BukkitRunnable() {
			            @Override
						public void run() {
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"IP: "+ChatColor.WHITE+args[0]);
							ArrayList<String> ign=new ArrayList<String>();
							for(String s:plugin.getDB().getUUIDssByIP(args[0])) {
								ign.add(Bukkit.getOfflinePlayer(UUID.fromString(s)).getName());
							}
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"Accounts: "+ChatColor.WHITE+ ign);
							BanList list=plugin.getServer().getBanList(BanList.Type.IP);
							if(list.isBanned(args[0])){
								BanEntry ban=list.getBanEntry(args[0]);
								sender.sendMessage(ChatColor.RED+"Banned: "+ChatColor.RESET+ ban.getCreated());
								sender.sendMessage(ChatColor.RED+"Banned by: "+ChatColor.RESET+ban.getSource());
								sender.sendMessage(ChatColor.RED+"Reason: "+ChatColor.RESET+ban.getReason());
							}
			            }
					}.runTaskAsynchronously(plugin);
					return true;
				}	
		}
		
		else if(command.getName().equalsIgnoreCase("stats")) {
			if(args.length==0) {
				
				if(sender instanceof Player) {
		            	new BukkitRunnable() {
				            @Override
							public void run() {
				            	Player p=(Player) sender;
								Scoreboard main=plugin.getServer().getScoreboardManager().getMainScoreboard();
								FileConfiguration config=plugin.getPlayerConfig(p.getUniqueId().toString());
			            		
								long time=0;
								time+=System.currentTimeMillis()-config.getLong("playtime.lastLogin");
								//if player has been on before, add the total time recorded
								if(config.isSet("playtime.totalAllTime")) 
									time+=config.getLong("playtime.totalAllTime");
			            		
								sender.sendMessage(ChatColor.YELLOW+p.getName()+"'s Stats:");
			            		sender.sendMessage(ChatColor.YELLOW+"Total Votes: "+ChatColor.GREEN+config.getInt("votes.totalVotes"));
			            		sender.sendMessage(ChatColor.YELLOW+"Playtime: "+ChatColor.GREEN+plugin.makeTimeReadable(time, true));
			            		sender.sendMessage(ChatColor.YELLOW+"Kills: "+ChatColor.GREEN+p.getStatistic(Statistic.PLAYER_KILLS));
			            		sender.sendMessage(ChatColor.YELLOW+"Deaths: "+ChatColor.GREEN+p.getStatistic(Statistic.DEATHS));
			            		sender.sendMessage(ChatColor.YELLOW+"Survival Points: "+ChatColor.GREEN+main.getObjective("su_points").getScore(p.getName()).getScore());
				            }
						}.runTaskAsynchronously(plugin);
            	}
            	else
            		sender.sendMessage(ChatColor.RED+"Ur not a player!");
				return true;
			}	
		}
		
		
		
		return false;
	}

}
