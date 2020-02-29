package codes.msr.drops.server;

import codes.msr.drops.common.config.ConfigHandler;
import codes.msr.drops.common.util.WeightedCollection;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class DropLootHandler {

    public static WeightedCollection<DropLoot> itemMap;

    public static void load() {
        itemMap = new WeightedCollection<>();
        ArrayList<DropLoot> config = ConfigHandler.dropContents;

        for (DropLoot loot : config) {
            itemMap.add(loot.getRarity(), loot);
        }
    }

    public static ItemStack getNextItem() {
        Random random = new Random();
        DropLoot loot = itemMap.next(random);

        int stackSize;
        if (loot.getMinStackSize() == loot.getMaxStackSize()) {
            stackSize = loot.getMinStackSize();
        } else {
            stackSize = loot.getMinStackSize() + random.nextInt(loot.getMaxStackSize() - loot.getMinStackSize());
        }

        ItemStack stack = loot.getItemStack();
        stack.setCount(stackSize);

        return stack;
    }

    public static ArrayList<ItemStack> getNextItems(int amount) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ret.add(getNextItem());
        }
        return ret;
    }

}
