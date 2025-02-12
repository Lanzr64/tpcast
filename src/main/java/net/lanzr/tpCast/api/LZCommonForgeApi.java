package net.lanzr.tpCast.api;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.swing.*;

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
}
