package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class IndividualPlayerManagementGUI extends BaseGUI {
    
    private final OfflinePlayer targetPlayer;
    
    public IndividualPlayerManagementGUI(SmartChat plugin, Player player, OfflinePlayer targetPlayer) {
        super(plugin, player, "§8§l◆ §b§lᴘʟᴀʏᴇʀ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ §8§l◆", 54);
        this.targetPlayer = targetPlayer;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PLAYER));
        addNavigationItems();
        
        inventory.setItem(4, createItem(Material.PLAYER_HEAD,
            "§b§lᴘʟᴀʏᴇʀ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ",
            "§7Managing: §f" + targetPlayer.getName(),
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