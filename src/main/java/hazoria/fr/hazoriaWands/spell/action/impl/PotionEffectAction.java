package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectAction implements SpellAction {

    private final List<PotionEffect> effects = new ArrayList<>();
    private final boolean targetSelf;

    @SuppressWarnings("deprecation")
    public PotionEffectAction(ConfigurationSection section) {
        this.targetSelf = "self".equalsIgnoreCase(section.getString("target", "target"));
        int durationTicks = (int) (section.getLong("duration", 3000) / 50);

        ConfigurationSection effectsSection = section.getConfigurationSection("add_effects");
        if (effectsSection != null) {
            for (String effectName : effectsSection.getKeys(false)) {
                PotionEffectType type = PotionEffectType.getByName(effectName.toUpperCase());
                if (type == null) continue;
                int amplifier = effectsSection.getInt(effectName, 0);
                effects.add(new PotionEffect(type, durationTicks, amplifier));
            }
        }
    }

    @Override
    public void execute(SpellContext ctx) {
        LivingEntity recipient = targetSelf ? ctx.caster : ctx.target;
        if (recipient == null) return;
        for (PotionEffect effect : effects) {
            recipient.addPotionEffect(effect);
        }
    }
}
