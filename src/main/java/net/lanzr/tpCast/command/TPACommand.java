package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.api.tpTools;
import net.lanzr.tpCast.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.List;

public class TPACommand {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("tpa")
                    .then(Commands.argument("target", EntityArgument.player())
                            .executes(ctx -> cb_tpa(ctx.getSource().getPlayerOrException(),EntityArgument.getPlayer(ctx,"target")))
                    )
        );
        event.getDispatcher().register(
                Commands.literal("tpahere")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> cb_tpahere(ctx.getSource().getPlayerOrException(),EntityArgument.getPlayer(ctx,"target")))
                    )
        );
        event.getDispatcher().register(
                Commands.literal("tpy")
                        .executes(ctx -> cb_tpy(ctx.getSource().getPlayerOrException(),ctx.getSource().getServer().getPlayerList().getPlayers()))
        );
        event.getDispatcher().register(
                Commands.literal("tpn")
                        .executes(ctx -> cb_tpn(ctx.getSource().getPlayerOrException(),ctx.getSource().getServer().getPlayerList().getPlayers()))
        );
    }
    private static int cb_tpa(ServerPlayer player, ServerPlayer targetPlayer) {
        if(targetPlayer.getUUID() != player.getUUID()) {
            tpTools.tpaRequests.add(targetPlayer.getUUID(),player.getUUID());
            targetPlayer.sendMessage(new TextComponent(String.format("！！！ %s 想来你的身边",player.getName().getString()))
                    .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
            targetPlayer.sendMessage(new TextComponent("使用 /tpy 接受 使用 /tpn 拒绝")
                    .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
        } else {
            targetPlayer.sendMessage(new TextComponent("禁止原地tp")
                    .withStyle(ChatFormatting.RED), Util.NIL_UUID);
        }
        return  1;
    }
    private static int cb_tpahere(ServerPlayer player, ServerPlayer targetPlayer) {
        if(targetPlayer.getUUID() != player.getUUID()) {
            tpTools.tpahereRequests.add(targetPlayer.getUUID(),player.getUUID());

            targetPlayer.sendMessage(new TextComponent(String.format("！！！ %s 想邀请你去他的身边",player.getName().getString()))
                    .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);

            targetPlayer.sendMessage(new TextComponent("使用 /tpy 接受 使用 /tpn 拒绝")
                    .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
        } else {
            targetPlayer.sendMessage(new TextComponent("禁止原地tp")
                    .withStyle(ChatFormatting.RED), Util.NIL_UUID);
        }
        return  1;
    }

    private static int cb_tpy(ServerPlayer player,List<ServerPlayer> playerList) {
        // tpa check
        if (tpTools.tpaRequests.pending(player.getUUID())) {
            Boolean playerFound = false;
            for (int i = 0; i < playerList.size(); ++ i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpaRequests.fromWho((player.getUUID())))) {
                    // 发现目标， 允许传送
                    playerFound = true;
                    ServerPlayer teleporter = playerList.get(i);
                    ServerPlayer target =  player;

                    BlockPos tPos = target.getOnPos();

                    tpCastTag tag = new tpCastTag(teleporter);
                    tpCastStr str = new tpCastStr(teleporter);

                    boolean castAble = (tag.getCoolDownLevel(teleporter.getLevel().getGameTime()) <= tag.MaxLevel);
                    if(!castAble) {

                        teleporter.sendMessage(new TextComponent("已经过载，无法传送")
                                .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
//                                         teleporter.sendSystemMessage(Component.literal("已经过载，无法传送"), false);
                        str.sendCoolDownInfoMsg();
                        return -1;
                    }

                    // 在这里进行判定检测是否可以传送
                    teleporter.teleportTo( target.getLevel(),
                            tPos.getX(),tPos.getY()+1,tPos.getZ(),
                            teleporter.getYRot(),teleporter.getXRot());
                    tag.castOverload((float) Config.levelCostTPA);
                    str.sendCoolDownInfoMsg();
                }
            }
            if (!playerFound) {
                player.sendMessage(new TextComponent("他似乎不在")
                        .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
            }
            tpTools.tpaRequests.remove(player.getUUID());
        } else if (tpTools.tpahereRequests.pending(player.getUUID())) {
            Boolean playerFound = false;
            for (int i = 0; i < playerList.size(); ++ i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpahereRequests.fromWho((player.getUUID())))) {
                    // 发现目标， 允许传送
                    playerFound = true;
                    ServerPlayer target = playerList.get(i);
                    ServerPlayer teleporter =  player;

                    BlockPos tPos = target.getOnPos();

                    tpCastTag tag = new tpCastTag(teleporter);
                    tpCastStr str = new tpCastStr(teleporter);

                    boolean castAble = (tag.getCoolDownLevel(teleporter.getLevel().getGameTime()) <= tag.MaxLevel);
                    if(!castAble) {

                        teleporter.sendMessage(new TextComponent("已经过载，无法传送")
                                .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
//                                         teleporter.sendSystemMessage(Component.literal("已经过载，无法传送"), false);
                        str.sendCoolDownInfoMsg();
                        return -1;
                    }

                    // 在这里进行判定检测是否可以传送
                    teleporter.teleportTo( target.getLevel(),
                            tPos.getX(),tPos.getY()+1,tPos.getZ(),
                            teleporter.getYRot(),teleporter.getXRot());
                    tag.castOverload((float) Config.levelCostTPA);
                    str.sendCoolDownInfoMsg();
                }
            }
            if (!playerFound) {
                player.sendMessage(new TextComponent("他似乎不在")
                        .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
            }
            tpTools.tpaRequests.remove(player.getUUID());
        } else {
            player.sendMessage(new TextComponent("看起来没有tp请求")
                    .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
        }
        return  1;
    }

    private static int cb_tpn(ServerPlayer player,List<ServerPlayer> playerList) {
        if (tpTools.tpaRequests.pending(player.getUUID())) {
            for (int i = 0; i < playerList.size(); ++ i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpaRequests.fromWho(player.getUUID()))) {
                    playerList.get(i).sendMessage(new TextComponent(String.format("%s 拒绝了你的tp请求", player.getName().getString()))
                            .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
                }
            }
            tpTools.tpaRequests.remove(player.getUUID());

        } else {
            player.sendMessage(new TextComponent("看起来没有tp请求")
                    .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
        }
        return  1;
    }

}
