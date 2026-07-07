package net.lanzr.tpCast.api;

import net.lanzr.tpCast.config.Config;
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
    final public long CoolDownPiece = Config.levelCoolDownPerLevel * 1000L; // ms
    final public int MaxLevel = Config.maxLevel;
    final public long MaxCoolDown = MaxLevel * CoolDownPiece;
    final private double punishLevel = Config.levelPunishFuseBlow;
    public final String HomePosAlias = "homePos";
    public final String HomeDimAlias = "homeDim";
    public final String BackPosAlias = "backPos";
    public final String BackDimAlias = "backDim";
    public final String BeaconPosAlias = "beaconPos";
    public final String BeaconDimAlias = "beaconDim";
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
        mTag.putIntArray(posKey, new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
        mTag.putString(dimKey, LZCommonForgeApi.playerGetLevel(mPlayer).dimension().location().toString());
    }

    /**
     * 设置指定坐标到位置键
     * @param posKey 位置坐标键名
     * @param dimKey 维度键名
     * @param pos 坐标
     * @param dim 维度
     */
    private void setPosition(String posKey, String dimKey, Vec3 pos, String dim) {
        mTag.putIntArray(posKey, new int[]{(int) pos.x, (int) pos.y, (int) pos.z});
        mTag.putString(dimKey, dim);
    }

    /**
     * 获取指定位置键的坐标和维度
     * @param posKey 位置坐标键名
     * @param dimKey 维度键名
     * @return 坐标和维度的Pair，如果不存在返回null
     */
    private Pair<Vec3, String> getPosition(String posKey, String dimKey) {
        if (!mTag.contains(posKey) || !mTag.contains(dimKey)) {
            return null;
        }
        int[] pos = mTag.getIntArray(posKey);
        Vec3 vec = new Vec3(pos[0], pos[1], pos[2]);
        String dim = mTag.getString(dimKey);
        return Pair.of(vec, dim);
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

    // ----------- beacon -----------

    public void setBeacon() {
        setCurrentPosition(BeaconPosAlias, BeaconDimAlias);
    }

    public Pair<Vec3, String> getBeacon() {
        return getPosition(BeaconPosAlias, BeaconDimAlias);
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
        mark.putIntArray("pos", new int[]{playerPos.getX(), playerPos.getY(), playerPos.getZ()});
        mark.putString("dim", LZCommonForgeApi.playerGetLevel(mPlayer).dimension().location().toString());
    }

    public Set<String> getMarks() {
        return mMarkList.getAllKeys();
    }

    public Pair<Vec3, String> getMark(String name) {
        // 不存在返回null
        if (!mMarkList.contains(name))
            return null;
        CompoundTag mark = mMarkList.getCompound(name);
        int[] pos = mark.getIntArray("pos");
        Vec3 vec = new Vec3(pos[0], pos[1], pos[2]);
        String dim = mark.getString("dim");
        return Pair.of(vec, dim);
    }

    public void rmMark(String name) {
        if (mMarkList.contains(name)) {
            mMarkList.remove(name);
        }
    }
}
