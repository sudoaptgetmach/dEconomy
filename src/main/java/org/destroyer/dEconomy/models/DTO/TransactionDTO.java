package org.destroyer.dEconomy.models.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(UUID senderUUID, UUID receiverUUID, Long amount, LocalDateTime date) {
}
