package dev.gillin.mc.vnllausermanagement.handlers;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandHandler {
    private final VnllaUserManagement plugin;
    private final Map<String, CommandExecutor> commands;

    public CommandHandler(VnllaUserManagement plugin) {
        this.plugin = plugin;
        this.commands = new HashMap<>();
    }

    public void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            if (executor instanceof TabCompleter) {
                command.setTabCompleter((TabCompleter) executor);
            }
        } else {
            plugin.getLogger().log(Level.WARNING,"The \"{0}\" command was not found. Please check your plugin.yml file.", commandName);
        }
        commands.put(commandName, executor);
    }

    public CommandExecutor getCommandExecutor(String commandName) {
        return commands.get(commandName);
    }
}
