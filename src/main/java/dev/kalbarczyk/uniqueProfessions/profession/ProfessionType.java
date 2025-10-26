package dev.kalbarczyk.uniqueProfessions.profession;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ProfessionType {
    MINER("Miner", ChatColor.GRAY, Material.IRON_PICKAXE,
            "Masters of the underground, extracting precious ores and minerals"),

    FARMER("Farmer", ChatColor.GREEN, Material.IRON_HOE,
            "Cultivators of the land, growing crops and raising animals"),

    LUMBERJACK("Lumberjack", ChatColor.DARK_GREEN, Material.IRON_AXE,
            "Woodcutters who fell trees and harvest timber"),

    BLACKSMITH("Blacksmith", ChatColor.GOLD, Material.ANVIL,
            "Craftsmen who forge weapons, tools, and armor"),

    BUILDER("Builder", ChatColor.BLUE, Material.BRICKS,
            "Architects who construct grand structures"),

    HUNTER("Hunter", ChatColor.RED, Material.BOW,
            "Trackers who hunt animals and monsters");

    private final String displayName;
    private final ChatColor color;
    private final Material icon;
    private final String description;

    ProfessionType(String displayName, ChatColor color, Material icon, String description) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() {
        return color + displayName;
    }

    public String getColoredName() {
        return color + displayName + ChatColor.RESET;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public static ProfessionType fromString(String name) {
        for (ProfessionType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
