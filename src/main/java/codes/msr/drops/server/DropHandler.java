package codes.msr.drops.server;

import net.minecraft.entity.effect.EntityLightningBolt;
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
    BlockPos dropPos;

    public DropHandler() {
        super("Drops");
    }

    public void endWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            World world = event.world;
            if (world.provider.getDimension() == 0) {
                int time = (int) (world.getWorldTime() % 24000);

                if (time == 13000) {
                    dayCounter++;
                    this.markDirty();
                }

                if (dayCounter >= 7) {  // (will trigger on the 7th night)
                    if (time == 13000) {
                        dropPos = findDropPosition(world);
                        alertDrop(world, dropPos, false);
                    } else if (time == 18000 && dropPos != null) {
                        spawnDrop(world, dropPos);
                        alertDrop(world, dropPos, true);

                        dayCounter = 0;
                        this.markDirty();
                    }
                }
            }
        }
    }

    private BlockPos findDropPosition(World world) {
        WorldBorder bdr = world.getWorldBorder();
        int radius = (int) bdr.getDiameter() / 2;

        // Choose X co-ord
        int posX = (int) bdr.getCenterX() + world.rand.nextInt(radius);
        int coin = world.rand.nextInt(2);
        if (coin == 0) {
            posX *= -1;
        }

        // Choose Z co-ord
        int posZ = (int) bdr.getCenterZ() + world.rand.nextInt(radius);
        coin = world.rand.nextInt(2);
        if (coin == 0) {
            posZ *= -1;
        }

        // Choose Y co-ord
        BlockPos pos = new BlockPos(posX, world.getActualHeight() - 1, posZ);
        while (world.isAirBlock(pos.add(0, -1, 0))) {
            pos = pos.add(0, -1, 0);
        }

        return pos;
    }

    private void spawnDrop(World world, BlockPos pos) {
        world.addWeatherEffect(new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true));

        world.setBlockState(pos, Blocks.CHEST.getDefaultState());
    }

    private void alertDrop(World world, BlockPos pos, boolean spawning) {
        String message = spawning ? "text.drops.spawningNow" : "text.drops.spawning";
        TextFormatting colour = spawning ? TextFormatting.RED : TextFormatting.GOLD;

        for (Object object : world.playerEntities) {
            EntityPlayer player = (EntityPlayer) object;
            player.sendStatusMessage(new TextComponentTranslation(message)
                    .appendText(" (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                    .setStyle(new Style().setColor(colour)), false);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.dayCounter = nbt.getInteger("dayCounter");

        int x = nbt.getInteger("dropX");
        int y = nbt.getInteger("dropY");
        int z = nbt.getInteger("dropZ");
        this.dropPos = new BlockPos(x, y, z);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("dayCounter", dayCounter);

        if (dropPos == null) {
            return nbt; // short
        }

        nbt.setInteger("dropX", dropPos.getX());
        nbt.setInteger("dropY", dropPos.getY());
        nbt.setInteger("dropZ", dropPos.getZ());
        return nbt;
    }

}
