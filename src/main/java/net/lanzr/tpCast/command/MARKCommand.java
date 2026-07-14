package net.lanzr.tpCast.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.command.tools.CommandTools;import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MARKCommand {
    public static void register(RegisterCommandsEvent event) {

        final LiteralArgumentBuilder<CommandSourceStack> literalargumentBuilder =
                Commands.literal("mark")
                        .executes(COMMAND_MARK_INFO);

        literalargumentBuilder
                .then(Commands.literal("set")
                        .then(Commands.argument("name", StringArgumentType.string())
                        .executes(COMMAND_SET_MARK)));

        RequiredArgumentBuilder<CommandSourceStack, String> marks_args = Commands.argument("marks", StringArgumentType.string());
        marks_args.suggests((ctx, builder) -> suggestMarks(ctx, builder));

        literalargumentBuilder
                .then(Commands.literal("go")
                                .then(marks_args.executes(COMMAND_GO_MARK)));

        literalargumentBuilder
                .then(Commands.literal("delete")
                        .then(marks_args.executes(COMMAND_RM_MARK)));

        CommandTools.registerWithPrefix(event, literalargumentBuilder);
    }

    /**
     * mark info命令：显示标记列表，需要冷却检查（因为消耗冷却）
     */
    private static final TpCommand COMMAND_MARK_INFO = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            Set<String> marks = tpPlayer.tag.getMarks();
            String marksStr = "标记: " + marks.toString();

            double CostLevel = Config.LEVEL_COST_MARK_BASE.get() + marks.size() * Config.LEVEL_COST_MARK_ADD.get();
            tpPlayer.tag.castOverload((float) CostLevel);
            String costStr = String.format(" 当前消耗:%.2f", CostLevel);
            LZCommonForgeApi.sendSystemMessage(tpPlayer.player, marksStr + costStr, LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());

            return 0;
        }
    };

    /**
     * mark set命令：设置标记，无需冷却检查
     */
    private static final TpCommand COMMAND_SET_MARK = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            String name = StringArgumentType.getString(ctx, "name");
            LZCommonForgeApi.sendSystemMessage(tpPlayer.player, String.format("添加标记 %s", name), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            tpPlayer.tag.setMark(name);
            return 0;
        }
    };

    /**
     * mark go命令：传送到标记点，需要冷却检查
     */
    private static final TpCommand COMMAND_GO_MARK = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            String name = StringArgumentType.getString(ctx, "marks");
            Pair<Vec3, String> mark = tpPlayer.tag.getMark(name);
            if (mark == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, String.format("无法找到标记 %s", name), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            } else {
                double CostLevel = Config.LEVEL_COST_MARK_BASE.get() + tpPlayer.tag.getMarks().size() * Config.LEVEL_COST_MARK_ADD.get();
                tpPlayer.tag.castOverload((float) CostLevel);

                LZCommonForgeApi.PairParseTeleport(tpPlayer.player, mark);

                tpPlayer.sendCoolDownInfoMsg();
                return 0;
            }
        }
    };

    /**
     * mark delete命令：删除标记，无需冷却检查
     */
    private static final TpCommand COMMAND_RM_MARK = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            String name = StringArgumentType.getString(ctx, "marks");
            tpPlayer.tag.rmMark(name);
            LZCommonForgeApi.sendSystemMessage(tpPlayer.player, String.format("删除标记 %s", name), LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return 0;
        }
    };

    /**
     * 创建标记建议列表
     */
    private static CompletableFuture<Suggestions> suggestMarks(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        tpCastTag tag = new tpCastTag(player);
        Set<String> playerNames = tag.getMarks();
        for (String name : playerNames) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }
}
