package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

public class BackSafeCommand {

    // 3×3 水平偏移，按距中心远近排序：先中心，再十字，再对角
    private static final int[][] HORIZONTAL_OFFSETS = {
            {0, 0},
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

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
            ResourceLocation rl = new ResourceLocation(back.getRight());
            ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, rl);
            ServerLevel targetLevel = tpPlayer.player.getServer().getLevel(dim);

            if (targetLevel == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "无法找到死亡所在维度!", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }

            BlockPos deathPos = new BlockPos((int) back.getLeft().x, (int) back.getLeft().y, (int) back.getLeft().z);
            BlockPos safePos = findSafePosition(targetLevel, deathPos);

            if (safePos == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,
                        "死亡点附近找不到安全位置",
                        LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }

            tpPlayer.player.teleportTo(targetLevel,
                    safePos.getX() + 0.5, safePos.getY() + 1, safePos.getZ() + 0.5,
                    tpPlayer.player.getYRot(), tpPlayer.player.getXRot());
            tpPlayer.tag.castOverload(Config.LEVEL_COST_BACK.get().floatValue());
            tpPlayer.tag.rmKey(tpPlayer.tag.BackPosAlias);
            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };

    private static BlockPos findSafePosition(ServerLevel level, BlockPos origin) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        // 半个世界高度作为扫描范围，双向即覆盖整列
        int halfRange = (maxY - minY) / 2;
        int ox = origin.getX();
        int oz = origin.getZ();
        int oy = origin.getY();

        for (int dy = 0; dy <= halfRange; dy++) {
            int upY = oy + dy;
            if (upY >= minY && upY <= maxY - 2) {
                BlockPos found = checkHorizontalLayer(level, ox, upY, oz);
                if (found != null) return found;
            }
            if (dy > 0) {
                int downY = oy - dy;
                if (downY >= minY && downY <= maxY - 2) {
                    BlockPos found = checkHorizontalLayer(level, ox, downY, oz);
                    if (found != null) return found;
                }
            }
        }
        return null;
    }

    /**
     * 检查某一 Y 层的 3×3 水平范围内是否有安全站立位置。
     * 按 HORIZONTAL_OFFSETS 顺序（中心→十字→对角）遍历。
     */
    private static BlockPos checkHorizontalLayer(ServerLevel level, int x, int y, int z) {
        for (int[] offset : HORIZONTAL_OFFSETS) {
            BlockPos candidate = new BlockPos(x + offset[0], y, z + offset[1]);
            if (isSafeStand(level, candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean isSafeStand(ServerLevel level, BlockPos groundPos) {
        BlockState ground = level.getBlockState(groundPos);
        BlockState feet = level.getBlockState(groundPos.above());
        BlockState head = level.getBlockState(groundPos.above(2));

        // 脚下必须有碰撞箱且非流体
        if (ground.getCollisionShape(level, groundPos).isEmpty()) return false;
        if (!ground.getFluidState().isEmpty()) return false;

        // 脚位必须可穿过且非流体
        if (!feet.getCollisionShape(level, groundPos.above()).isEmpty()) return false;
        if (!feet.getFluidState().isEmpty()) return false;

        // 头位必须可穿过且非流体
        if (!head.getCollisionShape(level, groundPos.above(2)).isEmpty()) return false;
        if (!head.getFluidState().isEmpty()) return false;

        return true;
    }
}
