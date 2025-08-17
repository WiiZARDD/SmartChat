package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.Appeal;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AppealReviewGUI extends BaseGUI {
    
    private List<Appeal> pendingAppeals;
    private int currentPage = 0;
    private final int appealsPerPage = 21; 
    
    public AppealReviewGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lᴀᴘᴘᴇᴀʟ ʀᴇᴠɪᴇᴡꜱ §8§l◆", 54);
        this.pendingAppeals = new ArrayList<>();
        loadAppeals();
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        int totalAppeals = plugin.getDatabaseManager().getPendingAppealsCount();
        inventory.setItem(4, createItem(Material.PAPER, 
            "§c§lᴀᴘᴘᴇᴀʟ ʀᴇᴠɪᴇᴡ ꜱʏꜱᴛᴇᴍ",
            "§7Review and process player appeals",
            "§7",
            "§8Total Pending: §e" + totalAppeals,
            "§8Page: §b" + (currentPage + 1) + "/" + Math.max(1, (totalAppeals + appealsPerPage - 1) / appealsPerPage)));
        
        
        setupControlButtons();
        
        
        displayAppeals();
        
        
        setupPageNavigation();
    }
    
    private void setupControlButtons() {
        
        inventory.setItem(1, createItem(Material.EMERALD, 
            "§a🔄 Refresh Appeals",
            "§7Click to refresh the appeal list",
            "§7",
            "§aLoads latest appeals from database"));
        
        
        inventory.setItem(7, createItem(Material.BOOK, 
            "§b📊 Appeal Statistics",
            "§7View appeal processing statistics",
            "§7",
            "§8Click to view detailed stats"));
        
        
        inventory.setItem(46, createItem(Material.COMMAND_BLOCK, 
            "§6⚙ Bulk Actions",
            "§7Perform actions on multiple appeals",
            "§7",
            "§8Coming soon!"));
    }
    
    private void displayAppeals() {
        if (pendingAppeals.isEmpty()) {
            
            inventory.setItem(22, createItem(Material.BARRIER, 
                "§7📭 No Pending Appeals",
                "§7There are currently no appeals",
                "§7waiting for review.",
                "§7",
                "§aGreat job keeping up with appeals!"));
            return;
        }
        
        int startIndex = currentPage * appealsPerPage;
        int endIndex = Math.min(startIndex + appealsPerPage, pendingAppeals.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            Appeal appeal = pendingAppeals.get(i);
            int slot = 10 + (i - startIndex) + ((i - startIndex) / 7) * 2; 
            
            List<String> appealLore = new ArrayList<>();
            appealLore.add("§7Player: §f" + appeal.getPlayerName());
            appealLore.add("§7Appeal ID: §e#" + appeal.getId());
            appealLore.add("§7Submitted: §b" + formatTimestamp(appeal.getTimestamp()));
            appealLore.add("§7");
            appealLore.add("§7Reason:");
            
            
            String reason = appeal.getReason();
            if (reason.length() > 30) {
                String[] words = reason.split(" ");
                StringBuilder line = new StringBuilder();
                for (String word : words) {
                    if (line.length() + word.length() > 30) {
                        appealLore.add("§f" + line.toString());
                        line = new StringBuilder(word);
                    } else {
                        if (line.length() > 0) line.append(" ");
                        line.append(word);
                    }
                }
                if (line.length() > 0) {
                    appealLore.add("§f" + line.toString());
                }
            } else {
                appealLore.add("§f" + reason);
            }
            
            appealLore.add("§7");
            appealLore.add("§aClick to review this appeal!");
            
            inventory.setItem(slot, createItem(Material.WRITABLE_BOOK, 
                "§e📋 Appeal #" + appeal.getId(), appealLore));
        }
    }
    
    private void setupPageNavigation() {
        int totalPages = Math.max(1, (pendingAppeals.size() + appealsPerPage - 1) / appealsPerPage);
        
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
    
    private void loadAppeals() {
        plugin.getDatabaseManager().getPendingAppeals().thenAccept(appeals -> {
            this.pendingAppeals = appeals;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 1: 
                loadAppeals();
                player.sendMessage("§aRefreshing appeals...");
                break;
                
            case 7: 
                showAppealStatistics();
                break;
                
            case 45: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
                
            case 53: 
                int totalPages = Math.max(1, (pendingAppeals.size() + appealsPerPage - 1) / appealsPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
                
            case 46: 
                player.sendMessage("§7Bulk actions coming soon!");
                break;
                
            default:
                
                handleAppealClick(slot, clickType);
                break;
        }
    }
    
    private void handleAppealClick(int slot, ClickType clickType) {
        
        int displayIndex = getAppealIndexFromSlot(slot);
        if (displayIndex == -1) return;
        
        int appealIndex = currentPage * appealsPerPage + displayIndex;
        if (appealIndex >= pendingAppeals.size()) return;
        
        Appeal appeal = pendingAppeals.get(appealIndex);
        
        
        openAppealReviewGUI(appeal);
    }
    
    private void openAppealReviewGUI(Appeal appeal) {
        AppealReviewDetailGUI appealDetailGUI = new AppealReviewDetailGUI(plugin, player, appeal);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), appealDetailGUI);
        appealDetailGUI.open();
    }
    
    private int getAppealIndexFromSlot(int slot) {
        
        if (slot < 10 || slot > 34) return -1;
        
        int row = (slot - 10) / 9;
        int col = (slot - 10) % 9;
        
        if (col > 6) return -1; 
        
        return row * 7 + col;
    }
    
    
    private void showAppealStatistics() {
        
        player.sendMessage("§7Appeal statistics coming soon!");
    }
    
    private String formatTimestamp(java.sql.Timestamp timestamp) {
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
}