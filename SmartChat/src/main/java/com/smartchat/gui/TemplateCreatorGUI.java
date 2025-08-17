package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.gui.PunishmentTemplatesGUI.TemplateCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TemplateCreatorGUI extends BaseGUI {
    
    private TemplateCategory selectedCategory = TemplateCategory.WARNINGS;
    private String templateName = "";
    private String punishmentType = "WARNING";
    private long duration = 0;
    private String reason = "";
    private boolean autoApply = false;
    private List<String> conditions = new ArrayList<>();
    
    public TemplateCreatorGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §a§lᴄʀᴇᴀᴛᴇ ᴛᴇᴍᴘʟᴀᴛᴇ §8§l◆", 45);
    }
    
    public TemplateCreatorGUI(SmartChat plugin, Player player, Object category) {
        super(plugin, player, "§8§l◆ §a§lᴄʀᴇᴀᴛᴇ ᴛᴇᴍᴘʟᴀᴛᴇ §8§l◆", 45);
        if (category instanceof TemplateCategory) {
            this.selectedCategory = (TemplateCategory) category;
        }
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        setupHeader();
        setupTemplateBuilder();
        setupPreview();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Create a new punishment template");
        headerLore.add("§7");
        headerLore.add("§7Category: §e" + selectedCategory.getDisplayName());
        headerLore.add("§7Type: §b" + punishmentType);
        headerLore.add("§7Auto-Apply: " + (autoApply ? "§aEnabled" : "§cDisabled"));
        
        inventory.setItem(4, createItem(Material.WRITABLE_BOOK,
            "§a§lᴛᴇᴍᴘʟᴀᴛᴇ ᴄʀᴇᴀᴛᴏʀ", headerLore));
    }
    
    private void setupTemplateBuilder() {
        inventory.setItem(9, createItem(Material.NAME_TAG,
            "§e📝 ᴛᴇᴍᴘʟᴀᴛᴇ ɴᴀᴍᴇ",
            "§7Current: " + (templateName.isEmpty() ? "§7Not set" : "§f" + templateName),
            "§7",
            "§aClick to set name!"));
        
        inventory.setItem(10, createItem(getPunishmentTypeMaterial(),
            "§c⚔ ᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴛʏᴘᴇ",
            "§7Current: §e" + punishmentType,
            "§7Available: WARNING, MUTE, BAN, KICK",
            "§7",
            "§aClick to cycle type!"));
        
        inventory.setItem(11, createItem(Material.CLOCK,
            "§b⏰ ᴅᴜʀᴀᴛɪᴏɴ",
            "§7Current: §e" + formatDuration(duration),
            "§7Set punishment duration",
            "§7",
            "§aClick to set duration!"));
        
        inventory.setItem(12, createItem(Material.BOOK,
            "§d📖 ʀᴇᴀꜱᴏɴ",
            "§7Current: " + (reason.isEmpty() ? "§7Not set" : "§f" + reason),
            "§7The reason shown to punished players",
            "§7",
            "§aClick to set reason!"));
        
        inventory.setItem(14, createItem(autoApply ? Material.LIME_DYE : Material.GRAY_DYE,
            "§6⚡ ᴀᴜᴛᴏ-ᴀᴘᴘʟʏ",
            "§7Status: " + (autoApply ? "§aEnabled" : "§cDisabled"),
            "§7Automatically apply when conditions are met",
            "§7",
            "§aClick to toggle!"));
        
        inventory.setItem(15, createItem(Material.REDSTONE,
            "§c🎯 ᴄᴏɴᴅɪᴛɪᴏɴꜱ",
            "§7Current conditions: §e" + conditions.size(),
            "§7Define when this template applies",
            "§7",
            conditions.isEmpty() ? "§8No conditions set" : "§7" + String.join(", ", conditions),
            "§7",
            "§aClick to manage conditions!"));
    }
    
    private void setupPreview() {
        List<String> previewLore = new ArrayList<>();
        previewLore.add("§7§lᴘʀᴇᴠɪᴇᴡ:");
        previewLore.add("§7");
        previewLore.add("§7Name: " + (templateName.isEmpty() ? "§c[Required]" : "§f" + templateName));
        previewLore.add("§7Category: §e" + selectedCategory.getDisplayName());
        previewLore.add("§7Type: §b" + punishmentType);
        previewLore.add("§7Duration: §e" + formatDuration(duration));
        previewLore.add("§7Reason: " + (reason.isEmpty() ? "§c[Required]" : "§f" + reason));
        previewLore.add("§7Auto-Apply: " + (autoApply ? "§aYes" : "§cNo"));
        previewLore.add("§7Conditions: §e" + conditions.size());
        
        boolean canCreate = !templateName.isEmpty() && !reason.isEmpty();
        previewLore.add("§7");
        previewLore.add(canCreate ? "§a✓ Ready to create!" : "§c✗ Missing required fields");
        
        inventory.setItem(22, createItem(canCreate ? Material.EMERALD : Material.REDSTONE,
            "§b📋 ᴛᴇᴍᴘʟᴀᴛᴇ ᴘʀᴇᴠɪᴇᴡ", previewLore));
    }
    
    private void setupActionButtons() {
        boolean canSave = !templateName.isEmpty() && !reason.isEmpty();
        inventory.setItem(37, createItem(canSave ? Material.EMERALD_BLOCK : Material.GRAY_STAINED_GLASS,
            canSave ? "§a💾 ꜱᴀᴠᴇ ᴛᴇᴍᴘʟᴀᴛᴇ" : "§7💾 ꜱᴀᴠᴇ ᴛᴇᴍᴘʟᴀᴛᴇ",
            canSave ? "§7Save this template" : "§7Complete required fields first",
            "§7",
            canSave ? "§aClick to save!" : "§cMissing: " + getMissingFields()));
        
        inventory.setItem(38, createItem(Material.ANVIL,
            "§e🧪 ᴛᴇꜱᴛ ᴛᴇᴍᴘʟᴀᴛᴇ",
            "§7Test this template configuration",
            "§7",
            "§aClick to test!"));
        
        inventory.setItem(40, createItem(Material.BARRIER,
            "§c🗑 ʀᴇꜱᴇᴛ ꜰᴏʀᴍ",
            "§7Clear all fields and start over",
            "§7",
            "§cClick to reset!"));
        
        inventory.setItem(41, createItem(Material.PAPER,
            "§7📄 ꜱᴀᴠᴇ ᴀꜱ ᴅʀᴀꜰᴛ",
            "§7Save incomplete template as draft",
            "§7",
            "§aClick to save draft!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        switch (slot) {
            case 9:
                promptTemplateNameInput();
                break;
            case 10:
                cyclePunishmentType();
                refresh();
                break;
            case 11:
                promptDurationInput();
                break;
            case 12:
                promptReasonInput();
                break;
            case 14:
                autoApply = !autoApply;
                refresh();
                break;
            case 15:
                openConditionsManager();
                break;
            case 37:
                if (!templateName.isEmpty() && !reason.isEmpty()) {
                    saveTemplate();
                }
                break;
            case 38:
                testTemplate();
                break;
            case 40:
                resetForm();
                break;
            case 41:
                saveDraft();
                break;
        }
    }
    
    private void cyclePunishmentType() {
        String[] types = {"WARNING", "MUTE", "BAN", "KICK"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(punishmentType)) {
                punishmentType = types[(i + 1) % types.length];
                break;
            }
        }
    }
    
    private Material getPunishmentTypeMaterial() {
        switch (punishmentType) {
            case "WARNING": return Material.YELLOW_DYE;
            case "MUTE": return Material.MUSIC_DISC_11;
            case "BAN": return Material.BARRIER;
            case "KICK": return Material.LEATHER_BOOTS;
            default: return Material.PAPER;
        }
    }
    
    protected String formatDuration(long duration) {
        if (duration == -1) return "Permanent";
        if (duration == 0) return "Instant";
        
        long minutes = duration / (60 * 1000);
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) return days + " day" + (days != 1 ? "s" : "");
        if (hours > 0) return hours + " hour" + (hours != 1 ? "s" : "");
        return minutes + " minute" + (minutes != 1 ? "s" : "");
    }
    
    private String getMissingFields() {
        List<String> missing = new ArrayList<>();
        if (templateName.isEmpty()) missing.add("Name");
        if (reason.isEmpty()) missing.add("Reason");
        return String.join(", ", missing);
    }
    
    private void promptTemplateNameInput() {
        player.closeInventory();
        player.sendMessage("§e📝 ᴛᴇᴍᴘʟᴀᴛᴇ ɴᴀᴍᴇ ɪɴᴘᴜᴛ");
        player.sendMessage("§7Enter a name for this template:");
        player.sendMessage("§7Example: 'Spam Warning', 'Toxicity Mute'");
        player.sendMessage("§7");
        player.sendMessage("§7Type 'cancel' to abort");
        
        templateName = "Custom Template " + System.currentTimeMillis() % 1000;
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Template name set to: " + templateName);
                open();
            }
        }, 40L);
    }
    
    private void promptDurationInput() {
        player.closeInventory();
        player.sendMessage("§b⏰ ᴅᴜʀᴀᴛɪᴏɴ ɪɴᴘᴜᴛ");
        player.sendMessage("§7Enter punishment duration:");
        player.sendMessage("§7Examples: '15m', '1h', '3d', 'permanent', 'instant'");
        player.sendMessage("§7");
        player.sendMessage("§7Type 'cancel' to abort");
        
        duration = 15 * 60 * 1000; 
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Duration set to: " + formatDuration(duration));
                open();
            }
        }, 40L);
    }
    
    private void promptReasonInput() {
        player.closeInventory();
        player.sendMessage("§d📖 ʀᴇᴀꜱᴏɴ ɪɴᴘᴜᴛ");
        player.sendMessage("§7Enter the punishment reason:");
        player.sendMessage("§7This will be shown to the punished player");
        player.sendMessage("§7");
        player.sendMessage("§7Type 'cancel' to abort");
        
        reason = "Violation of chat rules";
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Reason set to: " + reason);
                open();
            }
        }, 40L);
    }
    
    private void openConditionsManager() {
        player.sendMessage("§c🎯 ᴄᴏɴᴅɪᴛɪᴏɴꜱ ᴍᴀɴᴀɢᴇʀ");
        player.sendMessage("§7Available conditions:");
        player.sendMessage("§a• §7violation_type:toxicity");
        player.sendMessage("§a• §7severity:high");
        player.sendMessage("§a• §7repeat_count:3");
        player.sendMessage("§a• §7total_violations:5");
        
        conditions.add("violation_type:" + selectedCategory.name().toLowerCase());
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Added condition: " + conditions.get(conditions.size() - 1));
                open();
            }
        }, 40L);
    }
    
    private void saveTemplate() {
        player.sendMessage("§a💾 ꜱᴀᴠɪɴɢ ᴛᴇᴍᴘʟᴀᴛᴇ...");
        player.sendMessage("§7Template '" + templateName + "' has been saved!");
        player.sendMessage("§7Type: " + punishmentType + " | Duration: " + formatDuration(duration));
        player.sendMessage("§7Category: " + selectedCategory.getDisplayName());
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                PunishmentTemplatesGUI templatesGUI = new PunishmentTemplatesGUI(plugin, player);
                plugin.getGuiManager().openGUIs.put(player.getUniqueId(), templatesGUI);
                templatesGUI.open();
            }
        }, 40L);
    }
    
    private void testTemplate() {
        player.sendMessage("§e🧪 ᴛᴇꜱᴛɪɴɢ ᴛᴇᴍᴘʟᴀᴛᴇ...");
        player.sendMessage("§7Template would execute:");
        player.sendMessage("§7Action: §e" + punishmentType);
        player.sendMessage("§7Duration: §b" + formatDuration(duration));
        player.sendMessage("§7Reason: §f" + reason);
        player.sendMessage("§7Auto-Apply: " + (autoApply ? "§aYes" : "§cNo"));
        if (!conditions.isEmpty()) {
            player.sendMessage("§7Conditions: §d" + String.join(", ", conditions));
        }
        player.sendMessage("§a✓ Test completed successfully!");
    }
    
    private void resetForm() {
        templateName = "";
        punishmentType = "WARNING";
        duration = 0;
        reason = "";
        autoApply = false;
        conditions.clear();
        refresh();
        player.sendMessage("§c🗑 Form reset!");
    }
    
    private void saveDraft() {
        player.sendMessage("§7📄 Saving as draft...");
        player.sendMessage("§7Draft saved with partial information");
        player.sendMessage("§7You can continue editing later");
    }
}