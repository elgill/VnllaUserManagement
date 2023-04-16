package dev.gillin.mc.vnllaplayerinfo.groups;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class GroupSerializer {
    public static List<GroupModel> deserializeGroups(ConfigurationSection groupsSection) {
        List<GroupModel> groups = new ArrayList<>();

        for (String key : groupsSection.getKeys(false)) {
            String displayName = groupsSection.getString(key + ".displayName");
            int votesRequired = groupsSection.getInt(key + ".votesRequired");
            long rankLength = groupsSection.getLong(key + ".rankLength");
            List<String> earnRankCommands = groupsSection.getStringList(key + ".earnRankCommands");
            List<String> loseRankCommands = groupsSection.getStringList(key + ".loseRankCommands");
            List<String> permissions = groupsSection.getStringList(key + ".permissions");
            boolean voteAchievable = groupsSection.getBoolean(key + ".voteAchievable", true); // Default to true

            GroupModel group = new GroupModel(key, displayName, votesRequired, rankLength, earnRankCommands, loseRankCommands, voteAchievable, permissions);
            groups.add(group);
        }

        return groups;
    }
}
