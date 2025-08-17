package com.smartchat.listeners;

import com.smartchat.SmartChat;
import com.smartchat.models.*;
import com.smartchat.utils.MessageContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {
    
    private final SmartChat plugin;
    private final Map<UUID, MessageContext> playerContexts;
    private final Map<UUID, Long> appealCooldowns;
    private final Set<String> learnedPatterns;
    
    public ChatListener(SmartChat plugin) {
        this.plugin = plugin;
        this.playerContexts = new ConcurrentHashMap<>();
        this.appealCooldowns = new ConcurrentHashMap<>();
        this.learnedPatterns = ConcurrentHashMap.newKeySet();
        
        
        loadLearnedPatterns();
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        
        
        event.setCancelled(true);
        
        
        if (player.hasPermission("smartchat.bypass")) {
            
            event.setCancelled(false);
            return;
        }
        
        
        for (String category : Arrays.asList("toxicity", "spam", "harassment")) {
            if (player.hasPermission("smartchat.bypass." + category)) {
                
            }
        }
        
        
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld().getName())) {
            
            event.setCancelled(false);
            return;
        }
        
        
        if (plugin.getConfigManager().shouldBypassPlayer(player.getName(), player.getUniqueId().toString())) {
            
            event.setCancelled(false);
            return;
        }
        
        
        plugin.getDatabaseManager().getActivePunishment(player.getUniqueId(), "mute")
            .thenAccept(punishment -> {
                if (punishment != null && !punishment.isExpired()) {
                    event.setCancelled(true);
                    
                    long remaining = punishment.getRemainingTime();
                    String duration = formatDuration(remaining);
                    
                    player.sendMessage(plugin.getConfigManager().getMessage("punishments.mute.attempt",
                        "{remaining}", duration,
                        "{reason}", punishment.getReason()
                    ));
                    return;
                }
                
                
                processMessage(event);
            });
    }
    
    private void processMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        
        plugin.getMessageTracker().addMessage(player, message, false, null);
        plugin.getPerformanceTracker().recordMessage();
        
        
        if (plugin.getConfigManager().isMessageWhitelisted(message)) {
            
            event.setCancelled(false);
            updatePlayerContext(player, message, false);
            return;
        }
        
        
        if (plugin.getConfigManager().isMessageBlacklisted(message)) {
            event.setCancelled(true);
            handleViolation(event, new ModerationResult(
                message, true, "blacklisted", "extreme", 1.0,
                Collections.singletonMap("blacklisted", 1.0),
                "Message matched blacklist"
            ));
            return;
        }
        
        
        String normalizedMessage = normalizeForBypassDetection(message);
        
        for (String learnedPattern : learnedPatterns) {
            if (normalizedMessage.contains(learnedPattern)) {
                
                player.sendMessage("§4§l[BLOCKED] §cYour message was blocked for known inappropriate content.");
                player.sendMessage("§7This pattern was previously flagged by our AI system.");
                
                
                plugin.getLogger().info("INSTANT BLOCK: '" + message + "' matched learned pattern: " + learnedPattern);
                
                
                handleViolation(event, new ModerationResult(
                    message, true, "learned-pattern", "high", 1.0,
                    Collections.singletonMap("learned-pattern", 1.0),
                    "Matched learned pattern: " + learnedPattern
                ));
                return;
            }
        }
        
        
        String[] severeProfanity = {
            "nigger", "nigga", "faggot", "retard", "kike", "chink", "spic", "wetback", "gook"
        };
        
        for (String word : severeProfanity) {
            if (containsProfanityInMessage(normalizedMessage, word)) {
                
                player.sendMessage("§4§l[BLOCKED] §cYour message was blocked for containing hate speech.");
                
                
                learnedPatterns.add(normalizedMessage);
                
                
                handleViolation(event, new ModerationResult(
                    message, true, "hate-speech", "extreme", 1.0,
                    Collections.singletonMap("hate-speech", 1.0),
                    "Severe violation detected: " + word
                ));
                return;
            }
        }
        
        
        MessageContext context = playerContexts.computeIfAbsent(
            player.getUniqueId(), 
            k -> new MessageContext(player.getUniqueId())
        );
        
        
        String contextString = context.buildContextString();
        
        
        long processingStart = System.currentTimeMillis();
        
        
        plugin.getApiManager().analyzeMessage(message, player.getName(), contextString)
            .thenAccept(result -> {
                
                long processingTime = System.currentTimeMillis() - processingStart;
                plugin.getPerformanceTracker().recordProcessingTime(processingTime);
                if (result.isFlagged()) {
                    
                    
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        handleViolation(event, result);
                        
                        
                        storeLearnedPattern(message, result);
                        
                        
                        String severity = result.getSeverity();
                        if ("extreme".equals(severity) || "high".equals(severity)) {
                            player.sendMessage("§4§l[BLOCKED] §cYour message was blocked for inappropriate content.");
                            player.sendMessage("§cSeverity: §e" + severity.toUpperCase() + " §c| Confidence: §e" + 
                                String.format("%.0f%%", result.getConfidence() * 100));
                            player.sendMessage("§7This pattern will be blocked instantly in the future.");
                        } else {
                            player.sendMessage("§c§l[BLOCKED] §7Your message was blocked for: §e" + result.getPrimaryCategory());
                            player.sendMessage("§7Future similar messages will be blocked instantly.");
                        }
                    });
                } else {
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        
                        
                        resendCleanMessage(player, message);
                        updatePlayerContext(player, message, false);
                    });
                }
                
                
                plugin.getAnalyticsManager().recordMessage(
                    result.isFlagged(), 
                    result.getPrimaryCategory(),
                    result.isFlagged()
                );
            })
            .exceptionally(throwable -> {
                plugin.getLogger().warning("Error analyzing message: " + throwable.getMessage());
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§c§l[BLOCKED] §7Your message was blocked due to moderation system error.");
                });
                return null;
            });
    }
    
    private void handleViolation(AsyncPlayerChatEvent event, ModerationResult result) {
        Player player = event.getPlayer();
        
        
        plugin.getDatabaseManager().getPlayerRecord(player.getUniqueId())
            .thenAccept(playerRecord -> {
                int violationCount = playerRecord != null ? playerRecord.getFlaggedMessages() : 0;
                String action = determineAction(result.getSeverity(), violationCount);
                
                
                String filterMode = plugin.getConfigManager().getConfig().getString("filtering.mode", "replace");
                switch (filterMode) {
                    case "block":
                        event.setCancelled(true);
                        break;
                    case "replace":
                        event.setMessage(plugin.getConfigManager().getConfig()
                            .getString("filtering.replacement-message", "[Filtered]")
                            .replace("&", "§"));
                        break;
                    case "asterisk":
                        event.setMessage(filterMessage(result.getOriginalMessage()));
                        break;
                }
                
                
                Violation violation = new Violation(
                    player.getUniqueId(),
                    result.getOriginalMessage(),
                    event.isCancelled() ? null : event.getMessage(),
                    result.getPrimaryCategory(),
                    result.getSeverity(),
                    result.getConfidence(),
                    action,
                    buildServerContext()
                );
                
                plugin.getDatabaseManager().recordViolation(violation);
                
                
                plugin.getMessageTracker().addMessage(player, result.getOriginalMessage(), true, result.getPrimaryCategory());
                plugin.getPerformanceTracker().recordViolation();
                if (!action.equals("none")) {
                    plugin.getPerformanceTracker().recordAction();
                }
                
                
                plugin.getActionManager().executeAction(player, action, result, violationCount);
                
                
                notifyStaff(player, result, action);
                
                
                updatePlayerContext(player, result.getOriginalMessage(), true);
            });
    }
    
    private String determineAction(String severity, int violationCount) {
        
        if (plugin.getConfigManager().getConfig().getBoolean("progressive-punishment.enabled", true)) {
            int window = plugin.getConfigManager().getConfig().getInt("progressive-punishment.window", 86400);
            
            
            
            
            Map<Integer, String> levels = new HashMap<>();
            plugin.getConfigManager().getConfig().getConfigurationSection("progressive-punishment.levels")
                .getKeys(false).forEach(key -> {
                    levels.put(Integer.parseInt(key), 
                        plugin.getConfigManager().getConfig().getString("progressive-punishment.levels." + key));
                });
            
            
            int highestLevel = 1;
            for (int level : levels.keySet()) {
                if (violationCount >= level && level > highestLevel) {
                    highestLevel = level;
                }
            }
            
            severity = levels.getOrDefault(highestLevel, severity);
        }
        
        
        List<String> actions = plugin.getConfigManager().getConfig()
            .getStringList("actions." + severity);
        
        return String.join(",", actions);
    }
    
    private void notifyStaff(Player violator, ModerationResult result, String action) {
        if (!plugin.getConfigManager().getConfig().getBoolean("notifications.enabled", true)) {
            return;
        }
        
        String minSeverity = plugin.getConfigManager().getConfig()
            .getString("notifications.min-severity", "medium");
        
        if (!shouldNotify(result.getSeverity(), minSeverity)) {
            return;
        }
        
        String severityColor = plugin.getConfigManager().getMessages()
            .getString("staff.severity." + result.getSeverity(), "&c");
        
        String alertMessage = plugin.getConfigManager().getMessage("staff.alerts.header") + "\n" +
            plugin.getConfigManager().getMessage("staff.alerts.player", "{player}", violator.getName()) + "\n" +
            plugin.getConfigManager().getMessage("staff.alerts.severity", 
                "{severity_color}", severityColor,
                "{severity}", result.getSeverity().toUpperCase()) + "\n" +
            plugin.getConfigManager().getMessage("staff.alerts.message", "{message}", result.getOriginalMessage()) + "\n" +
            plugin.getConfigManager().getMessage("staff.alerts.confidence", 
                "{confidence}", String.format("%.0f", result.getConfidence() * 100)) + "\n" +
            plugin.getConfigManager().getMessage("staff.alerts.category", "{category}", result.getPrimaryCategory()) + "\n" +
            plugin.getConfigManager().getMessage("staff.alerts.action", "{action}", action);
        
        
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("smartchat.notify")) {
                staff.sendMessage(alertMessage);
                
                
                if (plugin.getConfigManager().getConfig().getBoolean("notifications.methods.sound", true)) {
                    String soundName = plugin.getConfigManager().getConfig()
                        .getString("notifications.sound.type", "BLOCK_NOTE_BLOCK_PLING");
                    float volume = (float) plugin.getConfigManager().getConfig()
                        .getDouble("notifications.sound.volume", 1.0);
                    float pitch = (float) plugin.getConfigManager().getConfig()
                        .getDouble("notifications.sound.pitch", 1.5);
                    
                    try {
                        staff.playSound(staff.getLocation(), soundName, volume, pitch);
                    } catch (Exception e) {
                        
                    }
                }
            }
        }
    }
    
    private boolean shouldNotify(String severity, String minSeverity) {
        List<String> severityOrder = Arrays.asList("low", "medium", "high", "extreme");
        return severityOrder.indexOf(severity) >= severityOrder.indexOf(minSeverity);
    }
    
    private void updatePlayerContext(Player player, String message, boolean wasViolation) {
        MessageContext context = playerContexts.computeIfAbsent(
            player.getUniqueId(), 
            k -> new MessageContext(player.getUniqueId())
        );
        
        context.addMessage(message, wasViolation);
        
        
        plugin.getDatabaseManager().updatePlayerStats(
            player.getUniqueId().toString(),
            player.getName(),
            wasViolation
        );
    }
    
    private String buildServerContext() {
        
        Map<String, Object> context = new HashMap<>();
        context.put("online_players", Bukkit.getOnlinePlayers().size());
        context.put("time", new Date().toString());
        context.put("tps", 20.0); 
        
        return context.toString();
    }
    
    private String filterMessage(String message) {
        
        StringBuilder filtered = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                filtered.append('*');
            } else {
                filtered.append(c);
            }
        }
        return filtered.toString();
    }
    
    private String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    private String normalizeForBypassDetection(String message) {
        String normalized = message.toLowerCase();
        
        
        normalized = normalized.replaceAll("[\\s\\-_\\.\\*\\+\\|\\\\/#@$%^&()\\[\\]{}]", "");
        
        
        Map<String, String> substitutions = new HashMap<>();
        substitutions.put("@", "a");
        substitutions.put("4", "a");
        substitutions.put("3", "e");
        substitutions.put("1", "i");
        substitutions.put("!", "i");
        substitutions.put("0", "o");
        substitutions.put("5", "s");
        substitutions.put("7", "t");
        substitutions.put("8", "b");
        substitutions.put("6", "g");
        substitutions.put("9", "g");
        substitutions.put("$", "s");
        substitutions.put("2", "z");
        
        
        for (Map.Entry<String, String> sub : substitutions.entrySet()) {
            normalized = normalized.replace(sub.getKey(), sub.getValue());
        }
        
        
        StringBuilder result = new StringBuilder();
        char lastChar = '\0';
        int repeatCount = 0;
        
        for (char c : normalized.toCharArray()) {
            if (c == lastChar) {
                repeatCount++;
                
                if (repeatCount < 2) {
                    result.append(c);
                }
            } else {
                result.append(c);
                lastChar = c;
                repeatCount = 0;
            }
        }
        
        return result.toString();
    }
    
    private void loadLearnedPatterns() {
        
        
        plugin.getLogger().info("Loaded " + learnedPatterns.size() + " learned patterns from database");
    }
    
    private void storeLearnedPattern(String message, ModerationResult result) {
        if (result.isFlagged() && result.getConfidence() >= 0.8) {
            String normalizedPattern = normalizeForBypassDetection(message);
            
            
            learnedPatterns.add(normalizedPattern);
            
            
            String[] words = normalizedPattern.split("\\s+");
            for (String word : words) {
                if (word.length() > 3) { 
                    learnedPatterns.add(word);
                }
            }
            
            plugin.getLogger().info("LEARNED PATTERN: '" + normalizedPattern + "' will now be instantly blocked");
            
            
            
        }
    }
    
    private void resendCleanMessage(Player player, String message) {
        
        String formattedMessage = "<" + player.getName() + "> " + message;
        
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            recipient.sendMessage(formattedMessage);
        }
        
        
        plugin.getLogger().info(formattedMessage);
    }
    
    private boolean containsProfanityInMessage(String normalizedMessage, String profanityWord) {
        
        
        
        if (normalizedMessage.contains(profanityWord)) {
            return true;
        }
        
        
        if (normalizedMessage.contains(" " + profanityWord + " ") ||
            normalizedMessage.startsWith(profanityWord + " ") ||
            normalizedMessage.endsWith(" " + profanityWord)) {
            return true;
        }
        
        
        String[] separators = {".", ",", "!", "?", ";", ":", "-", "_", "(", ")", "[", "]"};
        for (String sep : separators) {
            if (normalizedMessage.contains(sep + profanityWord + sep) ||
                normalizedMessage.contains(sep + profanityWord + " ") ||
                normalizedMessage.contains(" " + profanityWord + sep) ||
                normalizedMessage.startsWith(profanityWord + sep) ||
                normalizedMessage.endsWith(sep + profanityWord)) {
                return true;
            }
        }
        
        
        String[] words = normalizedMessage.split("[\\s\\-_\\.\\,\\!\\?\\;\\:\\(\\)\\[\\]]+");
        for (String word : words) {
            if (word.equals(profanityWord)) {
                return true;
            }
            
            
            if (word.contains(profanityWord) && word.length() <= profanityWord.length() + 3) {
                
                return true;
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        int currentPlayers = plugin.getServer().getOnlinePlayers().size();
        plugin.getPerformanceTracker().updatePeakPlayers(currentPlayers);
    }
}