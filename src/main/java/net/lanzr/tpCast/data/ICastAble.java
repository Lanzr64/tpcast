package net.lanzr.tpCast.data;

import net.lanzr.tpCast.player.CastPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICastAble {
    // common return
    int returnBed();
    int returnSpawn();
    int returnDeath();

    int checkOverLoad();

    int castAssist(CastPlayer player);

    // tp
    int tpAsk_request(ServerPlayer targetPlayer);
    int tpAskHere_request(ServerPlayer targetPlayer);

    int tpAsk_Accept();
    int tpAsk_Deny();

    // toy
    int dropAll();
    int dropItem(ItemStack item);

}
