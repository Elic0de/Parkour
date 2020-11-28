package elicode.parkour.util.text;

import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.message.MessageStyle;
import org.apache.commons.codec.language.bm.Lang;
import org.bukkit.entity.Player;

public class LangText implements TextStream {

    private final Text japanise;
    private final UserSet users = UserSet.getInstnace();

    public LangText(String key) {
        this.japanise = new Text(key);
    }

    public static LangText stream(final String key) {
        return new LangText(key);
    }

    @Override
    public TextStream color(char alternateColorCode) {
        japanise.color(alternateColorCode);
        return this;
    }

    @Override
    public TextStream setAttribute(String name, Object value) {
        japanise.setAttribute(name, value);

        return this;
    }

    @Override
    public TextStream setAttribute(String name, MessageStyle messageStyle) {
        japanise.setAttribute(name, messageStyle);

        return this;
    }

    @Override
    public Text textBy(Player receiver) {
        User user = users.getUser(receiver);
        if (user.language != null) {
            return user.language.equals("ja_jp") ? japanise : japanise;
        } else {
            return receiver.getLocale().equals("ja_jp") ? japanise : japanise;
        }
    }

    //使用言語に対応したStringを返す
    public String toString(Player player) {
        return textBy(player).toString();
    }

}