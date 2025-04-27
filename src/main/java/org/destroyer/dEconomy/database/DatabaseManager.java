package org.destroyer.dEconomy.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.destroyer.dEconomy.Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Gerencia a conexão com o banco e aplica migrações SQL.
 */
public class DatabaseManager {

    private final FileConfiguration config;
    private HikariDataSource dataSource;

    public DatabaseManager() {
        config = Main.getInstance().getConfigManager().getConfig();
    }

    public void setup() {
        String dbType = Objects.requireNonNull(config.getString("database.type")).toLowerCase();
        String url, username, password, driver;
        switch (dbType) {
            case "mysql", "mariadb" -> {
                driver = dbType.equals("mysql")
                        ? "com.mysql.cj.jdbc.Driver"
                        : "org.mariadb.jdbc.Driver";
                url = String.format("jdbc:%s://%s:%s/%s",
                        dbType.equals("mysql") ? "mysql" : "mariadb",
                        config.getString("database.host"),
                        config.getString("database.port"),
                        config.getString("database.name"));
                username = config.getString("database.username");
                password = config.getString("database.password");
            }
            case "sqlite" -> {
                driver = "org.sqlite.JDBC";
                url = "jdbc:sqlite:" + Main.getInstance().getDataFolder().getAbsolutePath() + "/data.db";
                username = "";
                password = "";
            }
            default -> throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(driver + " not found");
        }

        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(url);
        hc.setUsername(username);
        hc.setPassword(password);
        hc.setDriverClassName(driver);
        hc.setMaximumPoolSize(10);
        hc.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(hc);

        applySqlMigrations(dbType);
    }

    private void applySqlMigrations(String dbType) {
        String folder = switch (dbType) {
            case "mysql", "mariadb" -> "migrations/mysql";
            case "sqlite" -> "migrations/sqlite";
            default -> throw new IllegalArgumentException("No migration folder for: " + dbType);
        };
        try (Connection conn = getConnection()) {
            MigrationManager mm = new MigrationManager(conn, folder, dbType);
            mm.applyMigrations();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply SQL migrations", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null) dataSource.close();
    }
}