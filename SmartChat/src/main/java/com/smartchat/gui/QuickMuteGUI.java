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

public class QuickMuteGUI extends BaseGUI {
    
    private OfflinePlayer targetPlayer = null;
    private int muteDuration = 300; 
    private String muteReason = "Chat violation";
    
    public QuickMuteGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lQuick Mute §8§l◆", 45);
    }
    
    public QuickMuteGUI(SmartChat plugin, Player player, OfflinePlayer target) {
        super(plugin, player, "§8§l◆ §c§lQuick Mute §8§l◆", 45);
        this.targetPlayer = target;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.BARRIER, 
            "§c§lQuick Mute System",
            "§7Quickly mute a player for violations",
            "§7",
            "§8Select player, duration, and reason"));
        
        
        setupPlayerSelection();
        
        
        setupDurationSelection();
        
        
        setupReasonSelection();
        
        
        setupActionButtons();
    }
    
    private void setupPlayerSelection() {
        if (targetPlayer == null) {
            inventory.setItem(10, createItem(Material.PLAYER_HEAD, 
                "§e🔍 Select Player",
                "§7Click to choose a player to mute",
                "§7",
                "§cNo player selected",
                "§7",
                "§aClick to select player!"));
        } else {
            List<String> playerLore = new ArrayList<>();
            playerLore.add("§7Selected Player: §f" + targetPlayer.getName());
            playerLore.add("§7UUID: §e" + targetPlayer.getUniqueId());
            playerLore.add("§7Status: " + (targetPlayer.isOnline() ? "§aOnline" : "§7Offline"));
            playerLore.add("§7");
            playerLore.add("§aClick to change player!");
            
            inventory.setItem(10, createItem(Material.PLAYER_HEAD, 
                "§f👤 " + targetPlayer.getName(), playerLore));
        }
    }
    
    private void setupDurationSelection() {
        
        inventory.setItem(12, createItem(Material.CLOCK, 
            "§6⏱ Mute Duration",
            "§7Current: §e" + formatDuration(muteDuration),
            "§7",
            "§aClick to adjust duration!"));
        
        
        inventory.setItem(19, createItem(Material.YELLOW_CONCRETE, 
            "§e5 Minutes",
            "§7Quick mute for 5 minutes",
            "§7",
            "§8Good for minor violations",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(20, createItem(Material.ORANGE_CONCRETE, 
            "§630 Minutes",
            "§7Moderate mute for 30 minutes",
            "§7",
            "§8Good for repeated violations",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(21, createItem(Material.RED_CONCRETE, 
            "§c1 Hour",
            "§7Longer mute for 1 hour",
            "§7",
            "§8Good for serious violations",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(22, createItem(Material.PURPLE_CONCRETE, 
            "§524 Hours",
            "§7Extended mute for 24 hours",
            "§7",
            "§8Good for severe violations",
            "§7",
            "§aClick to select!"));
    }
    
    private void setupReasonSelection() {
        inventory.setItem(14, createItem(Material.WRITABLE_BOOK, 
            "§d📝 Mute Reason",
            "§7Current: §f" + muteReason,
            "§7",
            "§aClick to change reason!"));
        
        
        inventory.setItem(28, createItem(Material.PAPER, 
            "§eChat Violation",
            "§7General chat rule violation",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(29, createItem(Material.RED_DYE, 
            "§cInappropriate Language",
            "§7Used inappropriate or offensive language",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(30, createItem(Material.YELLOW_DYE, 
            "§eSpamming",
            "§7Excessive message spam or repetition",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(31, createItem(Material.ORANGE_DYE, 
            "§6Harassment",
            "§7Harassing or targeting other players",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(32, createItem(Material.PURPLE_DYE, 
            "§5Toxic Behavior",
            "§7Toxic or disruptive chat behavior",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(33, createItem(Material.GRAY_DYE, 
            "§7Custom Reason",
            "§7Specify a custom mute reason",
            "§7",
            "§aClick to enter custom reason!"));
    }
    
    private void setupActionButtons() {
        
        boolean canMute = targetPlayer != null;
        
        inventory.setItem(40, createItem(canMute ? Material.REDSTONE_BLOCK : Material.GRAY_CONCRETE, 
            canMute ? "§c🔇 MUTE PLAYER" : "§7🔇 Select Player First",
            canMute ? "§7Mute " + targetPlayer.getName() : "§7No player selected",
            canMute ? "§7Duration: §e" + formatDuration(muteDuration) : "§7",
            canMute ? "§7Reason: §f" + muteReason : "§7Choose a player to mute",
            "§7",
            canMute ? "§cClick to apply mute!" : "§8Select a player first"));
        
        
        inventory.setItem(38, createItem(Material.BOOK, 
            "§b📋 Preview Mute",
            "§7Review mute details before applying",
            "§7",
            "§8Player: " + (targetPlayer != null ? targetPlayer.getName() : "None"),
            "§8Duration: " + formatDuration(muteDuration),
            "§8Reason: " + muteReason,
            "§7",
            "§aClick to preview!"));
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
                adjustDuration();
                break;
            case 14: 
                changeReason();
                break;
            case 19: 
                muteDuration = 300;
                refresh();
                break;
            case 20: 
                muteDuration = 1800;
                refresh();
                break;
            case 21: 
                muteDuration = 3600;
                refresh();
                break;
            case 22: 
                muteDuration = 86400;
                refresh();
                break;
            case 28: 
                muteReason = "Chat violation";
                refresh();
                break;
            case 29: 
                muteReason = "Inappropriate language";
                refresh();
                break;
            case 30: 
                muteReason = "Spamming";
                refresh();
                break;
            case 31: 
                muteReason = "Harassment";
                refresh();
                break;
            case 32: 
                muteReason = "Toxic behavior";
                refresh();
                break;
            case 33: 
                customReason();
                break;
            case 38: 
                previewMute();
                break;
            case 40: 
                applyMute();
                break;
        }
    }
    
    private void selectPlayer() {
        player.sendMessage("§e=== Select Player to Mute ===");
        player.sendMessage("§7Online players:");
        
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cNo players online to mute!");
            return;
        }
        
        for (int i = 0; i < Math.min(onlinePlayers.size(), 10); i++) {
            Player p = onlinePlayers.get(i);
            if (!p.equals(player)) {
                player.sendMessage("§8" + (i + 1) + ". §f" + p.getName() + " §7(online)");
            }
        }
        
        player.sendMessage("§7");
        player.sendMessage("§7Type: §e/sc quickmute <player>");
        player.closeInventory();
    }
    
    private void adjustDuration() {
        DurationSelectionGUI durationGUI = new DurationSelectionGUI(plugin, player, muteDuration, duration -> {
            this.muteDuration = duration;
            this.open();
        });
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), durationGUI);
        durationGUI.open();
    }
    
    private void changeReason() {
        player.sendMessage("§d📝 Custom mute reason:");
        player.sendMessage("§7Type in chat or use preset buttons in GUI");
        player.sendMessage("§7Current reason: §f" + muteReason);
    }
    
    private void customReason() {
        player.sendMessage("§7Enter custom mute reason in chat:");
        player.sendMessage("§8Type your reason and it will be set automatically");
        player.closeInventory();
    }
    
    private void previewMute() {
        player.sendMessage("§b📋 === Mute Preview ===");
        player.sendMessage("§7Player: §f" + (targetPlayer != null ? targetPlayer.getName() : "None selected"));
        player.sendMessage("§7Duration: §e" + formatDuration(muteDuration));
        player.sendMessage("§7Reason: §f" + muteReason);
        player.sendMessage("§7Staff: §b" + player.getName());
        player.sendMessage("§7");
        
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ Cannot apply mute: No player selected!");
        } else {
            player.sendMessage("§aReady to apply mute. Click the mute button to proceed.");
        }
    }
    
    private void applyMute() {
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ Cannot mute: No player selected!");
            return;
        }
        
        player.sendMessage("§c🔇 Applying mute to " + targetPlayer.getName() + "...");
        
        
        player.sendMessage("§a✓ " + targetPlayer.getName() + " has been muted!");
        player.sendMessage("§7Duration: §e" + formatDuration(muteDuration));
        player.sendMessage("§7Reason: §f" + muteReason);
        
        
        if (targetPlayer.isOnline()) {
            Player onlineTarget = targetPlayer.getPlayer();
            onlineTarget.sendMessage("§c🔇 You have been muted!");
            onlineTarget.sendMessage("§7Duration: §e" + formatDuration(muteDuration));
            onlineTarget.sendMessage("§7Reason: §f" + muteReason);
            onlineTarget.sendMessage("§7Staff: §b" + player.getName());
        }
        
        player.closeInventory();
    }
}