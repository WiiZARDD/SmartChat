package com.smartchat.commands;

import com.smartchat.SmartChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    
    private final SmartChat plugin;
    
    public MainCommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            
            return new GUICommand(plugin).onCommand(sender, command, label, args);
        }
        
        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                break;
            case "reload":
                if (sender.hasPermission("smartchat.admin.reload")) {
                    plugin.reload();
                    sender.sendMessage(plugin.getConfigManager().getMessage("commands.reload.success"));
                } else {
                    sender.sendMessage(plugin.getConfigManager().getMessage("errors.permission.command"));
                }
                break;
            case "stats":
                if (sender.hasPermission("smartchat.admin.stats")) {
                    sendStats(sender);
                } else {
                    sender.sendMessage(plugin.getConfigManager().getMessage("errors.permission.command"));
                }
                break;
            case "gui":
                return new GUICommand(plugin).onCommand(sender, command, label, 
                    java.util.Arrays.copyOfRange(args, 1, args.length));
            case "player":
                return new PlayerCommand(plugin).onCommand(sender, command, label, 
                    java.util.Arrays.copyOfRange(args, 1, args.length));
            default:
                sendHelp(sender);
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        
        sender.sendMessage("");
        sender.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("§b§l                    SmartChat");
        sender.sendMessage("§7§l                  Advanced Chat Protection");
        sender.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
        
        
        sender.sendMessage("§e§l➤ §6§lADMIN COMMANDS");
        sender.sendMessage("§f/sc §8» §7Open the main admin panel");
        sender.sendMessage("§f/sc reload §8» §7Reload plugin configuration");
        sender.sendMessage("§f/sc stats §8» §7View detailed moderation statistics");
        sender.sendMessage("§f/sc player §8<§bname§8> §8» §7Manage specific player profile");
        sender.sendMessage("");
        
        
        sender.sendMessage("§c§l➤ §4§lMODERATION TOOLS");
        sender.sendMessage("§f/sccheck §8<§bmessage§8> §8» §7Manually analyze a message");
        sender.sendMessage("§f/appeal §8<§breason§8> §8» §7Submit an appeal for punishment");
        sender.sendMessage("");
        
        
        sender.sendMessage("§9§l➤ §1§lADDITIONAL INFO");
        sender.sendMessage("§7§l• §fPlugin Version: §a1.0.0");
        sender.sendMessage("§7§l• §fPermissions: §eschg.admin §7for full access");
        sender.sendMessage("§7§l• §fSupport: §b/sc gui §7→ §bHelp & Support");
        sender.sendMessage("");
        sender.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    
    private void sendStats(CommandSender sender) {
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
    }
}