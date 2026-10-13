package net.lanzr.tpCast.teleport;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

/** Resolves destinations and performs player teleports for command modules. */
public final class TeleportService {
    private TeleportService() {
    }

    public static ServerLevel resolveLevel(MinecraftServer server, String dimensionId) {
        ResourceLocation id = ResourceLocation.tryParse(dimensionId);
        if (id == null) {
            return null;
        }
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
    }

    public static boolean teleport(ServerPlayer player, ServerLevel level, double x, double y, double z) {
        if (level == null) {
            return false;
        }
        player.teleportTo(level, x, y, z, player.getYRot(), player.getXRot());
        return true;
    }

    public static boolean teleportSaved(ServerPlayer player, Pair<Vec3, String> saved,
                                        double xOffset, double yOffset, double zOffset) {
        if (saved == null) {
            return false;
        }
        ServerLevel level = resolveLevel(player.getServer(), saved.getRight());
        Vec3 pos = saved.getLeft();
        return teleport(player, level, pos.x + xOffset, pos.y + yOffset, pos.z + zOffset);
    }
}
