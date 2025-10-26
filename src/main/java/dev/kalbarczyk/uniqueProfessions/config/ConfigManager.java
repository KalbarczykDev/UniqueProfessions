package dev.kalbarczyk.uniqueProfessions.config;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;


public class ConfigManager {

    private final UniqueProfessions plugin;
    private final File configFile;

    public ConfigManager() {
        this.plugin = UniqueProfessions.getInstance();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        // Create plugin folder if it doesn't exist
        var dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().log(Level.SEVERE, "Could not create plugin data folder: " + dataFolder.getAbsolutePath());
        }


        // Save default config if it doesn't exist
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        loadConfig();

    }

    public void loadConfig() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        var defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null) {
            var defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.setDefaults(defConfig);
            var professionManager = plugin.getProfessionManager();
            professionManager.clear();
            professionManager.registerAll(ConfigLoader.loadProfessions(config));
        }
        plugin.getLogger().info("Loaded config file: " + configFile.getAbsolutePath());
    }


}