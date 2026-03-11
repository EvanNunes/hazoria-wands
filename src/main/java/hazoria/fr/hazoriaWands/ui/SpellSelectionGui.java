package hazoria.fr.hazoriaWands.ui;

import hazoria.fr.hazoriaWands.player.PlayerDataService;
import hazoria.fr.hazoriaWands.spell.Spell;
import hazoria.fr.hazoriaWands.spell.SpellRegistry;
import hazoria.fr.hazoriaWands.util.Colors;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpellSelectionGui implements Listener {

    public static final String TITLE = "§6✦ Sorts équipés ✦";
    private static final int MAX_EQUIPPED = 5;
    private static final int SPELLS_AREA_SIZE = 36; // rows 0-3
    private static final int SEPARATOR_START  = 36; // row 4
    private static final int EQUIPPED_START   = 45; // row 5

    private final SpellRegistry spellRegistry;
    private final WandItemService wandItemService;
    private final PlayerDataService playerDataService;

    public SpellSelectionGui(SpellRegistry spellRegistry, WandItemService wandItemService,
                              PlayerDataService playerDataService) {
        this.spellRegistry     = spellRegistry;
        this.wandItemService   = wandItemService;
        this.playerDataService = playerDataService;
    }

    public void open(Player player, ItemStack wand) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        populate(inv, player, wand);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);

        int slot = e.getRawSlot();
        if (slot < 0 || slot >= 54) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR
                || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        ItemStack wand = player.getInventory().getItemInMainHand();
        if (!wandItemService.isWand(wand) || !wandItemService.isOwner(player, wand)) {
            player.closeInventory();
            player.sendMessage(Colors.color("&cTenez votre baguette en main pour gérer vos sorts."));
            return;
        }

        List<String> unlocked = playerDataService.loadUnlockedSpells(player.getUniqueId());
        List<String> equipped  = new ArrayList<>(wandItemService.getSpells(wand));

        if (slot < SPELLS_AREA_SIZE) {
            if (slot >= unlocked.size()) return;
            String spellId = unlocked.get(slot);

            if (equipped.contains(spellId)) {
                equipped.remove(spellId);
            } else if (equipped.size() < MAX_EQUIPPED) {
                equipped.add(spellId);
            } else {
                player.sendMessage(Colors.color("&cMaximum &e" + MAX_EQUIPPED + " &csorts équipés !"));
                return;
            }
            applyAndRefresh(wand, equipped, e.getInventory(), player);

        } else if (slot >= EQUIPPED_START && slot < EQUIPPED_START + MAX_EQUIPPED) {
            int idx = slot - EQUIPPED_START;
            if (idx < equipped.size()) {
                equipped.remove(idx);
                applyAndRefresh(wand, equipped, e.getInventory(), player);
            }
        }
    }

    private void applyAndRefresh(ItemStack wand, List<String> equipped, Inventory inv, Player player) {
        ItemMeta meta = wand.getItemMeta();
        wandItemService.setSpells(meta, equipped);
        wandItemService.setSelectedIndex(meta, 0);
        wand.setItemMeta(meta);
        inv.clear();
        populate(inv, player, wand);
    }

    private void populate(Inventory inv, Player player, ItemStack wand) {
        List<String> unlocked = playerDataService.loadUnlockedSpells(player.getUniqueId());
        List<String> equipped  = new ArrayList<>(wandItemService.getSpells(wand));

        // Sorts débloqués (rows 0-3)
        for (int i = 0; i < unlocked.size() && i < SPELLS_AREA_SIZE; i++) {
            String spellId = unlocked.get(i);
            Spell spell = spellRegistry.get(spellId);
            boolean isEquipped = equipped.contains(spellId);
            int equippedSlot = equipped.indexOf(spellId) + 1;
            inv.setItem(i, buildSpellItem(spell, spellId, isEquipped, equippedSlot));
        }

        // Séparateur (row 4)
        ItemStack sep = glassPane();
        for (int i = SEPARATOR_START; i < SEPARATOR_START + 9; i++) inv.setItem(i, sep);

        // Slots équipés (row 5, slots 45-49)
        for (int i = 0; i < MAX_EQUIPPED; i++) {
            if (i < equipped.size()) {
                Spell spell = spellRegistry.get(equipped.get(i));
                String name = spell != null ? spell.displayName() : equipped.get(i);
                inv.setItem(EQUIPPED_START + i, buildEquippedSlotItem(name, i + 1));
            } else {
                inv.setItem(EQUIPPED_START + i, buildEmptySlotItem(i + 1));
            }
        }
        // Remplissage du reste de la row 5
        for (int i = EQUIPPED_START + MAX_EQUIPPED; i < 54; i++) inv.setItem(i, glassPane());
    }

    private ItemStack buildSpellItem(Spell spell, String spellId, boolean equipped, int equippedSlot) {
        ItemStack item = new ItemStack(equipped ? Material.ENCHANTED_BOOK : Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        String displayName = spell != null ? spell.displayName() : spellId;
        if (equipped) {
            meta.setDisplayName(Colors.color("&6" + displayName + " &7(Slot " + equippedSlot + ")"));
        } else {
            meta.setDisplayName(Colors.color("&f" + displayName));
        }

        List<String> lore = new ArrayList<>();
        if (spell != null) {
            lore.add(Colors.color("&7Mana : &b" + spell.manaCost()));
            lore.add(Colors.color("&7Cooldown : &e" + (spell.cooldownMs() / 1000.0) + "s"));
        }
        lore.add("");
        lore.add(equipped
                ? Colors.color("&a✔ Équipé &8— clic pour retirer")
                : Colors.color("&7Clic pour équiper"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildEquippedSlotItem(String displayName, int slot) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colors.color("&6[" + slot + "] &f" + displayName));
        meta.setLore(List.of(Colors.color("&7Clic pour retirer")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildEmptySlotItem(int slot) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colors.color("&7[ Slot " + slot + " vide ]"));
        meta.setLore(List.of(Colors.color("&8Équipez un sort depuis la liste ci-dessus")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack glassPane() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
}
