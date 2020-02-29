package codes.msr.drops.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
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
                    dayCounter++;

                    if (dayCounter >= 7) { // TODO: config dayCounter
                        WorldBorder bdr = world.getWorldBorder();

                        int posX = (int) bdr.getCenterX() + world.rand.nextInt((int) bdr.getDiameter() / 2);
                        int coin = world.rand.nextInt(2);
                        if (coin == 0) {
                            posX *= -1;
                        }

                        int posZ = (int) bdr.getCenterZ() + world.rand.nextInt((int) bdr.getDiameter() / 2);
                        coin = world.rand.nextInt(2);
                        if (coin == 0) {
                            posZ *= -1;
                        }

                        BlockPos pos = new BlockPos(posX, world.getActualHeight() - 1, posZ);

                        while (world.isAirBlock(pos.add(0, -1, 0))) {
                            pos = pos.add(0, -1, 0);
                        }

                        System.out.println(pos);

                        world.setBlockState(pos, Blocks.BEDROCK.getDefaultState());

                        for (Object object : world.playerEntities) {
                            EntityPlayer player = (EntityPlayer) object;
                            player.sendStatusMessage(new TextComponentTranslation("text.drops.spawning")
                                    .appendText(" (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                                    .setStyle(new Style().setColor(TextFormatting.GOLD)), false);
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
