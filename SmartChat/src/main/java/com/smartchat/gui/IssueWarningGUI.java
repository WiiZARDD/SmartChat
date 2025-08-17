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

public class IssueWarningGUI extends BaseGUI {
    
    private OfflinePlayer targetPlayer = null;
    private String warningType = "General";
    private String warningMessage = "Please follow server rules";
    private boolean publicWarning = false;
    
    public IssueWarningGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §e§lIssue Warning §8§l◆", 45);
    }
    
    public IssueWarningGUI(SmartChat plugin, Player player, OfflinePlayer target) {
        super(plugin, player, "§8§l◆ §e§lIssue Warning §8§l◆", 45);
        this.targetPlayer = target;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.PAPER, 
            "§e§lWarning Issuance System",
            "§7Issue warnings to players for violations",
            "§7",
            "§8Select player, type, and message"));
        
        
        setupPlayerSelection();
        
        
        setupWarningTypeSelection();
        
        
        setupWarningMessage();
        
        
        setupWarningOptions();
        
        
        setupActionButtons();
    }
    
    private void setupPlayerSelection() {
        if (targetPlayer == null) {
            inventory.setItem(10, createItem(Material.PLAYER_HEAD, 
                "§e🔍 Select Player",
                "§7Click to choose a player to warn",
                "§7",
                "§cNo player selected",
                "§7",
                "§aClick to select player!"));
        } else {
            List<String> playerLore = new ArrayList<>();
            playerLore.add("§7Selected Player: §f" + targetPlayer.getName());
            playerLore.add("§7UUID: §e" + targetPlayer.getUniqueId());
            playerLore.add("§7Status: " + (targetPlayer.isOnline() ? "§aOnline" : "§7Offline"));
            
            
            playerLore.add("§7Previous Warnings: §c" + "0"); 
            
            playerLore.add("§7");
            playerLore.add("§aClick to change player!");
            
            inventory.setItem(10, createItem(Material.PLAYER_HEAD, 
                "§f👤 " + targetPlayer.getName(), playerLore));
        }
    }
    
    private void setupWarningTypeSelection() {
        inventory.setItem(12, createItem(Material.WRITABLE_BOOK, 
            "§d📝 Warning Type",
            "§7Current: §f" + warningType,
            "§7",
            "§aClick to change type!"));
        
        
        inventory.setItem(19, createItem(Material.YELLOW_DYE, 
            "§eGeneral Warning",
            "§7Generic rule violation warning",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(20, createItem(Material.ORANGE_DYE, 
            "§6Chat Violation",
            "§7Warning for inappropriate chat",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(21, createItem(Material.RED_DYE, 
            "§cBehavior Warning",
            "§7Warning for disruptive behavior",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(22, createItem(Material.PURPLE_DYE, 
            "§5Final Warning",
            "§7Last warning before punishment",
            "§7",
            "§cStrong warning - next violation = punishment",
            "§7",
            "§aClick to select!"));
    }
    
    private void setupWarningMessage() {
        inventory.setItem(14, createItem(Material.BOOK, 
            "§b💬 Warning Message",
            "§7Current message:",
            "§f\"" + warningMessage + "\"",
            "§7",
            "§aClick to customize message!"));
        
        
        inventory.setItem(28, createItem(Material.PAPER, 
            "§7\"Please follow server rules\"",
            "§7Standard reminder message",
            "§7",
            "§aClick to use this message!"));
        
        inventory.setItem(29, createItem(Material.PAPER, 
            "§7\"Mind your language in chat\"",
            "§7Chat behavior reminder",
            "§7",
            "§aClick to use this message!"));
        
        inventory.setItem(30, createItem(Material.PAPER, 
            "§7\"Be respectful to other players\"",
            "§7Behavior reminder message",
            "§7",
            "§aClick to use this message!"));
        
        inventory.setItem(31, createItem(Material.PAPER, 
            "§7\"This is your final warning\"",
            "§7Final warning message",
            "§7",
            "§cStrong warning message",
            "§7",
            "§aClick to use this message!"));
        
        inventory.setItem(32, createItem(Material.WRITABLE_BOOK, 
            "§d📝 Custom Message",
            "§7Write a custom warning message",
            "§7",
            "§aClick to enter custom message!"));
    }
    
    private void setupWarningOptions() {
        
        inventory.setItem(16, createItem(publicWarning ? Material.LIME_DYE : Material.GRAY_DYE, 
            publicWarning ? "§a📢 Public Warning" : "§7🔒 Private Warning",
            "§7Visibility: " + (publicWarning ? "§aPublic (all players see)" : "§7Private (player only)"),
            "§7",
            "§8Public warnings are visible to all players",
            "§8Private warnings are sent only to the target",
            "§7",
            "§aClick to toggle visibility!"));
        
        
        String severityColor = "";
        String severityText = "";
        switch (warningType) {
            case "General":
                severityColor = "§e";
                severityText = "Low";
                break;
            case "Chat Violation":
                severityColor = "§6";
                severityText = "Medium";
                break;
            case "Behavior Warning":
                severityColor = "§c";
                severityText = "High";
                break;
            case "Final Warning":
                severityColor = "§4";
                severityText = "Critical";
                break;
            default:
                severityColor = "§7";
                severityText = "Unknown";
        }
        
        inventory.setItem(34, createItem(Material.BEACON, 
            severityColor + "⚠ Severity: " + severityText,
            "§7Warning severity level based on type",
            "§7",
            "§8Severity affects escalation tracking",
            "§8and automatic punishment progression",
            "§7",
            "§8Determined by warning type"));
    }
    
    private void setupActionButtons() {
        
        boolean canWarn = targetPlayer != null;
        
        inventory.setItem(40, createItem(canWarn ? Material.YELLOW_CONCRETE : Material.GRAY_CONCRETE, 
            canWarn ? "§e⚠ ISSUE WARNING" : "§7⚠ Select Player First",
            canWarn ? "§7Warn " + targetPlayer.getName() : "§7No player selected",
            canWarn ? "§7Type: §f" + warningType : "§7",
            canWarn ? "§7Message: §f\"" + warningMessage + "\"" : "§7Choose a player to warn",
            canWarn ? "§7Visibility: " + (publicWarning ? "§aPublic" : "§7Private") : "§7",
            "§7",
            canWarn ? "§aClick to issue warning!" : "§8Select a player first"));
        
        
        inventory.setItem(38, createItem(Material.BOOK, 
            "§b📋 Preview Warning",
            "§7Review warning details before issuing",
            "§7",
            "§8Player: " + (targetPlayer != null ? targetPlayer.getName() : "None"),
            "§8Type: " + warningType,
            "§8Message: \"" + warningMessage + "\"",
            "§8Visibility: " + (publicWarning ? "Public" : "Private"),
            "§7",
            "§aClick to preview!"));
        
        
        inventory.setItem(36, createItem(Material.CLOCK, 
            "§6📚 Warning History",
            "§7View this player's warning history",
            "§7",
            "§8See previous warnings and patterns",
            "§8Check escalation status",
            "§7",
            "§aClick to view history!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 10: 
                selectPlayer();
                break;
            case 12: 
                changeWarningType();
                break;
            case 14: 
                changeWarningMessage();
                break;
            case 16: 
                publicWarning = !publicWarning;
                refresh();
                break;
            case 19: 
                warningType = "General";
                refresh();
                break;
            case 20: 
                warningType = "Chat Violation";
                refresh();
                break;
            case 21: 
                warningType = "Behavior Warning";
                refresh();
                break;
            case 22: 
                warningType = "Final Warning";
                refresh();
                break;
            case 28: 
                warningMessage = "Please follow server rules";
                refresh();
                break;
            case 29: 
                warningMessage = "Mind your language in chat";
                refresh();
                break;
            case 30: 
                warningMessage = "Be respectful to other players";
                refresh();
                break;
            case 31: 
                warningMessage = "This is your final warning";
                refresh();
                break;
            case 32: 
                customMessage();
                break;
            case 36: 
                viewWarningHistory();
                break;
            case 38: 
                previewWarning();
                break;
            case 40: 
                issueWarning();
                break;
        }
    }
    
    private void selectPlayer() {
        player.sendMessage("§e=== Select Player to Warn ===");
        player.sendMessage("§7Online players:");
        
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cNo players online to warn!");
            return;
        }
        
        for (int i = 0; i < Math.min(onlinePlayers.size(), 10); i++) {
            Player p = onlinePlayers.get(i);
            if (!p.equals(player)) {
                player.sendMessage("§8" + (i + 1) + ". §f" + p.getName() + " §7(online)");
            }
        }
        
        player.sendMessage("§7");
        player.sendMessage("§7Type: §e/sc warn <player>");
        player.closeInventory();
    }
    
    private void changeWarningType() {
        player.sendMessage("§d📝 Warning types available:");
        player.sendMessage("§e1. General Warning §7- Standard rule reminder");
        player.sendMessage("§62. Chat Violation §7- Chat behavior issue");
        player.sendMessage("§c3. Behavior Warning §7- Disruptive behavior");
        player.sendMessage("§54. Final Warning §7- Last warning before punishment");
    }
    
    private void changeWarningMessage() {
        player.sendMessage("§b💬 Current warning message:");
        player.sendMessage("§f\"" + warningMessage + "\"");
        player.sendMessage("§7Use the template buttons or type a custom message.");
    }
    
    private void customMessage() {
        player.sendMessage("§d📝 Enter custom warning message:");
        player.sendMessage("§7Type your custom warning message in chat");
        player.sendMessage("§8The message will be automatically set when you type it");
        player.closeInventory();
    }
    
    private void viewWarningHistory() {
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ No player selected to view history for!");
            return;
        }
        
        player.sendMessage("§6📚 Warning history for " + targetPlayer.getName() + ":");
        player.sendMessage("§7Warning history viewer coming soon!");
        
    }
    
    private void previewWarning() {
        player.sendMessage("§b📋 === Warning Preview ===");
        player.sendMessage("§7Player: §f" + (targetPlayer != null ? targetPlayer.getName() : "None selected"));
        player.sendMessage("§7Type: §f" + warningType);
        player.sendMessage("§7Message: §f\"" + warningMessage + "\"");
        player.sendMessage("§7Visibility: " + (publicWarning ? "§aPublic" : "§7Private"));
        player.sendMessage("§7Staff: §b" + player.getName());
        player.sendMessage("§7");
        
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ Cannot issue warning: No player selected!");
        } else {
            player.sendMessage("§aReady to issue warning. Click the warning button to proceed.");
        }
    }
    
    private void issueWarning() {
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ Cannot issue warning: No player selected!");
            return;
        }
        
        player.sendMessage("§e⚠ Issuing " + warningType.toLowerCase() + " to " + targetPlayer.getName() + "...");
        
        
        player.sendMessage("§a✓ Warning issued to " + targetPlayer.getName() + "!");
        player.sendMessage("§7Type: §f" + warningType);
        player.sendMessage("§7Message: §f\"" + warningMessage + "\"");
        
        
        if (targetPlayer.isOnline()) {
            Player onlineTarget = targetPlayer.getPlayer();
            onlineTarget.sendMessage("");
            onlineTarget.sendMessage("§e⚠§l WARNING §e⚠");
            onlineTarget.sendMessage("§7Type: §f" + warningType);
            onlineTarget.sendMessage("§7Message: §f" + warningMessage);
            onlineTarget.sendMessage("§7Staff: §b" + player.getName());
            onlineTarget.sendMessage("");
        }
        
        
        if (publicWarning) {
            String publicMessage = "§e⚠ " + targetPlayer.getName() + " §7has been warned for: §f" + warningMessage;
            Bukkit.broadcastMessage(publicMessage);
        }
        
        player.closeInventory();
    }
}