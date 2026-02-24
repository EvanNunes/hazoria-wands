package hazoria.fr.hazoriaWands.spell.impl;

import hazoria.fr.hazoriaWands.spell.Spell;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class StupefixSpell implements Spell {

    @Override public String id() { return "stupefix"; }
    @Override public String displayName() { return "Stupefix"; }
    @Override public int manaCost() { return 15; }
    @Override public long cooldownMs() { return 1500; }

    @Override
    public void cast(Player caster) {
        var world = caster.getWorld();
        var start = caster.getEyeLocation();
        var dir = start.getDirection();

        // Trail simple
        for (double i = 0.0; i <= 12.0; i += 0.6) {
            var point = start.clone().add(dir.clone().multiply(i));
            world.spawnParticle(Particle.SMOKE, point, 1, 0, 0, 0, 0);
        }

        RayTraceResult hit = world.rayTraceEntities(
                start, dir, 12.0,
                e -> (e instanceof LivingEntity le) && !le.getUniqueId().equals(caster.getUniqueId())
        );

        if (hit == null) return;
        if (!(hit.getHitEntity() instanceof LivingEntity target)) return;

        target.damage(4.0, caster);
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
    }
}