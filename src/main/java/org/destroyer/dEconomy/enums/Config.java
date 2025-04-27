package org.destroyer.dEconomy.enums;

import org.bukkit.ChatColor;
import org.destroyer.dEconomy.manager.ConfigManager;
import org.destroyer.dEconomy.utils.PlaceholderUtils;

import java.util.Map;

public enum Config {

    INITIAL_BALANCE("economy.initial_balance"),
    INITIAL_BANK_BALANCE("economy.initial_bank_balance"),
    DAILY_REWARD_COOLDOWN("economy.daily_reward_hours_cooldown"),
    DAILY_REWARD_MIN("economy.daily_reward_min"),
    DAILY_REWARD_MAX("economy.daily_reward_max"),
    MAX_BALANCE("economy.max_balance");

    private final String path;
    private static ConfigManager configManager;

    Config(String path) {
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
        return ChatColor.RED + "Configuração não encontrada: " + path;
    }

    public String get(Map<String, String> placeholders) {
        if (configManager.getConfig().contains(path)) {
            String message = configManager.getConfig().getString(path);
            if (message != null) {
                return PlaceholderUtils.format(message, placeholders);
            }
        }
        return ChatColor.RED + "Configuração não encontrada: " + path;
    }
}
