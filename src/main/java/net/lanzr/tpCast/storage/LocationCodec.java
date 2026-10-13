package net.lanzr.tpCast.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

/** Shared NBT representation for saved coordinates and their dimension. */
public final class LocationCodec {
    private LocationCodec() {
    }

    public static void write(CompoundTag tag, String positionKey, String dimensionKey,
                             BlockPos position, String dimension) {
        tag.putIntArray(positionKey, new int[]{position.getX(), position.getY(), position.getZ()});
        tag.putString(dimensionKey, dimension);
    }

    public static Pair<Vec3, String> read(CompoundTag tag, String positionKey, String dimensionKey) {
        if (!tag.contains(positionKey, Tag.TAG_INT_ARRAY) || !tag.contains(dimensionKey, Tag.TAG_STRING)) {
            return null;
        }
        int[] position = tag.getIntArray(positionKey);
        String dimension = tag.getString(dimensionKey);
        if (position.length != 3 || dimension.isBlank()) {
            return null;
        }
        return Pair.of(new Vec3(position[0], position[1], position[2]), dimension);
    }
}
