package hazoria.fr.hazoriaWands.spell.action;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SpellContext {
    public final Player caster;
    public LivingEntity target;

    public SpellContext(Player caster) {
        this.caster = caster;
    }
}
