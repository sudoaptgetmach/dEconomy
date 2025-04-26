package org.destroyer.dEconomy.models;

import java.time.LocalDateTime;
import java.util.UUID;

public record DailyRewards(UUID userId, Integer amount, LocalDateTime last_claim_date) {
}
