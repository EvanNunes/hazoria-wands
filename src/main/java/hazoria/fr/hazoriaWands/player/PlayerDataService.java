package hazoria.fr.hazoriaWands.player;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataService {

    private final JavaPlugin plugin;
    private final File dataFolder;

    public PlayerDataService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        dataFolder.mkdirs();
    }

    public void save(UUID uuid, int mana) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();
        config.set("mana", mana);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data for " + uuid + ": " + e.getMessage());
        }
    }

    public int loadMana(UUID uuid, int defaultMana) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (!file.exists()) return defaultMana;
        return YamlConfiguration.loadConfiguration(file).getInt("mana", defaultMana);
    }

    public void saveUnlockedSpells(UUID uuid, List<String> spells) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();
        config.set("unlocked_spells", spells);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save unlocked spells for " + uuid + ": " + e.getMessage());
        }
    }

    public List<String> loadUnlockedSpells(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (!file.exists()) return new ArrayList<>();
        return new ArrayList<>(YamlConfiguration.loadConfiguration(file).getStringList("unlocked_spells"));
    }

    public List<String> loadEquippedSpells(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (!file.exists()) return new ArrayList<>();
        return new ArrayList<>(YamlConfiguration.loadConfiguration(file).getStringList("equipped_spells"));
    }

    public void saveEquippedSpells(UUID uuid, List<String> spells) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();
        config.set("equipped_spells", spells);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save equipped spells for " + uuid + ": " + e.getMessage());
        }
    }

    public int loadSelectedIndex(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (!file.exists()) return 0;
        return Math.max(0, YamlConfiguration.loadConfiguration(file).getInt("selected_index", 0));
    }

    public void saveSelectedIndex(UUID uuid, int idx) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration config = file.exists()
                ? YamlConfiguration.loadConfiguration(file)
                : new YamlConfiguration();
        config.set("selected_index", Math.max(0, idx));
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save selected index for " + uuid + ": " + e.getMessage());
        }
    }
}
