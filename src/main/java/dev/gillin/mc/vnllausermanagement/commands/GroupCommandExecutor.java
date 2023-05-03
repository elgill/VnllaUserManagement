package dev.gillin.mc.vnllausermanagement.commands;

import dev.gillin.mc.vnllausermanagement.CommonUtilities;
import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.groups.GroupModel;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupCommandExecutor implements TabExecutor {
    private final VnllaUserManagement plugin;
    public GroupCommandExecutor(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
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
            for(GroupModel groupModel:plugin.getGroups().getGroupModels()){
                if(!groupModel.isVoteAchievable()){
                    possibles.add(groupModel.getGroupKey());
                }
            }
            StringUtil.copyPartialMatches(args[2], possibles, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        // /group ExamplePlayer add mod
        if(args.length==3) {
            return handleGroupCommand(args);
        }
        return false;
    }

    private boolean handleGroupCommand(String[] args) {
        OfflinePlayer player= CommonUtilities.getOfflinePlayerByString(args[0]);
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, player.getUniqueId().toString());
        String addOrRemove = args[1];
        GroupModel groupModel = plugin.getGroups().getGroupModelByKey(args[2].toLowerCase());
        if(groupModel == null){
            return false;
        }
        if (addOrRemove.equalsIgnoreCase("add")){
            handleAddGroup(player, playerConfigModel, groupModel);
        } else if (addOrRemove.equalsIgnoreCase("remove")) {
            handleLoseGroup(player, playerConfigModel, groupModel);
        } else {
            return false;
        }
        return true;
    }

    private void handleLoseGroup(OfflinePlayer player, PlayerConfigModel playerConfigModel, GroupModel groupModel) {
        if(player.isOnline()){
            plugin.getGroups().loseGroup(player.getPlayer(), groupModel);
        } else {
            playerConfigModel.getPendingLostGroups().add(groupModel.getGroupKey());
            playerConfigModel.saveConfig(plugin);
        }
    }

    private void handleAddGroup(OfflinePlayer player, PlayerConfigModel playerConfigModel, GroupModel groupModel) {
        if(player.isOnline()){
            plugin.getGroups().earnGroup(player.getPlayer(), groupModel);
        } else {
            playerConfigModel.getPendingEarnedGroups().add(groupModel.getGroupKey());
            playerConfigModel.saveConfig(plugin);
        }
    }

}
