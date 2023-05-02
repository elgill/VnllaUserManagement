package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class GiveVoteExecutor implements CommandExecutor {
    private final VnllaUserManagement plugin;
    public GiveVoteExecutor(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("givevote") && strings.length == 1) {
            String playerInput = strings[0];
            OfflinePlayer player = CommonUtilities.getOfflinePlayerByString(playerInput);

            Bukkit.getLogger().log(Level.INFO, "Vote given to {0}", player.getName());
            PlayerConfigModel playerConfigModel= PlayerConfigModel.fromUUID(plugin, player.getUniqueId().toString());
            if (player.isOnline()) {
                plugin.getVoteHandler().giveVote(plugin, (Player) player, playerConfigModel, 1);
            }
            else {
                playerConfigModel.setVotesOwed(playerConfigModel.getVotesOwed() + 1);
                playerConfigModel.saveConfig(plugin);
            }

            return true;
        }
        return false;
    }
}
