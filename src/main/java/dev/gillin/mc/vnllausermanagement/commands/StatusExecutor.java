package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.GroupInfo;
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
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class StatusExecutor implements CommandExecutor{
	private final VnllaUserManagement plugin;

	public StatusExecutor(VnllaUserManagement plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String commandLabel, String[] args) {
		if(command.getName().equalsIgnoreCase("status")&&sender.hasPermission("VnllaPlayerInfo.seestatus")) {
			if(args.length==1) {
				new BukkitRunnable() {
					@Override
					public void run() {
						printStatusMessage(args, sender);
					}

				}.runTaskAsynchronously(plugin);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void printStatusMessage(String[] args, CommandSender sender) {
		OfflinePlayer offlinePlayer = CommonUtilities.getOfflinePlayerByString(args[0]);
		String playerName = offlinePlayer.getName();
		String uuid = offlinePlayer.getUniqueId().toString();
		List<String> ips=plugin.getPlayerData().getIPsByUUID(uuid);
		List<String> alts = getPlayerAlts(sender, offlinePlayer, uuid, ips);

		PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);

		sender.sendMessage(ChatColor.LIGHT_PURPLE+"Name: "+ChatColor.WHITE + offlinePlayer.getName());
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"UUID: "+ChatColor.WHITE + uuid);

		//if player has ever logged in
		if(playerConfigModel.getLastLogin()>0) {
			existingPlayerSections(sender, offlinePlayer, uuid, ips, alts, playerConfigModel);
		} else {
			sender.sendMessage(ChatColor.RED+"This player has never logged on..");
		}
		if(offlinePlayer.isBanned()) {
			bannedAltsSection(sender, playerName, alts);
		}
	}

	private void existingPlayerSections(CommandSender sender, OfflinePlayer offlinePlayer, String uuid, List<String> ips, List<String> alts, PlayerConfigModel playerConfigModel) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"Non Vote Groups: "+ChatColor.WHITE+ playerConfigModel.getGroups().toString());

		for(Map.Entry<String, GroupInfo> groupInfoEntry:playerConfigModel.getGroupInfos().entrySet()){
			String groupKey = groupInfoEntry.getKey();
			GroupInfo groupInfo = groupInfoEntry.getValue();
		}

		//TODO: Pending lose and earn

		sender.sendMessage(ChatColor.LIGHT_PURPLE+"IP: "+ChatColor.WHITE+ ips.toString());
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"Total Votes: "+ChatColor.WHITE+ playerConfigModel.getTotalVotes());

		statusObjectives(offlinePlayer, sender);
		statistics(sender, offlinePlayer);
		knownAlts(sender, alts);
		playtimeSection(sender, offlinePlayer, playerConfigModel);
		lastLocation(uuid, sender);
	}

	private List<String> getPlayerAlts(CommandSender sender, OfflinePlayer offlinePlayer, String uuid, List<String> ips) {
		List<String> alts = new ArrayList<>();
		// only populated alts list if it will be needed
		if(offlinePlayer.isBanned() || sender.hasPermission("VnllaPlayerInfo.seestatusalt")) {
			for (String ip: ips) {
				for(String x:plugin.getPlayerData().getUUIDsByIP(ip)) {
					String name=plugin.getServer().getOfflinePlayer(UUID.fromString(x)).getName();
					if(!x.equalsIgnoreCase(uuid)&&!alts.contains(name))
						alts.add(name);
				}
			}
		}
		return alts;
	}

	private static void knownAlts(CommandSender sender, List<String> alts) {
		if(sender.hasPermission("VnllaPlayerInfo.seestatusalt")) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE+"Known Alts: "+ChatColor.WHITE+ alts);
		}
	}

	private static void statistics(CommandSender sender, OfflinePlayer offlinePlayer) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"Player Kills: "+ChatColor.WHITE+ offlinePlayer.getStatistic(Statistic.PLAYER_KILLS));
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"Player Deaths: "+ChatColor.WHITE+ offlinePlayer.getStatistic(Statistic.DEATHS));
	}

	private static void playtimeSection(CommandSender sender, OfflinePlayer offlinePlayer, PlayerConfigModel playerConfigModel) {
		long time = playerConfigModel.getTotalPlaytime();
		//if online, add time since login
		if(offlinePlayer.isOnline()){
			time+=System.currentTimeMillis() - playerConfigModel.getLastLogin();
		}
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"Total Playtime: "+ChatColor.WHITE+ CommonUtilities.makeTimeReadable(time, true));

		if(playerConfigModel.getLastLogin() > 0) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Last Login: " + ChatColor.WHITE + CommonUtilities.makeTimeReadable(System.currentTimeMillis() - playerConfigModel.getLastLogin(), false) + " ago");
		}

		if(offlinePlayer.isOnline()) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Online: "+ChatColor.WHITE+"Now");
		} else if(playerConfigModel.getLastLogout() > 0) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE+"Last Online: "+ChatColor.WHITE+CommonUtilities.makeTimeReadable(System.currentTimeMillis() - playerConfigModel.getLastLogout(), false)+" ago");
		}
	}

	private void bannedAltsSection(CommandSender sender, String playerName, List<String> alts) {
		BanEntry ban=plugin.getServer().getBanList(BanList.Type.NAME).getBanEntry(playerName);
		if (ban != null) {
			sender.sendMessage(ChatColor.RED + "Banned: " + ChatColor.RESET + ban.getCreated());
			sender.sendMessage(ChatColor.RED + "Banned by: " + ChatColor.RESET + ban.getSource());
			sender.sendMessage(ChatColor.RED + "Reason: " + ChatColor.RESET + ban.getReason());
			sender.sendMessage(ChatColor.RED + "Known Alts: " + ChatColor.WHITE + alts);
		} else {
			sender.sendMessage(ChatColor.RED + "Failed to get player ban info" + ChatColor.RESET);
		}
	}

	private static void lastLocation(String uuid, CommandSender sender) {
		TextComponent lastLoc=new TextComponent("Last Location: ");
		lastLoc.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
		TextComponent click=new TextComponent("here");
		ClickEvent c=new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/lastlocation "+ uuid);
		click.setClickEvent(c);
		click.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		sender.spigot().sendMessage(lastLoc,click);
	}

	private void statusObjectives(OfflinePlayer p, CommandSender sender) {
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		if(scoreboardManager == null){
			Bukkit.getLogger().log(Level.SEVERE, "Failed to retrieve ScoreboardManager");
			return;
		}
		String playerName = p.getName();
		if (playerName == null){
			return;
		}
		Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
		for(String objectiveName: plugin.getServerConfigModel().getStatusObjectives()){
			Objective objective = mainScoreboard.getObjective(objectiveName);
			if(objective == null){
				Bukkit.getLogger().log(Level.WARNING, "Objective {0} does not exist!", objectiveName);
				continue;
			}
			int objectiveScore = objective.getScore(p.getName()).getScore();
			sender.sendMessage(ChatColor.LIGHT_PURPLE + objectiveName + ChatColor.WHITE + objectiveScore);
		}
	}

}
