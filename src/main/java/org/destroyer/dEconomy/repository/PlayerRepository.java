package org.destroyer.dEconomy.repository;

import org.destroyer.dEconomy.database.DatabaseManager;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.models.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/*
   playerId BINARY(16) PRIMARY KEY,
   name VARCHAR(255),
   title VARCHAR(255),
   onHandBalance BINARY(16)
*/
public class PlayerRepository {

    private final DatabaseManager databaseManager;

    public PlayerRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addPlayer(Player player) throws SQLException {
        String query = "INSERT INTO `Player` (playerId, name, title, onHandBalance) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(player.playerId()));
            statement.setString(2, player.name());
            statement.setString(3, player.title());
            statement.setLong(4, player.onHandBalance());
            statement.executeUpdate();
        }
    }

    public void updatePlayer(UUID playerId, PlayerDTO playerDTO) throws SQLException {
        String query = "UPDATE `Player` SET name = ?, title = ?, onHandBalance = ? WHERE playerId = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerDTO.name());
            statement.setString(2, playerDTO.title());
            statement.setLong(3, playerDTO.onHandBalance());
            statement.setString(4, String.valueOf(playerId));
            statement.executeUpdate();
        }
    }

    public void setTitle(UUID playerId, String title) throws SQLException {
        String query = "UPDATE `Player` SET title = ? WHERE playerId = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, String.valueOf(playerId));
            statement.executeUpdate();
        }
    }

    public Optional<Player> getPlayer(UUID playerId) throws SQLException {
        String query = "SELECT * FROM `Player` WHERE playerId = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(playerId));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Player player = new Player(
                            UUID.fromString(resultSet.getString("playerId")),
                            resultSet.getString("name"),
                            resultSet.getString("title"),
                            resultSet.getLong("onHandBalance")
                    );
                    return Optional.of(player);
                }
            }
        }
        return Optional.empty();
    }
}
