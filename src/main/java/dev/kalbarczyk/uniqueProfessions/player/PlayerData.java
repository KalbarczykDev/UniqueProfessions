package dev.kalbarczyk.uniqueProfessions.player;

import dev.kalbarczyk.uniqueProfessions.profession.ProfessionType;

import java.util.UUID;

public class PlayerData {

    private final UUID playerId;
    private ProfessionType profession;
    private int level;
    private double experience;
    private boolean hasProfession;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.profession = null;
        this.level = 1;
        this.experience = 0.0;
        this.hasProfession = false;
    }

    public PlayerData(UUID playerId, ProfessionType profession, int level, double experience) {
        this.playerId = playerId;
        this.profession = profession;
        this.level = level;
        this.experience = experience;
        this.hasProfession = profession != null;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public ProfessionType getProfession() {
        return profession;
    }

    public void setProfession(ProfessionType profession) {
        this.profession = profession;
        this.hasProfession = profession != null;
        if (profession != null && level == 0) {
            this.level = 1;
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public void addLevel(int amount) {
        this.level += amount;
    }

    public double getExperience() {
        return experience;
    }

    public void setExperience(double experience) {
        this.experience = Math.max(0, experience);
    }

    public void addExperience(double amount) {
        this.experience += amount;
    }

    public boolean hasProfession() {
        return hasProfession;
    }

    public void resetProfession() {
        this.profession = null;
        this.level = 1;
        this.experience = 0.0;
        this.hasProfession = false;
    }

    public double getExperienceToNextLevel(double xpRequired) {
        return xpRequired - experience;
    }

    public int getProgressPercentage(double xpRequired) {
        if (xpRequired <= 0) return 100;
        return (int) ((experience / xpRequired) * 100);
    }
}