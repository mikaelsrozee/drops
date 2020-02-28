package codes.msr.drops.server;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DropEventHandler {

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load event) {
        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
            DropHandler.INSTANCE = (DropHandler) event.getWorld().getMapStorage().getOrLoadData(DropHandler.class, "Drops");

            if (DropHandler.INSTANCE == null) {
                DropHandler.INSTANCE = new DropHandler();
                DropHandler.INSTANCE.markDirty();
            }

            event.getWorld().getMapStorage().setData("Drops", DropHandler.INSTANCE);
        }
    }

    @SubscribeEvent
    public void endWorldTick(TickEvent.WorldTickEvent event) {
        if (DropHandler.INSTANCE != null) {
            DropHandler.INSTANCE.endWorldTick(event);
        }
    }

}