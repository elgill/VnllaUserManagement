package dev.gillin.mc.vnllausermanagement;

import dev.gillin.mc.vnllausermanagement.commands.*;
import dev.gillin.mc.vnllausermanagement.database.PlayerData;
import dev.gillin.mc.vnllausermanagement.database.SQLiteConnection;
import dev.gillin.mc.vnllausermanagement.groups.GroupModel;
import dev.gillin.mc.vnllausermanagement.groups.Groups;
import dev.gillin.mc.vnllausermanagement.handlers.VoteHandler;
import dev.gillin.mc.vnllausermanagement.player.GroupInfo;
import dev.gillin.mc.vnllausermanagement.player.PlayerConfigModel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VnllaUserManagement extends JavaPlugin implements Listener, IVnllaUserManagement {
    private final VnllaUserManagement plugin = this;
    private VoteHandler voteHandler;
    private Groups groups;

    private SQLiteConnection connection;
    private PlayerData playerData;

    final Logger logger = plugin.getLogger();

    public VnllaUserManagement() {
    }

    protected VnllaUserManagement(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        groups = new Groups(this);

        registerCommand("group", new GroupCommandExecutor(this));
        registerCommand("stats", new StatsExecutor(this));
        registerCommand("status", new StatusExecutor(this));
        registerCommand("statusip", new StatusIPExecutor(this));
        registerCommand("lastlocation", new LastLocationExecutor(this));
        registerCommand("donor", this);
        registerCommand("wipeip", this);

        //initialize data folder
        createPlayerDataDirectory();

        //TODO Test This-file name doesnt work
        String dbName = getConfig().getString("sqlite.filename", "defaultsqlfilename");
        connection = new SQLiteConnection(dbName, getDataFolder());
        playerData = new PlayerData(connection);

        voteHandler = new VoteHandler();

        //set all players as logged in
        for (Player p : getServer().getOnlinePlayers()) {
            PlayerConfigModel playerConfigModel=PlayerConfigModel.fromUUID(plugin, p.getUniqueId().toString());
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


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        String uuid = joiningPlayer.getUniqueId().toString();
        PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);

        handleVotesOwed(joiningPlayer, playerConfigModel);

        new BukkitRunnable() {
            @Override
            public void run() {
                String ip = getPlayerIp(joiningPlayer);
                updatePlayerConfigModel(ip, joiningPlayer, playerConfigModel);
                playerData.insertPlayerIP(uuid, ip);

                //is one of their alts banned?!
                List<OfflinePlayer> banned = getBannedAlts(ip, uuid);

                sendAltWarning(banned, joiningPlayer);
            }
        }.runTaskAsynchronously(plugin);
    }

    private void sendAltWarning(List<OfflinePlayer> banned, Player joiningPlayer) {
        for (OfflinePlayer bannedPlayer : banned) {
            String playerName = bannedPlayer.getName();

            if (playerName == null) {
                logger.log(Level.SEVERE, "Banned player name is null");
                return;
            }
            BanEntry banEntry = getServer().getBanList(Type.NAME).getBanEntry(playerName);
            if (banEntry == null) {
                logger.log(Level.SEVERE, "No Ban entry found for Player: {0}", playerName);
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

    private void updatePlayerConfigModel(String ip, Player joiningPlayer, PlayerConfigModel playerConfigModel) {
        String name = joiningPlayer.getName();

        //lose vip if applicable
        plugin.checkLoseGroup(joiningPlayer, playerConfigModel);

        for(String earnGroupKey: playerConfigModel.getPendingEarnedGroups()){
            groups.earnGroup(joiningPlayer, groups.getGroupModelByKey(earnGroupKey));
        }
        for(String loseGroupKey: playerConfigModel.getPendingLostGroups()){
            groups.loseGroup(joiningPlayer, groups.getGroupModelByKey(loseGroupKey));
        }
        playerConfigModel.setPendingEarnedGroups(new ArrayList<>());
        playerConfigModel.setPendingLostGroups(new ArrayList<>());

        playerConfigModel.setLastPlayerName(name);
        List<String> ips = playerConfigModel.getIpAddresses();
        if (!ips.contains(ip)) {
            ips.add(ip);
        }
        playerConfigModel.setLastLogin(System.currentTimeMillis());
        //save changes
        playerConfigModel.saveConfig(plugin);
    }

    private void handleVotesOwed(Player joiningPlayer, PlayerConfigModel playerConfigModel) {
        if (playerConfigModel.getVotesOwed() > 0) {
            Bukkit.getLogger().log(Level.FINE, "Applying Votes owed to {0}", joiningPlayer.getName());
            plugin.giveVote(joiningPlayer, playerConfigModel, playerConfigModel.getVotesOwed());
            playerConfigModel.setVotesOwed(0);
            playerConfigModel.saveConfig(plugin);
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

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        //handle leaving events asynchronously, as to not lag server
        handleLeaving(event.getPlayer().getUniqueId().toString(), true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("givevote") && args.length == 1) {
            String playerInput = args[0];
            OfflinePlayer p = CommonUtilities.getOfflinePlayerByString(playerInput);

            logger.log(Level.INFO, "Vote given to {0}", p.getName());
            PlayerConfigModel playerConfigModel= PlayerConfigModel.fromUUID(plugin,p.getUniqueId().toString());
            if (p.isOnline()) {
                giveVote((Player) p, playerConfigModel, 1);
            }
            else {
                playerConfigModel.setVotesOwed(playerConfigModel.getVotesOwed() + 1);
                playerConfigModel.saveConfig(plugin);
            }

            return true;
        } else if (command.getName().equalsIgnoreCase("wipeip") && args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            playerData.deleteIPsByUUID(p.getUniqueId().toString());
            sender.sendMessage(ChatColor.GREEN + "IPs for " + p.getName() + " cleared from the alt detector db!");
            return true;
        }
        return false;
    }

    private List<OfflinePlayer> getBannedAlts(String playerIP, String playerUUID) {
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

    public void giveVote(Player p, PlayerConfigModel playerConfigModel, int num) {
        voteHandler.giveVote(plugin,p,playerConfigModel,num);
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

    //TODO not great code but it gets the job done - Check if this works now
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

    @Override
    public void createPlayerDataDirectory() {
        this.saveDefaultConfig();
        File playerDataFile = new File(this.getDataFolder() + File.separator + "playerdata");
        if (!playerDataFile.exists() && !playerDataFile.mkdir())
            getLogger().log(Level.SEVERE,"Failed to create PlayerData Directory");
    }
}
