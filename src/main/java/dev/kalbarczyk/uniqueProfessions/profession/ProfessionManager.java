package dev.kalbarczyk.uniqueProfessions.profession;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProfessionManager {
    private final Map<String, Profession> professions = new HashMap<>();

    public void register(final Profession profession) {
        professions.put(profession.name().toLowerCase(), profession);
    }

    public void registerAll(final Collection<Profession> professionList) {
        for (var profession : professionList) {
            register(profession);
        }
    }

    public Profession get(final String name) {
        if (name == null) return null;
        return professions.get(name.toLowerCase());
    }

    public boolean exists(final String name) {
        return get(name) != null;
    }

    public Collection<Profession> getAll() {
        return professions.values();
    }
}
