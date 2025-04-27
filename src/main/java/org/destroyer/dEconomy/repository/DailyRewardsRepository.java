package org.destroyer.dEconomy.repository;

/*

    userId BINARY(16) PRIMARY KEY,
    amount INT,
    last_claim_date DATETIME

 */

import org.destroyer.dEconomy.database.DatabaseManager;
import org.destroyer.dEconomy.models.DailyRewards;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class DailyRewardsRepository {

    private final DatabaseManager databaseManager;

    public DailyRewardsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addDailyRewards(DailyRewards dailyRewards) throws SQLException {
        String query = "INSERT INTO `DailyRewards` (userId, amount, last_claim_date) VALUES (?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(dailyRewards.userId()));
            statement.setLong(2, dailyRewards.amount());
            statement.setTimestamp(3, Timestamp.valueOf(dailyRewards.last_claim_date()));
            statement.executeUpdate();
        }
    }

    public Optional<DailyRewards> getDailyReward(UUID userId) throws SQLException {
        String query = "SELECT * FROM `DailyRewards` WHERE userId = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(userId));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    DailyRewards dailyRewards = new DailyRewards(
                            UUID.fromString(resultSet.getString("userId")),
                            resultSet.getInt("amount"),
                            resultSet.getTimestamp("last_claim_date").toLocalDateTime()
                    );
                    return Optional.of(dailyRewards);
                }
            }
        }
        return Optional.empty();
    }
}
