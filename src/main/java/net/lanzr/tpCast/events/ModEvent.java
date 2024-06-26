package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.TpCastMethod;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.tpCast;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
//                player.getPersistentData().putString(tpCast.MODID + "homedim", player.getLevel().dimension().location().toString());
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
            event.getDispatcher().register(
                    Commands.literal("cast-off").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Inventory inv = player.getInventory();
                        inv.dropAll();
                        player.sendSystemMessage(Component.literal("Cast! Off !!!!!"),true);
                        return 0;
                    })
            );

//            event.getDispatcher().register(
//                    Commands.literal("wTst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
////                        Level lvl = ctx.getSource().getLevel();
//                        long gt = ctx.getSource().getLevel().getGameTime();
//                        tpCastTag tag = new tpCastTag(player);
//                        player.sendSystemMessage(Component.literal("time "+gt),true);
//                        tag.setCoolDownStamp(gt);
//                        return 0;
//                    })
//            );
//            event.getDispatcher().register(
//                    Commands.literal("tst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
//                        long gt = ctx.getSource().getLevel().getGameTime();
//                        tpCastTag tag = new tpCastTag(player);
//                        tag.setCoolDownStamp(gt);
//                        return 0;
//                    })
//            );
//            event.getDispatcher().register(
//                    Commands.literal("rTst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
//                        tpCastStr str = new tpCastStr(player);
//                        str.sendCoolDownInfoMsg();
//                        return 1;
//                    })
//            );
            event.getDispatcher().register(
                    Commands.literal("sethome").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
                        tag.setHome();
                        return 1;
                    })
            );
            event.getDispatcher().register(
                    Commands.literal("home").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
                        tpCastStr str = new tpCastStr(player);
                        boolean castAble = (tag.getCoolDownLevel(player.getLevel().getGameTime()) <= tag.MaxLevel);
                        if(!castAble) {
                            str.sendCoolDownInfoMsg();
                            return -1;
                        }
                        if (tag.hasKey(tag.HomePosAlias)) {
                            tag.castOverload(1);
                            Pair<Vec3,String> home = tag.getHome();
                            ResourceLocation rl = new ResourceLocation(home.getRight());
                            ResourceKey<Level> mydim = ResourceKey.create(Registry.DIMENSION_REGISTRY,rl);
                            player.teleportTo(player.getServer().getLevel(mydim), home.getLeft().x,home.getLeft().y+1,home.getLeft().z,player.getYRot(),player.getXRot());
                            str.sendCoolDownInfoMsg();
                            return 1;
                        } else {
                            player.sendSystemMessage(Component.literal("你无家可归!"));
                            return -1;
                        }
                    })
            );
            event.getDispatcher().register(
                    Commands.literal("back").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
                        tpCastStr str = new tpCastStr(player);
                        boolean castAble = (tag.getCoolDownLevel(player.getLevel().getGameTime()) <= tag.MaxLevel);
                        if(!castAble) {
                            str.sendCoolDownInfoMsg();
                            return -1;
                        }
                        if (tag.hasKey(tag.BackPosAlias)) {
                            tag.castOverload(0.5f);
                            Pair<Vec3,String> home = tag.getBack();
                            ResourceLocation rl = new ResourceLocation(home.getRight());
                            ResourceKey<Level> mydim = ResourceKey.create(Registry.DIMENSION_REGISTRY,rl);
                            player.teleportTo(player.getServer().getLevel(mydim), home.getLeft().x,home.getLeft().y+1,home.getLeft().z,player.getYRot(),player.getXRot());
                            tag.rmKey(tag.BackPosAlias);
                            str.sendCoolDownInfoMsg();
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
