package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.models.Appeal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AppealReviewDetailGUI extends BaseGUI {
    
    private final Appeal appeal;
    private final OfflinePlayer targetPlayer;
    
    public AppealReviewDetailGUI(SmartChat plugin, Player player, Appeal appeal) {
        super(plugin, player, "§8§l◆ §e§lAppeal Review §8§l◆", 54);
        this.appeal = appeal;
        this.targetPlayer = Bukkit.getOfflinePlayer(appeal.getPlayerUuid());
    }
    
    @Override
    public void setupGUI() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        addNavigationItems();
        
        
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Reviewing appeal from: §f" + getPlayerName());
        headerLore.add("§7Appeal ID: §e#" + appeal.getId());
        headerLore.add("§7Violation ID: §c#" + appeal.getViolationId());
        headerLore.add("§7Submitted: §b" + formatTimestamp(appeal.getTimestamp()));
        headerLore.add("§7Status: §e" + appeal.getStatus().toUpperCase());
        
        inventory.setItem(4, createItem(Material.PAPER, 
            "§e§lAppeal Review #" + appeal.getId(), headerLore));
        
        
        setupAppealDetails();
        
        
        setupViolationInfo();
        
        
        setupActionButtons();
        
        
        setupAdditionalOptions();
    }
    
    private void setupAppealDetails() {
        
        List<String> reasonLore = new ArrayList<>();
        reasonLore.add("§7Player's appeal explanation:");
        reasonLore.add("§7");
        
        
        String reason = appeal.getReason();
        if (reason.length() > 40) {
            String[] words = reason.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                if (line.length() + word.length() > 40) {
                    reasonLore.add("§f" + line.toString());
                    line = new StringBuilder(word);
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }
            if (line.length() > 0) {
                reasonLore.add("§f" + line.toString());
            }
        } else {
            reasonLore.add("§f" + reason);
        }
        
        reasonLore.add("§7");
        reasonLore.add("§8Player's explanation for this violation");
        
        inventory.setItem(10, createItem(Material.WRITABLE_BOOK, 
            "§e📝 Appeal Reason", reasonLore));
        
        
        List<String> playerLore = new ArrayList<>();
        playerLore.add("§7Player Information:");
        playerLore.add("§7");
        playerLore.add("§8• Name: §f" + getPlayerName());
        playerLore.add("§8• UUID: §e" + appeal.getPlayerUuid());
        playerLore.add("§8• Status: " + (targetPlayer.isOnline() ? "§aOnline" : "§7Offline"));
        
        if (targetPlayer.getLastPlayed() > 0) {
            long timeSince = System.currentTimeMillis() - targetPlayer.getLastPlayed();
            playerLore.add("§8• Last Seen: §b" + formatDuration(timeSince / 1000));
        }
        
        playerLore.add("§7");
        playerLore.add("§aClick to view player profile!");
        
        inventory.setItem(12, createItem(Material.PLAYER_HEAD, 
            "§b👤 " + getPlayerName(), playerLore));
        
        
        List<String> timingLore = new ArrayList<>();
        timingLore.add("§7Appeal Timeline:");
        timingLore.add("§7");
        timingLore.add("§8• Submitted: §b" + appeal.getTimestamp());
        
        long timeSinceSubmission = System.currentTimeMillis() - appeal.getTimestamp().getTime();
        timingLore.add("§8• Time Pending: §e" + formatDuration(timeSinceSubmission / 1000));
        
        timingLore.add("§7");
        timingLore.add("§8Appeal submission information");
        
        inventory.setItem(14, createItem(Material.CLOCK, 
            "§6⏰ Appeal Timeline", timingLore));
    }
    
    private void setupViolationInfo() {
        
        List<String> violationLore = new ArrayList<>();
        violationLore.add("§7Original violation being appealed:");
        violationLore.add("§7");
        violationLore.add("§8• Violation ID: §c#" + appeal.getViolationId());
        violationLore.add("§8• Related to this appeal");
        violationLore.add("§7");
        violationLore.add("§aClick to view violation details!");
        
        inventory.setItem(19, createItem(Material.REDSTONE, 
            "§c⚠ Original Violation", violationLore));
        
        
        List<String> historyLore = new ArrayList<>();
        historyLore.add("§7Review player's complete violation");
        historyLore.add("§7history to make an informed decision.");
        historyLore.add("§7");
        historyLore.add("§8• Previous violations");
        historyLore.add("§8• Pattern analysis");
        historyLore.add("§8• Behavior trends");
        historyLore.add("§7");
        historyLore.add("§aClick to view violation history!");
        
        inventory.setItem(21, createItem(Material.BOOK, 
            "§6📊 Violation History", historyLore));
        
        
        List<String> similarLore = new ArrayList<>();
        similarLore.add("§7Review similar appeals and how");
        similarLore.add("§7they were handled for consistency.");
        similarLore.add("§7");
        similarLore.add("§8• Similar violation types");
        similarLore.add("§8• Previous decisions");
        similarLore.add("§8• Precedent cases");
        similarLore.add("§7");
        similarLore.add("§aClick to view similar cases!");
        
        inventory.setItem(23, createItem(Material.COMPARATOR, 
            "§d🔍 Similar Appeals", similarLore));
    }
    
    private void setupActionButtons() {
        
        inventory.setItem(28, createItem(Material.LIME_DYE, 
            "§a✓ APPROVE APPEAL",
            "§7Accept the player's appeal and",
            "§7reverse the violation/punishment.",
            "§7",
            "§8This will:",
            "§8• Remove the violation",
            "§8• Restore player privileges",
            "§8• Log the appeal approval",
            "§7",
            "§aClick to approve!"));
        
        
        inventory.setItem(29, createItem(Material.YELLOW_DYE, 
            "§e⚠ PARTIAL APPROVAL",
            "§7Partially accept the appeal with",
            "§7reduced punishment instead.",
            "§7",
            "§8This will:",
            "§8• Reduce punishment severity",
            "§8• Keep violation on record",
            "§8• Apply lighter sanctions",
            "§7",
            "§eClick for partial approval!"));
        
        
        inventory.setItem(30, createItem(Material.RED_DYE, 
            "§c✗ DENY APPEAL",
            "§7Reject the player's appeal and",
            "§7maintain original punishment.",
            "§7",
            "§8This will:",
            "§8• Keep original violation",
            "§8• Maintain current punishment",
            "§8• Log the appeal denial",
            "§7",
            "§cClick to deny!"));
        
        
        inventory.setItem(32, createItem(Material.PAPER, 
            "§b📝 REQUEST INFO",
            "§7Ask the player for additional",
            "§7information or clarification.",
            "§7",
            "§8This will:",
            "§8• Send message to player",
            "§8• Keep appeal pending",
            "§8• Request specific details",
            "§7",
            "§bClick to request info!"));
        
        
        inventory.setItem(34, createItem(Material.BELL, 
            "§6🔔 ESCALATE",
            "§7Forward this appeal to senior",
            "§7staff for additional review.",
            "§7",
            "§8This will:",
            "§8• Flag for senior review",
            "§8• Add to priority queue",
            "§8• Notify senior staff",
            "§7",
            "§6Click to escalate!"));
    }
    
    private void setupAdditionalOptions() {
        
        inventory.setItem(37, createItem(Material.FEATHER, 
            "§7✏ Add Notes",
            "§7Add internal notes about this",
            "§7appeal for future reference.",
            "§7",
            "§8For staff documentation"));
        
        
        inventory.setItem(39, createItem(Material.SPYGLASS, 
            "§e🔍 View Chat Logs",
            "§7Review recent chat messages",
            "§7from this player for context.",
            "§7",
            "§8Provides additional context"));
        
        
        inventory.setItem(41, createItem(Material.BOOK, 
            "§a📊 Appeal Stats",
            "§7View statistics about appeals",
            "§7for this violation type.",
            "§7",
            "§8Helps inform decision"));
        
        
        inventory.setItem(43, createItem(Material.CLOCK, 
            "§d⏳ Mark for Follow-up",
            "§7Schedule this appeal for",
            "§7follow-up review later.",
            "§7",
            "§8Reminder system"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 12: 
                openPlayerProfile();
                break;
            case 19: 
                viewOriginalViolation();
                break;
            case 21: 
                viewViolationHistory();
                break;
            case 23: 
                viewSimilarAppeals();
                break;
            case 28: 
                approveAppeal();
                break;
            case 29: 
                partialApproval();
                break;
            case 30: 
                denyAppeal();
                break;
            case 32: 
                requestMoreInfo();
                break;
            case 34: 
                escalateAppeal();
                break;
            case 37: 
                addNotes();
                break;
            case 39: 
                viewChatLogs();
                break;
            case 41: 
                viewAppealStats();
                break;
            case 43: 
                markForFollowUp();
                break;
        }
    }
    
    private void openPlayerProfile() {
        player.sendMessage("§7Opening player profile for " + getPlayerName() + "...");
        
    }
    
    private void viewOriginalViolation() {
        player.sendMessage("§7Loading violation #" + appeal.getViolationId() + "...");
        
    }
    
    private void viewViolationHistory() {
        player.sendMessage("§7Loading violation history for " + getPlayerName() + "...");
        
    }
    
    private void viewSimilarAppeals() {
        player.sendMessage("§7Finding similar appeals...");
        
    }
    
    private void approveAppeal() {
        player.sendMessage("§7Processing appeal approval...");
        
        plugin.getDatabaseManager().resolveAppeal(
            appeal.getId(), 
            player.getUniqueId(), 
            true, 
            "Appeal approved by " + player.getName()
        ).thenRun(() -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Appeal #" + appeal.getId() + " APPROVED!");
                player.sendMessage("§7Violation removed and punishment reversed.");
                
                
                if (targetPlayer.isOnline()) {
                    Player onlinePlayer = targetPlayer.getPlayer();
                    onlinePlayer.sendMessage("§a✓ Your appeal has been APPROVED!");
                    onlinePlayer.sendMessage("§7Appeal ID: #" + appeal.getId());
                    onlinePlayer.sendMessage("§7Your violation has been removed.");
                }
                
                player.closeInventory();
            }
        });
    }
    
    private void partialApproval() {
        player.sendMessage("§ePartial approval options coming soon!");
        
    }
    
    private void denyAppeal() {
        player.sendMessage("§7Processing appeal denial...");
        
        plugin.getDatabaseManager().resolveAppeal(
            appeal.getId(), 
            player.getUniqueId(), 
            false, 
            "Appeal denied by " + player.getName()
        ).thenRun(() -> {
            if (player.isOnline()) {
                player.sendMessage("§c✗ Appeal #" + appeal.getId() + " DENIED!");
                player.sendMessage("§7Original punishment maintained.");
                
                
                if (targetPlayer.isOnline()) {
                    Player onlinePlayer = targetPlayer.getPlayer();
                    onlinePlayer.sendMessage("§c✗ Your appeal has been DENIED.");
                    onlinePlayer.sendMessage("§7Appeal ID: #" + appeal.getId());
                    onlinePlayer.sendMessage("§7Original punishment remains in effect.");
                }
                
                player.closeInventory();
            }
        });
    }
    
    private void requestMoreInfo() {
        player.sendMessage("§bRequest for more information sent to " + getPlayerName());
        
    }
    
    private void escalateAppeal() {
        player.sendMessage("§6Appeal #" + appeal.getId() + " escalated to senior staff.");
        
    }
    
    private void addNotes() {
        player.sendMessage("§7Note system coming soon!");
        
    }
    
    private void viewChatLogs() {
        player.sendMessage("§7Chat log viewer coming soon!");
        
    }
    
    private void viewAppealStats() {
        player.sendMessage("§7Appeal statistics coming soon!");
        
    }
    
    private void markForFollowUp() {
        player.sendMessage("§dAppeal marked for follow-up review.");
        
    }
    
    private String getPlayerName() {
        if (appeal.getPlayerName() != null) {
            return appeal.getPlayerName();
        }
        return targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
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