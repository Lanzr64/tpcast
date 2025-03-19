package net.lanzr.tpCast.command;

import net.lanzr.tpCast.player.CastPlayer;
import net.lanzr.tpCast.player.CastPlayerList;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;

public class HOMECommand {
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("home").executes(ctx -> cb_returnBED(ctx.getSource().getPlayerOrException()))
        );
    }

    private static int cb_returnBED(ServerPlayer player) {
        CastPlayer commandPlayer = CastPlayerList.get(player.getUUID());
        commandPlayer.returnBed();
        return 1;
    }
}
