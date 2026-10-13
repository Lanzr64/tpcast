package net.lanzr.tpCast.teleport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Pending requests for one command registration lifecycle. */
public final class TeleportRequests {
    public enum Direction {
        TO_TARGET,
        TO_REQUESTER
    }

    public record Request(UUID requester, Direction direction) {
    }

    private final Map<UUID, Request> byRecipient = new HashMap<>();

    public void submit(UUID recipient, UUID requester, Direction direction) {
        byRecipient.put(recipient, new Request(requester, direction));
    }

    public Request take(UUID recipient) {
        return byRecipient.remove(recipient);
    }
}
