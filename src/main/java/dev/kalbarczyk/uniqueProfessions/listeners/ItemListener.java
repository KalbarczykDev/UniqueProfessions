package dev.kalbarczyk.uniqueProfessions.listeners;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.messages.MessageKey;
import dev.kalbarczyk.uniqueProfessions.messages.MessageManager;
import dev.kalbarczyk.uniqueProfessions.utils.ChatColors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {

    private final UniqueProfessions plugin;
    private final MessageManager mm;

    public ItemListener() {
        this.plugin = UniqueProfessions.getInstance();
        this.mm = plugin.getMessageManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = event.getItem();

        // No item in hand

        if (item == null || item.getType() == Material.AIR) return;

        // Allow creative mode and admin bypass
        if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("profession.bypass")) return;


        var data = plugin.getPlayerDataManager().getPlayerData(player);
        var material = item.getType();

        if (!isRestrictedTool(material)) return;

        // Restricted item logic
        if (data.getProfession().isEmpty()) {
            // Player has no profession -> cancel restricted items
            event.setCancelled(true);
            player.sendMessage(ChatColors.BORDER_COLOR + mm.get(MessageKey.NO_PROFESSION));
            return;
        }

        var profession = data.getProfession().get();
        if (!profession.canUseTool(material)) {
            // Player has a profession but cannot use this tool
            event.setCancelled(true);
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColors.DESCRIPTION_COLOR + mm.get(MessageKey.CANNOT_USE_TOOL))
            );
        }
    }

    private boolean isRestrictedTool(Material material) {
        if (plugin.getDefaultAllowedTools().contains(material)) {
            return false;
        }
        var name = material.name();
        // Check for tools
        return name.endsWith("_PICKAXE") ||
                name.endsWith("_AXE") ||
                name.endsWith("_SHOVEL") ||
                name.endsWith("_HOE");
    }


}