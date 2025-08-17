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

public class QuickBanGUI extends BaseGUI {
    
    private OfflinePlayer targetPlayer = null;
    private int banDuration = 86400; 
    private String banReason = "Serious violation";
    private boolean permanentBan = false;
    
    public QuickBanGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §4§lǫᴜɪᴄᴋ ʙᴀɴ §8§l◆", 45);
    }
    
    public QuickBanGUI(SmartChat plugin, Player player, OfflinePlayer target) {
        super(plugin, player, "§8§l◆ §4§lǫᴜɪᴄᴋ ʙᴀɴ §8§l◆", 45);
        this.targetPlayer = target;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.ANVIL, 
            "§4§lǫᴜɪᴄᴋ ʙᴀɴ ꜱʏꜱᴛᴇᴍ",
            "§7Quickly ban a player for serious violations",
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
                "§7Click to choose a player to ban",
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
        
        inventory.setItem(12, createItem(permanentBan ? Material.BARRIER : Material.CLOCK, 
            permanentBan ? "§4⚫ PERMANENT BAN" : "§6⏱ Temporary Ban",
            permanentBan ? "§7This will be a permanent ban" : "§7Duration: §e" + formatDuration(banDuration),
            "§7",
            permanentBan ? "§cClick to make temporary!" : "§aClick to adjust duration!"));
        
        if (!permanentBan) {
            
            inventory.setItem(19, createItem(Material.YELLOW_CONCRETE, 
                "§e1 Hour",
                "§7Short ban for 1 hour",
                "§7",
                "§8Good for cooling off periods",
                "§7",
                "§aClick to select!"));
            
            inventory.setItem(20, createItem(Material.ORANGE_CONCRETE, 
                "§624 Hours",
                "§7Standard ban for 24 hours",
                "§7",
                "§8Good for serious violations",
                "§7",
                "§aClick to select!"));
            
            inventory.setItem(21, createItem(Material.RED_CONCRETE, 
                "§c7 Days",
                "§7Week-long ban",
                "§7",
                "§8Good for repeated violations",
                "§7",
                "§aClick to select!"));
            
            inventory.setItem(22, createItem(Material.PURPLE_CONCRETE, 
                "§530 Days",
                "§7Month-long ban",
                "§7",
                "§8Good for severe violations",
                "§7",
                "§aClick to select!"));
        } else {
            
            inventory.setItem(19, createItem(Material.BARRIER, 
                "§4⚠ PERMANENT BAN",
                "§cThis ban will be permanent!",
                "§7",
                "§8Player will not be able to rejoin",
                "§8unless manually unbanned by staff",
                "§7",
                "§cUse with extreme caution!"));
        }
    }
    
    private void setupReasonSelection() {
        inventory.setItem(14, createItem(Material.WRITABLE_BOOK, 
            "§d📝 Ban Reason",
            "§7Current: §f" + banReason,
            "§7",
            "§aClick to change reason!"));
        
        
        inventory.setItem(28, createItem(Material.RED_DYE, 
            "§cSerious Violation",
            "§7Severe rule violation requiring ban",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(29, createItem(Material.ORANGE_DYE, 
            "§6Repeated Offenses",
            "§7Multiple violations despite warnings",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(30, createItem(Material.PURPLE_DYE, 
            "§5Harassment/Bullying",
            "§7Persistent harassment of other players",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(31, createItem(Material.BLACK_DYE, 
            "§8Toxic Behavior",
            "§7Extremely toxic or disruptive behavior",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(32, createItem(Material.MAGENTA_DYE, 
            "§dCheating/Exploiting",
            "§7Use of cheats or exploitation",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(33, createItem(Material.GRAY_DYE, 
            "§7Custom Reason",
            "§7Specify a custom ban reason",
            "§7",
            "§aClick to enter custom reason!"));
    }
    
    private void setupActionButtons() {
        
        boolean canBan = targetPlayer != null;
        
        String banText = permanentBan ? "§4🔨 PERMANENT BAN" : "§c🔨 TEMPORARY BAN";
        String banDurationText = permanentBan ? "§7Permanent ban" : "§7Duration: §e" + formatDuration(banDuration);
        
        inventory.setItem(40, createItem(canBan ? Material.REDSTONE_BLOCK : Material.GRAY_CONCRETE, 
            canBan ? banText : "§7🔨 Select Player First",
            canBan ? "§7Ban " + targetPlayer.getName() : "§7No player selected",
            canBan ? banDurationText : "§7",
            canBan ? "§7Reason: §f" + banReason : "§7Choose a player to ban",
            "§7",
            canBan ? "§cClick to apply ban!" : "§8Select a player first"));
        
        
        inventory.setItem(38, createItem(Material.BOOK, 
            "§b📋 Preview Ban",
            "§7Review ban details before applying",
            "§7",
            "§8Player: " + (targetPlayer != null ? targetPlayer.getName() : "None"),
            "§8Type: " + (permanentBan ? "Permanent" : "Temporary"),
            "§8Duration: " + (permanentBan ? "Forever" : formatDuration(banDuration)),
            "§8Reason: " + banReason,
            "§7",
            "§aClick to preview!"));
        
        
        inventory.setItem(36, createItem(permanentBan ? Material.LIME_DYE : Material.RED_DYE, 
            permanentBan ? "§a✓ Permanent Ban" : "§c✗ Temporary Ban",
            "§7Click to toggle between permanent",
            "§7and temporary ban modes.",
            "§7",
            "§8Currently: " + (permanentBan ? "Permanent" : "Temporary"),
            "§7",
            "§aClick to toggle!"));
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
                if (permanentBan) {
                    permanentBan = false;
                } else {
                    adjustDuration();
                }
                refresh();
                break;
            case 14: 
                changeReason();
                break;
            case 19: 
                if (!permanentBan) {
                    banDuration = 3600; 
                    refresh();
                }
                break;
            case 20: 
                if (!permanentBan) {
                    banDuration = 86400; 
                    refresh();
                }
                break;
            case 21: 
                if (!permanentBan) {
                    banDuration = 604800; 
                    refresh();
                }
                break;
            case 22: 
                if (!permanentBan) {
                    banDuration = 2592000; 
                    refresh();
                }
                break;
            case 28: 
                banReason = "Serious violation";
                refresh();
                break;
            case 29: 
                banReason = "Repeated offenses";
                refresh();
                break;
            case 30: 
                banReason = "Harassment/Bullying";
                refresh();
                break;
            case 31: 
                banReason = "Toxic behavior";
                refresh();
                break;
            case 32: 
                banReason = "Cheating/Exploiting";
                refresh();
                break;
            case 33: 
                customReason();
                break;
            case 36: 
                permanentBan = !permanentBan;
                refresh();
                break;
            case 38: 
                previewBan();
                break;
            case 40: 
                applyBan();
                break;
        }
    }
    
    private void selectPlayer() {
        player.sendMessage("§e=== Select Player to Ban ===");
        player.sendMessage("§7Online players:");
        
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§cNo players online to ban!");
            return;
        }
        
        for (int i = 0; i < Math.min(onlinePlayers.size(), 10); i++) {
            Player p = onlinePlayers.get(i);
            if (!p.equals(player)) {
                player.sendMessage("§8" + (i + 1) + ". §f" + p.getName() + " §7(online)");
            }
        }
        
        player.sendMessage("§7");
        player.sendMessage("§7Type: §e/sc quickban <player>");
        player.closeInventory();
    }
    
    private void adjustDuration() {
        DurationSelectionGUI durationGUI = new DurationSelectionGUI(plugin, player, banDuration, duration -> {
            this.banDuration = duration;
            this.open();
        });
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), durationGUI);
        durationGUI.open();
    }
    
    private void changeReason() {
        player.sendMessage("§d📝 Custom ban reason:");
        player.sendMessage("§7Type in chat or use preset buttons in GUI");
        player.sendMessage("§7Current reason: §f" + banReason);
    }
    
    private void customReason() {
        player.sendMessage("§7Enter custom ban reason in chat:");
        player.sendMessage("§8Type your reason and it will be set automatically");
        player.closeInventory();
    }
    
    private void previewBan() {
        player.sendMessage("§b📋 === Ban Preview ===");
        player.sendMessage("§7Player: §f" + (targetPlayer != null ? targetPlayer.getName() : "None selected"));
        player.sendMessage("§7Type: " + (permanentBan ? "§4Permanent" : "§6Temporary"));
        if (!permanentBan) {
            player.sendMessage("§7Duration: §e" + formatDuration(banDuration));
        }
        player.sendMessage("§7Reason: §f" + banReason);
        player.sendMessage("§7Staff: §b" + player.getName());
        player.sendMessage("§7");
        
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ Cannot apply ban: No player selected!");
        } else if (permanentBan) {
            player.sendMessage("§4⚠ WARNING: This is a PERMANENT ban!");
            player.sendMessage("§aReady to apply ban. Click the ban button to proceed.");
        } else {
            player.sendMessage("§aReady to apply ban. Click the ban button to proceed.");
        }
    }
    
    private void applyBan() {
        if (targetPlayer == null) {
            player.sendMessage("§c⚠ Cannot ban: No player selected!");
            return;
        }
        
        String banType = permanentBan ? "permanently" : "temporarily";
        player.sendMessage("§c🔨 Applying " + banType + " ban to " + targetPlayer.getName() + "...");
        
        
        player.sendMessage("§a✓ " + targetPlayer.getName() + " has been " + banType + " banned!");
        if (!permanentBan) {
            player.sendMessage("§7Duration: §e" + formatDuration(banDuration));
        }
        player.sendMessage("§7Reason: §f" + banReason);
        
        
        if (targetPlayer.isOnline()) {
            Player onlineTarget = targetPlayer.getPlayer();
            String kickMessage = "§c🔨 You have been " + banType + " banned!\n" +
                                "§7Reason: §f" + banReason + "\n" +
                                (permanentBan ? "§4This ban is permanent." : "§7Duration: §e" + formatDuration(banDuration)) + "\n" +
                                "§7Staff: §b" + player.getName();
            onlineTarget.kickPlayer(kickMessage);
        }
        
        player.closeInventory();
    }
}