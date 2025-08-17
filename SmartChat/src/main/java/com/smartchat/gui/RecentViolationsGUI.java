package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.Violation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.*;

public class RecentViolationsGUI extends BaseGUI {
    
    private List<Violation> violations;
    private int currentPage = 0;
    private final int violationsPerPage = 21; 
    private String currentFilter = "all"; 
    private boolean autoRefresh = false;
    private int refreshTask = -1;
    
    public RecentViolationsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lRecent Violations §8§l◆", 54);
        this.violations = new ArrayList<>();
        loadViolations();
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        int totalViolations = violations.size();
        int totalPages = Math.max(1, (totalViolations + violationsPerPage - 1) / violationsPerPage);
        
        inventory.setItem(4, createItem(Material.REDSTONE_BLOCK, 
            "§c§lRecent Violations",
            "§7View and manage recent chat violations",
            "§7",
            "§8Filter: §e" + currentFilter.toUpperCase(),
            "§8Total: §c" + totalViolations + " violations",
            "§8Page: §b" + (currentPage + 1) + "/" + totalPages,
            "§8Auto-refresh: " + (autoRefresh ? "§aEnabled" : "§cDisabled")));
        
        
        setupFilterButtons();
        
        
        setupControlPanel();
        
        
        displayViolations();
        
        
        setupPageNavigation();
    }
    
    private void setupFilterButtons() {
        
        inventory.setItem(1, createItem(
            currentFilter.equals("all") ? Material.LIME_DYE : Material.GRAY_DYE, 
            "§f📋 All Violations",
            "§7Show all violation types",
            "§7",
            "§8Current: " + (currentFilter.equals("all") ? "§aActive" : "§7Inactive"),
            "§aClick to filter!"));
        
        
        inventory.setItem(2, createItem(
            currentFilter.equals("toxicity") ? Material.RED_DYE : Material.GRAY_DYE, 
            "§c🔥 Toxicity",
            "§7Show toxicity violations only",
            "§7",
            "§8Current: " + (currentFilter.equals("toxicity") ? "§aActive" : "§7Inactive"),
            "§aClick to filter!"));
        
        
        inventory.setItem(3, createItem(
            currentFilter.equals("harassment") ? Material.ORANGE_DYE : Material.GRAY_DYE, 
            "§6🎯 Harassment",
            "§7Show harassment violations only",
            "§7",
            "§8Current: " + (currentFilter.equals("harassment") ? "§aActive" : "§7Inactive"),
            "§aClick to filter!"));
        
        
        inventory.setItem(5, createItem(
            currentFilter.equals("profanity") ? Material.PURPLE_DYE : Material.GRAY_DYE, 
            "§5💬 Profanity",
            "§7Show profanity violations only",
            "§7",
            "§8Current: " + (currentFilter.equals("profanity") ? "§aActive" : "§7Inactive"),
            "§aClick to filter!"));
        
        
        inventory.setItem(6, createItem(
            currentFilter.equals("spam") ? Material.YELLOW_DYE : Material.GRAY_DYE, 
            "§e📢 Spam",
            "§7Show spam violations only",
            "§7",
            "§8Current: " + (currentFilter.equals("spam") ? "§aActive" : "§7Inactive"),
            "§aClick to filter!"));
        
        
        inventory.setItem(7, createItem(
            currentFilter.equals("hate-speech") ? Material.BLACK_DYE : Material.GRAY_DYE, 
            "§8⚫ Hate Speech",
            "§7Show hate speech violations only",
            "§7",
            "§8Current: " + (currentFilter.equals("hate-speech") ? "§aActive" : "§7Inactive"),
            "§aClick to filter!"));
    }
    
    private void setupControlPanel() {
        
        inventory.setItem(46, createItem(autoRefresh ? Material.LIME_DYE : Material.GRAY_DYE, 
            "§a🔄 Auto-refresh",
            "§7Status: " + (autoRefresh ? "§aEnabled" : "§cDisabled"),
            "§7",
            "§7Automatically refresh violations",
            "§7every 10 seconds",
            "§7",
            "§aClick to toggle!"));
        
        
        inventory.setItem(47, createItem(Material.EMERALD, 
            "🔃 Manual Refresh",
            "§7Click to refresh violation list",
            "§7",
            "§8Updates from database"));
        
        
        inventory.setItem(48, createItem(Material.PAPER, 
            "§b📄 Export Data",
            "§7Export violations to file",
            "§7",
            "§8Generate detailed report"));
        
        
        inventory.setItem(50, createItem(Material.TNT, 
            "§c🗑 Clear Old",
            "§7Clear violations older than 7 days",
            "§7",
            "§cPermanent action!",
            "§8Shift-click to confirm"));
        
        
        inventory.setItem(51, createItem(Material.BOOK, 
            "§a📊 Statistics",
            "§7View detailed violation statistics",
            "§7",
            "§8Click to view analytics"));
    }
    
    private void displayViolations() {
        if (violations.isEmpty()) {
            
            inventory.setItem(22, createItem(Material.BARRIER, 
                "§7📭 No Violations Found",
                "§7There are no violations matching",
                "§7the current filter criteria.",
                "§7",
                "§aThis is good news!"));
            return;
        }
        
        int startIndex = currentPage * violationsPerPage;
        int endIndex = Math.min(startIndex + violationsPerPage, violations.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            Violation violation = violations.get(i);
            int slot = 10 + (i - startIndex) + ((i - startIndex) / 7) * 2; 
            
            
            if (slot >= 37) break;
            
            List<String> violationLore = new ArrayList<>();
            violationLore.add("§7Player: §f" + (violation.getPlayerName() != null ? violation.getPlayerName() : "Unknown"));
            violationLore.add("§7Violation ID: §e#" + violation.getId());
            violationLore.add("§7Category: " + getCategoryColor(violation.getCategory()) + violation.getCategory().toUpperCase());
            violationLore.add("§7Severity: " + getSeverityColor(violation.getSeverity()) + violation.getSeverity().toUpperCase());
            violationLore.add("§7Confidence: §e" + String.format("%.1f%%", violation.getConfidence() * 100));
            violationLore.add("§7Time: §b" + formatTimestamp(violation.getTimestamp()));
            violationLore.add("§7");
            
            
            String message = violation.getMessage();
            violationLore.add("§7Message:");
            if (message.length() > 35) {
                
                String[] words = message.split(" ");
                StringBuilder line = new StringBuilder();
                for (String word : words) {
                    if (line.length() + word.length() > 35) {
                        violationLore.add("§c" + line.toString());
                        line = new StringBuilder(word);
                    } else {
                        if (line.length() > 0) line.append(" ");
                        line.append(word);
                    }
                }
                if (line.length() > 0) {
                    violationLore.add("§c" + line.toString());
                }
            } else {
                violationLore.add("§c" + message);
            }
            
            violationLore.add("§7");
            if (violation.getActionTaken() != null && !violation.getActionTaken().isEmpty()) {
                violationLore.add("§7Action: §6" + violation.getActionTaken());
            } else {
                violationLore.add("§7Action: §8None");
            }
            
            violationLore.add("§7");
            violationLore.add("§aClick to open violation management!");
            
            
            Material violationMaterial;
            switch (violation.getSeverity().toLowerCase()) {
                case "extreme":
                    violationMaterial = Material.REDSTONE_BLOCK;
                    break;
                case "high":
                    violationMaterial = Material.RED_CONCRETE;
                    break;
                case "medium":
                    violationMaterial = Material.ORANGE_CONCRETE;
                    break;
                case "low":
                    violationMaterial = Material.YELLOW_CONCRETE;
                    break;
                default:
                    violationMaterial = Material.GRAY_CONCRETE;
                    break;
            }
            
            inventory.setItem(slot, createItem(violationMaterial, 
                "§c⚠ Violation #" + violation.getId(), violationLore));
        }
    }
    
    private void setupPageNavigation() {
        int totalPages = Math.max(1, (violations.size() + violationsPerPage - 1) / violationsPerPage);
        
        if (currentPage > 0) {
            inventory.setItem(45, createItem(Material.ARROW, 
                "§7← Previous Page",
                "§7Go to page " + currentPage,
                "§7",
                "§8Click to go back"));
        }
        
        if (currentPage < totalPages - 1) {
            inventory.setItem(53, createItem(Material.ARROW, 
                "§7Next Page →",
                "§7Go to page " + (currentPage + 2),
                "§7",
                "§8Click to continue"));
        }
    }
    
    private void loadViolations() {
        if (currentFilter.equals("all")) {
            plugin.getDatabaseManager().getRecentViolations(100).thenAccept(this::onViolationsLoaded);
        } else {
            plugin.getDatabaseManager().getViolationsByCategory(currentFilter, 100).thenAccept(this::onViolationsLoaded);
        }
    }
    
    private void onViolationsLoaded(List<Violation> loadedViolations) {
        this.violations = loadedViolations;
        if (player.isOnline()) {
            plugin.getServer().getScheduler().runTask(plugin, this::refresh);
        }
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 1: 
                setFilter("all");
                break;
            case 2: 
                setFilter("toxicity");
                break;
            case 3: 
                setFilter("harassment");
                break;
            case 5: 
                setFilter("profanity");
                break;
            case 6: 
                setFilter("spam");
                break;
            case 7: 
                setFilter("hate-speech");
                break;
            case 45: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
            case 53: 
                int totalPages = Math.max(1, (violations.size() + violationsPerPage - 1) / violationsPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
            case 46: 
                toggleAutoRefresh();
                break;
            case 47: 
                loadViolations();
                player.sendMessage("§aRefreshing violations...");
                break;
            case 48: 
                exportViolations();
                break;
            case 50: 
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    clearOldViolations();
                } else {
                    player.sendMessage("§cShift-click to confirm clearing old violations!");
                }
                break;
            case 51: 
                showViolationStatistics();
                break;
            default:
                
                handleViolationClick(slot, clickType);
                break;
        }
    }
    
    private void handleViolationClick(int slot, ClickType clickType) {
        
        int displayIndex = getViolationIndexFromSlot(slot);
        if (displayIndex == -1) return;
        
        int violationIndex = currentPage * violationsPerPage + displayIndex;
        if (violationIndex >= violations.size()) return;
        
        Violation violation = violations.get(violationIndex);
        
        
        openViolationManagementGUI(violation);
    }
    
    private int getViolationIndexFromSlot(int slot) {
        
        if (slot < 10 || slot > 34) return -1;
        
        int row = (slot - 10) / 9;
        int col = (slot - 10) % 9;
        
        if (col > 6) return -1; 
        
        return row * 7 + col;
    }
    
    private void setFilter(String filter) {
        if (!currentFilter.equals(filter)) {
            currentFilter = filter;
            currentPage = 0;
            loadViolations();
        }
    }
    
    private void toggleAutoRefresh() {
        autoRefresh = !autoRefresh;
        
        if (autoRefresh) {
            
            refreshTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(inventory)) {
                    loadViolations();
                } else {
                    
                    plugin.getServer().getScheduler().cancelTask(refreshTask);
                    autoRefresh = false;
                }
            }, 200L, 200L).getTaskId(); 
            
            player.sendMessage("§aAuto-refresh enabled! (10 second intervals)");
        } else {
            
            if (refreshTask != -1) {
                plugin.getServer().getScheduler().cancelTask(refreshTask);
                refreshTask = -1;
            }
            player.sendMessage("§cAuto-refresh disabled!");
        }
        
        refresh();
    }
    
    private void openViolationManagementGUI(Violation violation) {
        ViolationManagementGUI violationGUI = new ViolationManagementGUI(plugin, player, violation);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), violationGUI);
        violationGUI.open();
    }
    
    private void exportViolations() {
        player.sendMessage("§7Exporting violations to file...");
        
        plugin.getExportManager().exportViolationsToCSV(violations).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "violations");
            });
        });
    }
    
    private void clearOldViolations() {
        player.sendMessage("§cClearing violations older than 7 days...");
        
        player.sendMessage("§aOld violation cleanup feature coming soon!");
    }
    
    private void showViolationStatistics() {
        plugin.getDatabaseManager().getViolationCategoryCounts().thenAccept(counts -> {
            if (player.isOnline()) {
                player.sendMessage("§a§l==== Violation Statistics ====");
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    String category = entry.getKey();
                    int count = entry.getValue();
                    player.sendMessage("§8• " + getCategoryColor(category) + category.toUpperCase() + "§7: §e" + count);
                }
                player.sendMessage("§a§l========================");
            }
        });
    }
    
    private String getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "toxicity": return "§c";
            case "harassment": return "§6";
            case "profanity": return "§5";
            case "spam": return "§e";
            case "hate-speech": return "§8";
            default: return "§7";
        }
    }
    
    private String getSeverityColor(String severity) {
        switch (severity.toLowerCase()) {
            case "extreme": return "§4";
            case "high": return "§c";
            case "medium": return "§6";
            case "low": return "§e";
            default: return "§7";
        }
    }
    
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "Unknown";
        
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s") + " ago";
        }
    }
    
    @Override
    public void onClose() {
        
        if (autoRefresh && refreshTask != -1) {
            plugin.getServer().getScheduler().cancelTask(refreshTask);
            autoRefresh = false;
            refreshTask = -1;
        }
    }
}