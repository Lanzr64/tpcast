package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.*;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.tpCast;
//import net.minecraft.core.registries.Registries;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = tpCast.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvent {
//    @Mod.EventBusSubscriber(modid = tpCast.MODID)
//    public static class RegisterCommands {


        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerDeath(LivingDeathEvent event) {
            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
            if(isPlayer ) {
                ServerPlayer player  = (ServerPlayer) event.getEntity();
                tpCastTag tag = new tpCastTag(player);
                tag.setBack();
            }
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerCloned(PlayerEvent.Clone event) {
            Player old_player = event.getOriginal();
            Player new_player = event.getEntity();

            if(!old_player.getPersistentData().contains(tpCast.MODID))
                return;

            CompoundTag old_Tag = event.getOriginal().getPersistentData().getCompound(tpCast.MODID);
            new_player.getPersistentData().put(tpCast.MODID, old_Tag);
        }

        @SubscribeEvent
        public static void CommandRegistration(RegisterCommandsEvent event) {
            if (Config.ENABLE_MARK.get()) {
                MARKCommand.register(event);
            }
            if (Config.ENABLE_TPA.get()) {
                TPACommand.register(event);
            }
            if (Config.ENABLE_TOY.get()) {
                TOYCommand.register(event);
            }
            if (Config.ENABLE_HOME.get()) {
                HOMECommand.register(event);
            }
            if (Config.ENABLE_SPAWN.get()) {
                SpawnCommand.register(event);
            }
            if (Config.ENABLE_BACK.get()) {
                BackCommand.register(event);
            }
            if (Config.ENABLE_BACK_SAFE.get()) {
                BackSafeCommand.register(event);
            }
            if (Config.ENABLE_ASSIST.get()) {
                AssistCommand.register(event);
            }
            if (Config.ENABLE_COMMON.get()) {
                CommonCommand.register(event);
            }
            if (Config.ENABLE_OPS.get()) {
                OPCommand.register(event);
            }
            if (Config.ENABLE_BEACON.get()) {
                BeaconCommand.register(event);
            }
            if (Config.ENABLE_BODY.get()) {
                BodyCommand.register(event);
            }
        }
//    }

}
