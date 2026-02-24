package hazoria.fr.hazoriaWands.command;

import hazoria.fr.hazoriaWands.util.Colors;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WandsCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final WandItemService wandItemService;
    private final YamlConfiguration messages;

    public WandsCommand(JavaPlugin plugin, WandItemService wandItemService) {
        this.plugin = plugin;
        this.wandItemService = wandItemService;
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hazoriawands.admin")) {
            sender.sendMessage(Colors.color(messages.getString("no_permission")));
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
            if (args.length < 2) {
                sender.sendMessage(Colors.color(messages.getString("usage_give")));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(Colors.color(messages.getString("player_not_found")));
                return true;
            }

            target.getInventory().addItem(wandItemService.createWand(target));
            sender.sendMessage(Colors.color(messages.getString("gave_wand").replace("%player%", target.getName())));
            return true;
        }

        sender.sendMessage(Colors.color("&7Commande: /wands give <joueur>"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (!sender.hasPermission("hazoriawands.admin")) return out;

        if (args.length == 1) {
            out.add("give");
            return out;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
            return out;
        }
        return out;
    }
}