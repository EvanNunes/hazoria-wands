package hazoria.fr.hazoriaWands.wand;

import hazoria.fr.hazoriaWands.player.PlayerDataService;
import hazoria.fr.hazoriaWands.util.Colors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WandItemService {

    private final JavaPlugin plugin;
    private final PlayerDataService playerDataService;

    private final NamespacedKey KEY_WAND;
    private final NamespacedKey KEY_OWNER;
    private final NamespacedKey KEY_WAND_TYPE;

    public WandItemService(JavaPlugin plugin, PlayerDataService playerDataService) {
        this.plugin = plugin;
        this.playerDataService = playerDataService;
        this.KEY_WAND      = new NamespacedKey(plugin, "wand");
        this.KEY_OWNER     = new NamespacedKey(plugin, "owner");
        this.KEY_WAND_TYPE = new NamespacedKey(plugin, "wand_type");
    }

    public ItemStack createWand(Player owner, WandType type) {
        Material mat;
        try {
            mat = Material.valueOf(type.material.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Unknown material '" + type.material + "', defaulting to STICK");
            mat = Material.STICK;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();

        meta.setDisplayName(Colors.color(type.displayName));
        meta.setCustomModelData(type.customModelData);

        List<String> coloredLore = new ArrayList<>();
        for (String line : type.lore) coloredLore.add(Colors.color(line));
        meta.setLore(coloredLore);

        applyTooltipStyle(meta, type.tooltip);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_WAND,      PersistentDataType.BYTE,   (byte) 1);
        pdc.set(KEY_OWNER,     PersistentDataType.STRING, owner.getUniqueId().toString());
        pdc.set(KEY_WAND_TYPE, PersistentDataType.STRING, type.id);

        item.setItemMeta(meta);

        // Initialise les sorts équipés du joueur s'ils sont vides (premier wand)
        List<String> equipped = playerDataService.loadEquippedSpells(owner.getUniqueId());
        if (equipped.isEmpty() && !type.defaultSpells.isEmpty()) {
            List<String> seed = new ArrayList<>(type.defaultSpells);
            playerDataService.saveEquippedSpells(owner.getUniqueId(), seed);
            playerDataService.saveSelectedIndex(owner.getUniqueId(), 0);
        }

        return item;
    }

    public boolean isWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEY_WAND, PersistentDataType.BYTE);
    }

    public UUID getOwner(ItemStack item) {
        if (!isWand(item)) return null;
        String raw = item.getItemMeta().getPersistentDataContainer().get(KEY_OWNER, PersistentDataType.STRING);
        if (raw == null) return null;
        try { return UUID.fromString(raw); } catch (IllegalArgumentException e) { return null; }
    }

    public boolean isOwner(Player player, ItemStack item) {
        UUID owner = getOwner(item);
        return owner != null && owner.equals(player.getUniqueId());
    }

    public List<String> getSpells(UUID playerId) {
        return playerDataService.loadEquippedSpells(playerId);
    }

    public void setSpells(UUID playerId, List<String> spells) {
        playerDataService.saveEquippedSpells(playerId, spells);
    }

    public int getSelectedIndex(UUID playerId) {
        return playerDataService.loadSelectedIndex(playerId);
    }

    public void setSelectedIndex(UUID playerId, int idx) {
        playerDataService.saveSelectedIndex(playerId, idx);
    }

    public void cycleSpell(UUID playerId) {
        List<String> spells = getSpells(playerId);
        if (spells.isEmpty()) return;
        int idx = (getSelectedIndex(playerId) + 1) % spells.size();
        setSelectedIndex(playerId, idx);
    }

    public String getSelectedSpellId(UUID playerId) {
        List<String> spells = getSpells(playerId);
        if (spells.isEmpty()) return null;
        int idx = getSelectedIndex(playerId);
        if (idx >= spells.size()) idx = 0;
        return spells.get(idx);
    }

    private void applyTooltipStyle(ItemMeta meta, String tooltip) {
        if (tooltip == null || tooltip.isBlank()) return;

        NamespacedKey key = NamespacedKey.fromString(tooltip);
        if (key == null) {
            plugin.getLogger().warning("Invalid tooltip key '" + tooltip + "' (expected 'namespace:id')");
            return;
        }
        meta.setTooltipStyle(key);
    }
}
