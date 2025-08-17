package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WorldSettingsGUI extends BaseGUI {
    
    public WorldSettingsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §2§lWorld Settings §8§l◆", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.GREEN_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.GRASS_BLOCK, 
            "§2§lWorld Settings Management",
            "§7Configure moderation per world",
            "§7",
            "§8Enable or disable moderation for specific worlds"));
        
        
        boolean defaultEnabled = plugin.getConfigManager().getConfig().getBoolean("world-settings.default.enabled", true);
        
        inventory.setItem(10, createItem(Material.BEACON, 
            "§e🌟 Default World Settings",
            "§7Status: " + (defaultEnabled ? "§aEnabled" : "§cDisabled"),
            "§7",
            "§7Default settings for all worlds",
            "§7unless specifically configured.",
            "§7",
            "§aClick to toggle default settings!"));
        
        
        setupWorldList();
        
        
        inventory.setItem(45, createItem(Material.EMERALD_BLOCK, 
            "§a✓ Enable All Worlds",
            "§7Enable moderation for all worlds",
            "§7",
            "§aClick to enable all!"));
        
        inventory.setItem(46, createItem(Material.REDSTONE_BLOCK, 
            "§c✗ Disable All Worlds",
            "§7Disable moderation for all worlds",
            "§7",
            "§cClick to disable all!"));
        
        inventory.setItem(47, createItem(Material.BOOK, 
            "§b📖 Export Settings",
            "§7Export world configuration",
            "§7",
            "§bClick to export!"));
        
        inventory.setItem(48, createItem(Material.WRITABLE_BOOK, 
            "§d📝 Import Settings",
            "§7Import world configuration",
            "§7",
            "§dClick to import!"));
    }
    
    private void setupWorldList() {
        List<World> worlds = Bukkit.getWorlds();
        int startSlot = 19;
        
        for (int i = 0; i < Math.min(worlds.size(), 18); i++) {
            World world = worlds.get(i);
            String worldName = world.getName();
            
            boolean enabled = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".enabled", true);
            
            List<String> worldLore = new ArrayList<>();
            worldLore.add("§7World: §f" + worldName);
            worldLore.add("§7Status: " + (enabled ? "§aEnabled" : "§cDisabled"));
            worldLore.add("§7");
            worldLore.add("§7Environment: §e" + world.getEnvironment().name());
            worldLore.add("§7Players: §b" + world.getPlayers().size());
            worldLore.add("§7");
            worldLore.add("§8Moderation " + (enabled ? "active" : "inactive") + " in this world");
            worldLore.add("§7");
            worldLore.add("§aClick to configure this world!");
            
            Material worldMaterial = getWorldMaterial(world.getEnvironment());
            
            inventory.setItem(startSlot + i, createItem(worldMaterial, 
                "§f🌍 " + worldName, worldLore));
        }
        
        
        if (worlds.size() > 18) {
            inventory.setItem(43, createItem(Material.ARROW, 
                "§7... and " + (worlds.size() - 18) + " more worlds",
                "§7Click to view all worlds",
                "§7",
                "§8Pagination coming soon!"));
        }
    }
    
    private Material getWorldMaterial(World.Environment environment) {
        switch (environment) {
            case NORMAL:
                return Material.GRASS_BLOCK;
            case NETHER:
                return Material.NETHERRACK;
            case THE_END:
                return Material.END_STONE;
            default:
                return Material.STONE;
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
                toggleDefaultSettings();
                break;
            case 45: 
                enableAllWorlds();
                break;
            case 46: 
                disableAllWorlds();
                break;
            case 47: 
                exportSettings();
                break;
            case 48: 
                importSettings();
                break;
            case 43: 
                player.sendMessage("§7World pagination coming soon!");
                break;
            default:
                
                if (slot >= 19 && slot <= 36) {
                    handleWorldClick(slot);
                }
                break;
        }
    }
    
    private void toggleDefaultSettings() {
        boolean current = plugin.getConfigManager().getConfig().getBoolean("world-settings.default.enabled", true);
        plugin.getConfigManager().getConfig().set("world-settings.default.enabled", !current);
        refresh();
        
        player.sendMessage("§aDefault world settings " + (!current ? "enabled" : "disabled"));
    }
    
    private void enableAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            plugin.getConfigManager().getConfig().set("world-settings." + world.getName() + ".enabled", true);
        }
        refresh();
        player.sendMessage("§aModeration enabled for all worlds!");
    }
    
    private void disableAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            plugin.getConfigManager().getConfig().set("world-settings." + world.getName() + ".enabled", false);
        }
        refresh();
        player.sendMessage("§cModeration disabled for all worlds!");
    }
    
    private void exportSettings() {
        player.sendMessage("§bExporting world settings...");
        
        
        StringBuilder worldList = new StringBuilder();
        for (World world : Bukkit.getWorlds()) {
            worldList.append(world.getName()).append(", ");
        }
        String allWorlds = worldList.length() > 0 ? worldList.substring(0, worldList.length() - 2) : "No worlds";
        
        plugin.getExportManager().exportWorldConfiguration(allWorlds).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "world settings");
            });
        });
    }
    
    private void importSettings() {
        player.sendMessage("§dImporting world settings...");
        player.sendMessage("§7Import functionality coming soon!");
    }
    
    private void handleWorldClick(int slot) {
        List<World> worlds = Bukkit.getWorlds();
        int worldIndex = slot - 19;
        
        if (worldIndex >= worlds.size()) return;
        
        World world = worlds.get(worldIndex);
        openWorldConfigGUI(world);
    }
    
    private void openWorldConfigGUI(World world) {
        WorldConfigGUI worldConfigGUI = new WorldConfigGUI(plugin, player, world);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), worldConfigGUI);
        worldConfigGUI.open();
    }
}