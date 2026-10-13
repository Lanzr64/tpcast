package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.teleport.TeleportService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;

public class BackCommand {

    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("back").executes(BACK_COMMAND));
    }

    private static final TpCommand BACK_COMMAND = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            if (!tpPlayer.tag.hasKey(tpPlayer.tag.BackPosAlias)) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,"你还没死呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            }
            if (!TeleportService.teleportSaved(tpPlayer.player, tpPlayer.tag.getBack(), 0, 1, 0)) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,"无法找到死亡所在维度!",LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }
            tpPlayer.tag.castOverload(Config.LEVEL_COST_BACK.get().floatValue());
            tpPlayer.tag.rmKey(tpPlayer.tag.BackPosAlias);
            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };

}
