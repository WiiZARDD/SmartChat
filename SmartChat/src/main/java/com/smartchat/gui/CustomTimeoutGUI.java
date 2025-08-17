package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CustomTimeoutGUI extends BaseGUI {
    
    public CustomTimeoutGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lCustom Timeout §8§l◆", 27);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        int currentTimeout = plugin.getConfigManager().getConfig().getInt("api.timeout", 10);
        
        
        inventory.setItem(4, createItem(Material.ANVIL, 
            "§c§lCustom Timeout",
            "§7Current: §e" + currentTimeout + " seconds",
            "§7",
            "§8Fine-tune your timeout duration"));
        
        
        inventory.setItem(9, createItem(Material.RED_CONCRETE, 
            "§c▼▼ -10s",
            "§7Current: §e" + currentTimeout + "s",
            "§7New Value: §e" + Math.max(1, currentTimeout - 10) + "s",
            "§7",
            "§cClick to decrease by 10 seconds!"));
        
        inventory.setItem(10, createItem(Material.ORANGE_CONCRETE, 
            "§6▼ -5s",
            "§7Current: §e" + currentTimeout + "s",
            "§7New Value: §e" + Math.max(1, currentTimeout - 5) + "s",
            "§7",
            "§6Click to decrease by 5 seconds!"));
        
        inventory.setItem(11, createItem(Material.YELLOW_CONCRETE, 
            "§e▼ -1s",
            "§7Current: §e" + currentTimeout + "s",
            "§7New Value: §e" + Math.max(1, currentTimeout - 1) + "s",
            "§7",
            "§eClick to decrease by 1 second!"));
        
        
        inventory.setItem(13, createItem(Material.REDSTONE, 
            "§c⬥ " + currentTimeout + " seconds",
            "§7Current timeout setting",
            "§7",
            "§8Use buttons to adjust"));
        
        
        inventory.setItem(15, createItem(Material.LIME_CONCRETE, 
            "§a▲ +1s",
            "§7Current: §e" + currentTimeout + "s",
            "§7New Value: §e" + Math.min(120, currentTimeout + 1) + "s",
            "§7",
            "§aClick to increase by 1 second!"));
        
        inventory.setItem(16, createItem(Material.GREEN_CONCRETE, 
            "§2▲ +5s",
            "§7Current: §e" + currentTimeout + "s",
            "§7New Value: §e" + Math.min(120, currentTimeout + 5) + "s",
            "§7",
            "§2Click to increase by 5 seconds!"));
        
        inventory.setItem(17, createItem(Material.BLUE_CONCRETE, 
            "§9▲▲ +10s",
            "§7Current: §e" + currentTimeout + "s",
            "§7New Value: §e" + Math.min(120, currentTimeout + 10) + "s",
            "§7",
            "§9Click to increase by 10 seconds!"));
        
        
        inventory.setItem(22, createItem(Material.BARRIER, 
            "§c🔄 Reset to 10s",
            "§7Reset to recommended value",
            "§7",
            "§cClick to reset!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        int currentTimeout = plugin.getConfigManager().getConfig().getInt("api.timeout", 10);
        int newTimeout = currentTimeout;
        
        switch (slot) {
            case 9: 
                newTimeout = Math.max(1, currentTimeout - 10);
                break;
            case 10: 
                newTimeout = Math.max(1, currentTimeout - 5);
                break;
            case 11: 
                newTimeout = Math.max(1, currentTimeout - 1);
                break;
            case 15: 
                newTimeout = Math.min(120, currentTimeout + 1);
                break;
            case 16: 
                newTimeout = Math.min(120, currentTimeout + 5);
                break;
            case 17: 
                newTimeout = Math.min(120, currentTimeout + 10);
                break;
            case 22: 
                newTimeout = 10;
                break;
            default:
                return;
        }
        
        plugin.getConfigManager().getConfig().set("api.timeout", newTimeout);
        refresh();
        
        player.sendMessage("§aTimeout updated to " + newTimeout + " seconds");
    }
}