package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class KnockbackAction implements SpellAction {

    private final double strength;

    public KnockbackAction(ConfigurationSection section) {
        this.strength = section.getDouble("strength", 1.0);
    }

    @Override
    public void execute(SpellContext ctx) {
        if (ctx.target == null) return;

        Vector dir = ctx.target.getLocation()
                .subtract(ctx.caster.getLocation())
                .toVector();

        if (dir.lengthSquared() == 0) dir = ctx.caster.getLocation().getDirection();

        dir.normalize().setY(0.35).multiply(strength);
        ctx.target.setVelocity(dir);
    }
}
