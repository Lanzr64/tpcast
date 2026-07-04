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
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = tpCast.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @Mod.EventBusSubscriber(modid = tpCast.MODID)
    public static class RegisterCommands {


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
            if (Config.enableMark) {
                MARKCommand.register(event);
            }
            if (Config.enableTPA) {
                TPACommand.register(event);
            }
            if (Config.enableToy) {
                TOYCommand.register(event);
            }
            if (Config.enableHome) {
                HOMECommand.register(event);
            }
            if (Config.enableSpawn) {
                SpawnCommand.register(event);
            }
            if (Config.enableBack) {
                BackCommand.register(event);
            }
            if (Config.enableAssist) {
                AssistCommand.register(event);
            }
            if (Config.enableCommon) {
                CommonCommand.register(event);
            }
            if (Config.enableOps) {
                OPCommand.register(event);
            }

        }
    }

}
