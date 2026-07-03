package net.lanzr.tpCast.command.tools;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class CommandTools {
    private CommandTools() {
        throw new UnsupportedOperationException("Utility class");
    }


    public static void registerWithPrefix(RegisterCommandsEvent event,
                                          LiteralArgumentBuilder<CommandSourceStack> command,
                                          String prefix) {
        // 注册原始命令
        event.getDispatcher().register(command);

        // 注册带前缀的别名
        LiteralArgumentBuilder<CommandSourceStack> prefixBuilder = Commands.literal(prefix);
        prefixBuilder.then(command);
        event.getDispatcher().register(prefixBuilder);
    }
    public static void registerWithPrefix(RegisterCommandsEvent event,
                                          LiteralArgumentBuilder<CommandSourceStack> command) {
        registerWithPrefix(event,command,"tyj");
    }
}
