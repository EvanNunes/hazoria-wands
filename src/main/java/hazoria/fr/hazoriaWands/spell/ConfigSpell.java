package hazoria.fr.hazoriaWands.spell;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import hazoria.fr.hazoriaWands.spell.effect.SpellEffect;
import org.bukkit.entity.Player;

import java.util.List;

public class ConfigSpell implements Spell {

    private final String id;
    private final String displayName;
    private final int manaCost;
    private final long cooldownMs;
    private final List<SpellAction> actions;
    private final List<SpellEffect> effects;

    public ConfigSpell(String id, String displayName, int manaCost, long cooldownMs,
                       List<SpellAction> actions, List<SpellEffect> effects) {
        this.id = id;
        this.displayName = displayName;
        this.manaCost = manaCost;
        this.cooldownMs = cooldownMs;
        this.actions = actions;
        this.effects = effects;
    }

    @Override public String id()          { return id; }
    @Override public String displayName() { return displayName; }
    @Override public int manaCost()       { return manaCost; }
    @Override public long cooldownMs()    { return cooldownMs; }

    @Override
    public void cast(Player caster) {
        SpellContext ctx = new SpellContext(caster);
        for (SpellAction action : actions) action.execute(ctx);
        for (SpellEffect effect : effects) effect.play(ctx);
    }
}
