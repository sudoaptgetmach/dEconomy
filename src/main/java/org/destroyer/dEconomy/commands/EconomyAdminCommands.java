package org.destroyer.dEconomy.commands;

import org.destroyer.dEconomy.manager.ConfigManager;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static org.destroyer.dEconomy.enums.Messages.*;

@Command({"deconomy", "economyadmin"})
@CommandPermission("deconomy.admin")
public class EconomyAdminCommands {

    private final ConfigManager configManager;
    private final ConfigManager messagesManager;

    public EconomyAdminCommands(ConfigManager configManager, ConfigManager messagesManager) {
        this.configManager = configManager;
        this.messagesManager = messagesManager;
    }

    @Command({"deconomy", "economyadmin"})
    @CommandPermission("deconomy.admin")
    public void help(BukkitCommandActor sender) {
        sender.sendRawMessage("§6§l=== dEconomy Admin Help ===");
        sender.sendRawMessage("§eComandos principais:");
        sender.sendRawMessage("§7/deconomy help §8- §fMostra esta mensagem de ajuda");
        sender.sendRawMessage("§7/deconomy reload <config/messages> §8- §fRecarrega o respectivo arquivo de configurações");
    }

    @Subcommand("reload <configType>")
    @Usage("reload <configType>")
    @CommandPermission("deconomy.admin.reload")
    public void reloadConfig(BukkitCommandActor sender, @Named("configType") @Default("config") String config) {
        switch (config.toLowerCase()) {
            case "config" -> {
                configManager.reloadConfig();
                sender.sendRawMessage(CONFIG_RELOADED.get());
            }
            case "messages" -> {
                messagesManager.reloadConfig();
                sender.sendRawMessage(MESSAGES_RELOADED.get());
            }
            default -> sender.error(INVALID_RELOADCONFIG_ARGUMENT.get());
        }
    }
}
