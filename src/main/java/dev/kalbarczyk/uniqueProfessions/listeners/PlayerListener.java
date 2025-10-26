package dev.kalbarczyk.uniqueProfessions.listeners;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerListener(UniqueProfessions plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Load player data
        plugin.getPlayerDataManager().getPlayerData(event.getPlayer());

        // Send welcome message if they don't have a profession
        if (!plugin.getPlayerDataManager().getPlayerData(event.getPlayer()).hasProfession()) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Welcome! Choose your profession with:");
            event.getPlayer().sendMessage(ChatColor.GREEN + "/profession choose");
            event.getPlayer().sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // Save and unload player data
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer());
    }
}
