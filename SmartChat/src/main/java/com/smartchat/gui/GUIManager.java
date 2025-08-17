package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager implements Listener {
    
    private final SmartChat plugin;
    public final Map<UUID, BaseGUI> openGUIs;
    
    public GUIManager(SmartChat plugin) {
        this.plugin = plugin;
        this.openGUIs = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void openMainGUI(Player player) {
        if (!player.hasPermission("smartchat.gui")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        MainGUI mainGUI = new MainGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), mainGUI);
        mainGUI.open();
    }
    
    public void openConfigGUI(Player player) {
        if (!player.hasPermission("smartchat.gui.config")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        ConfigGUI configGUI = new ConfigGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), configGUI);
        configGUI.open();
    }
    
    public void openPlayerManagementGUI(Player player) {
        if (!player.hasPermission("smartchat.gui.players")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        EnhancedPlayerManagementGUI playerGUI = new EnhancedPlayerManagementGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), playerGUI);
        playerGUI.open();
    }
    
    public void openMonitoringGUI(Player player) {
        if (!player.hasPermission("smartchat.gui.monitor")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        MonitoringGUI monitorGUI = new MonitoringGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), monitorGUI);
        monitorGUI.open();
    }
    
    public void openAppealReviewGUI(Player player) {
        if (!player.hasPermission("smartchat.gui.appeals")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        AppealReviewGUI appealGUI = new AppealReviewGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), appealGUI);
        appealGUI.open();
    }
    
    public void openRecentViolationsGUI(Player player) {
        if (!player.hasPermission("smartchat.gui.monitor")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        RecentViolationsGUI violationsGUI = new RecentViolationsGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), violationsGUI);
        violationsGUI.open();
    }
    
    public void openAnalyticsGUI(Player player) {
        if (!player.hasPermission("smartchat.gui.analytics")) {
            player.sendMessage(plugin.getConfigManager().getMessage("errors.no-permission"));
            return;
        }
        
        AnalyticsReportsGUI analyticsGUI = new AnalyticsReportsGUI(plugin, player);
        openGUIs.put(player.getUniqueId(), analyticsGUI);
        analyticsGUI.open();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        BaseGUI gui = openGUIs.get(player.getUniqueId());
        
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                gui.handleClick(event.getSlot(), event.getCurrentItem(), event.getClick());
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        BaseGUI gui = openGUIs.get(player.getUniqueId());
        
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            gui.onClose();
            openGUIs.remove(player.getUniqueId());
        }
    }
    
    public void closeGUI(Player player) {
        BaseGUI gui = openGUIs.remove(player.getUniqueId());
        if (gui != null) {
            player.closeInventory();
        }
    }
    
    public void closeAllGUIs() {
        for (UUID uuid : openGUIs.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        }
        openGUIs.clear();
    }
}