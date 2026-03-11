package hazoria.fr.hazoriaWands.wand;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WandTypeRegistry {

    private final JavaPlugin plugin;
    private final Map<String, WandType> types = new LinkedHashMap<>();

    public WandTypeRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        types.clear();
        File file = new File(plugin.getDataFolder(), "wands.yml");
        if (!file.exists()) {
            plugin.saveResource("wands.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            try {
                types.put(key.toLowerCase(), loadType(key, section));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load wand type '" + key + "': " + e.getMessage());
            }
        }
    }

    private WandType loadType(String key, ConfigurationSection section) {
        String displayName = section.getString("name", key);

        // Parse "stick{CustomModelData:10128}"
        String iconRaw = section.getString("icon", "STICK");
        String material = "STICK";
        int customModelData = 1001;
        if (iconRaw.contains("{")) {
            material = iconRaw.substring(0, iconRaw.indexOf('{')).toUpperCase();
            String inner = iconRaw.substring(iconRaw.indexOf('{') + 1, iconRaw.indexOf('}'));
            for (String part : inner.split(",")) {
                if (part.startsWith("CustomModelData:")) {
                    try {
                        customModelData = Integer.parseInt(part.substring("CustomModelData:".length()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } else {
            material = iconRaw.toUpperCase();
        }

        List<String> lore    = section.getStringList("lore");
        int manaMax          = section.getInt("mana_max", 100);
        int manaStart        = section.getInt("mana_start", 100);
        int manaRegen        = section.getInt("mana_regeneration", 5);
        String effectColor   = section.getString("effect_color", "ffffff");

        List<String> rawSpells = section.getStringList("spells");
        List<String> spellIds  = new ArrayList<>();
        for (String s : rawSpells) spellIds.add(s.toLowerCase());

        return new WandType(key.toLowerCase(), displayName, material, customModelData,
                lore, manaMax, manaStart, manaRegen, spellIds, effectColor);
    }

    public WandType get(String id) {
        return types.get(id.toLowerCase());
    }

    public WandType getDefault() {
        return types.isEmpty() ? null : types.values().iterator().next();
    }

    public Collection<String> getIds() {
        return types.keySet();
    }
}
