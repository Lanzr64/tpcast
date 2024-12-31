package net.lanzr.tpCast.api;

import java.util.HashMap;
import java.util.UUID;

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
}
