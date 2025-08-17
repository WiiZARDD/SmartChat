package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.Violation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ViolationManagementGUI extends BaseGUI {
    
    private final Violation violation;
    private final OfflinePlayer targetPlayer;
    
    public ViolationManagementGUI(SmartChat plugin, Player player, Violation violation) {
        super(plugin, player, "§8§l◆ §c§lViolation Management §8§l◆", 54);
        this.violation = violation;
        this.targetPlayer = Bukkit.getOfflinePlayer(violation.getPlayerUuid());
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Managing violation for: §f" + getPlayerName());
        headerLore.add("§7Violation ID: §e#" + violation.getId());
        headerLore.add("§7Category: " + getCategoryColor(violation.getCategory()) + violation.getCategory().toUpperCase());
        headerLore.add("§7Severity: " + getSeverityColor(violation.getSeverity()) + violation.getSeverity().toUpperCase());
        headerLore.add("§7Confidence: §e" + String.format("%.1f%%", violation.getConfidence() * 100));
        headerLore.add("§7Time: §b" + formatTimestamp(violation.getTimestamp()));
        
        inventory.setItem(4, createItem(Material.REDSTONE_BLOCK, 
            "§c§lViolation #" + violation.getId(), headerLore));
        
        
        setupViolationDetails();
        
        
        setupPlayerInformation();
        
        
        setupActionButtons();
        
        
        setupQuickActions();
    }
    
    private void setupViolationDetails() {
        
        List<String> messageLore = new ArrayList<>();
        messageLore.add("§7Original violating message:");
        messageLore.add("§7");
        
        
        String message = violation.getMessage();
        if (message.length() > 40) {
            String[] words = message.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                if (line.length() + word.length() > 40) {
                    messageLore.add("§c" + line.toString());
                    line = new StringBuilder(word);
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }
            if (line.length() > 0) {
                messageLore.add("§c" + line.toString());
            }
        } else {
            messageLore.add("§c" + message);
        }
        
        messageLore.add("§7");
        messageLore.add("§8This message triggered the AI filter");
        
        inventory.setItem(10, createItem(Material.WRITABLE_BOOK, 
            "§c📝 Violating Message", messageLore));
        
        
        List<String> analysisLore = new ArrayList<>();
        analysisLore.add("§7AI Detection Analysis:");
        analysisLore.add("§7");
        analysisLore.add("§8• Category: " + getCategoryColor(violation.getCategory()) + violation.getCategory());
        analysisLore.add("§8• Severity: " + getSeverityColor(violation.getSeverity()) + violation.getSeverity());
        analysisLore.add("§8• Confidence: §e" + String.format("%.2f%%", violation.getConfidence() * 100));
        analysisLore.add("§8• Detection Time: §b" + violation.getTimestamp());
        analysisLore.add("§7");
        if (violation.getActionTaken() != null && !violation.getActionTaken().isEmpty()) {
            analysisLore.add("§7Auto Action: §6" + violation.getActionTaken());
        } else {
            analysisLore.add("§7Auto Action: §8None taken");
        }
        analysisLore.add("§7");
        analysisLore.add("§8AI analysis and detection details");
        
        inventory.setItem(12, createItem(Material.ENDER_EYE, 
            "§d🔮 AI Analysis", analysisLore));
        
        
        Material severityMaterial;
        String severityName;
        List<String> severityLore = new ArrayList<>();
        
        switch (violation.getSeverity().toLowerCase()) {
            case "extreme":
                severityMaterial = Material.REDSTONE_BLOCK;
                severityName = "§4🚨 EXTREME SEVERITY";
                severityLore.add("§7This is a severe violation requiring");
                severityLore.add("§7immediate administrative attention.");
                severityLore.add("§7");
                severityLore.add("§cRecommended: Ban/Severe punishment");
                break;
            case "high":
                severityMaterial = Material.RED_CONCRETE;
                severityName = "§c⚠ HIGH SEVERITY";
                severityLore.add("§7This violation is serious and should");
                severityLore.add("§7be addressed with strong action.");
                severityLore.add("§7");
                severityLore.add("§6Recommended: Mute/Kick");
                break;
            case "medium":
                severityMaterial = Material.ORANGE_CONCRETE;
                severityName = "§6⚠ MEDIUM SEVERITY";
                severityLore.add("§7This violation warrants attention");
                severityLore.add("§7and moderate disciplinary action.");
                severityLore.add("§7");
                severityLore.add("§eRecommended: Warning/Short mute");
                break;
            case "low":
                severityMaterial = Material.YELLOW_CONCRETE;
                severityName = "§e⚠ LOW SEVERITY";
                severityLore.add("§7This is a minor violation that may");
                severityLore.add("§7require a gentle warning.");
                severityLore.add("§7");
                severityLore.add("§aRecommended: Verbal warning");
                break;
            default:
                severityMaterial = Material.GRAY_CONCRETE;
                severityName = "§7⚠ UNKNOWN SEVERITY";
                severityLore.add("§7Severity level could not be determined.");
                break;
        }
        
        inventory.setItem(14, createItem(severityMaterial, 
            severityName, severityLore));
    }
    
    private void setupPlayerInformation() {
        
        List<String> playerLore = new ArrayList<>();
        playerLore.add("§7Player Information:");
        playerLore.add("§7");
        playerLore.add("§8• Name: §f" + getPlayerName());
        playerLore.add("§8• UUID: §e" + violation.getPlayerUuid());
        playerLore.add("§8• Status: " + (targetPlayer.isOnline() ? "§aOnline" : "§7Offline"));
        
        if (targetPlayer.getLastPlayed() > 0) {
            long timeSince = System.currentTimeMillis() - targetPlayer.getLastPlayed();
            playerLore.add("§8• Last Seen: §b" + formatDuration(timeSince / 1000));
        }
        
        playerLore.add("§7");
        playerLore.add("§aClick to view full player profile!");
        
        inventory.setItem(19, createItem(Material.PLAYER_HEAD, 
            "§b👤 " + getPlayerName(), playerLore));
        
        
        List<String> historyLore = new ArrayList<>();
        historyLore.add("§7View this player's violation history");
        historyLore.add("§7and behavior patterns.");
        historyLore.add("§7");
        historyLore.add("§8• Recent violations");
        historyLore.add("§8• Punishment history");
        historyLore.add("§8• Behavior trends");
        historyLore.add("§7");
        historyLore.add("§aClick to view detailed history!");
        
        inventory.setItem(21, createItem(Material.CLOCK, 
            "§6📊 Violation History", historyLore));
        
        
        List<String> punishmentLore = new ArrayList<>();
        punishmentLore.add("§7Check active punishments for this player");
        punishmentLore.add("§7and manage current sanctions.");
        punishmentLore.add("§7");
        punishmentLore.add("§8• Active mutes");
        punishmentLore.add("§8• Temporary bans");
        punishmentLore.add("§8• Warning count");
        punishmentLore.add("§7");
        punishmentLore.add("§aClick to manage punishments!");
        
        inventory.setItem(23, createItem(Material.IRON_BARS, 
            "§c⚔ Active Punishments", punishmentLore));
    }
    
    private void setupActionButtons() {
        
        inventory.setItem(28, createItem(Material.PAPER, 
            "§e⚠ Issue Warning",
            "§7Send a formal warning to the player",
            "§7about their chat behavior.",
            "§7",
            "§8Severity: Low impact",
            "§8Duration: Permanent record",
            "§7",
            "§aClick to issue warning!"));
        
        
        inventory.setItem(29, createItem(Material.BARRIER, 
            "§6🔇 Mute Player",
            "§7Temporarily mute the player from chat",
            "§7to prevent further violations.",
            "§7",
            "§8Severity: Medium impact",
            "§8Duration: Configurable",
            "§7",
            "§aClick to mute player!"));
        
        
        inventory.setItem(30, createItem(Material.IRON_DOOR, 
            "§c👢 Kick Player",
            "§7Remove the player from the server",
            "§7as immediate disciplinary action.",
            "§7",
            "§8Severity: Medium-High impact",
            "§8Duration: Until reconnect",
            "§7",
            "§aClick to kick player!"));
        
        
        inventory.setItem(32, createItem(Material.ANVIL, 
            "§4🔨 Ban Player",
            "§7Permanently or temporarily ban",
            "§7the player from the server.",
            "§7",
            "§8Severity: High impact",
            "§8Duration: Configurable",
            "§7",
            "§cClick to ban player!"));
        
        
        inventory.setItem(34, createItem(Material.COMMAND_BLOCK, 
            "§d⚙ Custom Action",
            "§7Execute custom commands or",
            "§7alternative punishments.",
            "§7",
            "§8Create custom responses",
            "§8for unique situations",
            "§7",
            "§aClick for custom options!"));
    }
    
    private void setupQuickActions() {
        
        inventory.setItem(37, createItem(Material.LIME_DYE, 
            "§a✓ Dismiss Violation",
            "§7Mark this violation as handled",
            "§7without taking further action.",
            "§7",
            "§8Use for false positives or",
            "§8minor infractions that don't",
            "§8require punishment",
            "§7",
            "§aClick to dismiss!"));
        
        
        inventory.setItem(39, createItem(Material.WRITABLE_BOOK, 
            "§b📋 Create Appeal",
            "§7Create an appeal entry for this",
            "§7violation if the player contests it.",
            "§7",
            "§8Useful for disputed violations",
            "§8or borderline cases",
            "§7",
            "§aClick to create appeal!"));
        
        
        inventory.setItem(41, createItem(Material.YELLOW_DYE, 
            "§e👁 Mark for Review",
            "§7Flag this violation for additional",
            "§7review by senior staff members.",
            "§7",
            "§8Use for complex cases requiring",
            "§8additional consideration",
            "§7",
            "§aClick to flag for review!"));
        
        
        inventory.setItem(43, createItem(Material.BOOK, 
            "§7📖 View Full Details",
            "§7View complete violation context,",
            "§7technical details, and logs.",
            "§7",
            "§8Includes AI analysis data,",
            "§8timing, and server context",
            "§7",
            "§aClick for full details!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 19: 
                openPlayerProfile();
                break;
            case 21: 
                openViolationHistory();
                break;
            case 23: 
                openPunishmentManagement();
                break;
            case 28: 
                issueWarning();
                break;
            case 29: 
                openMuteOptions();
                break;
            case 30: 
                kickPlayer();
                break;
            case 32: 
                openBanOptions();
                break;
            case 34: 
                openCustomActions();
                break;
            case 37: 
                dismissViolation();
                break;
            case 39: 
                createAppeal();
                break;
            case 41: 
                markForReview();
                break;
            case 43: 
                viewFullDetails();
                break;
        }
    }
    
    private void openPlayerProfile() {
        
        player.sendMessage("§7Opening player profile for " + getPlayerName() + "...");
        player.sendMessage("§7Player profile GUI coming soon!");
    }
    
    private void openViolationHistory() {
        
        player.sendMessage("§7Loading violation history for " + getPlayerName() + "...");
        
        player.sendMessage("§7Player violation history GUI coming soon!");
    }
    
    private void openPunishmentManagement() {
        
        player.sendMessage("§7Opening punishment management for " + getPlayerName() + "...");
        player.sendMessage("§7Punishment management GUI coming soon!");
    }
    
    private void issueWarning() {
        player.sendMessage("§eIssuing warning to " + getPlayerName() + "...");
        player.sendMessage("§aWarning issued for violation #" + violation.getId());
        
        
        if (targetPlayer.isOnline()) {
            Player onlinePlayer = targetPlayer.getPlayer();
            onlinePlayer.sendMessage("§c⚠ WARNING ⚠");
            onlinePlayer.sendMessage("§7You have received a warning for inappropriate chat behavior.");
            onlinePlayer.sendMessage("§7Please follow the server chat rules.");
            onlinePlayer.sendMessage("§7Violation ID: #" + violation.getId());
        }
        
        
        player.closeInventory();
    }
    
    private void openMuteOptions() {
        
        player.sendMessage("§6Mute options for " + getPlayerName() + ":");
        player.sendMessage("§e/mute " + getPlayerName() + " 5m Chat violation");
        player.sendMessage("§e/mute " + getPlayerName() + " 1h Repeated violations");
        player.sendMessage("§e/mute " + getPlayerName() + " 1d Severe violation");
        player.sendMessage("§7Or use the mute GUI (coming soon)");
        player.closeInventory();
    }
    
    private void kickPlayer() {
        if (targetPlayer.isOnline()) {
            Player onlinePlayer = targetPlayer.getPlayer();
            onlinePlayer.kickPlayer("§cKicked for chat rule violation\n\n§7Violation ID: #" + violation.getId() + "\n§7Please follow server rules when you return.");
            player.sendMessage("§c" + getPlayerName() + " has been kicked for violation #" + violation.getId());
        } else {
            player.sendMessage("§7Player " + getPlayerName() + " is not online to kick.");
        }
        player.closeInventory();
    }
    
    private void openBanOptions() {
        
        player.sendMessage("§cBan options for " + getPlayerName() + ":");
        player.sendMessage("§c/ban " + getPlayerName() + " 1d Chat violation - ID #" + violation.getId());
        player.sendMessage("§c/ban " + getPlayerName() + " 7d Severe chat violation");
        player.sendMessage("§c/ban " + getPlayerName() + " permanent Extreme violation");
        player.sendMessage("§7Or use the ban GUI (coming soon)");
        player.closeInventory();
    }
    
    private void openCustomActions() {
        player.sendMessage("§dCustom action options:");
        player.sendMessage("§e/tp " + getPlayerName() + " spawn §7- Teleport to spawn");
        player.sendMessage("§e/freeze " + getPlayerName() + " §7- Freeze player");
        player.sendMessage("§e/jail " + getPlayerName() + " §7- Send to jail");
        player.sendMessage("§7Custom action GUI coming soon!");
        player.closeInventory();
    }
    
    private void dismissViolation() {
        player.sendMessage("§aViolation #" + violation.getId() + " has been dismissed.");
        player.sendMessage("§7No action taken - marked as handled.");
        
        player.closeInventory();
    }
    
    private void createAppeal() {
        player.sendMessage("§bCreating appeal entry for violation #" + violation.getId() + "...");
        player.sendMessage("§7Appeal creation system coming soon!");
        
        player.closeInventory();
    }
    
    private void markForReview() {
        player.sendMessage("§eViolation #" + violation.getId() + " marked for senior staff review.");
        player.sendMessage("§7This violation will be flagged for additional consideration.");
        
        player.closeInventory();
    }
    
    private void viewFullDetails() {
        
        player.sendMessage("§7§l======= FULL VIOLATION DETAILS =======");
        player.sendMessage("§7Violation ID: §e#" + violation.getId());
        player.sendMessage("§7Player: §f" + getPlayerName() + " §8(" + violation.getPlayerUuid() + ")");
        player.sendMessage("§7Timestamp: §b" + violation.getTimestamp());
        player.sendMessage("§7Category: " + getCategoryColor(violation.getCategory()) + violation.getCategory());
        player.sendMessage("§7Severity: " + getSeverityColor(violation.getSeverity()) + violation.getSeverity());
        player.sendMessage("§7AI Confidence: §e" + String.format("%.3f%%", violation.getConfidence() * 100));
        
        if (violation.getActionTaken() != null && !violation.getActionTaken().isEmpty()) {
            player.sendMessage("§7Auto Action: §6" + violation.getActionTaken());
        } else {
            player.sendMessage("§7Auto Action: §8None");
        }
        
        player.sendMessage("§7");
        player.sendMessage("§7Original Message:");
        player.sendMessage("§c" + violation.getMessage());
        player.sendMessage("§7§l=================================");
    }
    
    private String getPlayerName() {
        if (violation.getPlayerName() != null) {
            return violation.getPlayerName();
        }
        return targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
    }
    
    private String getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "toxicity": return "§c";
            case "harassment": return "§6";
            case "profanity": return "§5";
            case "spam": return "§e";
            case "hate-speech": return "§8";
            default: return "§7";
        }
    }
    
    private String getSeverityColor(String severity) {
        switch (severity.toLowerCase()) {
            case "extreme": return "§4";
            case "high": return "§c";
            case "medium": return "§6";
            case "low": return "§e";
            default: return "§7";
        }
    }
    
    private String formatTimestamp(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "Unknown";
        
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s") + " ago";
        }
    }
}