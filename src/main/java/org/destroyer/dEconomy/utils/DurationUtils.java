package org.destroyer.dEconomy.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DurationUtils {
    public Date durationCheck(CommandSender sender, String durationArg) {
        if (!durationArg.matches("^[1-9]\\d*([smhdwMY])$")) {
            return null;
        }

        int timeValue = Integer.parseInt(durationArg.replaceAll("[^0-9]", ""));
        char timeUnit = durationArg.charAt(durationArg.length() - 1);

        Calendar calendar = Calendar.getInstance();

        switch (timeUnit) {
            case 's':
                calendar.add(Calendar.SECOND, timeValue);
                break;
            case 'm':
                calendar.add(Calendar.MINUTE, timeValue);
                break;
            case 'h':
                calendar.add(Calendar.HOUR, timeValue);
                break;
            case 'd':
                calendar.add(Calendar.DAY_OF_MONTH, timeValue);
                break;
            case 'w':
                calendar.add(Calendar.WEEK_OF_YEAR, timeValue);
                break;
            case 'M':
                calendar.add(Calendar.MONTH, timeValue);
                break;
            case 'Y':
                calendar.add(Calendar.YEAR, timeValue);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unidade de tempo inválida.");
                return null;
        }

        return calendar.getTime();
    }

    public static String formatDuration(String durationArg) {

        int timeValue = Integer.parseInt(durationArg.replaceAll("[^0-9]", ""));
        char timeUnit = durationArg.charAt(durationArg.length() - 1);

        return switch (timeUnit) {
            case 's' -> timeValue + " segundos.";
            case 'm' -> timeValue + " minutos.";
            case 'h' -> timeValue + " horas.";
            case 'd' -> timeValue + " dias.";
            case 'w' -> timeValue + " semanas.";
            case 'M' -> timeValue + " meses.";
            case 'Y' -> timeValue + " anos.";
            default -> "Unidade de tempo inválida.";
        };
    }

    public static long parseDurationToMillis(String durationStr) {
        durationStr = durationStr.toLowerCase();
        long multiplier;

        if (durationStr.endsWith("s")) {
            multiplier = 1000L;
        } else if (durationStr.endsWith("m")) {
            multiplier = 60 * 1000L;
        } else if (durationStr.endsWith("h")) {
            multiplier = 60 * 60 * 1000L;
        } else if (durationStr.endsWith("d")) {
            multiplier = 24 * 60 * 60 * 1000L;
        } else {
            throw new IllegalArgumentException("Formato de duração inválido: " + durationStr);
        }

        String numericPart = durationStr.substring(0, durationStr.length() - 1);
        long amount = Long.parseLong(numericPart);

        return amount * multiplier;
    }

    public static String formatDate(long millis) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(millis));
    }
}