package org.destroyer.dEconomy.enums;

import org.bukkit.ChatColor;
import org.destroyer.dEconomy.manager.ConfigManager;
import org.destroyer.dEconomy.utils.PlaceholderUtils;

import java.util.Map;

public enum Messages {
    NO_PERMISSION("no_permission"),
    NO_PERMISSION_CONSOLE("no_permission_console"),
    INSUFFICIENT_BALANCE("insufficient_balance"),
    DAILY_REWARD_ALREADY_CLAIMED("daily_reward_already_claimed"),
    INVALID_PLAYER("invalid_player"),

    TRANSFER_SUCCESS("transfer_success"),
    DAILY_REWARD_CLAIMED("daily_reward_claimed"),
    BALANCE_CHANGED_ADMIN("balance_changed_admin"),
    BANKBALANCE_CHANGED_ADMIN("bankbalance_changed_admin"),
    INVALID_BALANCE_ARGUMENT("invalid_balance_argument"),

    TRANSACTION_HISTORY("transaction_history"),
    BALANCE_MESSAGE("balance_message"),
    BALANCE_OTHERPLAYER_MESSAGE("balance_other_player_message");

    private final String path;
    private static ConfigManager configManager;

    Messages(String path) {
        this.path = path;
    }

    public static void init(ConfigManager manager) {
        configManager = manager;
    }

    public String get() {
        if (configManager.getConfig().contains(path)) {
            String message = configManager.getConfig().getString(path);
            if (message != null) {
                return PlaceholderUtils.format(message);
            }
        }
        return ChatColor.RED + "Mensagem não encontrada: " + path;
    }

    public String get(Map<String, String> placeholders) {
        if (configManager.getConfig().contains(path)) {
            String message = configManager.getConfig().getString(path);
            if (message != null) {
                return PlaceholderUtils.format(message, placeholders);
            }
        }
        return ChatColor.RED + "Mensagem não encontrada: " + path;
    }
}