package dev.gillin.mc.vnllausermanagement.groups;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Groups{
	private final List<GroupModel> groupModels;
	private final List<GroupModel> voteGroupModels;

	public Groups(VnllaUserManagement p) {
		Bukkit.getLogger().log(Level.INFO,"Parsing GroupModels... ");
		groupModels = GroupSerializer.deserializeGroups(p.getConfig().getConfigurationSection("groups"));
		voteGroupModels= new ArrayList<>();
		for(GroupModel groupModel: groupModels){
			if(groupModel.isVoteAchievable()){
				Bukkit.getLogger().log(Level.FINE,"Vote Achievable GroupModel: {0}",groupModel);
				voteGroupModels.add(groupModel);
			} else {
				Bukkit.getLogger().log(Level.FINE,"Non Vote Achievable GroupModel: {0}",groupModel);
			}
		}
	}

	public List<GroupModel> getGroupModels() {
		return groupModels;
	}

	public List<GroupModel> getVoteGroupModels() {
		return voteGroupModels;
	}

	public GroupModel getGroupModelByKey(String groupKey) {
		for (GroupModel groupModel : groupModels) {
			if (groupModel.getGroupKey().equalsIgnoreCase(groupKey)) {
				return groupModel;
			}
		}
		return null;
	}

	public void earnGroup(Player p, GroupModel groupModel){
		//TODO: Grant perms
		List<String> earnRankCommands = groupModel.getEarnRankCommands();
		for(String cmd: earnRankCommands){
			cmd = cmd.replace("%PLAYER%", p.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}
	public void loseGroup(Player p, GroupModel groupModel){
		//TODO: Revoke perms
		List<String> loseRankCommands = groupModel.getLoseRankCommands();
		for(String cmd: loseRankCommands){
			cmd = cmd.replace("%PLAYER%", p.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

}
