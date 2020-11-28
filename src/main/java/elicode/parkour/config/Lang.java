package elicode.parkour.config;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import elicode.parkour.Main;
import elicode.parkour.util.Reloadable;
import elicode.parkour.util.StringUtil;
import elicode.parkour.util.config.AbstractConfiguration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Lang extends AbstractConfiguration<Main> implements Reloadable {

    private final Config config;
    private final Map<String, String> messages = new HashMap<>();

    public Lang(final Main plugin) {
        super(plugin, "lang");
        this.config = plugin.getConfiguration();
    }

    @Override
    protected void loadValues(FileConfiguration configuration) throws Exception {
       /* if (configuration.getInt("config-version", 0) < getLatestVersion()) {
            configuration = convert(null);
        }*/

        final Map<String, String> strings = new HashMap<>();

        for (String key : configuration.getKeys(true)) {
            if (key.equals("config-version")) {
                continue;
            }

            // Fixes a weird occurrence with FileConfiguration#getKeys that an extra separator char is prepended when called after FileConfiguration#set
            if (key.startsWith(".")) {
                key = key.substring(1);
            }

            final Object value = configuration.get(key);

            if (value == null || value instanceof MemorySection) {
                continue;
            }

            final String message = value instanceof List ? StringUtil.fromList((List<?>) value) : value.toString();

            if (key.startsWith("STRINGS")) {
                final String[] args = key.split(Pattern.quote("."));
                strings.put(args[args.length - 1], message);
            } else {
                messages.put(key, message);
            }
        }

        messages.replaceAll((key, value) -> {
            for (final Map.Entry<String, String> entry : strings.entrySet()) {
                final String placeholder = "{" + entry.getKey() + "}";

                if (StringUtils.containsIgnoreCase(value, placeholder)) {
                    value = value.replaceAll("(?i)" + Pattern.quote(placeholder), entry.getValue());
                }
            }

            return value;
        });
    }

    @Override
    protected Set<String> transferredSections() {
        return Sets.newHashSet("STRINGS");
    }

    @Override
    public void handleUnload() {
        messages.clear();
    }

    private String getRawMessage(final String key) {
        final String message = messages.get(key);

        if (message == null) {
            Main.getPlugin().debug("Failed to load message: provided key '\" + key + \"' has no assigned value");
            return null;
        }

        // Allow disabling any message by setting it to ''
        if (message.isEmpty()) {
            return null;
        }

        return message;
    }

    public String getMessage(final String key) {
        final String message = getRawMessage(key);

        if (message == null) {
            return null;
        }

        return StringUtil.color(message);
    }

    public String getMessage(final String key, final Object... replacers) {
        final String message = getMessage(key);

        if (message == null) {
            return null;
        }

        return replace(message, replacers);
    }

    private String replace(String message, Object... replacers) {
        if (replacers.length == 1 && replacers[0] instanceof Object[]) {
            replacers = (Object[]) replacers[0];
        }

        for (int i = 0; i < replacers.length; i += 2) {
            if (i + 1 >= replacers.length) {
                break;
            }

            message = message.replace("%" + replacers[i].toString() + "%", String.valueOf(replacers[i + 1]));
        }

        return message;
    }

    public void sendMessage(final CommandSender receiver, final String key, final Object... replacers) {
        final String message = getRawMessage(key);

        if (message == null) {
            return;
        }

        if (receiver instanceof Player) {
            //config.playSound((Player) receiver, message);
        }

        receiver.sendMessage(StringUtil.color(replace(message, replacers)));
    }

    public void sendMessage(final Collection<Player> players, final String key, final Object... replacers) {
        final String message = getMessage(key, replacers);

        if (message == null) {
            return;
        }

        players.forEach(player -> player.sendMessage(message));
    }
}
