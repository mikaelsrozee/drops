package codes.msr.drops.common.config;

import codes.msr.drops.server.DropLoot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;

public final class ConfigHandler {

    public static Configuration config;
    private static String[] defaultDrop = new String[] {
            // id [rarity] [minStack] [maxStack] [nbt]
            // [optional]
            "minecraft:bone 1000 5 14",
            "minecraft:rotten_flesh 500 3 12",
            "minecraft:wooden_sword 300 1 1 {display:{Name:\"Cardboard Sword\"}}",
            "minecraft:iron_nugget 300 3 7",
            "minecraft:iron_ingot 200 1 4",
            "minecraft:gold_nugget 200 1 5",
            "minecraft:gold_ingot 150 1 2",
            "minecraft:saddle 100",
            "minecraft:diamond 100",
            "minecraft:blaze_rod 100",
            "minecraft:emerald 50",
            "minecraft:enchanted_book 15 1 1 {ench:[{id:32,lvl:5}],display:{Name:\"A Memoir of Speed\"}}",
            "minecraft:diamond_pickaxe 10 1 1 {ench:[{id:33,lvl:1}]}"
    };

    public static ArrayList<DropLoot> dropContents = new ArrayList<>();
    public static int daysPerDrop = 7;
    public static boolean shouldLightning = true;
    public static boolean shouldBeam = true;
    public static boolean shouldDespawn = true;
    public static int radius = 2000;

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        config.load();
        load();
    }

    public static void load() {
        String desc = "Possible contents of a drop.\n" +
                      "Each line should be in the format 'item' 'rarity' 'minStackSize' 'maxStackSize' 'nbtJSON' where:\n" +
                      "item is the item id (e.g. minecraft:saddle),\n" +
                      "rarity is an integer > 0 (bigger = more common),\n" +
                      "minStackSize is an integer > 0 and < 65 and\n" +
                      "maxStackSize is an integer > 0 and < 65.\n" +
                      "nbtJSON is a JSON string, in NBT format.\n" +
                      "'item' is the only required parameter.\n" +
                      "If 'minStackSize' is present so must 'maxStackSize'.\n" +
                      "If 'nbtJSON' is present, all other parameters must be present.\n";
        String[] load = config.getStringList("dropContents", "drops", defaultDrop, desc);
        parse(load);

        desc = "Number of days until a drop spawns";
        daysPerDrop = config.getInt("daysPerDrop", "drops", daysPerDrop,0, 100, desc);

        desc = "Should lightning spawn when the drop spawns?";
        shouldLightning = config.getBoolean("shouldLightning", "drops", shouldLightning, desc);

        desc = "Should a beacon spawn above the drop?";
        shouldBeam = config.getBoolean("shouldBeam", "drops", shouldBeam, desc);

        desc = "Should the drop despawn at sunrise if it has not been opened or destroyed prior?";
        shouldDespawn = config.getBoolean("shouldDespawn", "drops", shouldDespawn, desc);

        desc = "If there is no world border set, from what radius of world spawn should drops spawn?";
        radius = config.getInt("radius", "drops", radius, 10, Integer.MAX_VALUE, desc);

        config.save();
    }

    private static void parse(String[] list) {
        for (String string : list) {
            String[] split = string.split(" ");

            ResourceLocation id = new ResourceLocation(split[0]);
            Item item = ForgeRegistries.ITEMS.getValue(id);

            System.out.println(id + " >>> " + item);

            if (item == null) {
                throw new Error("Could not find matching item for '" + split[0] + "'.");
            }

            if (split.length == 1) { // [id]
                dropContents.add(new DropLoot(split[0]));
            } else if (split.length == 2) { // [id] [rarity]
                dropContents.add(new DropLoot(split[0], Integer.parseInt(split[1])));
            } else if (split.length >= 4) { // [id] [rarity] [minStackSize] [maxStackSize] [nbt]
                int min = Integer.parseInt(split[2]);
                int max = Integer.parseInt(split[3]);

                if (min < 1)
                    min = 1;

                if (min > 65)
                    min = 64;

                if (max > 65)
                    max = 64;

                if (min > max) {
                    throw new Error("Malformed config, " + split[0] + " has min value " + min + " > max value " + max);
                }

                if (split.length > 4) {
                    StringBuilder nbt = new StringBuilder();
                    for (int i = 4; i < split.length; i++) {
                        nbt.append(split[i]).append(" ");
                    }
                    nbt.setLength(nbt.length() - 1);
                    dropContents.add(new DropLoot(split[0], Integer.parseInt(split[1]), min, max, nbt.toString()));
                } else {
                    dropContents.add(new DropLoot(split[0], Integer.parseInt(split[1]), min, max));
                }
            }
        }
    }
}
