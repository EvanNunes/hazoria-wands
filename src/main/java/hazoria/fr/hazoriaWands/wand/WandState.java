package hazoria.fr.hazoriaWands.wand;

import java.util.HashMap;
import java.util.Map;

public class WandState {
    private int mana;
    private final Map<String, Long> cooldownUntilMs = new HashMap<>();

    public WandState(int mana) {
        this.mana = mana;
    }

    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = mana; }

    public boolean isOnCooldown(String spellId) {
        long now = System.currentTimeMillis();
        return cooldownUntilMs.getOrDefault(spellId, 0L) > now;
    }

    public void setCooldown(String spellId, long durationMs) {
        cooldownUntilMs.put(spellId, System.currentTimeMillis() + durationMs);
    }

    public long getRemainingCooldownMs(String spellId) {
        return Math.max(0L, cooldownUntilMs.getOrDefault(spellId, 0L) - System.currentTimeMillis());
    }
}