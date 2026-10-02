package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.CustomTeleportMenu;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CustomTeleportCommand {
    public static void register(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event, Commands.literal("custp").executes(new TpCommand() {
            @Override
            protected boolean requiresCooldownCheck() {
                return false;
            }

            @Override
            protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
                ServerPlayer player = tpPlayer.player;
                if (!player.hasPermissions(Config.CUSTP_PERMISSION_LEVEL.get())) {
                    player.sendSystemMessage(Component.literal("无权限使用该指令"));
                    return 0;
                }
                if (Config.CUSTP_OVERWORLD_ONLY.get() && player.level().dimension() != Level.OVERWORLD) {
                    player.sendSystemMessage(Component.literal("此传送只能在主世界使用"));
                    return 0;
                }
                player.openMenu(new SimpleMenuProvider(
                        (id, inventory, p) -> new CustomTeleportMenu(id, inventory, player),
                        Component.literal("坐标传送")));
                return 1;
            }
        }));
    }
}
