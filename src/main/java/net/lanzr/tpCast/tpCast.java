package net.lanzr.tpCast;

import com.mojang.logging.LogUtils;
import net.lanzr.tpCast.config.Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(tpCast.MODID)
public class tpCast
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tpcast";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public tpCast(IEventBus modEventBus, ModContainer modContainer)
    {
//        modContainer.registerExtensionPoint(IExtensionPoint.class, () ->
//                new IExtensionPoint(() ->
//                        .IGNORESERVERONLY, (a, b) -> true));
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC,"tpcast-server.toml");
    }

}
