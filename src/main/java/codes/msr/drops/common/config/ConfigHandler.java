package codes.msr.drops.common.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;

public final class ConfigHandler {

    public static Configuration config;
    private static String[] defaultDrop = new String[] {
            "minecraft:apple 100",
            "minecraft:saddle 12"
    };

    // <id, rarity>
    public static HashMap<String, Integer> dropContents = new HashMap<>();

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        config.load();
        load();
    }

    public static void load() {
        // String name, String category, String[] defaultValue, String comment, String[] validValues
        String[] load = config.getStringList("dropContents", "drops", defaultDrop, "Possible contents of a drop. Each line should be in the format 'item' 'rarity' where item is the item id (e.g. minecraft:saddle) and rarity is an integer (bigger = more common).");

        for (String string : load) {
            String[] split = string.split(" ");
            if (split.length != 2) {
                throw new Error("Malformed config");
            }

            String id = split[0];
            String[] idSplit = id.split(":");
            if (idSplit.length != 2) {
                throw new Error("Malformed config");
            }

            int rarity = Integer.parseInt(split[1]);

            dropContents.put(id, rarity);
            config.save();
        }
    }
}
