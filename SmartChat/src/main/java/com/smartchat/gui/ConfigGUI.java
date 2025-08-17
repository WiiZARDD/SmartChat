package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigGUI extends BaseGUI {
    
    public ConfigGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §e§lᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ", 54);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.CONFIG));
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.WRITABLE_BOOK, 
            "§e§lᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ",
            "§7Manage all plugin settings",
            "§7",
            "§8Click to adjust values and settings"));
        
        
        setupThresholds();
        
        
        setupActions();
        
        
        setupWorldSettings();
        
        
        setupAPIConfig();
        
        
        inventory.setItem(49, createItem(Material.EMERALD_BLOCK, 
            "§a💾 Save All Changes",
            "§7Click to save all configuration changes",
            "§7",
            "§aThis will reload the plugin configuration"));
    }
    
    private void setupThresholds() {
        double toxicity = plugin.getConfigManager().getConfig().getDouble("thresholds.toxicity", 0.75);
        double harassment = plugin.getConfigManager().getConfig().getDouble("thresholds.categories.harassment", 0.70);
        double profanity = plugin.getConfigManager().getConfig().getDouble("thresholds.categories.profanity", 0.65);
        double spam = plugin.getConfigManager().getConfig().getDouble("thresholds.categories.spam", 0.80);
        
        
        List<String> toxicityLore = new ArrayList<>();
        toxicityLore.add("§7Current Value: §e" + formatPercentage(toxicity));
        toxicityLore.add("§7");
        toxicityLore.add("§7Overall toxicity detection threshold");
        toxicityLore.add("§7Lower = more sensitive");
        toxicityLore.add("§7Higher = less sensitive");
        toxicityLore.add("§7");
        toxicityLore.add("§aClick to adjust threshold!");
        
        inventory.setItem(19, createItem(Material.RED_DYE, 
            "§c🔥 Toxicity Threshold", toxicityLore));
        
        
        List<String> harassmentLore = new ArrayList<>();
        harassmentLore.add("§7Current Value: §e" + formatPercentage(harassment));
        harassmentLore.add("§7");
        harassmentLore.add("§7Harassment detection threshold");
        harassmentLore.add("§7Covers bullying and targeted attacks");
        harassmentLore.add("§7");
        harassmentLore.add("§aClick to adjust threshold!");
        
        inventory.setItem(20, createItem(Material.ORANGE_DYE, 
            "§6🎯 Harassment Threshold", harassmentLore));
        
        
        List<String> profanityLore = new ArrayList<>();
        profanityLore.add("§7Current Value: §e" + formatPercentage(profanity));
        profanityLore.add("§7");
        profanityLore.add("§7Profanity detection threshold");
        profanityLore.add("§7Covers swearing and inappropriate language");
        profanityLore.add("§7");
        profanityLore.add("§aClick to adjust threshold!");
        
        inventory.setItem(21, createItem(Material.PURPLE_DYE, 
            "§5💬 Profanity Threshold", profanityLore));
        
        
        List<String> spamLore = new ArrayList<>();
        spamLore.add("§7Current Value: §e" + formatPercentage(spam));
        spamLore.add("§7");
        spamLore.add("§7Spam detection threshold");
        spamLore.add("§7Covers repetitive and excessive messages");
        spamLore.add("§7");
        spamLore.add("§aClick to adjust threshold!");
        
        inventory.setItem(22, createItem(Material.YELLOW_DYE, 
            "§e📢 Spam Threshold", spamLore));
    }
    
    private void setupActions() {
        boolean lowEnabled = plugin.getConfigManager().getConfig().getBoolean("actions.low.enabled", true);
        boolean mediumEnabled = plugin.getConfigManager().getConfig().getBoolean("actions.medium.enabled", true);
        boolean highEnabled = plugin.getConfigManager().getConfig().getBoolean("actions.high.enabled", true);
        boolean extremeEnabled = plugin.getConfigManager().getConfig().getBoolean("actions.extreme.enabled", true);
        
        
        List<String> lowLore = new ArrayList<>();
        lowLore.add("§7Status: " + (lowEnabled ? "§aEnabled" : "§cDisabled"));
        lowLore.add("§7");
        lowLore.add("§7Actions for minor violations:");
        List<String> lowActions = plugin.getConfigManager().getConfig().getStringList("actions.low.actions");
        for (String action : lowActions) {
            lowLore.add("§8• §f" + action);
        }
        lowLore.add("§7");
        lowLore.add("§8Click to toggle enabled/disabled");
        
        inventory.setItem(28, createItem(lowEnabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE, 
            "§a🟢 Low Severity Actions", lowLore));
        
        
        List<String> mediumLore = new ArrayList<>();
        mediumLore.add("§7Status: " + (mediumEnabled ? "§aEnabled" : "§cDisabled"));
        mediumLore.add("§7");
        mediumLore.add("§7Actions for moderate violations:");
        List<String> mediumActions = plugin.getConfigManager().getConfig().getStringList("actions.medium.actions");
        for (String action : mediumActions) {
            mediumLore.add("§8• §f" + action);
        }
        int muteDuration = plugin.getConfigManager().getConfig().getInt("actions.medium.mute-duration", 300);
        mediumLore.add("§7");
        mediumLore.add("§7Mute Duration: §e" + formatDuration(muteDuration));
        mediumLore.add("§8Click to toggle enabled/disabled");
        
        inventory.setItem(29, createItem(mediumEnabled ? Material.YELLOW_CONCRETE : Material.RED_CONCRETE, 
            "§e🟡 Medium Severity Actions", mediumLore));
        
        
        List<String> highLore = new ArrayList<>();
        highLore.add("§7Status: " + (highEnabled ? "§aEnabled" : "§cDisabled"));
        highLore.add("§7");
        highLore.add("§7Actions for serious violations:");
        List<String> highActions = plugin.getConfigManager().getConfig().getStringList("actions.high.actions");
        for (String action : highActions) {
            highLore.add("§8• §f" + action);
        }
        int highMuteDuration = plugin.getConfigManager().getConfig().getInt("actions.high.mute-duration", 3600);
        highLore.add("§7");
        highLore.add("§7Mute Duration: §e" + formatDuration(highMuteDuration));
        highLore.add("§8Click to toggle enabled/disabled");
        
        inventory.setItem(30, createItem(highEnabled ? Material.ORANGE_CONCRETE : Material.RED_CONCRETE, 
            "§6🟠 High Severity Actions", highLore));
        
        
        List<String> extremeLore = new ArrayList<>();
        extremeLore.add("§7Status: " + (extremeEnabled ? "§aEnabled" : "§cDisabled"));
        extremeLore.add("§7");
        extremeLore.add("§7Actions for extreme violations:");
        List<String> extremeActions = plugin.getConfigManager().getConfig().getStringList("actions.extreme.actions");
        for (String action : extremeActions) {
            extremeLore.add("§8• §f" + action);
        }
        int banDuration = plugin.getConfigManager().getConfig().getInt("actions.extreme.ban-duration", 86400);
        extremeLore.add("§7");
        extremeLore.add("§7Ban Duration: §e" + formatDuration(banDuration));
        extremeLore.add("§8Click to toggle enabled/disabled");
        
        inventory.setItem(31, createItem(extremeEnabled ? Material.RED_CONCRETE : Material.GRAY_CONCRETE, 
            "§c🔴 Extreme Severity Actions", extremeLore));
    }
    
    private void setupWorldSettings() {
        boolean defaultEnabled = plugin.getConfigManager().getConfig().getBoolean("world-settings.default.enabled", true);
        
        inventory.setItem(37, createItem(Material.GRASS_BLOCK, 
            "§2🌍 World Settings",
            "§7Default World: " + (defaultEnabled ? "§aEnabled" : "§cDisabled"),
            "§7",
            "§7Configure per-world moderation settings",
            "§7",
            "§aClick to configure world settings!"));
    }
    
    private void setupAPIConfig() {
        String apiKey = plugin.getConfigManager().getApiKey();
        boolean hasKey = apiKey != null && !apiKey.isEmpty() && !apiKey.equals("PASTE_YOUR_API_KEY_HERE");
        
        List<String> apiLore = new ArrayList<>();
        apiLore.add("§7API Status: " + (hasKey ? "§aConfigured" : "§cNot Configured"));
        if (hasKey) {
            apiLore.add("§7API Key: §f" + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        }
        apiLore.add("§7");
        apiLore.add("§7Endpoint: §f" + plugin.getConfigManager().getConfig().getString("api.endpoint", "Default"));
        apiLore.add("§7Rate Limit: §e" + plugin.getConfigManager().getConfig().getInt("api.rate-limit", 15) + "/min");
        apiLore.add("§7Timeout: §e" + plugin.getConfigManager().getConfig().getInt("api.timeout", 10) + "s");
        apiLore.add("§7");
        apiLore.add("§8Click to configure API settings");
        
        inventory.setItem(43, createItem(Material.ENDER_EYE, 
            "§d🔮 API Configuration", apiLore));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 19: 
                adjustThreshold("thresholds.toxicity", clickType);
                break;
            case 20: 
                adjustThreshold("thresholds.categories.harassment", clickType);
                break;
            case 21: 
                adjustThreshold("thresholds.categories.profanity", clickType);
                break;
            case 22: 
                adjustThreshold("thresholds.categories.spam", clickType);
                break;
            case 28: 
                toggleAction("actions.low.enabled");
                break;
            case 29: 
                toggleAction("actions.medium.enabled");
                break;
            case 30: 
                toggleAction("actions.high.enabled");
                break;
            case 31: 
                toggleAction("actions.extreme.enabled");
                break;
            case 37: 
                openWorldSettingsGUI();
                break;
            case 43: 
                openAPIConfigurationGUI();
                break;
            case 49: 
                saveChanges();
                break;
        }
    }
    
    private void adjustThreshold(String path, ClickType clickType) {
        
        String thresholdName = "";
        String description = "";
        
        switch (path) {
            case "thresholds.toxicity":
                thresholdName = "Toxicity";
                description = "Overall toxicity detection threshold";
                break;
            case "thresholds.categories.harassment":
                thresholdName = "Harassment";
                description = "Harassment and bullying detection threshold";
                break;
            case "thresholds.categories.profanity":
                thresholdName = "Profanity";
                description = "Profanity and inappropriate language threshold";
                break;
            case "thresholds.categories.spam":
                thresholdName = "Spam";
                description = "Spam and repetitive message threshold";
                break;
        }
        
        
        ThresholdAdjustmentGUI thresholdGUI = new ThresholdAdjustmentGUI(plugin, player, path, thresholdName, description);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), thresholdGUI);
        thresholdGUI.open();
    }
    
    private void toggleAction(String path) {
        boolean current = plugin.getConfigManager().getConfig().getBoolean(path, true);
        plugin.getConfigManager().getConfig().set(path, !current);
        refresh();
        
        player.sendMessage("§aSetting " + (!current ? "enabled" : "disabled"));
    }
    
    private void saveChanges() {
        plugin.getConfigManager().saveConfig();
        plugin.reload();
        player.sendMessage("§a✓ Configuration saved and reloaded successfully!");
        player.closeInventory();
    }
    
    private void openWorldSettingsGUI() {
        WorldSettingsGUI worldSettingsGUI = new WorldSettingsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), worldSettingsGUI);
        worldSettingsGUI.open();
    }
    
    private void openAPIConfigurationGUI() {
        APIConfigurationGUI apiConfigGUI = new APIConfigurationGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), apiConfigGUI);
        apiConfigGUI.open();
    }
}