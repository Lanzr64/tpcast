package net.lanzr.tpCast.config;

import net.lanzr.tpCast.tpCast;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = tpCast.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
//    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        setup(BUILDER);
        SPEC = BUILDER.build();
    }

    private static ModConfigSpec.IntValue MAX_LEVEL;

    private static ModConfigSpec.IntValue LEVEL_COOLDOWN_PERLEVEL;
    private static ModConfigSpec.DoubleValue LEVEL_COST_BACK;
    private static ModConfigSpec.DoubleValue LEVEL_COST_HOME;
    private static ModConfigSpec.DoubleValue LEVEL_COST_TPA;
    private static ModConfigSpec.DoubleValue LEVEL_COST_SPAWN;
    private static ModConfigSpec.DoubleValue LEVEL_PUNISH_FUSE_BLOW;


    public static boolean logDirtBlock;
    public static int maxLevel;
    public static int levelCoolDownPerLevel;
    public static double levelCostBack;
    public static double levelCostHome;
    public static double levelCostTPA;
    public static double levelCostSPAWN;
    public static double levelPunishFuseBlow;

    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private static void setup(ModConfigSpec.Builder builder) {
        builder.push("tpcast setup");
        MAX_LEVEL = builder
                .comment("the max overload level")
                .defineInRange("max level", 3, 0, Integer.MAX_VALUE);
        LEVEL_COOLDOWN_PERLEVEL = builder
                .comment("cooldown per level # unit: second")
                .defineInRange("cooldown per level", 180, 0, Integer.MAX_VALUE);
        LEVEL_COST_BACK = builder
                .comment("back cost level")
                .defineInRange("back cost", 0.5f, 0.f, 100.f);
        LEVEL_COST_HOME = builder
                .comment("home cost level")
                .defineInRange("home cost", 1.f, 0.f, 100.f);
        LEVEL_COST_TPA = builder
                .comment("tpa cost level")
                .defineInRange("tpa cost", 1.5f, 0.f, 100.f);
        LEVEL_COST_SPAWN = builder
                .comment("spawn cost level")
                .defineInRange("spawn cost", 1.f, 0.f, 100.f);
        LEVEL_PUNISH_FUSE_BLOW = builder
                .comment("Punishment level after fuse blow")
                .defineInRange("punishment", 2.f, 0.f, 100.f);
        builder.pop();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxLevel = MAX_LEVEL.get();

        levelCoolDownPerLevel = LEVEL_COOLDOWN_PERLEVEL.get();
        levelCostBack = LEVEL_COST_BACK.get();
        levelCostHome = LEVEL_COST_HOME.get();
        levelCostTPA = LEVEL_COST_TPA.get();
        levelCostSPAWN = LEVEL_COST_SPAWN.get();
        levelPunishFuseBlow = LEVEL_PUNISH_FUSE_BLOW.get();
    }
}
