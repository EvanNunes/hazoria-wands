package hazoria.fr.hazoriaWands.spell.action.impl;

import hazoria.fr.hazoriaWands.spell.action.SpellAction;
import hazoria.fr.hazoriaWands.spell.action.SpellContext;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.RayTraceResult;

public class OpenDoorsAction implements SpellAction {

    private final double range;

    public OpenDoorsAction(ConfigurationSection section) {
        this.range = section.getDouble("range", 5.0);
    }

    @Override
    public void execute(SpellContext ctx) {
        var start  = ctx.caster.getEyeLocation();
        var dir    = start.getDirection();
        var world  = ctx.caster.getWorld();

        RayTraceResult result = world.rayTraceBlocks(start, dir, range);
        if (result == null || result.getHitBlock() == null) return;

        Block block = result.getHitBlock();
        if (!(block.getBlockData() instanceof Openable openable)) return;

        openable.setOpen(!openable.isOpen());
        block.setBlockData(openable);

        Sound sound = openable.isOpen()
                ? Sound.BLOCK_IRON_DOOR_OPEN
                : Sound.BLOCK_IRON_DOOR_CLOSE;
        world.playSound(block.getLocation(), sound, 1.0f, 1.2f);
    }
}
