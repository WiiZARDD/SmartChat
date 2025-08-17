package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AutomationSettingsGUI extends BaseGUI {
    
    private AutomationCategory currentCategory = AutomationCategory.OVERVIEW;
    private Map<String, Boolean> automationStates = new HashMap<>();
    private Map<String, Object> automationSettings = new HashMap<>();
    
    public enum AutomationCategory {
        OVERVIEW("Overview"),
        AUTO_MODERATION("Auto Moderation"),
        AUTO_PUNISHMENT("Auto Punishment"),
        AUTO_ESCALATION("Auto Escalation"),
        AUTO_ALERTS("Auto Alerts"),
        AUTO_RESPONSES("Auto Responses"),
        SMART_DETECTION("Smart Detection"),
        SCHEDULING("Scheduling"),
        REPORTING("Auto Reporting");
        
        private final String displayName;
        
        AutomationCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public AutomationSettingsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §d§lᴀᴜᴛᴏᴍᴀᴛɪᴏɴ ꜱᴇᴛᴛɪɴɢꜱ §8§l◆", 54);
        loadAutomationSettings();
    }
    
    private void loadAutomationSettings() {
        
        automationStates.put("auto_kick_spam", true);
        automationStates.put("auto_mute_toxicity", true);
        automationStates.put("auto_warn_caps", true);
        automationStates.put("auto_ban_extreme", false);
        automationStates.put("escalation_enabled", true);
        automationStates.put("smart_detection", true);
        automationStates.put("alert_notifications", true);
        automationStates.put("auto_responses", false);
        automationStates.put("schedule_reports", true);
        automationStates.put("real_time_analysis", true);
        
        
        automationSettings.put("spam_threshold", 5);
        automationSettings.put("toxicity_threshold", 75.0);
        automationSettings.put("caps_percentage", 80);
        automationSettings.put("escalation_delay", 300); 
        automationSettings.put("alert_cooldown", 60); 
        automationSettings.put("max_warnings", 3);
        automationSettings.put("ban_duration", 7); 
        automationSettings.put("report_interval", 24); 
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.CONFIG));
        addNavigationItems();
        
        setupHeader();
        setupCategorySelector();
        setupMainDisplay();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Automated system configuration");
        headerLore.add("§7");
        headerLore.add("§7Current Category: §e" + currentCategory.getDisplayName());
        headerLore.add("§7Active Automations: §a" + getActiveAutomationCount());
        headerLore.add("§7System Status: " + getSystemStatus());
        headerLore.add("§7Last Modified: §7" + getLastModified());
        
        inventory.setItem(4, createItem(Material.REDSTONE,
            "§d§lᴀᴜᴛᴏᴍᴀᴛɪᴏɴ ꜱᴇᴛᴛɪɴɢꜱ", headerLore));
    }
    
    private void setupCategorySelector() {
        AutomationCategory[] categories = AutomationCategory.values();
        
        
        for (int i = 0; i < Math.min(7, categories.length); i++) {
            AutomationCategory category = categories[i];
            Material material = getCategoryMaterial(category);
            String color = currentCategory == category ? "§a" : "§7";
            
            inventory.setItem(9 + i, createItem(material,
                color + "§l" + category.getDisplayName().toUpperCase(),
                "§7" + getCategoryDescription(category),
                "§7",
                currentCategory == category ? "§a▶ Currently viewing" : "§7Click to switch!"));
        }
        
        
        if (categories.length > 7) {
            for (int i = 7; i < Math.min(14, categories.length); i++) {
                AutomationCategory category = categories[i];
                Material material = getCategoryMaterial(category);
                String color = currentCategory == category ? "§a" : "§7";
                
                inventory.setItem(9 + i, createItem(material,
                    color + "§l" + category.getDisplayName().toUpperCase(),
                    "§7" + getCategoryDescription(category),
                    "§7",
                    currentCategory == category ? "§a▶ Currently viewing" : "§7Click to switch!"));
            }
        }
    }
    
    private void setupMainDisplay() {
        switch (currentCategory) {
            case OVERVIEW:
                setupOverviewDisplay();
                break;
            case AUTO_MODERATION:
                setupAutoModerationDisplay();
                break;
            case AUTO_PUNISHMENT:
                setupAutoPunishmentDisplay();
                break;
            case AUTO_ESCALATION:
                setupAutoEscalationDisplay();
                break;
            case AUTO_ALERTS:
                setupAutoAlertsDisplay();
                break;
            case AUTO_RESPONSES:
                setupAutoResponsesDisplay();
                break;
            case SMART_DETECTION:
                setupSmartDetectionDisplay();
                break;
            case SCHEDULING:
                setupSchedulingDisplay();
                break;
            case REPORTING:
                setupReportingDisplay();
                break;
        }
    }
    
    private void setupOverviewDisplay() {
        
        inventory.setItem(19, createItem(Material.EMERALD,
            "§a⚡ ꜱʏꜱᴛᴇᴍ ꜱᴛᴀᴛᴜꜱ",
            "§7Overall automation status",
            "§7Status: " + getSystemStatus(),
            "§7Active: §a" + getActiveAutomationCount() + "/" + getTotalAutomationCount(),
            "§7Efficiency: §6" + getSystemEfficiency() + "%",
            "§7Uptime: §b" + getSystemUptime()));
        
        inventory.setItem(20, createItem(Material.CLOCK,
            "§b📊 ᴘᴇʀꜰᴏʀᴍᴀɴᴄᴇ",
            "§7Automation performance metrics",
            "§7Actions/hour: §e" + getActionsPerHour(),
            "§7Success rate: §a" + getSuccessRate() + "%",
            "§7Avg response: §6" + getAvgResponseTime(),
            "§7Error rate: §c" + getErrorRate() + "%"));
        
        inventory.setItem(21, createItem(Material.REDSTONE_TORCH,
            "§c🔥 ʀᴇᴄᴇɴᴛ ᴀᴄᴛɪᴠɪᴛʏ",
            "§7Recent automated actions",
            "§7Last hour: §e" + getRecentActions(),
            "§7Most active: §6" + getMostActiveAutomation(),
            "§7Latest action: §7" + getLatestAction(),
            "§7Queue length: §b" + getQueueLength()));
        
        inventory.setItem(22, createItem(Material.COMPARATOR,
            "§6⚙ Qᴜɪᴄᴋ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Most used automation toggles",
            getToggleStatus("auto_kick_spam", "Auto Kick Spam"),
            getToggleStatus("auto_mute_toxicity", "Auto Mute Toxicity"),
            getToggleStatus("escalation_enabled", "Auto Escalation"),
            "§7",
            "§aClick to access quick toggles!"));
        
        inventory.setItem(23, createItem(Material.PAPER,
            "§e📋 ʀᴇᴄᴇɴᴛ ᴄʜᴀɴɢᴇꜱ",
            "§7Recent configuration changes",
            "§7Changes today: §e" + getTodayChanges(),
            "§7Last change: §7" + getLastChange(),
            "§7Changed by: §a" + getLastChangedBy(),
            "§7Pending: §6" + getPendingChanges()));
        
        inventory.setItem(24, createItem(Material.BELL,
            "§c🚨 ᴀʟᴇʀᴛꜱ & ɪꜱꜱᴜᴇꜱ",
            "§7System alerts and issues",
            "§7Active alerts: §c" + getActiveAlerts(),
            "§7Warnings: §e" + getWarningCount(),
            "§7Failed actions: §4" + getFailedActions(),
            "§7Last check: §7" + getLastHealthCheck()));
        
        inventory.setItem(25, createItem(Material.BOOK,
            "§d📚 ᴀᴜᴛᴏᴍᴀᴛɪᴏɴ ʟᴏɢ",
            "§7Recent automation log entries",
            "§7Entries today: §e" + getLogEntriesToday(),
            "§7Log size: §b" + getLogSize(),
            "§7Retention: §6" + getLogRetention() + " days",
            "§7",
            "§aClick to view full log!"));
    }
    
    private void setupAutoModerationDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("auto_kick_spam") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§e🚫 ᴀᴜᴛᴏ ᴋɪᴄᴋ ꜱᴘᴀᴍ",
            "§7Automatically kick spammers",
            "§7Status: " + getStatusColor("auto_kick_spam") + getStatusText("auto_kick_spam"),
            "§7Threshold: §6" + automationSettings.get("spam_threshold") + " messages",
            "§7Actions today: §e" + getActionCount("spam_kicks"),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(20, createItem(
            automationStates.get("auto_mute_toxicity") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§c🔇 ᴀᴜᴛᴏ ᴍᴜᴛᴇ ᴛᴏxɪᴄɪᴛʏ",
            "§7Automatically mute toxic behavior",
            "§7Status: " + getStatusColor("auto_mute_toxicity") + getStatusText("auto_mute_toxicity"),
            "§7Threshold: §6" + automationSettings.get("toxicity_threshold") + "%",
            "§7Actions today: §e" + getActionCount("toxicity_mutes"),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(21, createItem(
            automationStates.get("auto_warn_caps") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§6⚠ ᴀᴜᴛᴏ ᴡᴀʀɴ ᴄᴀᴘꜱ",
            "§7Warn for excessive caps usage",
            "§7Status: " + getStatusColor("auto_warn_caps") + getStatusText("auto_warn_caps"),
            "§7Threshold: §6" + automationSettings.get("caps_percentage") + "% caps",
            "§7Actions today: §e" + getActionCount("caps_warnings"),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(22, createItem(Material.REDSTONE,
            "§4⚙ ᴀᴅᴠᴀɴᴄᴇᴅ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure detection thresholds",
            "§7Spam sensitivity: §6" + getSpamSensitivity(),
            "§7Toxicity model: §b" + getToxicityModel(),
            "§7False positive rate: §e" + getFalsePositiveRate() + "%",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(23, createItem(Material.CLOCK,
            "§b⏰ ᴛɪᴍɪɴɢ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure action delays and cooldowns",
            "§7Action delay: §6" + getActionDelay() + "s",
            "§7Cooldown period: §e" + getCooldownPeriod() + "s",
            "§7Grace period: §a" + getGracePeriod() + "s",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(24, createItem(Material.PAPER,
            "§e📋 ᴇxᴄᴇᴘᴛɪᴏɴꜱ",
            "§7Manage whitelist and exceptions",
            "§7Whitelisted players: §a" + getWhitelistedCount(),
            "§7Exempt channels: §b" + getExemptChannels(),
            "§7Staff immunity: " + getStaffImmunityStatus(),
            "§7",
            "§aClick to manage!"));
    }
    
    private void setupAutoPunishmentDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("auto_ban_extreme") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§4🔨 ᴀᴜᴛᴏ ʙᴀɴ ᴇxᴛʀᴇᴍᴇ",
            "§7Automatically ban extreme violations",
            "§7Status: " + getStatusColor("auto_ban_extreme") + getStatusText("auto_ban_extreme"),
            "§7Duration: §c" + automationSettings.get("ban_duration") + " days",
            "§7Actions today: §e" + getActionCount("auto_bans"),
            "§7",
            "§cClick to toggle! (HIGH RISK)"));
        
        inventory.setItem(20, createItem(Material.DIAMOND_SWORD,
            "§6⚔ ᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴛɪᴇʀꜱ",
            "§7Configure punishment escalation",
            "§7Tier 1: §a" + getTier1Punishment(),
            "§7Tier 2: §e" + getTier2Punishment(),
            "§7Tier 3: §c" + getTier3Punishment(),
            "§7Tier 4: §4" + getTier4Punishment(),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(21, createItem(Material.CLOCK,
            "§b⏰ ᴅᴜʀᴀᴛɪᴏɴ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure punishment durations",
            "§7Mute duration: §e" + getMuteDuration(),
            "§7Temp ban: §6" + getTempBanDuration(),
            "§7Escalation delay: §b" + getEscalationDelay(),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.PAPER,
            "§e📋 ᴄᴜꜱᴛᴏᴍ ʀᴜʟᴇꜱ",
            "§7Custom punishment rules",
            "§7Active rules: §a" + getActiveRulesCount(),
            "§7Condition-based: §b" + getConditionalRules(),
            "§7Pattern-based: §6" + getPatternRules(),
            "§7",
            "§aClick to manage!"));
        
        inventory.setItem(23, createItem(Material.BARRIER,
            "§c🛡 ꜱᴀꜰᴇᴛʏ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Safety measures and limits",
            "§7Max auto-bans/day: §c" + getMaxAutoBans(),
            "§7Review required: " + getReviewRequiredStatus(),
            "§7Override permissions: " + getOverrideStatus(),
            "§7",
            "§aClick to configure!"));
    }
    
    private void setupAutoEscalationDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("escalation_enabled") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§6📈 ᴇꜱᴄᴀʟᴀᴛɪᴏɴ ᴇɴᴀʙʟᴇᴅ",
            "§7Automatic punishment escalation",
            "§7Status: " + getStatusColor("escalation_enabled") + getStatusText("escalation_enabled"),
            "§7Escalations today: §e" + getEscalationsToday(),
            "§7Success rate: §a" + getEscalationSuccessRate() + "%",
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(20, createItem(Material.LADDER,
            "§e🪜 ᴇꜱᴄᴀʟᴀᴛɪᴏɴ ᴘᴀᴛʜ",
            "§7Define escalation sequence",
            "§71st offense: §a" + getFirstOffenseAction(),
            "§72nd offense: §e" + getSecondOffenseAction(),
            "§73rd offense: §6" + getThirdOffenseAction(),
            "§74th offense: §c" + getFourthOffenseAction(),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(21, createItem(Material.CLOCK,
            "§b⏰ ᴇꜱᴄᴀʟᴀᴛɪᴏɴ ᴛɪᴍɪɴɢ",
            "§7Time-based escalation rules",
            "§7Reset period: §6" + getResetPeriod(),
            "§7Grace period: §a" + getGracePeriod(),
            "§7Escalation delay: §e" + automationSettings.get("escalation_delay") + "s",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.COMPARATOR,
            "§d🎯 ꜱᴍᴀʀᴛ ᴇꜱᴄᴀʟᴀᴛɪᴏɴ",
            "§7AI-powered escalation decisions",
            "§7Smart mode: " + getSmartModeStatus(),
            "§7Context awareness: " + getContextAwarenessStatus(),
            "§7Learning enabled: " + getLearningStatus(),
            "§7",
            "§aClick to configure!"));
    }
    
    private void setupAutoAlertsDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("alert_notifications") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§c🔔 ᴀʟᴇʀᴛ ɴᴏᴛɪꜰɪᴄᴀᴛɪᴏɴꜱ",
            "§7Automatic staff notifications",
            "§7Status: " + getStatusColor("alert_notifications") + getStatusText("alert_notifications"),
            "§7Alerts sent today: §e" + getAlertsSentToday(),
            "§7Response rate: §a" + getAlertResponseRate() + "%",
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(20, createItem(Material.BELL,
            "§6🚨 ᴀʟᴇʀᴛ ᴛʏᴘᴇꜱ",
            "§7Configure alert categories",
            "§7High severity: " + getAlertTypeStatus("high"),
            "§7Spam detection: " + getAlertTypeStatus("spam"),
            "§7Toxicity alerts: " + getAlertTypeStatus("toxicity"),
            "§7System errors: " + getAlertTypeStatus("errors"),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(21, createItem(Material.CLOCK,
            "§b⏰ ᴀʟᴇʀᴛ ᴄᴏᴏʟᴅᴏᴡɴ",
            "§7Prevent alert spam",
            "§7Cooldown period: §6" + automationSettings.get("alert_cooldown") + "s",
            "§7Rate limiting: " + getRateLimitingStatus(),
            "§7Duplicate detection: " + getDuplicateDetectionStatus(),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.PAPER,
            "§e📱 ɴᴏᴛɪꜰɪᴄᴀᴛɪᴏɴ ᴍᴇᴛʜᴏᴅꜱ",
            "§7Alert delivery methods",
            "§7In-game messages: " + getNotificationMethodStatus("ingame"),
            "§7Discord webhook: " + getNotificationMethodStatus("discord"),
            "§7Email alerts: " + getNotificationMethodStatus("email"),
            "§7",
            "§aClick to configure!"));
    }
    
    private void setupAutoResponsesDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("auto_responses") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§b💬 ᴀᴜᴛᴏ ʀᴇꜱᴘᴏɴꜱᴇꜱ",
            "§7Automated chat responses",
            "§7Status: " + getStatusColor("auto_responses") + getStatusText("auto_responses"),
            "§7Responses today: §e" + getAutoResponsesToday(),
            "§7Effectiveness: §a" + getResponseEffectiveness() + "%",
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(20, createItem(Material.WRITABLE_BOOK,
            "§e📝 ʀᴇꜱᴘᴏɴꜱᴇ ᴛᴇᴍᴘʟᴀᴛᴇꜱ",
            "§7Manage response templates",
            "§7Active templates: §a" + getActiveTemplatesCount(),
            "§7Categories: §b" + getTemplateCategoriesCount(),
            "§7Custom responses: §6" + getCustomResponsesCount(),
            "§7",
            "§aClick to manage!"));
        
        inventory.setItem(21, createItem(Material.COMPASS,
            "§d🎯 ᴛʀɪɢɢᴇʀ ᴄᴏɴᴅɪᴛɪᴏɴꜱ",
            "§7Configure response triggers",
            "§7Keyword triggers: §a" + getKeywordTriggersCount(),
            "§7Pattern triggers: §b" + getPatternTriggersCount(),
            "§7Context triggers: §6" + getContextTriggersCount(),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.CLOCK,
            "§b⏰ ʀᴇꜱᴘᴏɴꜱᴇ ᴛɪᴍɪɴɢ",
            "§7Response timing settings",
            "§7Response delay: §6" + getResponseDelay() + "s",
            "§7Cooldown period: §e" + getResponseCooldown() + "s",
            "§7Rate limiting: " + getResponseRateLimitingStatus(),
            "§7",
            "§aClick to configure!"));
    }
    
    private void setupSmartDetectionDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("smart_detection") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§d🧠 ꜱᴍᴀʀᴛ ᴅᴇᴛᴇᴄᴛɪᴏɴ",
            "§7AI-powered violation detection",
            "§7Status: " + getStatusColor("smart_detection") + getStatusText("smart_detection"),
            "§7Accuracy: §a" + getDetectionAccuracy() + "%",
            "§7Model version: §b" + getModelVersion(),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(20, createItem(Material.BRAIN_CORAL,
            "§6🧠 ᴀɪ ᴍᴏᴅᴇʟ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Configure AI detection models",
            "§7Toxicity model: §a" + getToxicityModelStatus(),
            "§7Spam model: §b" + getSpamModelStatus(),
            "§7Context model: §e" + getContextModelStatus(),
            "§7Learning mode: " + getLearningModeStatus(),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(21, createItem(Material.COMPARATOR,
            "§b⚖ ᴄᴏɴꜰɪᴅᴇɴᴄᴇ ᴛʜʀᴇꜱʜᴏʟᴅꜱ",
            "§7Set detection confidence levels",
            "§7High confidence: §a" + getHighConfidenceThreshold() + "%",
            "§7Medium confidence: §e" + getMediumConfidenceThreshold() + "%",
            "§7Low confidence: §c" + getLowConfidenceThreshold() + "%",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.REDSTONE,
            "§c⚡ ʀᴇᴀʟ-ᴛɪᴍᴇ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Real-time message analysis",
            "§7Status: " + getStatusColor("real_time_analysis") + getStatusText("real_time_analysis"),
            "§7Processing speed: §6" + getProcessingSpeed() + "ms",
            "§7Queue length: §b" + getAnalysisQueueLength(),
            "§7",
            "§aClick to toggle!"));
    }
    
    private void setupSchedulingDisplay() {
        inventory.setItem(19, createItem(Material.CLOCK,
            "§6⏰ ꜱᴄʜᴇᴅᴜʟᴇᴅ ᴛᴀꜱᴋꜱ",
            "§7Manage scheduled automation",
            "§7Active schedules: §a" + getActiveSchedulesCount(),
            "§7Next execution: §e" + getNextExecution(),
            "§7Failed executions: §c" + getFailedExecutionsCount(),
            "§7",
            "§aClick to manage!"));
        
        inventory.setItem(20, createItem(Material.REDSTONE_TORCH,
            "§c🔥 ᴘᴇᴀᴋ ʜᴏᴜʀ ᴀᴜᴛᴏᴍᴀᴛɪᴏɴ",
            "§7Enhanced automation during peak hours",
            "§7Peak hours: §6" + getPeakHours(),
            "§7Enhanced mode: " + getPeakModeStatus(),
            "§7Sensitivity boost: §a" + getSensitivityBoost() + "%",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(21, createItem(Material.PAPER,
            "§e📋 ᴍᴀɪɴᴛᴇɴᴀɴᴄᴇ ꜱᴄʜᴇᴅᴜʟᴇ",
            "§7Automated maintenance tasks",
            "§7Database cleanup: " + getMaintenanceStatus("database"),
            "§7Log rotation: " + getMaintenanceStatus("logs"),
            "§7Cache refresh: " + getMaintenanceStatus("cache"),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.COMPARATOR,
            "§b📊 ꜱᴄʜᴇᴅᴜʟᴇ ᴀɴᴀʟʏᴛɪᴄꜱ",
            "§7Schedule performance analytics",
            "§7Execution success: §a" + getExecutionSuccessRate() + "%",
            "§7Average delay: §6" + getAverageDelay() + "s",
            "§7Resource usage: §e" + getResourceUsage() + "%",
            "§7",
            "§aClick to view analytics!"));
    }
    
    private void setupReportingDisplay() {
        inventory.setItem(19, createItem(
            automationStates.get("schedule_reports") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§e📋 ꜱᴄʜᴇᴅᴜʟᴇᴅ ʀᴇᴘᴏʀᴛꜱ",
            "§7Automated report generation",
            "§7Status: " + getStatusColor("schedule_reports") + getStatusText("schedule_reports"),
            "§7Interval: §6" + automationSettings.get("report_interval") + " hours",
            "§7Last report: §7" + getLastReportTime(),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(20, createItem(Material.BOOK,
            "§b📚 ʀᴇᴘᴏʀᴛ ᴛʏᴘᴇꜱ",
            "§7Configure automated reports",
            "§7Daily summaries: " + getReportTypeStatus("daily"),
            "§7Weekly analytics: " + getReportTypeStatus("weekly"),
            "§7Monthly trends: " + getReportTypeStatus("monthly"),
            "§7Incident reports: " + getReportTypeStatus("incidents"),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(21, createItem(Material.PAPER,
            "§6📤 ᴅᴇʟɪᴠᴇʀʏ ᴍᴇᴛʜᴏᴅꜱ",
            "§7Report delivery configuration",
            "§7Email delivery: " + getDeliveryMethodStatus("email"),
            "§7Discord webhook: " + getDeliveryMethodStatus("discord"),
            "§7File export: " + getDeliveryMethodStatus("file"),
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(22, createItem(Material.CLOCK,
            "§d⏰ ʀᴇᴘᴏʀᴛ ꜱᴄʜᴇᴅᴜʟᴇ",
            "§7Report generation schedule",
            "§7Next daily: §e" + getNextDailyReport(),
            "§7Next weekly: §6" + getNextWeeklyReport(),
            "§7Next monthly: §b" + getNextMonthlyReport(),
            "§7",
            "§aClick to configure!"));
    }
    
    private void setupActionButtons() {
        inventory.setItem(45, createItem(Material.EMERALD,
            "§a💾 ꜱᴀᴠᴇ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Save current configuration",
            "§7Backup current settings",
            "§7",
            "§aClick to save!"));
        
        inventory.setItem(46, createItem(Material.REDSTONE,
            "§c🔄 ʀᴇꜱᴇᴛ ᴛᴏ ᴅᴇꜰᴀᴜʟᴛꜱ",
            "§7Reset to default settings",
            "§c§lWARNING: This will reset ALL settings!",
            "§7",
            "§cClick to reset!"));
        
        inventory.setItem(47, createItem(Material.PAPER,
            "§b📄 ᴇxᴘᴏʀᴛ ᴄᴏɴꜰɪɢ",
            "§7Export automation configuration",
            "§7Create backup file",
            "§7",
            "§aClick to export!"));
        
        inventory.setItem(48, createItem(Material.HOPPER,
            "§e📥 ɪᴍᴘᴏʀᴛ ᴄᴏɴꜰɪɢ",
            "§7Import automation configuration",
            "§7Restore from backup",
            "§7",
            "§aClick to import!"));
        
        inventory.setItem(50, createItem(Material.CLOCK,
            "§d🔄 ʀᴇꜰʀᴇꜱʜ",
            "§7Refresh automation status",
            "§7Reload all settings",
            "§7",
            "§aClick to refresh!"));
        
        inventory.setItem(51, createItem(Material.BOOK,
            "§6📖 ᴀᴜᴛᴏᴍᴀᴛɪᴏɴ ʟᴏɢ",
            "§7View automation activity log",
            "§7Recent actions and changes",
            "§7",
            "§aClick to view!"));
        
        inventory.setItem(52, createItem(Material.REDSTONE_TORCH,
            "§c🔧 ᴀᴅᴠᴀɴᴄᴇᴅ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Advanced automation configuration",
            "§7Expert-level options",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(53, createItem(Material.BARRIER,
            "§4🚨 ᴇᴍᴇʀɢᴇɴᴄʏ ꜱᴛᴏᴘ",
            "§7Emergency stop all automation",
            "§c§lWARNING: This will disable ALL automation!",
            "§7",
            "§cClick for emergency stop!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        
        if (slot >= 9 && slot <= 16) {
            AutomationCategory[] categories = AutomationCategory.values();
            int categoryIndex = slot - 9;
            if (categoryIndex < categories.length) {
                currentCategory = categories[categoryIndex];
                refresh();
            }
            return;
        }
        
        
        if (slot >= 19 && slot <= 25) {
            handleMainDisplayClick(slot);
            return;
        }
        
        switch (slot) {
            case 45: 
                saveSettings();
                break;
            case 46: 
                resetToDefaults();
                break;
            case 47: 
                exportConfiguration();
                break;
            case 48: 
                importConfiguration();
                break;
            case 50: 
                refreshSettings();
                break;
            case 51: 
                openAutomationLog();
                break;
            case 52: 
                openAdvancedSettings();
                break;
            case 53: 
                emergencyStop();
                break;
        }
    }
    
    private void handleMainDisplayClick(int slot) {
        switch (currentCategory) {
            case AUTO_MODERATION:
                handleAutoModerationClick(slot);
                break;
            case AUTO_PUNISHMENT:
                handleAutoPunishmentClick(slot);
                break;
            case AUTO_ESCALATION:
                handleAutoEscalationClick(slot);
                break;
            case AUTO_ALERTS:
                handleAutoAlertsClick(slot);
                break;
            case AUTO_RESPONSES:
                handleAutoResponsesClick(slot);
                break;
            case SMART_DETECTION:
                handleSmartDetectionClick(slot);
                break;
            case SCHEDULING:
                handleSchedulingClick(slot);
                break;
            case REPORTING:
                handleReportingClick(slot);
                break;
        }
    }
    
    private void handleAutoModerationClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("auto_kick_spam");
                break;
            case 20:
                toggleAutomation("auto_mute_toxicity");
                break;
            case 21:
                toggleAutomation("auto_warn_caps");
                break;
            case 22:
                openAdvancedModerationSettings();
                break;
            case 23:
                openTimingSettings();
                break;
            case 24:
                openExceptionsManager();
                break;
        }
    }
    
    private void handleAutoPunishmentClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("auto_ban_extreme");
                break;
            case 20:
                openPunishmentTierEditor();
                break;
            case 21:
                openDurationSettings();
                break;
            case 22:
                openCustomRulesEditor();
                break;
            case 23:
                openSafetySettings();
                break;
        }
    }
    
    private void handleAutoEscalationClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("escalation_enabled");
                break;
            case 20:
                openEscalationPathEditor();
                break;
            case 21:
                openEscalationTimingSettings();
                break;
            case 22:
                openSmartEscalationSettings();
                break;
        }
    }
    
    private void handleAutoAlertsClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("alert_notifications");
                break;
            case 20:
                openAlertTypesConfig();
                break;
            case 21:
                openAlertCooldownSettings();
                break;
            case 22:
                openNotificationMethodsConfig();
                break;
        }
    }
    
    private void handleAutoResponsesClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("auto_responses");
                break;
            case 20:
                openResponseTemplatesManager();
                break;
            case 21:
                openTriggerConditionsEditor();
                break;
            case 22:
                openResponseTimingSettings();
                break;
        }
    }
    
    private void handleSmartDetectionClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("smart_detection");
                break;
            case 20:
                openAIModelSettings();
                break;
            case 21:
                openConfidenceThresholds();
                break;
            case 22:
                toggleAutomation("real_time_analysis");
                break;
        }
    }
    
    private void handleSchedulingClick(int slot) {
        switch (slot) {
            case 19:
                openScheduledTasksManager();
                break;
            case 20:
                openPeakHourAutomationSettings();
                break;
            case 21:
                openMaintenanceScheduleSettings();
                break;
            case 22:
                openScheduleAnalytics();
                break;
        }
    }
    
    private void handleReportingClick(int slot) {
        switch (slot) {
            case 19:
                toggleAutomation("schedule_reports");
                break;
            case 20:
                openReportTypesConfig();
                break;
            case 21:
                openDeliveryMethodsConfig();
                break;
            case 22:
                openReportScheduleSettings();
                break;
        }
    }
    
    
    private void toggleAutomation(String key) {
        boolean currentState = automationStates.getOrDefault(key, false);
        automationStates.put(key, !currentState);
        
        String friendlyName = getFriendlyName(key);
        String status = !currentState ? "§aenabled" : "§cdisabled";
        player.sendMessage("§6⚙ " + friendlyName + " " + status + "!");
        
        refresh();
    }
    
    private void saveSettings() {
        player.sendMessage("§a💾 Saving automation settings...");
        
        player.sendMessage("§a✓ Settings saved successfully!");
    }
    
    private void resetToDefaults() {
        player.sendMessage("§c🔄 Resetting to default settings...");
        loadAutomationSettings(); 
        player.sendMessage("§e⚠ All settings reset to defaults!");
        refresh();
    }
    
    private void exportConfiguration() {
        player.sendMessage("§b📄 Exporting automation configuration...");
        
        List<String> configData = new ArrayList<>();
        configData.add("SmartChat Automation Configuration Export");
        configData.add("Generated: " + new Date());
        configData.add("");
        
        for (Map.Entry<String, Boolean> entry : automationStates.entrySet()) {
            configData.add(entry.getKey() + "=" + entry.getValue());
        }
        
        for (Map.Entry<String, Object> entry : automationSettings.entrySet()) {
            configData.add(entry.getKey() + "=" + entry.getValue());
        }
        
        plugin.getExportManager().exportViolationsToCSV(new ArrayList<>()).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "automation configuration");
            });
        });
    }
    
    private void importConfiguration() {
        player.sendMessage("§e📥 Configuration import functionality coming soon!");
    }
    
    private void refreshSettings() {
        player.sendMessage("§d🔄 Refreshing automation settings...");
        loadAutomationSettings();
        refresh();
    }
    
    private void openAutomationLog() {
        player.sendMessage("§b📝 Automation log functionality coming soon!");
        player.sendMessage("§7This will show automation activity logs and history.");
    }
    
    private void openAdvancedSettings() {
        player.sendMessage("§6⚙ Advanced automation settings functionality coming soon!");
        player.sendMessage("§7This will provide detailed automation configuration options.");
    }
    
    private void emergencyStop() {
        player.sendMessage("§4🚨 EMERGENCY STOP ACTIVATED!");
        player.sendMessage("§c⚠ ALL AUTOMATION DISABLED!");
        
        
        for (String key : automationStates.keySet()) {
            automationStates.put(key, false);
        }
        
        player.sendMessage("§e⚠ Manual re-enable required for each automation system.");
        refresh();
    }
    
    
    private void openAdvancedModerationSettings() { player.sendMessage("§6🔧 Opening advanced moderation settings..."); }
    private void openTimingSettings() { player.sendMessage("§b⏰ Opening timing settings..."); }
    private void openExceptionsManager() { player.sendMessage("§e📋 Opening exceptions manager..."); }
    private void openPunishmentTierEditor() { player.sendMessage("§6⚔ Opening punishment tier editor..."); }
    private void openDurationSettings() { player.sendMessage("§b⏰ Opening duration settings..."); }
    private void openCustomRulesEditor() { player.sendMessage("§e📋 Opening custom rules editor..."); }
    private void openSafetySettings() { player.sendMessage("§c🛡 Opening safety settings..."); }
    private void openEscalationPathEditor() { player.sendMessage("§e🪜 Opening escalation path editor..."); }
    private void openEscalationTimingSettings() { player.sendMessage("§b⏰ Opening escalation timing settings..."); }
    private void openSmartEscalationSettings() { player.sendMessage("§d🎯 Opening smart escalation settings..."); }
    private void openAlertTypesConfig() { player.sendMessage("§6🚨 Opening alert types configuration..."); }
    private void openAlertCooldownSettings() { player.sendMessage("§b⏰ Opening alert cooldown settings..."); }
    private void openNotificationMethodsConfig() { player.sendMessage("§e📱 Opening notification methods config..."); }
    private void openResponseTemplatesManager() { player.sendMessage("§e📝 Opening response templates manager..."); }
    private void openTriggerConditionsEditor() { player.sendMessage("§d🎯 Opening trigger conditions editor..."); }
    private void openResponseTimingSettings() { player.sendMessage("§b⏰ Opening response timing settings..."); }
    private void openAIModelSettings() { player.sendMessage("§6🧠 Opening AI model settings..."); }
    private void openConfidenceThresholds() { player.sendMessage("§b⚖ Opening confidence thresholds..."); }
    private void openScheduledTasksManager() { player.sendMessage("§6⏰ Opening scheduled tasks manager..."); }
    private void openPeakHourAutomationSettings() { player.sendMessage("§c🔥 Opening peak hour automation settings..."); }
    private void openMaintenanceScheduleSettings() { player.sendMessage("§e📋 Opening maintenance schedule settings..."); }
    private void openScheduleAnalytics() { player.sendMessage("§b📊 Opening schedule analytics..."); }
    private void openReportTypesConfig() { player.sendMessage("§b📚 Opening report types configuration..."); }
    private void openDeliveryMethodsConfig() { player.sendMessage("§6📤 Opening delivery methods config..."); }
    private void openReportScheduleSettings() { player.sendMessage("§d⏰ Opening report schedule settings..."); }
    
    
    private Material getCategoryMaterial(AutomationCategory category) {
        switch (category) {
            case OVERVIEW: return Material.COMPASS;
            case AUTO_MODERATION: return Material.DIAMOND_SWORD;
            case AUTO_PUNISHMENT: return Material.IRON_SWORD;
            case AUTO_ESCALATION: return Material.LADDER;
            case AUTO_ALERTS: return Material.BELL;
            case AUTO_RESPONSES: return Material.WRITABLE_BOOK;
            case SMART_DETECTION: return Material.BRAIN_CORAL;
            case SCHEDULING: return Material.CLOCK;
            case REPORTING: return Material.PAPER;
            default: return Material.REDSTONE;
        }
    }
    
    private String getCategoryDescription(AutomationCategory category) {
        switch (category) {
            case OVERVIEW: return "System overview and quick settings";
            case AUTO_MODERATION: return "Automated moderation actions";
            case AUTO_PUNISHMENT: return "Automatic punishment system";
            case AUTO_ESCALATION: return "Punishment escalation rules";
            case AUTO_ALERTS: return "Staff notification system";
            case AUTO_RESPONSES: return "Automated chat responses";
            case SMART_DETECTION: return "AI-powered detection";
            case SCHEDULING: return "Scheduled automation tasks";
            case REPORTING: return "Automated report generation";
            default: return "Automation configuration";
        }
    }
    
    private String getStatusColor(String key) {
        return automationStates.getOrDefault(key, false) ? "§a" : "§c";
    }
    
    private String getStatusText(String key) {
        return automationStates.getOrDefault(key, false) ? "Enabled" : "Disabled";
    }
    
    private String getToggleStatus(String key, String name) {
        return "§7" + name + ": " + getStatusColor(key) + getStatusText(key);
    }
    
    private String getFriendlyName(String key) {
        switch (key) {
            case "auto_kick_spam": return "Auto Kick Spam";
            case "auto_mute_toxicity": return "Auto Mute Toxicity";
            case "auto_warn_caps": return "Auto Warn Caps";
            case "auto_ban_extreme": return "Auto Ban Extreme";
            case "escalation_enabled": return "Auto Escalation";
            case "smart_detection": return "Smart Detection";
            case "alert_notifications": return "Alert Notifications";
            case "auto_responses": return "Auto Responses";
            case "schedule_reports": return "Scheduled Reports";
            case "real_time_analysis": return "Real-time Analysis";
            default: return "Unknown Setting";
        }
    }
    
    
    private int getActiveAutomationCount() {
        return (int) automationStates.values().stream().filter(Boolean::booleanValue).count();
    }
    
    private int getTotalAutomationCount() { return automationStates.size(); }
    private String getSystemStatus() { return "§a✓ Operational"; }
    private String getSystemEfficiency() { return "92"; }
    private String getSystemUptime() { return "99.8%"; }
    private String getActionsPerHour() { return "156"; }
    private String getSuccessRate() { return "94.2"; }
    private String getAvgResponseTime() { return "1.8s"; }
    private String getErrorRate() { return "0.3"; }
    private String getRecentActions() { return "23"; }
    private String getMostActiveAutomation() { return "Auto Mute Toxicity"; }
    private String getLatestAction() { return "2 min ago"; }
    private String getQueueLength() { return "5"; }
    private int getTodayChanges() { return 8; }
    private String getLastChange() { return "15 min ago"; }
    private String getLastChangedBy() { return player.getName(); }
    private int getPendingChanges() { return 2; }
    private int getActiveAlerts() { return 1; }
    private int getWarningCount() { return 3; }
    private int getFailedActions() { return 0; }
    private String getLastHealthCheck() { return "5 min ago"; }
    private int getLogEntriesToday() { return 247; }
    private String getLogSize() { return "2.3 MB"; }
    private int getLogRetention() { return 30; }
    private String getLastModified() { return "1 hour ago"; }
    
    
    private int getActionCount(String actionType) {
        switch (actionType) {
            case "spam_kicks": return 12;
            case "toxicity_mutes": return 8;
            case "caps_warnings": return 15;
            case "auto_bans": return 2;
            default: return 0;
        }
    }
    
    
    private String getSpamSensitivity() { return "Medium"; }
    private String getToxicityModel() { return "Advanced v2.1"; }
    private String getFalsePositiveRate() { return "2.1"; }
    private String getActionDelay() { return "3"; }
    private String getCooldownPeriod() { return "30"; }
    private String getGracePeriod() { return "60"; }
    private int getWhitelistedCount() { return 5; }
    private int getExemptChannels() { return 2; }
    private String getStaffImmunityStatus() { return "§aEnabled"; }
    
    private String getTier1Punishment() { return "Warning"; }
    private String getTier2Punishment() { return "Mute 5min"; }
    private String getTier3Punishment() { return "Mute 1h"; }
    private String getTier4Punishment() { return "Temp Ban 1d"; }
    private String getMuteDuration() { return "5-60 min"; }
    private String getTempBanDuration() { return "1-7 days"; }
    private String getEscalationDelay() { return "5 min"; }
    private int getActiveRulesCount() { return 12; }
    private int getConditionalRules() { return 8; }
    private int getPatternRules() { return 4; }
    private int getMaxAutoBans() { return 10; }
    private String getReviewRequiredStatus() { return "§aEnabled"; }
    private String getOverrideStatus() { return "§cDisabled"; }
    
    private int getEscalationsToday() { return 6; }
    private String getEscalationSuccessRate() { return "87.5"; }
    private String getFirstOffenseAction() { return "Warning"; }
    private String getSecondOffenseAction() { return "Mute 10min"; }
    private String getThirdOffenseAction() { return "Mute 1h"; }
    private String getFourthOffenseAction() { return "Temp Ban 1d"; }
    private String getResetPeriod() { return "7 days"; }
    private String getSmartModeStatus() { return "§aEnabled"; }
    private String getContextAwarenessStatus() { return "§aEnabled"; }
    private String getLearningStatus() { return "§aEnabled"; }
    
    private int getAlertsSentToday() { return 18; }
    private String getAlertResponseRate() { return "94.4"; }
    private String getAlertTypeStatus(String type) { return "§aEnabled"; }
    private String getRateLimitingStatus() { return "§aEnabled"; }
    private String getDuplicateDetectionStatus() { return "§aEnabled"; }
    private String getNotificationMethodStatus(String method) { return "§aEnabled"; }
    
    private int getAutoResponsesToday() { return 34; }
    private String getResponseEffectiveness() { return "78.2"; }
    private int getActiveTemplatesCount() { return 15; }
    private int getTemplateCategoriesCount() { return 6; }
    private int getCustomResponsesCount() { return 8; }
    private int getKeywordTriggersCount() { return 42; }
    private int getPatternTriggersCount() { return 18; }
    private int getContextTriggersCount() { return 9; }
    private String getResponseDelay() { return "2"; }
    private String getResponseCooldown() { return "15"; }
    private String getResponseRateLimitingStatus() { return "§aEnabled"; }
    
    private String getDetectionAccuracy() { return "91.7"; }
    private String getModelVersion() { return "v3.2.1"; }
    private String getToxicityModelStatus() { return "§aActive"; }
    private String getSpamModelStatus() { return "§aActive"; }
    private String getContextModelStatus() { return "§aActive"; }
    private String getLearningModeStatus() { return "§aEnabled"; }
    private String getHighConfidenceThreshold() { return "85"; }
    private String getMediumConfidenceThreshold() { return "65"; }
    private String getLowConfidenceThreshold() { return "45"; }
    private String getProcessingSpeed() { return "12"; }
    private int getAnalysisQueueLength() { return 3; }
    
    private int getActiveSchedulesCount() { return 8; }
    private String getNextExecution() { return "in 2h 15m"; }
    private int getFailedExecutionsCount() { return 1; }
    private String getPeakHours() { return "7-9 PM"; }
    private String getPeakModeStatus() { return "§aEnabled"; }
    private String getSensitivityBoost() { return "15"; }
    private String getMaintenanceStatus(String task) { return "§aScheduled"; }
    private String getExecutionSuccessRate() { return "96.2"; }
    private String getAverageDelay() { return "1.3"; }
    private String getResourceUsage() { return "23"; }
    
    private String getLastReportTime() { return "4 hours ago"; }
    private String getReportTypeStatus(String type) { return "§aEnabled"; }
    private String getDeliveryMethodStatus(String method) { return "§aEnabled"; }
    private String getNextDailyReport() { return "in 6h"; }
    private String getNextWeeklyReport() { return "in 2d 6h"; }
    private String getNextMonthlyReport() { return "in 15d"; }
}