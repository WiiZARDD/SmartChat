package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.PlayerRecord;
import com.smartchat.models.Violation;
import com.smartchat.models.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishmentHistoryGUI extends BaseGUI {
    
    private final OfflinePlayer targetPlayer;
    private List<Violation> playerViolations = new ArrayList<>();
    private List<Punishment> playerPunishments = new ArrayList<>();
    private PlayerRecord playerRecord;
    private int currentPage = 0;
    private final int itemsPerPage = 21; 
    private HistoryView currentView = HistoryView.VIOLATIONS;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    
    public enum HistoryView {
        VIOLATIONS("Violations"),
        PUNISHMENTS("Punishments"),
        APPEALS("Appeals"),
        TIMELINE("Timeline");
        
        private final String displayName;
        
        HistoryView(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public PunishmentHistoryGUI(SmartChat plugin, Player player, OfflinePlayer targetPlayer) {
        super(plugin, player, "§8§l◆ §c§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ʜɪꜱᴛᴏʀʏ §8§l◆", 54);
        this.targetPlayer = targetPlayer;
        loadHistoryData();
    }
    
    private void loadHistoryData() {
        
        plugin.getDatabaseManager().getPlayerRecord(targetPlayer.getUniqueId()).thenAccept(record -> {
            this.playerRecord = record;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
        
        
        plugin.getDatabaseManager().getPlayerViolations(targetPlayer.getUniqueId(), 1000).thenAccept(violations -> {
            this.playerViolations = violations;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
        
        
        loadPlayerPunishments();
    }
    
    private void loadPlayerPunishments() {
        
        
        playerPunishments = new ArrayList<>();
        
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        setupHeader();
        setupViewSelector();
        setupHistoryDisplay();
        setupPaginationControls();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Complete punishment history for");
        headerLore.add("§7Player: §f" + targetPlayer.getName());
        headerLore.add("§7UUID: §8" + targetPlayer.getUniqueId().toString().substring(0, 8) + "...");
        headerLore.add("§7");
        headerLore.add("§7Current View: §e" + currentView.getDisplayName());
        headerLore.add("§7Total Violations: §c" + playerViolations.size());
        headerLore.add("§7Total Punishments: §6" + playerPunishments.size());
        
        if (playerRecord != null) {
            headerLore.add("§7Current Score: §6" + String.format("%.2f", playerRecord.getViolationScore()));
        }
        
        inventory.setItem(4, createItem(Material.PLAYER_HEAD, 
            "§c§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ʜɪꜱᴛᴏʀʏ", headerLore));
    }
    
    private void setupViewSelector() {
        
        inventory.setItem(9, createItem(getCurrentViewMaterial(HistoryView.VIOLATIONS),
            getViewColor(HistoryView.VIOLATIONS) + "⚠ ᴠɪᴏʟᴀᴛɪᴏɴꜱ",
            "§7View all chat violations",
            "§7Count: §c" + playerViolations.size(),
            "§7",
            currentView == HistoryView.VIOLATIONS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(10, createItem(getCurrentViewMaterial(HistoryView.PUNISHMENTS),
            getViewColor(HistoryView.PUNISHMENTS) + "⚔ ᴘᴜɴɪꜱʜᴍᴇɴᴛꜱ",
            "§7View all issued punishments",
            "§7Count: §6" + playerPunishments.size(),
            "§7",
            currentView == HistoryView.PUNISHMENTS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(11, createItem(getCurrentViewMaterial(HistoryView.APPEALS),
            getViewColor(HistoryView.APPEALS) + "📋 ᴀᴘᴘᴇᴀʟꜱ",
            "§7View appeal submissions",
            "§7Count: §e" + getAppealsCount(),
            "§7",
            currentView == HistoryView.APPEALS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(12, createItem(getCurrentViewMaterial(HistoryView.TIMELINE),
            getViewColor(HistoryView.TIMELINE) + "🕒 ᴛɪᴍᴇʟɪɴᴇ",
            "§7Chronological history view",
            "§7All events combined",
            "§7",
            currentView == HistoryView.TIMELINE ? "§a▶ Currently viewing" : "§7Click to view!"));
    }
    
    private void setupHistoryDisplay() {
        switch (currentView) {
            case VIOLATIONS:
                setupViolationsDisplay();
                break;
            case PUNISHMENTS:
                setupPunishmentsDisplay();
                break;
            case APPEALS:
                setupAppealsDisplay();
                break;
            case TIMELINE:
                setupTimelineDisplay();
                break;
        }
    }
    
    private void setupViolationsDisplay() {
        if (playerViolations.isEmpty()) {
            inventory.setItem(31, createItem(Material.LIME_DYE,
                "§a✓ ɴᴏ ᴠɪᴏʟᴀᴛɪᴏɴꜱ",
                "§7This player has a clean record!",
                "§7No chat violations found."));
            return;
        }
        
        
        List<Violation> sortedViolations = playerViolations.stream()
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
        
        
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, sortedViolations.size());
        
        
        for (int i = 19; i <= 43; i++) {
            if (i % 9 != 0 && i % 9 != 8) { 
                inventory.setItem(i, null);
            }
        }
        
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Violation violation = sortedViolations.get(i);
            
            List<String> violationLore = new ArrayList<>();
            violationLore.add("§7Time: §e" + dateFormat.format(violation.getTimestamp()));
            violationLore.add("§7Category: §c" + violation.getCategory());
            violationLore.add("§7Severity: " + getSeverityColor(violation.getSeverity()) + violation.getSeverity());
            violationLore.add("§7Confidence: §b" + String.format("%.1f%%", violation.getConfidence()));
            violationLore.add("§7Action: §6" + violation.getActionTaken());
            violationLore.add("§7");
            
            
            String message = violation.getMessage();
            if (message.length() > 40) {
                violationLore.add("§7Message: §f\"" + message.substring(0, 40) + "...\"");
            } else {
                violationLore.add("§7Message: §f\"" + message + "\"");
            }
            
            violationLore.add("§7");
            violationLore.add("§aClick to view full details!");
            
            int displaySlot = getHistoryDisplaySlot(slotIndex);
            if (displaySlot != -1) {
                inventory.setItem(displaySlot, createItem(
                    getSeverityMaterial(violation.getSeverity()),
                    "§c📋 ᴠɪᴏʟᴀᴛɪᴏɴ #" + violation.getId(),
                    violationLore));
            }
            
            slotIndex++;
        }
    }
    
    private void setupPunishmentsDisplay() {
        if (playerPunishments.isEmpty()) {
            inventory.setItem(31, createItem(Material.DIAMOND,
                "§b✨ ɴᴏ ᴘᴜɴɪꜱʜᴍᴇɴᴛꜱ",
                "§7This player has no punishment history.",
                "§7Clean disciplinary record!"));
            return;
        }
        
        
        inventory.setItem(31, createItem(Material.PAPER,
            "§e🚧 ᴘᴜɴɪꜱʜᴍᴇɴᴛ ʜɪꜱᴛᴏʀʏ",
            "§7Punishment history implementation",
            "§7coming soon!"));
    }
    
    private void setupAppealsDisplay() {
        inventory.setItem(31, createItem(Material.WRITABLE_BOOK,
            "§e📝 ᴀᴘᴘᴇᴀʟ ʜɪꜱᴛᴏʀʏ",
            "§7Appeal history implementation",
            "§7coming soon!"));
    }
    
    private void setupTimelineDisplay() {
        
        List<String> timelineEntries = new ArrayList<>();
        
        
        for (Violation violation : playerViolations) {
            timelineEntries.add(dateFormat.format(violation.getTimestamp()) + " - " +
                "§c[VIOLATION] " + violation.getCategory() + " (" + violation.getSeverity() + ")");
        }
        
        
        
        if (timelineEntries.isEmpty()) {
            inventory.setItem(31, createItem(Material.CLOCK,
                "§a✓ ᴄʟᴇᴀɴ ʜɪꜱᴛᴏʀʏ",
                "§7No events in player history.",
                "§7Perfect record!"));
            return;
        }
        
        
        timelineEntries.sort(String::compareTo);
        
        
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, timelineEntries.size());
        
        
        for (int i = 19; i <= 43; i++) {
            if (i % 9 != 0 && i % 9 != 8) {
                inventory.setItem(i, null);
            }
        }
        
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            String entry = timelineEntries.get(i);
            
            int displaySlot = getHistoryDisplaySlot(slotIndex);
            if (displaySlot != -1) {
                inventory.setItem(displaySlot, createItem(Material.PAPER,
                    "§f📅 ᴇᴠᴇɴᴛ #" + (i + 1),
                    "§7" + entry,
                    "§7",
                    "§aClick for details!"));
            }
            
            slotIndex++;
        }
    }
    
    private int getHistoryDisplaySlot(int index) {
        
        int[] slots = {19, 20, 21, 22, 23, 24, 25,
                      28, 29, 30, 31, 32, 33, 34,
                      37, 38, 39, 40, 41, 42, 43};
        return index < slots.length ? slots[index] : -1;
    }
    
    private void setupPaginationControls() {
        List<?> currentData = getCurrentViewData();
        if (currentData.isEmpty()) return;
        
        int totalPages = Math.max(1, (currentData.size() + itemsPerPage - 1) / itemsPerPage);
        
        
        if (currentPage > 0) {
            inventory.setItem(45, createItem(Material.ARROW,
                "§e← ᴘʀᴇᴠɪᴏᴜꜱ ᴘᴀɢᴇ",
                "§7Go to page " + currentPage,
                "§7",
                "§aClick to go back!"));
        }
        
        
        inventory.setItem(49, createItem(Material.COMPASS,
            "§b📍 ᴘᴀɢᴇ " + (currentPage + 1) + "/" + totalPages,
            "§7Showing " + currentView.getDisplayName().toLowerCase(),
            "§7Items: " + Math.min(itemsPerPage, currentData.size() - (currentPage * itemsPerPage)) + "/" + currentData.size()));
        
        
        if (currentPage < totalPages - 1) {
            inventory.setItem(53, createItem(Material.ARROW,
                "§e ɴᴇxᴛ ᴘᴀɢᴇ →",
                "§7Go to page " + (currentPage + 2),
                "§7",
                "§aClick to go forward!"));
        }
    }
    
    private void setupActionButtons() {
        
        inventory.setItem(46, createItem(Material.PLAYER_HEAD,
            "§b👤 ᴘʟᴀʏᴇʀ ᴘʀᴏꜰɪʟᴇ",
            "§7View full player profile",
            "§7",
            "§aClick to view profile!"));
        
        
        inventory.setItem(47, createItem(Material.PAPER,
            "§e📄 ᴇxᴘᴏʀᴛ ʜɪꜱᴛᴏʀʏ",
            "§7Export player history to file",
            "§7",
            "§aClick to export!"));
        
        
        inventory.setItem(51, createItem(Material.IRON_SWORD,
            "§6⚔ ᴀᴅᴅ ᴘᴜɴɪꜱʜᴍᴇɴᴛ",
            "§7Issue new punishment",
            "§7",
            "§aClick to punish!"));
        
        
        inventory.setItem(52, createItem(Material.BARRIER,
            "§c🗑 ᴄʟᴇᴀʀ ʜɪꜱᴛᴏʀʏ",
            "§7Clear player's history",
            "§c§lWARNING: This cannot be undone!",
            "§7",
            "§cClick to clear!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        
        if (slot >= 9 && slot <= 12) {
            HistoryView[] views = HistoryView.values();
            int viewIndex = slot - 9;
            if (viewIndex < views.length) {
                currentView = views[viewIndex];
                currentPage = 0; 
                refresh();
            }
            return;
        }
        
        switch (slot) {
            
            case 45: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
            case 53: 
                List<?> data = getCurrentViewData();
                int totalPages = Math.max(1, (data.size() + itemsPerPage - 1) / itemsPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
                
            
            case 46: 
                openPlayerProfile();
                break;
            case 47: 
                exportPlayerHistory();
                break;
            case 51: 
                openPunishmentIssuanceGUI();
                break;
            case 52: 
                confirmClearHistory();
                break;
                
            default:
                
                handleHistoryItemClick(slot);
                break;
        }
    }
    
    private void handleHistoryItemClick(int slot) {
        int itemIndex = getHistoryIndexFromSlot(slot);
        if (itemIndex >= 0) {
            int globalIndex = currentPage * itemsPerPage + itemIndex;
            
            switch (currentView) {
                case VIOLATIONS:
                    if (globalIndex < playerViolations.size()) {
                        Violation violation = playerViolations.get(globalIndex);
                        openViolationDetailsGUI(violation);
                    }
                    break;
                case PUNISHMENTS:
                    if (globalIndex < playerPunishments.size()) {
                        
                        player.sendMessage("§6⚔ Punishment details coming soon!");
                    }
                    break;
                case APPEALS:
                    
                    player.sendMessage("§e📝 Appeal details coming soon!");
                    break;
                case TIMELINE:
                    
                    player.sendMessage("§f📅 Timeline details coming soon!");
                    break;
            }
        }
    }
    
    private int getHistoryIndexFromSlot(int slot) {
        int[] slots = {19, 20, 21, 22, 23, 24, 25,
                      28, 29, 30, 31, 32, 33, 34,
                      37, 38, 39, 40, 41, 42, 43};
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) return i;
        }
        return -1;
    }
    
    
    private Material getCurrentViewMaterial(HistoryView view) {
        return currentView == view ? Material.LIME_STAINED_GLASS : Material.GRAY_STAINED_GLASS;
    }
    
    private String getViewColor(HistoryView view) {
        return currentView == view ? "§a" : "§7";
    }
    
    private List<?> getCurrentViewData() {
        switch (currentView) {
            case VIOLATIONS: return playerViolations;
            case PUNISHMENTS: return playerPunishments;
            case APPEALS: return new ArrayList<>(); 
            case TIMELINE: 
                List<Object> timeline = new ArrayList<>();
                timeline.addAll(playerViolations);
                timeline.addAll(playerPunishments);
                return timeline;
            default: return new ArrayList<>();
        }
    }
    
    private int getAppealsCount() {
        
        return 0;
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
    
    private Material getSeverityMaterial(String severity) {
        switch (severity.toLowerCase()) {
            case "low": return Material.LIME_DYE;
            case "medium": return Material.YELLOW_DYE;
            case "high": return Material.ORANGE_DYE;
            case "extreme": return Material.RED_DYE;
            default: return Material.GRAY_DYE;
        }
    }
    
    
    private void openPlayerProfile() {
        PlayerProfileGUI profileGUI = new PlayerProfileGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), profileGUI);
        profileGUI.open();
    }
    
    private void exportPlayerHistory() {
        player.sendMessage("§e📄 Exporting player history...");
        
        List<String> historyData = new ArrayList<>();
        historyData.add("Player: " + targetPlayer.getName());
        historyData.add("UUID: " + targetPlayer.getUniqueId());
        historyData.add("Export Date: " + dateFormat.format(new java.util.Date()));
        historyData.add("");
        historyData.add("VIOLATIONS (" + playerViolations.size() + " total):");
        
        for (Violation violation : playerViolations) {
            historyData.add("- " + dateFormat.format(violation.getTimestamp()) + 
                          " | " + violation.getCategory() + 
                          " | " + violation.getSeverity() + 
                          " | \"" + violation.getMessage() + "\"");
        }
        
        
        plugin.getExportManager().exportViolationsToCSV(playerViolations).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, 
                    targetPlayer.getName() + "'s history");
            });
        });
    }
    
    private void openPunishmentIssuanceGUI() {
        PlayerQuickActionsGUI actionsGUI = new PlayerQuickActionsGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), actionsGUI);
        actionsGUI.open();
    }
    
    private void confirmClearHistory() {
        player.closeInventory();
        player.sendMessage("§c⚠ ᴄʟᴇᴀʀ ʜɪꜱᴛᴏʀʏ ᴄᴏɴꜰɪʀᴍᴀᴛɪᴏɴ");
        player.sendMessage("§7Are you sure you want to clear all history for §f" + targetPlayer.getName() + "§7?");
        player.sendMessage("§c§lTHIS CANNOT BE UNDONE!");
        player.sendMessage("§7Type 'CONFIRM' in chat to proceed, or 'CANCEL' to abort.");
        
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§7Clear history confirmation timed out.");
                open(); 
            }
        }, 300L); 
    }
    
    private void openViolationDetailsGUI(Violation violation) {
        ViolationManagementGUI violationGUI = new ViolationManagementGUI(plugin, player, violation);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), violationGUI);
        violationGUI.open();
    }
}