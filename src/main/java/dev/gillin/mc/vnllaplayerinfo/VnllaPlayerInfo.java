package dev.gillin.mc.vnllaplayerinfo;

import dev.gillin.mc.vnllaplayerinfo.database.Database;
import dev.gillin.mc.vnllaplayerinfo.database.PlayerData;
import dev.gillin.mc.vnllaplayerinfo.database.SQLite;
import dev.gillin.mc.vnllaplayerinfo.commands.LastLocationExecutor;
import dev.gillin.mc.vnllaplayerinfo.commands.StatsExecutor;
import dev.gillin.mc.vnllaplayerinfo.commands.StatusExecutor;
import dev.gillin.mc.vnllaplayerinfo.commands.StatusIPExecutor;
import dev.gillin.mc.vnllaplayerinfo.database.SQLiteConnection;
import dev.gillin.mc.vnllaplayerinfo.groups.GroupModel;
import dev.gillin.mc.vnllaplayerinfo.groups.Groups;
import dev.gillin.mc.vnllaplayerinfo.handlers.VoteHandler;
import dev.gillin.mc.vnllaplayerinfo.player.GroupInfo;
import dev.gillin.mc.vnllaplayerinfo.player.PlayerConfigModel;
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

//TODO: Rename VnllaUserManagement
public class VnllaPlayerInfo extends JavaPlugin implements Listener, IVnllaPlayerInfo {
    public static final String FORGE = "forge";
    public static final String GROUP = "group";
    private final VnllaPlayerInfo plugin = this;
    private VoteHandler voteHandler;
    private Groups groups;

    private SQLiteConnection connection;
    private PlayerData playerData;

    final Logger logger = plugin.getLogger();

    public VnllaPlayerInfo() {
    }

    protected VnllaPlayerInfo(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        registerCommand("stats", new StatsExecutor(this));
        registerCommand("status", new StatusExecutor(this));
        registerCommand("statusip", new StatusIPExecutor(this));
        registerCommand("lastlocation", new LastLocationExecutor(this));
        registerCommand("donor", this);
        registerCommand("wipeip", this);

        //TODO: Break this off into different plugin
        TabExecutor forge = new Forge(this);
        registerCommand(FORGE, forge);

        groups = new Groups(this);
        registerCommand(GROUP, groups);


        //initialize data folder
        createPlayerDataDirectory();

        String dbName = getConfig().getString("SQLite.Filename", "defaultname");
        connection = new SQLiteConnection(dbName, getDataFolder());
        playerData = new PlayerData(connection);

        voteHandler = new VoteHandler();
        groups = new Groups(this);

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
        //all off of main thread
        String uuid = event.getPlayer().getUniqueId().toString();
        PlayerConfigModel playerConfigModel=PlayerConfigModel.fromUUID(plugin, uuid);
        if (playerConfigModel.getVotesOwed() > 0) {
            plugin.giveVote(event.getPlayer(), playerConfigModel, playerConfigModel.getVotesOwed());
            playerConfigModel.setVotesOwed(0);

            playerConfigModel.saveConfig(plugin);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                String ip;
                String name = event.getPlayer().getName();
                String uuid = event.getPlayer().getUniqueId().toString();
                //trim to ip from e.g. /127.0.0.1:32673
                try {
                    ip = event.getPlayer().spigot().getRawAddress().toString();
                    ip = ip.substring(ip.indexOf('/') + 1, ip.indexOf(':'));
                } catch (StringIndexOutOfBoundsException e) {
                    ip = "ERROR";
                    logger.log(Level.SEVERE, "Failed to Parse IP", e);
                }

                //save player data for joining player
                PlayerConfigModel playerConfigModel=PlayerConfigModel.fromUUID(plugin,uuid);

                //lose vip if applicable
                plugin.checkLoseGroup(event.getPlayer(), playerConfigModel);

                playerConfigModel.setLastPlayerName(name);
                List<String> ips = playerConfigModel.getIpAddresses();
                if (!ips.contains(ip)) {
                    ips.add(ip);
                }
                playerConfigModel.setLastLogin(System.currentTimeMillis());
                //save changes
                playerConfigModel.saveConfig(plugin);
                playerData.insertPlayerIP(uuid, ip);

                //is one of their alts banned?!
                ArrayList<OfflinePlayer> banned = new ArrayList<>();
                for (String uuids : playerData.getUUIDsByIP(ip)) {
                    //if it's their own uuid, then skip to the next one
                    if (uuids.equalsIgnoreCase(uuid))
                        continue;
                    OfflinePlayer p = getServer().getOfflinePlayer(UUID.fromString(uuids));
                    if (p.isBanned()) {
                        banned.add(p);
                    }
                }

                //tell all op players about their misdeeds
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.isOp()) {
                        for (OfflinePlayer p : banned) {
                            String playerName=p.getName();
                            String reason="";
                            if(playerName != null){
                                 BanEntry banEntry = getServer().getBanList(Type.NAME).getBanEntry(playerName);
                                 if(banEntry != null){
                                    reason = banEntry.getReason();
                                 }
                                 else{
                                     logger.log(Level.SEVERE, "No Ban entry found for Player: {0}", playerName);
                                 }
                            }

                            ClickEvent banPlay = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    String.format("/ban %s Alt of banned player: %s. Banned for %s", name, p.getName(), reason));
                            String[] strings = {p.getName(), " was banned for: ", reason};
                            net.md_5.bungee.api.ChatColor[] colors = {net.md_5.bungee.api.ChatColor.DARK_RED,
                                    net.md_5.bungee.api.ChatColor.RED, net.md_5.bungee.api.ChatColor.YELLOW};

                            TextComponent[] textComponents = new TextComponent[strings.length];
                            for (int x = 0; x < textComponents.length; x++) {
                                textComponents[x] = new TextComponent(strings[x]);
                                textComponents[x].setClickEvent(banPlay);
                                textComponents[x].setColor(colors[x]);
                            }


                            player.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " is an alt of banned player(s): " + ChatColor.DARK_RED + p.getName());
                            player.spigot().sendMessage(textComponents);
                        }
                    }
                }


            }
        }.runTaskAsynchronously(plugin);
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
        } else if (command.getName().equalsIgnoreCase("donor") && args.length == 1) {
            //TODO: reimplement this
            /*OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            FileConfiguration config = plugin.getPlayerConfig(p.getUniqueId().toString());
            config.set("votes.rank", "vip2");
            long vip2 = config.getLong("votes.vip2expire");
            if (vip2 < System.currentTimeMillis())
                vip2 = System.currentTimeMillis() + getConfig().getLong("votes.keeprank") * 1000;
            else
                vip2 += getConfig().getLong("votes.keeprank") * 1000;
            getServer().broadcastMessage(p.getName() + " has made the advancement " + ChatColor.GREEN + "[DONOR!]");
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent removetemp vip2");
            getServer().dispatchCommand(getServer().getConsoleSender(), "lp user " + p.getName() + " parent addtemp vip2 " + vip2 / 1000 + " replace");
            config.set("votes.vip2expire", vip2);
            if (!isStaff(config))
                config.set(GROUP, "vip2");
            config.set("votes.forgeitem", config.getInt("votes.forgeitem") + 1);
            this.savePlayerConfig(config, p.getUniqueId().toString());*/
            return true;
        } else if (command.getName().equalsIgnoreCase("forgegiven") && args.length == 1) {
            //TODO: figure this out
            /*OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            FileConfiguration config = plugin.getPlayerConfig(p.getUniqueId().toString());
            config.set("votes.forgeitem", config.getInt("votes.forgeitem") - 1);
            this.savePlayerConfig(config, p.getUniqueId().toString());*/
            return true;
        } else if (command.getName().equalsIgnoreCase("wipeip") && args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            playerData.deleteIPsByUUID(p.getUniqueId().toString());
            sender.sendMessage(ChatColor.GREEN + "IPs for " + p.getName() + " cleared from the alt detector db!");
            return true;
        }
        return false;
    }

    public void broadcastOPs(String s) {
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.isOp())
                p.sendMessage(ChatColor.RED + s);
        }
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
            getLogger().warning("The '" + commandName + "' command was not found. Please check your plugin.yml file.");
        }
    }

    public void checkLoseGroup(Player p, PlayerConfigModel playerConfigModel) {
        long currentTime = System.currentTimeMillis();
        HashMap<String, GroupInfo> groupInfoMap = playerConfigModel.getGroupInfos();
        for(String groupInfoKey: groupInfoMap.keySet()){
            GroupInfo groupInfo = groupInfoMap.get(groupInfoKey);
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
