package net.lanzr.tpCast.config;

import net.lanzr.tpCast.tpCast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import net.lanzr.tpCast.api.ConfigApi;
import net.lanzr.tpCast.api.ConfigApi.ConfigEntry;

@EventBusSubscriber(modid = tpCast.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        for (ConfigEntry<?, ?> entry : ConfigApi.ENTRIES) {
            entry.load();
        }
    }
    // ============ TPCAST COMMON SETUP ============
    static { BUILDER.push("TPCAST COMMON SETUP"); }

    public static final ConfigEntry<ModConfigSpec.IntValue, Integer> MAX_LEVEL = new ConfigEntry<>(BUILDER
            .comment("the max overload level")
            .defineInRange("max level", 5, 0, Integer.MAX_VALUE));
    public static final ConfigEntry<ModConfigSpec.IntValue, Integer> LEVEL_COOLDOWN_PERLEVEL = new ConfigEntry<>(BUILDER
            .comment("cooldown per level # unit: second")
            .defineInRange("cooldown per level", 240, 0, Integer.MAX_VALUE));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_PUNISH_FUSE_BLOW = new ConfigEntry<>(BUILDER
            .comment("Punishment level after fuse blow")
            .defineInRange("punish ment", 2.f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> MAX_COST_LIMIT = new ConfigEntry<>(BUILDER
            .comment("max cost limit")
            .defineInRange("max cost limit", 100.f, 0.f, 10000.f));

    static { BUILDER.pop(); }

    // ============ COST SETUP ============
    static { BUILDER.push("COST SETUP"); }

    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_BACK = new ConfigEntry<>(BUILDER
            .comment("back cost level")
            .defineInRange("back cost", 0.5f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_HOME = new ConfigEntry<>(BUILDER
            .comment("home cost level")
            .defineInRange("home cost", 0.25f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_TPA = new ConfigEntry<>(BUILDER
            .comment("tpa cost level")
            .defineInRange("tpa cost", 1.5f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_SPAWN = new ConfigEntry<>(BUILDER
            .comment("spawn cost level")
            .defineInRange("spawn cost", 1.f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_MARK_BASE = new ConfigEntry<>(BUILDER
            .comment("mark base cost level")
            .defineInRange("mark base cost", 0.8f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_MARK_ADD = new ConfigEntry<>(BUILDER
            .comment("mark added cost level per mark")
            .defineInRange("mark add cost", 0.2f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_ASSIST = new ConfigEntry<>(BUILDER
            .comment("assist cost level by user")
            .defineInRange("assist cost", 2.f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_ADD_BY_ASSIST = new ConfigEntry<>(BUILDER
            .comment("assist add level to target")
            .defineInRange("assist add", 1.4f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_REPAIR = new ConfigEntry<>(BUILDER
            .comment("repair cost level")
            .defineInRange("repair cost", 30f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_OVERLOAD_TP = new ConfigEntry<>(BUILDER
            .comment("overload tp cost level")
            .defineInRange("overload tp cost", 0.5f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> INDEX_COST_DISTANT = new ConfigEntry<>(BUILDER
            .comment("distant tp cost index")
            .defineInRange("distant tp index", 1f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_BEACON = new ConfigEntry<>(BUILDER
            .comment("beacon place cost level")
            .defineInRange("beacon cost", 0.8f, 0.f, 10000.f));
    public static final ConfigEntry<ModConfigSpec.DoubleValue, Double> LEVEL_COST_BEACON_GO = new ConfigEntry<>(BUILDER
            .comment("beacon go teleport cost level")
            .defineInRange("beacon go cost", 0.5f, 0.f, 10000.f));

    static { BUILDER.pop(); }

    // ============ COMMAND TOGGLE ============
    static { BUILDER.push("COMMAND TOGGLE"); }

    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_HOME = new ConfigEntry<>(BUILDER
            .comment("enable /home command")
            .define("enable home", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_MARK = new ConfigEntry<>(BUILDER
            .comment("enable /mark command")
            .define("enable mark", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_TPA = new ConfigEntry<>(BUILDER
            .comment("enable /tpa and /tpahere commands")
            .define("enable tpa", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_TOY = new ConfigEntry<>(BUILDER
            .comment("enable /cast-off /tyjtyj /repair /trashcan commands")
            .define("enable toy", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_SPAWN = new ConfigEntry<>(BUILDER
            .comment("enable /spawn command")
            .define("enable spawn", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_BACK = new ConfigEntry<>(BUILDER
            .comment("enable /back command")
            .define("enable back", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_BACK_SAFE = new ConfigEntry<>(BUILDER
            .comment("enable /back-safe command (safe teleport back to death point)")
            .define("enable back safe", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_ASSIST = new ConfigEntry<>(BUILDER
            .comment("enable /cast-assist command")
            .define("enable assist", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_COMMON = new ConfigEntry<>(BUILDER
            .comment("enable /self-check /resetCoolDown /c-tp /suicide commands")
            .define("enable common", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_OPS = new ConfigEntry<>(BUILDER
            .comment("enable /overload-tp  commands")
            .define("enable ops", false));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_BEACON = new ConfigEntry<>(BUILDER
            .comment("enable /beacon command (broadcast a clickable teleport link)")
            .define("enable beacon", true));
    public static final ConfigEntry<ModConfigSpec.BooleanValue, Boolean> ENABLE_BODY = new ConfigEntry<>(BUILDER
            .comment("enable /hat /body /leg /boot command (force change armor part)")
            .define("enable body", true));

    static { BUILDER.pop(); }

    public static final ModConfigSpec SPEC = BUILDER.build();

}
