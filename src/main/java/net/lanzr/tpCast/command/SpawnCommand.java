package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;

public class SpawnCommand {
    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("spawn").executes(COMMAND_SPAWN));
    }

    private static final TpCommand COMMAND_SPAWN = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            MinecraftServer server = player.getServer();
            ServerLevel sl = server.getLevel(Level.OVERWORLD);

            int x = sl.getSharedSpawnPos().getX();
            int y = sl.getSharedSpawnPos().getY();
            int z = sl.getSharedSpawnPos().getZ();

            tpPlayer.tag.castOverload((float) Config.levelCostSPAWN);
            player.teleportTo(sl, x, y, z, player.getYRot(), player.getXRot());
            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };
}
