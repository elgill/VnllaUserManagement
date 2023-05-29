package dev.gillin.mc.vnllausermanagement.groups;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class GroupSerializer {
    private GroupSerializer(){}

    public static List<GroupModel> deserializeGroups(ConfigurationSection groupsSection) {
        List<GroupModel> groups = new ArrayList<>();
        if(groupsSection == null){
            return groups;
        }

        for (String key : groupsSection.getKeys(false)) {
            String displayName = groupsSection.getString(key + ".displayName");
            int votesRequired = groupsSection.getInt(key + ".votesRequired");
            long rankLength = groupsSection.getLong(key + ".rankLength");
            List<String> earnRankCommands = groupsSection.getStringList(key + ".earnRankCommands");
            List<String> loseRankCommands = groupsSection.getStringList(key + ".loseRankCommands");
            String luckPermGroupName = groupsSection.getString(key + ".luckPermGroupName");
            boolean voteAchievable = groupsSection.getBoolean(key + ".voteAchievable", true); // Default to true

            GroupModel groupModel = new GroupModel(key);
            groupModel.setDisplayName(displayName);
            groupModel.setVotesRequired(votesRequired);
            groupModel.setRankLength(rankLength);
            groupModel.setEarnRankCommands(earnRankCommands);
            groupModel.setLoseRankCommands(loseRankCommands);
            groupModel.setVoteAchievable(voteAchievable);
            groupModel.setLuckPermsGroupName(luckPermGroupName);

            groups.add(groupModel);
        }

        return groups;
    }
}
