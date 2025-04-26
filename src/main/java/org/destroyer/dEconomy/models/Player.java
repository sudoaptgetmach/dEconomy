package org.destroyer.dEconomy.models;

import java.util.UUID;

public record Player(UUID playerId, String name, String title, Long onHandBalance) {
}
