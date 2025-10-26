package dev.kalbarczyk.uniqueProfessions.listeners;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public record ItemListener(UniqueProfessions plugin) implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = event.getItem();

        // No item in hand
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        // Allow creative mode and admin bypass
        if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("profession.bypass")) {
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(player);

        // Check if player has a profession
        if (!data.hasProfession()) {
            if (isRestrictedTool(item.getType())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You must choose a profession first! Use /profession");
            }
            return;
        }

        // Check if the item is a tool that requires profession check
        if (isRestrictedTool(item.getType())) {
            if (!plugin.getProfessionManager().canUseItem(player, item.getType())) {
                event.setCancelled(true);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.RED + "Your profession cannot use this tool!"));
                player.sendMessage(ChatColor.YELLOW + "You need to be a higher level " +
                        data.getProfession().getColoredName() + ChatColor.YELLOW + " to use this!");
            }
        }
    }

    private boolean isRestrictedTool(Material material) {
        var name = material.name();

        // Check for tools
        return name.endsWith("_PICKAXE") ||
                name.endsWith("_AXE") ||
                name.endsWith("_SHOVEL") ||
                name.endsWith("_HOE") ||
                name.endsWith("_SWORD") ||
                name.equals("BOW") ||
                name.equals("CROSSBOW") ||
                name.equals("TRIDENT") ||
                name.equals("SHEARS") ||
                name.equals("FISHING_ROD");
    }
}