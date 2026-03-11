package hazoria.fr.hazoriaWands.player;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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
        YamlConfiguration config = new YamlConfiguration();
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
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("mana", defaultMana);
    }
}
