package dev.kalbarczyk.uniqueProfessions.config;

import dev.kalbarczyk.uniqueProfessions.profession.Profession;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConfigLoader {

    public static List<Profession> loadProfessions(FileConfiguration config) {
        List<Profession> professions = new ArrayList<>();

        var section = config.getConfigurationSection("professions");
        if (section == null) return professions;

        for (String key : section.getKeys(false)) {
            var profSection = section.getConfigurationSection(key);
            if (profSection == null) continue;

            var displayName = profSection.getString("displayName", key);
            var description = profSection.getString("description", "");
            var allowedTools = new HashSet<Material>();

            var tools = profSection.getStringList("allowedTools");
            for (String toolName : tools) {
                try {
                    allowedTools.add(Material.valueOf(toolName.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }

            professions.add(new Profession(key, displayName, description, allowedTools));
        }

        return professions;
    }
}