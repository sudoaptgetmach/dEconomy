package org.destroyer.dEconomy.services;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.destroyer.dEconomy.models.DTO.PlayerDTO;
import org.destroyer.dEconomy.repository.PlayerRepository;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.destroyer.dEconomy.enums.Messages.INSUFFICIENT_BALANCE;
import static org.destroyer.dEconomy.enums.Messages.PURCHASE_SUCCESS;

public class ShopService {

    private final PlayerRepository playerRepository;

    public ShopService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void buyItem(Player player, Integer itemLocation, ItemStack item) throws SQLException {
        switch (itemLocation) {
            case 10, 13 -> payment(player, 1000L, item);
            case 11, 12 -> payment(player, 4000L, item);
            default -> {
            }
        }
    }

    private void payment(Player player, Long price, ItemStack item) throws SQLException {
        Optional<org.destroyer.dEconomy.models.Player> payer = playerRepository.getPlayer(player.getUniqueId());

        if (payer.get().onHandBalance() < price) {
            player.sendMessage(INSUFFICIENT_BALANCE.get());
            return;
        }

        playerRepository.updatePlayer(payer.get().playerId(), new PlayerDTO(payer.get().name(), payer.get().title(), payer.get().onHandBalance() - price));
        player.getInventory().addItem(item);
        player.sendMessage(PURCHASE_SUCCESS.get(Map.of("item", Objects.requireNonNull(item.getItemMeta()).getDisplayName(), "amount", String.valueOf(price))));
    }
}
