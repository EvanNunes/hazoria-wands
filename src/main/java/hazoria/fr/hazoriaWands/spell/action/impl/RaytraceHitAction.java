package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import hazoria.fr.hazoriaWands.util.Colors;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
            if (target instanceof Player targetPlayer && isShieldActive(targetPlayer)) {
                ctx.reflected = true;
                ctx.target = ctx.caster;
                playReflectEffect(targetPlayer);
                targetPlayer.sendMessage(Colors.color("&aVotre Protego a réfléchi le sort !"));
                ctx.caster.sendMessage(Colors.color("&cVotre sort a été réfléchi par Protego !"));
            } else {
                ctx.target = target;
            }
        }
    }

    private boolean isShieldActive(Player player) {
        if (!player.hasMetadata(ShieldAction.META_SHIELD_UNTIL)) return false;
        long until = player.getMetadata(ShieldAction.META_SHIELD_UNTIL).get(0).asLong();
        return System.currentTimeMillis() < until;
    }

    private void playReflectEffect(Player at) {
        var loc = at.getLocation().add(0, 1, 0);
        at.getWorld().spawnParticle(Particle.END_ROD, loc, 20, 0.3, 0.5, 0.3, 0.1);
        at.getWorld().playSound(loc, Sound.ITEM_SHIELD_BLOCK, 1.5f, 1.8f);
    }
}
