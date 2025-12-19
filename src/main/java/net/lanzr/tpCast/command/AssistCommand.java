package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;

public class AssistCommand {
    public static void register(RegisterCommandsEvent event) {
        if (!Config.enableAssist) {
            return;
        }
        event.getDispatcher().register(
                Commands.literal("cast-assist")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> cb_castAssist(ctx.getSource().getPlayerOrException(),EntityArgument.getPlayer(ctx,"target"))))
        );
    }

    private static int cb_castAssist(ServerPlayer player, ServerPlayer targetPlayer) {
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

}
