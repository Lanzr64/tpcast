package net.lanzr.tpCast.api;

import net.lanzr.tpCast.config.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class tpTools {
    public class tpaRequests {
        private static HashMap<UUID, UUID> requests = new HashMap<UUID, UUID>();
        public static void add(UUID target, UUID requester) {
            requests.put(target, requester);
        };
        public static boolean pending(UUID target) {
            return requests.containsKey(target);
        }
        public static void remove(UUID target) {
            if(pending(target)) {
                requests.remove(target);
            }
        }
        public static UUID fromWho(UUID target) {
            if(pending(target)) {
                return requests.get(target);
            }
            return null;
        }
    }
    public class tpahereRequests {
        private static HashMap<UUID, UUID> requests = new HashMap<UUID, UUID>();
        public static void add(UUID target, UUID requester) {
            requests.put(target, requester);
        };
        public static boolean pending(UUID target) {
            return requests.containsKey(target);
        }
        public static void remove(UUID target) {
            if(pending(target)) {
                requests.remove(target);
            }
        }
        public static UUID fromWho(UUID target) {
            if(pending(target)) {
                return requests.get(target);
            }
            return null;
        }
    }
    public static int overloadcheckFunc(ServerPlayer player, Supplier<Integer> func) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            str.sendCoolDownInfoMsg();
            return -1;
        }
        func.get();
        // 允许使用功能
        return 1;
    }
}
