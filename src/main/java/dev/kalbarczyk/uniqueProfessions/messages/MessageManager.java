package dev.kalbarczyk.uniqueProfessions.messages;

import dev.kalbarczyk.uniqueProfessions.UniqueProfessions;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;


public class MessageManager {
    private final UniqueProfessions plugin;
    private final Map<String, String> messages = new HashMap<>();

    public MessageManager() {
        this.plugin = UniqueProfessions.getInstance();
        loadMessages();
    }

    public void loadMessages() {
        var config = plugin.getConfigManager().getConfig();
        var section = config.getConfigurationSection("messages");
        if (section != null) {
            loadSection(section, "");
        }
    }

    private void loadSection(org.bukkit.configuration.ConfigurationSection section, String prefix) {
        for (var key : section.getKeys(false)) {
            var fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            var sub = section.getConfigurationSection(key);
            if (sub != null) {
                loadSection(sub, fullKey);
            } else {
                var value = section.getString(key, "");
                messages.put(fullKey, ChatColor.translateAlternateColorCodes('&', value));
            }
        }
    }

    public String get(MessageKey key) {
        return messages.getOrDefault(key.path(), key.path());
    }

    public String format(final MessageKey key, final Object... placeholders) {
        var msg = get(key);
        for (int i = 0; i < placeholders.length; i += 2) {
            msg = msg.replace("{" + placeholders[i] + "}", String.valueOf(placeholders[i + 1]));
        }
        return msg;
    }
}
