package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.PlayerRecord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EnhancedPlayerManagementGUI extends BaseGUI {
    
    private List<PlayerRecord> playerRecords = new ArrayList<>();
    private List<Player> onlinePlayers = new ArrayList<>();
    private int currentPage = 0;
    private final int playersPerPage = 21; 
    private String searchFilter = "";
    private SortType sortType = SortType.VIOLATION_SCORE;
    private boolean showOnlineOnly = false;
    
    public enum SortType {
        VIOLATION_SCORE("Violation Score"),
        TOTAL_MESSAGES("Total Messages"),
        FLAGGED_MESSAGES("Flagged Messages"),
        ALPHABETICAL("Name (A-Z)"),
        LAST_SEEN("Last Seen");
        
        private final String displayName;
        
        SortType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public EnhancedPlayerManagementGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §b§lᴘʟᴀʏᴇʀ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ §8§l◆", 54);
        loadPlayerData();
    }
    
    private void loadPlayerData() {
        onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            this.playerRecords = records;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PLAYER));
        addNavigationItems();
        
        setupHeader();
        setupControlButtons();
        setupPlayerList();
        setupPaginationControls();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Advanced player management system");
        headerLore.add("§7");
        headerLore.add("§7Total Players: §e" + playerRecords.size());
        headerLore.add("§7Online Players: §a" + onlinePlayers.size());
        headerLore.add("§7Current Filter: " + (searchFilter.isEmpty() ? "§7None" : "§e" + searchFilter));
        headerLore.add("§7Sort: §b" + sortType.getDisplayName());
        headerLore.add("§7Show: " + (showOnlineOnly ? "§aOnline Only" : "§7All Players"));
        
        inventory.setItem(4, createItem(Material.PLAYER_HEAD, 
            "§b§lᴘʟᴀʏᴇʀ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ", headerLore));
    }
    
    private void setupControlButtons() {
        
        inventory.setItem(9, createItem(Material.COMPASS, 
            "§e🔍 ꜱᴇᴀʀᴄʜ ᴘʟᴀʏᴇʀꜱ",
            "§7Search for players by name",
            "§7Current filter: " + (searchFilter.isEmpty() ? "§7None" : "§e" + searchFilter),
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(10, createItem(Material.HOPPER, 
            "§d📊 ꜱᴏʀᴛ ᴏᴘᴛɪᴏɴꜱ",
            "§7Change sorting method",
            "§7Current: §b" + sortType.getDisplayName(),
            "§7",
            "§aClick to change sort!"));
        
        
        inventory.setItem(11, createItem(Material.BREWING_STAND, 
            "§6🔧 ꜰɪʟᴛᴇʀ ᴏᴘᴛɪᴏɴꜱ",
            "§7Filter players by status",
            "§7Current: " + (showOnlineOnly ? "§aOnline Only" : "§7All Players"),
            "§7",
            "§aClick to toggle filter!"));
        
        
        inventory.setItem(13, createItem(Material.DIAMOND_SWORD, 
            "§c⚡ ʙᴜʟᴋ ᴀᴄᴛɪᴏɴꜱ",
            "§7Perform actions on multiple players",
            "§7",
            "§8• Mass warnings",
            "§8• Bulk punishments",
            "§8• Group management",
            "§7",
            "§aClick to access!"));
        
        
        inventory.setItem(14, createItem(Material.BOOK, 
            "§a📈 ᴘʟᴀʏᴇʀ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ",
            "§7View comprehensive player analytics",
            "§7",
            "§8• Behavior trends",
            "§8• Violation patterns",
            "§8• Performance metrics",
            "§7",
            "§aClick to view!"));
        
        
        inventory.setItem(15, createItem(Material.WRITABLE_BOOK, 
            "§e📝 ᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴛᴇᴍᴘʟᴀᴛᴇꜱ",
            "§7Manage punishment templates",
            "§7",
            "§8• Quick punishments",
            "§8• Custom templates",
            "§8• Auto-escalation",
            "§7",
            "§aClick to manage!"));
        
        
        inventory.setItem(17, createItem(Material.CLOCK, 
            "§9🔄 ʀᴇꜰʀᴇꜱʜ ᴅᴀᴛᴀ",
            "§7Reload all player data",
            "§7Last updated: §e" + java.time.LocalTime.now().toString().substring(0, 5),
            "§7",
            "§aClick to refresh!"));
    }
    
    private void setupPlayerList() {
        List<PlayerRecord> filteredPlayers = getFilteredAndSortedPlayers();
        
        
        int totalPages = Math.max(1, (filteredPlayers.size() + playersPerPage - 1) / playersPerPage);
        int startIndex = currentPage * playersPerPage;
        int endIndex = Math.min(startIndex + playersPerPage, filteredPlayers.size());
        
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            PlayerRecord record = filteredPlayers.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(record.getUuid());
            boolean isOnline = offlinePlayer.isOnline();
            
            List<String> playerLore = new ArrayList<>();
            playerLore.add("§7Status: " + (isOnline ? "§aOnline" : "§7Offline"));
            
            if (record.getLastSeen() != null && !isOnline) {
                playerLore.add("§7Last Seen: §e" + formatTimeAgo(record.getLastSeen().getTime()));
            }
            
            playerLore.add("§7");
            playerLore.add("§7Total Messages: §e" + record.getTotalMessages());
            playerLore.add("§7Flagged Messages: §c" + record.getFlaggedMessages());
            playerLore.add("§7Violation Score: §6" + String.format("%.2f", record.getViolationScore()));
            playerLore.add("§7Flag Rate: " + getScoreColor(record.getFlaggedPercentage()) + 
                          String.format("%.1f%%", record.getFlaggedPercentage()));
            
            
            double trustScore = calculateTrustScore(record);
            playerLore.add("§7Trust Level: " + getTrustColor(trustScore) + getTrustLevel(trustScore));
            
            playerLore.add("§7");
            playerLore.add("§aClick to manage this player!");
            
            int displaySlot = getPlayerDisplaySlot(slotIndex);
            inventory.setItem(displaySlot, createPlayerHeadByUUID(
                record.getUuid(),
                (isOnline ? "§a" : "§7") + record.getUsername(), 
                playerLore));
            
            slotIndex++;
        }
        
        
        for (int i = slotIndex; i < playersPerPage; i++) {
            int displaySlot = getPlayerDisplaySlot(i);
            inventory.setItem(displaySlot, null);
        }
    }
    
    private int getPlayerDisplaySlot(int index) {
        
        int row = index / 7;
        int col = index % 7;
        return 19 + row * 9 + col;
    }
    
    private List<PlayerRecord> getFilteredAndSortedPlayers() {
        List<PlayerRecord> filtered = playerRecords.stream()
            .filter(record -> {
                if (showOnlineOnly) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(record.getUuid());
                    if (!offlinePlayer.isOnline()) return false;
                }
                
                if (!searchFilter.isEmpty()) {
                    return record.getUsername().toLowerCase().contains(searchFilter.toLowerCase());
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        
        switch (sortType) {
            case VIOLATION_SCORE:
                filtered.sort((a, b) -> Double.compare(b.getViolationScore(), a.getViolationScore()));
                break;
            case TOTAL_MESSAGES:
                filtered.sort((a, b) -> Integer.compare(b.getTotalMessages(), a.getTotalMessages()));
                break;
            case FLAGGED_MESSAGES:
                filtered.sort((a, b) -> Integer.compare(b.getFlaggedMessages(), a.getFlaggedMessages()));
                break;
            case ALPHABETICAL:
                filtered.sort((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));
                break;
            case LAST_SEEN:
                filtered.sort((a, b) -> {
                    if (a.getLastSeen() == null && b.getLastSeen() == null) return 0;
                    if (a.getLastSeen() == null) return 1;
                    if (b.getLastSeen() == null) return -1;
                    return b.getLastSeen().compareTo(a.getLastSeen());
                });
                break;
        }
        
        return filtered;
    }
    
    private void setupPaginationControls() {
        List<PlayerRecord> filteredPlayers = getFilteredAndSortedPlayers();
        int totalPages = Math.max(1, (filteredPlayers.size() + playersPerPage - 1) / playersPerPage);
        
        
        if (currentPage > 0) {
            inventory.setItem(45, createItem(Material.ARROW, 
                "§e← ᴘʀᴇᴠɪᴏᴜꜱ ᴘᴀɢᴇ",
                "§7Go to page " + currentPage,
                "§7",
                "§aClick to go back!"));
        }
        
        
        inventory.setItem(49, createItem(Material.PAPER, 
            "§b📄 ᴘᴀɢᴇ " + (currentPage + 1) + "/" + totalPages,
            "§7Showing " + Math.min(playersPerPage, filteredPlayers.size() - (currentPage * playersPerPage)) + " players",
            "§7Total filtered: §e" + filteredPlayers.size() + " players"));
        
        
        if (currentPage < totalPages - 1) {
            inventory.setItem(53, createItem(Material.ARROW, 
                "§e ɴᴇxᴛ ᴘᴀɢᴇ →",
                "§7Go to page " + (currentPage + 2),
                "§7",
                "§aClick to go forward!"));
        }
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 9: 
                openPlayerLookupGUI();
                break;
            case 10: 
                cycleSortType();
                refresh();
                break;
            case 11: 
                showOnlineOnly = !showOnlineOnly;
                currentPage = 0; 
                refresh();
                break;
            case 13: 
                openBulkActionsGUI();
                break;
            case 14: 
                openPlayerStatisticsGUI();
                break;
            case 15: 
                openPunishmentTemplatesGUI();
                break;
            case 17: 
                loadPlayerData();
                break;
            case 45: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
            case 53: 
                List<PlayerRecord> filtered = getFilteredAndSortedPlayers();
                int totalPages = Math.max(1, (filtered.size() + playersPerPage - 1) / playersPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
            default:
                
                handlePlayerClick(slot);
                break;
        }
    }
    
    private void handlePlayerClick(int slot) {
        List<PlayerRecord> filteredPlayers = getFilteredAndSortedPlayers();
        int playerIndex = getPlayerIndexFromSlot(slot);
        
        if (playerIndex >= 0) {
            int globalIndex = currentPage * playersPerPage + playerIndex;
            if (globalIndex < filteredPlayers.size()) {
                PlayerRecord record = filteredPlayers.get(globalIndex);
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(record.getUuid());
                
                
                PlayerProfileGUI profileGUI = new PlayerProfileGUI(plugin, player, targetPlayer);
                plugin.getGuiManager().openGUIs.put(player.getUniqueId(), profileGUI);
                profileGUI.open();
            }
        }
    }
    
    private int getPlayerIndexFromSlot(int slot) {
        
        if (slot >= 19 && slot <= 25) return slot - 19; 
        if (slot >= 28 && slot <= 34) return slot - 28 + 7; 
        if (slot >= 37 && slot <= 43) return slot - 37 + 14; 
        return -1;
    }
    
    
    private void cycleSortType() {
        SortType[] values = SortType.values();
        int currentIndex = sortType.ordinal();
        sortType = values[(currentIndex + 1) % values.length];
        currentPage = 0; 
    }
    
    private double calculateTrustScore(PlayerRecord record) {
        if (record.getTotalMessages() == 0) return 100.0;
        
        double baseScore = 100.0;
        double violationRate = (double) record.getFlaggedMessages() / record.getTotalMessages();
        baseScore -= violationRate * 50; 
        baseScore -= record.getViolationScore() * 10; 
        
        return Math.max(0, Math.min(100, baseScore));
    }
    
    private String getTrustColor(double score) {
        if (score >= 80) return "§a";
        if (score >= 60) return "§e";
        if (score >= 40) return "§6";
        return "§c";
    }
    
    private String getTrustLevel(double score) {
        if (score >= 90) return "Excellent";
        if (score >= 70) return "Good";
        if (score >= 50) return "Fair";
        if (score >= 30) return "Poor";
        return "Very Poor";
    }
    
    private String getScoreColor(double percentage) {
        if (percentage <= 5) return "§a";
        if (percentage <= 15) return "§e";
        if (percentage <= 30) return "§6";
        return "§c";
    }
    
    private String formatTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long days = diff / (24 * 60 * 60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long minutes = diff / (60 * 1000);
        
        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        if (minutes > 0) return minutes + "m ago";
        return "Just now";
    }
    
    
    private void openPlayerLookupGUI() {
        PlayerLookupGUI lookupGUI = new PlayerLookupGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), lookupGUI);
        lookupGUI.open();
    }
    
    private void openBulkActionsGUI() {
        BulkActionsGUI bulkGUI = new BulkActionsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), bulkGUI);
        bulkGUI.open();
    }
    
    private void openPlayerStatisticsGUI() {
        PlayerStatisticsGUI statsGUI = new PlayerStatisticsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), statsGUI);
        statsGUI.open();
    }
    
    private void openPunishmentTemplatesGUI() {
        PunishmentTemplatesGUI templatesGUI = new PunishmentTemplatesGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), templatesGUI);
        templatesGUI.open();
    }
    
    private void openIndividualPlayerManagementGUI(OfflinePlayer targetPlayer) {
        IndividualPlayerManagementGUI playerGUI = new IndividualPlayerManagementGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), playerGUI);
        playerGUI.open();
    }
    
}