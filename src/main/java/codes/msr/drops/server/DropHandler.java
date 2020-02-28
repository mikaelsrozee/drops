package codes.msr.drops.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DropHandler extends WorldSavedData {

    public static DropHandler INSTANCE;

    int dayCounter;

    public DropHandler() {
        super("Drops");
    }

    public void endWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            World world = event.world;
            if (world.provider.getDimension() == 0) {
                int time = (int) (world.getWorldTime() % 24000);
                if (time == 18000) {
//                    System.out.println("yeet " + dayCounter);
                    dayCounter++;

                    if (dayCounter >= 7) { // TODO: config dayCounter
                        // TODO: spawnDrop();

                        world.setBlockState(new BlockPos(0, 100, 0), Blocks.BEDROCK.getDefaultState());

                        for (Object object : world.playerEntities) {
                            EntityPlayer player = (EntityPlayer) object;
                            player.sendStatusMessage(new TextComponentTranslation("text.drops.spawning").setStyle(new Style().setColor(TextFormatting.RED)), true);
                        }

                        dayCounter = 0;
                    }

                    this.markDirty();
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.dayCounter = nbt.getInteger("dayCounter");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("dayCounter", dayCounter);
        return nbt;
    }

}
