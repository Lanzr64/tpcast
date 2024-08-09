package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.TpCastMethod;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.api.tpTools.tpRequests;
import net.lanzr.tpCast.tpCast;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.tree.ExpandVetoException;
import java.util.Collections;
import java.util.List;

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
            // event.getDispatcher().register(
            //         Commands.literal("overload-tp")
            //             .then(Commands.argument("location", Vec3Argument.vec3())
            //             .executes(ctx -> {
            //                 Vec3 pos = Vec3Argument.getVec3(ctx,"location");
            //                 ServerPlayer player = ctx.getSource().getPlayerOrException();
            //                 tpCastTag tag = new tpCastTag(player);
            //                 tpCastStr str = new tpCastStr(player);
            //                 boolean castAble = (tag.getCoolDownLevel(player.getLevel().getGameTime()) <= tag.MaxLevel);
            //                 if(!castAble) {
            //                     str.sendCoolDownInfoMsg();
            //                     return -1;
            //                 }
            //                 player.teleportTo(pos.x,pos.y,pos.z);
            //                 tag.castOverload(7);
            //                 str.sendCoolDownInfoMsg();
            //                 return  1;
            //             })
            //         )
            // );
             event.getDispatcher().register(
                 Commands.literal("tpa")
                     .then(Commands.argument("target", EntityArgument.player())
                     .executes(ctx -> {
                             ServerPlayer player = ctx.getSource().getPlayerOrException();
                             ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx,"target");
                             if(targetPlayer.getUUID() != player.getUUID()) {
                                 tpRequests.add(targetPlayer.getUUID(),player.getUUID());

                                 targetPlayer.sendSystemMessage(Component.literal(String.format("！！！ %s 想来你的身边",player.getName().getString())).withStyle(ChatFormatting.YELLOW), false);
                                 targetPlayer.sendSystemMessage(Component.literal("使用 /tpy 接受 使用 /tpn 拒绝").withStyle(ChatFormatting.YELLOW), false);
                             }
                             return  1;
                         })
                    )
             );
             event.getDispatcher().register(
                 Commands.literal("tpy")
                     .executes(ctx -> {
                         ServerPlayer player = ctx.getSource().getPlayerOrException();

                         if (tpRequests.pending(player.getUUID())) {
                             List<ServerPlayer> playerlist = ctx.getSource().getLevel().getPlayers(players -> true);
                             Boolean playerFound = false;
                             for (int i = 0; i < playerlist.size(); ++ i) {
                                 if (playerlist.get(i).getUUID().equals(tpRequests.fromWho((player.getUUID())))) {
                                     // 发现目标， 允许传送
                                     playerFound = true;
                                     ServerPlayer teleporter = playerlist.get(i);
                                     ServerPlayer target =  player;

                                     BlockPos tPos = target.getOnPos();

                                     tpCastTag tag = new tpCastTag(teleporter);
                                     tpCastStr str = new tpCastStr(teleporter);

                                     boolean castAble = (tag.getCoolDownLevel(teleporter.level().getGameTime()) <= tag.MaxLevel);
                                     if(!castAble) {
                                         teleporter.sendSystemMessage(Component.literal("已经过载，无法传送"), false);
                                         str.sendCoolDownInfoMsg();
                                         return -1;
                                     }

                                     // 在这里进行判定检测是否可以传送
                                     teleporter.teleportTo( target.serverLevel(),
                                             tPos.getX(),tPos.getY()+1,tPos.getZ(),
                                             teleporter.getYRot(),teleporter.getXRot());
                                     tag.castOverload(1.5f);
                                     str.sendCoolDownInfoMsg();
                                 }
                             }
                             if (!playerFound) {
                                 player.sendSystemMessage(Component.literal("他似乎不在"), false);
                             }
                             tpRequests.remove(player.getUUID());
                         } else {
                             player.sendSystemMessage(Component.literal("看起来没有人想来找你"), false);
                         }
                         return  1;
                     })
             );
             event.getDispatcher().register(
                 Commands.literal("tpn")
                     .executes(ctx -> {
                         ServerPlayer player = ctx.getSource().getPlayerOrException();
                         tpCastTag tag = new tpCastTag(player);
                         tpCastStr str = new tpCastStr(player);
                         if (tpRequests.pending(player.getUUID())) {
                             List<ServerPlayer> playerlist = ctx.getSource().getLevel().getPlayers(players -> true);
                             for (int i = 0; i < playerlist.size(); ++ i) {
                                 if (playerlist.get(i).getUUID().equals(tpRequests.fromWho(player.getUUID()))) {
                                     playerlist.get(i).sendSystemMessage(Component.literal("看起来它不想你去找他"), false);
                                 }
                             }
                             tpRequests.remove(player.getUUID());

                         } else {
                             player.sendSystemMessage(Component.literal("看起来没有人想来找你"), false);
                         }
                         return  1;
                     })
             );
            event.getDispatcher().register(
                    Commands.literal("cast-off").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Inventory inv = player.getInventory();
                        inv.dropAll();
                        player.sendSystemMessage(Component.literal("Cast! Off !!!!!"),true);
                        return 0;
                    })
            );
            event.getDispatcher().register(
                    Commands.literal("self-check").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
                        tpCastStr str = new tpCastStr(player);
                        str.sendCoolDownInfoMsg();
                        return 0;
                    })
            );
            event.getDispatcher().register(
                    Commands.literal("resetCoolDown").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        long gt = ctx.getSource().getLevel().getGameTime();
                        tpCastTag tag = new tpCastTag(player);
                        tag.setCoolDownStamp(gt);
                        return 0;
                    })
                    .requires(ctx-> {return ctx.hasPermission(4);   
                    })
            );
//            event.getDispatcher().register(
//                    Commands.literal("wTst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
////                        Level lvl = ctx.getSource().level();
//                        long gt = ctx.getSource().level().getGameTime();
//                        tpCastTag tag = new tpCastTag(player);
//                        player.sendSystemMessage(Component.literal("time "+gt),true);
//                        tag.setCoolDownStamp(gt);
//                        return 0;
//                    })
//            );
//            event.getDispatcher().register(
//                    Commands.literal("tst").executes(ctx -> {
//                        ServerPlayer player = ctx.getSource().getPlayerOrException();
//                        long gt = ctx.getSource().level().getGameTime();
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
                        boolean castAble = (tag.getCoolDownLevel(player.level().getGameTime()) <= tag.MaxLevel);
                        if(!castAble) {
                            str.sendCoolDownInfoMsg();
                            return -1;
                        }
                        if (tag.hasKey(tag.HomePosAlias)) {
                            tag.castOverload(1);
                            Pair<Vec3,String> home = tag.getHome();
                            ResourceLocation rl = new ResourceLocation(home.getRight());

                            ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION, rl);
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
                    Commands.literal("spawn").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
                        tpCastStr str = new tpCastStr(player);
                        boolean castAble = (tag.getCoolDownLevel(player.level().getGameTime()) <= tag.MaxLevel);
                        if(!castAble) {
                            str.sendCoolDownInfoMsg();
                            return -1;
                        }

                        MinecraftServer server = player.getServer();
                        ServerLevel sl = server.getLevel(Level.OVERWORLD);

                        int x = sl.getSharedSpawnPos().getX();
                        int y = sl.getSharedSpawnPos().getY();
                        int z = sl.getSharedSpawnPos().getZ();

                        tag.castOverload(1);
                        player.teleportTo( sl,
                                x,y,z,
                                player.getYRot(),player.getXRot());
                        str.sendCoolDownInfoMsg();
                        return 1;
                    })
            );
            event.getDispatcher().register(
                    Commands.literal("back").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        tpCastTag tag = new tpCastTag(player);
                        tpCastStr str = new tpCastStr(player);
                        boolean castAble = (tag.getCoolDownLevel(player.level().getGameTime()) <= tag.MaxLevel);
                        if(!castAble) {
                            str.sendCoolDownInfoMsg();
                            return -1;
                        }
                        if (tag.hasKey(tag.BackPosAlias)) {
                            tag.castOverload(0.5f);
                            Pair<Vec3,String> home = tag.getBack();
                            ResourceLocation rl = new ResourceLocation(home.getRight());
                            ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
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
