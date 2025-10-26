package dev.kalbarczyk.uniqueProfessions.profession;

import org.bukkit.Material;

import java.util.*;

public record Profession(String name, String displayName, String description, Set<Material> allowedTools) {

    public boolean canUseTool(final Material tool) {
        return allowedTools.contains(tool);
    }
}
