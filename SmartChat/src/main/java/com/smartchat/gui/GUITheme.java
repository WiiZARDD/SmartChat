package com.smartchat.gui;

import org.bukkit.Material;

public class GUITheme {
    
    
    public static final Material MAIN_BORDER = Material.GRAY_STAINED_GLASS_PANE;           
    public static final Material CONFIG_BORDER = Material.ORANGE_STAINED_GLASS_PANE;       
    public static final Material MONITOR_BORDER = Material.PURPLE_STAINED_GLASS_PANE;      
    public static final Material PLAYER_BORDER = Material.LIGHT_BLUE_STAINED_GLASS_PANE;   
    public static final Material PUNISHMENT_BORDER = Material.RED_STAINED_GLASS_PANE;      
    public static final Material APPEAL_BORDER = Material.YELLOW_STAINED_GLASS_PANE;       
    public static final Material WORLD_BORDER = Material.GREEN_STAINED_GLASS_PANE;         
    
    
    public static final String MAIN_COLOR = "§8";           
    public static final String CONFIG_COLOR = "§6";         
    public static final String MONITOR_COLOR = "§d";        
    public static final String PLAYER_COLOR = "§b";         
    public static final String PUNISHMENT_COLOR = "§c";     
    public static final String APPEAL_COLOR = "§e";         
    public static final String WORLD_COLOR = "§2";          
    
    
    public static final String SUCCESS_COLOR = "§a";        
    public static final String ERROR_COLOR = "§c";          
    public static final String WARNING_COLOR = "§6";        
    public static final String INFO_COLOR = "§7";           
    public static final String ACCENT_COLOR = "§f";         
    public static final String SECONDARY_COLOR = "§8";      
    
    
    public static final Material INFO_MATERIAL = Material.BOOK;
    public static final Material SUCCESS_MATERIAL = Material.EMERALD;
    public static final Material ERROR_MATERIAL = Material.REDSTONE;
    public static final Material WARNING_MATERIAL = Material.GOLD_INGOT;
    public static final Material CONFIG_MATERIAL = Material.ANVIL;
    public static final Material PLAYER_MATERIAL = Material.PLAYER_HEAD;
    public static final Material PUNISHMENT_MATERIAL = Material.IRON_SWORD;
    public static final Material MONITOR_MATERIAL = Material.CLOCK;
    
    public static Material getBorderMaterial(GUIType type) {
        switch (type) {
            case MAIN: return MAIN_BORDER;
            case CONFIG: return CONFIG_BORDER;
            case MONITOR: return MONITOR_BORDER;
            case PLAYER: return PLAYER_BORDER;
            case PUNISHMENT: return PUNISHMENT_BORDER;
            case APPEAL: return APPEAL_BORDER;
            case WORLD: return WORLD_BORDER;
            default: return MAIN_BORDER;
        }
    }
    
    public static String getThemeColor(GUIType type) {
        switch (type) {
            case MAIN: return MAIN_COLOR;
            case CONFIG: return CONFIG_COLOR;
            case MONITOR: return MONITOR_COLOR;
            case PLAYER: return PLAYER_COLOR;
            case PUNISHMENT: return PUNISHMENT_COLOR;
            case APPEAL: return APPEAL_COLOR;
            case WORLD: return WORLD_COLOR;
            default: return MAIN_COLOR;
        }
    }
    
    public enum GUIType {
        MAIN,           
        CONFIG,         
        MONITOR,        
        PLAYER,         
        PUNISHMENT,     
        APPEAL,         
        WORLD           
    }
}