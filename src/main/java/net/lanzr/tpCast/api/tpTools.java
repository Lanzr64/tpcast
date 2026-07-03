package net.lanzr.tpCast.api;

import java.util.HashMap;
import java.util.UUID;

public class tpTools {
    // 通用请求管理器，用于管理TPA等请求
    public static class RequestManager {
        private final HashMap<UUID, UUID> requests = new HashMap<>();
        
        public void add(UUID target, UUID requester) {
            requests.put(target, requester);
        }
        // 检查是否有请求
        public boolean pending(UUID target) {
            return requests.containsKey(target);
        }
        
        public void remove(UUID target) {
            if (pending(target)) {
                requests.remove(target);
            }
        }
        
        public UUID fromWho(UUID target) {
            if (pending(target)) {
                return requests.get(target);
            }
            return null;
        }
    }
    
    public static final RequestManager tpaRequests = new RequestManager();
    
    public static final RequestManager tpahereRequests = new RequestManager();
}
