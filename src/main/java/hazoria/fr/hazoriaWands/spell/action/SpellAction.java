package hazoria.fr.hazoriaWands.spell.action;

import hazoria.fr.hazoriaWands.spell.action.impl.DamageAction;
import hazoria.fr.hazoriaWands.spell.action.impl.PotionEffectAction;
import hazoria.fr.hazoriaWands.spell.action.impl.RaytraceHitAction;
import hazoria.fr.hazoriaWands.spell.action.impl.ShieldAction;
import org.bukkit.configuration.ConfigurationSection;

public interface SpellAction {

    void execute(SpellContext ctx);

    static SpellAction fromConfig(ConfigurationSection section) {
        String cls = section.getString("class", "");
        return switch (cls.toLowerCase()) {
            case "raytracehit" -> new RaytraceHitAction(section);
            case "damage" -> new DamageAction(section);
            case "potioneffect" -> new PotionEffectAction(section);
            case "shield" -> new ShieldAction(section);
            default -> throw new IllegalArgumentException("Unknown action class: " + cls);
        };
    }
}
