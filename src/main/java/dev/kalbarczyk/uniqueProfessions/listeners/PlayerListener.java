package dev.kalbarczyk.uniqueProfessions.listeners;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.messages.MessageKey;
import dev.kalbarczyk.uniqueProfessions.messages.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final UniqueProfessions plugin;
    private final MessageManager messageManager;

    public PlayerListener() {
        this.plugin = UniqueProfessions.getInstance();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Load player data
        plugin.getPlayerDataManager().getPlayerData(event.getPlayer());

        // Send welcome message if they don't have a profession
        if (plugin.getPlayerDataManager().getPlayerData(event.getPlayer()).getProfession().isEmpty()) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            event.getPlayer().sendMessage(ChatColor.YELLOW + messageManager.format(MessageKey.WELCOME_MESSAGE_CHOOSE,"player", event.getPlayer().getName()));
            event.getPlayer().sendMessage(ChatColor.GREEN + "/up choose");
            event.getPlayer().sendMessage(ChatColor.YELLOW + messageManager.get(MessageKey.WELCOME_MESSAGE_PROFESSION));
            event.getPlayer().sendMessage(ChatColor.GREEN + "/up list");
            event.getPlayer().sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // Save and unload player data
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer());
    }
}
