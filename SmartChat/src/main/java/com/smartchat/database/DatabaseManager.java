package com.smartchat.database;

import com.smartchat.SmartChat;
import com.smartchat.models.*;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class DatabaseManager {
    
    private final SmartChat plugin;
    private Connection connection;
    private final String dbPath;
    
    public DatabaseManager(SmartChat plugin) {
        this.plugin = plugin;
        this.dbPath = plugin.getDataFolder() + File.separator + "smartchat.db";
    }
    
    public boolean initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            createTables();
            performMaintenance();
            
            plugin.getLogger().info("Database initialized successfully!");
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database", e);
            return false;
        }
    }
    
    private void createTables() throws SQLException {
        String[] tables = {
            
            "CREATE TABLE IF NOT EXISTS players (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "username VARCHAR(16) NOT NULL," +
                "first_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "total_messages INTEGER DEFAULT 0," +
                "flagged_messages INTEGER DEFAULT 0," +
                "violation_score DOUBLE DEFAULT 0.0" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS violations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "message TEXT NOT NULL," +
                "filtered_message TEXT," +
                "category VARCHAR(50) NOT NULL," +
                "severity VARCHAR(20) NOT NULL," +
                "confidence DOUBLE NOT NULL," +
                "action_taken VARCHAR(50)," +
                "server_context TEXT," +
                "FOREIGN KEY (player_uuid) REFERENCES players(uuid)" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS appeals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "violation_id INTEGER NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "reason TEXT NOT NULL," +
                "status VARCHAR(20) DEFAULT 'pending'," +
                "reviewed_by VARCHAR(36)," +
                "review_timestamp TIMESTAMP," +
                "review_notes TEXT," +
                "FOREIGN KEY (player_uuid) REFERENCES players(uuid)," +
                "FOREIGN KEY (violation_id) REFERENCES violations(id)" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS analytics (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date DATE NOT NULL," +
                "total_messages INTEGER DEFAULT 0," +
                "flagged_messages INTEGER DEFAULT 0," +
                "actions_taken INTEGER DEFAULT 0," +
                "unique_players INTEGER DEFAULT 0," +
                "category_stats TEXT," +
                "hourly_stats TEXT" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS config_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "config_data TEXT NOT NULL," +
                "changed_by VARCHAR(36)," +
                "change_notes TEXT" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS learning_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "context_key VARCHAR(100) NOT NULL," +
                "context_value TEXT NOT NULL," +
                "usage_count INTEGER DEFAULT 1," +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "UNIQUE(context_key)" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "type VARCHAR(20) NOT NULL," +
                "reason TEXT NOT NULL," +
                "start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "end_time TIMESTAMP," +
                "expires_at TIMESTAMP," +
                "issued_by VARCHAR(36)," +
                "active BOOLEAN DEFAULT 1," +
                "FOREIGN KEY (player_uuid) REFERENCES players(uuid)" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS mutes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "reason TEXT NOT NULL," +
                "muted_by VARCHAR(36)," +
                "muted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "expires_at TIMESTAMP," +
                "active BOOLEAN DEFAULT 1," +
                "FOREIGN KEY (player_uuid) REFERENCES players(uuid)" +
            ")",
            
            
            "CREATE TABLE IF NOT EXISTS bans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "reason TEXT NOT NULL," +
                "banned_by VARCHAR(36)," +
                "banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "expires_at TIMESTAMP," +
                "active BOOLEAN DEFAULT 1," +
                "FOREIGN KEY (player_uuid) REFERENCES players(uuid)" +
            ")"
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String table : tables) {
                stmt.execute(table);
            }
            
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_violations_player ON violations(player_uuid)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_violations_timestamp ON violations(timestamp)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_appeals_player ON appeals(player_uuid)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_appeals_status ON appeals(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_punishments_active ON punishments(player_uuid, active)");
        }
    }
    
    public CompletableFuture<Void> recordViolation(Violation violation) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO violations (player_uuid, message, filtered_message, category, " +
                        "severity, confidence, action_taken, server_context) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, violation.getPlayerUuid().toString());
                pstmt.setString(2, violation.getMessage());
                pstmt.setString(3, violation.getFilteredMessage());
                pstmt.setString(4, violation.getCategory());
                pstmt.setString(5, violation.getSeverity());
                pstmt.setDouble(6, violation.getConfidence());
                pstmt.setString(7, violation.getActionTaken());
                pstmt.setString(8, violation.getServerContext());
                
                pstmt.executeUpdate();
                
                
                
                
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to record violation", e);
            }
        });
    }
    
    public CompletableFuture<PlayerRecord> getPlayerRecord(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM players WHERE uuid = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new PlayerRecord(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("username"),
                        rs.getTimestamp("first_seen"),
                        rs.getTimestamp("last_seen"),
                        rs.getInt("total_messages"),
                        rs.getInt("flagged_messages"),
                        rs.getDouble("violation_score")
                    );
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get player record", e);
            }
            
            return null;
        });
    }
    
    
    public CompletableFuture<Integer> createAppeal(Appeal appeal) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO appeals (player_uuid, violation_id, reason) VALUES (?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, appeal.getPlayerUuid().toString());
                pstmt.setInt(2, appeal.getViolationId());
                pstmt.setString(3, appeal.getReason());
                
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create appeal", e);
            }
            
            return -1;
        });
    }
    
    public CompletableFuture<List<Appeal>> getPendingAppeals() {
        return CompletableFuture.supplyAsync(() -> {
            List<Appeal> appeals = new ArrayList<>();
            String sql = "SELECT a.*, p.username FROM appeals a " +
                        "JOIN players p ON a.player_uuid = p.uuid " +
                        "WHERE a.status = 'pending' ORDER BY a.timestamp DESC";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Appeal appeal = new Appeal(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getInt("violation_id"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("reason"),
                        rs.getString("status")
                    );
                    appeal.setPlayerName(rs.getString("username"));
                    appeals.add(appeal);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get pending appeals", e);
            }
            
            return appeals;
        });
    }
    
    public CompletableFuture<Void> resolveAppeal(int appealId, UUID reviewedBy, boolean approved, String notes) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE appeals SET status = ?, reviewed_by = ?, review_timestamp = CURRENT_TIMESTAMP, " +
                        "review_notes = ? WHERE id = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, approved ? "approved" : "denied");
                pstmt.setString(2, reviewedBy.toString());
                pstmt.setString(3, notes);
                pstmt.setInt(4, appealId);
                
                pstmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to resolve appeal", e);
            }
        });
    }
    
    public CompletableFuture<AnalyticsData> getAnalytics(java.util.Date date) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM analytics WHERE date = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(date.getTime()));
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new AnalyticsData(
                        rs.getDate("date"),
                        rs.getInt("total_messages"),
                        rs.getInt("flagged_messages"),
                        rs.getInt("actions_taken"),
                        rs.getInt("unique_players"),
                        rs.getString("category_stats"),
                        rs.getString("hourly_stats")
                    );
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get analytics", e);
            }
            
            return null;
        });
    }
    
    public CompletableFuture<Void> updateAnalytics(String category, boolean flagged, boolean actionTaken) {
        return CompletableFuture.runAsync(() -> {
            
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            
            String sql = "INSERT OR REPLACE INTO analytics (date, total_messages, flagged_messages, actions_taken) " +
                        "VALUES (?, " +
                        "COALESCE((SELECT total_messages FROM analytics WHERE date = ?) + 1, 1), " +
                        "COALESCE((SELECT flagged_messages FROM analytics WHERE date = ?) + ?, ?), " +
                        "COALESCE((SELECT actions_taken FROM analytics WHERE date = ?) + ?, ?))";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDate(1, today);
                pstmt.setDate(2, today);
                pstmt.setDate(3, today);
                pstmt.setInt(4, flagged ? 1 : 0);
                pstmt.setInt(5, flagged ? 1 : 0);
                pstmt.setDate(6, today);
                pstmt.setInt(7, actionTaken ? 1 : 0);
                pstmt.setInt(8, actionTaken ? 1 : 0);
                
                pstmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to update analytics", e);
            }
        });
    }
    
    public void updatePlayerStats(String uuid, String username, boolean flagged) {
        
        String insertSql = "INSERT OR IGNORE INTO players (uuid, username, total_messages, flagged_messages) VALUES (?, ?, 0, 0)";
        
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, uuid);
            insertStmt.setString(2, username);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to insert player record", e);
        }
        
        
        String updateSql = "UPDATE players SET " +
                          "total_messages = total_messages + 1, " +
                          "flagged_messages = flagged_messages + ?, " +
                          "last_seen = CURRENT_TIMESTAMP " +
                          "WHERE uuid = ?";
        
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setInt(1, flagged ? 1 : 0);
            updateStmt.setString(2, uuid);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update player stats", e);
        }
    }
    
    public CompletableFuture<Void> addPunishment(Punishment punishment) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO punishments (player_uuid, type, reason, end_time, issued_by) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, punishment.getPlayerUuid().toString());
                pstmt.setString(2, punishment.getType());
                pstmt.setString(3, punishment.getReason());
                pstmt.setTimestamp(4, punishment.getEndTime());
                pstmt.setString(5, punishment.getIssuedBy().toString());
                
                pstmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to add punishment", e);
            }
        });
    }
    
    
    public int getPendingAppealsCount() {
        String sql = "SELECT COUNT(*) FROM appeals WHERE status = 'pending'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Error getting pending appeals count", e);
        }
        return 0;
    }
    
    public int getTotalPlayersCount() {
        String sql = "SELECT COUNT(*) FROM players";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Error getting total players count", e);
        }
        return 0;
    }
    
    public int getTotalViolationsCount() {
        String sql = "SELECT COUNT(*) FROM violations";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Error getting total violations count", e);
        }
        return 0;
    }
    
    public CompletableFuture<List<Violation>> getRecentViolations(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<Violation> violations = new ArrayList<>();
            String sql = "SELECT v.*, p.username FROM violations v " +
                        "JOIN players p ON v.player_uuid = p.uuid " +
                        "ORDER BY v.timestamp DESC LIMIT ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, limit);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Violation violation = new Violation(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("message"),
                        rs.getString("category"),
                        rs.getDouble("confidence"),
                        rs.getString("severity"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("action_taken")
                    );
                    violation.setPlayerName(rs.getString("username"));
                    violations.add(violation);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get recent violations", e);
            }
            
            return violations;
        });
    }
    
    public CompletableFuture<List<Violation>> getPlayerViolations(UUID playerUuid, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<Violation> violations = new ArrayList<>();
            String sql = "SELECT v.*, p.username FROM violations v " +
                        "JOIN players p ON v.player_uuid = p.uuid " +
                        "WHERE v.player_uuid = ? ORDER BY v.timestamp DESC LIMIT ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, playerUuid.toString());
                pstmt.setInt(2, limit);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Violation violation = new Violation(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("message"),
                        rs.getString("category"),
                        rs.getDouble("confidence"),
                        rs.getString("severity"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("action_taken")
                    );
                    violation.setPlayerName(rs.getString("username"));
                    violations.add(violation);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get player violations", e);
            }
            
            return violations;
        });
    }
    
    public CompletableFuture<List<Violation>> getViolationsByCategory(String category, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<Violation> violations = new ArrayList<>();
            String sql = "SELECT v.*, p.username FROM violations v " +
                        "JOIN players p ON v.player_uuid = p.uuid " +
                        "WHERE v.category = ? ORDER BY v.timestamp DESC LIMIT ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, category);
                pstmt.setInt(2, limit);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Violation violation = new Violation(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("message"),
                        rs.getString("category"),
                        rs.getDouble("confidence"),
                        rs.getString("severity"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("action_taken")
                    );
                    violation.setPlayerName(rs.getString("username"));
                    violations.add(violation);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get violations by category", e);
            }
            
            return violations;
        });
    }
    
    public CompletableFuture<Map<String, Integer>> getViolationCategoryCounts() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> counts = new HashMap<>();
            String sql = "SELECT category, COUNT(*) as count FROM violations GROUP BY category ORDER BY count DESC";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    counts.put(rs.getString("category"), rs.getInt("count"));
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get violation category counts", e);
            }
            
            return counts;
        });
    }
    
    public CompletableFuture<Punishment> getActivePunishment(UUID uuid, String type) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM punishments WHERE player_uuid = ? AND type = ? AND active = 1 " +
                        "AND (end_time IS NULL OR end_time > CURRENT_TIMESTAMP)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setString(2, type);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new Punishment(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("type"),
                        rs.getString("reason"),
                        rs.getTimestamp("start_time"),
                        rs.getTimestamp("end_time"),
                        UUID.fromString(rs.getString("issued_by")),
                        rs.getBoolean("active")
                    );
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get active punishment", e);
            }
            
            return null;
        });
    }
    
    private void performMaintenance() {
        
        int violationRetention = plugin.getConfigManager().getConfig().getInt("database.retention.violations", 30);
        int appealRetention = plugin.getConfigManager().getConfig().getInt("database.retention.appeals", 90);
        
        String[] cleanupQueries = {
            "DELETE FROM violations WHERE timestamp < datetime('now', '-" + violationRetention + " days')",
            "DELETE FROM appeals WHERE timestamp < datetime('now', '-" + appealRetention + " days')",
            "UPDATE punishments SET active = 0 WHERE end_time < CURRENT_TIMESTAMP AND active = 1"
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String query : cleanupQueries) {
                stmt.execute(query);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to perform database maintenance", e);
        }
    }
    
    public CompletableFuture<Map<String, Integer>> getActivePunishmentCounts() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> counts = new HashMap<>();
            String sql = "SELECT type, COUNT(*) as count FROM punishments WHERE expires_at > datetime('now') OR expires_at IS NULL GROUP BY type";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    counts.put(rs.getString("type"), rs.getInt("count"));
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get punishment counts", e);
            }
            
            return counts;
        });
    }
    
    public CompletableFuture<Integer> removeAllActivePunishments(String type) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE punishments SET expires_at = datetime('now') WHERE type = ? AND (expires_at > datetime('now') OR expires_at IS NULL)";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, type);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to remove punishments", e);
                return 0;
            }
        });
    }
    
    public CompletableFuture<Boolean> clearPlayerHistory(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                
                String deleteViolations = "DELETE FROM violations WHERE player_uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deleteViolations)) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.executeUpdate();
                }
                
                
                String resetRecord = "UPDATE players SET total_messages = 0, flagged_messages = 0, " +
                    "violation_score = 0.0 WHERE uuid = ?";
                try (PreparedStatement stmt = connection.prepareStatement(resetRecord)) {
                    stmt.setString(1, playerUuid.toString());
                    stmt.executeUpdate();
                }
                
                return true;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to clear player history", e);
                return false;
            }
        });
    }
    
    public CompletableFuture<List<PlayerRecord>> getAllPlayerRecords() {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerRecord> playerRecords = new ArrayList<>();
            String sql = "SELECT uuid, username, first_seen, last_seen, total_messages, flagged_messages, violation_score " +
                        "FROM players ORDER BY violation_score DESC";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    PlayerRecord record = new PlayerRecord(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("username"),
                        rs.getTimestamp("first_seen"),
                        rs.getTimestamp("last_seen"),
                        rs.getInt("total_messages"),
                        rs.getInt("flagged_messages"),
                        rs.getDouble("violation_score")
                    );
                    playerRecords.add(record);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to get all player records", e);
            }
            
            return playerRecords;
        });
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to close database connection", e);
        }
    }
}