package hazoria.fr.hazoriaWands.command;

import hazoria.fr.hazoriaWands.ui.SpellSelectionGui;
import hazoria.fr.hazoriaWands.util.Colors;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SortsCommand implements CommandExecutor {

    private final WandItemService wandItemService;
    private final SpellSelectionGui gui;

    public SortsCommand(WandItemService wandItemService, SpellSelectionGui gui) {
        this.wandItemService = wandItemService;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }

        ItemStack wand = player.getInventory().getItemInMainHand();
        if (!wandItemService.isWand(wand) || !wandItemService.isOwner(player, wand)) {
            player.sendMessage(Colors.color("&cTenez votre baguette en main pour gérer vos sorts."));
            return true;
        }

        gui.open(player, wand);
        return true;
    }
}
