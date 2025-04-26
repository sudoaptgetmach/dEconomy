package org.destroyer.dEconomy.models;

import java.time.LocalDateTime;
import java.util.UUID;

public record Transactions(Long id, UUID senderUUID, UUID receiverUUID, Long amount, LocalDateTime date) {
}
