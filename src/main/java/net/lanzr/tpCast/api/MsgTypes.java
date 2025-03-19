package net.lanzr.tpCast.api;

import net.minecraft.ChatFormatting;

public enum MsgTypes {
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
