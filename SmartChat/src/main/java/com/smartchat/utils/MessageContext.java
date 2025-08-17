package com.smartchat.utils;

import java.util.*;

public class MessageContext {
    private final UUID playerUuid;
    private final LinkedList<MessageEntry> recentMessages;
    private final Map<String, Integer> violationCategories;
    private long lastMessageTime;
    private int rapidMessageCount;
    private static final int MAX_CONTEXT_SIZE = 10;
    private static final long RAPID_MESSAGE_THRESHOLD = 1000; 
    
    public MessageContext(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.recentMessages = new LinkedList<>();
        this.violationCategories = new HashMap<>();
        this.lastMessageTime = 0;
        this.rapidMessageCount = 0;
    }
    
    public void addMessage(String message, boolean wasViolation) {
        long currentTime = System.currentTimeMillis();
        
        
        if (currentTime - lastMessageTime < RAPID_MESSAGE_THRESHOLD) {
            rapidMessageCount++;
        } else {
            rapidMessageCount = 0;
        }
        
        lastMessageTime = currentTime;
        
        
        recentMessages.addLast(new MessageEntry(message, currentTime, wasViolation));
        
        
        while (recentMessages.size() > MAX_CONTEXT_SIZE) {
            recentMessages.removeFirst();
        }
    }
    
    public void recordViolation(String category) {
        violationCategories.merge(category, 1, Integer::sum);
    }
    
    public String buildContextString() {
        StringBuilder context = new StringBuilder();
        
        context.append("Recent message count: ").append(recentMessages.size()).append("\n");
        context.append("Rapid messages: ").append(rapidMessageCount).append("\n");
        
        if (!violationCategories.isEmpty()) {
            context.append("Previous violations: ");
            violationCategories.forEach((cat, count) -> 
                context.append(cat).append("(").append(count).append(") "));
            context.append("\n");
        }
        
        
        if (hasRepetitivePattern()) {
            context.append("Pattern: Repetitive messages detected\n");
        }
        
        if (hasCapsPattern()) {
            context.append("Pattern: Excessive caps usage\n");
        }
        
        return context.toString();
    }
    
    private boolean hasRepetitivePattern() {
        if (recentMessages.size() < 3) return false;
        
        Map<String, Integer> messageCount = new HashMap<>();
        for (MessageEntry entry : recentMessages) {
            messageCount.merge(entry.message.toLowerCase(), 1, Integer::sum);
        }
        
        return messageCount.values().stream().anyMatch(count -> count >= 3);
    }
    
    private boolean hasCapsPattern() {
        if (recentMessages.isEmpty()) return false;
        
        int capsMessages = 0;
        for (MessageEntry entry : recentMessages) {
            int caps = 0;
            for (char c : entry.message.toCharArray()) {
                if (Character.isUpperCase(c)) caps++;
            }
            
            if (entry.message.length() > 5 && caps > entry.message.length() * 0.5) {
                capsMessages++;
            }
        }
        
        return capsMessages >= 2;
    }
    
    public int getRapidMessageCount() {
        return rapidMessageCount;
    }
    
    public Map<String, Integer> getViolationCategories() {
        return new HashMap<>(violationCategories);
    }
    
    private static class MessageEntry {
        final String message;
        final long timestamp;
        final boolean wasViolation;
        
        MessageEntry(String message, long timestamp, boolean wasViolation) {
            this.message = message;
            this.timestamp = timestamp;
            this.wasViolation = wasViolation;
        }
    }
}