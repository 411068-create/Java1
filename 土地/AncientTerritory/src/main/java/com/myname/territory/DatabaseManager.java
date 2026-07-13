package com.myname.territory;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private Connection connection;
    private final String host = "localhost";
    private final String port = "3306";
    private final String database = "minecraft_db";
    private final String username = "root";
    private final String password = "你的密碼"; // ⚠️ 請更換為你的 MySQL 密碼

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
            connection = DriverManager.getConnection(url, username, password);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS ancient_lands ("
                   + "land_id VARCHAR(64) PRIMARY KEY, "
                   + "land_type VARCHAR(32), "
                   + "admin_level VARCHAR(32), "
                   + "owner_uuid VARCHAR(36), "
                   + "is_bought_out BOOLEAN, "
                   + "unpaid_periods INT DEFAULT 0"
                   + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 儲存或更新土地地契
    public void saveLand(LandData land) {
        String sql = "INSERT INTO ancient_lands (land_id, land_type, admin_level, owner_uuid, is_bought_out, unpaid_periods) "
                   + "VALUES (?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE owner_uuid=?, is_bought_out=?, unpaid_periods=?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, land.getLandId());
            ps.setString(2, land.getLandType());
            ps.setString(3, land.getAdminLevel());
            ps.setString(4, land.getOwnerUUID() != null ? land.getOwnerUUID().toString() : null);
            ps.setBoolean(5, land.isBoughtOut());
            ps.setInt(6, land.getUnpaidPeriods());
            
            ps.setString(7, land.getOwnerUUID() != null ? land.getOwnerUUID().toString() : null);
            ps.setBoolean(8, land.isBoughtOut());
            ps.setInt(9, land.getUnpaidPeriods());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 從 MySQL 載入單塊土地資料
    public LandData loadLand(String landId) {
        String sql = "SELECT * FROM ancient_lands WHERE land_id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, landId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LandData land = new LandData(landId, rs.getString("land_type"), rs.getString("admin_level"));
                String uuidStr = rs.getString("owner_uuid");
                if (uuidStr != null) land.setOwnerUUID(UUID.fromString(uuidStr));
                land.setBoughtOut(rs.getBoolean("is_bought_out"));
                return land;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}