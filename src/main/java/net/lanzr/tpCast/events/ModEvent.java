package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.tpCast;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.tree.ExpandVetoException;

@Mod.EventBusSubscriber(modid = tpCast.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {

    @Mod.EventBusSubscriber(modid = tpCast.MODID)
    public static class RegisterCommands {

        static public void printSTr(String str) {
            System.out.println(str);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            tpCastTag tag = new tpCastTag(player);

//            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
        }
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
//                player.getPersistentData().putString(tpCast.MODID + "homedim", player.getLevel().dimension().location().toString());
            }
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerCloned(PlayerEvent.Clone event) {
            tpCastTag oTag = new tpCastTag((ServerPlayer)event.getOriginal());
            ServerPlayer player = (ServerPlayer) event.getEntity();
            if(oTag.hasKey(oTag.HomePosAlias)) {
                tpCastTag nTag = new tpCastTag(player);
                Pair<Vec3,String> p = oTag.getHome();
                nTag.setHome(p.getLeft(),p.getRight());
            }
            if(oTag.hasKey(oTag.BackPosAlias)) {
                tpCastTag nTag = new tpCastTag(player);
                Pair<Vec3,String> p = oTag.getBack();
                nTag.setBack(p.getLeft(),p.getRight());
            }
        }
        @SubscribeEvent
        public static void CommandRegistration(RegisterCommandsEvent event) {
            event.getDispatcher().register(
                    Commands.literal("wTst").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Level lvl = ctx.getSource().getLevel();
                        System.out.println("game timestamp "+lvl.getGameTime());
                        System.out.println("day timestamp "+lvl.getDayTime());
                        return 0;
                    })
            );
            event.getDispatcher().register(
                    Commands.literal("cast-off").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Inventory inv = player.getInventory();
                        inv.dropAll();
                        return 0;
                    })
            );
//            event.getDispatcher().register(
//                    Commands.literal("rTst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
//                        boolean hasHomepos = player.getPersistentData().getIntArray(ExampleMod.MODID + "homepos").length != 0;
//
//                        if (hasHomepos) {
//                            int[] playerPos = player.getPersistentData().getIntArray(ExampleMod.MODID + "homepos");
//                            String dim = player.getPersistentData().getString(ExampleMod.MODID+"homedim");
////                        player.setPositionAndUpdate(playerPos[0], playerPos[1], playerPos[2]);
//
//                            ctx.getSource().sendSuccess(Component.nullToEmpty("data " + playerPos[0] + " " + playerPos[1] + " " + playerPos[2] + "   dim "+ dim ), true);
//                            return 1;
//                        } else {
//                            ctx.getSource().sendSuccess(Component.nullToEmpty("not any data"), true);
//                            return -1;
//                        }
//                    })
//            );

            event.getDispatcher().register(
                    Commands.literal("back").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
//
                        if (tag.hasKey(tag.BackPosAlias)) {
                            Pair<Vec3,String> home = tag.getBack();
                            ResourceLocation rl = new ResourceLocation(home.getRight());
                            ResourceKey<Level> mydim = ResourceKey.create(Registry.DIMENSION_REGISTRY,rl);
                            player.teleportTo(player.getServer().getLevel(mydim), home.getLeft().x,home.getLeft().y,home.getLeft().z,player.getYRot(),player.getXRot());
                            tag.rmKey(tag.BackPosAlias);
                            return 1;
                        } else {
                            player.sendSystemMessage(Component.literal("你还没死呢!"));
                            return -1;
                        }
                    })
            );
        }
    }

}
