package com.smartchat.commands;

import com.smartchat.SmartChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatsCommand implements CommandExecutor {
    
    private final SmartChat plugin;
    
    public StatsCommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smartchat.admin.stats")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.permission.command"));
            return true;
        }
        
        
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.stats.header"));
        
        int total = plugin.getAnalyticsManager().getTotalMessages();
        int flagged = plugin.getAnalyticsManager().getFlaggedMessages();
        double percent = total > 0 ? (double) flagged / total * 100 : 0;
        
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.stats.total-analyzed",
            "{count}", String.valueOf(total)));
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.stats.total-flagged",
            "{count}", String.valueOf(flagged),
            "{percent}", String.format("%.1f", percent)));
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.stats.total-actions",
            "{count}", String.valueOf(plugin.getAnalyticsManager().getActionsTaken())));
        
        
        plugin.getAnalyticsManager().getCategoryStats().forEach((category, count) -> {
            sender.sendMessage(plugin.getConfigManager().getMessage("commands.stats.by-category",
                "{category}", category,
                "{count}", String.valueOf(count)));
        });
        
        return true;
    }
}