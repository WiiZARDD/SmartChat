package com.smartchat.gui;

import com.smartchat.SmartChat;
import com.smartchat.gui.PunishmentTemplatesGUI.TemplateCategory;
import com.smartchat.gui.PunishmentTemplatesGUI.PunishmentTemplate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateStatsGUI extends BaseGUI {
    
    private List<PunishmentTemplate> templates;
    private StatsViewType currentView = StatsViewType.OVERVIEW;
    
    public enum StatsViewType {
        OVERVIEW("Overview"),
        USAGE("Usage Statistics"),
        CATEGORIES("Category Breakdown"),
        EFFECTIVENESS("Effectiveness Analysis");
        
        private final String displayName;
        
        StatsViewType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public TemplateStatsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §d§lᴛᴇᴍᴘʟᴀᴛᴇ ꜱᴛᴀᴛꜱ §8§l◆", 45);
        this.templates = new ArrayList<>();
    }
    
    public TemplateStatsGUI(SmartChat plugin, Player player, Object templatesObj) {
        super(plugin, player, "§8§l◆ §d§lᴛᴇᴍᴘʟᴀᴛᴇ ꜱᴛᴀᴛꜱ §8§l◆", 45);
        if (templatesObj instanceof List) {
            this.templates = (List<PunishmentTemplate>) templatesObj;
        } else {
            this.templates = new ArrayList<>();
        }
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.MONITOR));
        addNavigationItems();
        
        setupHeader();
        setupViewSelector();
        setupStatsDisplay();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Template usage and performance analytics");
        headerLore.add("§7");
        headerLore.add("§7Total Templates: §e" + templates.size());
        headerLore.add("§7Active Templates: §a" + getActiveCount());
        headerLore.add("§7Total Usage: §b" + getTotalUsage());
        headerLore.add("§7Current View: §d" + currentView.getDisplayName());
        
        inventory.setItem(4, createItem(Material.MAP,
            "§d§lᴛᴇᴍᴘʟᴀᴛᴇ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ", headerLore));
    }
    
    private void setupViewSelector() {
        for (int i = 0; i < StatsViewType.values().length; i++) {
            StatsViewType viewType = StatsViewType.values()[i];
            boolean isSelected = currentView == viewType;
            
            inventory.setItem(9 + i, createItem(
                isSelected ? Material.LIME_STAINED_GLASS : Material.GRAY_STAINED_GLASS,
                (isSelected ? "§a" : "§7") + "📊 " + viewType.getDisplayName(),
                "§7View " + viewType.getDisplayName().toLowerCase(),
                "§7",
                isSelected ? "§a▶ Currently viewing" : "§7Click to view!"));
        }
    }
    
    private void setupStatsDisplay() {
        switch (currentView) {
            case OVERVIEW:
                setupOverviewStats();
                break;
            case USAGE:
                setupUsageStats();
                break;
            case CATEGORIES:
                setupCategoryStats();
                break;
            case EFFECTIVENESS:
                setupEffectivenessStats();
                break;
        }
    }
    
    private void setupOverviewStats() {
        PunishmentTemplate mostUsed = templates.stream()
            .max((a, b) -> Integer.compare(a.getUsageCount(), b.getUsageCount()))
            .orElse(null);
        
        if (mostUsed != null) {
            inventory.setItem(19, createItem(Material.GOLD_INGOT,
                "§e🏆 ᴍᴏꜱᴛ ᴜꜱᴇᴅ ᴛᴇᴍᴘʟᴀᴛᴇ",
                "§7Name: §f" + mostUsed.getName(),
                "§7Usage Count: §e" + mostUsed.getUsageCount(),
                "§7Type: §b" + mostUsed.getPunishmentType(),
                "§7Category: §d" + mostUsed.getCategory().getDisplayName()));
        }
        
        long autoApplyCount = templates.stream().filter(PunishmentTemplate::isAutoApply).count();
        inventory.setItem(20, createItem(Material.REDSTONE_TORCH,
            "§c⚡ ᴀᴜᴛᴏ-ᴀᴘᴘʟʏ ꜱᴛᴀᴛꜱ",
            "§7Auto-Apply Templates: §e" + autoApplyCount + "/" + templates.size(),
            "§7Percentage: §b" + String.format("%.1f%%", 
                templates.size() > 0 ? (double) autoApplyCount / templates.size() * 100 : 0),
            "§7Total Auto-Actions: §a" + getAutoApplyUsage()));
        
        inventory.setItem(21, createItem(Material.DIAMOND,
            "§b💎 ᴛᴇᴍᴘʟᴀᴛᴇ ᴇꜰꜰɪᴄɪᴇɴᴄʏ",
            "§7Average Usage: §e" + String.format("%.1f", getAverageUsage()),
            "§7Most Effective: §a" + (mostUsed != null ? mostUsed.getName() : "None"),
            "§7Least Used: §c" + getLeastUsedTemplate()));
    }
    
    private void setupUsageStats() {
        List<PunishmentTemplate> sortedByUsage = templates.stream()
            .sorted((a, b) -> Integer.compare(b.getUsageCount(), a.getUsageCount()))
            .limit(5)
            .collect(Collectors.toList());
        
        for (int i = 0; i < Math.min(5, sortedByUsage.size()); i++) {
            PunishmentTemplate template = sortedByUsage.get(i);
            inventory.setItem(19 + i, createItem(getMaterialForRank(i),
                "§e#" + (i + 1) + " §f" + template.getName(),
                "§7Usage Count: §b" + template.getUsageCount(),
                "§7Type: §e" + template.getPunishmentType(),
                "§7Auto-Apply: " + (template.isAutoApply() ? "§aYes" : "§cNo"),
                "§7Category: §d" + template.getCategory().getDisplayName()));
        }
    }
    
    private void setupCategoryStats() {
        Map<TemplateCategory, Long> categoryUsage = templates.stream()
            .collect(Collectors.groupingBy(PunishmentTemplate::getCategory, 
                Collectors.summingLong(PunishmentTemplate::getUsageCount)));
        
        int slot = 19;
        for (TemplateCategory category : TemplateCategory.values()) {
            long usage = categoryUsage.getOrDefault(category, 0L);
            long count = templates.stream().filter(t -> t.getCategory() == category).count();
            
            if (count > 0) {
                inventory.setItem(slot++, createItem(getCategoryMaterial(category),
                    getCategoryColor(category) + category.getDisplayName(),
                    "§7Templates: §e" + count,
                    "§7Total Usage: §b" + usage,
                    "§7Average Usage: §a" + String.format("%.1f", count > 0 ? (double) usage / count : 0),
                    "§7Most Used: §f" + getMostUsedInCategory(category)));
            }
        }
    }
    
    private void setupEffectivenessStats() {
        inventory.setItem(19, createItem(Material.EMERALD,
            "§a📈 ᴇꜰꜰᴇᴄᴛɪᴠᴇɴᴇꜱꜱ ᴀɴᴀʟʏꜱɪꜱ",
            "§7Template effectiveness coming soon!",
            "§7This will show recidivism rates"));
        
        inventory.setItem(20, createItem(Material.REDSTONE,
            "§c📉 ᴘᴇʀꜰᴏʀᴍᴀɴᴄᴇ ᴍᴇᴛʀɪᴄꜱ",
            "§7Performance analysis coming soon!",
            "§7Success rates and impact data"));
    }
    
    private void setupActionButtons() {
        inventory.setItem(37, createItem(Material.PAPER,
            "§e📤 ᴇxᴘᴏʀᴛ ꜱᴛᴀᴛꜱ",
            "§7Export statistics to file",
            "§7",
            "§aClick to export!"));
        
        inventory.setItem(38, createItem(Material.CLOCK,
            "§9🔄 ʀᴇꜰʀᴇꜱʜ ᴅᴀᴛᴀ",
            "§7Reload template statistics",
            "§7",
            "§aClick to refresh!"));
        
        inventory.setItem(40, createItem(Material.COMPASS,
            "§b🔍 ᴠɪᴇᴡ ꜱᴇʟᴇᴄᴛᴏʀ",
            "§7Change statistics view",
            "§7Current: §d" + currentView.getDisplayName(),
            "§7",
            "§aClick to cycle views!"));
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        if (slot >= 9 && slot <= 12) {
            StatsViewType[] views = StatsViewType.values();
            int viewIndex = slot - 9;
            if (viewIndex < views.length) {
                currentView = views[viewIndex];
                refresh();
            }
            return;
        }
        
        switch (slot) {
            case 37:
                exportStats();
                break;
            case 38:
                refresh();
                player.sendMessage("§a✓ Statistics refreshed!");
                break;
            case 40:
                cycleView();
                refresh();
                break;
        }
    }
    
    private void cycleView() {
        StatsViewType[] views = StatsViewType.values();
        int currentIndex = currentView.ordinal();
        currentView = views[(currentIndex + 1) % views.length];
    }
    
    private void exportStats() {
        player.sendMessage("§e📤 ᴇxᴘᴏʀᴛɪɴɢ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ...");
        player.sendMessage("§7");
        player.sendMessage("§7§lᴛᴇᴍᴘʟᴀᴛᴇ ꜱᴛᴀᴛɪꜱᴛɪᴄꜱ ʀᴇᴘᴏʀᴛ:");
        player.sendMessage("§7Total Templates: §e" + templates.size());
        player.sendMessage("§7Active Templates: §a" + getActiveCount());
        player.sendMessage("§7Total Usage: §b" + getTotalUsage());
        player.sendMessage("§7Average Usage: §d" + String.format("%.1f", getAverageUsage()));
        player.sendMessage("§7");
        player.sendMessage("§a✓ Full report exported to file!");
    }
    
    private int getActiveCount() {
        return (int) templates.stream().filter(PunishmentTemplate::isAutoApply).count();
    }
    
    private int getTotalUsage() {
        return templates.stream().mapToInt(PunishmentTemplate::getUsageCount).sum();
    }
    
    private double getAverageUsage() {
        return templates.isEmpty() ? 0 : (double) getTotalUsage() / templates.size();
    }
    
    private int getAutoApplyUsage() {
        return templates.stream()
            .filter(PunishmentTemplate::isAutoApply)
            .mapToInt(PunishmentTemplate::getUsageCount)
            .sum();
    }
    
    private String getLeastUsedTemplate() {
        return templates.stream()
            .min((a, b) -> Integer.compare(a.getUsageCount(), b.getUsageCount()))
            .map(PunishmentTemplate::getName)
            .orElse("None");
    }
    
    private String getMostUsedInCategory(TemplateCategory category) {
        return templates.stream()
            .filter(t -> t.getCategory() == category)
            .max((a, b) -> Integer.compare(a.getUsageCount(), b.getUsageCount()))
            .map(PunishmentTemplate::getName)
            .orElse("None");
    }
    
    private Material getMaterialForRank(int rank) {
        switch (rank) {
            case 0: return Material.GOLD_INGOT;
            case 1: return Material.IRON_INGOT;
            case 2: return Material.COPPER_INGOT;
            default: return Material.STONE;
        }
    }
    
    private Material getCategoryMaterial(TemplateCategory category) {
        switch (category) {
            case WARNINGS: return Material.YELLOW_DYE;
            case MUTES: return Material.MUSIC_DISC_11;
            case BANS: return Material.BARRIER;
            case KICKS: return Material.LEATHER_BOOTS;
            case CUSTOM: return Material.WRITABLE_BOOK;
            case AUTO_ESCALATION: return Material.REDSTONE_TORCH;
            default: return Material.PAPER;
        }
    }
    
    private String getCategoryColor(TemplateCategory category) {
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
}