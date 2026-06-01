package net.lanzr.tpCast.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;




/**
 * 基础传送命令类，引入命令模式以减少重复的冷却判断代码。
 */
public abstract class TpCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!(context.getSource().getEntity() instanceof ServerPlayer)) {
            return 0;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        TpCastPlayer tpc = new TpCastPlayer(player);

        if (requiresCooldownCheck()) {
            boolean castAble = (tpc.tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tpc.tag.MaxLevel);
            if (!castAble) {
                // 无法使用，直接发送冷却信息并返回 -1
                tpc.sendCoolDownInfoMsg();


                return -1;
            }
        }

        // 调用子类的具体执行逻辑
        return execute(context, tpc);
    }

    /**
     * 是否需要冷却检查，默认需要检查。
     * 不需要检查的命令可以重写此方法并返回 false。
     */
    protected boolean requiresCooldownCheck() {
        return true;
    }

    /**
     * 子类需实现的具体命令逻辑
     */
    protected abstract int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException;
}
