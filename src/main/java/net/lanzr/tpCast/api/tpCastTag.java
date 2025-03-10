package net.lanzr.tpCast.api;

import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.tpCast;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import javax.print.attribute.standard.PrinterMakeAndModel;
import java.awt.desktop.PrintFilesEvent;
import java.text.CollationElementIterator;
import java.util.List;
import java.util.Set;

public class tpCastTag {
    private CompoundTag mTag;
    private ServerPlayer mPlayer;
    private CompoundTag mMarkList;
    final public int CoolDownPiece = Config.levelCoolDownPerLevel * 10; // 10 = 1s
    final public int MaxLevel = Config.maxLevel;
    final public long MaxCoolDown = MaxLevel *CoolDownPiece;
    final private double punishLevel = Config.levelPunishFuseBlow;
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

        // 不存在
        if(!pTag.contains(tpCast.MODID)) {
            pTag.put(tpCast.MODID,mTag);
        }

        // 不存在冷却时间 init
        if(!mTag.contains(CoolDownStampAlias)) {
            mTag.putLong(CoolDownStampAlias,LZCommonForgeApi.playerGetLevel(player).getGameTime());
        }

        if(mTag.contains("mark")) {
            mMarkList = mTag.getCompound("mark");
        }else {
            mTag.put("mark",mMarkList = new CompoundTag());
        }
    }
    public boolean castOverload(float level) {
        boolean ret = true;
        long st = mTag.getLong(CoolDownStampAlias);
        long gt = LZCommonForgeApi.playerGetLevel(mPlayer).getGameTime();
        int overLoad = (int)(CoolDownPiece* level);
        if(st - gt > MaxCoolDown-overLoad) {
            overLoad += (int)(CoolDownPiece * punishLevel);
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
        mTag.putString(HomeDimAlias,LZCommonForgeApi.playerGetLevel(mPlayer).dimension().location().toString());
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
        mTag.putString(BackDimAlias,LZCommonForgeApi.playerGetLevel(mPlayer).dimension().location().toString());
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

    public void setMark(String name) {
        BlockPos playerPos = mPlayer.getOnPos();
        CompoundTag mark = null;
        if (!mMarkList.contains(name)) {
            mMarkList.put(name,mark = new CompoundTag());
        } else {
            mark = mMarkList.getCompound(name);
        }
        mark.putIntArray("pos",new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
        mark.putString("dim", LZCommonForgeApi.playerGetLevel(mPlayer).dimension().location().toString());
    }
    public Set<String> getMarks() {
        Set<String> keys = mMarkList.getAllKeys();
        for (String key : keys) {
            System.out.println(key);
        }
        return keys;
    }
    public Pair<Vec3, String> getMark(String name) {
        // 不存在返回null
        if(!mMarkList.contains(name))
            return null;
        CompoundTag mark = mMarkList.getCompound(name);
        int[] pos =  mark.getIntArray("pos");
        Vec3 vec = new Vec3(pos[0],pos[1],pos[2]);
        String dim = mark.getString("dim");
        return Pair.of(vec, dim);
    }

    public int rmMark(String name) {
        if(mMarkList.contains(name)) {
            mMarkList.remove(name);
            return 1;
        }
        return 0;

    }

}
