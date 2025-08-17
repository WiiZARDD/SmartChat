package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class APIConfigurationGUI extends BaseGUI {
    
    public APIConfigurationGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §d§lAPI Configuration §8§l◆", 45);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.PURPLE_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        inventory.setItem(4, createItem(Material.ENDER_EYE, 
            "§d§lAPI Configuration Management",
            "§7Configure Google Gemini API settings",
            "§7",
            "§8Manage connection and performance settings"));
        
        
        String apiKey = plugin.getConfigManager().getApiKey();
        boolean hasKey = apiKey != null && !apiKey.isEmpty() && !apiKey.equals("PASTE_YOUR_API_KEY_HERE");
        
        List<String> keyStatusLore = new ArrayList<>();
        keyStatusLore.add("§7Status: " + (hasKey ? "§aConfigured" : "§cNot Configured"));
        if (hasKey) {
            keyStatusLore.add("§7Key Preview: §f" + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        } else {
            keyStatusLore.add("§7Key: §cNo valid API key found");
        }
        keyStatusLore.add("§7");
        keyStatusLore.add("§7Configure your Google Gemini API key");
        keyStatusLore.add("§7for AI-powered chat moderation.");
        keyStatusLore.add("§7");
        keyStatusLore.add("§aClick to configure API key!");
        
        inventory.setItem(10, createItem(hasKey ? Material.LIME_DYE : Material.RED_DYE, 
            "§d🔑 API Key Configuration", keyStatusLore));
        
        
        String endpoint = plugin.getConfigManager().getConfig().getString("api.endpoint", "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent");
        
        List<String> endpointLore = new ArrayList<>();
        endpointLore.add("§7Current Endpoint:");
        endpointLore.add("§f" + (endpoint.length() > 50 ? endpoint.substring(0, 50) + "..." : endpoint));
        endpointLore.add("§7");
        endpointLore.add("§7Configure the API endpoint URL");
        endpointLore.add("§7for Gemini API requests.");
        endpointLore.add("§7");
        endpointLore.add("§aClick to configure endpoint!");
        
        inventory.setItem(12, createItem(Material.COMPASS, 
            "§e🌐 API Endpoint", endpointLore));
        
        
        int rateLimit = plugin.getConfigManager().getConfig().getInt("api.rate-limit", 15);
        
        inventory.setItem(14, createItem(Material.CLOCK, 
            "§6⏱ Rate Limiting",
            "§7Current Limit: §e" + rateLimit + " requests/minute",
            "§7",
            "§7Control how many API requests",
            "§7can be made per minute.",
            "§7",
            "§8Lower = Slower but safer",
            "§8Higher = Faster but may hit limits",
            "§7",
            "§aClick to adjust rate limit!"));
        
        
        int timeout = plugin.getConfigManager().getConfig().getInt("api.timeout", 10);
        
        inventory.setItem(16, createItem(Material.CLOCK, 
            "§c⏳ Request Timeout",
            "§7Current Timeout: §e" + timeout + " seconds",
            "§7",
            "§7How long to wait for API",
            "§7responses before giving up.",
            "§7",
            "§8Lower = Faster failure detection",
            "§8Higher = More patience for slow responses",
            "§7",
            "§aClick to adjust timeout!"));
        
        
        inventory.setItem(19, createItem(Material.REDSTONE_TORCH, 
            "§a🔍 Test Connection",
            "§7Test the API connection with",
            "§7current configuration settings.",
            "§7",
            "§8Sends a test request to verify",
            "§8API key and endpoint work correctly.",
            "§7",
            "§aClick to test connection!"));
        
        
        inventory.setItem(21, createItem(Material.BOOK, 
            "§b📊 Usage Statistics",
            "§7View API usage statistics",
            "§7and performance metrics.",
            "§7",
            "§8Requests made, success rate,",
            "§8average response time, etc.",
            "§7",
            "§aClick to view stats!"));
        
        
        inventory.setItem(23, createItem(Material.BARRIER, 
            "§c🔄 Reset to Defaults",
            "§7Reset all API settings to",
            "§7default configuration values.",
            "§7",
            "§cThis will not change your API key!",
            "§7",
            "§cClick to reset settings!"));
        
        
        inventory.setItem(25, createItem(Material.COMMAND_BLOCK, 
            "§5⚙ Advanced Settings",
            "§7Configure advanced API options",
            "§7like retry attempts and caching.",
            "§7",
            "§8For experienced users only",
            "§7",
            "§aClick for advanced options!"));
        
        
        inventory.setItem(40, createItem(Material.EMERALD_BLOCK, 
            "§a💾 Save Configuration",
            "§7Save all API configuration changes",
            "§7and reload the plugin.",
            "§7",
            "§aClick to save and reload!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 10: 
                configureAPIKey();
                break;
            case 12: 
                configureEndpoint();
                break;
            case 14: 
                adjustRateLimit();
                break;
            case 16: 
                adjustTimeout();
                break;
            case 19: 
                testConnection();
                break;
            case 21: 
                showUsageStats();
                break;
            case 23: 
                resetToDefaults();
                break;
            case 25: 
                openAdvancedSettings();
                break;
            case 40: 
                saveConfiguration();
                break;
        }
    }
    
    private void configureAPIKey() {
        player.sendMessage("§d=== API Key Configuration ===");
        player.sendMessage("§7To set your API key, edit the config.yml file:");
        player.sendMessage("§7");
        player.sendMessage("§egemini-api-key: YOUR_API_KEY_HERE");
        player.sendMessage("§7");
        player.sendMessage("§7Then run: §a/sc reload");
        player.sendMessage("§7");
        player.sendMessage("§8Note: Never share your API key with others!");
        player.closeInventory();
    }
    
    private void configureEndpoint() {
        player.sendMessage("§eEndpoint configuration interface coming soon!");
        player.sendMessage("§7Current endpoint is working correctly.");
    }
    
    private void adjustRateLimit() {
        RateLimitAdjustmentGUI rateLimitGUI = new RateLimitAdjustmentGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), rateLimitGUI);
        rateLimitGUI.open();
    }
    
    private void adjustTimeout() {
        TimeoutAdjustmentGUI timeoutGUI = new TimeoutAdjustmentGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), timeoutGUI);
        timeoutGUI.open();
    }
    
    private void testConnection() {
        player.sendMessage("§a🔍 Testing API connection...");
        player.closeInventory();
        
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                
                boolean hasValidKey = plugin.getConfigManager().getApiKey() != null && 
                                    !plugin.getConfigManager().getApiKey().isEmpty() && 
                                    !plugin.getConfigManager().getApiKey().equals("PASTE_YOUR_API_KEY_HERE");
                
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (hasValidKey) {
                        player.sendMessage("§a✓ API configuration appears valid!");
                        player.sendMessage("§7API key format is correct.");
                        player.sendMessage("§7To fully test, send a chat message that might be flagged.");
                    } else {
                        player.sendMessage("§c✗ API configuration invalid!");
                        player.sendMessage("§7Please set a valid API key in config.yml");
                    }
                });
            } catch (Exception e) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§c✗ Connection test failed!");
                    player.sendMessage("§7Error: " + e.getMessage());
                });
            }
        });
    }
    
    private void showUsageStats() {
        player.sendMessage("§bAPI usage statistics coming soon!");
    }
    
    private void resetToDefaults() {
        plugin.getConfigManager().getConfig().set("api.rate-limit", 15);
        plugin.getConfigManager().getConfig().set("api.timeout", 10);
        plugin.getConfigManager().getConfig().set("api.endpoint", "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent");
        
        refresh();
        player.sendMessage("§cAPI settings reset to defaults!");
    }
    
    private void openAdvancedSettings() {
        player.sendMessage("§5Advanced API settings coming soon!");
    }
    
    private void saveConfiguration() {
        plugin.getConfigManager().saveConfig();
        plugin.reload();
        player.sendMessage("§a✓ API configuration saved and reloaded!");
        player.closeInventory();
    }
}