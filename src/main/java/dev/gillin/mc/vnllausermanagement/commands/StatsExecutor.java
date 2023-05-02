package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;


public class StatsExecutor implements CommandExecutor {
    private final VnllaUserManagement plugin;

    public StatsExecutor(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    showStatsMessage(sender);
                }
            }.runTaskAsynchronously(plugin);
        }
        else {
            sender.sendMessage(ChatColor.RED + "Ur not a player!");
        }
        return true;
    }

    private void showStatsMessage(@NotNull CommandSender sender) {
        Player p=(Player) sender;
        ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        if(scoreboardManager == null){
            Bukkit.getLogger().log(Level.SEVERE, "Failed to get ScoreboardManager");
            return;
        }
        Scoreboard main=scoreboardManager.getMainScoreboard();
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, p.getUniqueId().toString());

        long time = 0;
        time += System.currentTimeMillis() - playerConfigModel.getLastLogin();
        //if player has been on before, add the total time recorded
        time += playerConfigModel.getTotalPlaytime();

        sender.sendMessage(ChatColor.YELLOW + p.getName() + "'s Stats:");
        sender.sendMessage(ChatColor.YELLOW + "Total Votes: " + ChatColor.GREEN + playerConfigModel.getTotalVotes());
        sender.sendMessage(ChatColor.YELLOW + "Playtime: " + ChatColor.GREEN + CommonUtilities.makeTimeReadable(time, true));
        sender.sendMessage(ChatColor.YELLOW + "Kills: " + ChatColor.GREEN + p.getStatistic(Statistic.PLAYER_KILLS));
        sender.sendMessage(ChatColor.YELLOW + "Deaths: " + ChatColor.GREEN + p.getStatistic(Statistic.DEATHS));

        for(String objectiveName: plugin.getServerConfigModel().getStatsObjectives()){
            Objective objective = main.getObjective(objectiveName);
            if(objective == null){
                Bukkit.getLogger().log(Level.WARNING, "Objective [{0}] was not found", objectiveName);
                continue;
            }
            sender.sendMessage(ChatColor.YELLOW + objective.getDisplayName() + ": " + ChatColor.GREEN + objective.getScore(p.getName()).getScore());
        }
    }
}
