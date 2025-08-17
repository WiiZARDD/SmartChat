package com.smartchat.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsData {
    private static final Gson gson = new Gson();
    
    private final Date date;
    private final int totalMessages;
    private final int flaggedMessages;
    private final int actionsTaken;
    private final int uniquePlayers;
    private final Map<String, Integer> categoryStats;
    private final Map<Integer, Integer> hourlyStats;
    
    public AnalyticsData(Date date, int totalMessages, int flaggedMessages, 
                        int actionsTaken, int uniquePlayers, String categoryStatsJson, 
                        String hourlyStatsJson) {
        this.date = date;
        this.totalMessages = totalMessages;
        this.flaggedMessages = flaggedMessages;
        this.actionsTaken = actionsTaken;
        this.uniquePlayers = uniquePlayers;
        
        this.categoryStats = categoryStatsJson != null ? 
            gson.fromJson(categoryStatsJson, new TypeToken<Map<String, Integer>>(){}.getType()) : 
            new HashMap<>();
            
        this.hourlyStats = hourlyStatsJson != null ? 
            gson.fromJson(hourlyStatsJson, new TypeToken<Map<Integer, Integer>>(){}.getType()) : 
            new HashMap<>();
    }
    
    public Date getDate() { return date; }
    public int getTotalMessages() { return totalMessages; }
    public int getFlaggedMessages() { return flaggedMessages; }
    public int getActionsTaken() { return actionsTaken; }
    public int getUniquePlayers() { return uniquePlayers; }
    public Map<String, Integer> getCategoryStats() { return categoryStats; }
    public Map<Integer, Integer> getHourlyStats() { return hourlyStats; }
    
    public double getFlaggedPercentage() {
        return totalMessages > 0 ? (double) flaggedMessages / totalMessages * 100 : 0;
    }
    
    public String getCategoryStatsJson() {
        return gson.toJson(categoryStats);
    }
    
    public String getHourlyStatsJson() {
        return gson.toJson(hourlyStats);
    }
}