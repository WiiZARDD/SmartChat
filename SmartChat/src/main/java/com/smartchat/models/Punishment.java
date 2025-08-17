package com.smartchat.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Punishment {
    private final int id;
    private final UUID playerUuid;
    private final String type;
    private final String reason;
    private final Timestamp startTime;
    private final Timestamp endTime;
    private final UUID issuedBy;
    private final boolean active;
    
    public Punishment(int id, UUID playerUuid, String type, String reason, 
                     Timestamp startTime, Timestamp endTime, UUID issuedBy, boolean active) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.type = type;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.issuedBy = issuedBy;
        this.active = active;
    }
    
    public Punishment(UUID playerUuid, String type, String reason, Timestamp endTime, UUID issuedBy) {
        this(-1, playerUuid, type, reason, new Timestamp(System.currentTimeMillis()), 
             endTime, issuedBy, true);
    }
    
    public int getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getType() { return type; }
    public String getReason() { return reason; }
    public Timestamp getStartTime() { return startTime; }
    public Timestamp getEndTime() { return endTime; }
    public UUID getIssuedBy() { return issuedBy; }
    public boolean isActive() { return active; }
    
    public boolean isExpired() {
        if (endTime == null) return false;
        return System.currentTimeMillis() > endTime.getTime();
    }
    
    public long getRemainingTime() {
        if (endTime == null) return -1;
        long remaining = endTime.getTime() - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }
}