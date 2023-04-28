package dev.gillin.mc.vnllausermanagement;

import dev.gillin.mc.vnllausermanagement.commands.*;
import dev.gillin.mc.vnllausermanagement.database.PlayerData;
import dev.gillin.mc.vnllausermanagement.database.SQLiteConnection;
import dev.gillin.mc.vnllausermanagement.datamodels.ServerConfigModel;
import dev.gillin.mc.vnllausermanagement.events.EventListener;
import dev.gillin.mc.vnllausermanagement.groups.GroupModel;
import dev.gillin.mc.vnllausermanagement.groups.Groups;
import dev.gillin.mc.vnllausermanagement.handlers.VoteHandler;
import dev.gillin.mc.vnllausermanagement.player.GroupInfo;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class VnllaUserManagement extends JavaPlugin implements IVnllaUserManagement {
    private final VnllaUserManagement plugin = this;
    private VoteHandler voteHandler;
    private Groups groups;
    private ServerConfigModel serverConfigModel;
    private SQLiteConnection connection;
    private PlayerData playerData;

    public VnllaUserManagement() {
    }

    protected VnllaUserManagement(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        serverConfigModel = ServerConfigModel.fromConfigFile(getConfig());
        groups = new Groups(this);

        Bukkit.getLogger().log(Level.INFO, "Parsed server config: {0}", serverConfigModel);

        registerCommand("givevote", new GiveVoteExecutor(this));
        registerCommand("group", new GroupCommandExecutor(this));
        registerCommand("stats", new StatsExecutor(this));
        registerCommand("status", new StatusExecutor(this));
        registerCommand("statusip", new StatusIPExecutor(this));
        registerCommand("lastlocation", new LastLocationExecutor(this));
        registerCommand("wipeip", new WipeIpExecutor(this));

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
            handleLeaving(p.getUniqueId().toString(), false);
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


    public String getPlayerIp(Player player){
        String ip = player.spigot().getRawAddress().toString();
        //trim to ip from e.g. /127.0.0.1:32673
        try {
            ip = ip.substring(ip.indexOf('/') + 1, ip.indexOf(':'));
        } catch (StringIndexOutOfBoundsException e) {
            ip = "ERROR";
            Bukkit.getLogger().log(Level.SEVERE, "Failed to Parse IP", e);
        }
        return ip;
    }

    public List<OfflinePlayer> getBannedAlts(String playerIP, String playerUUID) {
        List<OfflinePlayer> banned = new ArrayList<>();
        for (String uuid : playerData.getUUIDsByIP(playerIP)) {
            if (uuid.equalsIgnoreCase(playerUUID))
                continue;
            OfflinePlayer p = getServer().getOfflinePlayer(UUID.fromString(uuid));
            if (p.isBanned()) {
                banned.add(p);
            }
        }
        return banned;
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            if (executor instanceof TabCompleter) {
                command.setTabCompleter((TabCompleter) executor);
            }
        } else {
            getLogger().log(Level.WARNING,"The \"{0}\" command was not found. Please check your plugin.yml file.", commandName);
        }
    }

    public void checkLoseGroup(Player p, PlayerConfigModel playerConfigModel) {
        long currentTime = System.currentTimeMillis();
        Map<String, GroupInfo> groupInfoMap = playerConfigModel.getGroupInfos();
        for(Map.Entry<String, GroupInfo> groupInfoEntry: groupInfoMap.entrySet()){
            String groupInfoKey = groupInfoEntry.getKey();
            GroupInfo groupInfo = groupInfoEntry.getValue();
            if(groupInfo.isActive() && groupInfo.getExpiration() < currentTime){
                groupInfo.setActive(false);
                GroupModel groupModel = groups.getGroupModelByKey(groupInfoKey);
                groups.loseGroup(p, groupModel);
            }
            groupInfoMap.put(groupInfoKey,groupInfo);
        }
        playerConfigModel.setGroupInfos(groupInfoMap);
        playerConfigModel.saveConfig(plugin);
    }

    //had to add a synchronous way because asynchronous doesn't work when server is shutting down
    public void handleLeaving(String uuid, boolean async) {
        Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
        if(player == null){
            return;
        }
        Location loc = player.getLocation();
        if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    doLeavingTasks(uuid, loc);
                }
            }.runTaskAsynchronously(plugin);
        } else {
            doLeavingTasks(uuid, loc);
        }
    }

    private void doLeavingTasks(String uuid, Location loc) {
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);
        long current = System.currentTimeMillis();
        long lastLogin = playerConfigModel.getLastLogin();

        playerConfigModel.setLastLogout(current);
        long currentSessionLength = (current - lastLogin);
        if (currentSessionLength < (1000 * 60 * 60 * 12)) {
            playerConfigModel.setTotalPlaytime(playerConfigModel.getTotalPlaytime() + currentSessionLength);
        }
        //TODO: Investigate why I did LastLocation manually- Location object would be better
        playerConfigModel.setLastLocationX(loc.getX());
        playerConfigModel.setLastLocationY(loc.getY());
        playerConfigModel.setLastLocationZ(loc.getZ());
        World world = loc.getWorld();
        if(world != null){
            playerConfigModel.setLastLocationWorld(loc.getWorld().getName());
        }

        playerConfigModel.saveConfig(plugin);
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

    @Override
    public void createPlayerDataDirectory() {
        this.saveDefaultConfig();
        File playerDataFile = new File(this.getDataFolder() + File.separator + "playerdata");
        if (!playerDataFile.exists() && !playerDataFile.mkdir())
            getLogger().log(Level.SEVERE,"Failed to create PlayerData Directory");
    }
}
