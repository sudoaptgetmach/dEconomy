package org.destroyer.dEconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.destroyer.dEconomy.models.Bank;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.models.DTO.TransactionDTO;
import org.destroyer.dEconomy.repository.BankRepository;
import org.destroyer.dEconomy.repository.PlayerRepository;
import org.destroyer.dEconomy.repository.TransactionsRepository;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.destroyer.dEconomy.enums.Messages.*;

public class PayCommand {

    private final PlayerRepository playerRepository;
    private final BankRepository bankRepository;
    private final TransactionsRepository transactionsRepository;

    public PayCommand(PlayerRepository playerRepository, BankRepository bankRepository, TransactionsRepository transactionsRepository) {
        this.playerRepository = playerRepository;
        this.bankRepository = bankRepository;
        this.transactionsRepository = transactionsRepository;
    }

    @Command({ "pay", "pagar" })
    @Usage("pay <name> <quantity>")
    @CommandPermission("deconomy.balance.command")
    public void pay(BukkitCommandActor actor, @Named("name") String playerName, @Named("quantity") Long quantity, @Named("type") @Default("cash") String type) throws SQLException {
        if (!actor.isPlayer()) { actor.error(NO_PERMISSION_CONSOLE.get()); return; }
        if (quantity.equals(0L)) { actor.error(INVALID_TRANSACTION_VALUE.get()); return; }

        Player payer = actor.asPlayer();
        OfflinePlayer receiver = Bukkit.getOfflinePlayer(playerName);

        if (payer.getName().equalsIgnoreCase(playerName) || !receiver.hasPlayedBefore()) { actor.error(INVALID_PLAYER.get()); return; }

        Optional<org.destroyer.dEconomy.models.Player> payerBalance = playerRepository.getPlayer(payer.getUniqueId());
        Optional<Bank> payerBankAccount = bankRepository.getPlayerBankAccount(payer.getUniqueId());

        Optional<org.destroyer.dEconomy.models.Player> receiverBalance = playerRepository.getPlayer(receiver.getUniqueId());

        switch (type) {
            case "cash" -> cashPayment(quantity, actor, payerBalance, receiverBalance);
            case "bank" -> bankPayment(quantity, actor, payerBankAccount, receiverBalance);
            default -> actor.error(INVALID_BALANCE_ARGUMENT.get());
        }
    }

    private void cashPayment(Long quantity, BukkitCommandActor actor, Optional<org.destroyer.dEconomy.models.Player> payerBalance, Optional<org.destroyer.dEconomy.models.Player> receiverBalance) throws SQLException {
        org.destroyer.dEconomy.models.Player payer = payerBalance.get();
        org.destroyer.dEconomy.models.Player receiver = receiverBalance.get();

        if (payer.onHandBalance() < quantity) {
            actor.error(INSUFFICIENT_BALANCE.get());
            return;
        }

        playerRepository.updatePlayer(payer.playerId(), new PlayerDTO(payer.name(), payer.title(), payer.onHandBalance() - quantity));
        playerRepository.updatePlayer(receiver.playerId(), new PlayerDTO(receiver.name(), receiver.title(), receiver.onHandBalance() + quantity));
        transactionsRepository.addTransaction(new TransactionDTO(payer.playerId(), receiver.playerId(), quantity, LocalDateTime.now()));

        notifyPlayers(actor.asPlayer(), receiver.playerId(), quantity);
    }

    private void bankPayment(Long quantity, BukkitCommandActor actor, Optional<Bank> payerBankAccount, Optional<org.destroyer.dEconomy.models.Player> receiverBalance) throws SQLException {
        Bank bank = payerBankAccount.get();
        org.destroyer.dEconomy.models.Player receiver = receiverBalance.get();

        if (bank.balance() < quantity) {
            actor.error(INSUFFICIENT_BALANCE.get());
            return;
        }

        bankRepository.updateBankAccountBalance(bank.userId(), bank.balance() - quantity);
        playerRepository.updatePlayer(receiver.playerId(), new PlayerDTO(receiver.name(), receiver.title(), receiver.onHandBalance() + quantity));
        transactionsRepository.addTransaction(new TransactionDTO(bank.userId(), receiver.playerId(), quantity, LocalDateTime.now()));

        notifyPlayers(actor.asPlayer(), receiver.playerId(), quantity);
    }

    private void notifyPlayers(Player payer, UUID receiverId, Long quantity) {
        Player receiverOnline = Bukkit.getPlayer(receiverId);
        OfflinePlayer receiverOffline = Bukkit.getOfflinePlayer(receiverId);

        if (receiverOnline != null) {
            receiverOnline.sendMessage(TRANSFER_RECEIVED.get(Map.of(
                    "player", payer.getName(),
                    "amount", String.valueOf(quantity)
            )));
        }

        if (receiverOffline.hasPlayedBefore()) {
            payer.sendMessage(TRANSFER_SUCCESS.get(Map.of(
                    "player", receiverOnline != null ? receiverOnline.getName() : Objects.requireNonNull(receiverOffline.getName()),
                    "amount", String.valueOf(quantity)
            )));
        }
    }
}
