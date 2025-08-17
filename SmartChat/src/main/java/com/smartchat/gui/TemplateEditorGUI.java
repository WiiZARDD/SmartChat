package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TemplateEditorGUI extends BaseGUI {
    
    private final String templateId;
    
    public TemplateEditorGUI(SmartChat plugin, Player player, String templateId) {
        super(plugin, player, "§8§l◆ §6§lᴇᴅɪᴛ ᴛᴇᴍᴘʟᴀᴛᴇ §8§l◆", 54);
        this.templateId = templateId;
    }
    
    public TemplateEditorGUI(SmartChat plugin, Player player, Object template) {
        super(plugin, player, "§8§l◆ §6§lᴇᴅɪᴛ ᴛᴇᴍᴘʟᴀᴛᴇ §8§l◆", 54);
        this.templateId = template.toString();
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        inventory.setItem(4, createItem(Material.WRITABLE_BOOK,
            "§6§lᴇᴅɪᴛ ᴛᴇᴍᴘʟᴀᴛᴇ",
            "§7Edit punishment template",
            "§7Template ID: §f" + templateId,
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