package net.lanzr.tpCast.player;

import net.lanzr.tpCast.api.*;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.data.ICastAble;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.IntSupplier;

public class CastPlayer implements ICastAble {
    private tpCastTag mTag = null;
    ServerPlayer mPlayer = null;

    public CastPlayer(ServerPlayer player) {
        this.mPlayer = player;
        this.mTag = new tpCastTag(player);
    }
    public boolean checkCastAble() {
        boolean ret = false;
        int gLv = getOverloadLevel();
        if (gLv >= 0) {
            ret = true;
        }
        return ret;
    }
    public boolean checkCastAble_msg(){
        boolean ret = false;
        int gLv = getOverloadLevel();
        StringBuffer strBuf = new StringBuffer();
        String outStr = "";
        for(int i = 0 ;i < tpCastTag.MaxLevel;i++){
            if(gLv < 0 || gLv > i) {
                strBuf.append("/");
            } else {
                strBuf.append("#");
            }
        }
        if (gLv < 0) {
            outStr = String.format("XXX[%s]XXX 已熔断 无法施术",strBuf);
        } else {
            outStr = String.format("-[%s]-过载等级: %d",strBuf,gLv);
            ret = true;
        }
        LZCommonForgeApi.sendCenterSystemMessage(mPlayer,outStr, ChatFormatting.WHITE);
        return ret;
    }
    private int getOverloadLevel() {
        long gt = LZCommonForgeApi.playerGetLevel(mPlayer).getGameTime();
        int overLevel = mTag.getCoolDownLevel(gt);
        if(overLevel > tpCastTag.MaxLevel) {
            overLevel = -1;
        }
        return overLevel;
    }
    public void sendSystemMessage(String msg, MsgTypes format) {
        mPlayer.sendSystemMessage(Component.literal(msg).withStyle(format.getmFmt()), false);
    }

    public void sendSystemMessage_translate(String key, MsgTypes format) {
        mPlayer.sendSystemMessage(Component.translatable(key).withStyle(format.getmFmt()), false);
    }
    // call back
    public int cb_afterCheck(IntSupplier supplier) {
        if (checkCastAble()) {
            supplier.getAsInt();
        }
        checkCastAble_msg();
        return 0;
    }

    @Override
    public int returnBed() {
        cb_afterCheck(()->{
            BlockPos respawnPos = mPlayer.getRespawnPosition();
            ResourceKey<Level> respawnDim = mPlayer.getRespawnDimension();
            if(respawnPos == null || respawnDim == null) {
//                LZCommonForgeApi.sendSystemMessage(mPlayer,"你还没有睡觉呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                sendSystemMessage_translate(TranslateKeyTool.COMMAND_KEY_PATH + "bed.failed", MsgTypes.NORMAL);

            } else {
                mTag.castOverload((float) Config.levelCostHome);
                mPlayer.teleportTo(mPlayer.getServer().getLevel(respawnDim),
                        respawnPos.getX(),respawnPos.getY(),respawnPos.getZ(),mPlayer.getYRot(),mPlayer.getXRot());
            }
            return 0;
        });
        return 0;
    }

    @Override
    public int returnSpawn() {
        cb_afterCheck(()->{
            MinecraftServer server = mPlayer.getServer();
            ServerLevel sl = server.getLevel(Level.OVERWORLD);
//                        ServerLevel sl = server.getLevel(twf);

            int x = sl.getSharedSpawnPos().getX();
            int y = sl.getSharedSpawnPos().getY();
            int z = sl.getSharedSpawnPos().getZ();

            mTag.castOverload((float) Config.levelCostSPAWN);
            mPlayer.teleportTo( sl,
                    x,y,z,
                    mPlayer.getYRot(),mPlayer.getXRot());
            return 0;
        });
        return 0;
    }

    @Override
    public int returnDeath() {
        cb_afterCheck(()->{
            if (mTag.hasKey(mTag.BackPosAlias)) {
                mTag.castOverload((float) Config.levelCostBack);
                Pair<Vec3,String> home = mTag.getBack();
                ResourceLocation rl = new ResourceLocation(home.getRight());
                ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
                mPlayer.teleportTo(mPlayer.getServer().getLevel(mydim),
                        home.getLeft().x,home.getLeft().y+1,home.getLeft().z,mPlayer.getYRot(),mPlayer.getXRot());
                mTag.rmKey(mTag.BackPosAlias);
                return 1;
            } else {
                sendSystemMessage_translate(TranslateKeyTool.COMMAND_KEY_PATH + "back.failed", MsgTypes.NORMAL);
                return -1;
            }
        });
        return 0;
    }

    @Override
    public int checkOverLoad() {
        return 0;
    }

    @Override
    public int castAssist(CastPlayer player) {
        return 0;
    }

    @Override
    public int tpAsk_request(ServerPlayer targetPlayer) {
        return 0;
    }

    @Override
    public int tpAskHere_request(ServerPlayer targetPlayer) {
        return 0;
    }

    @Override
    public int tpAsk_Accept() {
        return 0;
    }

    @Override
    public int tpAsk_Deny() {
        return 0;
    }

    @Override
    public int dropAll() {
        return 0;
    }

    @Override
    public int dropItem(ItemStack item) {
        return 0;
    }
}
