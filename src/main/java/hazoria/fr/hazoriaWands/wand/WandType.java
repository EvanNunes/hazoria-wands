package hazoria.fr.hazoriaWands.wand;

import java.util.List;

public class WandType {
    public final String id;
    public final String displayName;
    public final String material;
    public final int customModelData;
    public final List<String> lore;
    public final int manaMax;
    public final int manaStart;
    public final int manaRegen;
    public final List<String> defaultSpells;
    public final String effectColor;

    public WandType(String id, String displayName, String material, int customModelData,
                    List<String> lore, int manaMax, int manaStart, int manaRegen,
                    List<String> defaultSpells, String effectColor) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.customModelData = customModelData;
        this.lore = lore;
        this.manaMax = manaMax;
        this.manaStart = manaStart;
        this.manaRegen = manaRegen;
        this.defaultSpells = defaultSpells;
        this.effectColor = effectColor;
    }
}
