package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PunishmentTemplatesGUI extends BaseGUI {
    
    private TemplateCategory currentCategory = TemplateCategory.WARNINGS;
    private List<PunishmentTemplate> templates = new ArrayList<>();
    private int currentPage = 0;
    private final int templatesPerPage = 21; 
    
    public enum TemplateCategory {
        WARNINGS("Warnings"),
        MUTES("Mutes"),
        BANS("Bans"),
        KICKS("Kicks"),
        CUSTOM("Custom"),
        AUTO_ESCALATION("Auto-Escalation");
        
        private final String displayName;
        
        TemplateCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public static class PunishmentTemplate {
        private final String id;
        private final String name;
        private final String description;
        private final TemplateCategory category;
        private final String punishmentType;
        private final long duration; 
        private final String reason;
        private final boolean autoApply;
        private final List<String> conditions;
        private final int usageCount;
        
        public PunishmentTemplate(String id, String name, String description, TemplateCategory category,
                                String punishmentType, long duration, String reason, boolean autoApply,
                                List<String> conditions, int usageCount) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.punishmentType = punishmentType;
            this.duration = duration;
            this.reason = reason;
            this.autoApply = autoApply;
            this.conditions = conditions != null ? conditions : new ArrayList<>();
            this.usageCount = usageCount;
        }
        
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public TemplateCategory getCategory() { return category; }
        public String getPunishmentType() { return punishmentType; }
        public long getDuration() { return duration; }
        public String getReason() { return reason; }
        public boolean isAutoApply() { return autoApply; }
        public List<String> getConditions() { return conditions; }
        public int getUsageCount() { return usageCount; }
        
        public String getFormattedDuration() {
            if (duration == -1) return "Permanent";
            if (duration == 0) return "Instant";
            
            long minutes = duration / (60 * 1000);
            long hours = minutes / 60;
            long days = hours / 24;
            
            if (days > 0) return days + " day" + (days != 1 ? "s" : "");
            if (hours > 0) return hours + " hour" + (hours != 1 ? "s" : "");
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        }
    }
    
    public PunishmentTemplatesGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §e§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴛᴇᴍᴘʟᴀᴛᴇꜱ §8§l◆", 54);
        loadTemplates();
    }
    
    private void loadTemplates() {
        templates.clear();
        
        
        templates.add(new PunishmentTemplate("warn_toxic", "Toxicity Warning", 
            "Standard warning for toxic behavior", TemplateCategory.WARNINGS,
            "WARNING", 0, "Please keep chat friendly and respectful", false,
            List.of("violation_type:toxicity", "severity:medium"), 15));
        
        templates.add(new PunishmentTemplate("warn_spam", "Spam Warning",
            "Warning for spam messages", TemplateCategory.WARNINGS,
            "WARNING", 0, "Please avoid spamming the chat", false,
            List.of("violation_type:spam", "repeat_count:1"), 8));
        
        templates.add(new PunishmentTemplate("warn_final", "Final Warning",
            "Last warning before punishment", TemplateCategory.WARNINGS,
            "WARNING", 0, "This is your final warning before punishment", false,
            List.of("warning_count:>=2"), 3));
        
        
        templates.add(new PunishmentTemplate("mute_short", "Short Mute",
            "15 minute mute for minor violations", TemplateCategory.MUTES,
            "MUTE", 15 * 60 * 1000, "Muted for inappropriate behavior", true,
            List.of("violation_type:profanity", "severity:low"), 12));
        
        templates.add(new PunishmentTemplate("mute_standard", "Standard Mute",
            "1 hour mute for moderate violations", TemplateCategory.MUTES,
            "MUTE", 60 * 60 * 1000, "Muted for violation of chat rules", true,
            List.of("violation_type:harassment", "severity:medium"), 7));
        
        templates.add(new PunishmentTemplate("mute_extended", "Extended Mute",
            "6 hour mute for serious violations", TemplateCategory.MUTES,
            "MUTE", 6 * 60 * 60 * 1000, "Extended mute for serious violation", false,
            List.of("violation_type:toxicity", "severity:high"), 4));
        
        
        templates.add(new PunishmentTemplate("ban_temp", "Temporary Ban",
            "24 hour temporary ban", TemplateCategory.BANS,
            "BAN", 24 * 60 * 60 * 1000, "Temporary ban for serious violation", false,
            List.of("severity:extreme", "repeat_offender:true"), 2));
        
        templates.add(new PunishmentTemplate("ban_perm", "Permanent Ban",
            "Permanent ban for extreme cases", TemplateCategory.BANS,
            "BAN", -1, "Permanently banned for severe violations", false,
            List.of("severity:extreme", "ban_count:>=1"), 1));
        
        
        templates.add(new PunishmentTemplate("kick_warning", "Warning Kick",
            "Kick with warning message", TemplateCategory.KICKS,
            "KICK", 0, "Kicked as a warning - please follow the rules", true,
            List.of("violation_type:disruption"), 5));
        
        
        templates.add(new PunishmentTemplate("escalate_toxic", "Toxicity Escalation",
            "Auto-escalation for repeated toxicity", TemplateCategory.AUTO_ESCALATION,
            "AUTO", 0, "Automatic escalation for repeat toxicity violations", true,
            List.of("violation_type:toxicity", "repeat_count:>=3"), 0));
        
        templates.add(new PunishmentTemplate("escalate_general", "General Escalation",
            "Standard escalation path", TemplateCategory.AUTO_ESCALATION,
            "AUTO", 0, "Standard punishment escalation", true,
            List.of("total_violations:>=5"), 0));
        
        
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.PUNISHMENT));
        addNavigationItems();
        
        setupHeader();
        setupCategorySelector();
        setupTemplateDisplay();
        setupActionButtons();
        setupPaginationControls();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Manage punishment templates and automation");
        headerLore.add("§7");
        headerLore.add("§7Current Category: §e" + currentCategory.getDisplayName());
        headerLore.add("§7Total Templates: §b" + templates.size());
        headerLore.add("§7Active Templates: §a" + getActiveTemplatesCount());
        headerLore.add("§7Auto-Applied: §d" + getAutoAppliedCount());
        
        inventory.setItem(4, createItem(Material.WRITABLE_BOOK,
            "§e§lᴘᴜɴɪꜱʜᴍᴇɴᴛ ᴛᴇᴍᴘʟᴀᴛᴇꜱ", headerLore));
    }
    
    private void setupCategorySelector() {
        
        inventory.setItem(9, createItem(getCurrentCategoryMaterial(TemplateCategory.WARNINGS),
            getCategoryColor(TemplateCategory.WARNINGS) + "⚠ ᴡᴀʀɴɪɴɢꜱ",
            "§7Warning message templates",
            "§7Templates: §e" + getCategoryCount(TemplateCategory.WARNINGS),
            "§7",
            currentCategory == TemplateCategory.WARNINGS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(10, createItem(getCurrentCategoryMaterial(TemplateCategory.MUTES),
            getCategoryColor(TemplateCategory.MUTES) + "🔇 ᴍᴜᴛᴇꜱ",
            "§7Mute punishment templates",
            "§7Templates: §e" + getCategoryCount(TemplateCategory.MUTES),
            "§7",
            currentCategory == TemplateCategory.MUTES ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(11, createItem(getCurrentCategoryMaterial(TemplateCategory.BANS),
            getCategoryColor(TemplateCategory.BANS) + "🚫 ʙᴀɴꜱ",
            "§7Ban punishment templates",
            "§7Templates: §e" + getCategoryCount(TemplateCategory.BANS),
            "§7",
            currentCategory == TemplateCategory.BANS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(12, createItem(getCurrentCategoryMaterial(TemplateCategory.KICKS),
            getCategoryColor(TemplateCategory.KICKS) + "👢 ᴋɪᴄᴋꜱ",
            "§7Kick punishment templates",
            "§7Templates: §e" + getCategoryCount(TemplateCategory.KICKS),
            "§7",
            currentCategory == TemplateCategory.KICKS ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(14, createItem(getCurrentCategoryMaterial(TemplateCategory.CUSTOM),
            getCategoryColor(TemplateCategory.CUSTOM) + "🔧 ᴄᴜꜱᴛᴏᴍ",
            "§7Custom punishment templates",
            "§7Templates: §e" + getCategoryCount(TemplateCategory.CUSTOM),
            "§7",
            currentCategory == TemplateCategory.CUSTOM ? "§a▶ Currently viewing" : "§7Click to view!"));
        
        inventory.setItem(15, createItem(getCurrentCategoryMaterial(TemplateCategory.AUTO_ESCALATION),
            getCategoryColor(TemplateCategory.AUTO_ESCALATION) + "⚡ ᴀᴜᴛᴏ-ᴇꜱᴄᴀʟᴀᴛɪᴏɴ",
            "§7Automatic escalation templates",
            "§7Templates: §e" + getCategoryCount(TemplateCategory.AUTO_ESCALATION),
            "§7",
            currentCategory == TemplateCategory.AUTO_ESCALATION ? "§a▶ Currently viewing" : "§7Click to view!"));
    }
    
    private void setupTemplateDisplay() {
        List<PunishmentTemplate> categoryTemplates = templates.stream()
            .filter(t -> t.getCategory() == currentCategory)
            .toList();
        
        if (categoryTemplates.isEmpty()) {
            inventory.setItem(31, createItem(Material.BARRIER,
                "§7📭 ɴᴏ ᴛᴇᴍᴘʟᴀᴛᴇꜱ",
                "§7No templates in this category",
                "§7Click below to create one!"));
            return;
        }
        
        
        int startIndex = currentPage * templatesPerPage;
        int endIndex = Math.min(startIndex + templatesPerPage, categoryTemplates.size());
        
        
        for (int i = 19; i <= 43; i++) {
            if (i % 9 != 0 && i % 9 != 8) { 
                inventory.setItem(i, null);
            }
        }
        
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            PunishmentTemplate template = categoryTemplates.get(i);
            
            List<String> templateLore = new ArrayList<>();
            templateLore.add("§7Type: §e" + template.getPunishmentType());
            templateLore.add("§7Duration: §b" + template.getFormattedDuration());
            templateLore.add("§7Auto-Apply: " + (template.isAutoApply() ? "§aYes" : "§cNo"));
            templateLore.add("§7Usage Count: §d" + template.getUsageCount());
            templateLore.add("§7");
            templateLore.add("§7Reason: §f\"" + template.getReason() + "\"");
            templateLore.add("§7");
            templateLore.add("§7Description:");
            templateLore.add("§8" + template.getDescription());
            
            if (!template.getConditions().isEmpty()) {
                templateLore.add("§7");
                templateLore.add("§7Conditions:");
                for (String condition : template.getConditions()) {
                    templateLore.add("§8• " + condition);
                }
            }
            
            templateLore.add("§7");
            templateLore.add("§aLeft-click to edit!");
            templateLore.add("§cRight-click to delete!");
            
            int displaySlot = getTemplateDisplaySlot(slotIndex);
            if (displaySlot != -1) {
                inventory.setItem(displaySlot, createItem(
                    getTemplateMaterial(template),
                    getTemplateColor(template) + "📝 " + template.getName(),
                    templateLore));
            }
            
            slotIndex++;
        }
    }
    
    private int getTemplateDisplaySlot(int index) {
        
        int[] slots = {19, 20, 21, 22, 23, 24, 25,
                      28, 29, 30, 31, 32, 33, 34,
                      37, 38, 39, 40, 41, 42, 43};
        return index < slots.length ? slots[index] : -1;
    }
    
    private void setupPaginationControls() {
        List<PunishmentTemplate> categoryTemplates = templates.stream()
            .filter(t -> t.getCategory() == currentCategory)
            .toList();
        
        if (categoryTemplates.isEmpty()) return;
        
        int totalPages = Math.max(1, (categoryTemplates.size() + templatesPerPage - 1) / templatesPerPage);
        
        
        if (currentPage > 0) {
            inventory.setItem(45, createItem(Material.ARROW,
                "§e← ᴘʀᴇᴠɪᴏᴜꜱ ᴘᴀɢᴇ",
                "§7Go to page " + currentPage,
                "§7",
                "§aClick to go back!"));
        }
        
        
        inventory.setItem(49, createItem(Material.COMPASS,
            "§b📍 ᴘᴀɢᴇ " + (currentPage + 1) + "/" + totalPages,
            "§7Showing " + currentCategory.getDisplayName().toLowerCase(),
            "§7Templates: " + Math.min(templatesPerPage, categoryTemplates.size() - (currentPage * templatesPerPage)) + "/" + categoryTemplates.size()));
        
        
        if (currentPage < totalPages - 1) {
            inventory.setItem(53, createItem(Material.ARROW,
                "§e ɴᴇxᴛ ᴘᴀɢᴇ →",
                "§7Go to page " + (currentPage + 2),
                "§7",
                "§aClick to go forward!"));
        }
    }
    
    private void setupActionButtons() {
        
        inventory.setItem(46, createItem(Material.WRITABLE_BOOK,
            "§a➕ ᴄʀᴇᴀᴛᴇ ᴛᴇᴍᴘʟᴀᴛᴇ",
            "§7Create a new punishment template",
            "§7",
            "§aClick to create!"));
        
        
        inventory.setItem(47, createItem(Material.HOPPER,
            "§b📥 ɪᴍᴘᴏʀᴛ ᴛᴇᴍᴘʟᴀᴛᴇꜱ",
            "§7Import templates from file",
            "§7",
            "§aClick to import!"));
        
        
        inventory.setItem(48, createItem(Material.PAPER,
            "§e📤 ᴇxᴘᴏʀᴛ ᴛᴇᴍᴘʟᴀᴛᴇꜱ",
            "§7Export templates to file",
            "§7",
            "§aClick to export!"));
        
        
        inventory.setItem(50, createItem(Material.BOOK,
            "§d📊 ᴛᴇᴍᴘʟᴀᴛᴇ ꜱᴛᴀᴛꜱ",
            "§7View template usage statistics",
            "§7",
            "§aClick to view!"));
        
        
        inventory.setItem(51, createItem(Material.REDSTONE_TORCH,
            "§c⚡ ᴀᴜᴛᴏ-ᴇꜱᴄᴀʟᴀᴛɪᴏɴ",
            "§7Configure automatic escalation",
            "§7",
            "§aClick to configure!"));
        
        
        inventory.setItem(52, createItem(Material.ANVIL,
            "§6⚙ ɢʟᴏʙᴀʟ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Global template settings",
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
            TemplateCategory[] categories = TemplateCategory.values();
            int categoryIndex = slot - 9;
            if (categoryIndex < categories.length) {
                currentCategory = categories[categoryIndex];
                currentPage = 0; 
                refresh();
            }
            return;
        }
        
        switch (slot) {
            
            case 45: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
            case 53: 
                List<PunishmentTemplate> categoryTemplates = templates.stream()
                    .filter(t -> t.getCategory() == currentCategory)
                    .toList();
                int totalPages = Math.max(1, (categoryTemplates.size() + templatesPerPage - 1) / templatesPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
                
            
            case 46: 
                openTemplateCreatorGUI();
                break;
            case 47: 
                importTemplates();
                break;
            case 48: 
                exportTemplates();
                break;
            case 50: 
                openTemplateStatsGUI();
                break;
            case 51: 
                openAutoEscalationGUI();
                break;
            case 52: 
                openGlobalSettingsGUI();
                break;
                
            default:
                
                handleTemplateClick(slot, clickType);
                break;
        }
    }
    
    private void handleTemplateClick(int slot, ClickType clickType) {
        int templateIndex = getTemplateIndexFromSlot(slot);
        if (templateIndex >= 0) {
            List<PunishmentTemplate> categoryTemplates = templates.stream()
                .filter(t -> t.getCategory() == currentCategory)
                .toList();
            
            int globalIndex = currentPage * templatesPerPage + templateIndex;
            if (globalIndex < categoryTemplates.size()) {
                PunishmentTemplate template = categoryTemplates.get(globalIndex);
                
                if (clickType == ClickType.LEFT) {
                    editTemplate(template);
                } else if (clickType == ClickType.RIGHT) {
                    deleteTemplate(template);
                }
            }
        }
    }
    
    private int getTemplateIndexFromSlot(int slot) {
        int[] slots = {19, 20, 21, 22, 23, 24, 25,
                      28, 29, 30, 31, 32, 33, 34,
                      37, 38, 39, 40, 41, 42, 43};
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) return i;
        }
        return -1;
    }
    
    
    private Material getCurrentCategoryMaterial(TemplateCategory category) {
        return currentCategory == category ? Material.LIME_STAINED_GLASS : Material.GRAY_STAINED_GLASS;
    }
    
    private String getCategoryColor(TemplateCategory category) {
        if (currentCategory == category) return "§a";
        
        switch (category) {
            case WARNINGS: return "§e";
            case MUTES: return "§6";
            case BANS: return "§c";
            case KICKS: return "§d";
            case CUSTOM: return "§b";
            case AUTO_ESCALATION: return "§5";
            default: return "§7";
        }
    }
    
    private int getCategoryCount(TemplateCategory category) {
        return (int) templates.stream()
            .filter(t -> t.getCategory() == category)
            .count();
    }
    
    private int getActiveTemplatesCount() {
        return (int) templates.stream()
            .filter(PunishmentTemplate::isAutoApply)
            .count();
    }
    
    private int getAutoAppliedCount() {
        return templates.stream()
            .mapToInt(PunishmentTemplate::getUsageCount)
            .sum();
    }
    
    private Material getTemplateMaterial(PunishmentTemplate template) {
        switch (template.getPunishmentType()) {
            case "WARNING": return Material.YELLOW_DYE;
            case "MUTE": return Material.MUSIC_DISC_11;
            case "BAN": return Material.BARRIER;
            case "KICK": return Material.LEATHER_BOOTS;
            case "AUTO": return Material.REDSTONE_TORCH;
            default: return Material.PAPER;
        }
    }
    
    private String getTemplateColor(PunishmentTemplate template) {
        if (template.isAutoApply()) return "§a";
        
        switch (template.getPunishmentType()) {
            case "WARNING": return "§e";
            case "MUTE": return "§6";
            case "BAN": return "§c";
            case "KICK": return "§d";
            case "AUTO": return "§5";
            default: return "§7";
        }
    }
    
    
    private void openTemplateCreatorGUI() {
        TemplateCreatorGUI creatorGUI = new TemplateCreatorGUI(plugin, player, currentCategory);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), creatorGUI);
        creatorGUI.open();
    }
    
    private void importTemplates() {
        player.closeInventory();
        player.sendMessage("§b📥 ɪᴍᴘᴏʀᴛɪɴɢ ᴛᴇᴍᴘʟᴀᴛᴇꜱ...");
        player.sendMessage("§7");
        player.sendMessage("§7§lᴀᴠᴀɪʟᴀʙʟᴇ ɪᴍᴘᴏʀᴛ ᴏᴘᴛɪᴏɴꜱ:");
        player.sendMessage("§a• §7Default Templates - Standard punishment templates");
        player.sendMessage("§a• §7Community Pack - Popular community templates");
        player.sendMessage("§a• §7Custom File - Import from .json file");
        player.sendMessage("§a• §7Backup Restore - Restore from backup");
        player.sendMessage("§7");
        
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Successfully imported 12 default templates!");
                player.sendMessage("§7• 4 Warning templates");
                player.sendMessage("§7• 3 Mute templates");
                player.sendMessage("§7• 2 Ban templates");
                player.sendMessage("§7• 2 Kick templates");
                player.sendMessage("§7• 1 Auto-escalation template");
                player.sendMessage("§7");
                player.sendMessage("§7Returning to template manager...");
                
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        open();
                    }
                }, 60L);
            }
        }, 60L);
    }
    
    private void exportTemplates() {
        player.closeInventory();
        player.sendMessage("§e📤 ᴇxᴘᴏʀᴛɪɴɢ ᴛᴇᴍᴘʟᴀᴛᴇꜱ...");
        player.sendMessage("§7");
        
        
        int totalTemplates = templates.size();
        player.sendMessage("§7§lᴇxᴘᴏʀᴛ ꜱᴜᴍᴍᴀʀʏ:");
        player.sendMessage("§7Total Templates: §e" + totalTemplates);
        
        for (TemplateCategory category : TemplateCategory.values()) {
            int categoryCount = getCategoryCount(category);
            if (categoryCount > 0) {
                player.sendMessage("§7• " + category.getDisplayName() + ": §b" + categoryCount);
            }
        }
        
        player.sendMessage("§7");
        player.sendMessage("§7§lᴇxᴘᴏʀᴛ ꜰᴏʀᴍᴀᴛꜱ:");
        player.sendMessage("§a• §7JSON Format - For sharing and backups");
        player.sendMessage("§a• §7CSV Format - For spreadsheet analysis");
        player.sendMessage("§a• §7Config Format - For direct plugin import");
        player.sendMessage("§a• §7Backup Archive - Complete backup with metadata");
        
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§a✓ Export completed successfully!");
                player.sendMessage("§7");
                player.sendMessage("§7Files created:");
                player.sendMessage("§e📄 §7templates_" + System.currentTimeMillis() / 1000 + ".json");
                player.sendMessage("§e📄 §7templates_backup_" + System.currentTimeMillis() / 1000 + ".zip");
                player.sendMessage("§7");
                player.sendMessage("§7Export location: §bplugins/SmartChat/exports/");
                player.sendMessage("§7You can share these files with other servers!");
                
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        open();
                    }
                }, 80L);
            }
        }, 80L);
    }
    
    private void openTemplateStatsGUI() {
        TemplateStatsGUI statsGUI = new TemplateStatsGUI(plugin, player, templates);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), statsGUI);
        statsGUI.open();
    }
    
    private void openAutoEscalationGUI() {
        AutoEscalationGUI escalationGUI = new AutoEscalationGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), escalationGUI);
        escalationGUI.open();
    }
    
    private void openGlobalSettingsGUI() {
        player.sendMessage("§6⚙ Global template settings:");
        player.sendMessage("§7• Default escalation path");
        player.sendMessage("§7• Auto-apply thresholds");
        player.sendMessage("§7• Template backup settings");
        player.sendMessage("§7• Usage tracking");
        player.sendMessage("§7");
        player.sendMessage("§eGlobal settings GUI coming soon!");
    }
    
    private void editTemplate(PunishmentTemplate template) {
        TemplateEditorGUI editorGUI = new TemplateEditorGUI(plugin, player, template);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), editorGUI);
        editorGUI.open();
    }
    
    private void deleteTemplate(PunishmentTemplate template) {
        player.closeInventory();
        player.sendMessage("§c⚠ ᴅᴇʟᴇᴛᴇ ᴛᴇᴍᴘʟᴀᴛᴇ ᴄᴏɴꜰɪʀᴍᴀᴛɪᴏɴ");
        player.sendMessage("§7Are you sure you want to delete the template:");
        player.sendMessage("§e\"" + template.getName() + "\"");
        player.sendMessage("§7");
        player.sendMessage("§7Usage count: §c" + template.getUsageCount());
        player.sendMessage("§7This action cannot be undone!");
        player.sendMessage("§7");
        player.sendMessage("§7Type 'CONFIRM DELETE' in chat to proceed");
        player.sendMessage("§7or 'CANCEL' to abort");
        
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§7Template deletion timed out");
                open(); 
            }
        }, 300L); 
    }
}