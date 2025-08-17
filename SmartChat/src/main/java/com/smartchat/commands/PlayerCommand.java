package com.smartchat.commands;

import com.smartchat.SmartChat;
import com.smartchat.gui.PlayerProfileGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerCommand implements CommandExecutor, TabCompleter {
    
    private final SmartChat plugin;
    
    public PlayerCommand(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("smartchat.gui.players")) {
            player.sendMessage(plugin.getConfigManager().getMessage("messages.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage("§cUsage: /sc player <player name>");
            return true;
        }
        
        String targetName = args[0];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
        
        if (targetPlayer == null || (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline())) {
            player.sendMessage("§cPlayer '" + targetName + "' not found!");
            return true;
        }
        
        
        PlayerProfileGUI profileGUI = new PlayerProfileGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), profileGUI);
        profileGUI.open();
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            
            
            String input = args[0].toLowerCase();
            return playerNames.stream()
                .filter(name -> name.toLowerCase().startsWith(input))
                .sorted()
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}