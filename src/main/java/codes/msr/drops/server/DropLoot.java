package codes.msr.drops.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Objects;

public class DropLoot {

    private ResourceLocation id;
    private int rarity, minStackSize, maxStackSize;
    private NBTTagCompound nbt;

    public DropLoot(String id) {
        this(id, 1, 1, 1);
    }

    public DropLoot(String id, int rarity) {
        this(id, rarity, 1, 1);
    }

    public DropLoot(String id, int rarity, int minStackSize, int maxStackSize) {
        this(id, rarity, minStackSize, maxStackSize, "");
    }

    public DropLoot(String id, int rarity, int minStackSize, int maxStackSize, String nbt) {
        String[] split = id.split(":");
        if (split.length != 2) {
            throw new Error("Malformed drop");
        }

        this.id = new ResourceLocation(split[0], split[1]);
        this.rarity = rarity;
        this.minStackSize = minStackSize;
        this.maxStackSize = maxStackSize;

        try {
            this.nbt = JsonToNBT.getTagFromJson(nbt);
        } catch (Exception ignored) {
            this.nbt = null;
        }
    }

    public ItemStack getItemStack() {
        ItemStack ret = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id)));
        ret.setTagCompound(getNbt());

        return ret;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getRarity() {
        return rarity;
    }

    public int getMinStackSize() {
        return minStackSize;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }
}
