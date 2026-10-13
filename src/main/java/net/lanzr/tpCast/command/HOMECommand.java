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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;

public class HOMECommand {

    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("home")
                        .executes(COMMAND_RETURN_BED));
    }

    private static final TpCommand COMMAND_RETURN_BED = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            BlockPos respawnPos = tpPlayer.player.getRespawnPosition();
            ResourceKey<Level> respawnDim = tpPlayer.player.getRespawnDimension();
            if(respawnPos == null || respawnDim == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,"你还没有睡觉呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            } else {
                if (!TeleportService.teleport(tpPlayer.player, tpPlayer.player.getServer().getLevel(respawnDim),
                        respawnPos.getX(), respawnPos.getY(), respawnPos.getZ())) {
                    LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "无法找到重生点所在维度!", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                    return -1;
                }
                tpPlayer.tag.castOverload(Config.LEVEL_COST_HOME.get().floatValue());
                tpPlayer.sendCoolDownInfoMsg();
                return 1;
            }
        }
    };
}
