package com.smartchat.utils;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecentMessageTracker {
    
    public static class MessageEntry {
        private final String playerName;
        private final String message;
        private final long timestamp;
        private final boolean flagged;
        private final String violationType;
        
        public MessageEntry(String playerName, String message, long timestamp, boolean flagged, String violationType) {
            this.playerName = playerName;
            this.message = message;
            this.timestamp = timestamp;
            this.flagged = flagged;
            this.violationType = violationType;
        }
        
        public String getPlayerName() { return playerName; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
        public boolean isFlagged() { return flagged; }
        public String getViolationType() { return violationType; }
    }
    
    private final ConcurrentLinkedQueue<MessageEntry> recentMessages;
    private final int maxSize;
    
    public RecentMessageTracker(int maxSize) {
        this.recentMessages = new ConcurrentLinkedQueue<>();
        this.maxSize = maxSize;
    }
    
    public void addMessage(Player player, String message, boolean flagged, String violationType) {
        recentMessages.offer(new MessageEntry(
            player.getName(),
            message,
            System.currentTimeMillis(),
            flagged,
            violationType
        ));
        
        
        while (recentMessages.size() > maxSize) {
            recentMessages.poll();
        }
    }
    
    public List<MessageEntry> getRecentMessages(int count) {
        List<MessageEntry> result = new LinkedList<>();
        MessageEntry[] array = recentMessages.toArray(new MessageEntry[0]);
        
        
        int start = Math.max(0, array.length - count);
        for (int i = array.length - 1; i >= start; i--) {
            result.add(array[i]);
        }
        
        return result;
    }
    
    public List<MessageEntry> getRecentViolations(int count) {
        List<MessageEntry> result = new LinkedList<>();
        MessageEntry[] array = recentMessages.toArray(new MessageEntry[0]);
        
        
        for (int i = array.length - 1; i >= 0 && result.size() < count; i--) {
            if (array[i].isFlagged()) {
                result.add(array[i]);
            }
        }
        
        return result;
    }
    
    public int getTotalProcessed() {
        return recentMessages.size();
    }
}