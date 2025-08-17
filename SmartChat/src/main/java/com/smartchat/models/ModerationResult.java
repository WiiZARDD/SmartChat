package com.smartchat.models;

import java.util.Map;

public class ModerationResult {
    private final String originalMessage;
    private final boolean flagged;
    private final String primaryCategory;
    private final String severity;
    private final double confidence;
    private final Map<String, Double> categoryScores;
    private final String aiAnalysis;
    
    public ModerationResult(String originalMessage, boolean flagged, String primaryCategory,
                           String severity, double confidence, Map<String, Double> categoryScores,
                           String aiAnalysis) {
        this.originalMessage = originalMessage;
        this.flagged = flagged;
        this.primaryCategory = primaryCategory;
        this.severity = severity;
        this.confidence = confidence;
        this.categoryScores = categoryScores;
        this.aiAnalysis = aiAnalysis;
    }
    
    public String getOriginalMessage() { return originalMessage; }
    public boolean isFlagged() { return flagged; }
    public String getPrimaryCategory() { return primaryCategory; }
    public String getSeverity() { return severity; }
    public double getConfidence() { return confidence; }
    public Map<String, Double> getCategoryScores() { return categoryScores; }
    public String getAiAnalysis() { return aiAnalysis; }
    
    public double getScoreForCategory(String category) {
        return categoryScores.getOrDefault(category, 0.0);
    }
}