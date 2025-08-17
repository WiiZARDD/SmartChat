package com.smartchat.models;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerRecord {
    private final UUID uuid;
    private final String username;
    private final Timestamp firstSeen;
    private final Timestamp lastSeen;
    private final int totalMessages;
    private final int flaggedMessages;
    private final double violationScore;
    
    public PlayerRecord(UUID uuid, String username, Timestamp firstSeen, 
                       Timestamp lastSeen, int totalMessages, int flaggedMessages, 
                       double violationScore) {
        this.uuid = uuid;
        this.username = username;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.totalMessages = totalMessages;
        this.flaggedMessages = flaggedMessages;
        this.violationScore = violationScore;
    }
    
    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public Timestamp getFirstSeen() { return firstSeen; }
    public Timestamp getLastSeen() { return lastSeen; }
    public int getTotalMessages() { return totalMessages; }
    public int getFlaggedMessages() { return flaggedMessages; }
    public double getViolationScore() { return violationScore; }
    
    public double getFlaggedPercentage() {
        return totalMessages > 0 ? (double) flaggedMessages / totalMessages * 100 : 0;
    }
}