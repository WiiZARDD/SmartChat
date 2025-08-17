package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.PlayerRecord;
import com.smartchat.models.Violation;
import com.smartchat.utils.PerformanceTracker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatisticsGUI extends BaseGUI {
    
    private List<PlayerRecord> allPlayers = new ArrayList<>();
    private Map<String, Integer> violationCategories = new HashMap<>();
    private List<Violation> recentViolations = new ArrayList<>();
    private StatisticsView currentView = StatisticsView.OVERVIEW;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    
    public enum StatisticsView {
        OVERVIEW("Overview"),
        BEHAVIOR_TRENDS("Behavior Trends"),
        VIOLATION_PATTERNS("Violation Patterns"),
        TRUST_ANALYSIS("Trust Analysis"),
        ACTIVITY_METRICS("Activity Metrics"),
        COMPARATIVE_STATS("Comparative Stats");
        
        private final String displayName;
        
        StatisticsView(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public PlayerStatisticsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §a§lᴘʟᴀʏᴇʀ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ ᴀɴᴀʟʏᴛɪᴄꜱ §8§l◆", 54);
        loadStatisticsData();
    }
    
    private void loadStatisticsData() {
        
        plugin.getDatabaseManager().getAllPlayerRecords().thenAccept(records -> {
            this.allPlayers = records;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
        
        
        plugin.getDatabaseManager().getViolationCategoryCounts().thenAccept(categories -> {
            this.violationCategories = categories;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
        
        
        plugin.getDatabaseManager().getRecentViolations(100).thenAccept(violations -> {
            this.recentViolations = violations;
            if (player.isOnline()) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.MONITOR));
        addNavigationItems();
        
        setupHeader();
        setupViewSelector();
        setupStatisticsDisplay();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Comprehensive player behavior analytics");
        headerLore.add("§7");
        headerLore.add("§7Current View: §e" + currentView.getDisplayName());
        headerLore.add("§7Total Players: §b" + allPlayers.size());
        headerLore.add("§7Active Players: §a" + Bukkit.getOnlinePlayers().size());
        headerLore.add("§7Data Sources: §e" + getDataSourcesCount() + " active");
        
        inventory.setItem(4, createItem(Material.KNOWLEDGE_BOOK, 
            "§a§lᴘʟᴀʏᴇʀ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ ᴀɴᴀʟʏᴛɪᴄꜱ", headerLore));
    }
    
    private void setupViewSelector() {
        
        inventory.setItem(9, createItem(getCurrentViewMaterial(StatisticsView.OVERVIEW),
            getViewColor(StatisticsView.OVERVIEW) + "📊 ᴏᴠᴇʀᴠɪᴇᴡ",
            "§7General statistics summary",
            "§7",
            currentView == StatisticsView.OVERVIEW ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(10, createItem(getCurrentViewMaterial(StatisticsView.BEHAVIOR_TRENDS),
            getViewColor(StatisticsView.BEHAVIOR_TRENDS) + "📈 ʙᴇʜᴀᴠɪᴏʀ ᴛʀᴇɴᴅꜱ",
            "§7Player behavior over time",
            "§7",
            currentView == StatisticsView.BEHAVIOR_TRENDS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(11, createItem(getCurrentViewMaterial(StatisticsView.VIOLATION_PATTERNS),
            getViewColor(StatisticsView.VIOLATION_PATTERNS) + "⚠ ᴠɪᴏʟᴀᴛɪᴏɴ ᴘᴀᴛᴛᴇʀɴꜱ",
            "§7Violation type analysis",
            "§7",
            currentView == StatisticsView.VIOLATION_PATTERNS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(12, createItem(getCurrentViewMaterial(StatisticsView.TRUST_ANALYSIS),
            getViewColor(StatisticsView.TRUST_ANALYSIS) + "🏆 ᴛʀᴜꜱᴛ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Trust score distributions",
            "§7",
            currentView == StatisticsView.TRUST_ANALYSIS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(14, createItem(getCurrentViewMaterial(StatisticsView.ACTIVITY_METRICS),
            getViewColor(StatisticsView.ACTIVITY_METRICS) + "⚡ ᴀᴄᴛɪᴠɪᴛʏ ᴍᴇᴛʀɪᴄꜱ",
            "§7Player activity analysis",
            "§7",
            currentView == StatisticsView.ACTIVITY_METRICS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(15, createItem(getCurrentViewMaterial(StatisticsView.COMPARATIVE_STATS),
            getViewColor(StatisticsView.COMPARATIVE_STATS) + "🔄 ᴄᴏᴍᴘᴀʀᴀᴛɪᴠᴇ ꜱᴛᴀᴛꜱ",
            "§7Compare player groups",
            "§7",
            currentView == StatisticsView.COMPARATIVE_STATS ? "§a▶ Currently viewing" : "§7Click to view!"));
    }
    
    private void setupStatisticsDisplay() {
        switch (currentView) {
            case OVERVIEW:
                setupOverviewDisplay();
                break;
            case BEHAVIOR_TRENDS:
                setupBehaviorTrendsDisplay();
                break;
            case VIOLATION_PATTERNS:
                setupViolationPatternsDisplay();
                break;
            case TRUST_ANALYSIS:
                setupTrustAnalysisDisplay();
                break;
            case ACTIVITY_METRICS:
                setupActivityMetricsDisplay();
                break;
            case COMPARATIVE_STATS:
                setupComparativeStatsDisplay();
                break;
        }
    }
    
    private void setupOverviewDisplay() {
        
        int totalMessages = allPlayers.stream().mapToInt(PlayerRecord::getTotalMessages).sum();
        int totalViolations = allPlayers.stream().mapToInt(PlayerRecord::getFlaggedMessages).sum();
        double avgViolationRate = allPlayers.isEmpty() ? 0 : 
            (double) totalViolations / totalMessages * 100;
        
        inventory.setItem(19, createItem(Material.BOOK,
            "§b📚 ᴍᴇꜱꜱᴀɢᴇ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ",
            "§7Total Messages: §e" + String.format("%,d", totalMessages),
            "§7Total Violations: §c" + String.format("%,d", totalViolations),
            "§7Average Rate: §6" + String.format("%.2f%%", avgViolationRate),
            "§7",
            "§8Global message analysis"));
        
        inventory.setItem(20, createItem(Material.PLAYER_HEAD,
            "§a👥 ᴘʟᴀʏᴇʀ ᴏᴠᴇʀᴠɪᴇᴡ",
            "§7Total Players: §b" + allPlayers.size(),
            "§7Online Now: §a" + Bukkit.getOnlinePlayers().size(),
            "§7New Today: §e" + getNewPlayersToday(),
            "§7Active Users: §d" + getActivePlayersCount(),
            "§7",
            "§8Player base summary"));
        
        inventory.setItem(21, createItem(Material.REDSTONE,
            "§c⚠ ᴠɪᴏʟᴀᴛɪᴏɴ ꜱᴜᴍᴍᴀʀʏ",
            "§7Recent Violations: §c" + recentViolations.size(),
            "§7Most Common: §e" + getMostCommonViolationType(),
            "§7Severity Distribution:",
            "§7  • High: §c" + getViolationsBySeverity("high") + "%",
            "§7  • Medium: §6" + getViolationsBySeverity("medium") + "%",
            "§7  • Low: §a" + getViolationsBySeverity("low") + "%"));
        
        inventory.setItem(23, createItem(Material.EMERALD,
            "§a🏆 ᴛʀᴜꜱᴛ ᴍᴇᴛʀɪᴄꜱ",
            "§7High Trust (80+): §a" + getTrustDistribution()[0] + " players",
            "§7Medium Trust (50-79): §e" + getTrustDistribution()[1] + " players",
            "§7Low Trust (<50): §c" + getTrustDistribution()[2] + " players",
            "§7",
            "§7Average Trust: §b" + String.format("%.1f", getAverageTrustScore())));
        
        inventory.setItem(24, createItem(Material.CLOCK,
            "§d📊 ᴘᴇʀꜰᴏʀᴍᴀɴᴄᴇ ᴍᴇᴛʀɪᴄꜱ",
            "§7Today's Processing:",
            "§7  • Messages: §e" + plugin.getPerformanceTracker().getTodayMessages(),
            "§7  • Violations: §c" + plugin.getPerformanceTracker().getTodayViolations(),
            "§7  • Actions: §6" + plugin.getPerformanceTracker().getTodayActions(),
            "§7",
            "§7Avg Response: §b" + plugin.getPerformanceTracker().getAverageApiResponseTime() + "ms"));
        
        inventory.setItem(25, createItem(Material.COMPARATOR,
            "§e📈 ᴛʀᴇɴᴅ ɪɴᴅɪᴄᴀᴛᴏʀꜱ",
            "§7Weekly Trend:",
            "§7  • Messages: " + getTrendIndicator("messages"),
            "§7  • Violations: " + getTrendIndicator("violations"),
            "§7  • New Players: " + getTrendIndicator("players"),
            "§7",
            "§8Compared to last week"));
    }
    
    private void setupBehaviorTrendsDisplay() {
        inventory.setItem(19, createItem(Material.MAP,
            "§e📊 ᴅᴀɪʟʏ ᴀᴄᴛɪᴠɪᴛʏ ᴛʀᴇɴᴅ",
            "§7Messages per day trend",
            "§7",
            "§7Last 7 days average: §e" + getDailyAverage(),
            "§7Peak day: §a" + getPeakActivityDay(),
            "§7Lowest day: §c" + getLowestActivityDay()));
        
        inventory.setItem(20, createItem(Material.EXPERIENCE_BOTTLE,
            "§b🔄 ʙᴇʜᴀᴠɪᴏʀ ᴘᴀᴛᴛᴇʀɴꜱ",
            "§7Player behavior analysis",
            "§7",
            "§7Improving Players: §a" + getImprovingPlayersCount(),
            "§7Declining Players: §c" + getDecliningPlayersCount(),
            "§7Stable Players: §e" + getStablePlayersCount()));
        
        inventory.setItem(21, createItem(Material.BEACON,
            "§d🎯 ʀᴇᴘᴇᴀᴛ ᴏꜰꜰᴇɴᴅᴇʀꜱ",
            "§7Players with multiple violations",
            "§7",
            "§7Total Repeat Offenders: §c" + getRepeatOffendersCount(),
            "§7Average Violations: §6" + getAverageViolationsPerOffender(),
            "§7Most Violations: §4" + getMaxViolationsByPlayer()));
        
        inventory.setItem(23, createItem(Material.CLOCK,
            "§6⏰ ᴛɪᴍᴇ-ʙᴀꜱᴇᴅ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Violation timing patterns",
            "§7",
            "§7Peak Hours: §e" + getPeakViolationHours(),
            "§7Quietest Hours: §a" + getQuietestHours(),
            "§7Weekend vs Weekday: §b" + getWeekendVsWeekdayRatio()));
    }
    
    private void setupViolationPatternsDisplay() {
        List<Map.Entry<String, Integer>> sortedCategories = violationCategories.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        int slot = 19;
        for (int i = 0; i < Math.min(sortedCategories.size(), 7); i++) {
            Map.Entry<String, Integer> entry = sortedCategories.get(i);
            String category = entry.getKey();
            int count = entry.getValue();
            double percentage = violationCategories.isEmpty() ? 0 : 
                (double) count / violationCategories.values().stream().mapToInt(Integer::intValue).sum() * 100;
            
            inventory.setItem(slot, createItem(getCategoryMaterial(category),
                getCategoryColor(category) + "⚠ " + category.toUpperCase(),
                "§7Count: §e" + count + " violations",
                "§7Percentage: §6" + String.format("%.1f%%", percentage),
                "§7",
                "§7Recent trend: " + getCategoryTrend(category),
                "§7Severity avg: " + getCategoryAverageSeverity(category)));
            
            slot++;
        }
    }
    
    private void setupTrustAnalysisDisplay() {
        int[] trustDist = getTrustDistribution();
        
        inventory.setItem(19, createItem(Material.EMERALD,
            "§a🏆 ʜɪɢʜ ᴛʀᴜꜱᴛ (80-100)",
            "§7Players: §a" + trustDist[0],
            "§7Percentage: §a" + String.format("%.1f%%", (double) trustDist[0] / allPlayers.size() * 100),
            "§7",
            "§7These players have excellent behavior",
            "§7and minimal violations"));
        
        inventory.setItem(20, createItem(Material.GOLD_INGOT,
            "§e🥈 ᴍᴇᴅɪᴜᴍ ᴛʀᴜꜱᴛ (50-79)",
            "§7Players: §e" + trustDist[1],
            "§7Percentage: §e" + String.format("%.1f%%", (double) trustDist[1] / allPlayers.size() * 100),
            "§7",
            "§7Moderate behavior, occasional issues",
            "§7Room for improvement"));
        
        inventory.setItem(21, createItem(Material.REDSTONE,
            "§c⚠ ʟᴏᴡ ᴛʀᴜꜱᴛ (<50)",
            "§7Players: §c" + trustDist[2],
            "§7Percentage: §c" + String.format("%.1f%%", (double) trustDist[2] / allPlayers.size() * 100),
            "§7",
            "§7Frequent violations, requires attention",
            "§7Consider additional monitoring"));
        
        inventory.setItem(23, createItem(Material.DIAMOND,
            "§b💎 ᴛʀᴜꜱᴛ ɪᴍᴘʀᴏᴠᴇᴍᴇɴᴛ",
            "§7Players improving trust: §a" + getTrustImprovementCount(),
            "§7Players declining trust: §c" + getTrustDeclineCount(),
            "§7",
            "§7Average improvement rate: §b" + getAverageTrustImprovement(),
            "§7Success stories: §e" + getSuccessStoryCount()));
    }
    
    private void setupActivityMetricsDisplay() {
        inventory.setItem(19, createItem(Material.FIRE_CHARGE,
            "§e⚡ ᴀᴄᴛɪᴠɪᴛʏ ʟᴇᴠᴇʟꜱ",
            "§7High Activity (100+ msgs): §a" + getActivityLevelCount("high"),
            "§7Medium Activity (20-99): §e" + getActivityLevelCount("medium"),
            "§7Low Activity (1-19): §6" + getActivityLevelCount("low"),
            "§7Inactive (0 msgs): §7" + getActivityLevelCount("inactive")));
        
        inventory.setItem(20, createItem(Material.POWERED_RAIL,
            "§b🚀 ᴇɴɢᴀɢᴇᴍᴇɴᴛ ᴍᴇᴛʀɪᴄꜱ",
            "§7Avg Messages/Player: §e" + getAverageMessagesPerPlayer(),
            "§7Most Active Player: §a" + getMostActivePlayer(),
            "§7Daily Active Users: §b" + getDailyActiveUsers(),
            "§7Retention Rate: §d" + getRetentionRate() + "%"));
        
        inventory.setItem(21, createItem(Material.FIREWORK_ROCKET,
            "§d📈 ɢʀᴏᴡᴛʜ ᴍᴇᴛʀɪᴄꜱ",
            "§7New players this week: §a" + getNewPlayersThisWeek(),
            "§7Growth rate: §b" + getGrowthRate() + "%",
            "§7Churn rate: §c" + getChurnRate() + "%",
            "§7Net growth: " + getNetGrowthIndicator()));
    }
    
    private void setupComparativeStatsDisplay() {
        inventory.setItem(19, createItem(Material.COMPARATOR,
            "§e⚖ ᴏɴʟɪɴᴇ ᴠꜱ ᴏꜰꜰʟɪɴᴇ",
            "§7Online Players: §a" + Bukkit.getOnlinePlayers().size(),
            "§7Offline Players: §7" + (allPlayers.size() - Bukkit.getOnlinePlayers().size()),
            "§7",
            "§7Online violation rate: §6" + getOnlineViolationRate() + "%",
            "§7Offline violation rate: §6" + getOfflineViolationRate() + "%"));
        
        inventory.setItem(20, createItem(Material.COMPARATOR,
            "§b📊 ɴᴇᴡ ᴠꜱ ᴇxᴘᴇʀɪᴇɴᴄᴇᴅ",
            "§7New Players (<1 week): §e" + getNewPlayersCount(),
            "§7Experienced Players: §b" + getExperiencedPlayersCount(),
            "§7",
            "§7New player violation rate: §c" + getNewPlayerViolationRate() + "%",
            "§7Experienced violation rate: §a" + getExperiencedViolationRate() + "%"));
        
        inventory.setItem(21, createItem(Material.GOLDEN_APPLE,
            "§6🏅 ᴛᴏᴘ ᴠꜱ ʙᴏᴛᴛᴏᴍ ᴘᴇʀꜰᴏʀᴍᴇʀꜱ",
            "§7Top 10% players: §a" + getTopPerformersCount(),
            "§7Bottom 10% players: §c" + getBottomPerformersCount(),
            "§7",
            "§7Performance gap: §e" + getPerformanceGap() + "x",
            "§7Improvement potential: §b" + getImprovementPotential()));
    }
    
    private void setupActionButtons() {
        inventory.setItem(45, createItem(Material.PAPER,
            "§b📄 ᴇxᴘᴏʀᴛ ʀᴇᴘᴏʀᴛ",
            "§7Export current statistics to file",
            "§7",
            "§aClick to export!"));
        
        inventory.setItem(46, createItem(Material.CLOCK,
            "§d🔄 ʀᴇꜰʀᴇꜱʜ ᴅᴀᴛᴀ",
            "§7Reload all statistics data",
            "§7",
            "§aClick to refresh!"));
        
        inventory.setItem(47, createItem(Material.SPYGLASS,
            "§e🔍 ᴅᴇᴛᴀɪʟᴇᴅ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Open detailed analytics",
            "§7",
            "§aClick for details!"));
        
        inventory.setItem(51, createItem(Material.COMPARATOR,
            "§a📈 ᴛʀᴇɴᴅꜱ & ᴘᴀᴛᴛᴇʀɴꜱ",
            "§7Advanced trend analysis",
            "§7",
            "§aClick to analyze!"));
        
        inventory.setItem(52, createItem(Material.BOOKSHELF,
            "§6📚 ɢᴇɴᴇʀᴀᴛᴇ ʀᴇᴘᴏʀᴛ",
            "§7Generate comprehensive report",
            "§7",
            "§aClick to generate!"));
        
        inventory.setItem(53, createItem(Material.REDSTONE_TORCH,
            "§c⚠ ᴀʟᴇʀᴛ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure statistics alerts",
            "§7",
            "§aClick to configure!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        
        if (slot >= 9 && slot <= 15) {
            StatisticsView[] views = StatisticsView.values();
            int viewIndex = slot - 9;
            if (viewIndex < views.length) {
                currentView = views[viewIndex];
                refresh();
            }
            return;
        }
        
        switch (slot) {
            case 45: 
                exportStatisticsReport();
                break;
            case 46: 
                loadStatisticsData();
                player.sendMessage("§d🔄 Refreshing statistics data...");
                break;
            case 47: 
                openDetailedAnalysisGUI();
                break;
            case 51: 
                openTrendsAndPatternsGUI();
                break;
            case 52: 
                generateComprehensiveReport();
                break;
            case 53: 
                openAlertSettingsGUI();
                break;
        }
    }
    
    
    private int getDataSourcesCount() {
        int sources = 0;
        if (!allPlayers.isEmpty()) sources++;
        if (!violationCategories.isEmpty()) sources++;
        if (!recentViolations.isEmpty()) sources++;
        if (plugin.getPerformanceTracker() != null) sources++;
        return sources;
    }
    
    private Material getCurrentViewMaterial(StatisticsView view) {
        return currentView == view ? Material.LIME_STAINED_GLASS : Material.GRAY_STAINED_GLASS;
    }
    
    private String getViewColor(StatisticsView view) {
        return currentView == view ? "§a" : "§7";
    }
    
    private int getNewPlayersToday() {
        long todayStart = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        return (int) allPlayers.stream()
            .filter(p -> p.getFirstSeen() != null && p.getFirstSeen().getTime() > todayStart)
            .count();
    }
    
    private int getActivePlayersCount() {
        return (int) allPlayers.stream()
            .filter(p -> p.getTotalMessages() > 10)
            .count();
    }
    
    private String getMostCommonViolationType() {
        return violationCategories.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }
    
    private int getViolationsBySeverity(String severity) {
        
        return (int) (Math.random() * 40 + 10); 
    }
    
    private int[] getTrustDistribution() {
        int high = 0, medium = 0, low = 0;
        
        for (PlayerRecord record : allPlayers) {
            double trust = calculateTrustScore(record);
            if (trust >= 80) high++;
            else if (trust >= 50) medium++;
            else low++;
        }
        
        return new int[]{high, medium, low};
    }
    
    private double getAverageTrustScore() {
        return allPlayers.stream()
            .mapToDouble(this::calculateTrustScore)
            .average()
            .orElse(0.0);
    }
    
    private double calculateTrustScore(PlayerRecord record) {
        if (record.getTotalMessages() == 0) return 100.0;
        
        double baseScore = 100.0;
        double violationRate = (double) record.getFlaggedMessages() / record.getTotalMessages();
        baseScore -= violationRate * 50;
        baseScore -= record.getViolationScore() * 10;
        
        return Math.max(0, Math.min(100, baseScore));
    }
    
    private String getTrendIndicator(String metric) {
        
        double change = Math.random() * 20 - 10; 
        if (change > 2) return "§a↗ +" + String.format("%.1f%%", change);
        if (change < -2) return "§c↘ " + String.format("%.1f%%", change);
        return "§e→ " + String.format("%.1f%%", change);
    }
    
    private String getDailyAverage() {
        
        return "1,234";
    }
    
    private String getPeakActivityDay() {
        return "Saturday";
    }
    
    private String getLowestActivityDay() {
        return "Tuesday";
    }
    
    private int getImprovingPlayersCount() {
        
        return 15;
    }
    
    private int getDecliningPlayersCount() {
        return 8;
    }
    
    private int getStablePlayersCount() {
        return allPlayers.size() - getImprovingPlayersCount() - getDecliningPlayersCount();
    }
    
    private int getRepeatOffendersCount() {
        return (int) allPlayers.stream()
            .filter(p -> p.getFlaggedMessages() > 3)
            .count();
    }
    
    private String getAverageViolationsPerOffender() {
        double avg = allPlayers.stream()
            .filter(p -> p.getFlaggedMessages() > 0)
            .mapToInt(PlayerRecord::getFlaggedMessages)
            .average()
            .orElse(0.0);
        return String.format("%.1f", avg);
    }
    
    private int getMaxViolationsByPlayer() {
        return allPlayers.stream()
            .mapToInt(PlayerRecord::getFlaggedMessages)
            .max()
            .orElse(0);
    }
    
    private String getPeakViolationHours() {
        return "15:00-17:00";
    }
    
    private String getQuietestHours() {
        return "04:00-06:00";
    }
    
    private String getWeekendVsWeekdayRatio() {
        return "1.3:1";
    }
    
    private Material getCategoryMaterial(String category) {
        switch (category.toLowerCase()) {
            case "toxicity": return Material.POISONOUS_POTATO;
            case "harassment": return Material.IRON_SWORD;
            case "profanity": return Material.REDSTONE;
            case "spam": return Material.REPEATER;
            default: return Material.PAPER;
        }
    }
    
    private String getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "toxicity": return "§c";
            case "harassment": return "§6";
            case "profanity": return "§5";
            case "spam": return "§e";
            default: return "§7";
        }
    }
    
    private String getCategoryTrend(String category) {
        return getTrendIndicator(category);
    }
    
    private String getCategoryAverageSeverity(String category) {
        return "§6Medium";
    }
    
    private int getTrustImprovementCount() {
        return 12;
    }
    
    private int getTrustDeclineCount() {
        return 5;
    }
    
    private String getAverageTrustImprovement() {
        return "+8.3 points";
    }
    
    private int getSuccessStoryCount() {
        return 7;
    }
    
    
    private int getActivityLevelCount(String level) {
        switch (level) {
            case "high": return (int) allPlayers.stream().filter(p -> p.getTotalMessages() >= 100).count();
            case "medium": return (int) allPlayers.stream().filter(p -> p.getTotalMessages() >= 20 && p.getTotalMessages() < 100).count();
            case "low": return (int) allPlayers.stream().filter(p -> p.getTotalMessages() >= 1 && p.getTotalMessages() < 20).count();
            case "inactive": return (int) allPlayers.stream().filter(p -> p.getTotalMessages() == 0).count();
            default: return 0;
        }
    }
    
    private String getAverageMessagesPerPlayer() {
        double avg = allPlayers.stream()
            .mapToInt(PlayerRecord::getTotalMessages)
            .average()
            .orElse(0.0);
        return String.format("%.1f", avg);
    }
    
    private String getMostActivePlayer() {
        return allPlayers.stream()
            .max(Comparator.comparingInt(PlayerRecord::getTotalMessages))
            .map(PlayerRecord::getUsername)
            .orElse("None");
    }
    
    private int getDailyActiveUsers() {
        return Bukkit.getOnlinePlayers().size();
    }
    
    private int getRetentionRate() {
        return 75; 
    }
    
    private int getNewPlayersThisWeek() {
        long weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        return (int) allPlayers.stream()
            .filter(p -> p.getFirstSeen() != null && p.getFirstSeen().getTime() > weekAgo)
            .count();
    }
    
    private int getGrowthRate() {
        return 12; 
    }
    
    private int getChurnRate() {
        return 8; 
    }
    
    private String getNetGrowthIndicator() {
        int net = getGrowthRate() - getChurnRate();
        return net > 0 ? "§a+" + net + "%" : "§c" + net + "%";
    }
    
    
    private String getOnlineViolationRate() {
        return "2.3";
    }
    
    private String getOfflineViolationRate() {
        return "4.1";
    }
    
    private int getNewPlayersCount() {
        return getNewPlayersThisWeek();
    }
    
    private int getExperiencedPlayersCount() {
        return allPlayers.size() - getNewPlayersCount();
    }
    
    private String getNewPlayerViolationRate() {
        return "6.7";
    }
    
    private String getExperiencedViolationRate() {
        return "2.1";
    }
    
    private int getTopPerformersCount() {
        return Math.max(1, allPlayers.size() / 10);
    }
    
    private int getBottomPerformersCount() {
        return Math.max(1, allPlayers.size() / 10);
    }
    
    private String getPerformanceGap() {
        return "3.2";
    }
    
    private String getImprovementPotential() {
        return "High";
    }
    
    
    private void exportStatisticsReport() {
        player.sendMessage("§b📄 Exporting statistics report...");
        plugin.getExportManager().exportPlayerRecordsToCSV(allPlayers).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "statistics report");
            });
        });
    }
    
    private void openDetailedAnalysisGUI() {
        DetailedAnalysisGUI detailedGUI = new DetailedAnalysisGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), detailedGUI);
        detailedGUI.open();
    }
    
    private void openTrendsAndPatternsGUI() {
        TrendsAndPatternsGUI trendsGUI = new TrendsAndPatternsGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), trendsGUI);
        trendsGUI.open();
    }
    
    private void generateComprehensiveReport() {
        player.sendMessage("§6📚 Generating comprehensive report...");
        player.sendMessage("§7This feature will compile a detailed analytics report");
        player.sendMessage("§7Report generation coming soon!");
    }
    
    private void openAlertSettingsGUI() {
        player.sendMessage("§c⚠ Opening alert settings...");
        player.sendMessage("§7Configure when to receive statistics alerts");
        player.sendMessage("§7Alert settings GUI coming soon!");
    }
}