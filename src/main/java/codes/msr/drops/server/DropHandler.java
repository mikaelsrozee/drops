package codes.msr.drops.server;

import codes.msr.drops.common.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class DropHandler extends WorldSavedData {

    public static DropHandler INSTANCE;

    boolean forced, dropActive;
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

                if (dropActive && dropPos != null && world.getWorldTime() % 40 == 0) {
                    spawnBeam();
                }

                if (dropActive && dropPos != null && time == 0) {
                    alertDrop(world, dropPos, "despawned", TextFormatting.DARK_RED);
                    world.removeTileEntity(dropPos);
                    world.setBlockToAir(dropPos);
                    dropActive = false;
                }

                if (time == 13000) {
                    dayCounter++;
                    this.markDirty();
                }

                if (dayCounter >= ConfigHandler.daysPerDrop || forced) {
                    if (forced) {
                        dropPos = findDropPosition(world);
                        spawnDrop(world, dropPos);
                        alertDrop(world, dropPos, "spawningNow", TextFormatting.GOLD);
                        dropActive = true;
                        forced = false;
                    } else if (time == 13000) {
                        dropPos = findDropPosition(world);
                        alertDrop(world, dropPos, "spawning", TextFormatting.RED);
                    } else if (time == 18000 && dropPos != null) {
                        spawnDrop(world, dropPos);
                        alertDrop(world, dropPos, "spawningNow", TextFormatting.GOLD);

                        dayCounter = 0;
                        dropActive = true;
                        this.markDirty();
                    }
                }
            }
        }
    }

    private void spawnBeam() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;

        for (int y = dropPos.getY() + 1; y < world.getActualHeight(); y++) {
            EnumParticleTypes particle = y % 2 == 0 ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.REDSTONE;

            double x = dropPos.getX() + 0.5D + (world.rand.nextDouble() - 0.25D);
            double z = dropPos.getZ() + 0.5D + (world.rand.nextDouble() - 0.25D);

            world.spawnParticle(particle, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (dropActive && event.getPos().equals(dropPos)) {
            dropActive = false;
            alertDrop(event.getWorld(), dropPos, "captured", TextFormatting.BLUE);
        }
    }

    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (dropActive && event.getPos().equals(dropPos)) {
            dropActive = false;
            alertDrop(event.getWorld(), dropPos, "captured", TextFormatting.BLUE);
        }
    }

    private BlockPos findDropPosition(World world) {
        WorldBorder bdr = world.getWorldBorder();
        int radius = (int) bdr.getDiameter() / 2;

        int posX, posZ;
        if (radius == 0) {
            posX = (int) bdr.getCenterX();
            posZ = (int) bdr.getCenterZ();
        } else {
            // Choose X co-ord
            posX = (int) bdr.getCenterX() + world.rand.nextInt(radius);
            int coin = world.rand.nextInt(2);
            if (coin == 0) {
                posX *= -1;
            }

            // Choose Z co-ord
            posZ = (int) bdr.getCenterZ() + world.rand.nextInt(radius);
            coin = world.rand.nextInt(2);
            if (coin == 0) {
                posZ *= -1;
            }
        }

        // Choose Y co-ord
        BlockPos pos = new BlockPos(posX, world.getActualHeight() - 1, posZ);
        while (world.isAirBlock(pos.add(0, -1, 0))) {
            pos = pos.add(0, -1, 0);

            if (pos.getY() == 0) {
                pos.add(1, world.getActualHeight() - 1, 0);
            }
        }

        return pos;
    }

    private void spawnDrop(World world, BlockPos pos) {
        world.addWeatherEffect(new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true));

        world.setBlockState(pos, Blocks.CHEST.getDefaultState());

        TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
        assert chest != null;
        populateDrop(chest);
    }

    private void populateDrop(@Nonnull TileEntityChest chest) {
        int min = chest.getSizeInventory() / 5;
        int max = chest.getSizeInventory() / 3;

        int numItems = min + chest.getWorld().rand.nextInt(max);
        ArrayList<ItemStack> items = DropLootHandler.getNextItems(numItems);

        for (ItemStack itemStack : items) {
            boolean in = false;
            while (!in) {
                int i = chest.getWorld().rand.nextInt(chest.getSizeInventory() - 1);
                if (chest.getStackInSlot(i).isEmpty()) {
                    chest.setInventorySlotContents(i, itemStack);
                    in = true;
                }
            }
        }
    }

    public void force(boolean now) {
        if (now) {
            forced = true;
        } else {
            this.dayCounter = ConfigHandler.daysPerDrop - 1;
        }
    }

    private void alertDrop(World world, BlockPos pos, String alert, TextFormatting colour) {
        String message = "text.drops." + alert;

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
    @Nonnull
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
