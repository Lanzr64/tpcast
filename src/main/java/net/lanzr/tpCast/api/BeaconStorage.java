package net.lanzr.tpCast.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class BeaconStorage {
    private static final LevelResource BEACON_DIR = new LevelResource("tpcast_beacons");

    private static Path getBeaconFile(MinecraftServer server, UUID uuid) {
        return server.getWorldPath(BEACON_DIR).resolve(uuid + ".dat");
    }

    public static void saveBeacon(ServerPlayer player) {
        UUID uuid = player.getUUID();
        BlockPos pos = player.getOnPos();
        String dim = player.level().dimension().location().toString();

        CompoundTag tag = new CompoundTag();
        tag.putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        tag.putString("dim", dim);

        Path file = getBeaconFile(player.getServer(), uuid);
        try {
            file.getParent().toFile().mkdirs();
            NbtIo.writeCompressed(tag, file.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pair<Vec3, String> getBeacon(MinecraftServer server, UUID uuid) {
        Path file = getBeaconFile(server, uuid);
        if (!file.toFile().exists()) return null;

        try {
            CompoundTag tag = NbtIo.readCompressed(file.toFile());
            int[] pos = tag.getIntArray("pos");
            String dim = tag.getString("dim");
            return Pair.of(new Vec3(pos[0], pos[1], pos[2]), dim);
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean hasBeacon(ServerPlayer player) {
        Path file = getBeaconFile(player.getServer(), player.getUUID());
        return file.toFile().exists();
    }

    public static void removeBeacon(ServerPlayer player) {
        Path file = getBeaconFile(player.getServer(), player.getUUID());
        file.toFile().delete();
    }
}
