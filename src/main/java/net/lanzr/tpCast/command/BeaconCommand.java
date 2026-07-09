package net.lanzr.tpCast.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.BeaconStorage;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.tpCast;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class BeaconCommand {

    public static void register(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event,
                Commands.literal("beacon")
                        .executes(COMMAND_BEACON)
                        .then(Commands.literal("go")
                                .then(Commands.argument("owner", StringArgumentType.string())
                                        .executes(COMMAND_BEACON_GO))));
        CommandTools.registerWithPrefix(event,
                Commands.literal("debeacon").executes(COMMAND_DEBEACON));
    }

    // 维度中文名映射，用于广播消息显示
    private static String dimDisplayName(String dimId) {
        if (dimId.contains("overworld")) return "主世界";
        if (dimId.contains("nether")) return "下界";
        if (dimId.contains("the_end")) return "末地";
        return dimId;
    }

    private static final TpCommand COMMAND_BEACON = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            boolean hadOld = BeaconStorage.hasBeacon(player);

            BeaconStorage.saveBeacon(player);

            BlockPos pos = player.getOnPos();
            String playerName = player.getName().getString();
            String dimName = dimDisplayName(player.level().dimension().location().toString());

            String tpCommand = "/beacon go " + playerName;
            MutableComponent clickBtn = Component.literal("[ 点击传送 ]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withBold(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("点击传送到 " + playerName + " 的 beacon 位置")))
                    );

            MutableComponent message = Component.empty();
            if (hadOld) {
                message.append(Component.literal(playerName + " 移动了 beacon ").withStyle(ChatFormatting.YELLOW));
            } else {
                message.append(Component.literal(playerName + " 放置了一个 beacon ").withStyle(ChatFormatting.YELLOW));
            }
            message.append(Component.literal("[" + dimName + " " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] ").withStyle(ChatFormatting.GRAY));
            message.append(clickBtn);

            // 消耗过载
            tpPlayer.tag.castOverload(Config.LEVEL_COST_BEACON.get().floatValue());

            // 广播给所有在线玩家
            player.getServer().getPlayerList().broadcastSystemMessage(message, false);

            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };

    // /beacon go <owner>: 传送到 owner 的 beacon 位置（支持离线读取）
    private static final TpCommand COMMAND_BEACON_GO = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            String ownerName = StringArgumentType.getString(ctx, "owner");
            Pair<Vec3, String> beacon = resolveOwnerBeacon(tpPlayer.player.getServer(), ownerName);

            if (beacon == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, ownerName + " 还没有放置 beacon", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            }

            ResourceLocation rl = new ResourceLocation(beacon.getRight());
            ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, rl);
            ServerLevel targetLevel = tpPlayer.player.getServer().getLevel(dim);

            if (targetLevel == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "无法找到 beacon 所在维度!", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
                return -1;
            }

            Vec3 pos = beacon.getLeft();
            tpPlayer.player.teleportTo(targetLevel,
                    pos.x + 0.5, pos.y + 1, pos.z + 0.5,
                    tpPlayer.player.getYRot(), tpPlayer.player.getXRot());
            tpPlayer.tag.castOverload(Config.LEVEL_COST_BEACON_GO.get().floatValue());
            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };

    // /debeacon: 移除自己的 beacon
    private static final TpCommand COMMAND_DEBEACON = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            if (!BeaconStorage.hasBeacon(tpPlayer.player)) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "你还没有放置 beacon", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            }
            BeaconStorage.removeBeacon(tpPlayer.player);
            LZCommonForgeApi.sendSystemMessage(tpPlayer.player, "已移除你的 beacon", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return 1;
        }
    };

    /**
     * 根据玩家名获取 beacon 位置（支持离线读取）。
     * 从永久化存储 world/tpcast_beacons/&lt;uuid&gt;.dat 中读取。
     */
    private static Pair<Vec3, String> resolveOwnerBeacon(net.minecraft.server.MinecraftServer server, String name) {
        UUID uuid = resolveUuid(server, name);
        return BeaconStorage.getBeacon(server, uuid);
    }

    /**
     * 将玩家名解析为 UUID。
     * 优先用 profile cache（正版），fallback 到离线模式 UUID 计算。
     */
    private static UUID resolveUuid(net.minecraft.server.MinecraftServer server, String name) {
        Optional<GameProfile> profile = server.getProfileCache().get(name);
        if (profile.isPresent()) {
            return profile.get().getId();
        }
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
