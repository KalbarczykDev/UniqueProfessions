package dev.kalbarczyk.uniqueProfessions.messages;

public enum MessageKey {

    NO_PERMISSION("no-permission"),
    UNKNOWN_COMMAND("unknown-command"),
    HELP_CHOOSE("help-message.choose"),
    HELP_INFO("help-message.info"),
    HELP_LIST("help-message.list"),
    HELP_RESET("help-message.reset"),

    WELCOME_MESSAGE_CHOOSE("welcome-message-choose"),
    WELCOME_MESSAGE_PROFESSION("welcome-message-professions"),

    PROFESSION_INFO_HEADER("profession-info.header"),
    PROFESSION_INFO_NO_PROFESSION("profession-info.no-profession"),
    PROFESSION_INFO_CURRENT("profession-info.current"),
    PROFESSION_INFO_DESCRIPTION("profession-info.description"),
    PROFESSION_INFO_ALLOWED_PROFESSIONS_HEADER("profession-info.allowed-tools-header"),

    INVALID_PROFESSION("invalid-profession"),
    PROFESSION_ALREADY_SELECTED("profession-already-selected"),
    PROFESSION_SELECTED("profession-selected"),
    PROFESSION_RESET("profession-reset"),
    NO_PROFESSION("no-profession"),
    CANNOT_USE_TOOL("cannot-use-tool");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
