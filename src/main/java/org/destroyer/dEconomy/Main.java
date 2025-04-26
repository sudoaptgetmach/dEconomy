package org.destroyer.dEconomy;

import org.bukkit.plugin.java.JavaPlugin;
import org.destroyer.dEconomy.commands.*;
import org.destroyer.dEconomy.database.DatabaseManager;
import org.destroyer.dEconomy.enums.Config;
import org.destroyer.dEconomy.enums.Messages;
import org.destroyer.dEconomy.handler.LampExceptionHandler;
import org.destroyer.dEconomy.listener.EconomyListener;
import org.destroyer.dEconomy.manager.ConfigManager;
import org.destroyer.dEconomy.repository.BankRepository;
import org.destroyer.dEconomy.repository.DailyRewardsRepository;
import org.destroyer.dEconomy.repository.PlayerRepository;
import org.destroyer.dEconomy.repository.TransactionsRepository;
import revxrsal.commands.bukkit.BukkitLamp;

import static org.bukkit.Bukkit.getPluginManager;

public final class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseManager databaseManager;

    private BankRepository bankRepository;
    private PlayerRepository playerRepository;
    private TransactionsRepository transactionsRepository;

    private ConfigManager configManager;
    private ConfigManager messagesManager;

    @Override
    public void onEnable() {
        instance = this;
        initConfigs();

        databaseManager = new DatabaseManager();
        databaseManager.setup();

        initRepository();

        var lamp = BukkitLamp.builder(this)
                .exceptionHandler(new LampExceptionHandler())
                .build();

        lamp.register(new BalanceCommand(bankRepository, playerRepository));
        lamp.register(new BankCommands(bankRepository, playerRepository));
        lamp.register(new EconomyAdminCommands(configManager, messagesManager));
        lamp.register(new TransactionsCommand(transactionsRepository));
        lamp.register(new PayCommand(playerRepository, bankRepository, transactionsRepository));

        getPluginManager().registerEvents(new EconomyListener(playerRepository, bankRepository), this);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    public void initRepository() {
        bankRepository = new BankRepository(databaseManager);
        DailyRewardsRepository dailyRewardsRepository = new DailyRewardsRepository(databaseManager);
        playerRepository = new PlayerRepository(databaseManager);
        transactionsRepository = new TransactionsRepository(databaseManager);
    }

    public void initConfigs() {
        configManager = new ConfigManager(this, "config.yml");
        messagesManager = new ConfigManager(this, "messages.yml");
        ConfigManager permissionsManager = new ConfigManager(this, "permissions.yml");

        configManager.setup();
        messagesManager.setup();
        permissionsManager.setup();
        Messages.init(messagesManager);
        Config.init(configManager);

    }

    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public static Main getInstance() { return instance; }
}
