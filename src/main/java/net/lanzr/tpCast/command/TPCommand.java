package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.MsgTypes;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.player.CastPlayer;
import net.lanzr.tpCast.player.CastPlayerList;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.RegisterCommandsEvent;

public class TPCommand {
    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("spawn").executes(ctx -> cb_spawn(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("back").executes(ctx -> cb_back(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("self-check").executes(ctx -> cb_selfCheck(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("cast-assist")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> cb_castAssist(ctx.getSource().getPlayerOrException(),EntityArgument.getPlayer(ctx,"target"))))
        );
        event.getDispatcher().register(
                Commands.literal("resetCoolDown")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> cb_resetCoolDown(ctx.getSource().getPlayerOrException(),EntityArgument.getPlayer(ctx,"target"),ctx.getSource().getLevel().getGameTime())))
                                .requires(ctx-> ctx.hasPermission(4))
        );

        event.getDispatcher().register(
                Commands.literal("c-tp")
                        .then(Commands.argument("location", Vec2Argument.vec2())
                                .executes(ctx -> cb_chunktp(ctx.getSource().getPlayerOrException(), Vec2Argument.getVec2(ctx,"location"))))
                        .requires(ctx-> ctx.hasPermission(4))
        );

    }

    private static int cb_selfCheck(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        str.sendCoolDownInfoMsg();
        long gt = LZCommonForgeApi.playerGetLevel(player).getGameTime();
        int gLv = tag.getCoolDownLevel(gt);
        if(gLv > tag.MaxLevel) {
            long remain = tag.getCoolDownStamp() - gt - tag.CoolDownPiece * tag.MaxLevel;
            remain = remain < 0 ? 0 : remain / 10;
            LZCommonForgeApi.sendSystemMessage(player,String.format("熔断恢复倒计时 %d:%d ", remain/60, remain %60),MsgTypes.ALERT.getmFmt());
            return 0;
        }
        return 1;
    }

    private static int cb_resetCoolDown(ServerPlayer player,ServerPlayer targetPlayer, long gt) {

        CastPlayer commandPlayer = CastPlayerList.get(player.getUUID());

        tpCastTag tag = new tpCastTag(targetPlayer);
        tag.setCoolDownStamp(gt);
        LZCommonForgeApi.sendSystemMessage(player,String.format("%s SAMA清除了 %s 的过载", player.getName().getString(),
                targetPlayer.getName().getString()), MsgTypes.OTHER.getmFmt());
        return 0;
    }

    private static int cb_castAssist(ServerPlayer player,ServerPlayer targetPlayer) {
        if(targetPlayer.getUUID() != player.getUUID()) {
            tpCastTag tag = new tpCastTag(player);
            tpCastStr str = new tpCastStr(player);
            boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
            if(!castAble) {
                str.sendCoolDownInfoMsg();
                return -1;
            }
            tag.castOverload((float) 2);

            tpCastTag targetTag = new tpCastTag(targetPlayer);
            tpCastStr tagetStr = new tpCastStr(targetPlayer);

            targetTag.castOverload((float) -1.4);
            LZCommonForgeApi.sendSystemMessage(player,String.format("%s 对你的祈福生效了！ ", targetPlayer.getName().getString()),MsgTypes.OTHER.getmFmt());

            str.sendCoolDownInfoMsg();
            tagetStr.sendCoolDownInfoMsg();
        }
        return  1;
    }

    private static int cb_spawn(ServerPlayer player) {
        CastPlayer commandPlayer = CastPlayerList.get(player.getUUID());
        commandPlayer.returnSpawn();
        return 1;
    }

    private static int cb_back(ServerPlayer player) {
        CastPlayer commandPlayer = CastPlayerList.get(player.getUUID());
        commandPlayer.returnDeath();
        return 1;
    }
    private static int cb_chunktp(ServerPlayer player, Vec2 pos) {
        Vec2 tPos = new Vec2(pos.x * 16, pos.y * 16);
        player.teleportTo(tPos.x,player.getY(),tPos.y);
        return  1;
    }

}
