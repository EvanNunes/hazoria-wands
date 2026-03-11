package hazoria.fr.hazoriaWands.spell.effect;

import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import hazoria.fr.hazoriaWands.spell.effect.impl.ParticleEffect;
import hazoria.fr.hazoriaWands.spell.effect.impl.SoundEffect;
import hazoria.fr.hazoriaWands.spell.effect.impl.TrailEffect;
import org.bukkit.configuration.ConfigurationSection;

public interface SpellEffect {

    void play(SpellContext ctx);

    static SpellEffect fromConfig(ConfigurationSection section) {
        if (section.contains("trail")) {
            return new TrailEffect(section);
        } else if (section.contains("particle")) {
            return new ParticleEffect(section);
        } else if (section.contains("sound")) {
            return new SoundEffect(section);
        }
        throw new IllegalArgumentException("Cannot determine effect type from config section");
    }
}
