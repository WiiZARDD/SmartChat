package com.smartchat.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Appeal {
    private final int id;
    private final UUID playerUuid;
    private final int violationId;
    private final Timestamp timestamp;
    private final String reason;
    private final String status;
    private String playerName;
    private UUID reviewedBy;
    private Timestamp reviewTimestamp;
    private String reviewNotes;
    
    public Appeal(int id, UUID playerUuid, int violationId, Timestamp timestamp, 
                 String reason, String status) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.violationId = violationId;
        this.timestamp = timestamp;
        this.reason = reason;
        this.status = status;
    }
    
    public Appeal(UUID playerUuid, int violationId, String reason) {
        this(-1, playerUuid, violationId, new Timestamp(System.currentTimeMillis()), 
             reason, "pending");
    }
    
    public int getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public int getViolationId() { return violationId; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getPlayerName() { return playerName; }
    public UUID getReviewedBy() { return reviewedBy; }
    public Timestamp getReviewTimestamp() { return reviewTimestamp; }
    public String getReviewNotes() { return reviewNotes; }
    
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setReviewedBy(UUID reviewedBy) { this.reviewedBy = reviewedBy; }
    public void setReviewTimestamp(Timestamp reviewTimestamp) { this.reviewTimestamp = reviewTimestamp; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
}