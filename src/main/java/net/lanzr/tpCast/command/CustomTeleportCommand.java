package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.CustomTeleportMenu;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.config.CustomTeleportRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class CustomTeleportCommand {
    public static void register(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event, Commands.literal("custp")
                .executes(OPEN_MENU)
                .then(Commands.literal("help").executes(SHOW_HELP)));
    }

    private static final TpCommand OPEN_MENU = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            if (!hasPermission(player)) return 0;
            if (Config.CUSTP_OVERWORLD_ONLY.get() && player.level().dimension() != Level.OVERWORLD) {
                player.sendSystemMessage(Component.literal("此传送只能在主世界使用"));
                return 0;
            }
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> new CustomTeleportMenu(id, inventory, player),
                    Component.literal("坐标传送")));
            return 1;
        }
    };

    private static final TpCommand SHOW_HELP = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            if (!hasPermission(player)) return 0;
            CustomTeleportRules rules = CustomTeleportRules.fromConfig();
            player.sendSystemMessage(Component.literal("坐标传送物品（每件对应的格数）："));
            showAxis(player, "X", rules.x());
            showAxis(player, "Z", rules.z());
            return 1;
        }
    };

    private static boolean hasPermission(ServerPlayer player) {
        if (player.hasPermissions(Config.CUSTP_PERMISSION_LEVEL.get())) return true;
        player.sendSystemMessage(Component.literal("无权限使用该指令"));
        return false;
    }

    private static void showAxis(ServerPlayer player, String axis, Map<Item, Integer> rules) {
        player.sendSystemMessage(Component.literal(axis + " 轴："));
        if (rules.isEmpty()) {
            player.sendSystemMessage(Component.literal("  没有配置有效物品"));
            return;
        }
        rules.forEach((item, blocks) -> player.sendSystemMessage(
                Component.literal("  - ")
                        .append(new ItemStack(item).getHoverName())
                        .append(Component.literal(" (" + ForgeRegistries.ITEMS.getKey(item) + ")：" + blocks + " 格/个"))));
    }
}
