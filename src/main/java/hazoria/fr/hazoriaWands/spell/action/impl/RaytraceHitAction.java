package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;

public class RaytraceHitAction implements SpellAction {

    private final double range;

    public RaytraceHitAction(ConfigurationSection section) {
        this.range = section.getDouble("range", 12.0);
    }

    @Override
    public void execute(SpellContext ctx) {
        var world = ctx.caster.getWorld();
        var start = ctx.caster.getEyeLocation();
        var dir = start.getDirection();

        RayTraceResult hit = world.rayTraceEntities(
                start, dir, range,
                e -> (e instanceof LivingEntity le) && !le.getUniqueId().equals(ctx.caster.getUniqueId())
        );

        if (hit != null && hit.getHitEntity() instanceof LivingEntity target) {
            ctx.target = target;
        }
    }
}
