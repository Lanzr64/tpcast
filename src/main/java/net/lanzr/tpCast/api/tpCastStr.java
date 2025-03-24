package net.lanzr.tpCast.api;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;

public class tpCastStr {
    final ServerPlayer mPlayer;
    public tpCastStr(ServerPlayer player) {
        this.mPlayer = player;
    }
    private String genCoolDownStr() {
        boolean circusBreak = false;
        long gt = LZCommonForgeApi.playerGetLevel(mPlayer).getGameTime();
        tpCastTag tag = new tpCastTag(mPlayer);
        StringBuffer str = new StringBuffer();
        int gLv = tag.getCoolDownLevel(gt);
        if(gLv > tag.MaxLevel) {
            circusBreak = true;
            str.append("XXX[");
            for (int i = 0 ;i < 10;i++){
                str.append("/");
            }
            str.append("]XXX\n已熔断 无法施术");
        } else {
            str.append("-[");
            for (int i = 0 ;i < 10;i++){
                if(i < gLv) {
                    str.append("/");
                } else {
                    str.append(" ");
                }
            }
            str.append("]-过载等级: "+gLv);
        }

//        player.sendSystemMessage(Component.literal("gLv is "+ gLv),true);
        return str.toString();
    }
    public void sendCoolDownInfoMsg(){
        boolean ret = false;
        long gt = LZCommonForgeApi.playerGetLevel(mPlayer).getGameTime();
        tpCastTag tag = new tpCastTag(mPlayer);
        StringBuffer str = new StringBuffer();
        int gLv = tag.getCoolDownLevel(gt);
        if(gLv > tag.MaxLevel) {
            str.append("XXX[");
            for (int i = 0 ;i < tag.MaxLevel;i++){
                str.append("/");
            }
            str.append("]XXX 已熔断 无法施术");
        } else {
            ret = true;
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
        LZCommonForgeApi.sendCenterSystemMessage(mPlayer,str.toString(),ChatFormatting.WHITE);
    }
}
