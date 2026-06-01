package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommonCommand {
    public static void register(RegisterCommandsEvent event) {


        event.getDispatcher().register(
                Commands.literal("self-check")
                        .executes(COMMAND_SELF_CHECK)
        );

        event.getDispatcher().register(
                Commands.literal("resetCoolDown")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(COMMAND_RESET_COOL_DOWN)
                                .requires(ctx-> ctx.hasPermission(4))
                        )
        );

        event.getDispatcher().register(
                Commands.literal("c-tp")
                        .then(Commands.argument("location", Vec2Argument.vec2())
                                .executes(COMMAND_CHUNK_TP)
                                .requires(ctx-> ctx.hasPermission(4))
                        )
        );
        event.getDispatcher().register(
                Commands.literal("suicide")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(COMMAND_SUICIDE)
                        )
        );
//         event.getDispatcher().register(
//                 Commands.literal("overload-tp")
//                     .then(Commands.argument("location", Vec3Argument.vec3())
//                     .executes(ctx -> cb_overloadTP(ctx.getSource().getPlayerOrException(),Vec3Argument.getVec3(ctx,"location")))
//                 )
//         );
    }

    private static final TpCommand COMMAND_SELF_CHECK = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            tpPlayer.sendCoolDownInfoMsg();
            long gt = LZCommonForgeApi.playerGetLevel(tpPlayer.player).getGameTime();
            int gLv = tpPlayer.tag.getCoolDownLevel(gt);
            if(gLv > tpPlayer.tag.MaxLevel) {
                long remain = tpPlayer.tag.getCoolDownStamp() - gt - tpPlayer.tag.CoolDownPiece * tpPlayer.tag.MaxLevel;
                remain = remain < 0 ? 0 : remain / 10;
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,String.format("熔断恢复倒计时 %d:%d ", remain/60, remain %60),LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return 0;
            }
            return 1;
        }
    };
    private static final TpCommand COMMAND_RESET_COOL_DOWN = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            tpPlayer.tag.setCoolDownStamp(ctx.getSource().getLevel().getGameTime());
            LZCommonForgeApi.sendSystemMessage(tpPlayer.player,String.format("%s SAMA清除了 %s 的过载", tpPlayer.player.getName().getString(),
                    EntityArgument.getPlayer(ctx,"target").getName().getString()),LZCommonForgeApi.MsgTypes.OTHER.getmFmt());
            return 0;
        }
    };

    private static final TpCommand COMMAND_CHUNK_TP = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            Vec2 pos = Vec2Argument.getVec2(ctx,"location");
            Vec2 tPos = new Vec2(pos.x * 16, pos.y * 16);
            tpPlayer.player.teleportTo(tPos.x,tpPlayer.player.getY(),tPos.y);
            return  1;
        }
    };
    private static final TpCommand COMMAND_SUICIDE = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            tpPlayer.player.kill();
            return 1;
        }
    };
    private static final TpCommand COMMAND_OVERLOAD_TP = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            Vec3 pos = Vec3Argument.getVec3(ctx,"location");
            tpPlayer.player.teleportTo(pos.x,pos.y,pos.z);
            tpPlayer.tag.castOverload(7);
            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };


}
