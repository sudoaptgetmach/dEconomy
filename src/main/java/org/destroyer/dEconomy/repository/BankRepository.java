package org.destroyer.dEconomy.repository;

import org.destroyer.dEconomy.database.DatabaseManager;
import org.destroyer.dEconomy.models.Bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/*
Bank:
 userId BINARY(16) PRIMARY KEY,
 balance BIGINT DEFAULT 0 CHECK (balance >= 0)
*/
public class BankRepository {

    private final DatabaseManager databaseManager;

    public BankRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createPlayerBankAccount(Bank bankAccount) throws SQLException {
        String query = "INSERT INTO `Bank` (userId, balance) VALUES (?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(bankAccount.userId()));
            statement.setLong(2, bankAccount.balance());
            statement.executeUpdate();
        }
    }

    public Optional<Bank> getPlayerBankAccount(UUID userId) throws SQLException {
        String query = "SELECT * FROM `Bank` WHERE userId = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(userId));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Bank bank = new Bank(
                            UUID.fromString(resultSet.getString("userId")),
                            resultSet.getLong("balance")
                    );
                    return Optional.of(bank);
                }
            }
        }
        return Optional.empty();
    }

    public void updateBankAccountBalance(UUID userId, Long balance) throws SQLException {
        String query = "UPDATE `Bank` SET balance = ? WHERE userId = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, balance);
            statement.setString(2, String.valueOf(userId));
            statement.executeUpdate();
        }
    }

}
