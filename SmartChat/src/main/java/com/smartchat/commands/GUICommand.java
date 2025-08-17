package com.smartchat.commands;

import com.smartchat.SmartChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GUICommand implements CommandExecutor {
    
    private final SmartChat plugin;
    
    public GUICommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("smartchat.gui")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            
            plugin.getGuiManager().openMainGUI(player);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "main":
            case "menu":
                plugin.getGuiManager().openMainGUI(player);
                break;
                
            case "config":
            case "settings":
                if (!player.hasPermission("smartchat.gui.config")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
                    return true;
                }
                plugin.getGuiManager().openConfigGUI(player);
                break;
                
            case "players":
            case "player":
                if (!player.hasPermission("smartchat.gui.players")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
                    return true;
                }
                plugin.getGuiManager().openPlayerManagementGUI(player);
                break;
                
            case "monitor":
            case "monitoring":
            case "dashboard":
                if (!player.hasPermission("smartchat.gui.monitor")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
                    return true;
                }
                plugin.getGuiManager().openMonitoringGUI(player);
                break;
                
            case "appeals":
            case "appeal":
                if (!player.hasPermission("smartchat.gui.appeals")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
                    return true;
                }
                plugin.getGuiManager().openAppealReviewGUI(player);
                break;
                
            case "violations":
            case "recent":
            case "history":
                if (!player.hasPermission("smartchat.gui.monitor")) {
                    player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
                    return true;
                }
                plugin.getGuiManager().openRecentViolationsGUI(player);
                break;
                
            default:
                
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§6§l                    SmartChat GUI Help");
        player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");
        player.sendMessage("§e/sc gui §8- §7Open the main admin panel");
        player.sendMessage("§e/sc gui main §8- §7Open the main admin panel");
        player.sendMessage("§e/sc gui config §8- §7Open configuration management");
        player.sendMessage("§e/sc gui players §8- §7Open player management");
        player.sendMessage("§e/sc gui monitor §8- §7Open real-time monitoring dashboard");
        player.sendMessage("§e/sc gui appeals §8- §7Open appeal review system");
        player.sendMessage("§e/sc gui violations §8- §7View recent violations and take action");
        player.sendMessage("");
        player.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}