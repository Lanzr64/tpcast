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

@Mod.EventBusSubscriber(modid = tpCast.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
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
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_REPAIR;
    private static ForgeConfigSpec.DoubleValue LEVEL_ADD_BY_ASSIST;
    private static ForgeConfigSpec.DoubleValue LEVEL_COST_OVERLOAD_TP;
    private static ForgeConfigSpec.DoubleValue INDEX_COST_DISTANT;

    private static ForgeConfigSpec.BooleanValue ENABLE_HOME;
    private static ForgeConfigSpec.BooleanValue ENABLE_MARK;
    private static ForgeConfigSpec.BooleanValue ENABLE_TPA;
    private static ForgeConfigSpec.BooleanValue ENABLE_TOY;
    private static ForgeConfigSpec.BooleanValue ENABLE_SPAWN;
    private static ForgeConfigSpec.BooleanValue ENABLE_BACK;
    private static ForgeConfigSpec.BooleanValue ENABLE_ASSIST;
    private static ForgeConfigSpec.BooleanValue ENABLE_COMMON;
    private static ForgeConfigSpec.BooleanValue ENABLE_OPS;

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
    public static double levelCostRepair;
    public static double levelAddByAssist;
    public static double levelCostOverloadTP;
    public static double indexCostDistant;

    public static boolean enableHome;
    public static boolean enableMark;
    public static boolean enableTPA;
    public static boolean enableToy;
    public static boolean enableSpawn;
    public static boolean enableBack;
    public static boolean enableAssist;
    public static boolean enableCommon;
    public static boolean enableOps;


    private static void setup(ForgeConfigSpec.Builder builder) {
        builder.push("TPCAST COMMON SETUP");
        MAX_LEVEL = builder
                .comment("the max overload level")
                .defineInRange("max level", 5, 0, Integer.MAX_VALUE);
        LEVEL_COOLDOWN_PERLEVEL = builder
                .comment("cooldown per level # unit: second")
                .defineInRange("cooldown per level", 240, 0, Integer.MAX_VALUE);
        LEVEL_PUNISH_FUSE_BLOW = builder
                .comment("Punishment level after fuse blow")
                .defineInRange("punishment", 2.f, 0.f, 10000.f);
        MAX_COST_LIMIT = builder
                .comment("max cost limit")
                .defineInRange("max cost limit", 100.f, 0.f, 10000.f);
        builder.pop();
        builder.push("COST SETUP");
        LEVEL_COST_BACK = builder
                .comment("back cost level")
                .defineInRange("back cost", 0.5f, 0.f, 10000.f);
        LEVEL_COST_HOME = builder
                .comment("home cost level")
                .defineInRange("home cost", 0.25f, 0.f, 10000.f);
        LEVEL_COST_TPA = builder
                .comment("tpa cost level")
                .defineInRange("tpa cost", 1.5f, 0.f, 10000.f);
        LEVEL_COST_SPAWN = builder
                .comment("spawn cost level")
                .defineInRange("spawn cost", 1.f, 0.f, 10000.f);
        LEVEL_COST_MARK_BASE = builder
                .comment("mark base cost level")
                .defineInRange("mark base cost", 0.8f, 0.f, 10000.f);
        LEVEL_COST_MARK_ADD = builder
                .comment("mark added cost level per mark")
                .defineInRange("mark add cost", 0.2f, 0.f, 10000.f);

        LEVEL_COST_ASSIST = builder
                .comment("assist cost level by user")
                .defineInRange("assist cost", 2.f, 0.f, 10000.f);
        LEVEL_ADD_BY_ASSIST = builder
                .comment("assist add level to target")
                .defineInRange("assist add", 1.4f, 0.f, 10000.f);

        LEVEL_COST_REPAIR = builder
                .comment("repair cost level")
                .defineInRange("repair cost", 30f, 0.f, 10000.f);

        LEVEL_COST_OVERLOAD_TP = builder
                .comment("overload tp cost level")
                .defineInRange("overload tp cost", 0.5f, 0.f, 10000.f);
        INDEX_COST_DISTANT = builder
                .comment("distant tp cost index")
                .defineInRange("distant tp index", 1f, 0.f, 10000.f);

        builder.pop();
        builder.push("COMMAND TOGGLE");
        ENABLE_HOME = builder
                .comment("enable /home command")
                .define("enable home", true);
        ENABLE_MARK = builder
                .comment("enable /mark command")
                .define("enable mark", true);
        ENABLE_TPA = builder
                .comment("enable /tpa and /tpahere commands")
                .define("enable tpa", true);
        ENABLE_TOY = builder
                .comment("enable /cast-off /tyjtyj /repair /trashcan commands")
                .define("enable toy", true);
        ENABLE_SPAWN = builder
                .comment("enable /spawn command")
                .define("enable spawn", true);
        ENABLE_BACK = builder
                .comment("enable /back command")
                .define("enable back", true);
        ENABLE_ASSIST = builder
                .comment("enable /cast-assist command")
                .define("enable assist", true);
        ENABLE_COMMON = builder
                .comment("enable /self-check /resetCoolDown /c-tp /suicide commands")
                .define("enable common", true);
        ENABLE_OPS = builder
                .comment("enable /overload-tp  commands")
                .define("enable ops", false);
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

        levelCostRepair = LEVEL_COST_REPAIR.get();

        levelCostOverloadTP = LEVEL_COST_OVERLOAD_TP.get();
        indexCostDistant = INDEX_COST_DISTANT.get();

        enableHome = ENABLE_HOME.get();
        enableMark = ENABLE_MARK.get();
        enableTPA = ENABLE_TPA.get();
        enableToy = ENABLE_TOY.get();
        enableSpawn = ENABLE_SPAWN.get();
        enableBack = ENABLE_BACK.get();
        enableAssist = ENABLE_ASSIST.get();
        enableCommon = ENABLE_COMMON.get();
        enableOps = ENABLE_OPS.get();

    }
}
