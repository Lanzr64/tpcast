package net.lanzr.tpCast.api;

import net.lanzr.tpCast.tpCast;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.apache.commons.lang3.tuple.Pair;

public class tpCastTag {
    private CompoundTag mTag;
    private ServerPlayer mPlayer;
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
