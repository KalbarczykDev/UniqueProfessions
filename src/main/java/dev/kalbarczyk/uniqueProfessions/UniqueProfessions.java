package dev.kalbarczyk.uniqueProfessions;

import dev.kalbarczyk.uniqueProfessions.commands.AdminCommand;
import dev.kalbarczyk.uniqueProfessions.commands.ProfessionCommand;
import dev.kalbarczyk.uniqueProfessions.config.ConfigManager;
import dev.kalbarczyk.uniqueProfessions.listeners.BlockListener;
import dev.kalbarczyk.uniqueProfessions.listeners.ItemListener;
import dev.kalbarczyk.uniqueProfessions.listeners.PlayerListener;
import dev.kalbarczyk.uniqueProfessions.player.PlayerDataManager;
import dev.kalbarczyk.uniqueProfessions.profession.ProfessionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class UniqueProfessions extends JavaPlugin {

    private static UniqueProfessions instance;
    private PlayerDataManager playerDataManager;
    private ProfessionManager professionManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        professionManager = new ProfessionManager();
        configManager = new ConfigManager();
        playerDataManager = new PlayerDataManager();


        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Objects.requireNonNull(getCommand("profession")).setExecutor(new ProfessionCommand(this));
        Objects.requireNonNull(getCommand("professionadmin")).setExecutor(new AdminCommand(this));

        getLogger().info("ProfessionPlugin has been enabled!");

    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }

        getLogger().info("ProfessionPlugin has been disabled!");
    }

    public static UniqueProfessions getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public ProfessionManager getProfessionManager() {
        return professionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
