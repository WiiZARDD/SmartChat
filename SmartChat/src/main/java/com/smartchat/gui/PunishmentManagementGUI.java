package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PunishmentManagementGUI extends BaseGUI {
    
    public PunishmentManagementGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §6§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ §8§l◆", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.ORANGE_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.IRON_SWORD, 
            "§6§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ ꜱʏꜱᴛᴇᴍ",
            "§7Manage active punishments and sanctions",
            "§7",
            "§8Mutes, bans, warnings, and appeals"));
        
        
        setupOverviewSection();
        
        
        setupQuickActions();
        
        
        setupManagementTools();
        
        
        setupStatisticsSection();
    }
    
    private void setupOverviewSection() {
        
        inventory.setItem(10, createItem(Material.BARRIER, 
            "§c🔇 Active Mutes",
            "§7View and manage active mutes",
            "§7",
            "§8• Currently muted players",
            "§8• Mute duration and reasons",
            "§8• Unmute or modify existing mutes",
            "§7",
            "§aClick to view active mutes!"));
        
        
        inventory.setItem(12, createItem(Material.ANVIL, 
            "§4🔨 Active Bans",
            "§7View and manage active bans",
            "§7",
            "§8• Currently banned players",
            "§8• Ban duration and reasons",
            "§8• Unban or modify existing bans",
            "§7",
            "§aClick to view active bans!"));
        
        
        inventory.setItem(14, createItem(Material.WRITABLE_BOOK, 
            "§e📋 Pending Appeals",
            "§7Review punishment appeals",
            "§7",
            "§8• Players appealing punishments",
            "§8• Appeal reasons and evidence",
            "§8• Approve or deny appeals",
            "§7",
            "§aClick to review appeals!"));
        
        
        inventory.setItem(16, createItem(Material.PAPER, 
            "§e⚠ Recent Warnings",
            "§7View recent warnings issued",
            "§7",
            "§8• Warning history and patterns",
            "§8• Player escalation tracking",
            "§8• Warning effectiveness analysis",
            "§7",
            "§aClick to view warnings!"));
    }
    
    private void setupQuickActions() {
        
        inventory.setItem(19, createItem(Material.RED_DYE, 
            "§c🔇 Quick Mute Player",
            "§7Quickly mute a player",
            "§7",
            "§8Select player and duration",
            "§8Specify reason for mute",
            "§8Apply mute immediately",
            "§7",
            "§aClick to mute a player!"));
        
        
        inventory.setItem(21, createItem(Material.REDSTONE_BLOCK, 
            "§4🔨 Quick Ban Player",
            "§7Quickly ban a player",
            "§7",
            "§8Select player and duration",
            "§8Specify reason for ban",
            "§8Apply ban immediately",
            "§7",
            "§aClick to ban a player!"));
        
        
        inventory.setItem(23, createItem(Material.YELLOW_DYE, 
            "§e⚠ Issue Warning",
            "§7Issue a warning to a player",
            "§7",
            "§8Select player and violation",
            "§8Add custom warning message",
            "§8Track warning in player history",
            "§7",
            "§aClick to issue warning!"));
        
        
        inventory.setItem(25, createItem(Material.COMMAND_BLOCK, 
            "§6⚙ Bulk Actions",
            "§7Perform actions on multiple players",
            "§7",
            "§8Mass unmute/unban operations",
            "§8Bulk warning for related incidents",
            "§8Administrative cleanup tools",
            "§7",
            "§aClick for bulk actions!"));
    }
    
    private void setupManagementTools() {
        
        inventory.setItem(28, createItem(Material.SPYGLASS, 
            "§b🔍 Player Lookup",
            "§7Search for specific player records",
            "§7",
            "§8Find player by name or UUID",
            "§8View complete punishment history",
            "§8Access all related records",
            "§7",
            "§aClick to search players!"));
        
        
        inventory.setItem(30, createItem(Material.BOOK, 
            "§a📚 Punishment History",
            "§7Browse complete punishment logs",
            "§7",
            "§8Chronological punishment records",
            "§8Filter by type, staff, or date",
            "§8Export punishment reports",
            "§7",
            "§aClick to view history!"));
        
        
        inventory.setItem(32, createItem(Material.WRITABLE_BOOK, 
            "§d📝 Punishment Templates",
            "§7Manage punishment templates",
            "§7",
            "§8Pre-configured punishment reasons",
            "§8Standard duration presets",
            "§8Consistent messaging templates",
            "§7",
            "§aClick to manage templates!"));
        
        
        inventory.setItem(34, createItem(Material.GOLDEN_APPLE, 
            "§6👑 Staff Permissions",
            "§7Manage staff punishment permissions",
            "§7",
            "§8Configure who can ban/mute",
            "§8Set maximum punishment durations",
            "§8Review staff action logs",
            "§7",
            "§aClick to manage permissions!"));
    }
    
    private void setupStatisticsSection() {
        
        inventory.setItem(37, createItem(Material.BEACON, 
            "§e📊 Punishment Statistics",
            "§7View punishment effectiveness data",
            "§7",
            "§8Punishment success rates",
            "§8Recidivism analysis",
            "§8Staff performance metrics",
            "§7",
            "§aClick to view statistics!"));
        
        
        inventory.setItem(39, createItem(Material.MAP, 
            "§b📈 Trends & Patterns",
            "§7Analyze punishment trends",
            "§7",
            "§8Peak violation times",
            "§8Common violation patterns",
            "§8Seasonal behavior changes",
            "§7",
            "§aClick to view trends!"));
        
        
        inventory.setItem(41, createItem(Material.CHEST, 
            "§3📦 Export Tools",
            "§7Export punishment data",
            "§7",
            "§8Generate punishment reports",
            "§8Export to CSV/JSON formats",
            "§8Create compliance documentation",
            "§7",
            "§aClick to export data!"));
        
        
        inventory.setItem(43, createItem(Material.REDSTONE, 
            "§c🤖 Automation Settings",
            "§7Configure automatic punishments",
            "§7",
            "§8Auto-escalation rules",
            "§8Repeat offender handling",
            "§8Temporary punishment removal",
            "§7",
            "§aClick for automation settings!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 10: 
                openActiveMutesGUI();
                break;
            case 12: 
                openActiveBansGUI();
                break;
            case 14: 
                openPendingAppealsGUI();
                break;
            case 16: 
                openRecentWarningsGUI();
                break;
            case 19: 
                openQuickMuteGUI();
                break;
            case 21: 
                openQuickBanGUI();
                break;
            case 23: 
                openIssueWarningGUI();
                break;
            case 25: 
                openBulkActionsGUI();
                break;
            case 28: 
                openPlayerLookupGUI();
                break;
            case 30: 
                openPunishmentHistoryGUI();
                break;
            case 32: 
                openTemplatesGUI();
                break;
            case 34: 
                openStaffPermissionsGUI();
                break;
            case 37: 
                openStatisticsGUI();
                break;
            case 39: 
                openTrendsGUI();
                break;
            case 41: 
                openExportGUI();
                break;
            case 43: 
                openAutomationGUI();
                break;
        }
    }
    
    private void openActiveMutesGUI() {
        EnhancedPlayerManagementGUI playerMgmtGUI = new EnhancedPlayerManagementGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), playerMgmtGUI);
        playerMgmtGUI.open();
    }
    
    private void openActiveBansGUI() {
        EnhancedPlayerManagementGUI playerMgmtGUI = new EnhancedPlayerManagementGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), playerMgmtGUI);
        playerMgmtGUI.open();
    }
    
    private void openPendingAppealsGUI() {
        
        plugin.getGuiManager().openAppealReviewGUI(player);
    }
    
    private void openRecentWarningsGUI() {
        RecentViolationsGUI warningsGUI = new RecentViolationsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), warningsGUI);
        warningsGUI.open();
    }
    
    private void openQuickMuteGUI() {
        QuickMuteGUI quickMuteGUI = new QuickMuteGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), quickMuteGUI);
        quickMuteGUI.open();
    }
    
    private void openQuickBanGUI() {
        QuickBanGUI quickBanGUI = new QuickBanGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), quickBanGUI);
        quickBanGUI.open();
    }
    
    private void openIssueWarningGUI() {
        IssueWarningGUI warningGUI = new IssueWarningGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), warningGUI);
        warningGUI.open();
    }
    
    private void openBulkActionsGUI() {
        player.sendMessage("§6⚙ Bulk actions management coming soon!");
        player.sendMessage("§7This will allow mass operations on multiple players.");
    }
    
    private void openPlayerLookupGUI() {
        player.sendMessage("§b🔍 Player lookup system coming soon!");
        player.sendMessage("§7This will allow searching for specific players and their records.");
    }
    
    private void openPunishmentHistoryGUI() {
        player.sendMessage("§a📚 Punishment history viewer coming soon!");
        player.sendMessage("§7This will show complete punishment logs with filtering options.");
    }
    
    private void openTemplatesGUI() {
        player.sendMessage("§d📝 Punishment templates management coming soon!");
        player.sendMessage("§7This will allow creating and managing punishment reason templates.");
    }
    
    private void openStaffPermissionsGUI() {
        player.sendMessage("§6👑 Staff permissions management coming soon!");
        player.sendMessage("§7This will configure what punishment actions each staff rank can perform.");
    }
    
    private void openStatisticsGUI() {
        PunishmentStatisticsGUI statsGUI = new PunishmentStatisticsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), statsGUI);
        statsGUI.open();
    }
    
    private void openTrendsGUI() {
        TrendsAndPatternsGUI trendsGUI = new TrendsAndPatternsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), trendsGUI);
        trendsGUI.open();
    }
    
    private void openExportGUI() {
        player.sendMessage("§3📦 Exporting punishment data...");
        
        
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(playerRecords -> {
            plugin.getExportManager().exportPlayerRecordsToCSV(playerRecords).thenAccept(exportFile -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getExportManager().notifyExportComplete(player, exportFile, "punishment data");
                });
            });
        });
    }
    
    private void openAutomationGUI() {
        player.sendMessage("§c🤖 Automation settings coming soon!");
        player.sendMessage("§7This will configure automatic punishment escalation and removal.");
    }
}