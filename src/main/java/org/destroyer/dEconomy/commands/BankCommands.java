package org.destroyer.dEconomy.commands;

import org.destroyer.dEconomy.models.Bank;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.models.Player;
import org.destroyer.dEconomy.repository.BankRepository;
import org.destroyer.dEconomy.repository.PlayerRepository;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static org.destroyer.dEconomy.enums.Messages.*;

@Command({"banco", "bank"})
@CommandPermission("deconomy.bank.command")
public class BankCommands {

    private final BankRepository bankRepository;
    private final PlayerRepository playerRepository;

    public BankCommands(BankRepository bankRepository, PlayerRepository playerRepository) {
        this.bankRepository = bankRepository;
        this.playerRepository = playerRepository;
    }

    @Subcommand({"withdraw"})
    @Usage("withdraw <money>")
    public void bankWithdraw(BukkitCommandActor sender, @Named("money") Long money) throws SQLException {
        if (!sender.isPlayer()) {
            sender.error(NO_PERMISSION_CONSOLE.get());
            return;
        }
        if (money == 0) {
            sender.error(INVALID_TRANSACTION_VALUE.get());
            return;
        }

        Optional<Player> balance = playerRepository.getPlayer(sender.uniqueId());
        Optional<Bank> bank = bankRepository.getPlayerBankAccount(sender.uniqueId());

        if (balance.isEmpty()) {
            sender.error(INVALID_PLAYER_ACCOUNT.get());
            return;
        }
        if (bank.isEmpty()) {
            sender.error(INVALID_PLAYER_BANKACCOUNT.get());
            return;
        }

        if (bank.get().balance() >= money) {
            Long newBankBalance = bank.get().balance() - money;
            Long newOnHandBalance = balance.get().onHandBalance() + money;

            bankRepository.updateBankAccountBalance(sender.uniqueId(), newBankBalance);
            playerRepository.updatePlayer(sender.uniqueId(), new PlayerDTO(sender.name(), balance.get().title(), newOnHandBalance));
            sender.sendRawMessage(WITHDRAW_SUCCESS.get(Map.of("amount", String.valueOf(money))));
        } else {
            sender.error(INSUFFICIENT_BALANCE.get());
        }
    }

    @Subcommand({"deposit"})
    @Usage("deposit <money>")
    public void bankDeposit(BukkitCommandActor sender, @Named("money") Long money) throws SQLException {
        if (!sender.isPlayer()) {
            sender.error(NO_PERMISSION_CONSOLE.get());
            return;
        }
        if (money == 0) {
            sender.error(INVALID_TRANSACTION_VALUE.get());
            return;
        }

        Optional<Player> balance = playerRepository.getPlayer(sender.uniqueId());
        Optional<Bank> bank = bankRepository.getPlayerBankAccount(sender.uniqueId());

        if (balance.isEmpty()) {
            sender.error(INVALID_PLAYER_ACCOUNT.get());
            return;
        }
        if (bank.isEmpty()) {
            sender.error(INVALID_PLAYER_BANKACCOUNT.get());
            return;
        }

        if (balance.get().onHandBalance() >= money) {
            Long newOnHandBalance = balance.get().onHandBalance() - money;
            Long newBankBalance = bank.get().balance() + money;

            playerRepository.updatePlayer(sender.uniqueId(), new PlayerDTO(sender.name(), balance.get().title(), newOnHandBalance));
            bankRepository.updateBankAccountBalance(sender.uniqueId(), newBankBalance);
            sender.sendRawMessage(DEPOSIT_SUCCESS.get(Map.of("amount", String.valueOf(money))));
        } else {
            sender.error(INSUFFICIENT_BALANCE.get());
        }
    }

    @Command({"banco", "bank"})
    public void help(BukkitCommandActor sender) {
        sender.sendRawMessage("§6§l=== dBank Help ===");
        sender.sendRawMessage("§eComandos principais:");
        sender.sendRawMessage("§7/bank help §8- §fMostra esta mensagem de ajuda");
        sender.sendRawMessage("§7/bank deposit <valor> §8- §fDeposita uma quantia no banco");
        sender.sendRawMessage("§7/bank withdraw <valor> §8- §fSaca uma quantia no banco");
    }
}
