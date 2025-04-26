package org.destroyer.dEconomy.repository;

import org.destroyer.dEconomy.database.DatabaseManager;
import org.destroyer.dEconomy.models.DTO.TransactionDTO;
import org.destroyer.dEconomy.models.Transactions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*

    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    senderUUID BINARY(16),
    receiverUUID BINARY(16),
    amount BIGINT CHECK (amount > 0),
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (senderUUID) REFERENCES Bank(userId),
    FOREIGN KEY (receiverUUID) REFERENCES Bank(userId)

 */
public class TransactionsRepository {

    private final DatabaseManager databaseManager;

    public TransactionsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addTransaction(TransactionDTO transactionDTO) throws SQLException {
        String query = "INSERT INTO `Bank` (senderUUID, receiverUUID, amount, date) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(transactionDTO.senderUUID()));
            statement.setString(2, String.valueOf(transactionDTO.receiverUUID()));
            statement.setLong(3, transactionDTO.amount());
            statement.setTimestamp(4, Timestamp.valueOf(transactionDTO.date()));
            statement.executeUpdate();
        }
    }

    public List<Transactions> getTransactions(UUID playerId) throws SQLException {
        List<Transactions> transactions = new ArrayList<>();
        String query = "SELECT * FROM `Bank` WHERE senderUUID = ? OR receiverUUID = ? ORDER BY date DESC";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, String.valueOf(playerId));
            statement.setString(2, String.valueOf(playerId));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transactions transaction = new Transactions(
                        resultSet.getLong("id"),
                        UUID.fromString(resultSet.getString("senderUUID")),
                        UUID.fromString(resultSet.getString("receiverUUID")),
                        resultSet.getLong("amount"),
                        resultSet.getTimestamp("date").toLocalDateTime()
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }
}
