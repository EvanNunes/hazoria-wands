package hazoria.fr.hazoriaWands;

import hazoria.fr.hazoriaWands.command.WandsCommand;
import hazoria.fr.hazoriaWands.listener.WandListener;
import hazoria.fr.hazoriaWands.spell.SpellRegistry;
import hazoria.fr.hazoriaWands.spell.impl.ProtegoSpell;
import hazoria.fr.hazoriaWands.spell.impl.StupefixSpell;
import hazoria.fr.hazoriaWands.ui.ActionBarService;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import hazoria.fr.hazoriaWands.wand.WandStateService;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HazoriaWands extends JavaPlugin {

    private WandItemService wandItemService;
    private WandStateService wandStateService;
    private SpellRegistry spellRegistry;
    private ActionBarService actionBarService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveMessagesIfMissing();

        this.wandItemService = new WandItemService(this);
        this.wandStateService = new WandStateService(this, wandItemService);

        this.spellRegistry = new SpellRegistry();
        spellRegistry.register(new StupefixSpell());
        spellRegistry.register(new ProtegoSpell(this));

        getServer().getPluginManager().registerEvents(
                new WandListener(this, wandItemService, wandStateService, spellRegistry), this
        );

        if (getCommand("wands") != null) {
            WandsCommand wandsCommand = new WandsCommand(this, wandItemService);
            getCommand("wands").setExecutor(wandsCommand);
            getCommand("wands").setTabCompleter(wandsCommand);
        }

        wandStateService.startManaRegenTask();

        this.actionBarService = new ActionBarService(this, wandItemService, wandStateService);
        actionBarService.start();

        getLogger().info("HazoriaWands enabled (Spigot).");
    }

    @Override
    public void onDisable() {
        if (actionBarService != null) actionBarService.shutdown();
        if (wandStateService != null) wandStateService.shutdown();
    }

    private void saveMessagesIfMissing() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) saveResource("messages.yml", false);
    }
}