package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AutoEscalationGUI extends BaseGUI {
    
    public AutoEscalationGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lᴀᴜᴛᴏ ᴇꜱᴄᴀʟᴀᴛɪᴏɴ §8§l◆", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        inventory.setItem(4, createItem(Material.REDSTONE,
            "§c§lᴀᴜᴛᴏ ᴇꜱᴄᴀʟᴀᴛɪᴏɴ",
            "§7Automatic punishment escalation",
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