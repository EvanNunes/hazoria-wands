package hazoria.fr.hazoriaWands.listener;

import hazoria.fr.hazoriaWands.player.PlayerDataService;
import hazoria.fr.hazoriaWands.wand.WandStateService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerSessionListener implements Listener {

    private final WandStateService wandStateService;
    private final PlayerDataService playerDataService;

    public PlayerSessionListener(WandStateService wandStateService, PlayerDataService playerDataService) {
        this.wandStateService = wandStateService;
        this.playerDataService = playerDataService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        wandStateService.getOrCreate(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        playerDataService.save(uuid, wandStateService.getMana(uuid));
    }
}
