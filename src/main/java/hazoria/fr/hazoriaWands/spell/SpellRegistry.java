package hazoria.fr.hazoriaWands.spell;

import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    private final Map<String, Spell> spells = new HashMap<>();

    public void register(Spell spell) {
        spells.put(spell.id().toLowerCase(), spell);
    }

    public Spell get(String id) {
        if (id == null) return null;
        return spells.get(id.toLowerCase());
    }
}