package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;

public abstract class BaseGUI {
    
    protected final SmartChat plugin;
    protected final Player player;
    protected final Inventory inventory;
    protected final String title;
    protected final int size;
    
    public BaseGUI(SmartChat plugin, Player player, String title, int size) {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);
    }
    
    public abstract void setupGUI();
    
    public abstract void handleClick(int slot, ItemStack item, ClickType clickType);
    
    public void open() {
        setupGUI();
        player.openInventory(inventory);
    }
    
    public void refresh() {
        inventory.clear();
        setupGUI();
    }
    
    public void onClose() {
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§r" + name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    protected ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§r" + name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    protected ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§r" + name);
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    protected ItemStack createPlayerHead(String playerName, String displayName, String... lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r" + displayName);
            try {
                OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(playerName);
                meta.setOwningPlayer(offlinePlayer);
            } catch (Exception e) {
                meta.setOwner(playerName);
            }
            if (lore != null && lore.length > 0) {
                meta.setLore(java.util.Arrays.asList(lore));
            }
            head.setItemMeta(meta);
        }
        return head;
    }
    
    protected ItemStack createPlayerHead(String playerName, String displayName, java.util.List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r" + displayName);
            try {
                OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(playerName);
                meta.setOwningPlayer(offlinePlayer);
            } catch (Exception e) {
                meta.setOwner(playerName);
            }
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            head.setItemMeta(meta);
        }
        return head;
    }
    
    protected ItemStack createPlayerHeadByUUID(java.util.UUID uuid, String displayName, java.util.List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r" + displayName);
            OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(uuid);
            meta.setOwningPlayer(offlinePlayer);
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            head.setItemMeta(meta);
        }
        return head;
    }
    
    protected void fillBorder(Material material) {
        ItemStack borderItem = new ItemStack(material);
        ItemMeta meta = borderItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r");
            borderItem.setItemMeta(meta);
        }
        
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderItem);
            inventory.setItem(size - 9 + i, borderItem);
        }
        
        for (int i = 9; i < size - 9; i += 9) {
            inventory.setItem(i, borderItem);
            inventory.setItem(i + 8, borderItem);
        }
    }
    
    protected void addNavigationItems() {
        inventory.setItem(size - 9, createItem(Material.ARROW, 
            "§c« Back", 
            "§7Click to go back"));
            
        inventory.setItem(size - 1, createItem(Material.BARRIER, 
            "§c✕ Close", 
            "§7Click to close this menu"));
    }
    
    protected boolean isNavigationSlot(int slot) {
        return slot == size - 9 || slot == size - 1;
    }
    
    protected void handleNavigation(int slot) {
        if (slot == size - 9) {
            plugin.getGuiManager().openMainGUI(player);
        } else if (slot == size - 1) {
            player.closeInventory();
        }
    }
    
    protected String formatPercentage(double value) {
        return String.format("%.1f%%", value * 100);
    }
    
    protected String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " hours";
        } else {
            return (seconds / 86400) + " days";
        }
    }
}