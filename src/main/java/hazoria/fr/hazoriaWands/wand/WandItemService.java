package hazoria.fr.hazoriaWands.wand;

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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WandItemService {

    private final JavaPlugin plugin;

    private final NamespacedKey KEY_WAND;
    private final NamespacedKey KEY_OWNER;
    private final NamespacedKey KEY_SPELLS;
    private final NamespacedKey KEY_SELECTED;
    private final NamespacedKey KEY_WAND_TYPE;

    public WandItemService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.KEY_WAND      = new NamespacedKey(plugin, "wand");
        this.KEY_OWNER     = new NamespacedKey(plugin, "owner");
        this.KEY_SPELLS    = new NamespacedKey(plugin, "spells");
        this.KEY_SELECTED  = new NamespacedKey(plugin, "selected");
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

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_WAND,      PersistentDataType.BYTE,   (byte) 1);
        pdc.set(KEY_OWNER,     PersistentDataType.STRING, owner.getUniqueId().toString());
        pdc.set(KEY_WAND_TYPE, PersistentDataType.STRING, type.id);

        setSpells(meta, type.defaultSpells);
        setSelectedIndex(meta, 0);

        item.setItemMeta(meta);
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

    public List<String> getSpells(ItemStack item) {
        if (!isWand(item)) return List.of();
        ItemMeta meta = item.getItemMeta();
        String raw = meta.getPersistentDataContainer().get(KEY_SPELLS, PersistentDataType.STRING);
        if (raw == null || raw.isBlank()) return List.of();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public void setSpells(ItemMeta meta, List<String> spells) {
        meta.getPersistentDataContainer().set(KEY_SPELLS, PersistentDataType.STRING, String.join(",", spells));
    }

    public int getSelectedIndex(ItemStack item) {
        if (!isWand(item)) return 0;
        Integer idx = item.getItemMeta().getPersistentDataContainer().get(KEY_SELECTED, PersistentDataType.INTEGER);
        return idx == null ? 0 : Math.max(0, idx);
    }

    public void setSelectedIndex(ItemMeta meta, int idx) {
        meta.getPersistentDataContainer().set(KEY_SELECTED, PersistentDataType.INTEGER, Math.max(0, idx));
    }

    public void cycleSpell(ItemStack wand) {
        if (!isWand(wand)) return;
        List<String> spells = getSpells(wand);
        if (spells.isEmpty()) return;

        ItemMeta meta = wand.getItemMeta();
        int idx = getSelectedIndex(wand);
        idx = (idx + 1) % spells.size();
        setSelectedIndex(meta, idx);
        wand.setItemMeta(meta);
    }

    public String getSelectedSpellId(ItemStack wand) {
        List<String> spells = getSpells(wand);
        if (spells.isEmpty()) return null;
        int idx = getSelectedIndex(wand);
        if (idx >= spells.size()) idx = 0;
        return spells.get(idx);
    }
}
