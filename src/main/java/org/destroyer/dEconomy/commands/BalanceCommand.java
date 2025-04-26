package org.destroyer.dEconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.destroyer.dEconomy.models.Bank;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.models.Player;
import org.destroyer.dEconomy.repository.BankRepository;
import org.destroyer.dEconomy.repository.PlayerRepository;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static org.destroyer.dEconomy.enums.Messages.*;

@Command({ "balance", "bal", "carteira" })
@Usage("balance <playerName>")
@CommandPermission("deconomy.balance.command")
public class BalanceCommand {

    private final BankRepository bankRepository;
    private final PlayerRepository playerRepository;

    public BalanceCommand(BankRepository bankRepository, PlayerRepository playerRepository) {
        this.bankRepository = bankRepository;
        this.playerRepository = playerRepository;
    }

    @Command({ "balance", "bal", "carteira" })
    public void balance(BukkitCommandActor sender) throws SQLException {
        if (!sender.isPlayer()) {
            sender.error(NO_PERMISSION_CONSOLE.get());
            return;
        }

        Optional<Player> balance = playerRepository.getPlayer(sender.uniqueId());
        Optional<Bank> bank = bankRepository.getPlayerBankAccount(sender.uniqueId());

        sender.sendRawMessage(BALANCE_MESSAGE.get(Map.of(
                "bankBalance", String.valueOf(bank.map(Bank::balance).orElse(0L)),
                "balance", String.valueOf(balance.map(Player::onHandBalance).orElse(0L))
        )));
    }

    @Subcommand("<playerName>")
    @Usage("balance <playerName>")
    public void balanceWithArgs(BukkitCommandActor sender, @Named("playerName") @revxrsal.commands.annotation.Optional String playerName) throws SQLException {
        OfflinePlayer target = Bukkit.getPlayer(playerName);

        if (target == null) {
            sender.error(INVALID_PLAYER.get());
            return;
        }

        Optional<Player> balance = playerRepository.getPlayer(target.getUniqueId());
        Optional<Bank> bank = bankRepository.getPlayerBankAccount(target.getUniqueId());

        sender.sendRawMessage(BALANCE_OTHERPLAYER_MESSAGE.get(Map.of(
                "target", playerName,
                "bankBalance", String.valueOf(bank.map(Bank::balance).orElse(0L)),
                "balance", String.valueOf(balance.map(Player::onHandBalance).orElse(0L))
        )));
    }

    @Command("setbalance <player> <value> <type>")
    @Usage("setbalance <player> <value> <type>")
    @CommandPermission("deconomy.balance.admin")
    public void setBalance(BukkitCommandActor sender, @Named("player") String playerName, @Named("value") Long value, @Default("cash") @Named("type") String type) throws SQLException {
       if (playerName == null) {
           sender.error(INVALID_PLAYER.get());
           return;
       }

       if (!type.equalsIgnoreCase("bank") && !type.equalsIgnoreCase("cash")) {
           sender.error(INVALID_BALANCE_ARGUMENT.get());
           return;
       }

       OfflinePlayer target = Bukkit.getPlayer(playerName);

       if (target == null) {
           sender.error(INVALID_PLAYER.get());
           return;
       }

       Optional<Player> balance = playerRepository.getPlayer(target.getUniqueId());
       Optional<Bank> bank = bankRepository.getPlayerBankAccount(target.getUniqueId());

       if (type.equalsIgnoreCase("bank") && bank.isPresent()) {
           bankRepository.updateBankAccountBalance(target.getUniqueId(), value);
           sender.sendRawMessage(BANKBALANCE_CHANGED_ADMIN.get(Map.of("target", target.getName(), "money", String.valueOf(value))));
           return;
       }

       if (type.equalsIgnoreCase("cash") && balance.isPresent()) {
           playerRepository.updatePlayer(target.getUniqueId(), new PlayerDTO(target.getName(), balance.get().title(), value));
           sender.sendRawMessage(BALANCE_CHANGED_ADMIN.get(Map.of("target", target.getName(), "money", String.valueOf(value))));
           return;
       }

       sender.error(INVALID_PLAYER.get());
    }
}
