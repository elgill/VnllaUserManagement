package dev.gillin.mc.vnllausermanagement.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import dev.gillin.mc.vnllausermanagement.groups.GroupModel;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class LuckPermsHandler {
    private LuckPerms luckPerms;
    private final VnllaUserManagement plugin;

    public LuckPermsHandler(VnllaUserManagement plugin) {
        this.plugin = plugin;
    }

    public void loadLuckPermsIfPresent(PluginManager pluginManager) {
        // Check if LuckPerms is present and enabled, if so load groups
        Plugin luckPermsPlugin = pluginManager.getPlugin("LuckPerms");
        if (luckPermsPlugin != null && luckPermsPlugin.isEnabled()) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
                loadGroups();
            }
        } else {
            Bukkit.getLogger().log(Level.INFO, "LuckPerms not found. Group support disabled.");
        }
    }

    private void loadGroups() {
        for (GroupModel groupModel : plugin.getGroups().getGroupModels()) {

            String groupName = groupModel.getLuckPermsGroupName();
            // Create or update the LuckPerms group
            Group group = luckPerms.getGroupManager().getGroup(groupName);
            if (group == null) {
                try{
                    luckPerms.getGroupManager().createAndLoadGroup(groupName).join();
                } catch (Exception exception){
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to create new LuckPerms Group", exception);
                }
            }
        }
    }

    public void addGroupToPlayer(Player player, String groupName) {
        if(luckPerms == null){
            return;
        }
        UserManager userManager = luckPerms.getUserManager();

        // Load the user using their UUID
        User user;
        try {
            user = userManager.loadUser(player.getUniqueId()).get();
        } catch (ExecutionException | InterruptedException e) {
            Bukkit.getLogger().log(Level.WARNING,"Failed to load user [{0}] from LuckPerms", player.getName());
            Bukkit.getLogger().log(Level.SEVERE, "Exception while loading user from Luckperms");
            e.printStackTrace();
            return;
        }

        // Create a Node representing the group you want to add
        Node groupNode = InheritanceNode.builder(groupName).build();

        // Add the node to the user
        user.data().add(groupNode);

        // Save the user back to the LuckPerms storage
        userManager.saveUser(user);
    }
    public void removeGroupFromPlayer(Player player, String groupName) {
        if(luckPerms == null){
            return;
        }

        UserManager userManager = luckPerms.getUserManager();

        // Load the user using their UUID
        User user;
        try {
            user = userManager.loadUser(player.getUniqueId()).get();
        } catch (ExecutionException | InterruptedException e) {
            Bukkit.getLogger().warning("Failed to load user " + player.getName());
            e.printStackTrace();
            return;
        }

        // Find the Node representing the group you want to remove
        Node groupNode = InheritanceNode.builder(groupName).build();

        // Remove the node from the user
        user.data().remove(groupNode);

        // Save the user back to the LuckPerms storage
        userManager.saveUser(user);
    }
}
