package com.smartchat.commands;

import com.smartchat.SmartChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    
    private final SmartChat plugin;
    
    public ReloadCommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smartchat.admin.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.permission.command"));
            return true;
        }
        
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.reload.start"));
        plugin.reload();
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.reload.success"));
        
        return true;
    }
}