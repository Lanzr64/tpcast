package net.lanzr.tpCast.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;




public abstract class TpCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!(context.getSource().getEntity() instanceof ServerPlayer)) {
            return 0;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        TpCastPlayer tpc = new TpCastPlayer(player);
//        需要检查cd
        if (requiresCooldownCheck()) {
            boolean castAble = (tpc.tag.getCoolDownLevel(System.currentTimeMillis()) <= tpc.tag.MaxLevel);
            if (!castAble) {
                // 无法使用，直接发送cd信息并返回 -1
                tpc.sendCoolDownInfoMsg();

                return -1;
            }
        }

        // 调用子类的具体执行逻辑
        return execute(context, tpc);
    }

    protected boolean requiresCooldownCheck() {
        return true;
    }

    protected abstract int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException;
}
