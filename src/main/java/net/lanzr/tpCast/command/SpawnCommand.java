package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;

public class SpawnCommand {
    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("spawn").executes(ctx -> cb_spawn(ctx.getSource().getPlayerOrException()))
        );
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
}

