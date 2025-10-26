package dev.kalbarczyk.uniqueProfessions.commands;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.player.PlayerData;
import dev.kalbarczyk.uniqueProfessions.profession.ProfessionType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public record ProfessionCommand(UniqueProfessions plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(
            final @Nonnull CommandSender sender,
            final @Nonnull Command command,
            final @Nonnull String label,
            final @Nonnull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(player);

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "choose":
                if (args.length < 2) {
                    showAvailableProfessions(player);
                } else {
                    chooseProfession(player, data, args[1]);
                }
                break;
            case "info":
                showProfessionInfo(player, data);
            case "list":
                showAvailableProfessions(player);
                break;

            case "reset":
                resetProfession(player, data);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown command!");
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(final Player player) {
        player.sendMessage(ChatColor.GOLD + "━━━━━ " + ChatColor.YELLOW + "Profession Commands" + ChatColor.GOLD + " ━━━━━");
        player.sendMessage(ChatColor.YELLOW + "/profession choose <name>" + ChatColor.GRAY + " - Choose a profession");
        player.sendMessage(ChatColor.YELLOW + "/profession info" + ChatColor.GRAY + " - View your profession stats");
        player.sendMessage(ChatColor.YELLOW + "/profession list" + ChatColor.GRAY + " - List all professions");
        player.sendMessage(ChatColor.YELLOW + "/profession reset" + ChatColor.GRAY + " - Reset your profession");
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void showAvailableProfessions(final Player player) {
        player.sendMessage(ChatColor.GOLD + "━━━━━ " + ChatColor.YELLOW + "Available Professions" + ChatColor.GOLD + " ━━━━━");

        for (var type : ProfessionType.values()) {
            player.sendMessage(type.getColoredName() + ChatColor.GRAY + " - " + type.getDescription());
        }

        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.WHITE + "/profession choose <name>" + ChatColor.YELLOW + " to select");
    }

    private void chooseProfession(final Player player, final PlayerData data, final String professionName) {
        if (data.hasProfession()) {
            player.sendMessage(ChatColor.RED + "You already have a profession! Use /profession reset first.");
            return;
        }

        var type = ProfessionType.fromString(professionName);

        if (type == null) {
            player.sendMessage(ChatColor.RED + "Invalid profession! Use /profession list to see available professions.");
            return;
        }

        data.setProfession(type);
        plugin.getPlayerDataManager().savePlayerData(player);

        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColor.GREEN + "✓ Profession Selected!");
        player.sendMessage(ChatColor.YELLOW + "You are now a " + type.getColoredName());
        player.sendMessage(ChatColor.GRAY + type.getDescription());
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void showProfessionInfo(final Player player, final PlayerData data) {
        if (!data.hasProfession()) {
            player.sendMessage(ChatColor.RED + "You don't have a profession yet! Use /profession choose");
            return;
        }

        var profession = plugin.getProfessionManager().getProfession(data.getProfession());
        if (profession == null) return;

        double requiredXp = profession.getXpRequiredForLevel(data.getLevel() + 1);
        int maxLevel = profession.getMaxLevel();

        player.sendMessage(ChatColor.GOLD + "━━━━━ " + ChatColor.YELLOW + "Profession Info" + ChatColor.GOLD + " ━━━━━");
        player.sendMessage(ChatColor.YELLOW + "Profession: " + data.getProfession().getColoredName());
        player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + data.getLevel() + "/" + maxLevel);

        if (data.getLevel() < maxLevel) {
            player.sendMessage(ChatColor.YELLOW + "Experience: " + ChatColor.WHITE +
                    String.format("%.1f", data.getExperience()) + "/" +
                    String.format("%.1f", requiredXp) + " XP");

            int progress = (int) ((data.getExperience() / requiredXp) * 100);
            player.sendMessage(ChatColor.YELLOW + "Progress: " + createProgressBar(progress));
        } else {
            player.sendMessage(ChatColor.GREEN + "✓ MAX LEVEL REACHED!");
        }

        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private String createProgressBar(final int percentage) {
        int bars = 20;
        int filled = (int) ((percentage / 100.0) * bars);

        var bar = new StringBuilder(ChatColor.GREEN + "[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append(ChatColor.GRAY).append("█");
            }
        }
        bar.append(ChatColor.GREEN).append("] ").append(ChatColor.WHITE).append(percentage).append("%");

        return bar.toString();
    }

    private void resetProfession(final Player player, final PlayerData data) {
        if (!data.hasProfession()) {
            player.sendMessage(ChatColor.RED + "You don't have a profession to reset!");
            return;
        }

        //TODO: add cost of resetting profession

        var oldProfession = data.getProfession();
        data.resetProfession();
        plugin.getPlayerDataManager().savePlayerData(player);

        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColor.YELLOW + "Your " + oldProfession.getColoredName() + ChatColor.YELLOW + " profession has been reset.");
        player.sendMessage(ChatColor.GRAY + "Use /profession choose to select a new profession.");
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}