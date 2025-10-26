package dev.kalbarczyk.uniqueProfessions.profession;

import org.bukkit.Material;

import java.util.*;

public class Profession {

    private final ProfessionType type;
    private final Map<Integer, ProfessionLevel> levels;
    private final Map<Material, Double> xpGains;
    private final int maxLevel;
    private final boolean enabled;

    public Profession(ProfessionType type, int maxLevel) {
        this.type = type;
        this.maxLevel = maxLevel;
        this.levels = new HashMap<>();
        this.xpGains = new HashMap<>();
        this.enabled = true;
    }

    public Profession(ProfessionType type, int maxLevel, boolean enabled) {
        this.type = type;
        this.maxLevel = maxLevel;
        this.levels = new HashMap<>();
        this.xpGains = new HashMap<>();
        this.enabled = enabled;
    }

    // Getters

    public ProfessionType getType() {
        return type;
    }

    public String getName() {
        return type.name();
    }

    public String getDisplayName() {
        return type.getDisplayName();
    }

    public String getColoredName() {
        return type.getColoredName();
    }

    public Material getIcon() {
        return type.getIcon();
    }

    public String getDescription() {
        return type.getDescription();
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<Integer, ProfessionLevel> getLevels() {
        return new HashMap<>(levels);
    }

    public ProfessionLevel getLevel(final int level) {
        return levels.get(level);
    }

    public Map<Material, Double> getXpGains() {
        return new HashMap<>(xpGains);
    }

    // Level management

    public void addLevel(ProfessionLevel level) {
        levels.put(level.getLevel(), level);
    }

    public void addLevel(int levelNum, ProfessionLevel level) {
        levels.put(levelNum, level);
    }

    public boolean hasLevel(int level) {
        return levels.containsKey(level);
    }

    public Set<Integer> getAllLevelNumbers() {
        return new TreeSet<>(levels.keySet());
    }

    // XP management

    public void setXpGain(final Material material,final double xp) {
        xpGains.put(material, xp);
    }

    public double getXpGain(final Material material) {
        return xpGains.getOrDefault(material, 0.0);
    }

    public boolean hasXpGain(final Material material) {
        return xpGains.containsKey(material);
    }

    // Permission checks

    public boolean canUseTool(final int playerLevel,final Material tool) {
        // Check all levels up to player's current level
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null && level.canUseTool(tool)) {
                return true;
            }
        }
        return false;
    }

    public boolean canBreakBlock(final int playerLevel,final Material block) {
        // Check all levels up to player's current level
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null && level.canBreakBlock(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean canUseItem(final int playerLevel,final Material item) {
        // Check all levels up to player's current level
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null && level.canUseItem(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPerk(final int playerLevel,final String perk) {
        // Check all levels up to player's current level
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null && level.hasPerk(perk)) {
                return true;
            }
        }
        return false;
    }

    // Utility methods

    public double getXpRequiredForLevel(final int level) {
        var profLevel = levels.get(level);
        return profLevel != null ? profLevel.getXpRequired() : Double.MAX_VALUE;
    }

    public Set<Material> getAllAllowedToolsUpToLevel(final int playerLevel) {
        var  allTools = new HashSet<Material>();
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null) {
                allTools.addAll(level.getAllowedTools());
            }
        }
        return allTools;
    }

    public Set<Material> getAllAllowedBlocksUpToLevel(final int playerLevel) {
        var allBlocks = new HashSet<Material>();
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null) {
                allBlocks.addAll(level.getAllowedBlocks());
            }
        }
        return allBlocks;
    }

    public Set<String> getAllPerksUpToLevel(final int playerLevel) {
        var allPerks = new HashSet<String>();
        for (int i = 1; i <= playerLevel; i++) {
            var level = levels.get(i);
            if (level != null) {
                allPerks.addAll(level.getUnlockedPerks());
            }
        }
        return allPerks;
    }

    public List<String> getLevelProgression() {
        var progression = new ArrayList<String>();
        for (int i = 1; i <= maxLevel; i++) {
            var level = levels.get(i);
            if (level != null) {
                progression.add("Level " + i + ": " + level.getDescription());
            }
        }
        return progression;
    }

    @Override
    public String toString() {
        return type.getColoredName() + " (Max Level: " + maxLevel + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (Profession) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public static class Builder {
        private final ProfessionType type;
        private int maxLevel = 10;
        private boolean enabled = true;
        private final Map<Integer, ProfessionLevel> levels = new HashMap<>();
        private final Map<Material, Double> xpGains = new HashMap<>();

        public Builder(final ProfessionType type) {
            this.type = type;
        }

        public Builder maxLevel(final int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder enabled(final boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder addLevel(final ProfessionLevel level) {
            this.levels.put(level.getLevel(), level);
            return this;
        }

        public Builder addLevel(final int levelNum,final ProfessionLevel level) {
            this.levels.put(levelNum, level);
            return this;
        }

        public Builder addXpGain(final Material material,final double xp) {
            this.xpGains.put(material, xp);
            return this;
        }

        public Profession build() {
            var profession = new Profession(type, maxLevel, enabled);
            profession.levels.putAll(this.levels);
            profession.xpGains.putAll(this.xpGains);
            return profession;
        }
    }
}
