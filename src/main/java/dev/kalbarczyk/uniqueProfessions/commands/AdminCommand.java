package dev.kalbarczyk.uniqueProfessions.commands;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.profession.ProfessionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;


public record AdminCommand(UniqueProfessions plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(
            final @Nonnull CommandSender sender,
            final @Nonnull Command command,
            final @Nonnull String label,
            final @Nonnull String[] args) {
        if (!sender.hasPermission("profession.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            showAdminHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /professionadmin set <player> <profession>");
                } else {
                    setProfession(sender, args[1], args[2]);
                }
                break;

            case "setlevel":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /professionadmin setlevel <player> <level>");
                } else {
                    setLevel(sender, args[1], args[2]);
                }
                break;

            case "addxp":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /professionadmin addxp <player> <amount>");
                } else {
                    addXP(sender, args[1], args[2]);
                }
                break;

            case "reset":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /professionadmin reset <player>");
                } else {
                    resetPlayer(sender, args[1]);
                }
                break;

            case "reload":
                reloadConfig(sender);
                break;

            case "info":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /professionadmin info <player>");
                } else {
                    showPlayerInfo(sender, args[1]);
                }
                break;

            default:
                showAdminHelp(sender);
                break;
        }

        return true;
    }

    private void showAdminHelp(final CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "━━━━━ " + ChatColor.YELLOW + "Profession Admin Commands" + ChatColor.GOLD + " ━━━━━");
        sender.sendMessage(ChatColor.YELLOW + "/professionadmin set <p> <prof>" + ChatColor.GRAY + " - Set player's profession");
        sender.sendMessage(ChatColor.YELLOW + "/professionadmin setlevel <p> <lvl>" + ChatColor.GRAY + " - Set player's level");
        sender.sendMessage(ChatColor.YELLOW + "/professionadmin addxp <p> <xp>" + ChatColor.GRAY + " - Add XP to player");
        sender.sendMessage(ChatColor.YELLOW + "/professionadmin reset <p>" + ChatColor.GRAY + " - Reset player's profession");
        sender.sendMessage(ChatColor.YELLOW + "/professionadmin info <p>" + ChatColor.GRAY + " - View player's info");
        sender.sendMessage(ChatColor.YELLOW + "/professionadmin reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void setProfession(final CommandSender sender, final String playerName, final String professionName) {
        var target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        var type = ProfessionType.fromString(professionName);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Invalid profession!");
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(target);
        data.setProfession(type);
        plugin.getPlayerDataManager().savePlayerData(target);

        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s profession to " + type.getColoredName());
        target.sendMessage(ChatColor.YELLOW + "Your profession has been set to " + type.getColoredName());
    }

    private void setLevel(final CommandSender sender, final String playerName, final String levelStr) {
        var target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        int level;
        try {
            level = Integer.parseInt(levelStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid level number!");
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(target);
        if (!data.hasProfession()) {
            sender.sendMessage(ChatColor.RED + "Player doesn't have a profession!");
            return;
        }

        data.setLevel(level);
        plugin.getPlayerDataManager().savePlayerData(target);

        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s level to " + level);
        target.sendMessage(ChatColor.YELLOW + "Your level has been set to " + level);
    }

    private void addXP(final CommandSender sender, final String playerName, final String xpStr) {
        var target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        double xp;
        try {
            xp = Double.parseDouble(xpStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid XP amount!");
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(target);
        if (!data.hasProfession()) {
            sender.sendMessage(ChatColor.RED + "Player doesn't have a profession!");
            return;
        }

        data.addExperience(xp);
        plugin.getPlayerDataManager().savePlayerData(target);

        sender.sendMessage(ChatColor.GREEN + "Added " + xp + " XP to " + target.getName());
        target.sendMessage(ChatColor.YELLOW + "You received " + xp + " XP!");
    }

    private void resetPlayer(final CommandSender sender,final String playerName) {
        var target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(target);
        data.resetProfession();
        plugin.getPlayerDataManager().savePlayerData(target);

        sender.sendMessage(ChatColor.GREEN + "Reset " + target.getName() + "'s profession");
        target.sendMessage(ChatColor.YELLOW + "Your profession has been reset by an administrator");
    }

    private void reloadConfig(final CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
    }

    private void showPlayerInfo(final CommandSender sender,final String playerName) {
        var target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        var data = plugin.getPlayerDataManager().getPlayerData(target);

        sender.sendMessage(ChatColor.GOLD + "━━━━━ " + ChatColor.YELLOW + target.getName() + "'s Info" + ChatColor.GOLD + " ━━━━━");

        if (data.hasProfession()) {
            sender.sendMessage(ChatColor.YELLOW + "Profession: " + data.getProfession().getColoredName());
            sender.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + data.getLevel());
            sender.sendMessage(ChatColor.YELLOW + "Experience: " + ChatColor.WHITE + String.format("%.1f", data.getExperience()));
        } else {
            sender.sendMessage(ChatColor.RED + "No profession selected");
        }

        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}