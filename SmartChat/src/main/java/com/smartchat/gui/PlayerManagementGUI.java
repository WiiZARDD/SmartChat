package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerManagementGUI extends BaseGUI {
    
    public PlayerManagementGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §b§lᴘʟᴀʏᴇʀ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PLAYER));
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.PLAYER_HEAD, 
            "§b§lᴘʟᴀʏᴇʀ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ",
            "§7Manage players and their violations",
            "§7",
            "§8Click on a player to view details"));
        
        
        inventory.setItem(10, createItem(Material.COMPASS, 
            "§e🔍 Search Player",
            "§7Search for a specific player",
            "§7",
            "§aClick to open search!"));
        
        
        inventory.setItem(12, createItem(Material.REDSTONE, 
            "§c📋 Recent Violations",
            "§7View recent chat violations",
            "§7",
            "§aClick to view!"));
        
        
        inventory.setItem(14, createItem(Material.BOOK, 
            "§a📊 Player Statistics",
            "§7View player behavior statistics",
            "§7",
            "§aClick to view!"));
        
        
        inventory.setItem(16, createItem(Material.IRON_SWORD, 
            "§6⚔ Punishment Management",
            "§7Manage active punishments",
            "§7",
            "§aClick to manage!"));
        
        
        setupOnlinePlayersDisplay();
    }
    
    private void setupOnlinePlayersDisplay() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int startSlot = 28;
        int maxDisplay = 18; 
        
        for (int i = 0; i < Math.min(onlinePlayers.size(), maxDisplay); i++) {
            Player onlinePlayer = onlinePlayers.get(i);
            
            
            List<String> playerLore = new ArrayList<>();
            playerLore.add("§7Status: §aOnline");
            playerLore.add("§7");
            
            try {
                
                playerLore.add("§7Messages: §e" + "N/A");
                playerLore.add("§7Violations: §c" + "N/A");
                playerLore.add("§7Last Violation: §7" + "Never");
            } catch (Exception e) {
                playerLore.add("§7Stats: §cError loading");
            }
            
            playerLore.add("§7");
            playerLore.add("§aClick to manage this player!");
            
            inventory.setItem(startSlot + i, createItem(Material.PLAYER_HEAD, 
                "§f" + onlinePlayer.getName(), playerLore));
        }
        
        
        if (onlinePlayers.size() > maxDisplay) {
            inventory.setItem(46, createItem(Material.GRAY_DYE, 
                "§7... and " + (onlinePlayers.size() - maxDisplay) + " more online",
                "§7Click to view all players"));
        }
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 10: 
                player.sendMessage("§7Type the player name in chat:");
                player.sendMessage("§e/sc player <name>");
                player.closeInventory();
                break;
                
            case 12: 
                openRecentViolationsGUI();
                break;
                
            case 14: 
                openPlayerStatsGUI();
                break;
                
            case 16: 
                openPunishmentManagementGUI();
                break;
                
            case 46: 
                openAllPlayersGUI();
                break;
                
            default:
                
                if (slot >= 28 && slot <= 45) {
                    handlePlayerClick(slot, clickType);
                }
                break;
        }
    }
    
    private void handlePlayerClick(int slot, ClickType clickType) {
        
        ItemStack item = inventory.getItem(slot);
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        
        String playerName = item.getItemMeta().getDisplayName().substring(2); 
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
        
        
        openPlayerManagementGUI(targetPlayer);
    }
    
    private void openPlayerManagementGUI(OfflinePlayer targetPlayer) {
        
        player.sendMessage("§7Opening management options for " + targetPlayer.getName() + "...");
        player.sendMessage("§7Player management GUI coming soon!");
    }
    
    private void openRecentViolationsGUI() {
        plugin.getGuiManager().openRecentViolationsGUI(player);
    }
    
    private void openPlayerStatsGUI() {
        player.sendMessage("§7Player statistics GUI coming soon!");
        
    }
    
    private void openPunishmentManagementGUI() {
        PunishmentManagementGUI punishmentGUI = new PunishmentManagementGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), punishmentGUI);
        punishmentGUI.open();
    }
    
    private void openAllPlayersGUI() {
        player.sendMessage("§7All players GUI coming soon!");
        
    }
    
}