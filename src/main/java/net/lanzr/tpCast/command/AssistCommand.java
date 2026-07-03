package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;

public class AssistCommand {
    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("cast-assist")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(COMMAND_CAST_ASSIST)
                        ));
    }

    private static final TpCommand COMMAND_CAST_ASSIST = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "target");
            if(targetPlayer.getUUID() != tpPlayer.player.getUUID()) {
                tpPlayer.tag.castOverload((float) 2);

                tpCastTag targetTag = new tpCastTag(targetPlayer);
                TpCastPlayer targetPlayerWrapper = new TpCastPlayer(targetPlayer);

                targetTag.castOverload((float) -1.4);
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, String.format("%s 对你的祈福生效了！ ", targetPlayer.getName().getString()), LZCommonForgeApi.MsgTypes.OTHER.getmFmt());

                tpPlayer.sendCoolDownInfoMsg();
                targetPlayerWrapper.sendCoolDownInfoMsg();
            }
            return 1;
        }
    };

}
