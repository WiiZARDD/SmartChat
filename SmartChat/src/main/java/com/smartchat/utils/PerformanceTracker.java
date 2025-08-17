package com.smartchat.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceTracker {
    
    private final AtomicLong totalApiResponseTime = new AtomicLong(0);
    private final AtomicInteger apiRequestCount = new AtomicInteger(0);
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    private final AtomicInteger processedCount = new AtomicInteger(0);
    
    private final ConcurrentLinkedQueue<Long> recentApiTimes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Long> recentProcessingTimes = new ConcurrentLinkedQueue<>();
    
    private final AtomicInteger todayMessages = new AtomicInteger(0);
    private final AtomicInteger todayViolations = new AtomicInteger(0);
    private final AtomicInteger todayActions = new AtomicInteger(0);
    private final AtomicInteger todayFalsePositives = new AtomicInteger(0);
    
    private final int maxRecent = 100;
    private int peakPlayers = 0;
    
    public void recordApiResponse(long responseTime) {
        totalApiResponseTime.addAndGet(responseTime);
        apiRequestCount.incrementAndGet();
        
        recentApiTimes.offer(responseTime);
        while (recentApiTimes.size() > maxRecent) {
            recentApiTimes.poll();
        }
    }
    
    public void recordProcessingTime(long processingTime) {
        totalProcessingTime.addAndGet(processingTime);
        processedCount.incrementAndGet();
        
        recentProcessingTimes.offer(processingTime);
        while (recentProcessingTimes.size() > maxRecent) {
            recentProcessingTimes.poll();
        }
    }
    
    public void recordMessage() {
        todayMessages.incrementAndGet();
    }
    
    public void recordViolation() {
        todayViolations.incrementAndGet();
    }
    
    public void recordAction() {
        todayActions.incrementAndGet();
    }
    
    public void recordFalsePositive() {
        todayFalsePositives.incrementAndGet();
    }
    
    public void updatePeakPlayers(int currentPlayers) {
        if (currentPlayers > peakPlayers) {
            peakPlayers = currentPlayers;
        }
    }
    
    public long getAverageApiResponseTime() {
        if (recentApiTimes.isEmpty()) return 0;
        
        long sum = 0;
        for (Long time : recentApiTimes) {
            sum += time;
        }
        return sum / recentApiTimes.size();
    }
    
    public long getAverageProcessingTime() {
        if (recentProcessingTimes.isEmpty()) return 0;
        
        long sum = 0;
        for (Long time : recentProcessingTimes) {
            sum += time;
        }
        return sum / recentProcessingTimes.size();
    }
    
    public double getAccuracyRate() {
        int total = todayViolations.get() + todayFalsePositives.get();
        if (total == 0) return 100.0;
        
        return ((double) todayViolations.get() / total) * 100.0;
    }
    
    public int getTodayMessages() { return todayMessages.get(); }
    public int getTodayViolations() { return todayViolations.get(); }
    public int getTodayActions() { return todayActions.get(); }
    public int getTodayFalsePositives() { return todayFalsePositives.get(); }
    public int getPeakPlayers() { return peakPlayers; }
    
    public void resetDaily() {
        todayMessages.set(0);
        todayViolations.set(0);
        todayActions.set(0);
        todayFalsePositives.set(0);
        peakPlayers = 0;
    }
}