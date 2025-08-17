package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ThresholdAdjustmentGUI extends BaseGUI {
    
    private final String configPath;
    private final String thresholdName;
    private final String description;
    
    public ThresholdAdjustmentGUI(SmartChat plugin, Player player, String configPath, String thresholdName, String description) {
        super(plugin, player, "§8§l◆ §e§lAdjust " + thresholdName + " §8§l◆", 27);
        this.configPath = configPath;
        this.thresholdName = thresholdName;
        this.description = description;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        addNavigationItems();
        
        double currentValue = plugin.getConfigManager().getConfig().getDouble(configPath, 0.75);
        
        
        inventory.setItem(4, createItem(Material.WRITABLE_BOOK, 
            "§e§l" + thresholdName + " Threshold",
            "§7" + description,
            "§7",
            "§7Current Value: §e" + formatPercentage(currentValue),
            "§7",
            "§8Adjust the threshold using the buttons below"));
        
        
        inventory.setItem(10, createItem(Material.RED_CONCRETE, 
            "§c▼▼ Decrease by 10%",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7New Value: §e" + formatPercentage(Math.max(0.0, currentValue - 0.10)),
            "§7",
            "§cClick to decrease by 10%!"));
        
        inventory.setItem(11, createItem(Material.ORANGE_CONCRETE, 
            "§6▼ Decrease by 5%",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7New Value: §e" + formatPercentage(Math.max(0.0, currentValue - 0.05)),
            "§7",
            "§6Click to decrease by 5%!"));
        
        inventory.setItem(12, createItem(Material.YELLOW_CONCRETE, 
            "§e▼ Decrease by 1%",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7New Value: §e" + formatPercentage(Math.max(0.0, currentValue - 0.01)),
            "§7",
            "§eClick to decrease by 1%!"));
        
        
        inventory.setItem(13, createItem(Material.DIAMOND, 
            "§b⬥ Current Value",
            "§7" + thresholdName + " Threshold",
            "§7",
            "§bCurrent: §e" + formatPercentage(currentValue),
            "§7",
            "§8This is the current threshold value"));
        
        
        inventory.setItem(14, createItem(Material.LIME_CONCRETE, 
            "§a▲ Increase by 1%",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7New Value: §e" + formatPercentage(Math.min(1.0, currentValue + 0.01)),
            "§7",
            "§aClick to increase by 1%!"));
        
        inventory.setItem(15, createItem(Material.GREEN_CONCRETE, 
            "§2▲ Increase by 5%",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7New Value: §e" + formatPercentage(Math.min(1.0, currentValue + 0.05)),
            "§7",
            "§2Click to increase by 5%!"));
        
        inventory.setItem(16, createItem(Material.BLUE_CONCRETE, 
            "§9▲▲ Increase by 10%",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7New Value: §e" + formatPercentage(Math.min(1.0, currentValue + 0.10)),
            "§7",
            "§9Click to increase by 10%!"));
        
        
        inventory.setItem(22, createItem(Material.BARRIER, 
            "§c🔄 Reset to Default",
            "§7Reset threshold to default value",
            "§7",
            "§7Current: §e" + formatPercentage(currentValue),
            "§7Default: §e75%",
            "§7",
            "§cClick to reset to default!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        double currentValue = plugin.getConfigManager().getConfig().getDouble(configPath, 0.75);
        double newValue = currentValue;
        
        switch (slot) {
            case 10: 
                newValue = Math.max(0.0, currentValue - 0.10);
                break;
            case 11: 
                newValue = Math.max(0.0, currentValue - 0.05);
                break;
            case 12: 
                newValue = Math.max(0.0, currentValue - 0.01);
                break;
            case 14: 
                newValue = Math.min(1.0, currentValue + 0.01);
                break;
            case 15: 
                newValue = Math.min(1.0, currentValue + 0.05);
                break;
            case 16: 
                newValue = Math.min(1.0, currentValue + 0.10);
                break;
            case 22: 
                newValue = 0.75;
                break;
            default:
                return;
        }
        
        
        plugin.getConfigManager().getConfig().set(configPath, newValue);
        refresh();
        
        player.sendMessage("§a" + thresholdName + " threshold updated to " + formatPercentage(newValue));
    }
}