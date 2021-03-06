package codes.msr.drops.server;

import codes.msr.drops.Dictionary;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DropEventHandler {

    public static DropHandler INSTANCE;

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load event) {
        DropLootHandler.load();

        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
            DropHandler handler = (DropHandler) event.getWorld().getMapStorage().getOrLoadData(DropHandler.class, "Drops");

            if (handler == null) {
                handler = new DropHandler();
                handler.markDirty();
            }

            INSTANCE = handler;
            event.getWorld().getMapStorage().setData(Dictionary.MOD_ID + "_data", handler);
        }
    }

    @SubscribeEvent
    public void endWorldTick(TickEvent.WorldTickEvent event) {
        if (INSTANCE != null) {
            INSTANCE.endWorldTick(event);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
            INSTANCE.onRightClickBlock(event);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
            INSTANCE.onBlockBreak(event);
        }
    }

}
