package net.lanzr.tpCast.config;

import net.lanzr.tpCast.tpCast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = tpCast.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_LEVEL = BUILDER
            .comment("the max overload level")
            .defineInRange("max level", 3, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue LEVEL_COST_BACK = BUILDER
            .comment("back cost level")
            .defineInRange("cost", 0.5f, 0.f, 100.f);
    private static final ForgeConfigSpec.DoubleValue LEVEL_COST_HOME = BUILDER.comment("home cost level")
            .defineInRange("cost", 1.f, 0.f, 100.f);
    private static final ForgeConfigSpec.DoubleValue LEVEL_COST_TPA = BUILDER
            .comment("tpa cost level")
            .defineInRange("cost", 1.5f, 0.f, 100.f);
    private static final ForgeConfigSpec.DoubleValue LEVEL_COST_SPAWN = BUILDER
            .comment("spawn cost level")
            .defineInRange("cost", 1.f, 0.f, 100.f);
    private static final ForgeConfigSpec.DoubleValue LEVEL_PUNISH_FUSE_BLOW = BUILDER
            .comment("Punishment level after fuse blow")
            .defineInRange("punishment", 2.f, 0.f, 100.f);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int maxLevel;
    public static double levelCostBack;
    public static double levelCostHome;
    public static double levelCostTPA;
    public static double levelCostSPAWN;
    public static double levelPunishFuseBlow;

    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxLevel = MAX_LEVEL.get();
        levelCostBack = LEVEL_COST_BACK.get();
        levelCostHome = LEVEL_COST_HOME.get();
        levelCostTPA = LEVEL_COST_TPA.get();
        levelCostSPAWN = LEVEL_COST_SPAWN.get();
        levelPunishFuseBlow = LEVEL_PUNISH_FUSE_BLOW.get();
    }
}
