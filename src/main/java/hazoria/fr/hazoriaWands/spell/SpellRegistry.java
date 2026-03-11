package hazoria.fr.hazoriaWands.spell;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpellRegistry {

    private final Map<String, Spell> spells = new HashMap<>();

    public void register(Spell spell) {
        spells.put(spell.id().toLowerCase(), spell);
    }

    public Spell get(String id) {
        if (id == null) return null;
        return spells.get(id.toLowerCase());
    }

    public Set<String> getIds() {
        return Collections.unmodifiableSet(spells.keySet());
    }

    public void clear() {
        spells.clear();
    }
}
