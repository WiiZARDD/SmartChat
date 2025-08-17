package com.smartchat.actions;

import com.smartchat.SmartChat;
import com.smartchat.models.ModerationResult;
import com.smartchat.models.Punishment;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionManager {
    
    private final SmartChat plugin;
    private final Map<UUID, Integer> warningCounts;
    
    public ActionManager(SmartChat plugin) {
        this.plugin = plugin;
        this.warningCounts = new HashMap<>();
    }
    
    public void executeAction(Player player, String actions, ModerationResult result, int violationCount) {
        if (actions == null || actions.isEmpty()) return;
        
        for (String action : actions.split(",")) {
            action = action.trim().toLowerCase();
            
            switch (action) {
                case "warn":
                    warn(player, result);
                    break;
                case "filter_message":
                    
                    break;
                case "block_message":
                    
                    break;
                case "temp_mute":
                    tempMute(player, result);
                    break;
                case "kick":
                    kick(player, result);
                    break;
                case "temp_ban":
                    tempBan(player, result);
                    break;
                case "notify_staff":
                    
                    break;
                case "log_to_database":
                    
                    break;
            }
        }
    }
    
    private void warn(Player player, ModerationResult result) {
        int warnings = warningCounts.getOrDefault(player.getUniqueId(), 0) + 1;
        warningCounts.put(player.getUniqueId(), warnings);
        
        int maxWarnings = 3; 
        
        if (warnings == 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("warnings.first.chat",
                "{reason}", result.getPrimaryCategory()));
        } else if (warnings < maxWarnings) {
            player.sendMessage(plugin.getConfigManager().getMessage("warnings.repeated.chat",
                "{count}", String.valueOf(warnings),
                "{max}", String.valueOf(maxWarnings),
                "{reason}", result.getPrimaryCategory()));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("warnings.final.chat"));
        }
    }
    
    private void tempMute(Player player, ModerationResult result) {
        String severity = result.getSeverity();
        int duration = plugin.getConfigManager().getConfig()
            .getInt("actions." + severity + ".mute-duration", 300);
        
        
        if (tryEssentialsMute(player, duration, result.getPrimaryCategory())) {
            plugin.getLogger().info("Muted " + player.getName() + " using Essentials for " + duration + " seconds");
        } else {
            plugin.getLogger().info("Essentials not found, using built-in mute system");
        }
        
        
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + (duration * 1000L));
        
        Punishment punishment = new Punishment(
            player.getUniqueId(),
            "mute",
            result.getPrimaryCategory(),
            endTime,
            UUID.fromString("00000000-0000-0000-0000-000000000000") 
        );
        
        plugin.getDatabaseManager().addPunishment(punishment);
        
        player.sendMessage(plugin.getConfigManager().getMessage("punishments.mute.applied",
            "{duration}", formatDuration(duration),
            "{reason}", result.getPrimaryCategory()));
    }
    
    private boolean tryEssentialsMute(Player player, int duration, String reason) {
        try {
            
            org.bukkit.plugin.Plugin essentials = plugin.getServer().getPluginManager().getPlugin("Essentials");
            if (essentials != null && essentials.isEnabled()) {
                
                String command = "mute " + player.getName() + " " + duration + "s " + reason;
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to use Essentials mute: " + e.getMessage());
        }
        return false;
    }
    
    private void kick(Player player, ModerationResult result) {
        String kickMessage = plugin.getConfigManager().getMessage("punishments.kick.screen",
            "{reason}", result.getPrimaryCategory(),
            "{count}", String.valueOf(warningCounts.getOrDefault(player.getUniqueId(), 0)));
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.kickPlayer(kickMessage);
        });
    }
    
    private void tempBan(Player player, ModerationResult result) {
        String severity = result.getSeverity();
        int duration = plugin.getConfigManager().getConfig()
            .getInt("actions." + severity + ".ban-duration", 86400);
        
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + (duration * 1000L));
        
        String banMessage = plugin.getConfigManager().getMessage("punishments.ban.screen",
            "{reason}", result.getPrimaryCategory(),
            "{duration}", formatDuration(duration),
            "{expiry}", endTime.toString());
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getBanList(BanList.Type.NAME).addBan(
                player.getName(),
                banMessage,
                new java.util.Date(endTime.getTime()),
                "SmartChat"
            );
            player.kickPlayer(banMessage);
        });
        
        Punishment punishment = new Punishment(
            player.getUniqueId(),
            "ban",
            result.getPrimaryCategory(),
            endTime,
            UUID.fromString("00000000-0000-0000-0000-000000000000")
        );
        
        plugin.getDatabaseManager().addPunishment(punishment);
    }
    
    private String formatDuration(int seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " hours";
        } else {
            return (seconds / 86400) + " days";
        }
    }
    
    public void reload() {
        warningCounts.clear();
    }
}