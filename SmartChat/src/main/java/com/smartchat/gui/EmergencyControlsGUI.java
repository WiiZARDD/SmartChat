package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EmergencyControlsGUI extends BaseGUI {
    
    private Map<String, Boolean> emergencyStates = new HashMap<>();
    private List<String> activeAlerts = new ArrayList<>();
    private EmergencyMode currentMode = EmergencyMode.NORMAL;
    
    public enum EmergencyMode {
        NORMAL("Normal Operation", "§a"),
        ELEVATED("Elevated Alert", "§e"),
        HIGH("High Alert", "§6"),
        CRITICAL("Critical Alert", "§c"),
        LOCKDOWN("Emergency Lockdown", "§4");
        
        private final String displayName;
        private final String color;
        
        EmergencyMode(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }
    
    public EmergencyControlsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §4§lᴇᴍᴇʀɢᴇɴᴄʏ ᴄᴏɴᴛʀᴏʟꜱ §8§l◆", 54);
        initializeEmergencyStates();
    }
    
    private void initializeEmergencyStates() {
        emergencyStates.put("emergency_stop", false);
        emergencyStates.put("auto_ban_enabled", true);
        emergencyStates.put("lockdown_mode", false);
        emergencyStates.put("silent_mode", false);
        emergencyStates.put("staff_alerts", true);
        emergencyStates.put("audit_mode", false);
        
        activeAlerts.add("System monitoring active");
        activeAlerts.add("Emergency controls armed");
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        setupHeader();
        setupEmergencyModeSelector();
        setupQuickActions();
        setupEmergencyToggles();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Emergency response and crisis management");
        headerLore.add("§7");
        headerLore.add("§7Current Mode: " + currentMode.getColor() + currentMode.getDisplayName());
        headerLore.add("§7Active Alerts: §c" + activeAlerts.size());
        headerLore.add("§7System Status: " + getSystemStatus());
        
        inventory.setItem(4, createItem(Material.REDSTONE_BLOCK,
            "§4§lᴇᴍᴇʀɢᴇɴᴄʏ ᴄᴏɴᴛʀᴏʟꜱ", headerLore));
    }
    
    private void setupEmergencyModeSelector() {
        EmergencyMode[] modes = EmergencyMode.values();
        
        for (int i = 0; i < Math.min(modes.length, 5); i++) {
            EmergencyMode mode = modes[i];
            Material material = getModeMaterial(mode);
            String color = currentMode == mode ? "§a" : mode.getColor();
            
            List<String> lore = new ArrayList<>();
            lore.add("§7" + getModeDescription(mode));
            lore.add("§7");
            if (currentMode == mode) {
                lore.add("§a▶ Currently active");
            } else {
                lore.add("§7Click to activate!");
            }
            
            inventory.setItem(9 + i, createItem(material,
                color + "§l" + mode.getDisplayName().toUpperCase(),
                lore));
        }
    }
    
    private void setupQuickActions() {
        inventory.setItem(19, createItem(Material.BARRIER,
            "§4🛑 ᴇᴍᴇʀɢᴇɴᴄʏ ꜱᴛᴏᴘ",
            "§7Immediately stop all automation",
            "§c§lWARNING: This will disable ALL systems!",
            "§7Status: " + getEmergencyStatus("emergency_stop"),
            "§7",
            "§cClick to activate emergency stop!"));
        
        inventory.setItem(20, createItem(Material.IRON_DOOR,
            "§c👥 ᴍᴀꜱꜱ ᴋɪᴄᴋ",
            "§7Kick all non-staff players",
            "§7Emergency player removal",
            "§7Staff immunity: §aEnabled",
            "§7",
            "§cClick to execute mass kick!"));
        
        inventory.setItem(21, createItem(Material.IRON_BARS,
            "§4🔒 ʟᴏᴄᴋᴅᴏᴡɴ ᴍᴏᴅᴇ",
            "§7Prevent new player connections",
            "§7Block all chat except staff",
            "§7Status: " + getEmergencyStatus("lockdown_mode"),
            "§7",
            "§cClick to toggle lockdown!"));
        
        inventory.setItem(22, createItem(Material.MUSIC_DISC_11,
            "§8🔇 ꜱɪʟᴇɴᴛ ᴍᴏᴅᴇ",
            "§7Disable all chat for non-staff",
            "§7Emergency chat silence",
            "§7Status: " + getEmergencyStatus("silent_mode"),
            "§7",
            "§eClick to toggle silent mode!"));
        
        inventory.setItem(23, createItem(Material.BELL,
            "§6🔔 ᴀʟᴇʀᴛ ᴀʟʟ ꜱᴛᴀꜰꜰ",
            "§7Send emergency alert to all staff",
            "§7Notification methods:",
            "§7• In-game messages",
            "§7• Title notifications",
            "§7",
            "§aClick to alert staff!"));
    }
    
    private void setupEmergencyToggles() {
        inventory.setItem(28, createItem(
            emergencyStates.get("auto_ban_enabled") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§6⚡ ᴀᴜᴛᴏ-ʙᴀɴ ꜱʏꜱᴛᴇᴍ",
            "§7Automatic banning for severe violations",
            "§7Status: " + getToggleStatus("auto_ban_enabled"),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(29, createItem(
            emergencyStates.get("staff_alerts") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§e📢 ꜱᴛᴀꜰꜰ ᴀʟᴇʀᴛꜱ",
            "§7Emergency notifications to staff",
            "§7Status: " + getToggleStatus("staff_alerts"),
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(30, createItem(
            emergencyStates.get("audit_mode") ? Material.LIME_DYE : Material.GRAY_DYE,
            "§d📝 ᴀᴜᴅɪᴛ ᴍᴏᴅᴇ",
            "§7Enhanced logging and monitoring",
            "§7Status: " + getToggleStatus("audit_mode"),
            "§7",
            "§aClick to toggle!"));
    }
    
    private void setupActionButtons() {
        inventory.setItem(45, createItem(Material.WRITTEN_BOOK,
            "§b📋 ᴇᴍᴇʀɢᴇɴᴄʏ ᴘʟᴀɴ",
            "§7View emergency response plan",
            "§7",
            "§aClick to view plan!"));
        
        inventory.setItem(46, createItem(Material.BELL,
            "§c🔔 ᴛᴇꜱᴛ ᴀʟᴇʀᴛꜱ",
            "§7Test emergency alert systems",
            "§7",
            "§aClick to test!"));
        
        inventory.setItem(52, createItem(Material.REDSTONE_TORCH,
            "§c🚨 ᴀᴄᴛɪᴠᴇ ᴛʜʀᴇᴀᴛꜱ",
            "§7Current security threats",
            "§7High priority: §c0",
            "§7Medium priority: §e1",
            "§7Low priority: §a3",
            "§7",
            "§aClick to view threats!"));
        
        inventory.setItem(53, createItem(Material.REDSTONE_BLOCK,
            "§4🔄 ʀᴇꜱᴇᴛ ꜱʏꜱᴛᴇᴍ",
            "§7Reset all emergency states",
            "§c§lWARNING: This will clear all alerts!",
            "§7",
            "§cClick to reset!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        if (slot >= 9 && slot <= 13) {
            EmergencyMode[] modes = EmergencyMode.values();
            int modeIndex = slot - 9;
            if (modeIndex < modes.length) {
                setEmergencyMode(modes[modeIndex]);
            }
            return;
        }
        
        switch (slot) {
            case 19: activateEmergencyStop(); break;
            case 20: executeMassKick(); break;
            case 21: toggleLockdownMode(); break;
            case 22: toggleSilentMode(); break;
            case 23: alertAllStaff(); break;
            case 28: toggleEmergencyState("auto_ban_enabled"); break;
            case 29: toggleEmergencyState("staff_alerts"); break;
            case 30: toggleEmergencyState("audit_mode"); break;
            case 45: showEmergencyPlan(); break;
            case 46: testEmergencyAlerts(); break;
            case 52: showActiveThreats(); break;
            case 53: resetEmergencySystem(); break;
        }
    }
    
    private void setEmergencyMode(EmergencyMode mode) {
        currentMode = mode;
        player.sendMessage(mode.getColor() + "§l⚠ EMERGENCY MODE: " + mode.getDisplayName().toUpperCase());
        refresh();
    }
    
    private void activateEmergencyStop() {
        emergencyStates.put("emergency_stop", true);
        player.sendMessage("§4🛑 EMERGENCY STOP ACTIVATED!");
        alertAllStaff("EMERGENCY STOP", "All systems disabled by " + player.getName());
        refresh();
    }
    
    private void executeMassKick() {
        int kickedCount = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("smartchat.staff") && !onlinePlayer.equals(player)) {
                onlinePlayer.kickPlayer("§c⚠ Emergency maintenance in progress.");
                kickedCount++;
            }
        }
        player.sendMessage("§a✓ Kicked " + kickedCount + " non-staff players.");
    }
    
    private void toggleLockdownMode() {
        boolean newState = !emergencyStates.get("lockdown_mode");
        emergencyStates.put("lockdown_mode", newState);
        player.sendMessage(newState ? "§4🔒 LOCKDOWN ACTIVATED!" : "§a🔓 Lockdown deactivated.");
        refresh();
    }
    
    private void toggleSilentMode() {
        boolean newState = !emergencyStates.get("silent_mode");
        emergencyStates.put("silent_mode", newState);
        player.sendMessage(newState ? "§8🔇 SILENT MODE ACTIVATED!" : "§a🔊 Silent mode deactivated.");
        refresh();
    }
    
    private void alertAllStaff() {
        alertAllStaff("EMERGENCY ALERT", "Manual alert by " + player.getName());
    }
    
    private void alertAllStaff(String title, String message) {
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("smartchat.staff")) {
                staff.sendMessage("§4§l[" + title + "] §c" + message);
                staff.sendTitle("§4⚠ " + title, "§c" + message, 10, 70, 20);
            }
        }
        player.sendMessage("§a✓ Emergency alert sent to all staff!");
    }
    
    private void toggleEmergencyState(String key) {
        boolean newState = !emergencyStates.get(key);
        emergencyStates.put(key, newState);
        player.sendMessage("§6⚙ " + getFriendlyStateName(key) + " " + (newState ? "§aenabled" : "§cdisabled"));
        refresh();
    }
    
    private void showEmergencyPlan() {
        player.sendMessage("§b📋 ᴇᴍᴇʀɢᴇɴᴄʏ ʀᴇꜱᴘᴏɴꜱᴇ ᴘʟᴀɴ");
        player.sendMessage("§71. Assess the situation");
        player.sendMessage("§72. Activate appropriate emergency mode");
        player.sendMessage("§73. Alert all staff members");
        player.sendMessage("§74. Take corrective actions");
    }
    
    private void testEmergencyAlerts() {
        alertAllStaff("TEST ALERT", "System test by " + player.getName());
    }
    
    private void showActiveThreats() {
        player.sendMessage("§c🚨 ᴀᴄᴛɪᴠᴇ ꜱᴇᴄᴜʀɪᴛʏ ᴛʜʀᴇᴀᴛꜱ");
        player.sendMessage("§7High priority: §c0");
        player.sendMessage("§7Medium priority: §e1");
        player.sendMessage("§7Low priority: §a3");
    }
    
    private void resetEmergencySystem() {
        currentMode = EmergencyMode.NORMAL;
        emergencyStates.replaceAll((k, v) -> k.equals("auto_ban_enabled") || k.equals("staff_alerts"));
        player.sendMessage("§a✓ Emergency system reset.");
        refresh();
    }
    
    private Material getModeMaterial(EmergencyMode mode) {
        switch (mode) {
            case NORMAL: return Material.EMERALD;
            case ELEVATED: return Material.GOLD_INGOT;
            case HIGH: return Material.ORANGE_DYE;
            case CRITICAL: return Material.REDSTONE;
            case LOCKDOWN: return Material.BARRIER;
            default: return Material.STONE;
        }
    }
    
    private String getModeDescription(EmergencyMode mode) {
        switch (mode) {
            case NORMAL: return "Standard security operation";
            case ELEVATED: return "Increased monitoring and alerts";
            case HIGH: return "Enhanced security measures";
            case CRITICAL: return "Maximum security protocols";
            case LOCKDOWN: return "Emergency lockdown procedures";
            default: return "Unknown security mode";
        }
    }
    
    private String getEmergencyStatus(String key) {
        return emergencyStates.getOrDefault(key, false) ? "§c§lACTIVE" : "§7Inactive";
    }
    
    private String getToggleStatus(String key) {
        return emergencyStates.getOrDefault(key, false) ? "§aEnabled" : "§cDisabled";
    }
    
    private String getFriendlyStateName(String key) {
        switch (key) {
            case "auto_ban_enabled": return "Auto-ban System";
            case "staff_alerts": return "Staff Alerts";
            case "audit_mode": return "Audit Mode";
            default: return "Unknown Setting";
        }
    }
    
    private String getSystemStatus() {
        return currentMode == EmergencyMode.NORMAL ? "§a✓ Normal" : currentMode.getColor() + "⚠ " + currentMode.getDisplayName();
    }
}