package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.*;
import net.lanzr.tpCast.tpCast;
//import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.commons.lang3.tuple.Pair;

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
            tpCastTag oTag = new tpCastTag((ServerPlayer)event.getOriginal());
            ServerPlayer player = (ServerPlayer) event.getEntity();
            tpCastTag nTag = new tpCastTag(player);

            if(oTag.hasKey(oTag.HomePosAlias)) {
                Pair<Vec3,String> p = oTag.getHome();
                nTag.setHome(p.getLeft(),p.getRight());
            }
            if(oTag.hasKey(oTag.BackPosAlias)) {
                Pair<Vec3,String> p = oTag.getBack();
                nTag.setBack(p.getLeft(),p.getRight());
            }
            nTag.setCoolDownStamp(oTag.getCoolDownStamp());
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
