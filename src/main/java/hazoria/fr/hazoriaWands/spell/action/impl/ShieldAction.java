package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class ShieldAction implements SpellAction {

    public static final String META_SHIELD_UNTIL = "hazoria_shield_until";
    public static final String META_SHIELD_REDUCTION = "hazoria_shield_reduction";

    private final long durationMs;
    private final double damageReduction;
    private final JavaPlugin plugin;

    public ShieldAction(ConfigurationSection section) {
        this.durationMs = section.getLong("duration_ms", 3000);
        this.damageReduction = section.getDouble("damage_reduction", 0.6);
        this.plugin = JavaPlugin.getProvidingPlugin(ShieldAction.class);
    }

    @Override
    public void execute(SpellContext ctx) {
        long until = System.currentTimeMillis() + durationMs;
        ctx.caster.setMetadata(META_SHIELD_UNTIL, new FixedMetadataValue(plugin, until));
        ctx.caster.setMetadata(META_SHIELD_REDUCTION, new FixedMetadataValue(plugin, damageReduction));
    }
}
