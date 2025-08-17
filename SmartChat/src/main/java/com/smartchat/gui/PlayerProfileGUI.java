package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.PlayerRecord;
import com.smartchat.models.Violation;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerProfileGUI extends BaseGUI {
    
    private final OfflinePlayer targetPlayer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    private PlayerRecord playerRecord = null;
    private List<Violation> recentViolations = new ArrayList<>();
    
    public PlayerProfileGUI(SmartChat plugin, Player player, OfflinePlayer targetPlayer) {
        super(plugin, player, "§8§l◆ §b§lᴘʟᴀʏᴇʀ ᴘʀᴏꜰɪʟᴇ §8§l◆", 54);
        this.targetPlayer = targetPlayer;
        loadPlayerData();
    }
    
    private void loadPlayerData() {
        
        plugin.getDatabaseManager().getPlayerRecord(targetPlayer.getUniqueId()).thenAccept(record -> {
            this.playerRecord = record;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
        
        
        plugin.getDatabaseManager().getPlayerViolations(targetPlayer.getUniqueId(), 5).thenAccept(violations -> {
            this.recentViolations = violations;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PLAYER));
        addNavigationItems();
        
        
        setupPlayerHeader();
        
        
        setupStatistics();
        
        
        setupRecentViolations();
        
        
        setupActionButtons();
    }
    
    private void setupPlayerHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Viewing profile for: §f" + targetPlayer.getName());
        headerLore.add("§7UUID: §e" + targetPlayer.getUniqueId());
        headerLore.add("§7Status: " + (targetPlayer.isOnline() ? "§aOnline" : "§7Offline"));
        
        if (targetPlayer.getFirstPlayed() > 0) {
            headerLore.add("§7First Joined: §b" + dateFormat.format(new Date(targetPlayer.getFirstPlayed())));
        }
        if (targetPlayer.getLastPlayed() > 0 && !targetPlayer.isOnline()) {
            headerLore.add("§7Last Seen: §b" + dateFormat.format(new Date(targetPlayer.getLastPlayed())));
        }
        
        inventory.setItem(4, createItem(Material.PLAYER_HEAD, 
            "§b§l" + targetPlayer.getName(), headerLore));
    }
    
    private void setupStatistics() {
        if (playerRecord == null) {
            
            inventory.setItem(19, createItem(Material.BARRIER, 
                "§c📊 No Data Available",
                "§7This player has no recorded activity",
                "§7in the moderation system yet."));
            return;
        }
        
        
        inventory.setItem(19, createItem(Material.BOOK, 
            "§a📚 Message Statistics",
            "§7Total Messages: §b" + playerRecord.getTotalMessages(),
            "§7Clean Messages: §a" + (playerRecord.getTotalMessages() - playerRecord.getFlaggedMessages()),
            "§7Flagged Messages: §c" + playerRecord.getFlaggedMessages(),
            "§7",
            "§7Flag Rate: §e" + String.format("%.1f%%", 
                playerRecord.getTotalMessages() > 0 
                    ? (double) playerRecord.getFlaggedMessages() / playerRecord.getTotalMessages() * 100 
                    : 0.0),
            "§7",
            "§8Message history and patterns"));
        
        
        inventory.setItem(20, createItem(Material.REDSTONE, 
            "§c⚠ Violation Breakdown",
            "§7Total Violations: §c" + playerRecord.getFlaggedMessages(),
            "§7Violation Score: §e" + String.format("%.2f", playerRecord.getViolationScore()),
            "§7Flag Rate: §6" + String.format("%.1f%%", playerRecord.getFlaggedPercentage()),
            "§7",
            "§8Detailed breakdown coming soon",
            "§7",
            "§8Violation type distribution"));
        
        
        double trustScore = calculateTrustScore();
        Material trustMaterial = trustScore >= 80 ? Material.EMERALD : 
                                trustScore >= 60 ? Material.GOLD_INGOT :
                                trustScore >= 40 ? Material.IRON_INGOT : Material.REDSTONE;
        
        inventory.setItem(21, createItem(trustMaterial, 
            "§b✨ Trust Score",
            "§7Current Score: " + getTrustColor(trustScore) + String.format("%.0f", trustScore) + "/100",
            "§7",
            "§7Trust Level: " + getTrustLevel(trustScore),
            "§7Based on: §bViolation patterns",
            "§7",
            "§8Trust calculation based on",
            "§8messages and violations"));
        
        
        AtomicReference<String> activePunishments = new AtomicReference<>("§7None");
        plugin.getDatabaseManager().getActivePunishment(targetPlayer.getUniqueId(), null)
            .thenAccept(punishment -> {
                if (punishment != null) {
                    activePunishments.set("§c" + punishment.getType().toUpperCase() + " (Active)");
                }
            });
        
        inventory.setItem(22, createItem(Material.IRON_BARS, 
            "§6⚔ Punishment Status",
            "§7Active Punishments: " + activePunishments.get(),
            "§7Total Violations: §e" + playerRecord.getFlaggedMessages(),
            "§7Violation Score: §c" + String.format("%.2f", playerRecord.getViolationScore()),
            "§7",
            "§aClick to view punishment history!"));
    }
    
    private void setupRecentViolations() {
        if (recentViolations.isEmpty()) {
            inventory.setItem(29, createItem(Material.LIME_DYE, 
                "§a✓ No Recent Violations",
                "§7This player has no recent",
                "§7violations on record.",
                "§7",
                "§8Clean chat history"));
            return;
        }
        
        
        int startSlot = 28;
        for (int i = 0; i < Math.min(recentViolations.size(), 5); i++) {
            Violation violation = recentViolations.get(i);
            
            List<String> violationLore = new ArrayList<>();
            violationLore.add("§7Time: §e" + formatTimestamp(violation.getTimestamp()));
            violationLore.add("§7Type: §c" + violation.getCategory());
            violationLore.add("§7Severity: " + getSeverityColor(violation.getSeverity()) + violation.getSeverity());
            violationLore.add("§7Action: §6" + violation.getActionTaken());
            violationLore.add("§7");
            
            String message = violation.getMessage();
            if (message.length() > 30) {
                violationLore.add("§7Message: §f" + message.substring(0, 30) + "...");
            } else {
                violationLore.add("§7Message: §f" + message);
            }
            
            violationLore.add("§7");
            violationLore.add("§aClick to view full details!");
            
            inventory.setItem(startSlot + i, createItem(Material.PAPER, 
                "§c📋 Violation #" + violation.getId(), violationLore));
        }
    }
    
    private void setupActionButtons() {
        
        inventory.setItem(37, createItem(Material.BOOK, 
            "§b📚 Full History",
            "§7View complete violation",
            "§7and punishment history.",
            "§7",
            "§aClick to view history!"));
        
        
        inventory.setItem(39, createItem(Material.IRON_SWORD, 
            "§6⚡ Quick Actions",
            "§7Perform quick moderation",
            "§7actions on this player.",
            "§7",
            "§8• Warn",
            "§8• Mute",
            "§8• Ban",
            "§7",
            "§aClick for actions!"));
        
        
        inventory.setItem(41, createItem(Material.FEATHER, 
            "§d📝 Player Notes",
            "§7View or add notes about",
            "§7this player for staff.",
            "§7",
            "§aClick to manage notes!"));
        
        
        inventory.setItem(43, createItem(Material.WRITABLE_BOOK, 
            "§e📋 Appeal History",
            "§7View this player's",
            "§7appeal submissions.",
            "§7",
            "§7Total Violations: §b" + (playerRecord != null ? 
                playerRecord.getFlaggedMessages() : 0),
            "§7",
            "§aClick to view appeals!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 22: 
                viewPunishmentHistory();
                break;
            case 28:
            case 29:
            case 30:
            case 31:
            case 32: 
                int violationIndex = slot - 28;
                if (violationIndex < recentViolations.size()) {
                    viewViolationDetails(recentViolations.get(violationIndex));
                }
                break;
            case 37: 
                viewFullHistory();
                break;
            case 39: 
                openQuickActions();
                break;
            case 41: 
                managePlayerNotes();
                break;
            case 43: 
                viewAppealHistory();
                break;
        }
    }
    
    private void viewPunishmentHistory() {
        PunishmentHistoryGUI historyGUI = new PunishmentHistoryGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), historyGUI);
        historyGUI.open();
    }
    
    private void viewViolationDetails(Violation violation) {
        ViolationManagementGUI violationGUI = new ViolationManagementGUI(plugin, player, violation);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), violationGUI);
        violationGUI.open();
    }
    
    private void viewFullHistory() {
        PunishmentHistoryGUI historyGUI = new PunishmentHistoryGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), historyGUI);
        historyGUI.open();
    }
    
    private void openQuickActions() {
        PlayerQuickActionsGUI actionsGUI = new PlayerQuickActionsGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), actionsGUI);
        actionsGUI.open();
    }
    
    private void managePlayerNotes() {
        player.sendMessage("§dPlayer notes system coming soon!");
        
    }
    
    private void viewAppealHistory() {
        AppealReviewGUI appealGUI = new AppealReviewGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), appealGUI);
        appealGUI.open();
    }
    
    private double calculateTrustScore() {
        if (playerRecord == null || playerRecord.getTotalMessages() == 0) {
            return 100.0; 
        }
        
        double baseScore = 100.0;
        
        
        double violationRate = (double) playerRecord.getFlaggedMessages() / playerRecord.getTotalMessages();
        baseScore -= violationRate * 50; 
        
        
        baseScore -= playerRecord.getViolationScore() * 10;
        
        return Math.max(0, Math.min(100, baseScore));
    }
    
    private String getTrustColor(double score) {
        if (score >= 80) return "§a";
        if (score >= 60) return "§e";
        if (score >= 40) return "§6";
        return "§c";
    }
    
    private String getTrustLevel(double score) {
        if (score >= 90) return "§aExcellent";
        if (score >= 70) return "§eGood";
        if (score >= 50) return "§6Fair";
        if (score >= 30) return "§cPoor";
        return "§4Very Poor";
    }
    
    private String getSeverityColor(String severity) {
        switch (severity.toLowerCase()) {
            case "low": return "§a";
            case "medium": return "§e";
            case "high": return "§6";
            case "extreme": return "§c";
            default: return "§7";
        }
    }
    
    private String formatTimestamp(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "Unknown";
        
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d ago";
        } else if (hours > 0) {
            return hours + "h ago";
        } else if (minutes > 0) {
            return minutes + "m ago";
        } else {
            return seconds + "s ago";
        }
    }
}