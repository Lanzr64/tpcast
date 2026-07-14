package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class OPCommand {
    public static void register(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event,
                Commands.literal("overload-tp")
                         .then(Commands.argument("location", Vec3Argument.vec3())
                            .executes(COMMAND_OVERLOAD_TP)
                         )
        );
    }

    private static final TpCommand COMMAND_OVERLOAD_TP = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            Vec3 pos = Vec3Argument.getVec3(ctx,"location");
            double distant = tpPlayer.player.distanceToSqr(pos.x,pos.y,pos.z);
            float cost = (float)(Config.LEVEL_COST_OVERLOAD_TP.get().floatValue() + distant/1000 * Config.INDEX_COST_DISTANT.get().floatValue());
            if (cost > 100) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,"距离太远，过载值将会超过100无法使用!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return 1;
            } else {
                tpPlayer.player.teleportTo(tpPlayer.player.serverLevel(), pos.x, pos.y, pos.z, tpPlayer.player.getYRot(), tpPlayer.player.getXRot());
                tpPlayer.tag.castOverload(cost);
                tpPlayer.sendCoolDownInfoMsg();
                return 1;
            }
        }
    };

}
