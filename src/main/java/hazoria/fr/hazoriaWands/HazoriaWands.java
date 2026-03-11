package hazoria.fr.hazoriaWands;

import hazoria.fr.hazoriaWands.command.SortsCommand;
import hazoria.fr.hazoriaWands.command.WandsCommand;
import hazoria.fr.hazoriaWands.listener.PlayerSessionListener;
import hazoria.fr.hazoriaWands.listener.WandListener;
import hazoria.fr.hazoriaWands.listener.WandProtectionListener;
import hazoria.fr.hazoriaWands.player.PlayerDataService;
import hazoria.fr.hazoriaWands.spell.SpellLoader;
import hazoria.fr.hazoriaWands.spell.SpellRegistry;
import hazoria.fr.hazoriaWands.ui.ActionBarService;
import hazoria.fr.hazoriaWands.ui.SpellSelectionGui;
import hazoria.fr.hazoriaWands.wand.WandItemService;
import hazoria.fr.hazoriaWands.wand.WandStateService;
import hazoria.fr.hazoriaWands.wand.WandTypeRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HazoriaWands extends JavaPlugin {

    private WandItemService wandItemService;
    private WandStateService wandStateService;
    private SpellRegistry spellRegistry;
    private WandTypeRegistry wandTypeRegistry;
    private PlayerDataService playerDataService;
    private ActionBarService actionBarService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResourceIfMissing("messages.yml");
        saveResourceIfMissing("spells.yml");
        saveResourceIfMissing("wands.yml");

        this.wandTypeRegistry = new WandTypeRegistry(this);
        wandTypeRegistry.load();

        this.spellRegistry = new SpellRegistry();
        new SpellLoader(this).load().forEach(spellRegistry::register);

        this.wandItemService  = new WandItemService(this);
        this.playerDataService = new PlayerDataService(this);
        this.wandStateService = new WandStateService(this, wandItemService, playerDataService);
        wandStateService.startManaRegenTask();

        this.actionBarService = new ActionBarService(this, wandItemService, wandStateService);
        actionBarService.start();

        getServer().getPluginManager().registerEvents(
                new WandListener(this, wandItemService, wandStateService, spellRegistry), this);
        getServer().getPluginManager().registerEvents(
                new WandProtectionListener(wandItemService), this);
        getServer().getPluginManager().registerEvents(
                new PlayerSessionListener(wandStateService, playerDataService), this);

        SpellSelectionGui spellGui = new SpellSelectionGui(spellRegistry, wandItemService, playerDataService);
        getServer().getPluginManager().registerEvents(spellGui, this);

        if (getCommand("wands") != null) {
            WandsCommand cmd = new WandsCommand(this, wandItemService, wandTypeRegistry, spellRegistry, playerDataService);
            getCommand("wands").setExecutor(cmd);
            getCommand("wands").setTabCompleter(cmd);
        }
        if (getCommand("sorts") != null) {
            getCommand("sorts").setExecutor(new SortsCommand(wandItemService, spellGui));
        }

        getLogger().info("HazoriaWands enabled.");
    }

    @Override
    public void onDisable() {
        if (actionBarService != null) actionBarService.shutdown();
        if (wandStateService  != null) wandStateService.shutdown();
    }

    public void reload() {
        reloadConfig();
        wandTypeRegistry.load();
        spellRegistry.clear();
        new SpellLoader(this).load().forEach(spellRegistry::register);
        getLogger().info("HazoriaWands config reloaded.");
    }

    private void saveResourceIfMissing(String resourceName) {
        File file = new File(getDataFolder(), resourceName);
        if (!file.exists()) saveResource(resourceName, false);
    }
}
