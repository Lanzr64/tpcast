package net.lanzr.tpCast.api;

import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.storage.LocationCodec;
import net.lanzr.tpCast.tpCast;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class tpCastTag {
    private final CompoundTag mTag;
    private final ServerPlayer mPlayer;
    private final CompoundTag mMarkList;
    final public long CoolDownPiece = Config.LEVEL_COOLDOWN_PERLEVEL.get() * 1000L; // ms
    final public int MaxLevel = Config.MAX_LEVEL.get();
    final public long MaxCoolDown = MaxLevel * CoolDownPiece;
    final private double punishLevel = Config.LEVEL_PUNISH_FUSE_BLOW.get();
    public final String HomePosAlias = "homePos";
    public final String HomeDimAlias = "homeDim";
    public final String BackPosAlias = "backPos";
    public final String BackDimAlias = "backDim";
    public final String CoolDownStampAlias = "stamp";

    public tpCastTag(ServerPlayer player) {
        CompoundTag pTag = player.getPersistentData();
        mTag = pTag.getCompound(tpCast.MODID);
        mPlayer = player;
        if (!pTag.contains(tpCast.MODID)) {
            pTag.put(tpCast.MODID, mTag);
        }
        // 不存在冷却时间 init
        if (!mTag.contains(CoolDownStampAlias)) {
            mTag.putLong(CoolDownStampAlias, System.currentTimeMillis());
        }

        if (mTag.contains("mark")) {
            mMarkList = mTag.getCompound("mark");
        } else {
            mTag.put("mark", mMarkList = new CompoundTag());
        }
    }

    public void castOverload(float level) {
        boolean ret = true;
        long st = mTag.getLong(CoolDownStampAlias);
        long now = System.currentTimeMillis();
        long overLoad = (long) (CoolDownPiece * level);
        if ((st - now > MaxCoolDown - overLoad) && level >= 0) {
            overLoad += (long) (CoolDownPiece * punishLevel);
            ret = false;
        }
        setCoolDownStamp(st + overLoad);
    }

    public boolean hasKey(String str) {
        return mTag.contains(str);
    }

    public void rmKey(String str) {
        mTag.remove(str);
    }

    public void setCoolDownStamp(long stamp) {
        mTag.putLong(CoolDownStampAlias, stamp);
    }

    public long getCoolDownStamp() {
        return mTag.getLong(CoolDownStampAlias);
    }

    public int getCoolDownLevel(long gt) {
        long gap = mTag.getLong(CoolDownStampAlias) - gt;
        int lv = (int) (gap / CoolDownPiece);
        lv = Math.max(lv, 0);
        if (gap < 0) {
            setCoolDownStamp(gt);
        }
        return lv;
    }

    // ----------- 通用位置存储方法 -----------

    /**
     * 设置玩家当前位置到指定的位置键
     * @param posKey 位置坐标键名
     * @param dimKey 维度键名
     */
    private void setCurrentPosition(String posKey, String dimKey) {
        BlockPos playerPos = mPlayer.getOnPos();
        LocationCodec.write(mTag, posKey, dimKey, playerPos,
                mPlayer.serverLevel().dimension().location().toString());
    }

    /**
     * 设置指定坐标到位置键
     * @param posKey 位置坐标键名
     * @param dimKey 维度键名
     * @param pos 坐标
     * @param dim 维度
     */
    private void setPosition(String posKey, String dimKey, Vec3 pos, String dim) {
        LocationCodec.write(mTag, posKey, dimKey,
                new BlockPos((int) pos.x, (int) pos.y, (int) pos.z), dim);
    }

    /**
     * 获取指定位置键的坐标和维度
     * @param posKey 位置坐标键名
     * @param dimKey 维度键名
     * @return 坐标和维度的Pair，如果不存在返回null
     */
    private Pair<Vec3, String> getPosition(String posKey, String dimKey) {
        return LocationCodec.read(mTag, posKey, dimKey);
    }

    // ----------- home -----------

    public void setHome() {
        setCurrentPosition(HomePosAlias, HomeDimAlias);
    }

    public void setHome(Vec3 pos, String dim) {
        setPosition(HomePosAlias, HomeDimAlias, pos, dim);
    }

    public Pair<Vec3, String> getHome() {
        return getPosition(HomePosAlias, HomeDimAlias);
    }

    // ----------- back -----------

    public void setBack() {
        setCurrentPosition(BackPosAlias, BackDimAlias);
    }

    public void setBack(Vec3 pos, String dim) {
        setPosition(BackPosAlias, BackDimAlias, pos, dim);
    }

    public Pair<Vec3, String> getBack() {
        return getPosition(BackPosAlias, BackDimAlias);
    }

    // ----------- mark -----------

    public void setMark(String name) {
        BlockPos playerPos = mPlayer.getOnPos();
        CompoundTag mark;
        if (!mMarkList.contains(name)) {
            mMarkList.put(name, mark = new CompoundTag());
        } else {
            mark = mMarkList.getCompound(name);
        }
        LocationCodec.write(mark, "pos", "dim", playerPos,
                mPlayer.serverLevel().dimension().location().toString());
    }

    public Set<String> getMarks() {
        return mMarkList.getAllKeys();
    }

    public Pair<Vec3, String> getMark(String name) {
        // 不存在返回null
        if (!mMarkList.contains(name))
            return null;
        CompoundTag mark = mMarkList.getCompound(name);
        return LocationCodec.read(mark, "pos", "dim");
    }

    public void rmMark(String name) {
        if (mMarkList.contains(name)) {
            mMarkList.remove(name);
        }
    }
}
