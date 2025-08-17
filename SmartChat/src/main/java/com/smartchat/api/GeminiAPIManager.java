package com.smartchat.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartchat.SmartChat;
import com.smartchat.models.ModerationResult;
import okhttp3.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class GeminiAPIManager {
    
    private final SmartChat plugin;
    private String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    private final Map<String, CachedResult> cache;
    private final Queue<Long> rateLimitQueue;
    private final int rateLimit;
    private final long cacheExpiry;
    
    private boolean isOffline = false;
    private long offlineUntil = 0;
    
    public GeminiAPIManager(SmartChat plugin, String apiKey) {
        this.plugin = plugin;
        this.apiKey = apiKey;
        this.gson = new Gson();
        this.cache = new ConcurrentHashMap<>();
        this.rateLimitQueue = new ConcurrentLinkedQueue<>();
        
        this.rateLimit = plugin.getConfigManager().getConfig().getInt("api.rate-limit", 15);
        this.cacheExpiry = plugin.getConfigManager().getConfig().getInt("api.cache-duration", 300) * 1000L;
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(plugin.getConfigManager().getConfig().getInt("api.timeout", 10), TimeUnit.SECONDS)
            .build();
        
        
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::cleanupCache, 
            20L * 60, 20L * 60); 
    }
    
    public CompletableFuture<ModerationResult> analyzeMessage(String message, String playerName, String context) {
        return CompletableFuture.supplyAsync(() -> {
            
            if (!isApiAvailable()) {
                plugin.getLogger().warning("API not available, using limited fallback analysis");
                return limitedFallbackAnalysis(message);
            }
            
            
            String cacheKey = generateCacheKey(message, context);
            CachedResult cached = cache.get(cacheKey);
            if (cached != null && !cached.isExpired()) {
                return cached.result;
            }
            
            
            if (!checkRateLimit()) {
                plugin.getLogger().warning("Rate limit exceeded, using fallback analysis");
                return limitedFallbackAnalysis(message);
            }
            
            try {
                
                String prompt = buildPrompt(message, playerName, context);
                String endpoint = plugin.getConfigManager().getConfig().getString("api.endpoint");
                
                if (!endpoint.contains("?")) {
                    endpoint += "?key=" + apiKey;
                } else {
                    endpoint += "&key=" + apiKey;
                }
                
                
                JsonObject requestBody = new JsonObject();
                
                JsonObject textPart = new JsonObject();
                textPart.addProperty("text", prompt);
                
                JsonObject parts = new JsonObject();
                parts.add("parts", gson.toJsonTree(Collections.singletonList(textPart)));
                
                requestBody.add("contents", gson.toJsonTree(Collections.singletonList(parts)));
                
                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(requestBody)
                );
                
                Request request = new Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .build();
                
                plugin.getLogger().info("🤖 USING GEMINI AI to analyze: " + message);
                plugin.getLogger().info("Sending API request to: " + endpoint);
                
                
                long startTime = System.currentTimeMillis();
                
                
                Response response = executeWithRetries(request);
                
                
                long responseTime = System.currentTimeMillis() - startTime;
                plugin.getPerformanceTracker().recordApiResponse(responseTime);
                
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    plugin.getLogger().info("✅ GEMINI AI RESPONSE received");
                    ModerationResult result = parseApiResponse(responseBody, message);
                    plugin.getLogger().info("🧠 AI DECISION: " + (result.isFlagged() ? "FLAGGED" : "CLEAN") + 
                        " (confidence: " + String.format("%.1f", result.getConfidence() * 100) + "%)");
                    
                    
                    if (result.isFlagged()) {
                        learnFromViolation(message, result);
                    }
                    
                    
                    cache.put(cacheKey, new CachedResult(result));
                    
                    return result;
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    plugin.getLogger().warning("API request failed: " + response.code() + " " + response.message());
                    plugin.getLogger().warning("Error response: " + errorBody);
                    handleApiError(response.code());
                    return limitedFallbackAnalysis(message);
                }
                
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error analyzing message", e);
                return limitedFallbackAnalysis(message);
            }
        });
    }
    
    private String buildPrompt(String message, String playerName, String context) {
        String promptTemplate = plugin.getConfigManager().getConfig().getString("ai-prompts.analysis");
        String serverType = plugin.getConfigManager().getConfig().getString("ai-prompts.server-type", "general");
        
        
        String recentViolations = getRecentViolationsContext();
        
        return promptTemplate
            .replace("{message}", message)
            .replace("{player}", playerName)
            .replace("{context}", context != null ? context : "No additional context")
            .replace("{server_type}", serverType)
            .replace("{recent_violations}", recentViolations);
    }
    
    private String getRecentViolationsContext() {
        
        try {
            
            
            return "Recent server violations have included character substitutions like n!66er, f@ggot, " +
                   "spacing tricks like 'n i g g e r', and leetspeak like '5h1t'. " +
                   "Watch for similar bypass attempts and new variations.";
        } catch (Exception e) {
            return "No recent violation data available.";
        }
    }
    
    private void learnFromViolation(String message, ModerationResult result) {
        
        try {
            String normalizedMessage = normalizeForBypassDetection(message);
            String learningKey = "violation_pattern_" + result.getPrimaryCategory();
            
            
            plugin.getLogger().info("LEARNING: Flagged message '" + message + "' normalized to '" + 
                normalizedMessage + "' for category: " + result.getPrimaryCategory());
                
            
            
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to store learning data: " + e.getMessage());
        }
    }
    
    private ModerationResult parseApiResponse(String responseBody, String originalMessage) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            
            JsonObject candidate = response.getAsJsonArray("candidates").get(0).getAsJsonObject();
            JsonObject content = candidate.getAsJsonObject("content");
            String text = content.getAsJsonArray("parts").get(0).getAsJsonObject()
                .get("text").getAsString();
            
            
            Map<String, Double> scores = parseScores(text);
            
            
            String primaryCategory = "none";
            double highestScore = 0.0;
            String severity = "low";
            
            for (Map.Entry<String, Double> entry : scores.entrySet()) {
                if (entry.getValue() > highestScore) {
                    highestScore = entry.getValue();
                    primaryCategory = entry.getKey();
                }
            }
            
            
            if (highestScore >= 0.85) {
                severity = "extreme";
            } else if (highestScore >= 0.75) {
                severity = "high";
            } else if (highestScore >= 0.60) {
                severity = "medium";
            }
            
            
            boolean shouldFlag = false;
            
            
            if (scores.containsKey("toxicity")) {
                double toxicityThreshold = plugin.getConfigManager().getConfig()
                    .getDouble("thresholds.toxicity", 0.75);
                plugin.getLogger().info("Checking toxicity: " + scores.get("toxicity") + " >= " + toxicityThreshold);
                if (scores.get("toxicity") >= toxicityThreshold) {
                    shouldFlag = true;
                    plugin.getLogger().info("Flagged due to toxicity threshold");
                }
            }
            
            
            for (String category : scores.keySet()) {
                double threshold = plugin.getConfigManager().getConfig()
                    .getDouble("thresholds.categories." + category, 0.75);
                plugin.getLogger().info("Checking " + category + ": " + scores.get(category) + " >= " + threshold);
                if (scores.get(category) >= threshold) {
                    shouldFlag = true;
                    plugin.getLogger().info("Flagged due to " + category + " threshold");
                    break;
                }
            }
            
            return new ModerationResult(
                originalMessage,
                shouldFlag,
                primaryCategory,
                severity,
                highestScore,
                scores,
                text 
            );
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to parse API response", e);
            return limitedFallbackAnalysis(originalMessage);
        }
    }
    
    private Map<String, Double> parseScores(String aiResponse) {
        Map<String, Double> scores = new HashMap<>();
        
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            line = line.toLowerCase().trim();
            
            
            if (line.contains(":")) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String category = parts[0].trim();
                    try {
                        
                        String numberStr = parts[1].trim();
                        
                        numberStr = numberStr.replaceAll("[^0-9.]", "");
                        if (!numberStr.isEmpty()) {
                            double score = Double.parseDouble(numberStr);
                            scores.put(category, Math.min(1.0, Math.max(0.0, score)));
                            
                            
                            plugin.getLogger().info("Parsed " + category + ": " + score);
                        }
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Failed to parse score for: " + line);
                    }
                }
            }
        }
        
        
        plugin.getLogger().info("Full AI Response: " + aiResponse);
        
        
        String[] defaultCategories = {"toxicity", "harassment", "spam", "profanity"};
        for (String category : defaultCategories) {
            scores.putIfAbsent(category, 0.0);
        }
        
        plugin.getLogger().info("Final scores: " + scores);
        return scores;
    }
    
    private ModerationResult limitedFallbackAnalysis(String message) {
        plugin.getLogger().warning("⚠️ USING FALLBACK (No AI) for: " + message);
        
        
        Map<String, Double> scores = new HashMap<>();
        scores.put("toxicity", 0.0);
        scores.put("harassment", 0.0);
        scores.put("spam", 0.0);
        scores.put("profanity", 0.0);
        
        String lowerMessage = message.toLowerCase();
        boolean flagged = false;
        String category = "none";
        double confidence = 0.0;
        
        
        String normalizedMessage = normalizeForBypassDetection(message);
        
        
        String[] severeProfanity = {
            "nigger", "nigga", "neger", "niger", "nig", "faggot", "fag", "fagot", 
            "retard", "retrd", "kike", "chink", "spic", "wetback", "gook", "tranny"
        };
        
        
        String[] commonProfanity = {
            "fuck", "fck", "fuk", "shit", "sht", "bitch", "btch", 
            "cunt", "cnt", "whore", "whor", "slut", "slt", 
            "kill yourself", "kys", "killyourself", "porn", "prn", "rape", "rpe"
            
            
        };
        
        
        for (String word : severeProfanity) {
            if (containsProfanity(normalizedMessage, word)) {
                scores.put("profanity", 1.0);
                scores.put("toxicity", 1.0);
                scores.put("harassment", 0.9);
                flagged = true;
                category = "hate-speech";
                confidence = 1.0;
                plugin.getLogger().warning("SEVERE VIOLATION DETECTED: " + message + " (found: " + word + ")");
                break;
            }
        }
        
        
        
        if (!flagged) {
            for (String word : commonProfanity) {
                
                if (isObviousProfanity(normalizedMessage, word)) {
                    scores.put("profanity", 0.9);
                    scores.put("toxicity", 0.8);
                    flagged = true;
                    category = "profanity";
                    confidence = 0.9;
                    plugin.getLogger().info("Found OBVIOUS profanity '" + word + "' in message: " + normalizedMessage);
                    break;
                }
            }
        }
        
        
        List<String> blacklist = plugin.getConfigManager().getConfig()
            .getStringList("overrides.blacklist");
        for (String blocked : blacklist) {
            if (lowerMessage.contains(blocked.toLowerCase())) {
                flagged = true;
                category = "blacklisted";
                confidence = 1.0;
                break;
            }
        }
        
        
        if (!flagged) {
            
            int caps = 0;
            for (char c : message.toCharArray()) {
                if (Character.isUpperCase(c)) caps++;
            }
            if (message.length() > 5 && caps > message.length() * 0.7) {
                scores.put("spam", 0.8);
                flagged = true;
                category = "spam";
                confidence = 0.8;
            }
            
            
            if (message.length() > 10) {
                for (int i = 3; i <= message.length() / 2; i++) {
                    String pattern = message.substring(0, i);
                    if (message.equals(pattern.repeat(message.length() / i))) {
                        scores.put("spam", 0.9);
                        flagged = true;
                        category = "spam";
                        confidence = 0.9;
                        break;
                    }
                }
            }
        }
        
        plugin.getLogger().info("Fallback result - flagged: " + flagged + ", category: " + category + ", confidence: " + confidence);
        
        return new ModerationResult(
            message,
            flagged,
            category,
            flagged ? "medium" : "low",
            confidence,
            scores,
            "Fallback analysis (API unavailable)"
        );
    }
    
    private boolean isApiAvailable() {
        plugin.getLogger().info("DEBUG: Checking API availability...");
        plugin.getLogger().info("DEBUG: API key from config: " + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "NULL"));
        plugin.getLogger().info("DEBUG: API key length: " + (apiKey != null ? apiKey.length() : 0));
        plugin.getLogger().info("DEBUG: API key equals placeholder: " + (apiKey != null && apiKey.equals("PASTE_YOUR_API_KEY_HERE")));
        
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("PASTE_YOUR_API_KEY_HERE")) {
            plugin.getLogger().warning("API unavailable: Invalid or missing API key");
            plugin.getLogger().warning("DEBUG: Key is null: " + (apiKey == null));
            plugin.getLogger().warning("DEBUG: Key is empty: " + (apiKey != null && apiKey.isEmpty()));
            plugin.getLogger().warning("DEBUG: Key is placeholder: " + (apiKey != null && apiKey.equals("PASTE_YOUR_API_KEY_HERE")));
            return false;
        }
        
        if (isOffline && System.currentTimeMillis() < offlineUntil) {
            plugin.getLogger().info("API unavailable: Currently offline until " + offlineUntil);
            return false;
        }
        
        isOffline = false;
        plugin.getLogger().info("API available: Using Gemini AI with key: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        return true;
    }
    
    private boolean checkRateLimit() {
        long now = System.currentTimeMillis();
        
        
        while (!rateLimitQueue.isEmpty() && rateLimitQueue.peek() < now - 60000) {
            rateLimitQueue.poll();
        }
        
        if (rateLimitQueue.size() >= rateLimit) {
            return false;
        }
        
        rateLimitQueue.offer(now);
        return true;
    }
    
    private Response executeWithRetries(Request request) throws IOException {
        int maxRetries = plugin.getConfigManager().getConfig().getInt("api.retry-attempts", 2);
        int retryDelay = plugin.getConfigManager().getConfig().getInt("api.retry-delay", 1000);
        
        IOException lastException = null;
        
        for (int i = 0; i <= maxRetries; i++) {
            try {
                return httpClient.newCall(request).execute();
            } catch (IOException e) {
                lastException = e;
                if (i < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        
        throw lastException;
    }
    
    private void handleApiError(int statusCode) {
        switch (statusCode) {
            case 401:
                plugin.getLogger().severe("Invalid API key! Please check your configuration.");
                isOffline = true;
                offlineUntil = System.currentTimeMillis() + 300000; 
                break;
            case 429:
                plugin.getLogger().warning("Rate limit exceeded. Backing off...");
                break;
            case 500:
            case 502:
            case 503:
                plugin.getLogger().warning("API service temporarily unavailable.");
                isOffline = true;
                offlineUntil = System.currentTimeMillis() + 60000; 
                break;
        }
    }
    
    private String generateCacheKey(String message, String context) {
        return (message + "|" + (context != null ? context : "")).hashCode() + "";
    }
    
    private void cleanupCache() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public void updateApiKey(String newApiKey) {
        this.apiKey = newApiKey;
        this.isOffline = false;
        this.offlineUntil = 0;
    }
    
    private String normalizeForBypassDetection(String message) {
        String normalized = message.toLowerCase();
        
        
        normalized = normalized.replaceAll("[\\s\\-_\\.\\*\\+\\|\\\\/#@$%^&()\\[\\]{}]", "");
        
        
        Map<String, String> substitutions = new HashMap<>();
        substitutions.put("@", "a");
        substitutions.put("4", "a");
        substitutions.put("3", "e");
        substitutions.put("1", "i");
        substitutions.put("!", "i");
        substitutions.put("0", "o");
        substitutions.put("5", "s");
        substitutions.put("7", "t");
        substitutions.put("8", "b");
        substitutions.put("6", "g");
        substitutions.put("9", "g");
        substitutions.put("$", "s");
        substitutions.put("2", "z");
        
        
        for (Map.Entry<String, String> sub : substitutions.entrySet()) {
            normalized = normalized.replace(sub.getKey(), sub.getValue());
        }
        
        
        StringBuilder result = new StringBuilder();
        char lastChar = '\0';
        int repeatCount = 0;
        
        for (char c : normalized.toCharArray()) {
            if (c == lastChar) {
                repeatCount++;
                
                if (repeatCount < 2) {
                    result.append(c);
                }
            } else {
                result.append(c);
                lastChar = c;
                repeatCount = 0;
            }
        }
        
        plugin.getLogger().info("Normalized '" + message + "' to '" + result.toString() + "'");
        return result.toString();
    }
    
    private boolean containsProfanity(String normalizedMessage, String profanityWord) {
        
        
        
        if (normalizedMessage.contains(profanityWord)) {
            return true;
        }
        
        
        if (normalizedMessage.contains(" " + profanityWord + " ") ||
            normalizedMessage.startsWith(profanityWord + " ") ||
            normalizedMessage.endsWith(" " + profanityWord)) {
            return true;
        }
        
        
        String[] separators = {".", ",", "!", "?", ";", ":", "-", "_", "(", ")", "[", "]"};
        for (String sep : separators) {
            if (normalizedMessage.contains(sep + profanityWord + sep) ||
                normalizedMessage.contains(sep + profanityWord + " ") ||
                normalizedMessage.contains(" " + profanityWord + sep) ||
                normalizedMessage.startsWith(profanityWord + sep) ||
                normalizedMessage.endsWith(sep + profanityWord)) {
                return true;
            }
        }
        
        
        String[] words = normalizedMessage.split("[\\s\\-_\\.\\,\\!\\?\\;\\:\\(\\)\\[\\]]+");
        for (String word : words) {
            if (word.equals(profanityWord)) {
                return true;
            }
            
            
            
            if (word.contains(profanityWord) && !isValidWord(word, profanityWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isValidWord(String word, String profanityWord) {
        
        
        
        Map<String, String[]> whitelist = new HashMap<>();
        whitelist.put("hell", new String[]{"hello", "shell", "smell", "well", "hell"}); 
        whitelist.put("ass", new String[]{"class", "pass", "mass", "glass", "grass", "bass"});
        whitelist.put("shit", new String[]{}); 
        whitelist.put("damn", new String[]{}); 
        
        if (whitelist.containsKey(profanityWord)) {
            String[] allowedWords = whitelist.get(profanityWord);
            for (String allowed : allowedWords) {
                if (word.equals(allowed)) {
                    return true; 
                }
            }
        }
        
        return false; 
    }
    
    private boolean isObviousProfanity(String normalizedMessage, String profanityWord) {
        
        
        
        
        String[] words = normalizedMessage.split("[\\s\\-_\\.\\,\\!\\?\\;\\:\\(\\)\\[\\]]+");
        for (String word : words) {
            if (word.equals(profanityWord)) {
                return true; 
            }
        }
        
        
        if (normalizedMessage.equals(profanityWord) ||
            normalizedMessage.startsWith(profanityWord + " ") ||
            normalizedMessage.endsWith(" " + profanityWord) ||
            normalizedMessage.contains(" " + profanityWord + " ")) {
            return true;
        }
        
        
        
        
        return false;
    }
    
    private static class CachedResult {
        final ModerationResult result;
        final long expiry;
        
        CachedResult(ModerationResult result) {
            this.result = result;
            this.expiry = System.currentTimeMillis() + 300000; 
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiry;
        }
    }
}