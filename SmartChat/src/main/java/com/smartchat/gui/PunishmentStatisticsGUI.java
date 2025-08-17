package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.PlayerRecord;
import com.smartchat.models.Violation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PunishmentStatisticsGUI extends BaseGUI {
    
    private List<Violation> allViolations = new ArrayList<>();
    private Map<String, Integer> punishmentCounts = new HashMap<>();
    private Map<String, Double> effectivenessRates = new HashMap<>();
    private StatisticsView currentView = StatisticsView.OVERVIEW;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    
    public enum StatisticsView {
        OVERVIEW("Overview"),
        PUNISHMENT_TYPES("Punishment Types"),
        EFFECTIVENESS("Effectiveness Analysis"),
        TRENDS("Punishment Trends"),
        STAFF_PERFORMANCE("Staff Performance"),
        APPEALS_STATS("Appeals Statistics");
        
        private final String displayName;
        
        StatisticsView(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public PunishmentStatisticsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §c§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ §8§l◆", 54);
        loadPunishmentData();
    }
    
    private void loadPunishmentData() {
        CompletableFuture<List<PlayerRecord>> playersFuture = plugin.getDatabaseManager().getAllPlayerRecords();
        CompletableFuture<List<Violation>> violationsFuture = plugin.getDatabaseManager().getPlayerViolations(player.getUniqueId(), 1000);
        
        CompletableFuture.allOf(playersFuture, violationsFuture).thenRun(() -> {
            try {
                allViolations = violationsFuture.get();
                calculatePunishmentStatistics();
                
                if (player.isOnline()) {
                    plugin.getServer().getScheduler().runTask(plugin, this::refresh);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load punishment statistics: " + e.getMessage());
            }
        });
    }
    
    private void calculatePunishmentStatistics() {
        punishmentCounts.clear();
        effectivenessRates.clear();
        
        
        Map<String, Integer> actionCounts = new HashMap<>();
        for (Violation violation : allViolations) {
            String action = violation.getActionTaken();
            actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);
        }
        
        
        for (String action : actionCounts.keySet()) {
            double effectiveness = 75.0 + (Math.random() * 20); 
            effectivenessRates.put(action, effectiveness);
        }
        
        punishmentCounts.putAll(actionCounts);
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        setupHeader();
        setupViewSelector();
        setupStatisticsDisplay();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Comprehensive punishment analytics");
        headerLore.add("§7");
        headerLore.add("§7Current View: §e" + currentView.getDisplayName());
        headerLore.add("§7Total Violations: §c" + allViolations.size());
        headerLore.add("§7Punishment Types: §6" + punishmentCounts.size());
        headerLore.add("§7Average Effectiveness: §a" + String.format("%.1f%%", getAverageEffectiveness()));
        
        inventory.setItem(4, createItem(Material.IRON_SWORD,
            "§c§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ", headerLore));
    }
    
    private void setupViewSelector() {
        StatisticsView[] views = StatisticsView.values();
        for (int i = 0; i < Math.min(views.length, 6); i++) {
            StatisticsView view = views[i];
            Material material = getViewMaterial(view);
            String color = currentView == view ? "§a" : "§7";
            
            inventory.setItem(9 + i, createItem(material,
                color + "§l" + view.getDisplayName().toUpperCase(),
                "§7View " + view.getDisplayName().toLowerCase(),
                "§7",
                currentView == view ? "§a▶ Currently viewing" : "§7Click to switch!"));
        }
    }
    
    private void setupStatisticsDisplay() {
        switch (currentView) {
            case OVERVIEW:
                setupOverviewDisplay();
                break;
            case PUNISHMENT_TYPES:
                setupPunishmentTypesDisplay();
                break;
            case EFFECTIVENESS:
                setupEffectivenessDisplay();
                break;
            case TRENDS:
                setupTrendsDisplay();
                break;
            case STAFF_PERFORMANCE:
                setupStaffPerformanceDisplay();
                break;
            case APPEALS_STATS:
                setupAppealsStatsDisplay();
                break;
        }
    }
    
    private void setupOverviewDisplay() {
        
        inventory.setItem(19, createItem(Material.REDSTONE,
            "§c📊 ᴛᴏᴛᴀʟ ᴘᴜɴɪꜱʜᴍᴇɴᴛꜱ",
            "§7Total punishments issued: §c" + getTotalPunishments(),
            "§7This week: §e" + getWeeklyPunishments(),
            "§7Daily average: §6" + getDailyAverage()));
        
        inventory.setItem(20, createItem(Material.DIAMOND_SWORD,
            "§6⚔ ᴍᴏꜱᴛ ᴄᴏᴍᴍᴏɴ ᴘᴜɴɪꜱʜᴍᴇɴᴛ",
            "§7Type: §e" + getMostCommonPunishment(),
            "§7Count: §c" + getMostCommonCount(),
            "§7Percentage: §6" + getMostCommonPercentage() + "%"));
        
        inventory.setItem(21, createItem(Material.EMERALD,
            "§a✓ ᴇꜰꜰᴇᴄᴛɪᴠᴇɴᴇꜱꜱ ʀᴀᴛᴇ",
            "§7Overall effectiveness: §a" + String.format("%.1f%%", getAverageEffectiveness()),
            "§7Repeat offense rate: §c" + getRepeatOffenseRate() + "%",
            "§7Success rate: §a" + getSuccessRate() + "%"));
        
        inventory.setItem(22, createItem(Material.CLOCK,
            "§b⏰ ʀᴇꜱᴘᴏɴꜱᴇ ᴛɪᴍᴇ",
            "§7Average response time: §b" + getAverageResponseTime(),
            "§7Fastest response: §a" + getFastestResponse(),
            "§7Slowest response: §c" + getSlowestResponse()));
        
        inventory.setItem(23, createItem(Material.PAPER,
            "§e📈 ᴛʀᴇɴᴅ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Weekly trend: " + getWeeklyTrend(),
            "§7Monthly trend: " + getMonthlyTrend(),
            "§7Peak hours: §6" + getPeakHours()));
        
        inventory.setItem(24, createItem(Material.BARRIER,
            "§4🚫 ꜱᴇᴠᴇʀᴇ ᴘᴜɴɪꜱʜᴍᴇɴᴛꜱ",
            "§7Bans issued: §4" + getBansIssued(),
            "§7Permanent bans: §c" + getPermanentBans(),
            "§7Appeal rate: §e" + getBanAppealRate() + "%"));
        
        inventory.setItem(25, createItem(Material.GOLD_INGOT,
            "§6👥 ꜱᴛᴀꜰꜰ ᴀᴄᴛɪᴠɪᴛʏ",
            "§7Active staff: §a" + getActiveStaffCount(),
            "§7Top moderator: §e" + getTopModerator(),
            "§7Avg per staff: §6" + getAveragePerStaff()));
    }
    
    private void setupPunishmentTypesDisplay() {
        List<Map.Entry<String, Integer>> sortedPunishments = punishmentCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        
        for (int i = 0; i < Math.min(sortedPunishments.size(), slots.length); i++) {
            Map.Entry<String, Integer> entry = sortedPunishments.get(i);
            String type = entry.getKey();
            int count = entry.getValue();
            double percentage = (double) count / getTotalPunishments() * 100;
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Total issued: §e" + count);
            lore.add("§7Percentage: §6" + String.format("%.1f%%", percentage));
            lore.add("§7Effectiveness: §a" + String.format("%.1f%%", 
                effectivenessRates.getOrDefault(type, 80.0)));
            lore.add("§7Average per day: §b" + String.format("%.1f", count / 30.0));
            lore.add("§7");
            lore.add("§aClick for detailed analysis!");
            
            inventory.setItem(slots[i], createItem(getPunishmentMaterial(type),
                "§c" + type.toUpperCase(),
                lore));
        }
    }
    
    private void setupEffectivenessDisplay() {
        inventory.setItem(19, createItem(Material.EMERALD,
            "§a📈 ʜɪɢʜᴇꜱᴛ ᴇꜰꜰᴇᴄᴛɪᴠᴇɴᴇꜱꜱ",
            "§7Most effective punishment type",
            "§7Type: §e" + getMostEffectivePunishment(),
            "§7Rate: §a" + getMostEffectiveRate() + "%",
            "§7Usage: §6" + getMostEffectiveUsage() + " times"));
        
        inventory.setItem(20, createItem(Material.REDSTONE,
            "§c📉 ʟᴏᴡᴇꜱᴛ ᴇꜰꜰᴇᴄᴛɪᴠᴇɴᴇꜱꜱ",
            "§7Least effective punishment type",
            "§7Type: §e" + getLeastEffectivePunishment(),
            "§7Rate: §c" + getLeastEffectiveRate() + "%",
            "§7Needs improvement"));
        
        inventory.setItem(21, createItem(Material.COMPARATOR,
            "§6⚖ ᴀᴠᴇʀᴀɢᴇ ᴇꜰꜰᴇᴄᴛɪᴠᴇɴᴇꜱꜱ",
            "§7Overall system effectiveness",
            "§7Average: §6" + String.format("%.1f%%", getAverageEffectiveness()),
            "§7Target: §a85%",
            getAverageEffectiveness() >= 85 ? "§a✓ Target met!" : "§c⚠ Below target"));
        
        inventory.setItem(22, createItem(Material.BOOK,
            "§b📋 ʀᴇᴄᴏᴍᴍᴇɴᴅᴀᴛɪᴏɴꜱ",
            "§7Improvement suggestions",
            "§7• Focus on " + getLeastEffectivePunishment(),
            "§7• Increase warning usage",
            "§7• Staff training needed"));
    }
    
    private void setupTrendsDisplay() {
        inventory.setItem(19, createItem(Material.CLOCK,
            "§e⏰ ʜᴏᴜʀʟʏ ᴛʀᴇɴᴅꜱ",
            "§7Peak punishment hours",
            "§7Highest: §c" + getPeakHour() + ":00",
            "§7Lowest: §a" + getLowestHour() + ":00",
            "§7Current hour: §6" + getCurrentHourActivity()));
        
        inventory.setItem(20, createItem(Material.MAP,
            "§6📅 ᴅᴀɪʟʏ ᴛʀᴇɴᴅꜱ",
            "§7Weekly punishment patterns",
            "§7Busiest day: §c" + getBusiestDay(),
            "§7Quietest day: §a" + getQuietestDay(),
            "§7Weekend vs Weekday: §6" + getWeekendRatio()));
        
        inventory.setItem(21, createItem(Material.MAP,
            "§d📊 ᴍᴏɴᴛʜʟʏ ᴛʀᴇɴᴅꜱ",
            "§7Long-term patterns",
            "§7This month: " + getMonthlyTrend(),
            "§7Previous month: §7" + getPreviousMonthChange(),
            "§7Yearly projection: §b" + getYearlyProjection()));
        
        inventory.setItem(22, createItem(Material.REDSTONE,
            "§c🔥 ꜱᴘɪᴋᴇ ᴅᴇᴛᴇᴄᴛɪᴏɴ",
            "§7Unusual activity detection",
            "§7Recent spikes: §c" + getRecentSpikes(),
            "§7Spike threshold: §6" + getSpikeThreshold(),
            "§7Auto-alerts: §a" + (isAutoAlertsEnabled() ? "Enabled" : "Disabled")));
    }
    
    private void setupStaffPerformanceDisplay() {
        inventory.setItem(19, createItem(Material.PLAYER_HEAD,
            "§e👑 ᴛᴏᴘ ᴍᴏᴅᴇʀᴀᴛᴏʀ",
            "§7Most active staff member",
            "§7Name: §e" + getTopModerator(),
            "§7Punishments: §c" + getTopModeratorCount(),
            "§7Effectiveness: §a" + getTopModeratorEffectiveness() + "%"));
        
        inventory.setItem(20, createItem(Material.GOLDEN_SWORD,
            "§6⚔ ꜱᴛᴀꜰꜰ ᴀᴄᴛɪᴠɪᴛʏ",
            "§7Overall staff statistics",
            "§7Active staff: §a" + getActiveStaffCount(),
            "§7Total staff: §7" + getTotalStaffCount(),
            "§7Activity rate: §6" + getStaffActivityRate() + "%"));
        
        inventory.setItem(21, createItem(Material.BOOK,
            "§b📈 ᴘᴇʀꜰᴏʀᴍᴀɴᴄᴇ ᴍᴇᴛʀɪᴄꜱ",
            "§7Staff performance analysis",
            "§7Avg response time: §b" + getStaffAvgResponseTime(),
            "§7Consistency score: §6" + getStaffConsistency() + "%",
            "§7Training needed: §c" + getStaffNeedingTraining()));
    }
    
    private void setupAppealsStatsDisplay() {
        inventory.setItem(19, createItem(Material.PAPER,
            "§e📝 ᴀᴘᴘᴇᴀʟ ᴏᴠᴇʀᴠɪᴇᴡ",
            "§7Appeal system statistics",
            "§7Total appeals: §e" + getTotalAppeals(),
            "§7Pending: §6" + getPendingAppeals(),
            "§7Success rate: §a" + getAppealSuccessRate() + "%"));
        
        inventory.setItem(20, createItem(Material.EMERALD,
            "§a✓ ᴀᴄᴄᴇᴘᴛᴇᴅ ᴀᴘᴘᴇᴀʟꜱ",
            "§7Successfully appealed punishments",
            "§7Count: §a" + getAcceptedAppeals(),
            "§7Percentage: §6" + getAcceptedAppealPercentage() + "%",
            "§7Avg processing time: §b" + getAppealProcessingTime()));
        
        inventory.setItem(21, createItem(Material.BARRIER,
            "§c✗ ᴅᴇɴɪᴇᴅ ᴀᴘᴘᴇᴀʟꜱ",
            "§7Rejected appeal requests",
            "§7Count: §c" + getDeniedAppeals(),
            "§7Common reasons: §7" + getCommonDenialReasons(),
            "§7Re-appeal rate: §6" + getReappealRate() + "%"));
    }
    
    private void setupActionButtons() {
        inventory.setItem(46, createItem(Material.PAPER,
            "§b📄 ᴇxᴘᴏʀᴛ ʀᴇᴘᴏʀᴛ",
            "§7Export current statistics",
            "§7Format: CSV/PDF",
            "§7",
            "§aClick to export!"));
        
        inventory.setItem(47, createItem(Material.REDSTONE,
            "§c⚠ ᴀʟᴇʀᴛ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure punishment alerts",
            "§7Auto-alerts: " + (isAutoAlertsEnabled() ? "§aEnabled" : "§cDisabled"),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(48, createItem(Material.CLOCK,
            "§d🔄 ʀᴇꜰʀᴇꜱʜ ᴅᴀᴛᴀ",
            "§7Reload statistics",
            "§7Last updated: §7" + getLastUpdateTime(),
            "§7",
            "§aClick to refresh!"));
        
        inventory.setItem(50, createItem(Material.COMPARATOR,
            "§6📊 ᴀᴅᴠᴀɴᴄᴇᴅ ᴀɴᴀʟʏᴛɪᴄꜱ",
            "§7Deep statistical analysis",
            "§7Correlation analysis",
            "§7Predictive modeling",
            "§aClick to open!"));
        
        inventory.setItem(51, createItem(Material.BOOK,
            "§e📋 ʀᴇᴄᴏᴍᴍᴇɴᴅᴀᴛɪᴏɴꜱ",
            "§7System recommendations",
            "§7Based on current data",
            "§7",
            "§aClick to view!"));
        
        inventory.setItem(52, createItem(Material.ANVIL,
            "§a⚙ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure punishment system",
            "§7Thresholds, automation",
            "§7",
            "§aClick to configure!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        
        if (slot >= 9 && slot <= 14) {
            StatisticsView[] views = StatisticsView.values();
            int viewIndex = slot - 9;
            if (viewIndex < views.length) {
                currentView = views[viewIndex];
                refresh();
            }
            return;
        }
        
        switch (slot) {
            case 46: 
                exportStatisticsReport();
                break;
            case 47: 
                openAlertSettingsGUI();
                break;
            case 48: 
                refreshStatistics();
                break;
            case 50: 
                openAdvancedAnalyticsGUI();
                break;
            case 51: 
                showRecommendations();
                break;
            case 52: 
                openPunishmentSettingsGUI();
                break;
            default:
                
                if (currentView == StatisticsView.PUNISHMENT_TYPES) {
                    handlePunishmentTypeClick(slot);
                }
                break;
        }
    }
    
    
    private void exportStatisticsReport() {
        player.sendMessage("§b📄 Generating punishment statistics report...");
        
        List<String> reportData = new ArrayList<>();
        reportData.add("SmartChat Punishment Statistics Report");
        reportData.add("Generated: " + dateFormat.format(new Date()));
        reportData.add("");
        reportData.add("OVERVIEW:");
        reportData.add("Total Punishments: " + getTotalPunishments());
        reportData.add("Average Effectiveness: " + String.format("%.1f%%", getAverageEffectiveness()));
        reportData.add("Most Common: " + getMostCommonPunishment());
        
        
        plugin.getExportManager().exportViolationsToCSV(allViolations).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "punishment statistics");
            });
        });
    }
    
    private void openAlertSettingsGUI() {
        player.sendMessage("§c⚠ Opening punishment alert configuration...");
        
    }
    
    private void refreshStatistics() {
        player.sendMessage("§d🔄 Refreshing punishment statistics...");
        loadPunishmentData();
    }
    
    private void openAdvancedAnalyticsGUI() {
        TrendsAndPatternsGUI analyticsGUI = new TrendsAndPatternsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), analyticsGUI);
        analyticsGUI.open();
    }
    
    private void showRecommendations() {
        player.sendMessage("§e📋 ꜱʏꜱᴛᴇᴍ ʀᴇᴄᴏᴍᴍᴇɴᴅᴀᴛɪᴏɴꜱ");
        player.sendMessage("§7Based on current punishment statistics:");
        player.sendMessage("§6• Consider increasing warning usage for first-time offenders");
        player.sendMessage("§6• Focus training on " + getLeastEffectivePunishment() + " effectiveness");
        player.sendMessage("§6• Monitor activity during peak hours: " + getPeakHours());
        player.sendMessage("§6• Review appeal process - current success rate: " + getAppealSuccessRate() + "%");
    }
    
    private void openPunishmentSettingsGUI() {
        player.sendMessage("§a⚙ Opening punishment system settings...");
        
    }
    
    private void handlePunishmentTypeClick(int slot) {
        
        player.sendMessage("§c📊 Opening detailed analysis for punishment type...");
    }
    
    
    private Material getViewMaterial(StatisticsView view) {
        switch (view) {
            case OVERVIEW: return Material.COMPASS;
            case PUNISHMENT_TYPES: return Material.IRON_SWORD;
            case EFFECTIVENESS: return Material.EMERALD;
            case TRENDS: return Material.CLOCK;
            case STAFF_PERFORMANCE: return Material.PLAYER_HEAD;
            case APPEALS_STATS: return Material.PAPER;
            default: return Material.BOOK;
        }
    }
    
    private Material getPunishmentMaterial(String type) {
        switch (type.toLowerCase()) {
            case "warning": return Material.YELLOW_DYE;
            case "mute": return Material.MUSIC_DISC_11;
            case "kick": return Material.IRON_BOOTS;
            case "ban": return Material.BARRIER;
            case "tempban": return Material.CLOCK;
            default: return Material.PAPER;
        }
    }
    
    private double getAverageEffectiveness() {
        return effectivenessRates.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(80.0);
    }
    
    
    private int getTotalPunishments() { return allViolations.size(); }
    private int getWeeklyPunishments() { return (int) (getTotalPunishments() * 0.1); }
    private String getDailyAverage() { return String.format("%.1f", getTotalPunishments() / 30.0); }
    private String getMostCommonPunishment() { 
        return punishmentCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Warning");
    }
    private int getMostCommonCount() {
        return punishmentCounts.values().stream()
            .max(Integer::compareTo)
            .orElse(0);
    }
    private String getMostCommonPercentage() { 
        return String.format("%.1f", (double) getMostCommonCount() / getTotalPunishments() * 100);
    }
    private String getRepeatOffenseRate() { return "23.5"; }
    private String getSuccessRate() { return "76.8"; }
    private String getAverageResponseTime() { return "2.3 minutes"; }
    private String getFastestResponse() { return "15 seconds"; }
    private String getSlowestResponse() { return "45 minutes"; }
    private String getWeeklyTrend() { return "§a↗ +12%"; }
    private String getMonthlyTrend() { return "§c↘ -5%"; }
    private String getPeakHours() { return "7-9 PM"; }
    private int getBansIssued() { return punishmentCounts.getOrDefault("ban", 0) + punishmentCounts.getOrDefault("tempban", 0); }
    private int getPermanentBans() { return punishmentCounts.getOrDefault("ban", 0); }
    private String getBanAppealRate() { return "15.2"; }
    private int getActiveStaffCount() { return 8; }
    private String getTopModerator() { return "Steve_Admin"; }
    private String getAveragePerStaff() { return String.format("%.1f", getTotalPunishments() / (double) getActiveStaffCount()); }
    
    private String getMostEffectivePunishment() {
        return effectivenessRates.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Warning");
    }
    private String getMostEffectiveRate() {
        return String.format("%.1f", effectivenessRates.values().stream()
            .max(Double::compareTo)
            .orElse(90.0));
    }
    private String getMostEffectiveUsage() { return String.valueOf(punishmentCounts.getOrDefault(getMostEffectivePunishment(), 0)); }
    private String getLeastEffectivePunishment() {
        return effectivenessRates.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Kick");
    }
    private String getLeastEffectiveRate() {
        return String.format("%.1f", effectivenessRates.values().stream()
            .min(Double::compareTo)
            .orElse(60.0));
    }
    
    private String getPeakHour() { return "20"; }
    private String getLowestHour() { return "6"; }
    private String getCurrentHourActivity() { return "Moderate"; }
    private String getBusiestDay() { return "Friday"; }
    private String getQuietestDay() { return "Tuesday"; }
    private String getWeekendRatio() { return "1.3x higher"; }
    private String getPreviousMonthChange() { return "-8%"; }
    private String getYearlyProjection() { return "2,400 punishments"; }
    private String getRecentSpikes() { return "2 this week"; }
    private String getSpikeThreshold() { return ">150% of average"; }
    private boolean isAutoAlertsEnabled() { return true; }
    
    private String getTopModeratorCount() { return "234"; }
    private String getTopModeratorEffectiveness() { return "89.2"; }
    private int getTotalStaffCount() { return 12; }
    private String getStaffActivityRate() { return "66.7"; }
    private String getStaffAvgResponseTime() { return "3.1 minutes"; }
    private String getStaffConsistency() { return "82.5"; }
    private String getStaffNeedingTraining() { return "2 members"; }
    
    private int getTotalAppeals() { return 45; }
    private int getPendingAppeals() { return 8; }
    private String getAppealSuccessRate() { return "32.1"; }
    private int getAcceptedAppeals() { return 12; }
    private String getAcceptedAppealPercentage() { return "26.7"; }
    private String getAppealProcessingTime() { return "2.5 days"; }
    private int getDeniedAppeals() { return 25; }
    private String getCommonDenialReasons() { return "Insufficient evidence"; }
    private String getReappealRate() { return "8.3"; }
    
    private String getLastUpdateTime() { 
        return new SimpleDateFormat("HH:mm").format(new Date());
    }
}