package net.lanzr.tpCast.command;

import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.tpCast;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Keeps feature switches and command registration in one place. */
@Mod.EventBusSubscriber(modid = tpCast.MODID)
public final class CommandRegistry {
    private CommandRegistry() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        registerIfEnabled(event, Config.ENABLE_CUSTP::get, CustomTeleportCommand::register);
        registerIfEnabled(event, Config.ENABLE_MARK::get, MARKCommand::register);
        registerIfEnabled(event, Config.ENABLE_TPA::get, TPACommand::register);
        registerIfEnabled(event, Config.ENABLE_TOY::get, TOYCommand::register);
        registerIfEnabled(event, Config.ENABLE_HOME::get, HOMECommand::register);
        registerIfEnabled(event, Config.ENABLE_SPAWN::get, SpawnCommand::register);
        registerIfEnabled(event, Config.ENABLE_BACK::get, BackCommand::register);
        registerIfEnabled(event, Config.ENABLE_BACK_SAFE::get, BackSafeCommand::register);
        registerIfEnabled(event, Config.ENABLE_ASSIST::get, AssistCommand::register);
        registerIfEnabled(event, Config.ENABLE_COMMON::get, CommonCommand::register);
        registerIfEnabled(event, Config.ENABLE_OPS::get, OPCommand::register);
        registerIfEnabled(event, Config.ENABLE_BEACON::get, BeaconCommand::register);
        registerIfEnabled(event, Config.ENABLE_BODY::get, BodyCommand::register);
    }

    private static void registerIfEnabled(RegisterCommandsEvent event,
                                          BooleanSupplier enabled,
                                          Consumer<RegisterCommandsEvent> registrar) {
        if (enabled.getAsBoolean()) {
            registrar.accept(event);
        }
    }
}
