package hazoria.fr.hazoriaWands.command;

import hazoria.fr.hazoriaWands.HazoriaWands;
import hazoria.fr.hazoriaWands.player.PlayerDataService;
import hazoria.fr.hazoriaWands.spell.SpellRegistry;
import hazoria.fr.hazoriaWands.util.Colors;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import hazoria.fr.hazoriaWands.wand.WandType;
import hazoria.fr.hazoriaWands.wand.WandTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WandsCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final WandItemService wandItemService;
    private final WandTypeRegistry wandTypeRegistry;
    private final SpellRegistry spellRegistry;
    private final PlayerDataService playerDataService;
    private YamlConfiguration messages;

    public WandsCommand(JavaPlugin plugin, WandItemService wandItemService,
                        WandTypeRegistry wandTypeRegistry, SpellRegistry spellRegistry,
                        PlayerDataService playerDataService) {
        this.plugin             = plugin;
        this.wandItemService    = wandItemService;
        this.wandTypeRegistry   = wandTypeRegistry;
        this.spellRegistry      = spellRegistry;
        this.playerDataService  = playerDataService;
        loadMessages();
    }

    private void loadMessages() {
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hazoriawands.admin")) {
            sender.sendMessage(Colors.color(messages.getString("no_permission")));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give"        -> handleGive(sender, args);
            case "addspell"    -> handleAddSpell(sender, args);
            case "removespell" -> handleRemoveSpell(sender, args);
            case "unlock"      -> handleUnlock(sender, args);
            case "reload"      -> handleReload(sender);
            default            -> sendHelp(sender);
        }
        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Colors.color(messages.getString("usage_give")));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Colors.color(messages.getString("player_not_found")));
            return;
        }

        WandType type;
        if (args.length >= 3) {
            type = wandTypeRegistry.get(args[2]);
            if (type == null) {
                sender.sendMessage(Colors.color("&cType de baguette inconnu: &e" + args[2]));
                return;
            }
        } else {
            type = wandTypeRegistry.getDefault();
            if (type == null) {
                sender.sendMessage(Colors.color("&cAucun type de baguette configuré dans wands.yml."));
                return;
            }
        }

        target.getInventory().addItem(wandItemService.createWand(target, type));

        // Auto-débloque les sorts par défaut de la baguette
        List<String> unlocked = playerDataService.loadUnlockedSpells(target.getUniqueId());
        for (String spellId : type.defaultSpells) {
            String id = spellId.toLowerCase();
            if (!unlocked.contains(id)) unlocked.add(id);
        }
        playerDataService.saveUnlockedSpells(target.getUniqueId(), unlocked);

        sender.sendMessage(Colors.color(messages.getString("gave_wand").replace("%player%", target.getName())));
    }

    private void handleAddSpell(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colors.color("&cUsage: /wands addspell <joueur> <sort>"));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Colors.color(messages.getString("player_not_found")));
            return;
        }

        String spellId = args[2].toLowerCase();
        if (spellRegistry.get(spellId) == null) {
            sender.sendMessage(Colors.color("&cSort inconnu: &e" + spellId));
            return;
        }

        ItemStack wand = findWand(target);
        if (wand == null) {
            sender.sendMessage(Colors.color("&c" + target.getName() + " ne possède pas de baguette."));
            return;
        }

        List<String> spells = new ArrayList<>(wandItemService.getSpells(wand));
        if (spells.contains(spellId)) {
            sender.sendMessage(Colors.color("&c" + target.getName() + " possède déjà ce sort."));
            return;
        }
        spells.add(spellId);

        var meta = wand.getItemMeta();
        wandItemService.setSpells(meta, spells);
        wand.setItemMeta(meta);

        // Auto-débloque le sort pour le joueur
        List<String> unlocked = playerDataService.loadUnlockedSpells(target.getUniqueId());
        if (!unlocked.contains(spellId)) {
            unlocked.add(spellId);
            playerDataService.saveUnlockedSpells(target.getUniqueId(), unlocked);
        }

        sender.sendMessage(Colors.color("&aSort &e" + spellId + "&a ajouté à &e" + target.getName() + "&a."));
    }

    private void handleRemoveSpell(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colors.color("&cUsage: /wands removespell <joueur> <sort>"));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Colors.color(messages.getString("player_not_found")));
            return;
        }

        String spellId = args[2].toLowerCase();
        ItemStack wand = findWand(target);
        if (wand == null) {
            sender.sendMessage(Colors.color("&c" + target.getName() + " ne possède pas de baguette."));
            return;
        }

        List<String> spells = new ArrayList<>(wandItemService.getSpells(wand));
        if (!spells.remove(spellId)) {
            sender.sendMessage(Colors.color("&c" + target.getName() + " ne possède pas ce sort."));
            return;
        }

        var meta = wand.getItemMeta();
        wandItemService.setSpells(meta, spells);
        wand.setItemMeta(meta);

        sender.sendMessage(Colors.color("&aSort &e" + spellId + "&a retiré de &e" + target.getName() + "&a."));
    }

    private void handleUnlock(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colors.color("&cUsage: /wands unlock <joueur> <sort>"));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Colors.color(messages.getString("player_not_found")));
            return;
        }
        String spellId = args[2].toLowerCase();
        if (spellRegistry.get(spellId) == null) {
            sender.sendMessage(Colors.color("&cSort inconnu: &e" + spellId));
            return;
        }
        List<String> unlocked = playerDataService.loadUnlockedSpells(target.getUniqueId());
        if (unlocked.contains(spellId)) {
            sender.sendMessage(Colors.color("&c" + target.getName() + " possède déjà ce sort débloqué."));
            return;
        }
        unlocked.add(spellId);
        playerDataService.saveUnlockedSpells(target.getUniqueId(), unlocked);
        sender.sendMessage(Colors.color("&aSort &e" + spellId + "&a débloqué pour &e" + target.getName() + "&a."));
        target.sendMessage(Colors.color("&6✦ &eNouvel sort débloqué : &f" + spellId));
    }

    private void handleReload(CommandSender sender) {
        ((HazoriaWands) plugin).reload();
        loadMessages();
        sender.sendMessage(Colors.color("&aHazoriaWands rechargé."));
    }

    private ItemStack findWand(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && wandItemService.isWand(item) && wandItemService.isOwner(player, item)) {
                return item;
            }
        }
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Colors.color("&7/wands give <joueur> [type]"));
        sender.sendMessage(Colors.color("&7/wands addspell <joueur> <sort>"));
        sender.sendMessage(Colors.color("&7/wands removespell <joueur> <sort>"));
        sender.sendMessage(Colors.color("&7/wands unlock <joueur> <sort>"));
        sender.sendMessage(Colors.color("&7/wands reload"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (!sender.hasPermission("hazoriawands.admin")) return out;

        if (args.length == 1) {
            return filterStartsWith(List.of("give", "addspell", "removespell", "unlock", "reload"), args[0]);
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "give", "addspell", "removespell", "unlock" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
                    return filterStartsWith(out, args[1]);
                }
            }
        }
        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "give" -> {
                    out.addAll(wandTypeRegistry.getIds());
                    return filterStartsWith(out, args[2]);
                }
                case "addspell", "removespell", "unlock" -> {
                    out.addAll(spellRegistry.getIds());
                    return filterStartsWith(out, args[2]);
                }
            }
        }
        return out;
    }

    private List<String> filterStartsWith(List<String> list, String prefix) {
        if (prefix.isEmpty()) return new ArrayList<>(list);
        List<String> filtered = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(prefix.toLowerCase())) filtered.add(s);
        }
        return filtered;
    }
}
