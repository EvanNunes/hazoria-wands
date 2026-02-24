package hazoria.fr.hazoriaWands.util;

import org.bukkit.ChatColor;

public final class Colors {
    private Colors() {}

    public static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}