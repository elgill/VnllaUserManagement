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

            GroupModel group = new GroupModel(displayName, votesRequired, rankLength, earnRankCommands.toArray(new String[0]), loseRankCommands.toArray(new String[0]));
            groups.add(group);
        }

        return groups;
    }
}
