package net.lanzr.tpCast.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MARKCommand {
    public static void register(RegisterCommandsEvent event) {

        final LiteralArgumentBuilder<CommandSourceStack> literalargumentBuilder =
                Commands.literal("mark")
                        .executes(ctx -> cb_markInfo(ctx.getSource().getPlayerOrException()));

        literalargumentBuilder
                .then(Commands.literal("set")
                        .then(Commands.argument("name", StringArgumentType.string())
                        .executes(ctx -> cb_setMark(ctx.getSource().getPlayerOrException(),StringArgumentType.getString(ctx,"name")))));
        RequiredArgumentBuilder<CommandSourceStack, String> marks_args = Commands.argument("marks", StringArgumentType.string());
        marks_args.suggests((ctx, builder) -> suggestMarks(ctx, builder));

        literalargumentBuilder
                .then(Commands.literal("go")
                                .then(marks_args.executes(ctx -> cb_goMark(ctx.getSource().getPlayerOrException(),StringArgumentType.getString(ctx,"marks")))));

        literalargumentBuilder
                .then(Commands.literal("delete")
                        .then(marks_args.executes(ctx -> cb_rmMark(ctx.getSource().getPlayerOrException(),StringArgumentType.getString(ctx,"marks")))));

//        literalargumentBuilder
//                .then(Commands.literal("tst")
//                        .then(Commands.argument("name", StringArgumentType.string())
//                        .executes(ctx -> cb_goMark(ctx.getSource().getPlayerOrException(),StringArgumentType.getString(ctx,"name")))));
        event.getDispatcher().register(literalargumentBuilder);
    }
    private static int cb_markInfo(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        Set<String> marks = tag.getMarks();
        String marksStr = "标记: " + marks.toString();

        double CostLevel = Config.levelCostMarkBase + marks.size() * Config.levelCostMarkAdd;
        tag.castOverload((float) CostLevel);
        String costStr = String.format(" 当前消耗:%.2f",CostLevel);
        LZCommonForgeApi.sendSystemMessage(player,marksStr + costStr,LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());

        return 0;
    }
    private static int cb_setMark(ServerPlayer player, String name) {
        tpCastTag tag = new tpCastTag(player);
        LZCommonForgeApi.sendSystemMessage(player,String.format("添加标记 %s",name),LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        tag.setMark(name);
        return 0;
    }
    private static int cb_goMark(ServerPlayer player, String name) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            // 无法使用
            str.sendCoolDownInfoMsg();
            return -1;
        }

        Pair<Vec3, String> mark = tag.getMark(name);
        if(mark == null) {
            LZCommonForgeApi.sendSystemMessage(player,String.format("无法找到标记 %s",name),LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return -1;
        } else {
            double CostLevel = Config.levelCostMarkBase + tag.getMarks().size() * Config.levelCostMarkAdd;
            tag.castOverload((float) CostLevel);

            LZCommonForgeApi.PairParseTeleport(player,tag.getMark(name));

            str.sendCoolDownInfoMsg();
            return 0;
        }

    }
    private static int cb_rmMark(ServerPlayer player, String name) {
        tpCastTag tag = new tpCastTag(player);
        tag.rmMark(name);
        LZCommonForgeApi.sendSystemMessage(player,String.format("删除标记 %s",name),LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
        return 0;
    }
    // 创建建议目标
    private static CompletableFuture<Suggestions> suggestMarks(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // 获取所有玩家名称 (替换为你自己的建议逻辑)
        ServerPlayer player = context.getSource().getPlayerOrException();
        // 添加建议
        tpCastTag tag = new tpCastTag(player);
        Set<String> playerNames = tag.getMarks();
        for (String name : playerNames) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

}
