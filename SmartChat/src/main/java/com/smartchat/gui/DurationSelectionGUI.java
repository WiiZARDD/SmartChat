package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class DurationSelectionGUI extends BaseGUI {
    
    private final int currentDuration;
    private final Consumer<Integer> durationCallback;
    
    public DurationSelectionGUI(SmartChat plugin, Player player, int currentDuration, Consumer<Integer> callback) {
        super(plugin, player, "§8§l◆ §6§lSelect Duration §8§l◆", 27);
        this.currentDuration = currentDuration;
        this.durationCallback = callback;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.CLOCK, 
            "§6§lDuration Selection",
            "§7Current: §e" + formatDuration(currentDuration),
            "§7",
            "§8Choose a punishment duration"));
        
        
        inventory.setItem(9, createItem(Material.LIME_CONCRETE, 
            "§a5 Minutes",
            "§7Short duration for minor violations",
            "§7",
            "§a300 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(10, createItem(Material.YELLOW_CONCRETE, 
            "§e15 Minutes",
            "§7Moderate duration for repeat offenses",
            "§7",
            "§e900 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(11, createItem(Material.ORANGE_CONCRETE, 
            "§630 Minutes",
            "§7Extended duration for serious violations",
            "§7",
            "§61800 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(12, createItem(Material.RED_CONCRETE, 
            "§c1 Hour",
            "§7Long duration for major violations",
            "§7",
            "§c3600 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(13, createItem(Material.PURPLE_CONCRETE, 
            "§56 Hours",
            "§7Extended punishment",
            "§7",
            "§521600 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(14, createItem(Material.MAGENTA_CONCRETE, 
            "§d24 Hours",
            "§7Full day punishment",
            "§7",
            "§d86400 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(15, createItem(Material.BLACK_CONCRETE, 
            "§87 Days",
            "§7Week-long punishment",
            "§7",
            "§8604800 seconds",
            "§7",
            "§aClick to select!"));
        
        inventory.setItem(16, createItem(Material.GRAY_CONCRETE, 
            "§730 Days",
            "§7Month-long punishment",
            "§7",
            "§72592000 seconds",
            "§7",
            "§aClick to select!"));
        
        
        inventory.setItem(22, createItem(Material.ANVIL, 
            "§b⚙ Custom Duration",
            "§7Enter a custom duration",
            "§7",
            "§bSpecify exact time needed",
            "§7",
            "§aClick for custom duration!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        int selectedDuration = -1;
        
        switch (slot) {
            case 9: 
                selectedDuration = 300;
                break;
            case 10: 
                selectedDuration = 900;
                break;
            case 11: 
                selectedDuration = 1800;
                break;
            case 12: 
                selectedDuration = 3600;
                break;
            case 13: 
                selectedDuration = 21600;
                break;
            case 14: 
                selectedDuration = 86400;
                break;
            case 15: 
                selectedDuration = 604800;
                break;
            case 16: 
                selectedDuration = 2592000;
                break;
            case 22: 
                customDuration();
                return;
            default:
                return;
        }
        
        if (selectedDuration > 0) {
            durationCallback.accept(selectedDuration);
            player.sendMessage("§aDuration set to " + formatDuration(selectedDuration));
        }
    }
    
    private void customDuration() {
        player.sendMessage("§b⚙ Custom Duration Entry");
        player.sendMessage("§7Enter duration in format: 1d2h30m45s");
        player.sendMessage("§7Examples:");
        player.sendMessage("§8• §e30m §7= 30 minutes");
        player.sendMessage("§8• §e2h30m §7= 2 hours 30 minutes");
        player.sendMessage("§8• §e1d §7= 1 day");
        player.sendMessage("§8• §e7d12h §7= 7 days 12 hours");
        player.closeInventory();
    }
}