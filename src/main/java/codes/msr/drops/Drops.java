package codes.msr.drops;

import codes.msr.drops.common.config.ConfigHandler;
import codes.msr.drops.server.DropCommand;
import codes.msr.drops.server.DropEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Dictionary.MOD_ID, name = Dictionary.MOD_NAME, version = Dictionary.VERSION)
public class Drops {

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DropEventHandler());
        ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DropCommand());
    }

}
