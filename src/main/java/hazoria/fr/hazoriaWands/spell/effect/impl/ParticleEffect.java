package hazoria.fr.hazoriaWands.spell.effect.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import hazoria.fr.hazoriaWands.spell.effect.SpellEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleEffect implements SpellEffect {

    private final Particle particle;
    private final int count;
    private final double offsetX, offsetY, offsetZ;
    private final boolean useTarget;

    public ParticleEffect(ConfigurationSection section) {
        String name = section.getString("particle", "smoke").toLowerCase();
        this.particle = switch (name) {
            case "spell"       -> Particle.ENCHANT;
            case "smoke"       -> Particle.SMOKE;
            case "flame"       -> Particle.FLAME;
            case "crit"        -> Particle.CRIT;
            case "heart"       -> Particle.HEART;
            case "dragon"      -> Particle.DRAGON_BREATH;
            case "portal"      -> Particle.PORTAL;
            case "witch"       -> Particle.WITCH;
            case "soul_fire"   -> Particle.SOUL_FIRE_FLAME;
            case "totem"       -> Particle.TOTEM_OF_UNDYING;
            case "lava"        -> Particle.LAVA;
            case "large_smoke" -> Particle.LARGE_SMOKE;
            case "end_rod"     -> Particle.END_ROD;
            case "spark"       -> Particle.ELECTRIC_SPARK;
            default -> {
                try { yield Particle.valueOf(name.toUpperCase()); }
                catch (IllegalArgumentException e) { yield Particle.SMOKE; }
            }
        };
        this.count      = section.getInt("particle_count", 10);
        this.offsetX    = section.getDouble("particle_offset_x", 0.3);
        this.offsetY    = section.getDouble("particle_offset_y", 0.3);
        this.offsetZ    = section.getDouble("particle_offset_z", 0.3);
        this.useTarget  = "target".equalsIgnoreCase(section.getString("particle_target", "caster"));
    }

    @Override
    public void play(SpellContext ctx) {
        Location base;
        if (useTarget && ctx.target != null) {
            base = ctx.target.getLocation().add(0, 1, 0);
        } else {
            base = ctx.caster.getLocation().add(0, 1, 0);
        }
        base.getWorld().spawnParticle(particle, base, count, offsetX, offsetY, offsetZ, 0.0);
    }
}
