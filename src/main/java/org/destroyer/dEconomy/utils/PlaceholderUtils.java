package org.destroyer.dEconomy.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;

public class PlaceholderUtils {

    public static String applyPlaceholder(String message, Map<String, String> placeholder) {
        if (message == null) return null;

        for (Map.Entry<String, String> entry : placeholder.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        message = message.replace("\\n", "\n");

        if (message.endsWith("\n")) {
            message += " ";
        }

        return message;
    }

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message.replace("\\n", "\n"));
    }

    public static String format(String message, Map<String, String> placeholders) {
        return ChatColor.translateAlternateColorCodes('&', applyPlaceholder(message, placeholders));
    }


    public static String getPlayerNameFromUUID(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.hasPlayedBefore()) {
            return player.getName();
        } else {
            return "Jogador inválido";
        }
    }
}