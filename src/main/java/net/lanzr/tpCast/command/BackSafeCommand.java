package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.teleport.SafePositionFinder;
import net.lanzr.tpCast.teleport.TeleportService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

public class BackSafeCommand {

    public static void register(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event,
                Commands.literal("back-safe").executes(BACK_SAFE_COMMAND));
    }

    private static final TpCommand BACK_SAFE_COMMAND = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            if (!tpPlayer.tag.hasKey(tpPlayer.tag.BackPosAlias)) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "你还没死呢!", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            }

            Pair<Vec3, String> back = tpPlayer.tag.getBack();
            if (back == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "死亡位置记录无效!", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }
            ServerLevel targetLevel = TeleportService.resolveLevel(tpPlayer.player.getServer(), back.getRight());

            if (targetLevel == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "无法找到死亡所在维度!", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }

            BlockPos deathPos = new BlockPos((int) back.getLeft().x, (int) back.getLeft().y, (int) back.getLeft().z);
            BlockPos safePos = SafePositionFinder.find(targetLevel, deathPos);

            if (safePos == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,
                        "死亡点附近找不到安全位置",
                        LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }

            TeleportService.teleport(tpPlayer.player, targetLevel,
                    safePos.getX() + 0.5, safePos.getY() + 1, safePos.getZ() + 0.5);
            tpPlayer.tag.castOverload(Config.LEVEL_COST_BACK.get().floatValue());
            tpPlayer.tag.rmKey(tpPlayer.tag.BackPosAlias);
            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };

}
