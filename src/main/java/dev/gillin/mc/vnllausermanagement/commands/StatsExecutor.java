package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;


public class StatsExecutor implements CommandExecutor {
    private final VnllaUserManagement plugin;

    public StatsExecutor(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if(command.getName().equalsIgnoreCase("stats")) {
                if(sender instanceof Player) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player p=(Player) sender;
                            Scoreboard main=plugin.getServer().getScoreboardManager().getMainScoreboard();
                            PlayerConfigModel playerConfigModel=PlayerConfigModel.fromUUID(plugin, p.getUniqueId().toString());

                            long time=0;
                            time+=System.currentTimeMillis()-playerConfigModel.getLastLogin();
                            //if player has been on before, add the total time recorded
                            time+=playerConfigModel.getTotalPlaytime();

                            sender.sendMessage(ChatColor.YELLOW + p.getName() + "'s Stats:");
                            sender.sendMessage(ChatColor.YELLOW + "Total Votes: " + ChatColor.GREEN + playerConfigModel.getTotalVotes());
                            sender.sendMessage(ChatColor.YELLOW + "Playtime: " + ChatColor.GREEN + CommonUtilities.makeTimeReadable(time, true));
                            sender.sendMessage(ChatColor.YELLOW + "Kills: " + ChatColor.GREEN + p.getStatistic(Statistic.PLAYER_KILLS));
                            sender.sendMessage(ChatColor.YELLOW + "Deaths: " + ChatColor.GREEN + p.getStatistic(Statistic.DEATHS));
                            //TODO: Server config for objectives
                            if (main.getObjective("su_points") != null)
                                sender.sendMessage(ChatColor.YELLOW + "Survival Points: " + ChatColor.GREEN+main.getObjective("su_points").getScore(p.getName()).getScore());
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else {
                    sender.sendMessage(ChatColor.RED+"Ur not a player!");
                }
                return true;

        }
        return false;
    }
}
