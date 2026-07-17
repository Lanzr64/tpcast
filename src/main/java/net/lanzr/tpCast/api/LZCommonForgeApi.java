package net.lanzr.tpCast.api;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class LZCommonForgeApi {
    public static enum MsgTypes {
        NORMAL(ChatFormatting.YELLOW),
        ALERT(ChatFormatting.RED),
        OTHER(ChatFormatting.AQUA);
        public final ChatFormatting FORMAT;
        MsgTypes(ChatFormatting format) {
            this.FORMAT = format;
        }
        public ChatFormatting getmFmt() {
            return FORMAT;
        }
    }
    public static void sendSystemMessage(ServerPlayer player, String message, ChatFormatting format) {
        player.sendSystemMessage(Component.literal(message).withStyle(format), false);
    }
    public static void sendCenterSystemMessage(ServerPlayer player, String message, ChatFormatting format) {
        player.sendSystemMessage(Component.literal(message).withStyle(format), true);
    }
    public static ServerLevel playerGetLevel(ServerPlayer player) {
        return player.serverLevel();
    }

    public static ResourceLocation getDimensionResourceLocation(String name) {
        return ResourceLocation.parse(name);
    }

    /**
     * Teleport a player to a saved position. Returns true if teleported, false if aborted
     * due to abnormal coordinates (e.g. unconverted sable sublevel real position).
     */
    public static boolean PairParseTeleport(ServerPlayer player, Pair<Vec3,String> cp) {
        ResourceLocation rl = getDimensionResourceLocation(cp.getRight());
        ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION,rl);
        Vec3 pos = cp.getLeft();
        if (SableCompat.isSableLoaded() && SableCompat.isAbnormalCoord(BlockPos.containing(pos))) {
            sendSystemMessage(player, "目的地已迷失", MsgTypes.ALERT.getmFmt());
            return false;
        }
        player.teleportTo(player.getServer().getLevel(dim),
                pos.x, pos.y + 1, pos.z,
                java.util.Set.of(), player.getYRot(), player.getXRot());
        return true;
    }
}
