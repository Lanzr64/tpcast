package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.teleport.TeleportRequests;
import net.lanzr.tpCast.teleport.TeleportService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.UUID;

public final class TPACommand {
    private final TeleportRequests requests = new TeleportRequests();

    private TPACommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        new TPACommand().registerCommands(event);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpa")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(requestCommand(TeleportRequests.Direction.TO_TARGET))));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpahere")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(requestCommand(TeleportRequests.Direction.TO_REQUESTER))));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpy")
                        .executes(ctx -> accept(ctx.getSource().getPlayerOrException())));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tpn")
                        .executes(ctx -> reject(ctx.getSource().getPlayerOrException())));
    }

    private TpCommand requestCommand(TeleportRequests.Direction direction) {
        return new TpCommand() {
            @Override
            protected boolean requiresCooldownCheck() {
                return false;
            }

            @Override
            protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer)
                    throws CommandSyntaxException {
                ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                if (target.getUUID().equals(tpPlayer.player.getUUID())) {
                    LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "禁止原地tp",
                            LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                    return -1;
                }

                requests.submit(target.getUUID(), tpPlayer.player.getUUID(), direction);
                String message = direction == TeleportRequests.Direction.TO_TARGET
                        ? "！！！ %s 想来你的身边"
                        : "！！！ %s 想把你送到他的身边";
                LZCommonForgeApi.sendSystemMessage(target,
                        String.format(message, tpPlayer.player.getName().getString()),
                        LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                LZCommonForgeApi.sendSystemMessage(target, "使用 /tpy 接受 使用 /tpn 拒绝",
                        LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return 1;
            }
        };
    }

    private int accept(ServerPlayer recipient) {
        TeleportRequests.Request request = requests.take(recipient.getUUID());
        if (request == null) {
            return noRequest(recipient);
        }
        ServerPlayer requester = findPlayer(recipient, request.requester());
        if (requester == null) {
            LZCommonForgeApi.sendSystemMessage(recipient, "他似乎不在",
                    LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return -1;
        }

        ServerPlayer teleporter = request.direction() == TeleportRequests.Direction.TO_TARGET
                ? requester : recipient;
        ServerPlayer destination = request.direction() == TeleportRequests.Direction.TO_TARGET
                ? recipient : requester;
        TpCastPlayer caster = new TpCastPlayer(teleporter);
        if (caster.tag.getCoolDownLevel(System.currentTimeMillis()) > caster.tag.MaxLevel) {
            LZCommonForgeApi.sendSystemMessage(recipient, "已经过载，无法传送",
                    LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            caster.sendCoolDownInfoMsg();
            return -1;
        }

        BlockPos pos = destination.getOnPos();
        TeleportService.teleport(teleporter, destination.serverLevel(),
                pos.getX(), pos.getY() + 1, pos.getZ());
        caster.tag.castOverload(Config.LEVEL_COST_TPA.get().floatValue());
        LZCommonForgeApi.sendSystemMessage(recipient,
                String.format("！！！ %s 接受了tp请求", destination.getName().getString()),
                LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        caster.sendCoolDownInfoMsg();
        return 1;
    }

    private int reject(ServerPlayer recipient) {
        TeleportRequests.Request request = requests.take(recipient.getUUID());
        if (request == null) {
            return noRequest(recipient);
        }
        ServerPlayer requester = findPlayer(recipient, request.requester());
        if (requester != null) {
            String command = request.direction() == TeleportRequests.Direction.TO_TARGET
                    ? "tp" : "tpahere";
            LZCommonForgeApi.sendSystemMessage(requester,
                    String.format("%s 拒绝了你的%s请求", recipient.getName().getString(), command),
                    LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        }
        return 1;
    }

    private static ServerPlayer findPlayer(ServerPlayer recipient, UUID uuid) {
        return recipient.getServer().getPlayerList().getPlayer(uuid);
    }

    private static int noRequest(ServerPlayer recipient) {
        LZCommonForgeApi.sendSystemMessage(recipient, "看起来没有tp请求",
                LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        return -1;
    }
}
