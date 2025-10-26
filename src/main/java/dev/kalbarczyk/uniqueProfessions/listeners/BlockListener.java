package dev.kalbarczyk.uniqueProfessions.listeners;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public record BlockListener(UniqueProfessions plugin) implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        var player = event.getPlayer();

        // Allow creative mode and admin bypass
        if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("profession.bypass")) {
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(player);

        // Check if player has a profession
        if (!data.hasProfession()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must choose a profession first! Use /profession");
            return;
        }

        var blockType = event.getBlock().getType();

        // Check if player can break this block
        if (!plugin.getProfessionManager().canBreakBlock(player, blockType)) {
            event.setCancelled(true);
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColor.RED + "Your profession cannot break this block!")
            );
            return;
        }

        // Grant experience for breaking the block
        plugin.getProfessionManager().grantExperience(player, blockType, 1.0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        var player = event.getPlayer();

        // Allow creative mode and admin bypass
        if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("profession.bypass")) {
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(player);

        // Check if player has a profession
        if (!data.hasProfession()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must choose a profession first! Use /profession");
          //  return;
        }

        //TODO: Implement block placing restrictions based on profession

    }
}