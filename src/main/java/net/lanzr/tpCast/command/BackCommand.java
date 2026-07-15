package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.SableCompat;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

public class BackCommand {

    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("back").executes(BACK_COMMAND));
    }

    private static final TpCommand BACK_COMMAND = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            if (tpPlayer.tag.hasKey(tpPlayer.tag.BackPosAlias)) {
                tpPlayer.tag.castOverload((Config.LEVEL_COST_BACK.get().floatValue()));
                Pair<Vec3,String> home = tpPlayer.tag.getBack();
                ResourceLocation rl = ResourceLocation.parse(home.getRight());
                ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
                tpPlayer.player.teleportTo(tpPlayer.player.getServer().getLevel(mydim),
                        home.getLeft().x,home.getLeft().y+1,home.getLeft().z,
                        java.util.Set.of(), tpPlayer.player.getYRot(), tpPlayer.player.getXRot());
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
