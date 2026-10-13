package net.lanzr.tpCast.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** Finds a safe standing block near a saved death position. */
public final class SafePositionFinder {
    private static final int[][] HORIZONTAL_OFFSETS = {
            {0, 0},
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    private SafePositionFinder() {
    }

    public static BlockPos find(ServerLevel level, BlockPos origin) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
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

        if (ground.getCollisionShape(level, groundPos).isEmpty()) return false;
        if (!ground.getFluidState().isEmpty()) return false;
        if (!feet.getCollisionShape(level, groundPos.above()).isEmpty()) return false;
        if (!feet.getFluidState().isEmpty()) return false;
        if (!head.getCollisionShape(level, groundPos.above(2)).isEmpty()) return false;
        return head.getFluidState().isEmpty();
    }
}
