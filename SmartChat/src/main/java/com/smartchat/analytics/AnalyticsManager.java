package com.smartchat.analytics;

import com.smartchat.SmartChat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AnalyticsManager {
    
    private final SmartChat plugin;
    private final Map<String, Integer> categoryStats;
    private int totalMessages;
    private int flaggedMessages;
    private int actionsTaken;
    
    public AnalyticsManager(SmartChat plugin) {
        this.plugin = plugin;
        this.categoryStats = new ConcurrentHashMap<>();
        this.totalMessages = 0;
        this.flaggedMessages = 0;
        this.actionsTaken = 0;
    }
    
    public void recordMessage(boolean flagged, String category, boolean actionTaken) {
        totalMessages++;
        
        if (flagged) {
            flaggedMessages++;
            categoryStats.merge(category, 1, Integer::sum);
        }
        
        if (actionTaken) {
            actionsTaken++;
        }
        
        
        plugin.getDatabaseManager().updateAnalytics(category, flagged, actionTaken);
    }
    
    public void saveAnalytics() {
        
    }
    
    public int getTotalMessages() { return totalMessages; }
    public int getFlaggedMessages() { return flaggedMessages; }
    public int getActionsTaken() { return actionsTaken; }
    public Map<String, Integer> getCategoryStats() { return new ConcurrentHashMap<>(categoryStats); }
}