package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RateLimitAdjustmentGUI extends BaseGUI {
    
    public RateLimitAdjustmentGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §6§lʀᴀᴛᴇ ʟɪᴍɪᴛ ꜱᴇᴛᴛɪɴɢꜱ §8§l◆", 27);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        addNavigationItems();
        
        int currentLimit = plugin.getConfigManager().getConfig().getInt("api.rate-limit", 15);
        
        
        inventory.setItem(4, createItem(Material.CLOCK, 
            "§6§lʀᴀᴛᴇ ʟɪᴍɪᴛ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ",
            "§7Current: §e" + currentLimit + " requests/minute",
            "§7",
            "§8Adjust API request rate limiting"));
        
        
        inventory.setItem(10, createItem(Material.RED_CONCRETE, 
            "§c🐌 Conservative (5/min)",
            "§7Safe for free API tiers",
            "§7Very low chance of hitting limits",
            "§7",
            "§c5 requests per minute",
            "§7",
            "§aClick to set conservative limit!"));
        
        inventory.setItem(11, createItem(Material.ORANGE_CONCRETE, 
            "§6📊 Balanced (15/min)",
            "§7Recommended for most servers",
            "§7Good balance of speed and safety",
            "§7",
            "§615 requests per minute",
            "§7",
            "§aClick to set balanced limit!"));
        
        inventory.setItem(12, createItem(Material.YELLOW_CONCRETE, 
            "§e⚡ Active (30/min)",
            "§7For busy servers with paid API",
            "§7Higher performance but watch limits",
            "§7",
            "§e30 requests per minute",
            "§7",
            "§aClick to set active limit!"));
        
        inventory.setItem(13, createItem(Material.LIME_CONCRETE, 
            "§a🚀 Aggressive (60/min)",
            "§7Maximum performance mode",
            "§7Requires high API tier",
            "§7",
            "§a60 requests per minute",
            "§7",
            "§aClick to set aggressive limit!"));
        
        
        inventory.setItem(15, createItem(Material.ANVIL, 
            "§b⚙ Custom Value",
            "§7Current: §e" + currentLimit,
            "§7",
            "§7Fine-tune the rate limit",
            "§7to your specific needs.",
            "§7",
            "§aClick for custom adjustment!"));
        
        
        inventory.setItem(22, createItem(Material.BOOK, 
            "§7📖 Rate Limit Info",
            "§7Rate limiting controls how many",
            "§7API requests can be made per minute.",
            "§7",
            "§8• Too low = Slow moderation",
            "§8• Too high = API quota exceeded",
            "§8• Recommended: 15-30 for most servers",
            "§7",
            "§8Check your API plan limits!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        int newLimit = -1;
        
        switch (slot) {
            case 10: 
                newLimit = 5;
                break;
            case 11: 
                newLimit = 15;
                break;
            case 12: 
                newLimit = 30;
                break;
            case 13: 
                newLimit = 60;
                break;
            case 15: 
                openCustomAdjustment();
                return;
        }
        
        if (newLimit > 0) {
            plugin.getConfigManager().getConfig().set("api.rate-limit", newLimit);
            refresh();
            
            String limitType = "";
            switch (newLimit) {
                case 5: limitType = " (Conservative)"; break;
                case 15: limitType = " (Balanced)"; break;
                case 30: limitType = " (Active)"; break;
                case 60: limitType = " (Aggressive)"; break;
            }
            
            player.sendMessage("§aRate limit updated to " + newLimit + "/minute" + limitType);
        }
    }
    
    private void openCustomAdjustment() {
        CustomRateLimitGUI customGUI = new CustomRateLimitGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), customGUI);
        customGUI.open();
    }
}