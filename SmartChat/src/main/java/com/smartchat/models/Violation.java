package com.smartchat.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Violation {
    private final int id;
    private final UUID playerUuid;
    private final Timestamp timestamp;
    private final String message;
    private final String filteredMessage;
    private final String category;
    private final String severity;
    private final double confidence;
    private final String actionTaken;
    private final String serverContext;
    private String playerName; 
    
    public Violation(int id, UUID playerUuid, Timestamp timestamp, String message, 
                    String filteredMessage, String category, String severity, 
                    double confidence, String actionTaken, String serverContext) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.timestamp = timestamp;
        this.message = message;
        this.filteredMessage = filteredMessage;
        this.category = category;
        this.severity = severity;
        this.confidence = confidence;
        this.actionTaken = actionTaken;
        this.serverContext = serverContext;
    }
    
    public Violation(UUID playerUuid, String message, String filteredMessage, 
                    String category, String severity, double confidence, 
                    String actionTaken, String serverContext) {
        this(-1, playerUuid, new Timestamp(System.currentTimeMillis()), message, 
             filteredMessage, category, severity, confidence, actionTaken, serverContext);
    }
    
    
    public Violation(int id, UUID playerUuid, String message, String category, 
                    double confidence, String severity, Timestamp timestamp, String actionTaken) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.timestamp = timestamp;
        this.message = message;
        this.filteredMessage = null; 
        this.category = category;
        this.severity = severity;
        this.confidence = confidence;
        this.actionTaken = actionTaken;
        this.serverContext = null; 
        this.playerName = null;
    }
    
    public int getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public String getFilteredMessage() { return filteredMessage; }
    public String getCategory() { return category; }
    public String getSeverity() { return severity; }
    public double getConfidence() { return confidence; }
    public String getActionTaken() { return actionTaken; }
    public String getServerContext() { return serverContext; }
    public String getPlayerName() { return playerName; }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}