package net.lanzr.tpCast.events;

import net.lanzr.tpCast.ExampleMod;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.w3c.dom.events.EventTarget;

import java.awt.print.Printable;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {

    @Mod.EventBusSubscriber(modid = ExampleMod.MODID)
    public static class RegisterCommands {

        static public void printSTr(String str) {
            System.out.println(str);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerDeath(LivingDeathEvent event) {
//            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
//            if(isPlayer ) {
//                printSTr("is player");
//                ServerPlayer player  = (ServerPlayer) event.getEntity();
//                BlockPos playerPos = player.getOnPos();
//                boolean hasHomepos = player.getPersistentData().getIntArray(ExampleMod.MODID + "homepos").length != 0;
//                printSTr("has flag ?"+hasHomepos);
//                player.getPersistentData().putIntArray(ExampleMod.MODID + "homepos",
//                        new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
            boolean isPlayer = event.getEntity() instanceof ServerPlayer;
            if(isPlayer) {
                ServerPlayer player  = (ServerPlayer) event.getEntity();
                BlockPos playerPos = player.getOnPos();
                player.getPersistentData().putIntArray(ExampleMod.MODID + "homepos",new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
                player.getPersistentData().putString(ExampleMod.MODID + "homedim", new String(player.getLevel().dimension().location().toString()));
            }
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static synchronized void onPlayerCloned(PlayerEvent.Clone event) {
            ServerPlayer player  = (ServerPlayer) event.getEntity();
            // old is original
            boolean hasHomepos = event.getOriginal().getPersistentData().getIntArray(ExampleMod.MODID + "homepos").length != 0;
            if(hasHomepos) {
                int[] playerPos = event.getOriginal().getPersistentData().getIntArray(ExampleMod.MODID + "homepos");
                String dim = event.getOriginal().getPersistentData().getString(ExampleMod.MODID+"homedim");


                player.getPersistentData().putIntArray(ExampleMod.MODID+"homepos",new int[]{playerPos[0],playerPos[1],playerPos[2]});
                player.getPersistentData().putString(ExampleMod.MODID+"homedim",new String(dim));
            }
        }

        @SubscribeEvent
        public static void CommandRegistration(RegisterCommandsEvent event) {
//            event.getDispatcher().register(
//                    Commands.literal("wTst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
//                        BlockPos playerPos = player.getOnPos();
//                        String pos = "(" + playerPos.getX() + ", " + playerPos.getY() + ", " + playerPos.getZ() + ")";
//
//                        player.getPersistentData().putIntArray(ExampleMod.MODID + "homepos",new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
//                        player.getPersistentData().putString(ExampleMod.MODID + "homedim", new String(player.getLevel().dimension().location().toString()));
//                        ctx.getSource().sendSuccess(Component.nullToEmpty("set pos data"), true);
//                        return 0;
//                    })
//            );
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
                        boolean hasHomepos = player.getPersistentData().getIntArray(ExampleMod.MODID + "homepos").length != 0;
                        if (hasHomepos) {
                            int[] playerPos = player.getPersistentData().getIntArray(ExampleMod.MODID + "homepos");
                            String dim = player.getPersistentData().getString(ExampleMod.MODID+"homedim");
                            ResourceLocation rl = new ResourceLocation(dim);
                            ResourceKey<Level> mydim = ResourceKey.create(Registry.DIMENSION_REGISTRY,rl);

                            player.teleportTo(player.getServer().getLevel(mydim), playerPos[0],playerPos[1],playerPos[2],player.getYRot(),player.getXRot());
//                        player.setPositionAndUpdate(playerPos[0], playerPos[1], playerPos[2]);
//                            player.teleportToWithTicket(playerPos[0],playerPos[1],playerPos[2]);

                            CompoundTag tag = player.getPersistentData();
                            String entrie = ExampleMod.MODID +"homepos";
                            if (tag.contains(entrie)) {
                                // 删除条目
                                tag.remove(entrie);

                                player.getPersistentData().put("CustomData", tag);
                            }

                            return 1;
                        } else {
//                            ctx.getSource().sendSuccess(Component.nullToEmpty("not now"), true);
                            return -1;
                        }
                    })
            );
        }
    }

}
