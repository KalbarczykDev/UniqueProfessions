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
    private final Map<ProfessionType, Map<Integer, Set<Material>>> allowedTools;
    private final Map<ProfessionType, Map<Integer, Set<Material>>> allowedBlocks;
    private final Map<ProfessionType, Map<Integer, Double>> xpRequirements;
    private final Map<ProfessionType, Map<Material, Double>> xpGains;

    public ProfessionManager(final UniqueProfessions plugin) {
        this.plugin = plugin;
        this.allowedTools = new HashMap<>();
        this.allowedBlocks = new HashMap<>();
        this.xpRequirements = new HashMap<>();
        this.xpGains = new HashMap<>();

        loadProfessionData();
    }

    private void loadProfessionData() {
        var professionsSection = plugin.getConfig().getConfigurationSection("professions");
        if (professionsSection == null) return;

        for (var profKey : professionsSection.getKeys(false)) {
            var type = ProfessionType.fromString(profKey);
            if (type == null) continue;

            var profSection = professionsSection.getConfigurationSection(profKey);

            // Load levels
            assert profSection != null;
            var levelsSection = profSection.getConfigurationSection("levels");
            if (levelsSection != null) {
                var toolsMap = new HashMap<Integer, Set<Material>>();
                var blocksMap = new HashMap<Integer, Set<Material>>();
                var xpMap = new HashMap<Integer, Double>();

                for (var levelKey : levelsSection.getKeys(false)) {
                    int level = Integer.parseInt(levelKey);
                    var levelSection = levelsSection.getConfigurationSection(levelKey);

                    // Load allowed tools
                    assert levelSection != null;
                    var tools = levelSection.getStringList("allowed-tools");
                    var toolSet = new HashSet<Material>();
                    for (var tool : tools) {
                        var mat = Material.getMaterial(tool);
                        if (mat != null) toolSet.add(mat);
                    }
                    toolsMap.put(level, toolSet);

                    // Load allowed blocks
                    var blocks = levelSection.getStringList("allowed-blocks");
                    var blockSet = new HashSet<Material>();
                    for (var block : blocks) {
                        var mat = Material.getMaterial(block);
                        if (mat != null) blockSet.add(mat);
                    }
                    blocksMap.put(level, blockSet);

                    // Load XP requirement
                    xpMap.put(level, levelSection.getDouble("xp-required", 0.0));
                }

                allowedTools.put(type, toolsMap);
                allowedBlocks.put(type, blocksMap);
                xpRequirements.put(type, xpMap);
            }

            // Load XP gains
            var xpGainsSection = profSection.getConfigurationSection("xp-gains");
            if (xpGainsSection != null) {
                var gainsMap = new HashMap<Material, Double>();
                for (var matKey : xpGainsSection.getKeys(false)) {
                    var mat = Material.getMaterial(matKey);
                    if (mat != null) {
                        gainsMap.put(mat, xpGainsSection.getDouble(matKey));
                    }
                }
                xpGains.put(type, gainsMap);
            }
        }
    }

    private boolean canUseMaterial(
            final Player player,
            final Material material,
            final Map<ProfessionType, Map<Integer, Set<Material>>> allowedMap
    ) {
        var data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.hasProfession()) return false;

        var levelMaterials = allowedMap.get(data.getProfession());
        if (levelMaterials == null) return false;

        for (int i = 1; i <= data.getLevel(); i++) {
            var materials = levelMaterials.get(i);
            if (materials != null && materials.contains(material)) {
                return true;
            }
        }

        return false;
    }

    public boolean canUseItem(final Player player, final Material material) {
        return canUseMaterial(player, material, allowedTools);
    }

    public boolean canBreakBlock(final Player player, final Material material) {
        return canUseMaterial(player, material, allowedBlocks);
    }

    public void grantExperience(final Player player, final Material material, final double multiplier) {
        var data = plugin.getPlayerDataManager().getPlayerData(player);
        if (!data.hasProfession()) return;

        var gains = xpGains.get(data.getProfession());
        if (gains == null || !gains.containsKey(material)) return;

        double xp = gains.get(material) * multiplier;
        data.addExperience(xp);

        // Check for level up
        checkLevelUp(player, data);

        // Show XP gain
        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(ChatColor.GOLD + "+" + String.format("%.1f", xp) + " XP")
        );
    }

    private void checkLevelUp(final Player player, final PlayerData data) {
        var xpReqs = xpRequirements.get(data.getProfession());
        if (xpReqs == null) return;

        int nextLevel = data.getLevel() + 1;
        var requiredXp = xpReqs.get(nextLevel);

        if (requiredXp != null && data.getExperience() >= requiredXp) {
            // Level up!
            data.addLevel(1);
            data.setExperience(0);

            player.sendMessage(ChatColor.GREEN + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage(ChatColor.GOLD + "⚡ LEVEL UP! ⚡");
            player.sendMessage(ChatColor.YELLOW + "You are now a Level " + data.getLevel() + " " + data.getProfession().getColoredName());
            player.sendMessage(ChatColor.GREEN + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            plugin.getPlayerDataManager().savePlayerData(player);
        }
    }

    public double getRequiredXpForNextLevel(final PlayerData data) {
        if (!data.hasProfession()) return 0;

        var xpReqs = xpRequirements.get(data.getProfession());
        if (xpReqs == null) return 0;

        return xpReqs.getOrDefault(data.getLevel() + 1, Double.MAX_VALUE);
    }

    public int getMaxLevel(final ProfessionType profession) {
        var xpReqs = xpRequirements.get(profession);
        if (xpReqs == null) return 1;
        return Collections.max(xpReqs.keySet());
    }
}