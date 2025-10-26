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
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(final UniqueProfessions plugin) {
        this.plugin = plugin;
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
        this.config = YamlConfiguration.loadConfiguration(configFile);

        // Load defaults
        var defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null) {
            var defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.setDefaults(defConfig);
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    // Utility methods for common config operations

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    // Profession-specific config helpers

    public boolean isProfessionEnabled(String profession) {
        return config.getBoolean("professions." + profession + ".enabled", true);
    }

    public int getMaxLevel(String profession) {
        return config.getInt("professions." + profession + ".max-level", 10);
    }

    public double getXpRequirement(String profession, int level) {
        return config.getDouble("professions." + profession + ".levels." + level + ".xp-required", 0.0);
    }

    public double getXpGain(String profession, String material) {
        return config.getDouble("professions." + profession + ".xp-gains." + material, 0.0);
    }

    // Settings

    public boolean allowProfessionReset() {
        return config.getBoolean("settings.allow-profession-reset", true);
    }

    public double getProfessionResetCost() {
        return config.getDouble("settings.profession-reset-cost", 0.0);
    }

    public boolean showXpMessages() {
        return config.getBoolean("settings.show-xp-messages", true);
    }

    public boolean showLevelUpEffects() {
        return config.getBoolean("settings.show-levelup-effects", true);
    }

    public String getMessagePrefix() {
        return config.getString("settings.message-prefix", "&6[Profession]&r ");
    }

    public boolean debugMode() {
        return config.getBoolean("settings.debug-mode", false);
    }
}