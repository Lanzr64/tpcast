package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

public class BackCommand {
    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("back").executes(ctx -> cb_back(ctx.getSource().getPlayerOrException()))
        );
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
            ResourceLocation rl = new ResourceLocation(home.getRight());
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
}
