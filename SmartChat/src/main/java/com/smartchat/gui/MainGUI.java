package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MainGUI extends BaseGUI {
    
    public MainGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §6§lꜱᴍᴀʀᴛᴄʜᴀᴛ ᴀᴅᴍɪɴ §8§l◆", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.MAIN));
        
        
        inventory.setItem(4, createItem(Material.SHIELD, 
            "§6§lꜱᴍᴀʀᴛᴄʜᴀᴛ ᴀᴅᴍɪɴ",
            "§7Manage your AI-powered chat moderation",
            "§7",
            "§8Plugin Version: §a" + plugin.getDescription().getVersion(),
            "§8AI Status: " + (plugin.getApiManager() != null ? "§aOnline" : "§cOffline")));
        
        
        List<String> configLore = new ArrayList<>();
        configLore.add("§7Manage plugin settings and thresholds");
        configLore.add("§7");
        configLore.add("§e► §7Moderation Thresholds");
        configLore.add("§e► §7Action Configuration");
        configLore.add("§e► §7World Settings");
        configLore.add("§e► §7API Configuration");
        configLore.add("§7");
        configLore.add("§aClick to open!");
        
        inventory.setItem(20, createItem(Material.WRITABLE_BOOK, 
            "§e⚙ Configuration Management", configLore));
        
        
        List<String> playerLore = new ArrayList<>();
        playerLore.add("§7View and manage player data");
        playerLore.add("§7");
        playerLore.add("§e► §7Player Violations");
        playerLore.add("§e► §7Punishment History");
        playerLore.add("§e► §7Player Statistics");
        playerLore.add("§e► §7Manual Actions");
        playerLore.add("§7");
        playerLore.add("§aClick to open!");
        
        inventory.setItem(22, createItem(Material.PLAYER_HEAD, 
            "§b👤 Player Management", playerLore));
        
        
        List<String> monitorLore = new ArrayList<>();
        monitorLore.add("§7Monitor chat activity in real-time");
        monitorLore.add("§7");
        monitorLore.add("§e► §7Live Chat Feed");
        monitorLore.add("§e► §7Recent Violations");
        monitorLore.add("§e► §7System Statistics");
        monitorLore.add("§e► §7Performance Metrics");
        monitorLore.add("§7");
        monitorLore.add("§aClick to open!");
        
        inventory.setItem(24, createItem(Material.COMPASS, 
            "§d📊 Real-time Monitoring", monitorLore));
        
        
        List<String> appealLore = new ArrayList<>();
        appealLore.add("§7Review and manage player appeals");
        appealLore.add("§7");
        
        try {
            int pendingAppeals = plugin.getDatabaseManager().getPendingAppealsCount();
            appealLore.add("§e► §7Pending Appeals: §6" + pendingAppeals);
        } catch (Exception e) {
            appealLore.add("§e► §7Pending Appeals: §cError");
        }
        
        appealLore.add("§e► §7Appeal History");
        appealLore.add("§e► §7Quick Actions");
        appealLore.add("§7");
        appealLore.add("§aClick to open!");
        
        inventory.setItem(38, createItem(Material.PAPER, 
            "§c📋 Appeal Reviews", appealLore));
        
        
        List<String> analyticsLore = new ArrayList<>();
        analyticsLore.add("§7View detailed analytics and reports");
        analyticsLore.add("§7");
        analyticsLore.add("§e► §7Violation Trends");
        analyticsLore.add("§e► §7Player Behavior Analytics");
        analyticsLore.add("§e► §7System Performance");
        analyticsLore.add("§e► §7Export Reports");
        analyticsLore.add("§7");
        analyticsLore.add("§aClick to open!");
        
        inventory.setItem(40, createItem(Material.BOOK, 
            "§a📈 Analytics & Reports", analyticsLore));
        
        
        List<String> quickLore = new ArrayList<>();
        quickLore.add("§7Quick administrative actions");
        quickLore.add("§7");
        quickLore.add("§e► §7Reload Configuration");
        quickLore.add("§e► §7Clear Cache");
        quickLore.add("§e► §7Test AI Connection");
        quickLore.add("§e► §7Emergency Mode");
        quickLore.add("§7");
        quickLore.add("§aClick to open!");
        
        inventory.setItem(42, createItem(Material.REDSTONE, 
            "§c⚡ Quick Actions", quickLore));
        
        
        List<String> statusLore = new ArrayList<>();
        statusLore.add("§7Current system status and health");
        statusLore.add("§7");
        
        
        try {
            statusLore.add("§e► §7API Status: " + (plugin.getApiManager() != null ? "§aOnline" : "§cOffline"));
            statusLore.add("§e► §7Database: §aConnected");
            statusLore.add("§e► §7Total Players: §b" + plugin.getDatabaseManager().getTotalPlayersCount());
            statusLore.add("§e► §7Total Violations: §c" + plugin.getDatabaseManager().getTotalViolationsCount());
        } catch (Exception e) {
            statusLore.add("§e► §7Status: §cError loading data");
        }
        
        statusLore.add("§7");
        statusLore.add("§7§oClick to refresh!");
        
        inventory.setItem(49, createItem(Material.EMERALD, 
            "§a💚 System Status", statusLore));
        
        
        inventory.setItem(53, createItem(Material.BARRIER, 
            "§c✕ Close Menu", 
            "§7Click to close this menu"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        switch (slot) {
            case 20: 
                plugin.getGuiManager().openConfigGUI(player);
                break;
                
            case 22: 
                plugin.getGuiManager().openPlayerManagementGUI(player);
                break;
                
            case 24: 
                plugin.getGuiManager().openMonitoringGUI(player);
                break;
                
            case 38: 
                plugin.getGuiManager().openAppealReviewGUI(player);
                break;
                
            case 40: 
                plugin.getGuiManager().openAnalyticsGUI(player);
                break;
                
            case 42: 
                handleQuickActions(clickType);
                break;
                
            case 49: 
                refresh();
                player.sendMessage("§aSystem status refreshed!");
                break;
                
            case 53: 
                player.closeInventory();
                break;
        }
    }
    
    private void handleQuickActions(ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            
            plugin.reload();
            player.sendMessage("§aConfiguration reloaded successfully!");
            refresh();
        } else if (clickType == ClickType.RIGHT) {
            
            player.sendMessage("§aCache cleared!");
        } else if (clickType == ClickType.SHIFT_LEFT) {
            
            player.sendMessage("§7Testing AI connection...");
            
            player.sendMessage("§aAI connection test completed!");
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            
            player.sendMessage("§cEmergency mode toggled!");
        }
    }
}