package net.lanzr.tpCast.api;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Reflection-based compatibility layer for the sable mod's sublevel coordinate system.
 *
 * <p>All sable classes are accessed via {@code Class.forName()} — there is zero compile-time
 * dependency on the sable mod. Each reflection handle is resolved independently so that a
 * single method change in a future sable version does not disable the entire compat layer.</p>
 */
public class SableCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final boolean IS_SABLE_LOADED;
    private static Object SABLE_HELPER_INSTANCE;
    private static Method HELPER_GET_TRACKING_SUBLEVEL;
    private static Method SUBLEVEL_LOGICAL_POSE;
    private static Method SUBLEVEL_IS_REMOVED;
    private static Method POSE_TRANSFORM_POSITION_INVERSE;
    /** Found at runtime by name from ServerPlayer methods. */
    private static Method SET_TRACKING_SUBLEVEL;
    /** Cached Sable.Helper interface — resolved once at init. */
    private static Class<?> SABLE_HELPER_INTERFACE;

    // ---- resolve helper: tries Class.forName + finds one method, returns null on failure ----

    private static Method findHelperMethod(String name, Class<?>... params) {
        if (SABLE_HELPER_INSTANCE == null) return null;
        try {
            Class<?> clazz = SABLE_HELPER_INSTANCE.getClass();
            try {
                return clazz.getMethod(name, params);
            } catch (NoSuchMethodException e) {
                for (Class<?> iface : clazz.getInterfaces()) {
                    try {
                        return iface.getMethod(name, params);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                // Brute-force by name + parameter count
                for (Method m : clazz.getMethods()) {
                    if (m.getName().equals(name) && m.getParameterCount() == params.length) {
                        return m;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("SableCompat: could not resolve helper method {}: {}", name, e.getMessage());
        }
        return null;
    }

    private static Method findSubLevelMethod(String name, Class<?>... params) {
        try {
            Class<?> subLevelClass = Class.forName("dev.ryanhcode.sable.sublevel.SubLevel");
            return subLevelClass.getMethod(name, params);
        } catch (Exception e) {
            LOGGER.warn("SableCompat: could not resolve SubLevel method {}: {}", name, e.getMessage());
            return null;
        }
    }

    private static Method findPoseMethod(String name, Class<?>... params) {
        try {
            Class<?> poseClass = Class.forName("dev.ryanhcode.sable.companion.math.Pose3dc");
            return poseClass.getMethod(name, params);
        } catch (Exception e) {
            LOGGER.warn("SableCompat: could not resolve Pose3dc method {}: {}", name, e.getMessage());
            return null;
        }
    }

    static {
        boolean loaded = false;
        try {
            Class<?> sableClass = Class.forName("dev.ryanhcode.sable.Sable");
            java.lang.reflect.Field helperField = sableClass.getField("HELPER");
            SABLE_HELPER_INSTANCE = helperField.get(null);
            // Resolve Sable.Helper inner interface (used as fallback for getContaining lookup)
            for (Class<?> inner : sableClass.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("Helper")) {
                    SABLE_HELPER_INTERFACE = inner;
                    break;
                }
            }
            loaded = true;
        } catch (Exception e) {
            LOGGER.warn("Sable mod not detected — SableCompat features will be disabled");
        }

        // Resolve each method independently — partial success is fine
        if (loaded) {
            HELPER_GET_TRACKING_SUBLEVEL = findHelperMethod(
                    "getTrackingSubLevel", net.minecraft.world.entity.Entity.class);
            SUBLEVEL_LOGICAL_POSE = findSubLevelMethod("logicalPose");
            SUBLEVEL_IS_REMOVED = findSubLevelMethod("isRemoved");
            POSE_TRANSFORM_POSITION_INVERSE = findPoseMethod(
                    "transformPositionInverse", Vector3d.class);

            // Resolve sable$setTrackingSubLevel from ServerPlayer methods
            try {
                for (Method m : net.minecraft.server.level.ServerPlayer.class.getMethods()) {
                    if (m.getName().equals("sable$setTrackingSubLevel") && m.getParameterCount() == 1) {
                        SET_TRACKING_SUBLEVEL = m;
                        break;
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("SableCompat: could not resolve sable$setTrackingSubLevel: {}", e.getMessage());
            }

            // Log what we got
            int ok = 0, total = 0;
            for (Method m : new Method[]{
                    HELPER_GET_TRACKING_SUBLEVEL, SUBLEVEL_LOGICAL_POSE,
                    SUBLEVEL_IS_REMOVED, POSE_TRANSFORM_POSITION_INVERSE, SET_TRACKING_SUBLEVEL}) {
                total++;
                if (m != null) ok++;
            }
            LOGGER.info("SableCompat: initialized with {}/{} method handles", ok, total);
        }

        IS_SABLE_LOADED = loaded;
    }

    /** Private constructor — utility class. */
    private SableCompat() {
    }

    // ---------------------------------------------------------------
    //  Public API
    // ---------------------------------------------------------------

    /** Returns {@code true} if the sable mod is present on the classpath. */
    public static boolean isSableLoaded() {
        return IS_SABLE_LOADED;
    }

    /**
     * Converts a real-world {@link BlockPos} to the visual (sublevel-local) coordinate space
     * using the player's currently tracked sublevel.
     */
    public static BlockPos getVisualPosition(ServerPlayer player, BlockPos realPos) {
        if (!IS_SABLE_LOADED || player == null || realPos == null
                || HELPER_GET_TRACKING_SUBLEVEL == null
                || SUBLEVEL_LOGICAL_POSE == null
                || SUBLEVEL_IS_REMOVED == null
                || POSE_TRANSFORM_POSITION_INVERSE == null) {
            return realPos;
        }
        try {
            Object subLevel = HELPER_GET_TRACKING_SUBLEVEL.invoke(SABLE_HELPER_INSTANCE, player);
            if (subLevel != null) {
                boolean removed = (boolean) SUBLEVEL_IS_REMOVED.invoke(subLevel);
                if (!removed) {
                    Object pose = SUBLEVEL_LOGICAL_POSE.invoke(subLevel);
                    return transformPositionInverse(pose, realPos);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("SableCompat.getVisualPosition failed: {}", e.getMessage());
        }
        return realPos;
    }

    /**
     * Checks whether the given position lies inside any sable {@code SubLevel}.
     * Dynamically resolves getContaining at call-time since this method's exact
     * location varies across sable versions.
     */
    public static boolean isPositionInSubLevel(Level level, BlockPos pos) {
        if (!IS_SABLE_LOADED || level == null || pos == null || SABLE_HELPER_INSTANCE == null) {
            return false;
        }
        try {
            Method containing = findContainingMethod();
            if (containing == null) return false;
            Object subLevel = containing.invoke(SABLE_HELPER_INSTANCE, level, pos);
            return subLevel != null;
        } catch (Exception e) {
            LOGGER.warn("SableCompat.isPositionInSubLevel failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Converts a real-world {@link BlockPos} to the visual coordinate space
     * by first looking up which sable {@code SubLevel} contains the position.
     */
    public static BlockPos getVisualPositionFromSubLevel(Level level, BlockPos realPos) {
        if (!IS_SABLE_LOADED || level == null || realPos == null
                || SUBLEVEL_LOGICAL_POSE == null
                || POSE_TRANSFORM_POSITION_INVERSE == null
                || SABLE_HELPER_INSTANCE == null) {
            return realPos;
        }
        try {
            Method containing = findContainingMethod();
            if (containing == null) return realPos;
            Object subLevel = containing.invoke(SABLE_HELPER_INSTANCE, level, realPos);
            if (subLevel != null) {
                Object pose = SUBLEVEL_LOGICAL_POSE.invoke(subLevel);
                return transformPositionInverse(pose, realPos);
            }
        } catch (Exception e) {
            LOGGER.warn("SableCompat.getVisualPositionFromSubLevel failed: {}", e.getMessage());
        }
        return realPos;
    }

    /**
     * Prepares a player for teleport by clearing any active sable tracking sublevel.
     * MUST be called BEFORE teleporting a player away from a sublevel.
     */
    public static void beforeTeleport(ServerPlayer player) {
        clearTrackingSubLevel(player);
    }

    /**
     * Clears the player's sable tracking sublevel, preventing sable from adjusting
     * their position via the tick handler (avoids AABB corruption after teleport).
     */
    public static void clearTrackingSubLevel(ServerPlayer player) {
        if (player == null) return;
        // Use cached method if available
        if (SET_TRACKING_SUBLEVEL != null) {
            try {
                SET_TRACKING_SUBLEVEL.invoke(player, (Object) null);
                return;
            } catch (Exception e) {
                LOGGER.warn("SableCompat.clearTrackingSubLevel failed: {}", e.getMessage());
            }
        }
        // Fallback: try to find and call at runtime
        if (!IS_SABLE_LOADED) return;
        try {
            for (Method m : player.getClass().getMethods()) {
                if (m.getName().equals("sable$setTrackingSubLevel") && m.getParameterCount() == 1) {
                    m.invoke(player, (Object) null);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("SableCompat.clearTrackingSubLevel (fallback) failed: {}", e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    //  Internal helpers
    // ---------------------------------------------------------------

    /**
     * Dynamically finds the {@code getContaining(Level, BlockPos)} method.
     * Uses the Helper interface directly + exhaustively searches all methods
     * by name + parameter count, accepting any first param assignable from Level.
     */
    private static Method findContainingMethod() {
        if (SABLE_HELPER_INSTANCE == null) return null;
        try {
            // Priority 1: try the Sable.Helper interface directly
            if (SABLE_HELPER_INTERFACE != null) {
                try {
                    return SABLE_HELPER_INTERFACE.getMethod("getContaining", Level.class, BlockPos.class);
                } catch (NoSuchMethodException ignored) {}
                for (Method m : SABLE_HELPER_INTERFACE.getMethods()) {
                    if (m.getName().equals("getContaining") && m.getParameterCount() == 2) {
                        return m;
                    }
                }
            }
            // Priority 2: try the runtime class + all superclasses up to Object
            Class<?> clazz = SABLE_HELPER_INSTANCE.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getName().equals("getContaining") && m.getParameterCount() == 2) {
                        return m;
                    }
                }
                // Check interfaces of this class
                for (Class<?> iface : clazz.getInterfaces()) {
                    try {
                        return iface.getMethod("getContaining", Level.class, BlockPos.class);
                    } catch (NoSuchMethodException ignored) {}
                }
                clazz = clazz.getSuperclass();
            }
            LOGGER.warn("SableCompat: getContaining(Level,BlockPos) not found");
        } catch (Exception e) {
            LOGGER.warn("SableCompat: getContaining lookup failed: {}", e.getMessage());
        }
        return null;
    }

    /** Applies Pose3dc#transformPositionInverse to convert real→visual coords. */
    private static BlockPos transformPositionInverse(Object pose, BlockPos realPos)
            throws Exception {
        Vec3 center = Vec3.atBottomCenterOf(realPos);
        Vector3d jomlVec = new Vector3d(center.x, center.y, center.z);
        Vector3d result = (Vector3d) POSE_TRANSFORM_POSITION_INVERSE.invoke(pose, jomlVec);
        return new BlockPos((int) result.x, (int) result.y, (int) result.z);
    }
}
