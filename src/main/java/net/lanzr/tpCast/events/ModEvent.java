package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.*;
import net.lanzr.tpCast.tpCast;
//import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

@EventBusSubscriber(modid = tpCast.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvent {
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

            // 必须有新的数据
            CompoundTag old_Tag = event.getOriginal().getPersistentData().getCompound(tpCast.MODID);
            new_player.getPersistentData().put(tpCast.MODID, old_Tag);
        }

        @SubscribeEvent
        public static void CommandRegistration(RegisterCommandsEvent event) {
            MARKCommand.register(event);
            TPACommand.register(event);
            TPCommand.register(event);
            TOYCommand.register(event);
            HOMECommand.register(event);
        }
}
