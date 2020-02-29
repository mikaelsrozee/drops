package codes.msr.drops.server;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DropLoot {

    private ResourceLocation id;
    private int rarity, minStackSize, maxStackSize;

    public DropLoot(String id) {
        this(id, 1, 1, 1);
    }

    public DropLoot(String id, int rarity) {
        this(id, rarity, 1, 1);
    }

    public DropLoot(String id, int rarity, int minStackSize, int maxStackSize) {
        String[] split = id.split(":");
        if (split.length != 2) {
            throw new Error("Malformed drop");
        }

        this.id = new ResourceLocation(split[0], split[1]);
        this.rarity = rarity;
        this.minStackSize = minStackSize;
        this.maxStackSize = maxStackSize;
    }

    public Item getItem() {
        return ForgeRegistries.ITEMS.getValue(id);
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
}
