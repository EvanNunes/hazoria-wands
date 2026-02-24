package hazoria.fr.hazoriaWands.spell.impl;

import hazoria.fr.hazoriaWands.spell.Spell;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtegoSpell implements Spell {

    public static final String META_PROTEGO_UNTIL = "hazoria_protego_until";
    private final JavaPlugin plugin;

    public ProtegoSpell(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String id() { return "protego"; }
    @Override public String displayName() { return "Protego"; }
    @Override public int manaCost() { return 20; }
    @Override public long cooldownMs() { return 5000; }

    @Override
    public void cast(Player caster) {
        long until = System.currentTimeMillis() + 3000; // 3 sec
        caster.setMetadata(META_PROTEGO_UNTIL, new FixedMetadataValue(plugin, until));
        caster.getWorld().spawnParticle(Particle.ENCHANT, caster.getLocation().add(0, 1, 0),
                30, 0.6, 0.6, 0.6, 0.0);
    }
}