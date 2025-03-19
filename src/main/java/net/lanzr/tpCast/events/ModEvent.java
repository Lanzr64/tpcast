package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.TOYCommand;
import net.lanzr.tpCast.command.TPACommand;
import net.lanzr.tpCast.command.TPCommand;
import net.lanzr.tpCast.tpCast;
//import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
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
//    public static float levelCostBack = (float)Config.levelCostBack;
//    public static float levelCostHome = (float)Config.levelCostHome;
//    public static float levelCostTPA = (float)Config.levelCostTPA;
//    public static float levelCostSPAWN = (float)Config.levelCostSPAWN;
    @Mod.EventBusSubscriber(modid = tpCast.MODID)
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
            TPACommand.register(event);
            TPCommand.register(event);
            TOYCommand.register(event);
        }
    }

}
