package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.*;
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
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

@EventBusSubscriber(modid = tpCast.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvent {
//    public static float levelCostBack = (float)Config.levelCostBack;
//    public static float levelCostHome = (float)Config.levelCostHome;
//    public static float levelCostTPA = (float)Config.levelCostTPA;
//    public static float levelCostSPAWN = (float)Config.levelCostSPAWN;
    @EventBusSubscriber(modid = tpCast.MODID)
    public static class RegisterCommands {
        static public void printSTr(String str) {
            System.out.println(str);
        }

//        @SubscribeEvent(priority = EventPriority.HIGHEST)
//        public static synchronized void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
//            ServerPlayer player = (ServerPlayer) event.getEntity();
//            tpCastTag tag = new tpCastTag(player);
////            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
//        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerDeath(LivingDeathEvent event) {
            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
            if(isPlayer ) {
                ServerPlayer player  = (ServerPlayer) event.getEntity();
                tpCastTag tag = new tpCastTag(player);
                tag.setBack();
//                printSTr("is player");
//                BlockPos playerPos = player.getOnPos();
//                boolean hasHomepos = player.getPersistentData().getIntArray(ExampleMod.MODID + "homepos").length != 0;
//                printSTr("has flag ?"+hasHomepos);
//                player.getPersistentData().putIntArray(ExampleMod.MODID + "homepos",
//                        new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
//            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
//            if(isPlayer) {
//                ServerPlayer player  = (ServerPlayer) event.getEntity();
//                BlockPos playerPos = player.getOnPos();
//                int[] pos = new int[3];
//                pos[0] = playerPos.getX();
//                pos[1] = playerPos.getY();
//                pos[2] = playerPos.getZ();
//                player.getPersistentData().putIntArray(tpCast.MODID + "homepos", pos);
//                player.getPersistentData().putString(tpCast.MODID + "homedim", player.level().dimension().location().toString());
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
            TOYCommand.register(event);
            HOMECommand.register(event);
            SpawnCommand.register(event);
            BackCommand.register(event);
            AssistCommand.register(event);
            CommonCommand.register(event);

        }
    }

}
