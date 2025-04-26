package org.destroyer.dEconomy.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.destroyer.dEconomy.models.Bank;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.repository.BankRepository;
import org.destroyer.dEconomy.repository.PlayerRepository;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static org.destroyer.dEconomy.enums.Config.INITIAL_BALANCE;
import static org.destroyer.dEconomy.enums.Config.INITIAL_BANK_BALANCE;

public class EconomyListener implements Listener {

    private PlayerRepository playerRepository;
    private BankRepository bankRepository;

    public EconomyListener(PlayerRepository playerRepository, BankRepository bankRepository) {
        this.playerRepository = playerRepository;
        this.bankRepository = bankRepository;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        // pegar jogador, se for primeira vez ou se nao tiver perfil, criar
        Player player = e.getPlayer();
        Optional<org.destroyer.dEconomy.models.Player> profile = playerRepository.getPlayer(player.getUniqueId());
        Optional<Bank> bank = bankRepository.getPlayerBankAccount(player.getUniqueId());

        if (!player.hasPlayedBefore() || profile.isEmpty() && bank.isEmpty()) {
            playerRepository.addPlayer(new org.destroyer.dEconomy.models.Player(
                    player.getUniqueId(), player.getName(), "", Long.valueOf(INITIAL_BALANCE.get())
            ));
            bankRepository.createPlayerBankAccount(
                    new Bank(player.getUniqueId(), Long.valueOf(INITIAL_BANK_BALANCE.get()))
            );
        }

        if (profile.isPresent() && bank.isPresent()) {
            if (!Objects.equals(profile.get().name(), player.getName())) {
                playerRepository.updatePlayer(player.getUniqueId(), new PlayerDTO(player.getName(), profile.get().title(), profile.get().onHandBalance()));
            }
        }
    }
}
