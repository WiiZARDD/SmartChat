package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PlayerQuickActionsGUI extends BaseGUI {
    
    private final OfflinePlayer targetPlayer;
    
    public PlayerQuickActionsGUI(SmartChat plugin, Player player, OfflinePlayer targetPlayer) {
        super(plugin, player, "§8§l◆ §6§lǫᴜɪᴄᴋ ᴀᴄᴛɪᴏɴs §8§l◆", 27);
        this.targetPlayer = targetPlayer;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.ORANGE_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.PLAYER_HEAD, 
            "§6§lǫᴜɪᴄᴋ ᴀᴄᴛɪᴏɴs",
            "§7Target: §f" + targetPlayer.getName(),
            "§7",
            "§8Select an action to perform"));
        
        
        inventory.setItem(10, createItem(Material.PAPER, 
            "§e⚠ Issue Warning",
            "§7Send a warning to this player",
            "§7for minor violations.",
            "§7",
            "§8• Records warning in database",
            "§8• Notifies player",
            "§8• No immediate punishment",
            "§7",
            "§aClick to issue warning!"));
        
        
        inventory.setItem(12, createItem(Material.BARRIER, 
            "§6🔇 Mute Player",
            "§7Temporarily prevent this player",
            "§7from sending chat messages.",
            "§7",
            "§8• Choose duration",
            "§8• Add reason",
            "§8• Immediate effect",
            "§7",
            "§aClick to mute player!"));
        
        
        inventory.setItem(14, createItem(Material.ANVIL, 
            "§c🔨 Ban Player",
            "§7Remove this player from",
            "§7the server temporarily.",
            "§7",
            "§8• Choose duration",
            "§8• Add reason",
            "§8• Kicks player",
            "§7",
            "§aClick to ban player!"));
        
        
        inventory.setItem(16, createItem(Material.WATER_BUCKET, 
            "§b🧹 Clear History",
            "§7Clear this player's violation",
            "§7history and reset trust score.",
            "§7",
            "§8• Removes all violations",
            "§8• Resets statistics",
            "§8• Fresh start",
            "§7",
            "§cAdmin only - Use carefully!",
            "§7",
            "§aClick to clear history!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 10: 
                IssueWarningGUI warningGUI = new IssueWarningGUI(plugin, player, targetPlayer);
                plugin.getGuiManager().openGUIs.put(player.getUniqueId(), warningGUI);
                warningGUI.open();
                break;
                
            case 12: 
                QuickMuteGUI muteGUI = new QuickMuteGUI(plugin, player, targetPlayer);
                plugin.getGuiManager().openGUIs.put(player.getUniqueId(), muteGUI);
                muteGUI.open();
                break;
                
            case 14: 
                QuickBanGUI banGUI = new QuickBanGUI(plugin, player, targetPlayer);
                plugin.getGuiManager().openGUIs.put(player.getUniqueId(), banGUI);
                banGUI.open();
                break;
                
            case 16: 
                if (player.hasPermission("smartchat.admin.clearhistory")) {
                    clearPlayerHistory();
                } else {
                    player.sendMessage("§cYou don't have permission to clear player history!");
                }
                break;
        }
    }
    
    private void clearPlayerHistory() {
        player.sendMessage("§b🧹 Clearing history for " + targetPlayer.getName() + "...");
        
        
        plugin.getDatabaseManager().clearPlayerHistory(targetPlayer.getUniqueId()).thenAccept(success -> {
            if (player.isOnline()) {
                if (success) {
                    player.sendMessage("§a✓ History cleared successfully!");
                    player.sendMessage("§7All violations and statistics have been reset.");
                } else {
                    player.sendMessage("§c✗ Failed to clear history!");
                }
            }
        });
        
        player.closeInventory();
    }
}