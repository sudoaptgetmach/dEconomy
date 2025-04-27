package org.destroyer.dEconomy.commands;

import org.bukkit.entity.Player;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.models.DailyRewards;
import org.destroyer.dEconomy.repository.DailyRewardsRepository;
import org.destroyer.dEconomy.repository.PlayerRepository;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.destroyer.dEconomy.enums.Config.*;
import static org.destroyer.dEconomy.enums.Messages.*;

public class DailyRewardCommand {

    private final PlayerRepository playerRepository;
    private final DailyRewardsRepository dailyRewardsRepository;

    public DailyRewardCommand(PlayerRepository playerRepository, DailyRewardsRepository dailyRewardsRepository) {
        this.playerRepository = playerRepository;
        this.dailyRewardsRepository = dailyRewardsRepository;
    }

    @Command({"dailyreward", "recompensadiaria"})
    @CommandPermission("deconomy.dailyreward.command")
    public void dailyReward(BukkitCommandActor actor) throws SQLException {
        if (!actor.isPlayer()) {
            actor.error(NO_PERMISSION_CONSOLE.get());
            return;
        }

        Player sender = actor.asPlayer();
        Optional<org.destroyer.dEconomy.models.Player> playerOpt = playerRepository.getPlayer(sender.getUniqueId());

        if (playerOpt.isEmpty()) {
            actor.error(PLAYER_NOT_FOUND.get());
            return;
        }

        org.destroyer.dEconomy.models.Player player = playerOpt.get();

        dailyRewardsRepository.getDailyReward(sender.getUniqueId())
                .ifPresentOrElse(
                        dailyReward -> handleExistingReward(actor, sender, player, dailyReward),
                        () -> {
                            try {
                                claimReward(sender, player);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                actor.error(DAILY_REWARD_ERROR.get());
                            }
                        }
                );
    }

    private void handleExistingReward(BukkitCommandActor actor, Player sender, org.destroyer.dEconomy.models.Player player, DailyRewards dailyReward) {
        LocalDateTime lastClaimDate = dailyReward.last_claim_date();
        Duration duration = Duration.between(lastClaimDate, LocalDateTime.now());
        long hoursRemaining = Integer.parseInt(DAILY_REWARD_COOLDOWN.get()) - duration.toHours();

        if (hoursRemaining <= 0) {
            try {
                claimReward(sender, player);
            } catch (SQLException e) {
                e.printStackTrace();
                actor.error(DAILY_REWARD_ERROR.get());
            }
        } else {
            actor.error(DAILY_REWARD_ALREADY_CLAIMED.get(Map.of("hours", String.valueOf(hoursRemaining))));
        }
    }

    private void claimReward(Player sender, org.destroyer.dEconomy.models.Player player) throws SQLException {
        int min = Integer.parseInt(DAILY_REWARD_MIN.get());
        int max = Integer.parseInt(DAILY_REWARD_MAX.get()) + 1;
        int amount = ThreadLocalRandom.current().nextInt(min, max);

        long updatedOnHandBalance = player.onHandBalance() + amount;

        dailyRewardsRepository.addDailyRewards(new DailyRewards(sender.getUniqueId(), amount, LocalDateTime.now()));
        playerRepository.updatePlayer(sender.getUniqueId(), new PlayerDTO(sender.getName(), player.title(), updatedOnHandBalance));

        sender.sendMessage(DAILY_REWARD_CLAIMED.get(Map.of("amount", String.valueOf(amount))));
    }
}
