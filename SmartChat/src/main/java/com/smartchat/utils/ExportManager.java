package com.smartchat.utils;

import com.smartchat.SmartChat;
import com.smartchat.models.PlayerRecord;
import com.smartchat.models.Violation;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExportManager {
    
    private final SmartChat plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private final SimpleDateFormat readableDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    
    public ExportManager(SmartChat plugin) {
        this.plugin = plugin;
    }
    
    public CompletableFuture<File> exportViolationsToCSV(List<Violation> violations) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File exportDir = new File(plugin.getDataFolder(), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                
                String filename = "violations_" + dateFormat.format(new Date()) + ".csv";
                File csvFile = new File(exportDir, filename);
                
                try (FileWriter writer = new FileWriter(csvFile)) {
                    
                    writer.append("ID,Player UUID,Player Name,Timestamp,Message,Category,Severity,Confidence,Action Taken\n");
                    
                    
                    for (Violation violation : violations) {
                        writer.append(String.valueOf(violation.getId())).append(",");
                        writer.append(violation.getPlayerUuid().toString()).append(",");
                        writer.append(escapeCSV(violation.getPlayerName())).append(",");
                        writer.append(readableDateFormat.format(violation.getTimestamp())).append(",");
                        writer.append(escapeCSV(violation.getMessage())).append(",");
                        writer.append(escapeCSV(violation.getCategory())).append(",");
                        writer.append(escapeCSV(violation.getSeverity())).append(",");
                        writer.append(String.valueOf(violation.getConfidence())).append(",");
                        writer.append(escapeCSV(violation.getActionTaken())).append("\n");
                    }
                }
                
                return csvFile;
                
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to export violations to CSV: " + e.getMessage());
                return null;
            }
        });
    }
    
    public CompletableFuture<File> exportPlayerRecordsToCSV(List<PlayerRecord> playerRecords) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File exportDir = new File(plugin.getDataFolder(), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                
                String filename = "player_records_" + dateFormat.format(new Date()) + ".csv";
                File csvFile = new File(exportDir, filename);
                
                try (FileWriter writer = new FileWriter(csvFile)) {
                    
                    writer.append("UUID,Username,First Seen,Last Seen,Total Messages,Flagged Messages,Violation Score,Flag Percentage\n");
                    
                    
                    for (PlayerRecord record : playerRecords) {
                        writer.append(record.getUuid().toString()).append(",");
                        writer.append(escapeCSV(record.getUsername())).append(",");
                        writer.append(record.getFirstSeen() != null ? readableDateFormat.format(record.getFirstSeen()) : "Unknown").append(",");
                        writer.append(record.getLastSeen() != null ? readableDateFormat.format(record.getLastSeen()) : "Unknown").append(",");
                        writer.append(String.valueOf(record.getTotalMessages())).append(",");
                        writer.append(String.valueOf(record.getFlaggedMessages())).append(",");
                        writer.append(String.format("%.2f", record.getViolationScore())).append(",");
                        writer.append(String.format("%.2f", record.getFlaggedPercentage())).append("\n");
                    }
                }
                
                return csvFile;
                
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to export player records to CSV: " + e.getMessage());
                return null;
            }
        });
    }
    
    public CompletableFuture<File> exportMonitoringDataToCSV() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File exportDir = new File(plugin.getDataFolder(), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                
                String filename = "monitoring_data_" + dateFormat.format(new Date()) + ".csv";
                File csvFile = new File(exportDir, filename);
                
                PerformanceTracker perf = plugin.getPerformanceTracker();
                
                try (FileWriter writer = new FileWriter(csvFile)) {
                    
                    writer.append("Metric,Value,Unit\n");
                    
                    
                    writer.append("Average API Response Time,").append(String.valueOf(perf.getAverageApiResponseTime())).append(",milliseconds\n");
                    writer.append("Average Processing Time,").append(String.valueOf(perf.getAverageProcessingTime())).append(",milliseconds\n");
                    writer.append("Messages Today,").append(String.valueOf(perf.getTodayMessages())).append(",count\n");
                    writer.append("Violations Today,").append(String.valueOf(perf.getTodayViolations())).append(",count\n");
                    writer.append("Actions Today,").append(String.valueOf(perf.getTodayActions())).append(",count\n");
                    writer.append("Accuracy Rate,").append(String.format("%.2f", perf.getAccuracyRate())).append(",percentage\n");
                    writer.append("Peak Players,").append(String.valueOf(perf.getPeakPlayers())).append(",count\n");
                    
                    
                    List<RecentMessageTracker.MessageEntry> recentMessages = plugin.getMessageTracker().getRecentMessages(50);
                    writer.append("Recent Messages Count,").append(String.valueOf(recentMessages.size())).append(",count\n");
                    
                    long flaggedCount = recentMessages.stream().mapToLong(msg -> msg.isFlagged() ? 1 : 0).sum();
                    writer.append("Recent Flagged Messages,").append(String.valueOf(flaggedCount)).append(",count\n");
                    
                    double recentFlagRate = recentMessages.size() > 0 ? (double) flaggedCount / recentMessages.size() * 100 : 0;
                    writer.append("Recent Flag Rate,").append(String.format("%.2f", recentFlagRate)).append(",percentage\n");
                }
                
                return csvFile;
                
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to export monitoring data to CSV: " + e.getMessage());
                return null;
            }
        });
    }
    
    public CompletableFuture<File> exportWorldConfiguration(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File exportDir = new File(plugin.getDataFolder(), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                
                String filename = "world_config_" + worldName + "_" + dateFormat.format(new Date()) + ".txt";
                File configFile = new File(exportDir, filename);
                
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.append("SmartChat World Configuration Export\n");
                    writer.append("===============================================\n");
                    writer.append("World: ").append(worldName).append("\n");
                    writer.append("Export Date: ").append(readableDateFormat.format(new Date())).append("\n");
                    writer.append("Plugin Version: ").append(plugin.getDescription().getVersion()).append("\n\n");
                    
                    
                    writer.append("API Configuration:\n");
                    writer.append("- API Key Configured: ").append(String.valueOf(plugin.getConfigManager().getApiKey() != null && !plugin.getConfigManager().getApiKey().isEmpty())).append("\n\n");
                    
                    writer.append("Action Thresholds:\n");
                    writer.append("- Toxicity Threshold: ").append(String.valueOf(plugin.getConfigManager().getThreshold("toxicity"))).append("%\n");
                    writer.append("- Harassment Threshold: ").append(String.valueOf(plugin.getConfigManager().getThreshold("harassment"))).append("%\n");
                    writer.append("- Profanity Threshold: ").append(String.valueOf(plugin.getConfigManager().getThreshold("profanity"))).append("%\n");
                    writer.append("- Spam Threshold: ").append(String.valueOf(plugin.getConfigManager().getThreshold("spam"))).append("%\n");
                }
                
                return configFile;
                
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to export world configuration: " + e.getMessage());
                return null;
            }
        });
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    public void notifyExportComplete(Player player, File exportFile, String dataType) {
        if (exportFile != null) {
            player.sendMessage("§a✓ " + dataType + " exported successfully!");
            player.sendMessage("§7File: §b" + exportFile.getName());
            player.sendMessage("§7Location: §e" + exportFile.getAbsolutePath());
        } else {
            player.sendMessage("§c✗ Failed to export " + dataType + ". Check console for errors.");
        }
    }
}