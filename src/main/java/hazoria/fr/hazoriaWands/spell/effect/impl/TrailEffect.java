package hazoria.fr.hazoriaWands.spell.effect.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import hazoria.fr.hazoriaWands.spell.effect.SpellEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class TrailEffect implements SpellEffect {

    private final Particle particle;
    private final double step;
    private final double offsetX, offsetY, offsetZ;
    private final double fallbackRange;

    public TrailEffect(ConfigurationSection section) {
        String name = section.getString("trail", "smoke").toLowerCase();
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
        this.step          = section.getDouble("trail_step", 0.4);
        this.offsetX       = section.getDouble("particle_offset_x", 0.02);
        this.offsetY       = section.getDouble("particle_offset_y", 0.02);
        this.offsetZ       = section.getDouble("particle_offset_z", 0.02);
        this.fallbackRange = section.getDouble("fallback_range", 12.0);
    }

    @Override
    public void play(SpellContext ctx) {
        Location start = ctx.caster.getEyeLocation();

        Location end;
        if (ctx.target != null) {
            end = ctx.target.getLocation().add(0, 1, 0);
        } else {
            Vector dir = start.getDirection().normalize();
            end = start.clone().add(dir.multiply(fallbackRange));
        }

        Vector path = end.toVector().subtract(start.toVector());
        double distance = path.length();
        if (distance < 0.1) return;

        int steps = Math.min((int) (distance / step), 150); // cap à 150 pts
        Vector stepVec = path.clone().normalize().multiply(step);

        Location point = start.clone();
        for (int i = 0; i < steps; i++) {
            point.add(stepVec);
            point.getWorld().spawnParticle(particle, point, 1, offsetX, offsetY, offsetZ, 0.0);
        }
    }
}
