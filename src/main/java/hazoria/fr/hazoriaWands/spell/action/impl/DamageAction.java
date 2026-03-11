package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import org.bukkit.configuration.ConfigurationSection;

public class DamageAction implements SpellAction {

    private final double amount;

    public DamageAction(ConfigurationSection section) {
        this.amount = section.getDouble("amount", 1.0);
    }

    @Override
    public void execute(SpellContext ctx) {
        if (ctx.target == null) return;
        ctx.target.damage(amount, ctx.caster);
    }
}
