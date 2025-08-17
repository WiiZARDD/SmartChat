package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CustomRateLimitGUI extends BaseGUI {
    
    public CustomRateLimitGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §6§lᴄᴜꜱᴛᴏᴍ ʀᴀᴛᴇ ʟɪᴍɪᴛ §8§l◆", 27);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        addNavigationItems();
        
        int currentLimit = plugin.getConfigManager().getConfig().getInt("api.rate-limit", 15);
        
        
        inventory.setItem(4, createItem(Material.ANVIL, 
            "§6§lᴄᴜꜱᴛᴏᴍ ʀᴀᴛᴇ ʟɪᴍɪᴛ",
            "§7Current: §e" + currentLimit + " requests/minute",
            "§7",
            "§8Fine-tune your rate limit"));
        
        
        inventory.setItem(9, createItem(Material.RED_CONCRETE, 
            "§c▼▼ -10",
            "§7Current: §e" + currentLimit,
            "§7New Value: §e" + Math.max(1, currentLimit - 10),
            "§7",
            "§cClick to decrease by 10!"));
        
        inventory.setItem(10, createItem(Material.ORANGE_CONCRETE, 
            "§6▼ -5",
            "§7Current: §e" + currentLimit,
            "§7New Value: §e" + Math.max(1, currentLimit - 5),
            "§7",
            "§6Click to decrease by 5!"));
        
        inventory.setItem(11, createItem(Material.YELLOW_CONCRETE, 
            "§e▼ -1",
            "§7Current: §e" + currentLimit,
            "§7New Value: §e" + Math.max(1, currentLimit - 1),
            "§7",
            "§eClick to decrease by 1!"));
        
        
        inventory.setItem(13, createItem(Material.GOLD_INGOT, 
            "§6⬥ " + currentLimit + "/min",
            "§7Current rate limit setting",
            "§7",
            "§8Use buttons to adjust"));
        
        
        inventory.setItem(15, createItem(Material.LIME_CONCRETE, 
            "§a▲ +1",
            "§7Current: §e" + currentLimit,
            "§7New Value: §e" + Math.min(300, currentLimit + 1),
            "§7",
            "§aClick to increase by 1!"));
        
        inventory.setItem(16, createItem(Material.GREEN_CONCRETE, 
            "§2▲ +5",
            "§7Current: §e" + currentLimit,
            "§7New Value: §e" + Math.min(300, currentLimit + 5),
            "§7",
            "§2Click to increase by 5!"));
        
        inventory.setItem(17, createItem(Material.BLUE_CONCRETE, 
            "§9▲▲ +10",
            "§7Current: §e" + currentLimit,
            "§7New Value: §e" + Math.min(300, currentLimit + 10),
            "§7",
            "§9Click to increase by 10!"));
        
        
        inventory.setItem(22, createItem(Material.BARRIER, 
            "§c🔄 Reset to 15",
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
        
        int currentLimit = plugin.getConfigManager().getConfig().getInt("api.rate-limit", 15);
        int newLimit = currentLimit;
        
        switch (slot) {
            case 9: 
                newLimit = Math.max(1, currentLimit - 10);
                break;
            case 10: 
                newLimit = Math.max(1, currentLimit - 5);
                break;
            case 11: 
                newLimit = Math.max(1, currentLimit - 1);
                break;
            case 15: 
                newLimit = Math.min(300, currentLimit + 1);
                break;
            case 16: 
                newLimit = Math.min(300, currentLimit + 5);
                break;
            case 17: 
                newLimit = Math.min(300, currentLimit + 10);
                break;
            case 22: 
                newLimit = 15;
                break;
            default:
                return;
        }
        
        plugin.getConfigManager().getConfig().set("api.rate-limit", newLimit);
        refresh();
        
        player.sendMessage("§aRate limit updated to " + newLimit + " requests/minute");
    }
}