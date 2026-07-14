package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.api.tpTools;
import net.lanzr.tpCast.command.tools.CommandTools;import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

public class TPACommand {
    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("tpa")
                    .then(Commands.argument("target", EntityArgument.player())
                            .executes(COMMAND_TPA)
                    ));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpahere")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(COMMAND_TPAHERE)
                    ));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpy")
                        .executes(ctx -> cb_tpy(ctx.getSource().getPlayerOrException(), ctx.getSource().getServer().getPlayerList().getPlayers())));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpn")
                        .executes(ctx -> cb_tpn(ctx.getSource().getPlayerOrException(), ctx.getSource().getServer().getPlayerList().getPlayers())));
    }

    private static final TpCommand COMMAND_TPA = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "target");

            if (targetPlayer.getUUID() != tpPlayer.player.getUUID()) {
                tpTools.tpaRequests.add(targetPlayer.getUUID(), tpPlayer.player.getUUID());
                LZCommonForgeApi.sendSystemMessage(targetPlayer, String.format("！！！ %s 想来你的身边", tpPlayer.player.getName().getString()), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                LZCommonForgeApi.sendSystemMessage(targetPlayer, "使用 /tpy 接受 使用 /tpn 拒绝", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            } else {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "禁止原地tp", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
            }
            return 1;
        }
    };

    private static final TpCommand COMMAND_TPAHERE = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "target");
            if (targetPlayer.getUUID() != tpPlayer.player.getUUID()) {
                tpTools.tpahereRequests.add(targetPlayer.getUUID(), tpPlayer.player.getUUID());
                LZCommonForgeApi.sendSystemMessage(targetPlayer, String.format("！！！ %s 想把你送到他的身边", tpPlayer.player.getName().getString()), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                LZCommonForgeApi.sendSystemMessage(targetPlayer, "使用 /tpy 接受 使用 /tpn 拒绝", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            } else {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "禁止原地tp", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
            }
            return 1;
        }
    };

    private static int executeTpaTeleport(ServerPlayer teleporter, ServerPlayer target, ServerPlayer player) {
        BlockPos tPos = target.getOnPos();
        TpCastPlayer tpc = new TpCastPlayer(teleporter);

        boolean castAble = (tpc.tag.getCoolDownLevel(System.currentTimeMillis()) <= tpc.tag.MaxLevel);
        if (!castAble) {
            LZCommonForgeApi.sendSystemMessage(player, "已经过载，无法传送", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            tpc.sendCoolDownInfoMsg();
            return -1;
        }

        teleporter.teleportTo(target.serverLevel(), tPos.getX(), tPos.getY() + 1, tPos.getZ(), teleporter.getYRot(), teleporter.getXRot());
        tpc.tag.castOverload(Config.LEVEL_COST_TPA.get().floatValue());
        LZCommonForgeApi.sendSystemMessage(player, String.format("！！！ %s 接受了tp请求", target.getName().getString()), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        tpc.sendCoolDownInfoMsg();
        return 1;
    }

    private static int cb_tpy(ServerPlayer player, List<ServerPlayer> playerList) {
        // 检查TPA请求
        if (tpTools.tpaRequests.pending(player.getUUID())) {
            boolean playerFound = false;
            for (int i = 0; i < playerList.size(); ++i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpaRequests.fromWho(player.getUUID()))) {
                    playerFound = true;
                    ServerPlayer teleporter = playerList.get(i);
                    ServerPlayer target = player;
                    executeTpaTeleport(teleporter, target, player);
                }
            }
            if (!playerFound) {
                LZCommonForgeApi.sendSystemMessage(player, "他似乎不在", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            }
            tpTools.tpaRequests.remove(player.getUUID());
        }
        // 检查TPA Here请求
        else if (tpTools.tpahereRequests.pending(player.getUUID())) {
            boolean playerFound = false;
            for (int i = 0; i < playerList.size(); ++i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpahereRequests.fromWho(player.getUUID()))) {
                    playerFound = true;
                    ServerPlayer target = playerList.get(i);
                    ServerPlayer teleporter = player;
                    executeTpaTeleport(teleporter, target, player);
                }
            }
            if (!playerFound) {
                LZCommonForgeApi.sendSystemMessage(player, "他似乎不在", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            }
            tpTools.tpahereRequests.remove(player.getUUID());
        }
        // 没有待处理的请求
        else {
            LZCommonForgeApi.sendSystemMessage(player, "看起来没有tp请求", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        }
        return 1;
    }

    private static int cb_tpn(ServerPlayer player, List<ServerPlayer> playerList) {
        // 检查TPA请求
        if (tpTools.tpaRequests.pending(player.getUUID())) {
            for (int i = 0; i < playerList.size(); ++i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpaRequests.fromWho(player.getUUID()))) {
                    LZCommonForgeApi.sendSystemMessage(playerList.get(i), String.format("%s 拒绝了你的tp请求", player.getName().getString()), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                }
            }
            tpTools.tpaRequests.remove(player.getUUID());
        }
        // 检查TPA Here请求
        else if (tpTools.tpahereRequests.pending(player.getUUID())) {
            for (int i = 0; i < playerList.size(); ++i) {
                if (playerList.get(i).getUUID().equals(tpTools.tpahereRequests.fromWho(player.getUUID()))) {
                    LZCommonForgeApi.sendSystemMessage(playerList.get(i), String.format("%s 拒绝了你的tpahere请求", player.getName().getString()), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                }
            }
            tpTools.tpahereRequests.remove(player.getUUID());
        }
        // 没有待处理的请求
        else {
            LZCommonForgeApi.sendSystemMessage(player, "看起来没有tp请求", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        }
        return 1;
    }
}
