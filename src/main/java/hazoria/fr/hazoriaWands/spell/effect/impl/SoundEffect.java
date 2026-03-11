package hazoria.fr.hazoriaWands.spell.effect.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import hazoria.fr.hazoriaWands.spell.effect.SpellEffect;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundEffect implements SpellEffect {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundEffect(ConfigurationSection section) {
        String rawName = section.getString("sound", "block.note_block.pling")
                .toUpperCase()
                .replace(".", "_");
        Sound resolved;
        try {
            resolved = Sound.valueOf(rawName);
        } catch (IllegalArgumentException e) {
            resolved = Sound.BLOCK_NOTE_BLOCK_PLING;
        }
        this.sound  = resolved;
        this.volume = (float) section.getDouble("sound_volume", 1.0);
        this.pitch  = (float) section.getDouble("sound_pitch", 1.0);
    }

    @Override
    public void play(SpellContext ctx) {
        ctx.caster.getWorld().playSound(ctx.caster.getLocation(), sound, volume, pitch);
    }
}
