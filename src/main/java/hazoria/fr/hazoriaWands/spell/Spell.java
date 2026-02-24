package hazoria.fr.hazoriaWands.spell;

import org.bukkit.entity.Player;

public interface Spell {
    String id();
    String displayName();
    int manaCost();
    long cooldownMs();
    void cast(Player caster);
}