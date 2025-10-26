package dev.kalbarczyk.uniqueProfessions.player;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.profession.ProfessionManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final UniqueProfessions plugin;
    private final Map<UUID, PlayerData> playerDataCache;
    private final File dataFolder;
    private final ProfessionManager professionManager;

    public PlayerDataManager() {
        this.plugin = UniqueProfessions.getInstance();
        this.playerDataCache = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.professionManager = plugin.getProfessionManager();

        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().severe("Failed to create player data folder: " + dataFolder.getAbsolutePath());
        }
    }

    public PlayerData getPlayerData(final UUID playerId) {
        // Return from cache if available
        if (playerDataCache.containsKey(playerId)) {
            return playerDataCache.get(playerId);
        }

        // Load from file
        var data = loadPlayerData(playerId);
        playerDataCache.put(playerId, data);
        return data;
    }

    public PlayerData getPlayerData(final Player player) {
        return getPlayerData(player.getUniqueId());
    }

    private PlayerData loadPlayerData(final UUID playerId) {
        var file = new File(dataFolder, playerId.toString() + ".yml");

        if (!file.exists()) {
            return new PlayerData(playerId);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        var professionName = config.getString("profession");
        var profession = professionName != null
                ? professionManager.get(professionName)
                : null;


        return new PlayerData(playerId, profession);
    }

    public void savePlayerData(final UUID playerId) {
        var data = playerDataCache.get(playerId);
        if (data == null) return;

        var file = new File(dataFolder, playerId.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        if (data.getProfession().isPresent()) {
            config.set("profession", data.getProfession().get().name());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + playerId + ": " + e.getMessage());
        }
    }

    public void savePlayerData(final Player player) {
        savePlayerData(player.getUniqueId());
    }

    public void saveAll() {
        for (UUID playerId : playerDataCache.keySet()) {
            savePlayerData(playerId);
        }
    }

    public void unloadPlayerData(final UUID playerId) {
        savePlayerData(playerId);
        playerDataCache.remove(playerId);
    }

    public void unloadPlayerData(final Player player) {
        unloadPlayerData(player.getUniqueId());
    }
}
