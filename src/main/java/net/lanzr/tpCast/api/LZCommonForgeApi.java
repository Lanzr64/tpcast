package net.lanzr.tpCast.api;

import net.minecraft.ChatFormatting;
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
        return new ResourceLocation(name);
    }

    public static void PairParseTeleport(ServerPlayer player, Pair<Vec3,String> cp) {
        ResourceLocation rl = getDimensionResourceLocation(cp.getRight());
        ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION,rl);
        player.teleportTo(player.getServer().getLevel(dim), cp.getLeft().x,cp.getLeft().y+1,cp.getLeft().z,player.getYRot(),player.getXRot());
    }
}
