package org.destroyer.dEconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.destroyer.dEconomy.models.Transactions;
import org.destroyer.dEconomy.repository.TransactionsRepository;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.destroyer.dEconomy.enums.Messages.*;
import static org.destroyer.dEconomy.utils.PlaceholderUtils.getPlayerNameFromUUID;

public class TransactionsCommand {

    private final TransactionsRepository transactionsRepository;

    public TransactionsCommand(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @Command({ "transacoes", "transactions" })
    @CommandPermission("deconomy.transactions")
    public void transactions(BukkitCommandActor sender, @Optional @Named("name") String playerName) throws SQLException {
        if (!sender.isPlayer() && playerName == null) { sender.error(NO_PERMISSION_CONSOLE.get()); return; }
        if (playerName != null && !sender.isConsole() && !sender.asPlayer().hasPermission("deconomy.transactions.seeOther")) { sender.error(NO_PERMISSION.get()); return; }

        if (playerName != null) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore()) {
                sender.error(PLAYER_NOT_FOUND.get(Map.of("player", playerName)));
                return;
            }
            transactionsWithArgs(sender, target);
            return;
        }

        List<Transactions> transactions = transactionsRepository.getTransactions(sender.uniqueId());
        if (transactions.isEmpty()) { sender.error(NO_TRANSACTIONS.get(Map.of("player", sender.asPlayer().getName()))); return; }

        sendTransactionHistory(sender, transactions);
    }

    public void transactionsWithArgs(BukkitCommandActor sender, OfflinePlayer target) throws SQLException {
        List<Transactions> transactions = transactionsRepository.getTransactions(target.getUniqueId());
        if (transactions.isEmpty()) { sender.error(NO_TRANSACTIONS.get(Map.of("player", Objects.requireNonNull(target.getName())))); return; }

        sendTransactionHistory(sender, transactions);
    }

    private void sendTransactionHistory(BukkitCommandActor sender, List<Transactions> transactions) {
        sender.sendRawMessage(TRANSACTION_HISTORY_HEADER.get());
        for (Transactions transaction : transactions) {
            sender.sendRawMessage(TRANSACTION_HISTORY_ENTRY.get(Map.of(
                    "id", String.valueOf(transaction.id()),
                    "sender", getPlayerNameFromUUID(transaction.senderUUID()),
                    "receiver", getPlayerNameFromUUID(transaction.receiverUUID()),
                    "amount", String.valueOf(transaction.amount()),
                    "date", transaction.date().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            )));
        }
    }
}
