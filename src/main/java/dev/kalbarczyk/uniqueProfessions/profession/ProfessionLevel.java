package dev.kalbarczyk.uniqueProfessions.profession;

import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfessionLevel {

    private final int level;
    private final Set<Material> allowedTools;
    private final Set<Material> allowedBlocks;
    private final Set<Material> allowedItems;
    private final double xpRequired;
    private final String description;
    private final Set<String> unlockedPerks;

    public ProfessionLevel(int level, double xpRequired) {
        this.level = level;
        this.xpRequired = xpRequired;
        this.allowedTools = new HashSet<>();
        this.allowedBlocks = new HashSet<>();
        this.allowedItems = new HashSet<>();
        this.unlockedPerks = new HashSet<>();
        this.description = "";
    }

    public ProfessionLevel(int level, double xpRequired, String description) {
        this.level = level;
        this.xpRequired = xpRequired;
        this.allowedTools = new HashSet<>();
        this.allowedBlocks = new HashSet<>();
        this.allowedItems = new HashSet<>();
        this.unlockedPerks = new HashSet<>();
        this.description = description;
    }

    public ProfessionLevel(
            final int level,
            final Set<Material> allowedTools,
            final Set<Material> allowedBlocks,
            final double xpRequired,
            final String description
    ) {
        this.level = level;
        this.allowedTools = new HashSet<>(allowedTools);
        this.allowedBlocks = new HashSet<>(allowedBlocks);
        this.allowedItems = new HashSet<>();
        this.xpRequired = xpRequired;
        this.description = description;
        this.unlockedPerks = new HashSet<>();
    }

    // Getters

    public int getLevel() {
        return level;
    }

    public Set<Material> getAllowedTools() {
        return new HashSet<>(allowedTools);
    }

    public Set<Material> getAllowedBlocks() {
        return new HashSet<>(allowedBlocks);
    }

    public Set<Material> getAllowedItems() {
        return new HashSet<>(allowedItems);
    }

    public double getXpRequired() {
        return xpRequired;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getUnlockedPerks() {
        return new HashSet<>(unlockedPerks);
    }

    // Modifiers

    public void addAllowedTool(Material tool) {
        this.allowedTools.add(tool);
    }

    public void addAllowedTools(List<Material> tools) {
        this.allowedTools.addAll(tools);
    }

    public void addAllowedBlock(Material block) {
        this.allowedBlocks.add(block);
    }

    public void addAllowedBlocks(List<Material> blocks) {
        this.allowedBlocks.addAll(blocks);
    }

    public void addAllowedItem(Material item) {
        this.allowedItems.add(item);
    }

    public void addAllowedItems(List<Material> items) {
        this.allowedItems.addAll(items);
    }

    public void addPerk(String perk) {
        this.unlockedPerks.add(perk);
    }

    public void addPerks(List<String> perks) {
        this.unlockedPerks.addAll(perks);
    }

    // Checkers

    public boolean canUseTool(Material tool) {
        return allowedTools.contains(tool);
    }

    public boolean canBreakBlock(Material block) {
        return allowedBlocks.contains(block);
    }

    public boolean canUseItem(Material item) {
        return allowedItems.contains(item);
    }

    public boolean hasPerk(String perk) {
        return unlockedPerks.contains(perk);
    }

    // Display information

    public String getUnlockSummary() {
        var summary = new StringBuilder();

        if (!allowedTools.isEmpty()) {
            summary.append("Tools: ").append(allowedTools.size()).append(" new");
        }

        if (!allowedBlocks.isEmpty()) {
            if (!summary.isEmpty()) summary.append(", ");
            summary.append("Blocks: ").append(allowedBlocks.size()).append(" new");
        }

        if (!allowedItems.isEmpty()) {
            if (!summary.isEmpty()) summary.append(", ");
            summary.append("Items: ").append(allowedItems.size()).append(" new");
        }

        if (!unlockedPerks.isEmpty()) {
            if (!summary.isEmpty()) summary.append(", ");
            summary.append("Perks: ").append(unlockedPerks.size()).append(" new");
        }

        return !summary.isEmpty() ? summary.toString() : "No new unlocks";
    }

    @Override
    public String toString() {
        return "Level " + level + " (Requires " + xpRequired + " XP)";
    }



    public static class Builder {
        private final int level;
        private final double xpRequired;
        private String description = "";
        private final Set<Material> allowedTools = new HashSet<>();
        private final Set<Material> allowedBlocks = new HashSet<>();
        private final Set<Material> allowedItems = new HashSet<>();
        private final Set<String> perks = new HashSet<>();

        public Builder(final int level,final double xpRequired) {
            this.level = level;
            this.xpRequired = xpRequired;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addTool(Material tool) {
            this.allowedTools.add(tool);
            return this;
        }

        public Builder addTools(Material... tools) {
            Collections.addAll(this.allowedTools, tools);
            return this;
        }

        public Builder addBlock(Material block) {
            this.allowedBlocks.add(block);
            return this;
        }

        public Builder addBlocks(Material... blocks) {
            Collections.addAll(this.allowedBlocks, blocks);
            return this;
        }

        public Builder addItem(Material item) {
            this.allowedItems.add(item);
            return this;
        }

        public Builder addItems(Material... items) {
            Collections.addAll(this.allowedItems, items);
            return this;
        }

        public Builder addPerk(String perk) {
            this.perks.add(perk);
            return this;
        }

        public Builder addPerks(String... perks) {
            Collections.addAll(this.perks, perks);
            return this;
        }

        public ProfessionLevel build() {
            var level = new ProfessionLevel(this.level, this.xpRequired, this.description);
            level.allowedTools.addAll(this.allowedTools);
            level.allowedBlocks.addAll(this.allowedBlocks);
            level.allowedItems.addAll(this.allowedItems);
            level.unlockedPerks.addAll(this.perks);
            return level;
        }
    }
}
