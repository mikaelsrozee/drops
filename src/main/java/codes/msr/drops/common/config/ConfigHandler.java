package codes.msr.drops.common.config;

import codes.msr.drops.server.DropLoot;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;

public final class ConfigHandler {

    public static Configuration config;
    private static String[] defaultDrop = new String[] {
            // id [rarity] [minStack] [maxStack] [nbt]
            // [optional]
            "minecraft:apple 100",
            "minecraft:diamond_sword 200 1 1 {display:{Lore:[\"\\\"A legendary weapon\\\"\"]}}",
            "minecraft:saddle 12"
    };

    public static ArrayList<DropLoot> dropContents = new ArrayList<>();
    public static int daysPerDrop = 7;

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

        config.save();
    }

    private static void parse(String[] list) {
        for (String string : list) {
            String[] split = string.split(" ");

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
