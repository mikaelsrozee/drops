package codes.msr.drops.common.config;

import codes.msr.drops.server.DropLoot;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;

public final class ConfigHandler {

    public static Configuration config;
    private static String[] defaultDrop = new String[] {
            // id [rarity] [minStack] [maxStack]
            // [optional]
            "minecraft:apple 100",
            "minecraft:stone 200 16 32",
            "minecraft:saddle 12"
    };

    // <id, rarity>
    public static ArrayList<DropLoot> dropContents = new ArrayList<>();

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        config.load();
        load();
    }

    public static void load() {
        String desc = "Possible contents of a drop.\n" +
                      "Each line should be in the format 'item' 'rarity' 'minStackSize' 'maxStackSize' where:\n" +
                      "item is the item id (e.g. minecraft:saddle),\n" +
                      "rarity is an integer > 0 (bigger = more common),\n" +
                      "minStackSize is an integer > 0 and < 65 and\n" +
                      "maxStackSize is an integer > 0 and < 65.\n" +
                      "'item' is the only required parameter.\n" +
                      "If 'minStackSize' is present so must 'maxStackSize'.\n";
        String[] load = config.getStringList("dropContents", "drops", defaultDrop, desc);

        for (String string : load) {
            String[] split = string.split(" ");

            if (split.length == 1) { // [id]
                dropContents.add(new DropLoot(split[0]));
            } else if (split.length == 2) { // [id] [rarity]
                dropContents.add(new DropLoot(split[0], Integer.parseInt(split[1])));
            } else if (split.length == 4) { // [id] [rarity] [minStackSize] [maxStackSize]
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

                dropContents.add(new DropLoot(split[0], Integer.parseInt(split[1]), min, max));
            }

            config.save();
        }
    }
}
