package dev.gillin.mc.vnllaplayerinfo;

import dev.gillin.mc.vnllaplayerinfo.Database.Database;
import dev.gillin.mc.vnllaplayerinfo.Database.SQLite;
import dev.gillin.mc.vnllaplayerinfo.commands.LastLocationExecutor;
import dev.gillin.mc.vnllaplayerinfo.commands.StatsExecutor;
import dev.gillin.mc.vnllaplayerinfo.commands.StatusExecutor;
import dev.gillin.mc.vnllaplayerinfo.commands.StatusIPExecutor;
import dev.gillin.mc.vnllaplayerinfo.handlers.VoteHandler;
import dev.gillin.mc.vnllaplayerinfo.player.PlayerConfigModel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanList.Type;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VnllaPlayerInfo extends JavaPlugin implements Listener, IVnllaPlayerInfo {
    public static final String FORGE = "forge";
    public static final String GROUP = "group";
    private final VnllaPlayerInfo plugin = this;
    private VoteHandler voteHandler;
    private Groups groups;
    private Database db;

    final Logger logger = plugin.getLogger();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        getCommand("stats").setExecutor(new StatsExecutor(this));
        getCommand("status").setExecutor(new StatusExecutor(this));
        getCommand("statusip").setExecutor(new StatusIPExecutor(this));
        getCommand("lastlocation").setExecutor(new LastLocationExecutor(this));
        getCommand("donor").setExecutor(this);
        getCommand("wipeip").setExecutor(this);

        TabExecutor forge = new Forge(this);
        getCommand(FORGE).setExecutor(forge);
        getCommand(FORGE).setTabCompleter(forge);

        TabExecutor groups = new Groups(this);
        getCommand(GROUP).setExecutor(groups);
        getCommand(GROUP).setTabCompleter(groups);

        //initialize data folder
        createPlayerDataDirectory();


        //load db
        this.db = new SQLite(this);
        this.db.load();

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
        //log leave times for players on /stop
        for (Player p : getServer().getOnlinePlayers()) {
            handleLeaving(p.getUniqueId().toString(), false);
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            //all off of main thread
            String uuid = event.getPlayer().getUniqueId().toString();
            PlayerConfigModel playerConfigModel=PlayerConfigModel.fromUUID(plugin, uuid);
            //FileConfiguration config = plugin.getPlayerConfig(uuid);
            if (playerConfigModel.getVotesOwed() > 0) {
                //TODO: new thread
                plugin.giveVote(event.getPlayer(), playerConfigModel, playerConfigModel.getVotesOwed());
                playerConfigModel.setVotesOwed(0);

                playerConfigModel.saveConfig(plugin);
            }
            try {
                plugin.updateRank(event.getPlayer(), plugin.getPlayerConfig(uuid));
            } catch (Exception e) {
                plugin.broadcastOPs("Error while logging in#0.5 " + e);
            }
        } catch (Exception e) {
            plugin.broadcastOPs("Error while logging in#1 " + e);
        }

        try {
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
                        ip = "Error #1";
                    } catch (Exception ex) {
                        ip = "Error #2";
                    }

                    //save player data for joining player
                    FileConfiguration config = plugin.getPlayerConfig(uuid);
                    if (!config.isSet(GROUP)) {
                        config.set(GROUP, "default");
                    }


                    //TODO: new thread
                    //lose vip if applicable


                    config.set("lastPlayerName", name);
                    List<String> ips = config.getStringList("ips");
                    if (!ips.contains(ip)) {
                        ips.add(ip);
                        config.set("ips", ips);
                    }
                    config.set("playtime.lastLogin", System.currentTimeMillis());
                    //save changes
                    plugin.savePlayerConfig(config, uuid);
                    db.insertTokens(uuid, ip);

                    //is one of their alts banned?!
                    ArrayList<OfflinePlayer> banned = new ArrayList<>();
                    for (String uuids : db.getUUIDssByIP(ip)) {
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
                                net.md_5.bungee.api.ChatColor[] colors = {net.md_5.bungee.api.ChatColor.DARK_RED, net.md_5.bungee.api.ChatColor.RED, net.md_5.bungee.api.ChatColor.YELLOW};

                                TextComponent[] textComponents = new TextComponent[strings.length];
                                for (int x = 0; x < textComponents.length; x++) {
                                    textComponents[x] = new TextComponent(strings[x]);
                                    textComponents[x].setClickEvent(banPlay);
                                    textComponents[x].setColor(colors[x]);
                                }


                                player.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " is an alt of banned player(s): " + ChatColor.DARK_RED + p.getName());
                                player.spigot().sendMessage(textComponents);
                                //player.sendMessage(ChatColor.DARK_RED+p.getName()+ChatColor.RED+" was banned for: "+ChatColor.YELLOW+reason);
                            }
                        }
                    }


                }
            }.runTaskAsynchronously(plugin);
        } catch (Exception e) {
            plugin.broadcastOPs("Error in login#2");
        }
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
            OfflinePlayer p;
            String playerInput = args[0];
            if(CommonUtilities.isValidUUID(playerInput)){
                p = getServer().getOfflinePlayer(UUID.fromString(args[0]));
            } else {
                p = getServer().getOfflinePlayer(args[0]);
            }

            logger.log(Level.INFO, "Vote given to {0}", p.getName());
            PlayerConfigModel playerConfigModel= PlayerConfigModel.fromUUID(plugin,p.getUniqueId().toString());
            if (p.isOnline())
                giveVote((Player) p, playerConfigModel, 1);
            else {
                playerConfigModel.setVotesOwed(playerConfigModel.getVotesOwed()+1);
                playerConfigModel.saveConfig(plugin);
            }

            return true;
        } else if (command.getName().equalsIgnoreCase("donor") && args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
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
            this.savePlayerConfig(config, p.getUniqueId().toString());
            return true;
        } else if (command.getName().equalsIgnoreCase("forgegiven") && args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            FileConfiguration config = plugin.getPlayerConfig(p.getUniqueId().toString());
            config.set("votes.forgeitem", config.getInt("votes.forgeitem") - 1);
            this.savePlayerConfig(config, p.getUniqueId().toString());
            return true;
        } else if (command.getName().equalsIgnoreCase("wipeip") && args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            this.db.wipeIP(p.getUniqueId().toString());
            sender.sendMessage(ChatColor.GREEN + "IPs for " + p.getName() + " cleared from the alt detector db!");
            return true;
        }
        return false;
    }


    //returns file from playerdata folder in plugin folder
    public File getPlayerFile(String uuid) {
        File f = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                if(f.createNewFile())
                    logger.log(Level.INFO, "File created: {0}.yml", uuid);
                else
                    logger.log(Level.SEVERE,"Failed to create file: {0}.yml", uuid);

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Me failed to make the new player file, me sorry :(", e);
            }
        }
        return f;
    }


    public FileConfiguration getPlayerConfig(String uuid) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
        if (!config.isSet("uuid")) {
            config.set("uuid", uuid);
        }
        if (!config.isSet(GROUP)) {
            config.set(GROUP, "default");
        }
        return config;
    }

    //save changes in config file
    public boolean savePlayerConfig(FileConfiguration config, String uuid) {
        try {
            config.save(new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Me failed to write changes to the player file, me sorry :(", e);
        }
        return true;
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

    public boolean isStaff(FileConfiguration config) {
        String group = config.getString(GROUP);
        return group != null && (group.equals("mod") || group.equals("admin") || group.equals("owner"));
    }

    public void updateRank(Player p, FileConfiguration config) {
        long vip2expire = config.getLong("votes.vip2expire");
        long vip1expire = config.getLong("votes.vip1expire");
        long current = System.currentTimeMillis();
        String originalRank = config.getString("votes.rank");
        String rank = config.getString("votes.rank");

        //if vip2 expires
        if (rank != null && rank.equalsIgnoreCase("vip2") && vip2expire < current) {
            //if vip1 still valid
            if (vip1expire > current) {
                rank = "vip";
            } else {
                rank = "default";
            }
        }
        if (rank != null && rank.equalsIgnoreCase("vip") && vip1expire < current) {
            rank = "default";
        }
        if (rank != null && !originalRank.equals(rank))
            new Groups(plugin).votesRankChange(p, rank, config);
        config.set("votes.rank", rank);
        this.savePlayerConfig(config, p.getUniqueId().toString());
    }

    //TODO not great code but it gets the job done
    //had to add a synchronous way because asynchronous doesn't work when server is shutting down
    public void handleLeaving(String uuid, boolean async) {
        Location loc = plugin.getServer().getPlayer(UUID.fromString(uuid)).getLocation();
        if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);
                    long current = System.currentTimeMillis();
                    long lastLogin = playerConfigModel.getLastLogin();

                    playerConfigModel.setLastLogout(current);
                    long add = (current - lastLogin);
                    if (add < (1000 * 60 * 60 * 12))
                        playerConfigModel.setTotalPlaytime(playerConfigModel.getTotalPlaytime()+add);

                    playerConfigModel.setLastLocationX(loc.getX());
                    playerConfigModel.setLastLocationY(loc.getY());
                    playerConfigModel.setLastLocationZ(loc.getZ());
                    playerConfigModel.setLastLocationWorld(loc.getWorld().getName());

                    playerConfigModel.saveConfig(plugin);

                }
            }.runTaskAsynchronously(plugin);
        } else {
            PlayerConfigModel playerConfigModel = PlayerConfigModel.fromUUID(plugin, uuid);
            long current = System.currentTimeMillis();
            long lastLogin = playerConfigModel.getLastLogin();

            playerConfigModel.setLastLogout(current);
            long add = (current - lastLogin);
            if (add < (1000 * 60 * 60 * 12))
                playerConfigModel.setTotalPlaytime(playerConfigModel.getTotalPlaytime()+add);

            playerConfigModel.setLastLocationX(loc.getX());
            playerConfigModel.setLastLocationY(loc.getY());
            playerConfigModel.setLastLocationZ(loc.getZ());
            playerConfigModel.setLastLocationWorld(loc.getWorld().getName());

            playerConfigModel.saveConfig(plugin);
        }
    }

    //actually completely useless
    public Database getDB() {
        return this.db;
    }

    @Override
    public void createPlayerDataDirectory() {
        this.saveDefaultConfig();
        File playerDataFile = new File(this.getDataFolder() + File.separator + "playerdata");
        if (!playerDataFile.exists() && !playerDataFile.mkdir())
            getLogger().log(Level.SEVERE,"Failed to create PlayerData Directory");
    }
}
