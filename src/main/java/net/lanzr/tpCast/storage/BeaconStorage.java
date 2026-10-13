package net.lanzr.tpCast.storage;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class BeaconStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LevelResource BEACON_DIR = new LevelResource("tpcast_beacons");

    private static Path getBeaconFile(MinecraftServer server, UUID uuid) {
        return server.getWorldPath(BEACON_DIR).resolve(uuid + ".dat");
    }

    public static boolean saveBeacon(ServerPlayer player) {
        UUID uuid = player.getUUID();
        BlockPos pos = player.getOnPos();
        String dim = player.level().dimension().location().toString();

        CompoundTag tag = new CompoundTag();
        LocationCodec.write(tag, "pos", "dim", pos, dim);

        Path file = getBeaconFile(player.getServer(), uuid);
        Path temp = null;
        try {
            Files.createDirectories(file.getParent());
            temp = Files.createTempFile(file.getParent(), uuid + "-", ".tmp");
            NbtIo.writeCompressed(tag, temp.toFile());
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | SecurityException e) {
            LOGGER.error("Failed to save beacon for {}", uuid, e);
            return false;
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException e) {
                    LOGGER.warn("Failed to delete temporary beacon file for {}", uuid, e);
                }
            }
        }
    }

    public static Pair<Vec3, String> getBeacon(MinecraftServer server, UUID uuid) {
        Path file = getBeaconFile(server, uuid);
        if (!file.toFile().exists()) return null;

        try {
            CompoundTag tag = NbtIo.readCompressed(file.toFile());
            return LocationCodec.read(tag, "pos", "dim");
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean hasBeacon(ServerPlayer player) {
        Path file = getBeaconFile(player.getServer(), player.getUUID());
        return file.toFile().exists();
    }

    public static boolean removeBeacon(ServerPlayer player) {
        Path file = getBeaconFile(player.getServer(), player.getUUID());
        try {
            return Files.deleteIfExists(file);
        } catch (IOException | SecurityException e) {
            LOGGER.error("Failed to remove beacon for {}", player.getUUID(), e);
            return false;
        }
    }
}
