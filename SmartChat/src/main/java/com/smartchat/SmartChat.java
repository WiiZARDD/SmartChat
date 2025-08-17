package com.smartchat;

import com.smartchat.api.GeminiAPIManager;
import com.smartchat.commands.*;
import com.smartchat.config.ConfigManager;
import com.smartchat.database.DatabaseManager;
import com.smartchat.listeners.ChatListener;
import com.smartchat.analytics.AnalyticsManager;
import com.smartchat.actions.ActionManager;
import com.smartchat.gui.GUIManager;
import com.smartchat.utils.RecentMessageTracker;
import com.smartchat.utils.PerformanceTracker;
import com.smartchat.utils.ExportManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SmartChat extends JavaPlugin {
    
    private static SmartChat instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private GeminiAPIManager apiManager;
    private AnalyticsManager analyticsManager;
    private ActionManager actionManager;
    private GUIManager guiManager;
    private RecentMessageTracker messageTracker;
    private PerformanceTracker performanceTracker;
    private ExportManager exportManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("SmartChat v" + getDescription().getVersion() + " starting...");
        
        if (!initializeComponents()) {
            getLogger().severe("Failed to initialize SmartChat! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        registerCommands();
        registerListeners();
        
        
        scheduleDailyReset();
        
        
        performanceTracker.updatePeakPlayers(getServer().getOnlinePlayers().size());
        
        getLogger().info("SmartChat successfully enabled!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        if (analyticsManager != null) {
            analyticsManager.saveAnalytics();
        }
        
        if (guiManager != null) {
            guiManager.closeAllGUIs();
        }
        
        getLogger().info("SmartChat disabled.");
    }
    
    private boolean initializeComponents() {
        try {
            configManager = new ConfigManager(this);
            configManager.loadConfig();
            
            databaseManager = new DatabaseManager(this);
            if (!databaseManager.initialize()) {
                getLogger().severe("Failed to initialize database!");
                return false;
            }
            
            String apiKey = configManager.getApiKey();
            getLogger().info("DEBUG: Config loaded API key: " + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "NULL"));
            getLogger().info("DEBUG: Config API key length: " + (apiKey != null ? apiKey.length() : 0));
            
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("PASTE_YOUR_API_KEY_HERE")) {
                getLogger().warning("==================================================");
                getLogger().warning("No valid Gemini API key found!");
                getLogger().warning("Please add your API key to config.yml");
                getLogger().warning("Get your free key at: https://ai.google.dev/");
                getLogger().warning("Plugin will run in limited mode without AI features.");
                getLogger().warning("==================================================");
            }
            
            apiManager = new GeminiAPIManager(this, apiKey);
            actionManager = new ActionManager(this);
            analyticsManager = new AnalyticsManager(this);
            guiManager = new GUIManager(this);
            messageTracker = new RecentMessageTracker(100); 
            performanceTracker = new PerformanceTracker();
            exportManager = new ExportManager(this);
            
            return true;
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error initializing components", e);
            return false;
        }
    }
    
    private void registerCommands() {
        getCommand("smartchat").setExecutor(new MainCommand(this));
        getCommand("screload").setExecutor(new ReloadCommand(this));
        getCommand("scstats").setExecutor(new StatsCommand(this));
        getCommand("sccheck").setExecutor(new CheckCommand(this));
        getCommand("appeal").setExecutor(new AppealCommand(this));
        
        
        PlayerCommand playerCommand = new PlayerCommand(this);
        getCommand("scplayer").setExecutor(playerCommand);
        getCommand("scplayer").setTabCompleter(playerCommand);
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
    }
    
    private void scheduleDailyReset() {
        
        long currentTime = System.currentTimeMillis();
        long midnight = (currentTime / 86400000L + 1) * 86400000L;
        long ticksUntilMidnight = (midnight - currentTime) / 50L;
        
        
        getServer().getScheduler().runTaskLater(this, () -> {
            performanceTracker.resetDaily();
            getLogger().info("Daily statistics reset completed.");
            
            
            getServer().getScheduler().runTaskTimer(this, () -> {
                performanceTracker.resetDaily();
                getLogger().info("Daily statistics reset completed.");
            }, 0L, 1728000L); 
        }, ticksUntilMidnight);
    }
    
    public void reload() {
        configManager.loadConfig();
        apiManager.updateApiKey(configManager.getApiKey());
        actionManager.reload();
        getLogger().info("SmartChat configuration reloaded!");
    }
    
    public static SmartChat getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public GeminiAPIManager getApiManager() {
        return apiManager;
    }
    
    public AnalyticsManager getAnalyticsManager() {
        return analyticsManager;
    }
    
    public ActionManager getActionManager() {
        return actionManager;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
    
    public RecentMessageTracker getMessageTracker() {
        return messageTracker;
    }
    
    public PerformanceTracker getPerformanceTracker() {
        return performanceTracker;
    }
    
    public ExportManager getExportManager() {
        return exportManager;
    }
}