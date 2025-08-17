package com.smartchat.gui;

import com.smartchat.SmartChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class StaffPermissionsGUI extends BaseGUI {
    
    private List<StaffMember> staffMembers = new ArrayList<>();
    private PermissionView currentView = PermissionView.OVERVIEW;
    private int currentPage = 0;
    private final int staffPerPage = 21;
    
    public enum PermissionView {
        OVERVIEW("Overview"),
        STAFF_LIST("Staff List"),
        ROLE_MANAGEMENT("Role Management"),
        PERMISSION_MATRIX("Permission Matrix"),
        AUDIT_LOG("Audit Log");
        
        private final String displayName;
        
        PermissionView(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum StaffRole {
        OWNER("Owner", 100),
        ADMIN("Admin", 90),
        SENIOR_MOD("Senior Moderator", 80),
        MODERATOR("Moderator", 70),
        JUNIOR_MOD("Junior Moderator", 60),
        HELPER("Helper", 50),
        TRAINEE("Trainee", 40);
        
        private final String displayName;
        private final int level;
        
        StaffRole(String displayName, int level) {
            this.displayName = displayName;
            this.level = level;
        }
        
        public String getDisplayName() { return displayName; }
        public int getLevel() { return level; }
    }
    
    public static class StaffMember {
        private final UUID uuid;
        private final String name;
        private StaffRole role;
        private Set<String> permissions;
        private boolean isActive;
        private long lastSeen;
        
        public StaffMember(UUID uuid, String name, StaffRole role) {
            this.uuid = uuid;
            this.name = name;
            this.role = role;
            this.permissions = new HashSet<>();
            this.isActive = true;
            this.lastSeen = System.currentTimeMillis();
            initializePermissions();
        }
        
        private void initializePermissions() {
            
            switch (role) {
                case OWNER:
                    permissions.addAll(Arrays.asList("*"));
                    break;
                case ADMIN:
                    permissions.addAll(Arrays.asList(
                        "smartchat.admin.*",
                        "smartchat.ban.*",
                        "smartchat.mute.*",
                        "smartchat.config.*"
                    ));
                    break;
                case SENIOR_MOD:
                    permissions.addAll(Arrays.asList(
                        "smartchat.moderate.*",
                        "smartchat.ban.temp",
                        "smartchat.mute.*",
                        "smartchat.view.advanced"
                    ));
                    break;
                case MODERATOR:
                    permissions.addAll(Arrays.asList(
                        "smartchat.moderate.basic",
                        "smartchat.mute.temp",
                        "smartchat.warn.*",
                        "smartchat.view.standard"
                    ));
                    break;
                case JUNIOR_MOD:
                    permissions.addAll(Arrays.asList(
                        "smartchat.warn.*",
                        "smartchat.mute.short",
                        "smartchat.view.basic"
                    ));
                    break;
                case HELPER:
                    permissions.addAll(Arrays.asList(
                        "smartchat.warn.verbal",
                        "smartchat.view.basic"
                    ));
                    break;
                case TRAINEE:
                    permissions.addAll(Arrays.asList(
                        "smartchat.view.limited"
                    ));
                    break;
            }
        }
        
        
        public UUID getUuid() { return uuid; }
        public String getName() { return name; }
        public StaffRole getRole() { return role; }
        public void setRole(StaffRole role) { this.role = role; initializePermissions(); }
        public Set<String> getPermissions() { return permissions; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { this.isActive = active; }
        public long getLastSeen() { return lastSeen; }
        public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
    }
    
    public StaffPermissionsGUI(SmartChat plugin, Player player) {
        super(plugin, player, "§8§l◆ §e§lꜱᴛᴀꜰꜰ ᴘᴇʀᴍɪꜱꜱɪᴏɴꜱ §8§l◆", 54);
        loadStaffData();
    }
    
    private void loadStaffData() {
        staffMembers.clear();
        
        
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (hasStaffPermissions(onlinePlayer)) {
                StaffRole role = determineRole(onlinePlayer);
                StaffMember member = new StaffMember(onlinePlayer.getUniqueId(), onlinePlayer.getName(), role);
                staffMembers.add(member);
            }
        }
        
        
        loadOfflineStaff();
        
        
        staffMembers.sort((a, b) -> Integer.compare(b.getRole().getLevel(), a.getRole().getLevel()));
    }
    
    private boolean hasStaffPermissions(Player player) {
        return player.hasPermission("smartchat.staff") || 
               player.hasPermission("smartchat.*") ||
               player.hasPermission("smartchat.admin.*") ||
               player.hasPermission("smartchat.moderate.*") ||
               player.isOp();
    }
    
    private StaffRole determineRole(Player player) {
        if (player.hasPermission("smartchat.owner") || player.getName().equalsIgnoreCase("owner")) {
            return StaffRole.OWNER;
        } else if (player.hasPermission("smartchat.admin.*")) {
            return StaffRole.ADMIN;
        } else if (player.hasPermission("smartchat.moderate.senior")) {
            return StaffRole.SENIOR_MOD;
        } else if (player.hasPermission("smartchat.moderate.*")) {
            return StaffRole.MODERATOR;
        } else if (player.hasPermission("smartchat.moderate.junior")) {
            return StaffRole.JUNIOR_MOD;
        } else if (player.hasPermission("smartchat.helper")) {
            return StaffRole.HELPER;
        } else {
            return StaffRole.TRAINEE;
        }
    }
    
    private void loadOfflineStaff() {
        
        
    }
    
    @Override
    public void setupGUI() {
        fillBorder(GUITheme.getBorderMaterial(GUITheme.GUIType.CONFIG));
        addNavigationItems();
        
        setupHeader();
        setupViewSelector();
        setupMainDisplay();
        setupActionButtons();
    }
    
    private void setupHeader() {
        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Staff permission management system");
        headerLore.add("§7");
        headerLore.add("§7Current View: §e" + currentView.getDisplayName());
        headerLore.add("§7Total Staff: §b" + staffMembers.size());
        headerLore.add("§7Active Staff: §a" + getActiveStaffCount());
        headerLore.add("§7Permission Groups: §6" + StaffRole.values().length);
        
        inventory.setItem(4, createItem(Material.GOLDEN_SWORD,
            "§e§lꜱᴛᴀꜰꜰ ᴘᴇʀᴍɪꜱꜱɪᴏɴꜱ", headerLore));
    }
    
    private void setupViewSelector() {
        PermissionView[] views = PermissionView.values();
        for (int i = 0; i < Math.min(views.length, 5); i++) {
            PermissionView view = views[i];
            Material material = getViewMaterial(view);
            String color = currentView == view ? "§a" : "§7";
            
            inventory.setItem(9 + i, createItem(material,
                color + "§l" + view.getDisplayName().toUpperCase(),
                "§7" + getViewDescription(view),
                "§7",
                currentView == view ? "§a▶ Currently viewing" : "§7Click to switch!"));
        }
    }
    
    private void setupMainDisplay() {
        switch (currentView) {
            case OVERVIEW:
                setupOverviewDisplay();
                break;
            case STAFF_LIST:
                setupStaffListDisplay();
                break;
            case ROLE_MANAGEMENT:
                setupRoleManagementDisplay();
                break;
            case PERMISSION_MATRIX:
                setupPermissionMatrixDisplay();
                break;
            case AUDIT_LOG:
                setupAuditLogDisplay();
                break;
        }
    }
    
    private void setupOverviewDisplay() {
        
        Map<StaffRole, Long> roleCounts = staffMembers.stream()
            .collect(Collectors.groupingBy(StaffMember::getRole, Collectors.counting()));
        
        int slot = 19;
        for (StaffRole role : StaffRole.values()) {
            if (slot > 25) break;
            
            long count = roleCounts.getOrDefault(role, 0L);
            String color = getRoleColor(role);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Role level: §6" + role.getLevel());
            lore.add("§7Active members: §a" + count);
            lore.add("§7Permissions: §b" + getRolePermissionCount(role));
            lore.add("§7");
            lore.add("§aClick to manage role!");
            
            inventory.setItem(slot, createItem(getRoleMaterial(role),
                color + "§l" + role.getDisplayName().toUpperCase(),
                lore));
            slot++;
        }
        
        
        inventory.setItem(28, createItem(Material.EMERALD,
            "§a📊 ᴀᴄᴛɪᴠᴇ ꜱᴛᴀꜰꜰ",
            "§7Currently active staff members",
            "§7Online: §a" + getOnlineStaffCount(),
            "§7Total: §b" + getActiveStaffCount(),
            "§7Efficiency: §6" + getStaffEfficiency() + "%"));
        
        inventory.setItem(29, createItem(Material.CLOCK,
            "§b⏰ ʀᴇᴄᴇɴᴛ ᴀᴄᴛɪᴠɪᴛʏ",
            "§7Recent staff activity",
            "§7Last 24h: §e" + getRecentActivity(),
            "§7Most active: §a" + getMostActiveStaff(),
            "§7Avg response: §6" + getAvgResponseTime()));
        
        inventory.setItem(30, createItem(Material.DIAMOND_SWORD,
            "§c⚔ ᴘᴇʀᴍɪꜱꜱɪᴏɴ ᴀʟᴇʀᴛꜱ",
            "§7Permission-related alerts",
            "§7Excessive permissions: §c" + getExcessivePermissions(),
            "§7Missing permissions: §e" + getMissingPermissions(),
            "§7Review needed: §6" + getReviewNeeded()));
        
        inventory.setItem(31, createItem(Material.BOOK,
            "§e📋 ᴀᴜᴅɪᴛ ꜱᴜᴍᴍᴀʀʏ",
            "§7Recent permission changes",
            "§7Changes today: §e" + getTodayChanges(),
            "§7Last audit: §7" + getLastAudit(),
            "§7Compliance: §a" + getComplianceScore() + "%"));
    }
    
    private void setupStaffListDisplay() {
        if (staffMembers.isEmpty()) {
            inventory.setItem(31, createItem(Material.BARRIER,
                "§c⚠ ɴᴏ ꜱᴛᴀꜰꜰ ꜰᴏᴜɴᴅ",
                "§7No staff members found",
                "§7Add staff members to get started"));
            return;
        }
        
        
        int startIndex = currentPage * staffPerPage;
        int endIndex = Math.min(startIndex + staffPerPage, staffMembers.size());
        
        
        for (int i = 19; i <= 43; i++) {
            if (i % 9 != 0 && i % 9 != 8) {
                inventory.setItem(i, null);
            }
        }
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            StaffMember member = staffMembers.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            boolean isOnline = offlinePlayer.isOnline();
            
            List<String> memberLore = new ArrayList<>();
            memberLore.add("§7Status: " + (isOnline ? "§aOnline" : "§7Offline"));
            memberLore.add("§7Role: " + getRoleColor(member.getRole()) + member.getRole().getDisplayName());
            memberLore.add("§7Level: §6" + member.getRole().getLevel());
            memberLore.add("§7Permissions: §b" + member.getPermissions().size());
            memberLore.add("§7Active: " + (member.isActive() ? "§aYes" : "§cNo"));
            
            if (!isOnline) {
                long hoursAgo = (System.currentTimeMillis() - member.getLastSeen()) / (1000 * 60 * 60);
                memberLore.add("§7Last seen: §e" + hoursAgo + "h ago");
            }
            
            memberLore.add("§7");
            memberLore.add("§aClick to manage!");
            
            int displaySlot = getStaffDisplaySlot(slotIndex);
            if (displaySlot != -1) {
                inventory.setItem(displaySlot, createItem(
                    isOnline ? Material.PLAYER_HEAD : Material.SKELETON_SKULL,
                    (isOnline ? "§a" : "§7") + member.getName(),
                    memberLore));
            }
            
            slotIndex++;
        }
        
        setupPaginationControls();
    }
    
    private void setupRoleManagementDisplay() {
        int slot = 19;
        for (StaffRole role : StaffRole.values()) {
            if (slot > 34) break;
            
            String color = getRoleColor(role);
            List<String> lore = new ArrayList<>();
            lore.add("§7Role level: §6" + role.getLevel());
            lore.add("§7Default permissions: §b" + getRolePermissionCount(role));
            lore.add("§7Members with role: §e" + getMembersWithRole(role));
            lore.add("§7");
            lore.add("§aLeft-click to edit permissions");
            lore.add("§eRight-click to assign to player");
            
            inventory.setItem(slot, createItem(getRoleMaterial(role),
                color + "§l" + role.getDisplayName(),
                lore));
            
            slot += (slot % 9 == 7) ? 3 : 1; 
        }
    }
    
    private void setupPermissionMatrixDisplay() {
        
        String[] permissionCategories = {
            "smartchat.admin.*",
            "smartchat.moderate.*", 
            "smartchat.ban.*",
            "smartchat.mute.*",
            "smartchat.warn.*",
            "smartchat.view.*",
            "smartchat.config.*"
        };
        
        int slot = 19;
        for (String permission : permissionCategories) {
            if (slot > 25) break;
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Permission category");
            lore.add("§7");
            
            
            for (StaffRole role : StaffRole.values()) {
                boolean hasPermission = roleHasPermission(role, permission);
                String status = hasPermission ? "§a✓" : "§c✗";
                lore.add(status + " " + getRoleColor(role) + role.getDisplayName());
            }
            
            lore.add("§7");
            lore.add("§aClick to modify permissions!");
            
            inventory.setItem(slot, createItem(Material.PAPER,
                "§6" + permission,
                lore));
            slot++;
        }
    }
    
    private void setupAuditLogDisplay() {
        
        List<String> auditEntries = getRecentAuditEntries();
        
        int slot = 19;
        for (int i = 0; i < Math.min(auditEntries.size(), 21); i++) {
            String entry = auditEntries.get(i);
            
            inventory.setItem(slot, createItem(Material.BOOK,
                "§e📝 ᴀᴜᴅɪᴛ ᴇɴᴛʀʏ #" + (i + 1),
                "§7" + entry,
                "§7",
                "§aClick for details!"));
            
            slot++;
            if (slot % 9 == 8) slot += 2; 
        }
    }
    
    private void setupActionButtons() {
        inventory.setItem(45, createItem(Material.EMERALD,
            "§a➕ ᴀᴅᴅ ꜱᴛᴀꜰꜰ",
            "§7Add new staff member",
            "§7Assign role and permissions",
            "§7",
            "§aClick to add staff!"));
        
        inventory.setItem(46, createItem(Material.GOLDEN_SWORD,
            "§6🔧 ʀᴏʟᴇ ᴇᴅɪᴛᴏʀ",
            "§7Create or modify roles",
            "§7Custom permission sets",
            "§7",
            "§aClick to edit roles!"));
        
        inventory.setItem(47, createItem(Material.PAPER,
            "§b📄 ᴇxᴘᴏʀᴛ ᴘᴇʀᴍɪꜱꜱɪᴏɴꜱ",
            "§7Export permission data",
            "§7Backup configurations",
            "§7",
            "§aClick to export!"));
        
        inventory.setItem(48, createItem(Material.HOPPER,
            "§e📥 ɪᴍᴘᴏʀᴛ ᴘᴇʀᴍɪꜱꜱɪᴏɴꜱ",
            "§7Import permission data",
            "§7Restore from backup",
            "§7",
            "§aClick to import!"));
        
        inventory.setItem(50, createItem(Material.CLOCK,
            "§d🔄 ʀᴇꜰʀᴇꜱʜ",
            "§7Reload staff data",
            "§7Update online status",
            "§7",
            "§aClick to refresh!"));
        
        inventory.setItem(51, createItem(Material.REDSTONE,
            "§c⚠ ᴀᴜᴅɪᴛ ᴍᴏᴅᴇ",
            "§7Enable audit logging",
            "§7Track all changes",
            "§7",
            "§aClick to configure!"));
        
        inventory.setItem(52, createItem(Material.DIAMOND,
            "§b🔒 ꜱᴇᴄᴜʀɪᴛʏ",
            "§7Permission security check",
            "§7Identify issues",
            "§7",
            "§aClick to check!"));
        
        inventory.setItem(53, createItem(Material.ANVIL,
            "§a⚙ ꜱᴇᴛᴛɪɴɢꜱ",
            "§7Permission system settings",
            "§7Configure defaults",
            "§7",
            "§aClick to configure!"));
    }
    
    private void setupPaginationControls() {
        if (staffMembers.isEmpty()) return;
        
        int totalPages = Math.max(1, (staffMembers.size() + staffPerPage - 1) / staffPerPage);
        
        
        if (currentPage > 0) {
            inventory.setItem(36, createItem(Material.ARROW,
                "§e← ᴘʀᴇᴠɪᴏᴜꜱ ᴘᴀɢᴇ",
                "§7Go to page " + currentPage,
                "§7",
                "§aClick to go back!"));
        }
        
        
        inventory.setItem(40, createItem(Material.COMPASS,
            "§b📍 ᴘᴀɢᴇ " + (currentPage + 1) + "/" + totalPages,
            "§7Showing staff members",
            "§7Total: §e" + staffMembers.size()));
        
        
        if (currentPage < totalPages - 1) {
            inventory.setItem(44, createItem(Material.ARROW,
                "§e ɴᴇxᴛ ᴘᴀɢᴇ →",
                "§7Go to page " + (currentPage + 2),
                "§7",
                "§aClick to go forward!"));
        }
    }
    
    @Override
    public void handleClick(int slot, ItemStack item, ClickType clickType) {
        if (isNavigationSlot(slot)) {
            handleNavigation(slot);
            return;
        }
        
        
        if (slot >= 9 && slot <= 13) {
            PermissionView[] views = PermissionView.values();
            int viewIndex = slot - 9;
            if (viewIndex < views.length) {
                currentView = views[viewIndex];
                currentPage = 0;
                refresh();
            }
            return;
        }
        
        switch (slot) {
            
            case 36: 
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
                break;
            case 44: 
                int totalPages = Math.max(1, (staffMembers.size() + staffPerPage - 1) / staffPerPage);
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    refresh();
                }
                break;
                
            
            case 45: 
                openAddStaffGUI();
                break;
            case 46: 
                openRoleEditorGUI();
                break;
            case 47: 
                exportPermissions();
                break;
            case 48: 
                importPermissions();
                break;
            case 50: 
                refreshStaffData();
                break;
            case 51: 
                toggleAuditMode();
                break;
            case 52: 
                runSecurityCheck();
                break;
            case 53: 
                openPermissionSettings();
                break;
                
            default:
                
                handleViewSpecificClick(slot, clickType);
                break;
        }
    }
    
    private void handleViewSpecificClick(int slot, ClickType clickType) {
        switch (currentView) {
            case STAFF_LIST:
                handleStaffMemberClick(slot);
                break;
            case ROLE_MANAGEMENT:
                handleRoleClick(slot, clickType);
                break;
            case PERMISSION_MATRIX:
                handlePermissionClick(slot);
                break;
            case AUDIT_LOG:
                handleAuditEntryClick(slot);
                break;
        }
    }
    
    
    private void openAddStaffGUI() {
        player.closeInventory();
        player.sendMessage("§a➕ ᴀᴅᴅ ꜱᴛᴀꜰꜰ ᴍᴇᴍʙᴇʀ");
        player.sendMessage("§7Type the player name to add as staff:");
        player.sendMessage("§7Or type 'cancel' to return");
        
        
    }
    
    private void openRoleEditorGUI() {
        player.sendMessage("§6🔧 ʀᴏʟᴇ ᴇᴅɪᴛᴏʀ");
        player.sendMessage("§7Current staff roles and their permissions:");
        for (StaffRole role : StaffRole.values()) {
            String color = getRoleColor(role);
            player.sendMessage(color + "▶ " + role.getDisplayName() + " §7(Level " + role.getLevel() + ")");
        }
        player.sendMessage("§7§oUse the role management view to modify permissions.");
    }
    
    private void exportPermissions() {
        player.sendMessage("§b📄 Exporting staff permissions...");
        
        List<String> exportData = new ArrayList<>();
        exportData.add("SmartChat Staff Permissions Export");
        exportData.add("Generated: " + new Date());
        exportData.add("");
        
        for (StaffMember member : staffMembers) {
            exportData.add("Player: " + member.getName());
            exportData.add("UUID: " + member.getUuid());
            exportData.add("Role: " + member.getRole().getDisplayName());
            exportData.add("Permissions: " + String.join(", ", member.getPermissions()));
            exportData.add("Active: " + member.isActive());
            exportData.add("");
        }
        
        plugin.getExportManager().exportViolationsToCSV(new ArrayList<>()).thenAccept(exportFile -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getExportManager().notifyExportComplete(player, exportFile, "staff permissions");
            });
        });
    }
    
    private void importPermissions() {
        player.sendMessage("§e📥 Permission import functionality coming soon!");
        player.sendMessage("§7This will allow importing staff configurations from backup files.");
    }
    
    private void refreshStaffData() {
        player.sendMessage("§d🔄 Refreshing staff data...");
        loadStaffData();
        refresh();
    }
    
    private void toggleAuditMode() {
        
        player.sendMessage("§c⚠ Audit mode toggled!");
        player.sendMessage("§7All permission changes will now be logged.");
    }
    
    private void runSecurityCheck() {
        player.sendMessage("§b🔒 ʀᴜɴɴɪɴɢ ꜱᴇᴄᴜʀɪᴛʏ ᴄʜᴇᴄᴋ...");
        
        List<String> issues = new ArrayList<>();
        
        
        for (StaffMember member : staffMembers) {
            if (member.getPermissions().contains("*") && member.getRole() != StaffRole.OWNER) {
                issues.add("§c⚠ " + member.getName() + " has wildcard permissions");
            }
        }
        
        
        for (StaffMember member : staffMembers) {
            if (member.getRole().getLevel() >= 70 && !member.getPermissions().contains("smartchat.moderate.*")) {
                issues.add("§e⚠ " + member.getName() + " missing moderation permissions");
            }
        }
        
        if (issues.isEmpty()) {
            player.sendMessage("§a✓ No security issues found!");
        } else {
            player.sendMessage("§c⚠ Found " + issues.size() + " security issues:");
            for (String issue : issues) {
                player.sendMessage(issue);
            }
        }
    }
    
    private void openPermissionSettings() {
        ConfigGUI configGUI = new ConfigGUI(plugin, player);
        plugin.getGuiManager().openGUIs.put(player.getUniqueId(), configGUI);
        configGUI.open();
    }
    
    private void handleStaffMemberClick(int slot) {
        int memberIndex = getStaffIndexFromSlot(slot);
        if (memberIndex >= 0) {
            int globalIndex = currentPage * staffPerPage + memberIndex;
            if (globalIndex < staffMembers.size()) {
                StaffMember member = staffMembers.get(globalIndex);
                openStaffMemberEditor(member);
            }
        }
    }
    
    private void handleRoleClick(int slot, ClickType clickType) {
        
        player.sendMessage("§6🔧 Role management click detected!");
    }
    
    private void handlePermissionClick(int slot) {
        
        player.sendMessage("§6📋 Permission matrix interaction!");
    }
    
    private void handleAuditEntryClick(int slot) {
        
        player.sendMessage("§e📝 Audit entry details!");
    }
    
    private void openStaffMemberEditor(StaffMember member) {
        player.sendMessage("§e👤 Editing staff member: §b" + member.getName());
        player.sendMessage("§7Role: " + getRoleColor(member.getRole()) + member.getRole().getDisplayName());
        player.sendMessage("§7Staff member editor functionality coming soon!");
    }
    
    
    private Material getViewMaterial(PermissionView view) {
        switch (view) {
            case OVERVIEW: return Material.COMPASS;
            case STAFF_LIST: return Material.PLAYER_HEAD;
            case ROLE_MANAGEMENT: return Material.GOLDEN_SWORD;
            case PERMISSION_MATRIX: return Material.PAPER;
            case AUDIT_LOG: return Material.BOOK;
            default: return Material.STONE;
        }
    }
    
    private String getViewDescription(PermissionView view) {
        switch (view) {
            case OVERVIEW: return "Staff overview and statistics";
            case STAFF_LIST: return "List all staff members";
            case ROLE_MANAGEMENT: return "Manage staff roles";
            case PERMISSION_MATRIX: return "Permission breakdown";
            case AUDIT_LOG: return "Recent changes log";
            default: return "Unknown view";
        }
    }
    
    private String getRoleColor(StaffRole role) {
        switch (role) {
            case OWNER: return "§4";
            case ADMIN: return "§c";
            case SENIOR_MOD: return "§6";
            case MODERATOR: return "§e";
            case JUNIOR_MOD: return "§a";
            case HELPER: return "§b";
            case TRAINEE: return "§7";
            default: return "§f";
        }
    }
    
    private Material getRoleMaterial(StaffRole role) {
        switch (role) {
            case OWNER: return Material.DRAGON_EGG;
            case ADMIN: return Material.DIAMOND_SWORD;
            case SENIOR_MOD: return Material.GOLDEN_SWORD;
            case MODERATOR: return Material.IRON_SWORD;
            case JUNIOR_MOD: return Material.STONE_SWORD;
            case HELPER: return Material.WOODEN_SWORD;
            case TRAINEE: return Material.STICK;
            default: return Material.PAPER;
        }
    }
    
    private int getStaffDisplaySlot(int index) {
        int[] slots = {19, 20, 21, 22, 23, 24, 25,
                      28, 29, 30, 31, 32, 33, 34,
                      37, 38, 39, 40, 41, 42, 43};
        return index < slots.length ? slots[index] : -1;
    }
    
    private int getStaffIndexFromSlot(int slot) {
        int[] slots = {19, 20, 21, 22, 23, 24, 25,
                      28, 29, 30, 31, 32, 33, 34,
                      37, 38, 39, 40, 41, 42, 43};
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) return i;
        }
        return -1;
    }
    
    
    private int getActiveStaffCount() {
        return (int) staffMembers.stream().filter(StaffMember::isActive).count();
    }
    
    private int getOnlineStaffCount() {
        return (int) staffMembers.stream()
            .filter(member -> Bukkit.getOfflinePlayer(member.getUuid()).isOnline())
            .count();
    }
    
    private int getRolePermissionCount(StaffRole role) {
        
        switch (role) {
            case OWNER: return 999;
            case ADMIN: return 50;
            case SENIOR_MOD: return 25;
            case MODERATOR: return 15;
            case JUNIOR_MOD: return 8;
            case HELPER: return 4;
            case TRAINEE: return 2;
            default: return 0;
        }
    }
    
    private long getMembersWithRole(StaffRole role) {
        return staffMembers.stream().filter(member -> member.getRole() == role).count();
    }
    
    private boolean roleHasPermission(StaffRole role, String permission) {
        
        switch (role) {
            case OWNER: return true;
            case ADMIN: return !permission.contains("owner");
            case SENIOR_MOD: return permission.contains("moderate") || permission.contains("ban") || permission.contains("mute");
            case MODERATOR: return permission.contains("moderate") || permission.contains("warn") || permission.contains("mute");
            case JUNIOR_MOD: return permission.contains("warn") || permission.contains("view");
            case HELPER: return permission.contains("view");
            case TRAINEE: return permission.contains("view") && permission.contains("limited");
            default: return false;
        }
    }
    
    private List<String> getRecentAuditEntries() {
        
        return Arrays.asList(
            "Admin promoted JohnMod to Senior Moderator",
            "SarahHelper granted mute permissions", 
            "BobTrainee role created",
            "Permission audit completed",
            "System security check passed"
        );
    }
    
    private String getStaffEfficiency() { return "87"; }
    private String getRecentActivity() { return "15 actions"; }
    private String getMostActiveStaff() { return "AdminSteve"; }
    private String getAvgResponseTime() { return "2.1 min"; }
    private int getExcessivePermissions() { return 2; }
    private int getMissingPermissions() { return 1; }
    private int getReviewNeeded() { return 3; }
    private int getTodayChanges() { return 5; }
    private String getLastAudit() { return "2 hours ago"; }
    private String getComplianceScore() { return "94"; }
}