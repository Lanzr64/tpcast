package net.lanzr.tpCast.api;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
public class TpCastPlayer {
    public ServerPlayer player;
    public tpCastTag tag;
    public TpCastPlayer(ServerPlayer player) {
        this.player = player;
        this.tag = new tpCastTag(player);
    }

    public void sendCoolDownInfoMsg(){
        long gt = LZCommonForgeApi.playerGetLevel(player).getGameTime();
        StringBuffer str = new StringBuffer();
        int gLv = tag.getCoolDownLevel(gt);
        if(gLv > tag.MaxLevel) {
            str.append("XXX[");
            for (int i = 0 ;i < tag.MaxLevel;i++){
                str.append("/");
            }
            str.append("]XXX 已熔断 无法施术");
        } else {
            str.append("-[");
            for (int i = 0 ;i < tag.MaxLevel;i++){
                if(i < gLv) {
                    str.append("/");
                } else {
                    str.append("#");
                }
            }
            str.append("]-过载等级: "+gLv);
        }
        LZCommonForgeApi.sendCenterSystemMessage(player, str.toString(), ChatFormatting.WHITE);
    }
}
