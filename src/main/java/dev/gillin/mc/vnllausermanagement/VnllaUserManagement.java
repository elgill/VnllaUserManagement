package dev.gillin.mc.vnllausermanagement;

import dev.gillin.mc.vnllausermanagement.commands.*;
import dev.gillin.mc.vnllausermanagement.database.PlayerData;
import dev.gillin.mc.vnllausermanagement.database.SQLiteConnection;
import dev.gillin.mc.vnllausermanagement.datamodels.ServerConfigModel;
import dev.gillin.mc.vnllausermanagement.events.PluginEventListener;
import dev.gillin.mc.vnllausermanagement.groups.GroupModel;
import dev.gillin.mc.vnllausermanagement.groups.Groups;
import dev.gillin.mc.vnllausermanagement.handlers.CommandHandler;
import dev.gillin.mc.vnllausermanagement.handlers.LuckPermsHandler;
import dev.gillin.mc.vnllausermanagement.handlers.VoteHandler;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class VnllaUserManagement extends JavaPlugin implements IVnllaUserManagement {
    private final VnllaUserManagement plugin = this;
    private VoteHandler voteHandler;
    private Groups groups;
    private ServerConfigModel serverConfigModel;
    private SQLiteConnection connection;
    private PlayerData playerData;
    private PluginEventListener pluginEventListener;
    private LuckPermsHandler luckPermsHandler;
    public VnllaUserManagement() {
    }

    protected VnllaUserManagement(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {

        pluginEventListener = new PluginEventListener(this);
        serverConfigModel = ServerConfigModel.fromConfigFile(getConfig());
        groups = new Groups(this);

        PluginManager pluginManager = Bukkit.getPluginManager();

        luckPermsHandler = new LuckPermsHandler(this);
        luckPermsHandler.loadLuckPermsIfPresent(pluginManager);

        pluginManager.registerEvents(pluginEventListener, this);

        Bukkit.getLogger().log(Level.INFO, "Parsed server config: {0}", serverConfigModel);

        CommandHandler commandHandler = new CommandHandler(this);

        commandHandler.registerCommand("givevote", new GiveVoteExecutor(this));
        commandHandler.registerCommand("group", new GroupCommandExecutor(this));
        commandHandler.registerCommand("stats", new StatsExecutor(this));
        commandHandler.registerCommand("status", new StatusExecutor(this));
        commandHandler.registerCommand("statusip", new StatusIPExecutor(this));
        commandHandler.registerCommand("lastlocation", new LastLocationExecutor(this));
        commandHandler.registerCommand("wipeip", new WipeIpExecutor(this));

        //initialize data folder
        createPlayerDataDirectory();

        connection = new SQLiteConnection(serverConfigModel.getSqliteFileName(), getDataFolder());
        playerData = new PlayerData(connection);

        voteHandler = new VoteHandler();

        //set all players as logged in
        for (Player player : getServer().getOnlinePlayers()) {
            PlayerConfigModel playerConfigModel=PlayerConfigModel.fromUUID(plugin, player.getUniqueId().toString());
            playerConfigModel.setLastLogin(System.currentTimeMillis());
            playerConfigModel.saveConfig(plugin);
        }
    }

    @Override
    public void onDisable() {
        Collection<? extends Player> playerList=getServer().getOnlinePlayers();
        //log leave times for players on /stop
        for (Player p : playerList) {
            pluginEventListener.getPlayerQuitHandler().handleLeaving(p.getUniqueId().toString(), false);
        }
        if (connection != null) {
            connection.close();
        }
    }

    public void sendAltWarning(List<OfflinePlayer> banned, Player joiningPlayer) {
        for (OfflinePlayer bannedPlayer : banned) {
            String playerName = bannedPlayer.getName();

            if (playerName == null) {
                Bukkit.getLogger().log(Level.SEVERE, "Banned player name is null");
                return;
            }
            BanEntry banEntry = getServer().getBanList(Type.NAME).getBanEntry(playerName);
            if (banEntry == null) {
                Bukkit.getLogger().log(Level.SEVERE, "No Ban entry found for Player: {0}", playerName);
                return;
            }

            String reason = banEntry.getReason();

            ClickEvent banPlay = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                    String.format("/ban %s Alt of banned player: %s. Banned for %s",
                            joiningPlayer.getName(), bannedPlayer.getName(), reason));
            String[] strings = {bannedPlayer.getName(), " was banned for: ", reason};
            net.md_5.bungee.api.ChatColor[] colors = {net.md_5.bungee.api.ChatColor.DARK_RED,
                    net.md_5.bungee.api.ChatColor.RED, net.md_5.bungee.api.ChatColor.YELLOW};

            TextComponent[] textComponents = new TextComponent[strings.length];
            for (int x = 0; x < textComponents.length; x++) {
                textComponents[x] = new TextComponent(strings[x]);
                textComponents[x].setClickEvent(banPlay);
                textComponents[x].setColor(colors[x]);
            }

            Bukkit.getLogger().log(Level.WARNING, () -> String.format("%s is an alt of banned player(s): %s",
                    joiningPlayer.getName(), bannedPlayer.getName()));

            Bukkit.getLogger().log(Level.INFO, () -> String.format("%s was banned for: %s",
                    bannedPlayer.getName(), reason));

            for (Player player : getServer().getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.YELLOW + joiningPlayer.getName() + ChatColor.RED +
                            " is an alt of banned player(s): " + ChatColor.DARK_RED + bannedPlayer.getName());
                    player.spigot().sendMessage(textComponents);
                }
            }
        }

    }


    public List<OfflinePlayer> getBannedAlts(String playerIP, String playerUUID) {
        return playerData.getUUIDsByIP(playerIP).stream()
                .filter(uuid -> !uuid.equalsIgnoreCase(playerUUID))
                .map(uuid -> getServer().getOfflinePlayer(UUID.fromString(uuid)))
                .filter(OfflinePlayer::isBanned)
                .collect(Collectors.toList());
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Groups getGroups() {
        return groups;
    }

    public ServerConfigModel getServerConfigModel() {
        return serverConfigModel;
    }

    public VoteHandler getVoteHandler() {
        return voteHandler;
    }

    public LuckPermsHandler getLuckPermsHandler() {
        return luckPermsHandler;
    }

    @Override
    public void createPlayerDataDirectory() {
        this.saveDefaultConfig();
        File playerDataFile = new File(this.getDataFolder() + File.separator + "playerdata");
        if (!playerDataFile.exists() && !playerDataFile.mkdir())
            getLogger().log(Level.SEVERE,"Failed to create PlayerData Directory");
    }
}
