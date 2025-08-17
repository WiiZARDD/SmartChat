package com.smartchat.config;

import com.smartchat.SmartChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigManager {
    
    private final SmartChat plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private File configFile;
    private File messagesFile;
    
    public ConfigManager(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        
        checkConfig();
        
        
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        
        checkMessages();
        
        plugin.getLogger().info("Configuration loaded successfully!");
    }
    
    private void checkConfig() {
        boolean modified = false;
        
        
        InputStream defaultConfigStream = plugin.getResource("config.yml");
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultConfigStream)
            );
            
            
            for (String key : defaultConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.get(key));
                    modified = true;
                    plugin.getLogger().info("Added missing config option: " + key);
                }
            }
        }
        
        if (modified) {
            saveConfig();
        }
    }
    
    private void checkMessages() {
        boolean modified = false;
        
        
        InputStream defaultMessagesStream = plugin.getResource("messages.yml");
        if (defaultMessagesStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultMessagesStream)
            );
            
            
            for (String key : defaultMessages.getKeys(true)) {
                if (!messages.contains(key)) {
                    messages.set(key, defaultMessages.get(key));
                    modified = true;
                    plugin.getLogger().info("Added missing message: " + key);
                }
            }
        }
        
        if (modified) {
            saveMessages();
        }
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config.yml", e);
        }
    }
    
    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save messages.yml", e);
        }
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public FileConfiguration getMessages() {
        return messages;
    }
    
    public String getApiKey() {
        plugin.getLogger().info("DEBUG: ConfigManager getApiKey() called");
        plugin.getLogger().info("DEBUG: Config file exists: " + configFile.exists());
        plugin.getLogger().info("DEBUG: Config file path: " + configFile.getAbsolutePath());
        
        
        if (config == null) {
            plugin.getLogger().warning("DEBUG: Config is NULL! Reloading...");
            loadConfig();
        }
        
        
        plugin.getLogger().info("DEBUG: All config keys: " + config.getKeys(false));
        
        
        String key = config.getString("gemini-api-key", "");
        String keyWithDefault = config.getString("gemini-api-key", "DEFAULT_NOT_FOUND");
        Object rawValue = config.get("gemini-api-key");
        
        plugin.getLogger().info("DEBUG: Raw key from config.getString(): " + (key != null ? "'" + key + "'" : "NULL"));
        plugin.getLogger().info("DEBUG: Key with default: " + (keyWithDefault != null ? "'" + keyWithDefault + "'" : "NULL"));
        plugin.getLogger().info("DEBUG: Raw object from config.get(): " + (rawValue != null ? rawValue.getClass().getSimpleName() + " = '" + rawValue + "'" : "NULL"));
        plugin.getLogger().info("DEBUG: Key length: " + (key != null ? key.length() : 0));
        plugin.getLogger().info("DEBUG: Key is empty: " + (key != null && key.isEmpty()));
        
        return key;
    }
    
    public void setApiKey(String apiKey) {
        config.set("gemini-api-key", apiKey);
        saveConfig();
    }
    
    public String getMessage(String path, String... replacements) {
        String message = messages.getString(path, "&cMessage not found: " + path);
        
        
        if (!path.contains("punishments.kick.screen") && 
            !path.contains("punishments.ban.screen") && 
            !path.contains(".header") && 
            !path.contains(".footer")) {
            String prefix = messages.getString("prefix", "");
            message = prefix + message;
        }
        
        
        if (replacements.length > 0 && replacements.length % 2 == 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        
        
        return message.replace("&", "§");
    }
    
    public double getThreshold(String category) {
        
        if (config.contains("thresholds.categories." + category)) {
            return config.getDouble("thresholds.categories." + category);
        }
        
        
        if (config.contains("thresholds.custom." + category)) {
            return config.getDouble("thresholds.custom." + category);
        }
        
        
        return config.getDouble("thresholds.toxicity", 0.75);
    }
    
    public boolean isWorldEnabled(String worldName) {
        
        if (config.contains("world-settings." + worldName + ".enabled")) {
            return config.getBoolean("world-settings." + worldName + ".enabled");
        }
        
        
        return config.getBoolean("world-settings.default.enabled", true);
    }
    
    public double getWorldThreshold(String worldName, String category) {
        
        String path = "world-settings." + worldName + ".thresholds." + category;
        if (config.contains(path)) {
            return config.getDouble(path);
        }
        
        
        return getThreshold(category);
    }
    
    public boolean shouldBypassPlayer(String playerName, String uuid) {
        
        if (config.getStringList("overrides.bypass-players.uuids").contains(uuid)) {
            return true;
        }
        
        
        return false;
    }
    
    public boolean isMessageWhitelisted(String message) {
        for (String pattern : config.getStringList("overrides.whitelist")) {
            if (message.matches(pattern) || message.equalsIgnoreCase(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isMessageBlacklisted(String message) {
        for (String pattern : config.getStringList("overrides.blacklist")) {
            if (message.matches(pattern) || message.toLowerCase().contains(pattern.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}