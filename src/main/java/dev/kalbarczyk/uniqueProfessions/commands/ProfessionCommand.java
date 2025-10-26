package dev.kalbarczyk.uniqueProfessions.commands;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import dev.kalbarczyk.uniqueProfessions.messages.MessageKey;
import dev.kalbarczyk.uniqueProfessions.messages.MessageManager;
import dev.kalbarczyk.uniqueProfessions.player.PlayerData;
import dev.kalbarczyk.uniqueProfessions.utils.ChatColors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ProfessionCommand implements CommandExecutor {

    private final UniqueProfessions plugin;
    private final MessageManager mm;


    public ProfessionCommand() {
        this.plugin = UniqueProfessions.getInstance();
        this.mm = plugin.getMessageManager();
    }

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
            case "help":
                showHelp(player);
                break;
            case "choose":
                if (args.length < 2) {
                    showAvailableProfessions(player);
                } else {
                    chooseProfession(player, data, args[1]);
                }
                break;
            case "info":
                showProfessionInfo(player, data);
                break;
            case "list":
                showAvailableProfessions(player);
                break;
            case "reset":
                resetProfession(player, data);
                break;
            default:
                player.sendMessage(mm.format(MessageKey.UNKNOWN_COMMAND, "command", "/up help"));
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(final Player player) {
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColors.COMMAND_COLOR + "/up choose <name> " + ChatColors.DESCRIPTION_COLOR
                + "- " + mm.get(MessageKey.HELP_CHOOSE));
        player.sendMessage(ChatColors.COMMAND_COLOR + "/up info " + ChatColors.DESCRIPTION_COLOR
                + "- " + mm.get(MessageKey.HELP_INFO));
        player.sendMessage(ChatColors.COMMAND_COLOR + "/up list " + ChatColors.DESCRIPTION_COLOR
                + "- " + mm.get(MessageKey.HELP_LIST));
        player.sendMessage(ChatColors.COMMAND_COLOR + "/up " + ChatColors.DESCRIPTION_COLOR
                + "- " + mm.get(MessageKey.HELP_RESET));
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void showAvailableProfessions(final Player player) {
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColors.COMMAND_COLOR + mm.get(MessageKey.PROFESSION_INFO_HEADER));
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (var profession : plugin.getProfessionManager().getAll()) {
            var line = ChatColors.COMMAND_COLOR + profession.displayName() + " "
                    + ChatColors.DESCRIPTION_COLOR + "- " + profession.description();
            player.sendMessage(line);
        }

        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColors.COMMAND_COLOR + mm.get(MessageKey.HELP_CHOOSE) + ": "
                + ChatColors.DESCRIPTION_COLOR + "/up choose <name>");
    }

    private void chooseProfession(final Player player, final PlayerData data, final String professionName) {
        if (data.getProfession().isPresent()) {
            player.sendMessage(mm.get(MessageKey.PROFESSION_ALREADY_SELECTED));
            return;
        }

        var type = plugin.getProfessionManager().get(professionName);


        if (type == null) {
            player.sendMessage(ChatColor.RED + mm.format(MessageKey.INVALID_PROFESSION, "profession", professionName));
            return;
        }

        data.setProfession(type);
        plugin.getPlayerDataManager().savePlayerData(player);

        data.setProfession(type);
        plugin.getPlayerDataManager().savePlayerData(player);

        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColors.COMMAND_COLOR + mm.format(MessageKey.PROFESSION_SELECTED, "profession", type.displayName()));
        player.sendMessage(ChatColors.DESCRIPTION_COLOR + type.description());
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void showProfessionInfo(final Player player, final PlayerData data) {
        if (data.getProfession().isEmpty()) {
            player.sendMessage(mm.get(MessageKey.NO_PROFESSION));
            return;
        }

        var profession = plugin.getProfessionManager().get(data.getProfession().get().displayName());
        if (profession == null) return;

        var allowedTools = profession.allowedTools();

        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━ " + ChatColors.COMMAND_COLOR + mm.format(MessageKey.PROFESSION_INFO_HEADER, "profession", profession.displayName()) + ChatColors.BORDER_COLOR + " ━━━━━");
        player.sendMessage(ChatColors.DESCRIPTION_COLOR + profession.description());
        player.sendMessage(ChatColors.COMMAND_COLOR + mm.get(MessageKey.PROFESSION_INFO_ALLOWED_TOOLS_HEADER) + ":");
        for (var tool : allowedTools) {
            player.sendMessage(ChatColors.DESCRIPTION_COLOR + "- " + tool.name());
        }
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }


    private void resetProfession(final Player player, final PlayerData data) {
        if (data.getProfession().isEmpty()) {
            player.sendMessage(mm.get(MessageKey.NO_PROFESSION));
            return;
        }

        var oldProfession = data.getProfession();
        data.resetProfession();
        plugin.getPlayerDataManager().savePlayerData(player);

        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColors.COMMAND_COLOR + mm.format(MessageKey.PROFESSION_RESET, "profession", oldProfession.get().displayName()));
        player.sendMessage(ChatColors.BORDER_COLOR + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}