package codes.msr.drops.server;

import codes.msr.drops.common.config.ConfigHandler;
import codes.msr.drops.common.util.WeightedCollection;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DropLootHandler {

    public static WeightedCollection<Item> itemMap;

    public static void load() {
        itemMap = new WeightedCollection<>();
        HashMap<String, Integer> config = ConfigHandler.dropContents;

        for (String str : config.keySet()) {
            String[] split = str.split(":");
            String modid = split[0];
            String itemid = split[1];

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid, itemid));
            itemMap.add(config.get(str), item);
        }
    }

    public static Item getNextItem() {
        return itemMap.next(new Random());
    }

    public static ArrayList<Item> getNextItems(int amount) {
        ArrayList<Item> ret = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ret.add(getNextItem());
        }
        return ret;
    }

}
