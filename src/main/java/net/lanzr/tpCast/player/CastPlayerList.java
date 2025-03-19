package net.lanzr.tpCast.player;


import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

public class CastPlayerList {
    private static final HashMap<UUID, CastPlayer> PLAYERS = new HashMap<UUID, CastPlayer>();
    public static void add(ServerPlayer player) {
        PLAYERS.put(player.getUUID(), new CastPlayer(player));
    }
    public static CastPlayer get(UUID uuid) {
        return PLAYERS.get(uuid);
    }
    public static void remove(UUID uuid) {
        PLAYERS.remove(uuid);
    }
    public static int getSize() {
        return PLAYERS.size();
    }
}
