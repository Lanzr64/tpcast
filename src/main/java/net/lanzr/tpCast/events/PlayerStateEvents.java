package net.lanzr.tpCast.events;

import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.tpCast;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = tpCast.MODID)
public final class PlayerStateEvents {
    private PlayerStateEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            new tpCastTag(player).setBack();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        if (!original.getPersistentData().contains(tpCast.MODID)) {
            return;
        }

        CompoundTag saved = original.getPersistentData().getCompound(tpCast.MODID);
        event.getEntity().getPersistentData().put(tpCast.MODID, saved.copy());
    }
}
