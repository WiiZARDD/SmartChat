package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TimeoutAdjustmentGUI extends BaseGUI {
    
    public TimeoutAdjustmentGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lTimeout Settings §8§l◆", 27);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        int currentTimeout = plugin.getConfigManager().getConfig().getInt("api.timeout", 10);
        
        
        inventory.setItem(4, createItem(Material.CLOCK, 
            "§c§lTimeout Configuration",
            "§7Current: §e" + currentTimeout + " seconds",
            "§7",
            "§8Adjust API request timeout duration"));
        
        
        inventory.setItem(10, createItem(Material.GREEN_CONCRETE, 
            "§a⚡ Fast (5 seconds)",
            "§7Quick failure detection",
            "§7Good for fast connections",
            "§7",
            "§a5 second timeout",
            "§7",
            "§aClick to set fast timeout!"));
        
        inventory.setItem(11, createItem(Material.YELLOW_CONCRETE, 
            "§e📊 Balanced (10 seconds)",
            "§7Recommended for most servers",
            "§7Good balance of speed and reliability",
            "§7",
            "§e10 second timeout",
            "§7",
            "§aClick to set balanced timeout!"));
        
        inventory.setItem(12, createItem(Material.ORANGE_CONCRETE, 
            "§6⏳ Patient (20 seconds)",
            "§7For slower connections",
            "§7More patience for API responses",
            "§7",
            "§620 second timeout",
            "§7",
            "§aClick to set patient timeout!"));
        
        inventory.setItem(13, createItem(Material.RED_CONCRETE, 
            "§c🐌 Very Patient (30 seconds)",
            "§7Maximum patience mode",
            "§7For very slow connections",
            "§7",
            "§c30 second timeout",
            "§7",
            "§aClick to set very patient timeout!"));
        
        
        inventory.setItem(15, createItem(Material.ANVIL, 
            "§b⚙ Custom Value",
            "§7Current: §e" + currentTimeout + " seconds",
            "§7",
            "§7Fine-tune the timeout duration",
            "§7to your connection speed.",
            "§7",
            "§aClick for custom adjustment!"));
        
        
        inventory.setItem(22, createItem(Material.BOOK, 
            "§7📖 Timeout Info",
            "§7Timeout controls how long to wait",
            "§7for API responses before giving up.",
            "§7",
            "§8• Too low = Frequent failures",
            "§8• Too high = Slow failure detection",
            "§8• Recommended: 5-15 seconds",
            "§7",
            "§8Consider your internet speed!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        int newTimeout = -1;
        
        switch (slot) {
            case 10: 
                newTimeout = 5;
                break;
            case 11: 
                newTimeout = 10;
                break;
            case 12: 
                newTimeout = 20;
                break;
            case 13: 
                newTimeout = 30;
                break;
            case 15: 
                openCustomAdjustment();
                return;
        }
        
        if (newTimeout > 0) {
            plugin.getConfigManager().getConfig().set("api.timeout", newTimeout);
            refresh();
            
            String timeoutType = "";
            switch (newTimeout) {
                case 5: timeoutType = " (Fast)"; break;
                case 10: timeoutType = " (Balanced)"; break;
                case 20: timeoutType = " (Patient)"; break;
                case 30: timeoutType = " (Very Patient)"; break;
            }
            
            player.sendMessage("§aTimeout updated to " + newTimeout + " seconds" + timeoutType);
        }
    }
    
    private void openCustomAdjustment() {
        CustomTimeoutGUI customGUI = new CustomTimeoutGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), customGUI);
        customGUI.open();
    }
}