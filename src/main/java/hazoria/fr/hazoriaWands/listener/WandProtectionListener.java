package hazoria.fr.hazoriaWands.listener;

import hazoria.fr.hazoriaWands.wand.WandItemService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class WandProtectionListener implements Listener {

    private final WandItemService wandItemService;

    public WandProtectionListener(WandItemService wandItemService) {
        this.wandItemService = wandItemService;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        if (wandItemService.isWand(item) && wandItemService.isOwner(e.getPlayer(), item)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        // Only act when an external inventory (chest, etc.) is open
        var top = e.getView().getTopInventory();
        if (top.getType() == InventoryType.CRAFTING || top.getType() == InventoryType.PLAYER) return;

        ItemStack cursor  = e.getCursor();
        ItemStack current = e.getCurrentItem();

        if (isPlayerWand(cursor, player) || isPlayerWand(current, player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if (wandItemService.isWand(e.getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        e.getDrops().removeIf(item -> wandItemService.isWand(item) && wandItemService.isOwner(player, item));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent e) {
        Entity entity = e.getEntity();
        ItemStack item = e.getItem().getItemStack();
        if (!wandItemService.isWand(item)) return;
        if (entity instanceof Player player && wandItemService.isOwner(player, item)) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemFrameInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof ItemFrame frame)) return;

        // Empêche de poser la baguette dans le frame
        ItemStack inHand = e.getPlayer().getInventory().getItem(e.getHand());
        if (isPlayerWand(inHand, e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

        // Empêche de prendre une baguette qui est déjà dans le frame
        if (wandItemService.isWand(frame.getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemFrameBreak(HangingBreakByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame frame)) return;
        if (!wandItemService.isWand(frame.getItem())) return;
        frame.setItem(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!wandItemService.isWand(e.getOldCursor())) return;
        if (!wandItemService.isOwner(player, e.getOldCursor())) return;

        int topSize = e.getView().getTopInventory().getSize();
        for (int slot : e.getRawSlots()) {
            if (slot < topSize) {
                e.setCancelled(true);
                return;
            }
        }
    }

    private boolean isPlayerWand(ItemStack item, Player player) {
        return wandItemService.isWand(item) && wandItemService.isOwner(player, item);
    }
}
