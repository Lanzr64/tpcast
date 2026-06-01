package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class HOMECommand {
    public static void register(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
//        event.getDispatcher().register(
//                Commands.literal("sethome").executes(ctx -> cb_sethome(ctx.getSource().getPlayerOrException()))
//        );
        event.getDispatcher().register(
//                Commands.literal("home").executes(ctx -> cb_home(ctx.getSource().getPlayerOrException()))
                Commands.literal("home").executes(ctx -> cb_returnBED(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("home-desp").executes(ctx -> cb_home(ctx.getSource().getPlayerOrException()))
        );
    }

    private static int cb_returnBED(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            // 无法使用
            str.sendCoolDownInfoMsg();
            return -1;
        }

        BlockPos respawnPos = player.getRespawnPosition();
        ResourceKey<Level> respawnDim = player.getRespawnDimension();
        if(respawnPos == null || respawnDim == null) {
            LZCommonForgeApi.sendSystemMessage(player,"你还没有睡觉呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return -1;
        } else {
            tag.castOverload((float) Config.levelCostHome);
            player.teleportTo(player.getServer().getLevel(respawnDim),
                    respawnPos.getX(),respawnPos.getY(),respawnPos.getZ(),player.getYRot(),player.getXRot());
            str.sendCoolDownInfoMsg();
            return 1;
        }
    }

    private static int cb_home(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            str.sendCoolDownInfoMsg();
            return -1;
        }
        if (tag.hasKey(tag.HomePosAlias)) {
            tag.castOverload((float) Config.levelCostHome);
            Pair<Vec3,String> home = tag.getHome();
            ResourceLocation rl = LZCommonForgeApi.getDimensionResourceLocation(home.getRight());


            ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
            player.teleportTo(player.getServer().getLevel(mydim), home.getLeft().x,home.getLeft().y+1,home.getLeft().z,player.getYRot(),player.getXRot());

            str.sendCoolDownInfoMsg();
            return 1;
        } else {
            LZCommonForgeApi.sendSystemMessage(player,"你无家可归!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return -1;
        }
    }
}
