package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatusExecutor implements CommandExecutor{
	private final VnllaUserManagement plugin;
	public StatusExecutor(VnllaUserManagement plugin) {
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
						List<String> ips=plugin.getPlayerData().getIPsByUUID(uuid);
						List<String> alts=new ArrayList<>();

						PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);

						//TODO: move to server config
						Scoreboard main=Bukkit.getScoreboardManager().getMainScoreboard();
						int su_points=0;
						int timesSuspected=0;
						Objective suPoints=main.getObjective("su_points");
						Objective timeSuspected=main.getObjective("timesSuspected");
						if(suPoints != null)
							su_points = suPoints.getScore(p.getName()).getScore();
						if(timeSuspected != null)
							timesSuspected = timeSuspected.getScore(p.getName()).getScore();
						
						// only populated alts list if it will be needed
						if(p.isBanned() || sender.hasPermission("VnllaPlayerInfo.seestatusalt")) {
							for (String ip:ips) {
								for(String x:plugin.getPlayerData().getUUIDsByIP(ip)) {
									String name=plugin.getServer().getOfflinePlayer(UUID.fromString(x)).getName();
									if(!x.equalsIgnoreCase(uuid)&&!alts.contains(name)) 
										alts.add(name);
								}
							}
						}
						
						sender.sendMessage(ChatColor.LIGHT_PURPLE+"Name: "+ChatColor.WHITE+p.getName());
						sender.sendMessage(ChatColor.LIGHT_PURPLE+"UUID: "+ChatColor.WHITE+uuid);
						
						
						
						//if player has ever logged in
						if(playerConfigModel.getLastLogin()>0) {
							try {
								//TODO: Should we replace this?
							/*String rank=config.getString("votes.rank");
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"viprank: "+ChatColor.WHITE+rank);
							sender.sendMessage(ChatColor.LIGHT_PURPLE+"group: "+ChatColor.WHITE+config.getString("group"));*/

								sender.sendMessage(ChatColor.LIGHT_PURPLE+"IP: "+ChatColor.WHITE+ips.toString());
							
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Total Votes: "+ChatColor.WHITE+playerConfigModel.getTotalVotes());
								//TODO: replace this
								/*sender.sendMessage(ChatColor.LIGHT_PURPLE+"Vip1 Votes: "+ChatColor.WHITE +pl);
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Vip2 Votes: "+ChatColor.WHITE+config.getInt("votes.vip2Votes"));*/
								/*Date expire;
								if(rank!=null&&rank.equalsIgnoreCase("vip"))
									expire=new Date(config.getLong("votes.vip1expire"));
								else if(rank!=null&&rank.equalsIgnoreCase("vip2"))
									expire=new Date(config.getLong("votes.vip2expire"));
								else
									expire=null;
								if(expire!=null)
									sender.sendMessage(ChatColor.LIGHT_PURPLE+"Rank Expires: "+ChatColor.WHITE+ expire);*/
								//TODO: Server Config
								/*sender.sendMessage(ChatColor.LIGHT_PURPLE+"su_points: "+ChatColor.WHITE+su_points);
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Times Suspected: "+ChatColor.WHITE+timesSuspected);
								if(config.isSet("votes.forgeitem"))
									sender.sendMessage(ChatColor.LIGHT_PURPLE+"Forge items owed: "+ChatColor.WHITE+config.getInt("votes.forgeitem"));
								*/
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Player Kills: "+ChatColor.WHITE+p.getStatistic(Statistic.PLAYER_KILLS));
							
								if(sender.hasPermission("VnllaPlayerInfo.seestatusalt")) {
									sender.sendMessage(ChatColor.LIGHT_PURPLE+"Known Alts: "+ChatColor.WHITE+ alts);
								}
							
								long time = playerConfigModel.getTotalPlaytime();
								//if online, add time since login
								if(p.isOnline()){
									time+=System.currentTimeMillis() - playerConfigModel.getLastLogin();
								}
								sender.sendMessage(ChatColor.LIGHT_PURPLE+"Total Playtime: "+ChatColor.WHITE+ CommonUtilities.makeTimeReadable(time, true));

								if(playerConfigModel.getLastLogin() > 0) {
									sender.sendMessage(ChatColor.LIGHT_PURPLE + "Last Login: " + ChatColor.WHITE + CommonUtilities.makeTimeReadable(System.currentTimeMillis() - playerConfigModel.getLastLogin(), false) + " ago");
								}

								if(p.isOnline()) {
									sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Online: "+ChatColor.WHITE+"Now");
								}
								else if(playerConfigModel.getLastLogout() > 0) {
									sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Online: "+ChatColor.WHITE+CommonUtilities.makeTimeReadable(System.currentTimeMillis() - playerConfigModel.getLastLogout(), false)+" ago");
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
							if (ban != null) {
								sender.sendMessage(ChatColor.RED + "Banned: " + ChatColor.RESET + ban.getCreated());
								sender.sendMessage(ChatColor.RED + "Banned by: " + ChatColor.RESET + ban.getSource());
								sender.sendMessage(ChatColor.RED + "Reason: " + ChatColor.RESET + ban.getReason());
								sender.sendMessage(ChatColor.RED + "Known Alts: " + ChatColor.WHITE + alts);
							} else {
								sender.sendMessage(ChatColor.RED + "Failed to get player ban info" + ChatColor.RESET);
							}

						}
		            }
		            
		        }.runTaskAsynchronously(plugin);
		        return true;
			}
		}
		return false;
	}

}
