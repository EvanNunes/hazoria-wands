package hazoria.fr.hazoriaWands.wand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WandStateService {

    private final JavaPlugin plugin;
    private final WandItemService wandItemService;
    private final Map<UUID, WandState> states = new ConcurrentHashMap<>();
    private BukkitTask regenTask;

    public WandStateService(JavaPlugin plugin, WandItemService wandItemService) {
        this.plugin = plugin;
        this.wandItemService = wandItemService;
    }

    public WandState getOrCreate(UUID uuid) {
        int max = plugin.getConfig().getInt("mana.max", 100);
        return states.computeIfAbsent(uuid, u -> new WandState(max));
    }

    public int getMana(UUID uuid) {
        return getOrCreate(uuid).getMana();
    }

    public boolean tryConsumeMana(UUID uuid, int cost) {
        int max = plugin.getConfig().getInt("mana.max", 100);
        WandState st = getOrCreate(uuid);
        int mana = st.getMana();
        if (mana < cost) return false;
        st.setMana(Math.max(0, Math.min(max, mana - cost)));
        return true;
    }

    public void startManaRegenTask() {
        int regen = plugin.getConfig().getInt("mana.regen_per_second", 5);
        int max = plugin.getConfig().getInt("mana.max", 100);

        regenTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ItemStack inHand = p.getInventory().getItemInMainHand();
                if (!wandItemService.isWand(inHand)) continue;
                if (!wandItemService.isOwner(p, inHand)) continue;

                WandState st = getOrCreate(p.getUniqueId());
                st.setMana(Math.min(max, st.getMana() + regen));
            }
        }, 20L, 20L);
    }

    public void shutdown() {
        if (regenTask != null) regenTask.cancel();
        states.clear();
    }
}