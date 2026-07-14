package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class HOMECommand {

//    private static final Logger LOGGER = LogUtils.getLogger();
    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("home")
                        .executes(COMMAND_RETURN_BED));
    }

    private static final TpCommand COMMAND_RETURN_BED = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            BlockPos respawnPos = tpPlayer.player.getRespawnPosition();
            ResourceKey<Level> respawnDim = tpPlayer.player.getRespawnDimension();
            if(respawnPos == null || respawnDim == null) {
                LZCommonForgeApi.sendSystemMessage(tpPlayer.player,"你还没有睡觉呢!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                return -1;
            } else {
                tpPlayer.tag.castOverload(Config.LEVEL_COST_HOME.get().floatValue());
                tpPlayer.player.teleportTo(tpPlayer.player.getServer().getLevel(respawnDim),
                        respawnPos.getX(),respawnPos.getY(),respawnPos.getZ(),tpPlayer.player.getYRot(),tpPlayer.player.getXRot());
                tpPlayer.sendCoolDownInfoMsg();
                return 1;
            }
        }
    };
//
//    private static int cb_home(ServerPlayer player) {
//        tpCastTag tag = new tpCastTag(player);
//        tpCastStr str = new tpCastStr(player);
//        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
//        if(!castAble) {
//            str.sendCoolDownInfoMsg();
//            return -1;
//        }
//        if (tag.hasKey(tag.HomePosAlias)) {
//            tag.castOverload(Config.LEVEL_COST_HOME.get().floatValue());
//            Pair<Vec3,String> home = tag.getHome();
//            ResourceLocation rl = LZCommonForgeApi.getDimensionResourceLocation(home.getRight());
//
//
//            ResourceKey<Level> mydim = ResourceKey.create(Registries.DIMENSION,rl);
//            player.teleportTo(player.getServer().getLevel(mydim), home.getLeft().x,home.getLeft().y+1,home.getLeft().z,player.getYRot(),player.getXRot());
//
//            str.sendCoolDownInfoMsg();
//            return 1;
//        } else {
//            LZCommonForgeApi.sendSystemMessage(player,"你无家可归!",LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
//            return -1;
//        }
//    }
}
