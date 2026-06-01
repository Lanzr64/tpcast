package net.lanzr.tpCast.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

public class BackCommand {

    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("back").executes(BACK_COMMAND)
        );

        final LiteralArgumentBuilder<CommandSourceStack> literalargumentBuilder =
                Commands.literal("tyj");

        literalargumentBuilder
                .then(Commands.literal("back").executes(BACK_COMMAND));

        event.getDispatcher().register(literalargumentBuilder);
    }

    private static final TpCommand BACK_COMMAND = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            if (tpPlayer.tag.hasKey(tpPlayer.tag.BackPosAlias)) {
                tpPlayer.tag.castOverload((float) Config.levelCostBack);
                Pair<Vec3,String> home = tpPlayer.tag.getBack();
                ResourceLocation rl = LZCommonForgeApi.getDimensionResourceLocation(home.getRight());

                ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
                tpPlayer.player.teleportTo(tpPlayer.player.getServer().getLevel(mydim),
                        home.getLeft().x,home.getLeft().y+1,home.getLeft().z,
                        tpPlayer.player.getYRot(),tpPlayer.player.getXRot());
                tpPlayer.tag.rmKey(tpPlayer.tag.BackPosAlias);
                tpPlayer.sendCoolDownInfoMsg();
                return 1;
            } else {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,"你还没死呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            }
        }
    };

}
