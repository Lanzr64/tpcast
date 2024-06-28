package net.lanzr.tpCast.api;

import net.lanzr.tpCast.tpCast;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.desktop.PrintFilesEvent;
import java.text.CollationElementIterator;

public class tpCastTag {
    private CompoundTag mTag;
    private ServerPlayer mPlayer;
    final public int CoolDownPiece = 6000; // 10 = 1s
    final public int MaxLevel = 5;
    final public long MaxCoolDown = MaxLevel *CoolDownPiece;

    public String
            HomePosAlias ="homePos",
            HomeDimAlias ="homeDim",
            BackPosAlias ="backPos",
            BackDimAlias ="backDim",
            CoolDownStampAlias="stamp";
    public tpCastTag(ServerPlayer player) {
        CompoundTag pTag = player.getPersistentData();
        mTag  = pTag.getCompound(tpCast.MODID);
        mPlayer = player;
        if(!pTag.contains(tpCast.MODID)) {
            pTag.put(tpCast.MODID,mTag);
        }
        // 不存在冷却时间 init
        if(!mTag.contains(CoolDownStampAlias)) {
            mTag.putLong(CoolDownStampAlias,player.getLevel().getGameTime());
        }

    }
    public boolean castOverload(float level) {
        boolean ret = true;
        long st = mTag.getLong(CoolDownStampAlias);
        long gt = mPlayer.getLevel().getGameTime();
        int overLoad = (int)(CoolDownPiece* level);
        if(st - gt > MaxCoolDown-overLoad) {
            overLoad += (int)(CoolDownPiece * level);
            ret = false;
        }
        setCoolDownStamp(st+overLoad);
        return ret;
    }

    public boolean hasKey(String str){
        return mTag.contains(str);
    }

    public void rmKey(String str) {
        mTag.remove(str);
    }

    public void setCoolDownStamp(long stamp) {
        mTag.putLong(CoolDownStampAlias,stamp);
    }
    public long getCoolDownStamp() {
        return mTag.getLong(CoolDownStampAlias);
    }
    public int getCoolDownLevel(long gt) {
        long gap = mTag.getLong(CoolDownStampAlias) - gt;
        int lv = (int)(gap / CoolDownPiece);
        lv = Math.max(lv,0);
        if(gap < 0 ) {
            setCoolDownStamp(gt);
        }
        return lv;
    }
    // -----------home / back
    public void setHome() {
        BlockPos playerPos = mPlayer.getOnPos();
        mTag.putIntArray(HomePosAlias,new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
        mTag.putString(HomeDimAlias,mPlayer.getLevel().dimension().location().toString());
    }
    public void setHome(Vec3 pos, String dim) {
        mTag.putIntArray(HomePosAlias,new int[]{(int)pos.x,(int)pos.y,(int)pos.z});
        mTag.putString(HomeDimAlias, dim);
    }
    public Pair<Vec3, String> getHome() {
        int[] pos =  mTag.getIntArray(HomePosAlias);
        Vec3 vec = new Vec3(pos[0],pos[1],pos[2]);
        String dim = mTag.getString(HomeDimAlias);
        return Pair.of(vec, dim);
    }

    public void setBack() {
        BlockPos playerPos = mPlayer.getOnPos();
        mTag.putIntArray(BackPosAlias,new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
        mTag.putString(BackDimAlias,mPlayer.getLevel().dimension().location().toString());
    }

    public void setBack(Vec3 pos, String dim) {
        mTag.putIntArray(BackPosAlias,new int[]{(int)pos.x,(int)pos.y,(int)pos.z});
        mTag.putString(BackDimAlias, dim);
    }

    public Pair<Vec3, String> getBack() {
        int[] pos =  mTag.getIntArray(BackPosAlias);
        Vec3 vec = new Vec3(pos[0],pos[1],pos[2]);
        String dim = mTag.getString(BackDimAlias);
        return Pair.of(vec, dim);
    }

}
