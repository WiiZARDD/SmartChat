package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class DetailedAnalysisGUI extends BaseGUI {
    
    public DetailedAnalysisGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §b§lᴅᴇᴛᴀɪʟᴇᴅ ᴀɴᴀʟʏꜱɪꜱ §8§l◆", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.MONITOR));
        addNavigationItems();
        
        inventory.setItem(4, createItem(Material.SPYGLASS,
            "§b§lᴅᴇᴛᴀɪʟᴇᴅ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Advanced statistical analysis",
            "§7",
            "§cComing soon!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
        }
    }
}