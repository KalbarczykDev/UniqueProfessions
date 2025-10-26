package dev.kalbarczyk.uniqueProfessions.player;

import dev.kalbarczyk.uniqueProfessions.profession.Profession;

import java.util.Optional;
import java.util.UUID;

public class PlayerData {

    private final UUID playerId;
    private Profession profession;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.profession = null;
    }

    public PlayerData(final UUID playerId, final Profession profession) {
        this.playerId = playerId;
        this.profession = profession;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Optional<Profession> getProfession() {
        return Optional.of(profession);
    }

    public void setProfession(final Profession profession) {
        this.profession = profession;
    }

    public void resetProfession() {
        this.profession = null;
    }
}