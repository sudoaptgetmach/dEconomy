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
    DAILY_REWARD_ERROR("daily_reward_error"),
    INVALID_PLAYER("invalid_player"),
    PLAYER_NOT_FOUND("player_not_found"),
    INVALID_TRANSACTION_VALUE("invalid_transaction_value"),
    INVALID_BALANCE_ARGUMENT("invalid_balance_argument"),
    INVALID_RELOADCONFIG_ARGUMENT("invalid_reloadconfig_argument"),
    INVALID_PLAYER_ACCOUNT("invalid_player_account"),
    INVALID_PLAYER_BANKACCOUNT("invalid_player_bankaccount"),
    NO_TRANSACTIONS("no_transactions"),
    TRANSACTION_ERROR("transaction_error"),

    TRANSFER_SUCCESS("transfer_success"),
    TRANSFER_RECEIVED("transfer_received"),
    WITHDRAW_SUCCESS("withdraw_success"),
    DEPOSIT_SUCCESS("deposit_success"),
    DAILY_REWARD_CLAIMED("daily_reward_claimed"),
    BALANCE_CHANGED_ADMIN("balance_changed_admin"),
    BANKBALANCE_CHANGED_ADMIN("bankbalance_changed_admin"),

    CONFIG_RELOADED("config_reloaded"),
    MESSAGES_RELOADED("messages_config_reloaded"),

    TRANSACTION_HISTORY_HEADER("transaction_history_header"),
    TRANSACTION_HISTORY_ENTRY("transaction_history_entry"),
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