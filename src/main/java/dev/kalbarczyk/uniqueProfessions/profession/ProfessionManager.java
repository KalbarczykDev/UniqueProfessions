package dev.kalbarczyk.uniqueProfessions.profession;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.player.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;



import java.util.*;

public class ProfessionManager {

    private final UniqueProfessions plugin;
    private final Map<ProfessionType, Profession> professions;

    public ProfessionManager(final UniqueProfessions plugin) {
        this.plugin = plugin;
        this.professions = new HashMap<>();
        loadProfessionData();
    }

    public Profession getProfession(final ProfessionType type) {
        return professions.get(type);
    }

    public Collection<Profession> getAllProfessions() {
        return professions.values();
    }

    private void loadProfessionData() {
        var professionsSection = plugin.getConfig().getConfigurationSection("professions");
        if (professionsSection == null) return;

        for (var profKey : professionsSection.getKeys(false)) {
            var type = ProfessionType.fromString(profKey);
            if (type == null) continue;

            var profSection = professionsSection.getConfigurationSection(profKey);
            if (profSection == null) continue;

            int maxLevel = Objects.requireNonNull(profSection.getConfigurationSection("levels")).getKeys(false).size();
            var profession = new Profession.Builder(type)
                    .maxLevel(maxLevel)
                    .build();

            var levelsSection = profSection.getConfigurationSection("levels");
            if (levelsSection != null) {
                for (var levelKey : levelsSection.getKeys(false)) {
                    var levelNum = Integer.parseInt(levelKey);
                    var levelSection = levelsSection.getConfigurationSection(levelKey);
                    if (levelSection == null) continue;

                    var levelBuilder = new ProfessionLevel.Builder(levelNum, levelSection.getDouble("xp-required", 0.0))
                            .description(levelSection.getString("description", ""));

                    for (var toolName : levelSection.getStringList("allowed-tools")) {
                        var mat = Material.getMaterial(toolName);
                        if (mat != null) levelBuilder.addTool(mat);
                    }

                    for (var blockName : levelSection.getStringList("allowed-blocks")) {
                        var mat = Material.getMaterial(blockName);
                        if (mat != null) levelBuilder.addBlock(mat);
                    }

                    profession.addLevel(levelBuilder.build());
                }
            }

            var xpGainsSection = profSection.getConfigurationSection("xp-gains");
            if (xpGainsSection != null) {
                for (var matKey : xpGainsSection.getKeys(false)) {
                    var mat = Material.getMaterial(matKey);
                    if (mat != null) {
                        profession.setXpGain(mat, xpGainsSection.getDouble(matKey));
                    }
                }
            }

            professions.put(type, profession);
        }
    }

    public boolean canUseItem(final Player player, final Material material) {
        var data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.hasProfession()) return false;
        var profession = getProfession(data.getProfession());
        return profession != null && profession.canUseItem(data.getLevel(), material);
    }

    public boolean canBreakBlock(final Player player, final Material material) {
        var data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.hasProfession()) return false;
        var profession = getProfession(data.getProfession());
        return profession != null && profession.canBreakBlock(data.getLevel(), material);
    }

    public void grantExperience(final Player player, final Material material, final double multiplier) {
        var data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.hasProfession()) return;

        var profession = getProfession(data.getProfession());
        if (profession == null || !profession.hasXpGain(material)) return;

        var xp = profession.getXpGain(material) * multiplier;
        data.addExperience(xp);
        checkLevelUp(player, data, profession);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + "+" + String.format("%.1f", xp) + " XP"));
    }

    private void checkLevelUp(final Player player, final PlayerData data, final Profession profession) {
        var nextLevel = data.getLevel() + 1;
        var requiredXp = profession.getXpRequiredForLevel(nextLevel);
        if (data.getExperience() >= requiredXp) {
            data.addLevel(1);
            data.setExperience(0);

            player.sendMessage(ChatColor.GREEN + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage(ChatColor.GOLD + "⚡ LEVEL UP! ⚡");
            player.sendMessage(ChatColor.YELLOW + "You are now a Level " + data.getLevel() + " " + profession.getColoredName());
            player.sendMessage(ChatColor.GREEN + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            plugin.getPlayerDataManager().savePlayerData(player);
        }
    }
}
