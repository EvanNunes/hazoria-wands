package hazoria.fr.hazoriaWands.listener;

import hazoria.fr.hazoriaWands.spell.Spell;
import hazoria.fr.hazoriaWands.spell.SpellRegistry;
import hazoria.fr.hazoriaWands.spell.impl.ProtegoSpell;
import hazoria.fr.hazoriaWands.util.Colors;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import hazoria.fr.hazoriaWands.wand.WandState;
import hazoria.fr.hazoriaWands.wand.WandStateService;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WandListener implements Listener {

    private final WandItemService wandItemService;
    private final WandStateService wandStateService;
    private final SpellRegistry spellRegistry;
    private final YamlConfiguration messages;

    public WandListener(JavaPlugin plugin,
                        WandItemService wandItemService,
                        WandStateService wandStateService,
                        SpellRegistry spellRegistry) {
        this.wandItemService = wandItemService;
        this.wandStateService = wandStateService;
        this.spellRegistry = spellRegistry;
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!wandItemService.isWand(item)) return;

        e.setCancelled(true);

        if (!wandItemService.isOwner(p, item)) {
            p.sendMessage(Colors.color(messages.getString("not_owner")));
            return;
        }

        switch (e.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                wandItemService.cycleSpell(item);
                String spellId = wandItemService.getSelectedSpellId(item);
                p.sendMessage(Colors.color(messages.getString("spell_switched").replace("%spell%", String.valueOf(spellId))));
            }
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                String spellId = wandItemService.getSelectedSpellId(item);
                Spell spell = spellRegistry.get(spellId);
                if (spell == null) {
                    p.sendMessage(Colors.color(messages.getString("unknown_spell").replace("%spell%", String.valueOf(spellId))));
                    return;
                }

                WandState st = wandStateService.getOrCreate(p.getUniqueId());

                if (st.isOnCooldown(spell.id())) {
                    p.sendMessage(Colors.color(messages.getString("cast_denied_cd")));
                    return;
                }

                if (!wandStateService.tryConsumeMana(p.getUniqueId(), spell.manaCost())) {
                    p.sendMessage(Colors.color(messages.getString("cast_denied_mana")));
                    return;
                }

                st.setCooldown(spell.id(), spell.cooldownMs());
                spell.cast(p);
            }
            default -> {}
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!p.hasMetadata(ProtegoSpell.META_PROTEGO_UNTIL)) return;

        long until = p.getMetadata(ProtegoSpell.META_PROTEGO_UNTIL).get(0).asLong();
        if (System.currentTimeMillis() > until) return;

        // Bouclier: -60% dégâts
        e.setDamage(e.getDamage() * 0.4);
    }
}