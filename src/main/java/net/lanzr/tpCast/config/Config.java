package net.lanzr.tpCast.config;

import net.lanzr.tpCast.tpCast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

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
    private static ForgeConfigSpec.DoubleValue MAX_COST_LIMIT;
    private static ForgeConfigSpec.IntValue LEVEL_COOLDOWN_PERLEVEL;
    private static ForgeConfigSpec.DoubleValue LEVEL_PUNISH_FUSE_BLOW;

    private static ForgeConfigSpec.DoubleValue LEVEL_COST_BACK;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_HOME;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_TPA;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_SPAWN;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_MARK_BASE;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_MARK_ADD;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_ASSIST;
    private static ForgeConfigSpec.DoubleValue LEVEL_ADD_BY_ASSIST;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_OVERLOAD_TP;
    private static ForgeConfigSpec.DoubleValue INDEX_COST_DISTANT;



    public static int maxLevel;
    public static int levelCoolDownPerLevel;
    public static double levelPunishFuseBlow;
    public static double maxCostLimit;

    public static double levelCostBack;
    public static double levelCostHome;
    public static double levelCostTPA;
    public static double levelCostSPAWN;
    public static double levelCostMarkBase;
    public static double levelCostMarkAdd;
    public static double levelCostAssist;
    public static double levelAddByAssist;
    public static double levelCostOverloadTP;
    public static double indexCostDistant;



    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    private static void setup(ForgeConfigSpec.Builder builder) {
        builder.push("TPCAST COMMON SETUP");
        MAX_LEVEL = builder
                .comment("the max overload level")
                .defineInRange("max level", 3, 0, Integer.MAX_VALUE);
        LEVEL_COOLDOWN_PERLEVEL = builder
                .comment("cooldown per level # unit: second")
                .defineInRange("cooldown per level", 180, 0, Integer.MAX_VALUE);
        LEVEL_PUNISH_FUSE_BLOW = builder
                .comment("Punishment level after fuse blow")
                .defineInRange("punishment", 2.f, 0.f, 100.f);
        MAX_COST_LIMIT = builder
                .comment("max cost limit")
                .defineInRange("max cost limit", 20.f, 0.f, 10000.f);
        builder.pop();
        builder.push("COST SETUP");
        LEVEL_COST_BACK = builder
                .comment("back cost level")
                .defineInRange("back cost", 0.5f, 0.f, 100.f);
        LEVEL_COST_HOME = builder
                .comment("home cost level")
                .defineInRange("home cost", 0.25f, 0.f, 100.f);
        LEVEL_COST_TPA = builder
                .comment("tpa cost level")
                .defineInRange("tpa cost", 1.5f, 0.f, 100.f);
        LEVEL_COST_SPAWN = builder
                .comment("spawn cost level")
                .defineInRange("spawn cost", 1.f, 0.f, 100.f);
        LEVEL_COST_MARK_BASE = builder
                .comment("mark base cost level")
                .defineInRange("mark base cost", 0.8f, 0.f, 100.f);
        LEVEL_COST_MARK_ADD = builder
                .comment("mark added cost level per mark")
                .defineInRange("mark add cost", 0.2f, 0.f, 100.f);

        LEVEL_COST_ASSIST = builder
                .comment("assist cost level by user")
                .defineInRange("assist cost", 2.f, 0.f, 100.f);
        LEVEL_ADD_BY_ASSIST = builder
                .comment("assist add level to target")
                .defineInRange("assist add", 1.4f, 0.f, 100.f);

        LEVEL_COST_OVERLOAD_TP = builder
                .comment("overload tp cost level")
                .defineInRange("overload tp cost", 6.f, 0.f, 100.f);
        INDEX_COST_DISTANT = builder
                .comment("distant tp cost index")
                .defineInRange("distant tp index", 1/200000000.f, 0.f, 100.f);
        builder.pop();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxLevel = MAX_LEVEL.get();
        levelCoolDownPerLevel = LEVEL_COOLDOWN_PERLEVEL.get();
        levelPunishFuseBlow = LEVEL_PUNISH_FUSE_BLOW.get();
        maxCostLimit = MAX_COST_LIMIT.get();

        levelCostBack = LEVEL_COST_BACK.get();
        levelCostHome = LEVEL_COST_HOME.get();
        levelCostTPA = LEVEL_COST_TPA.get();
        levelCostSPAWN = LEVEL_COST_SPAWN.get();
        levelCostMarkBase = LEVEL_COST_MARK_BASE.get();
        levelCostMarkAdd = LEVEL_COST_MARK_ADD.get();

        levelCostAssist = LEVEL_COST_ASSIST.get();
        levelAddByAssist = LEVEL_ADD_BY_ASSIST.get();

        levelCostOverloadTP = LEVEL_COST_OVERLOAD_TP.get();
        indexCostDistant = INDEX_COST_DISTANT.get();

    }
}
