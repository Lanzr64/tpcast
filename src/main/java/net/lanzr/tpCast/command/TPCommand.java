package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class TPCommand {
    public static void register(net.neoforged.neoforge.event.RegisterCommandsEvent event) {

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
//         event.getDispatcher().register(
//                 Commands.literal("overload-tp")
//                     .then(Commands.argument("location", Vec3Argument.vec3())
//                     .executes(ctx -> cb_overloadTP(ctx.getSource().getPlayerOrException(),Vec3Argument.getVec3(ctx,"location")))
//                 )
//         );
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
            LZCommonForgeApi.sendSystemMessage(player,String.format("熔断恢复倒计时 %d:%d ", remain/60, remain %60),LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
            return 0;
        }
        return 1;
    }
    private static int cb_resetCoolDown(ServerPlayer player,ServerPlayer targetPlayer,long gt) {
        tpCastTag tag = new tpCastTag(targetPlayer);
        tag.setCoolDownStamp(gt);
        LZCommonForgeApi.sendSystemMessage(player,String.format("%s SAMA清除了 %s 的过载", player.getName().getString(),
                targetPlayer.getName().getString()),LZCommonForgeApi.MsgTypes.OTHER.getmFmt());
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
            LZCommonForgeApi.sendSystemMessage(player,String.format("%s 对你的祈福生效了！ ", targetPlayer.getName().getString()),LZCommonForgeApi.MsgTypes.OTHER.getmFmt());

            str.sendCoolDownInfoMsg();
            tagetStr.sendCoolDownInfoMsg();
        }
        return  1;
    }


    private static int cb_spawn(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            str.sendCoolDownInfoMsg();
            return -1;
        }

        MinecraftServer server = player.getServer();
        ServerLevel sl = server.getLevel(Level.OVERWORLD);
//                        ServerLevel sl = server.getLevel(twf);

        int x = sl.getSharedSpawnPos().getX();
        int y = sl.getSharedSpawnPos().getY();
        int z = sl.getSharedSpawnPos().getZ();

        tag.castOverload((float) Config.levelCostSPAWN);
        player.teleportTo( sl,
                x,y,z,
                player.getYRot(),player.getXRot());
        str.sendCoolDownInfoMsg();
        return 1;
    }
    private static int cb_back(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            str.sendCoolDownInfoMsg();
            return -1;
        }
        if (tag.hasKey(tag.BackPosAlias)) {
            tag.castOverload((float) Config.levelCostBack);
            Pair<Vec3,String> home = tag.getBack();
            ResourceLocation rl = LZCommonForgeApi.getDimensionResourceLocation(home.getRight());
            ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
            player.teleportTo(player.getServer().getLevel(mydim), home.getLeft().x,home.getLeft().y+1,home.getLeft().z,player.getYRot(),player.getXRot());
            tag.rmKey(tag.BackPosAlias);
            str.sendCoolDownInfoMsg();
            return 1;
        } else {
            LZCommonForgeApi.sendSystemMessage(player,"你还没死呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return -1;
        }
    }
    private static int cb_chunktp(ServerPlayer player, Vec2 pos) {
        Vec2 tPos = new Vec2(pos.x * 16, pos.y * 16);
        player.teleportTo(tPos.x,player.getY(),tPos.y);
        return  1;
    }

    private static int cb_overloadTP(ServerPlayer player, Vec3 pos) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            str.sendCoolDownInfoMsg();
            return -1;
        }
        player.teleportTo(pos.x,pos.y,pos.z);
        tag.castOverload(7);
        str.sendCoolDownInfoMsg();
        return  1;
    }
}
