package hazoria.fr.hazoriaWands.ui;

import hazoria.fr.hazoriaWands.util.Colors;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import hazoria.fr.hazoriaWands.wand.WandStateService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarService {

    private final JavaPlugin plugin;
    private final WandItemService wandItemService;
    private final WandStateService wandStateService;
    private BukkitTask task;

    public ActionBarService(JavaPlugin plugin, WandItemService wandItemService, WandStateService wandStateService) {
        this.plugin = plugin;
        this.wandItemService = wandItemService;
        this.wandStateService = wandStateService;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("actionbar.enabled", true)) return;
        long period = plugin.getConfig().getLong("actionbar.update_ticks", 5L);

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ItemStack wand = p.getInventory().getItemInMainHand();
                if (!wandItemService.isWand(wand)) continue;
                if (!wandItemService.isOwner(p, wand)) continue;

                String spellId = wandItemService.getSelectedSpellId(wand);
                int mana = wandStateService.getMana(p.getUniqueId());

                String cdPart = "";
                if (spellId != null) {
                    long remainingMs = wandStateService.getRemainingCooldownMs(p.getUniqueId(), spellId);
                    if (remainingMs > 0) {
                        String seconds = String.format("%.1f", remainingMs / 1000.0);
                        cdPart = Colors.color(" &7| ⏳ CD: &c" + seconds + "s");
                    }
                }

                String msg = Colors.color("&bMana: &f" + mana + "&7 | &e" + (spellId == null ? "Aucun" : spellId)) + cdPart;
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        }, period, period);
    }

    public void shutdown() {
        if (task != null) task.cancel();
    }
}