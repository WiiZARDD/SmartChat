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

public class PlayerLookupGUI extends BaseGUI {
    
    private List<PlayerRecord> searchResults = new ArrayList<>();
    private String lastSearchTerm = "";
    private SearchCriteria searchCriteria = SearchCriteria.USERNAME;
    private int currentPage = 0;
    private final int resultsPerPage = 28; 
    
    public enum SearchCriteria {
        USERNAME("Username"),
        UUID("UUID"),
        HIGH_VIOLATIONS("High Violations (>5)"),
        RECENT_ACTIVITY("Recent Activity"),
        TRUST_SCORE("Low Trust Score"),
        ONLINE_STATUS("Online Players");
        
        private final String displayName;
        
        SearchCriteria(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public PlayerLookupGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §e§lᴘʟᴀʏᴇʀ ʟᴏᴏᴋᴜᴘ ꜱʏꜱᴛᴇᴍ §8§l◆", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PLAYER));
        addNavigationItems();
        
        setupHeader();
        setupSearchControls();
        setupQuickSearchOptions();
        setupSearchResults();
        setupPaginationControls();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Advanced player search system");
        headerLore.add("§7");
        headerLore.add("§7Search Criteria: §e" + searchCriteria.getDisplayName());
        headerLore.add("§7Last Search: " + (lastSearchTerm.isEmpty() ? "§7None" : "§e" + lastSearchTerm));
        headerLore.add("§7Results Found: §b" + searchResults.size());
        
        inventory.setItem(4, createItem(Material.SPYGLASS, 
            "§e§lᴘʟᴀʏᴇʀ ʟᴏᴏᴋᴜᴘ ꜱʏꜱᴛᴇᴍ", headerLore));
    }
    
    private void setupSearchControls() {
        
        inventory.setItem(9, createItem(Material.WRITABLE_BOOK, 
            "§d🔧 ꜱᴇᴀʀᴄʜ ᴄʀɪᴛᴇʀɪᴀ",
            "§7Select what to search by",
            "§7Current: §e" + searchCriteria.getDisplayName(),
            "§7",
            "§aClick to change criteria!"));
        
        
        inventory.setItem(10, createItem(Material.HOPPER, 
            "§e⚙ ǫᴜɪᴄᴋ ꜰɪʟᴛᴇʀꜱ",
            "§7Apply common search filters",
            "§7Recent players, active players, etc.",
            "§7",
            "§aClick to filter!"));
        
        
        inventory.setItem(11, createItem(Material.HOPPER_MINECART, 
            "§6⚙ ᴀᴅᴠᴀɴᴄᴇᴅ ꜰɪʟᴛᴇʀꜱ",
            "§7Multiple search criteria",
            "§7",
            "§8• Violation count ranges",
            "§8• Date ranges",
            "§8• Punishment history",
            "§7",
            "§aClick to configure!"));
        
        
        inventory.setItem(13, createItem(Material.BARRIER, 
            "§c🗑 ᴄʟᴇᴀʀ ꜱᴇᴀʀᴄʜ",
            "§7Clear current search results",
            "§7",
            "§aClick to clear!"));
        
        
        inventory.setItem(14, createItem(Material.WRITABLE_BOOK, 
            "§b📄 ᴇxᴘᴏʀᴛ ʀᴇꜱᴜʟᴛꜱ",
            "§7Export search results to file",
            "§7Results: §e" + searchResults.size() + " players",
            "§7",
            "§aClick to export!"));
        
        
        inventory.setItem(15, createItem(Material.CLOCK, 
            "§9🕒 ʀᴇᴄᴇɴᴛ ꜱᴇᴀʀᴄʜᴇꜱ",
            "§7View recent search history",
            "§7",
            "§aClick to view!"));
    }
    
    private void setupQuickSearchOptions() {
        
        inventory.setItem(19, createItem(Material.REDSTONE, 
            "§c⚠ ᴘʀᴏʙʟᴇᴍ ᴘʟᴀʏᴇʀꜱ",
            "§7Players with high violation rates",
            "§7Violation score > 50",
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(20, createItem(Material.LIME_DYE, 
            "§a🌟 ɴᴇᴡ ᴘʟᴀʏᴇʀꜱ",
            "§7Recently joined players",
            "§7Joined in last 7 days",
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(21, createItem(Material.DIAMOND, 
            "§b💎 ᴀᴄᴛɪᴠᴇ ᴘʟᴀʏᴇʀꜱ",
            "§7Highly active players",
            "§7Message count > 100",
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(22, createItem(Material.EMERALD, 
            "§a🏆 ᴛʀᴜꜱᴛᴇᴅ ᴘʟᴀʏᴇʀꜱ",
            "§7Players with high trust scores",
            "§7Trust score > 80",
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(23, createItem(Material.BARRIER, 
            "§4🚫 ʙᴀɴɴᴇᴅ ᴘʟᴀʏᴇʀꜱ",
            "§7Currently banned players",
            "§7Active bans only",
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(24, createItem(Material.MUSIC_DISC_11, 
            "§6🔇 ᴍᴜᴛᴇᴅ ᴘʟᴀʏᴇʀꜱ",
            "§7Currently muted players",
            "§7Active mutes only",
            "§7",
            "§aClick to search!"));
        
        
        inventory.setItem(25, createItem(Material.GOLDEN_SWORD, 
            "§e👑 ꜱᴛᴀꜰꜰ ᴍᴇᴍʙᴇʀꜱ",
            "§7Players with staff permissions",
            "§7Admin and moderator ranks",
            "§7",
            "§aClick to search!"));
    }
    
    private void setupSearchResults() {
        if (searchResults.isEmpty()) {
            inventory.setItem(31, createItem(Material.GRAY_DYE, 
                "§7📭 ɴᴏ ʀᴇꜱᴜʟᴛꜱ",
                "§7No players found matching criteria",
                "§7Try adjusting your search terms"));
            return;
        }
        
        
        int startIndex = currentPage * resultsPerPage;
        int endIndex = Math.min(startIndex + resultsPerPage, searchResults.size());
        
        
        for (int i = 28; i <= 43; i++) {
            if (i % 9 != 0 && i % 9 != 8) { 
                inventory.setItem(i, null);
            }
        }
        
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            PlayerRecord record = searchResults.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(record.getUuid());
            boolean isOnline = offlinePlayer.isOnline();
            
            List<String> resultLore = new ArrayList<>();
            resultLore.add("§7Status: " + (isOnline ? "§aOnline" : "§7Offline"));
            resultLore.add("§7UUID: §8" + record.getUuid().toString().substring(0, 8) + "...");
            
            if (record.getFirstSeen() != null) {
                resultLore.add("§7First Seen: §e" + formatDate(record.getFirstSeen().getTime()));
            }
            if (record.getLastSeen() != null && !isOnline) {
                resultLore.add("§7Last Seen: §e" + formatTimeAgo(record.getLastSeen().getTime()));
            }
            
            resultLore.add("§7");
            resultLore.add("§7Messages: §e" + record.getTotalMessages());
            resultLore.add("§7Violations: §c" + record.getFlaggedMessages());
            resultLore.add("§7Score: §6" + String.format("%.1f", record.getViolationScore()));
            resultLore.add("§7");
            resultLore.add("§aClick to view profile!");
            
            int displaySlot = getResultDisplaySlot(slotIndex);
            if (displaySlot != -1) {
                inventory.setItem(displaySlot, createItem(
                    isOnline ? Material.PLAYER_HEAD : Material.SKELETON_SKULL,
                    (isOnline ? "§a" : "§7") + record.getUsername(),
                    resultLore));
            }
            
            slotIndex++;
        }
    }
    
    private int getResultDisplaySlot(int index) {
        
        int[] slots = {28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        return index < slots.length ? slots[index] : -1;
    }
    
    private void setupPaginationControls() {
        if (searchResults.isEmpty()) return;
        
        int totalPages = Math.max(1, (searchResults.size() + resultsPerPage - 1) / resultsPerPage);
        
        
        if (currentPage > 0) {
            inventory.setItem(45, createItem(Material.ARROW, 
                "§e← ᴘʀᴇᴠɪᴏᴜꜱ ᴘᴀɢᴇ",
                "§7Go to page " + currentPage,
                "§7",
                "§aClick to go back!"));
        }
        
        
        inventory.setItem(49, createItem(Material.COMPASS, 
            "§b📍 ᴘᴀɢᴇ " + (currentPage + 1) + "/" + totalPages,
            "§7Showing results " + (currentPage * resultsPerPage + 1) + "-" + 
            Math.min((currentPage + 1) * resultsPerPage, searchResults.size()),
            "§7Total results: §e" + searchResults.size()));
        
        
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
                cycleCriteria();
                refresh();
                break;
            case 10: 
                openQuickFiltersMenu();
                break;
            case 11: 
                openAdvancedFiltersGUI();
                break;
            case 13: 
                clearSearch();
                break;
            case 14: 
                exportResults();
                break;
            case 15: 
                showRecentSearches();
                break;
            
            
            case 19: 
                searchProblemPlayers();
                break;
            case 20: 
                searchNewPlayers();
                break;
            case 21: 
                searchActivePlayers();
                break;
            case 22: 
                searchTrustedPlayers();
                break;
            case 23: 
                searchBannedPlayers();
                break;
            case 24: 
                searchMutedPlayers();
                break;
            case 25: 
                searchStaffMembers();
                break;
                
            
            case 45: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
            case 53: 
                int totalPages = Math.max(1, (searchResults.size() + resultsPerPage - 1) / resultsPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
                
            default:
                
                handleResultClick(slot);
                break;
        }
    }
    
    private void handleResultClick(int slot) {
        
        int resultIndex = getResultIndexFromSlot(slot);
        if (resultIndex >= 0) {
            int globalIndex = currentPage * resultsPerPage + resultIndex;
            if (globalIndex < searchResults.size()) {
                PlayerRecord record = searchResults.get(globalIndex);
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(record.getUuid());
                openPlayerProfile(targetPlayer);
            }
        }
    }
    
    private int getResultIndexFromSlot(int slot) {
        int[] slots = {28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) return i;
        }
        return -1;
    }
    
    
    private void cycleCriteria() {
        SearchCriteria[] values = SearchCriteria.values();
        int currentIndex = searchCriteria.ordinal();
        searchCriteria = values[(currentIndex + 1) % values.length];
        currentPage = 0;
    }
    
    private void openQuickFiltersMenu() {
        player.sendMessage("§e⚙ ǫᴜɪᴄᴋ ꜰɪʟᴛᴇʀꜱ");
        player.sendMessage("§7Select a quick filter option:");
        player.sendMessage("§a▶ §7Recent Players - showing last 20 players");
        searchRecentPlayers();
    }
    
    private void searchRecentPlayers() {
        lastSearchTerm = "Recent Players";
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            searchResults = records.stream()
                .limit(20)
                .collect(Collectors.toList());
            currentPage = 0;
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                refresh();
                player.sendMessage("§b🔍 Showing " + searchResults.size() + " recent players");
            });
        });
    }
    
    private void clearSearch() {
        searchResults.clear();
        lastSearchTerm = "";
        currentPage = 0;
        refresh();
        player.sendMessage("§a✓ Search results cleared!");
    }
    
    private void exportResults() {
        if (searchResults.isEmpty()) {
            player.sendMessage("§c✗ No results to export!");
            return;
        }
        
        plugin.getExportManager().exportPlayerRecordsToCSV(searchResults).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "search results");
            });
        });
    }
    
    private void showRecentSearches() {
        player.sendMessage("§9🕒 ʀᴇᴄᴇɴᴛ ꜱᴇᴀʀᴄʜᴇꜱ");
        player.sendMessage("§7Recent search history feature coming soon!");
    }
    
    
    private void searchProblemPlayers() {
        lastSearchTerm = "Problem Players";
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            searchResults = records.stream()
                .filter(r -> r.getViolationScore() > 50 || r.getFlaggedPercentage() > 20)
                .collect(Collectors.toList());
            currentPage = 0;
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                refresh();
                player.sendMessage("§c⚠ Found " + searchResults.size() + " problem players");
            });
        });
    }
    
    private void searchNewPlayers() {
        lastSearchTerm = "New Players";
        long weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            searchResults = records.stream()
                .filter(r -> r.getFirstSeen() != null && r.getFirstSeen().getTime() > weekAgo)
                .collect(Collectors.toList());
            currentPage = 0;
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                refresh();
                player.sendMessage("§a🌟 Found " + searchResults.size() + " new players");
            });
        });
    }
    
    private void searchActivePlayers() {
        lastSearchTerm = "Active Players";
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            searchResults = records.stream()
                .filter(r -> r.getTotalMessages() > 100)
                .collect(Collectors.toList());
            currentPage = 0;
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                refresh();
                player.sendMessage("§b💎 Found " + searchResults.size() + " active players");
            });
        });
    }
    
    private void searchTrustedPlayers() {
        lastSearchTerm = "Trusted Players";
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            searchResults = records.stream()
                .filter(r -> {
                    double trustScore = calculateTrustScore(r);
                    return trustScore > 80;
                })
                .collect(Collectors.toList());
            currentPage = 0;
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                refresh();
                player.sendMessage("§a🏆 Found " + searchResults.size() + " trusted players");
            });
        });
    }
    
    private void searchBannedPlayers() {
        lastSearchTerm = "Banned Players";
        
        player.sendMessage("§4🚫 Banned players search coming soon!");
    }
    
    private void searchMutedPlayers() {
        lastSearchTerm = "Muted Players";
        
        player.sendMessage("§6🔇 Muted players search coming soon!");
    }
    
    private void searchStaffMembers() {
        lastSearchTerm = "Staff Members";
        searchResults = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("smartchat.admin.*") || 
                onlinePlayer.hasPermission("smartchat.*")) {
                
                
                plugin.getDatabaseManager().getPlayerRecord(onlinePlayer.getUniqueId())
                    .thenAccept(record -> {
                        if (record != null) {
                            searchResults.add(record);
                        }
                    });
            }
        }
        currentPage = 0;
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            refresh();
            player.sendMessage("§e👑 Found " + searchResults.size() + " staff members");
        }, 20L);
    }
    
    
    private double calculateTrustScore(PlayerRecord record) {
        if (record.getTotalMessages() == 0) return 100.0;
        
        double baseScore = 100.0;
        double violationRate = (double) record.getFlaggedMessages() / record.getTotalMessages();
        baseScore -= violationRate * 50;
        baseScore -= record.getViolationScore() * 10;
        
        return Math.max(0, Math.min(100, baseScore));
    }
    
    private String formatTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long days = diff / (24 * 60 * 60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        
        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        return "Recently";
    }
    
    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd");
        return sdf.format(new java.util.Date(timestamp));
    }
    
    
    private void openAdvancedFiltersGUI() {
        player.sendMessage("§6⚙ Advanced filters GUI coming soon!");
    }
    
    private void openPlayerProfile(OfflinePlayer targetPlayer) {
        PlayerProfileGUI profileGUI = new PlayerProfileGUI(plugin, player, targetPlayer);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), profileGUI);
        profileGUI.open();
    }
}