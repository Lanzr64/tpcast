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
//    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        setup(BUILDER);
        SPEC = BUILDER.build();
    }

    private static ForgeConfigSpec.IntValue MAX_LEVEL;

    private static ForgeConfigSpec.IntValue LEVEL_COOLDOWN_PERLEVEL;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_BACK;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_HOME;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_TPA;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_SPAWN;
    private static ForgeConfigSpec.DoubleValue LEVEL_PUNISH_FUSE_BLOW;


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
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    private static void setup(ForgeConfigSpec.Builder builder) {
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
