package hazoria.fr.hazoriaWands.spell;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.effect.SpellEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpellLoader {

    private final JavaPlugin plugin;

    public SpellLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public List<Spell> load() {
        File file = new File(plugin.getDataFolder(), "spells.yml");
        if (!file.exists()) {
            plugin.saveResource("spells.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<Spell> result = new ArrayList<>();
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            try {
                result.add(loadSpell(key, section));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load spell '" + key + "': " + e.getMessage());
            }
        }
        return result;
    }

    private Spell loadSpell(String key, ConfigurationSection section) {
        String id = key.toLowerCase();
        String displayName = section.getString("name", key);

        ConfigurationSection params = section.getConfigurationSection("parameters");
        int mana      = params != null ? params.getInt("mana", 0)      : 0;
        long cooldown = params != null ? params.getLong("cooldown", 0) : 0;

        List<SpellAction> actions = new ArrayList<>();
        for (Map<?, ?> map : section.getMapList("actions.cast")) {
            try {
                actions.add(SpellAction.fromConfig(mapToSection(map)));
            } catch (Exception e) {
                plugin.getLogger().warning("Action failed in spell '" + key + "': " + e.getMessage());
            }
        }

        List<SpellEffect> effects = new ArrayList<>();
        for (Map<?, ?> map : section.getMapList("effects.cast")) {
            try {
                effects.add(SpellEffect.fromConfig(mapToSection(map)));
            } catch (Exception e) {
                plugin.getLogger().warning("Effect failed in spell '" + key + "': " + e.getMessage());
            }
        }

        return new ConfigSpell(id, displayName, mana, cooldown, actions, effects);
    }

    @SuppressWarnings("unchecked")
    private static ConfigurationSection mapToSection(Map<?, ?> map) {
        MemoryConfiguration config = new MemoryConfiguration();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String k = entry.getKey().toString();
            Object v = entry.getValue();
            if (v instanceof Map<?, ?> nested) {
                config.createSection(k, (Map<String, Object>) (Map<?, ?>) nested);
            } else {
                config.set(k, v);
            }
        }
        return config;
    }
}
