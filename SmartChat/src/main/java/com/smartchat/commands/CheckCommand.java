package com.smartchat.commands;

import com.smartchat.SmartChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CheckCommand implements CommandExecutor {
    
    private final SmartChat plugin;
    
    public CheckCommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("smartchat.admin.check")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.permission.command"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(plugin.getConfigManager().getMessage("errors.general.invalid-args",
                "{usage}", "/sccheck <message>"));
            return true;
        }
        
        String message = String.join(" ", args);
        sender.sendMessage(plugin.getConfigManager().getMessage("commands.check.analyzing"));
        
        plugin.getApiManager().analyzeMessage(message, sender.getName(), "Manual check")
            .thenAccept(result -> {
                if (result.isFlagged()) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("commands.check.result-flagged",
                        "{category}", result.getPrimaryCategory(),
                        "{confidence}", String.format("%.0f", result.getConfidence() * 100)));
                } else {
                    sender.sendMessage(plugin.getConfigManager().getMessage("commands.check.result-safe",
                        "{confidence}", String.format("%.0f", (1 - result.getConfidence()) * 100)));
                }
            })
            .exceptionally(throwable -> {
                sender.sendMessage(plugin.getConfigManager().getMessage("commands.check.error",
                    "{error}", throwable.getMessage()));
                return null;
            });
        
        return true;
    }
}