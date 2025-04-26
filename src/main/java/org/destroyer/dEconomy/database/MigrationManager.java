package org.destroyer.dEconomy.database;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MigrationManager {

    private final Connection connection;
    private final String migrationFolder;
    private final String dbType;
    private final String createSchemaTable;

    public MigrationManager(Connection connection, String migrationFolder, String dbType) throws SQLException {
        this.connection = connection;
        this.migrationFolder = migrationFolder;
        this.dbType = dbType;

        // Define criação da tabela de versionamento conforme o banco
        if (dbType.equals("sqlite")) {
            createSchemaTable = "CREATE TABLE IF NOT EXISTS schema_version (version TEXT PRIMARY KEY)";
        } else {
            createSchemaTable = "CREATE TABLE IF NOT EXISTS schema_version (version VARCHAR(50) PRIMARY KEY)";
        }

        try (PreparedStatement stmt = connection.prepareStatement(createSchemaTable)) {
            stmt.execute();
        }
    }

    public void applyMigrations() throws SQLException, IOException, URISyntaxException {
        URI uri = Objects.requireNonNull(getClass().getClassLoader().getResource(migrationFolder)).toURI();

        try (FileSystem fileSystem = (uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Map.of()) : null);
             Stream<Path> paths = Files.walk(Paths.get(uri))) {

            List<Path> migrationFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("." + dbType + ".sql")) // Filtra pelo tipo correto
                    .sorted()
                    .toList();

            for (Path migrationFile : migrationFiles) {
                String version = getMigrationVersion(migrationFile);
                if (!isMigrationApplied(version)) {
                    System.out.println("Aplicando migração: " + version);
                    applyMigration(migrationFile);
                    recordMigration(version);
                }
            }
        }
    }

    private boolean isMigrationApplied(String version) throws SQLException {
        String query = "SELECT COUNT(*) FROM schema_version WHERE version = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, version);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void applyMigration(Path migrationFile) throws SQLException, IOException {
        String sql = Files.readString(migrationFile);

        String[] statements = sql.split("(?<=;)(\\s*\\R)");

        connection.setAutoCommit(false);
        try (Statement stmt = connection.createStatement()) {
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void recordMigration(String version) throws SQLException {
        String insert = "INSERT INTO schema_version (version) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, version);
            stmt.executeUpdate();
        }
    }

    private String getMigrationVersion(Path migrationFile) {
        String filename = migrationFile.getFileName().toString();
        return filename.split("__")[0];
    }
}