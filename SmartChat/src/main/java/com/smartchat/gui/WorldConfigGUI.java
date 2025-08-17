package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WorldConfigGUI extends BaseGUI {
    
    private final World world;
    
    public WorldConfigGUI(SmartChat plugin, Player player, World world) {
        super(plugin, player, "§8§l◆ §2§l" + world.getName() + " Settings §8§l◆", 45);
        this.world = world;
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.GREEN_STAINED_GLASS_PANE);
        addNavigationItems();
        
        String worldName = world.getName();
        boolean enabled = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".enabled", true);
        
        
        inventory.setItem(4, createItem(getWorldMaterial(), 
            "§2§l" + worldName + " Configuration",
            "§7Configure moderation for this world",
            "§7",
            "§7Environment: §e" + world.getEnvironment().name(),
            "§7Players: §b" + world.getPlayers().size(),
            "§7Status: " + (enabled ? "§aEnabled" : "§cDisabled")));
        
        
        inventory.setItem(10, createItem(enabled ? Material.LIME_DYE : Material.RED_DYE, 
            enabled ? "§a✓ Moderation Enabled" : "§c✗ Moderation Disabled",
            "§7Click to " + (enabled ? "disable" : "enable") + " moderation",
            "§7in this world.",
            "§7",
            "§8When disabled, no chat messages will",
            "§8be processed for violations in this world.",
            "§7",
            "§aClick to toggle moderation!"));
        
        
        boolean useCustomThresholds = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".use-custom-thresholds", false);
        
        inventory.setItem(12, createItem(Material.COMPARATOR, 
            "§e⚙ Custom Thresholds",
            "§7Status: " + (useCustomThresholds ? "§aUsing Custom" : "§7Using Global"),
            "§7",
            "§7Configure world-specific detection thresholds",
            "§7instead of using global settings.",
            "§7",
            "§aClick to configure thresholds!"));
        
        
        boolean useCustomActions = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".use-custom-actions", false);
        
        inventory.setItem(14, createItem(Material.COMMAND_BLOCK, 
            "§6🔧 Custom Actions",
            "§7Status: " + (useCustomActions ? "§aUsing Custom" : "§7Using Global"),
            "§7",
            "§7Configure world-specific punishment actions",
            "§7instead of using global settings.",
            "§7",
            "§aClick to configure actions!"));
        
        
        List<String> exemptedPlayers = plugin.getConfigManager().getConfig().getStringList("world-settings." + worldName + ".exempted-players");
        
        inventory.setItem(16, createItem(Material.GOLDEN_APPLE, 
            "§d👑 Exempted Players",
            "§7Players exempt from moderation: §e" + exemptedPlayers.size(),
            "§7",
            "§7Players in this list will not have",
            "§7their messages checked for violations.",
            "§7",
            "§aClick to manage exemptions!"));
        
        
        boolean logMessages = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".log-messages", true);
        
        inventory.setItem(19, createItem(Material.WRITABLE_BOOK, 
            "§b📝 Message Logging",
            "§7Status: " + (logMessages ? "§aEnabled" : "§cDisabled"),
            "§7",
            "§7Log all chat messages from this world",
            "§7for analysis and appeals.",
            "§7",
            "§aClick to toggle logging!"));
        
        
        inventory.setItem(21, createItem(Material.BOOK, 
            "§a📊 World Statistics",
            "§7View moderation statistics",
            "§7for this specific world.",
            "§7",
            "§8Total violations, appeals, etc.",
            "§7",
            "§aClick to view stats!"));
        
        
        inventory.setItem(23, createItem(Material.BARRIER, 
            "§c🔄 Reset to Defaults",
            "§7Reset all world settings to",
            "§7match global configuration.",
            "§7",
            "§cThis will remove all custom settings!",
            "§7",
            "§cClick to reset!"));
        
        
        inventory.setItem(40, createItem(Material.EMERALD_BLOCK, 
            "§a💾 Save Changes",
            "§7Save all configuration changes",
            "§7for this world.",
            "§7",
            "§aClick to save!"));
    }
    
    private Material getWorldMaterial() {
        switch (world.getEnvironment()) {
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
        
        String worldName = world.getName();
        
        switch (slot) {
            case 10: 
                toggleModeration(worldName);
                break;
            case 12: 
                openCustomThresholds(worldName);
                break;
            case 14: 
                openCustomActions(worldName);
                break;
            case 16: 
                openExemptedPlayers(worldName);
                break;
            case 19: 
                toggleMessageLogging(worldName);
                break;
            case 21: 
                openWorldStats(worldName);
                break;
            case 23: 
                resetToDefaults(worldName);
                break;
            case 40: 
                saveChanges();
                break;
        }
    }
    
    private void toggleModeration(String worldName) {
        boolean current = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".enabled", true);
        plugin.getConfigManager().getConfig().set("world-settings." + worldName + ".enabled", !current);
        refresh();
        
        player.sendMessage("§aModeration " + (!current ? "enabled" : "disabled") + " for " + worldName);
    }
    
    private void openCustomThresholds(String worldName) {
        player.sendMessage("§eCustom thresholds configuration coming soon!");
    }
    
    private void openCustomActions(String worldName) {
        player.sendMessage("§6Custom actions configuration coming soon!");
    }
    
    private void openExemptedPlayers(String worldName) {
        player.sendMessage("§dExempted players management coming soon!");
    }
    
    private void toggleMessageLogging(String worldName) {
        boolean current = plugin.getConfigManager().getConfig().getBoolean("world-settings." + worldName + ".log-messages", true);
        plugin.getConfigManager().getConfig().set("world-settings." + worldName + ".log-messages", !current);
        refresh();
        
        player.sendMessage("§aMessage logging " + (!current ? "enabled" : "disabled") + " for " + worldName);
    }
    
    private void openWorldStats(String worldName) {
        player.sendMessage("§aWorld statistics coming soon!");
    }
    
    private void resetToDefaults(String worldName) {
        plugin.getConfigManager().getConfig().set("world-settings." + worldName, null);
        refresh();
        
        player.sendMessage("§cWorld settings reset to defaults for " + worldName);
    }
    
    private void saveChanges() {
        plugin.getConfigManager().saveConfig();
        player.sendMessage("§a✓ World configuration saved for " + world.getName() + "!");
        player.closeInventory();
    }
}