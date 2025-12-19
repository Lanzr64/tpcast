package net.lanzr.tpCast;

import com.mojang.logging.LogUtils;
import net.lanzr.tpCast.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(tpCast.MODID)
public class tpCast
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tpcast";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public tpCast()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () ->
                new IExtensionPoint.DisplayTest(() ->
                        NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC,"tpcast-server.toml");
    }

//
//    // You can use SubscribeEvent and let the Event Bus discover methods to call
//    @SubscribeEvent
//    public void onServerStarting(ServerStartingEvent event)
//    {
//    }

}
