package com.smartchat.commands;

import com.smartchat.SmartChat;
import com.smartchat.models.Appeal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AppealCommand implements CommandExecutor {
    
    private final SmartChat plugin;
    
    public AppealCommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("smartchat.appeal")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.permission.command"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.general.invalid-args",
                "{usage}", "/appeal <reason>"));
            return true;
        }
        
        String reason = String.join(" ", args);
        
        
        int maxLength = plugin.getConfigManager().getConfig().getInt("appeals.max-length", 200);
        if (reason.length() > maxLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("appeals.submit.too-long",
                "{max}", String.valueOf(maxLength)));
            return true;
        }
        
        
        plugin.getDatabaseManager().getPlayerViolations(player.getUniqueId(), 1)
            .thenAccept(violations -> {
                if (violations.isEmpty()) {
                    player.sendMessage("You have no recent violations to appeal.");
                    return;
                }
                
                Appeal appeal = new Appeal(
                    player.getUniqueId(),
                    violations.get(0).getId(),
                    reason
                );
                
                plugin.getDatabaseManager().createAppeal(appeal)
                    .thenAccept(appealId -> {
                        if (appealId > 0) {
                            player.sendMessage(plugin.getConfigManager().getMessage("appeals.submit.success",
                                "{id}", String.valueOf(appealId)));
                        } else {
                            player.sendMessage("Failed to submit appeal. Please try again later.");
                        }
                    });
            });
        
        return true;
    }
}