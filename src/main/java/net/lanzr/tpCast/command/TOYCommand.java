package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;

public class TOYCommand {
    public static void register(RegisterCommandsEvent event) {
        if (!Config.enableToy) {
            return;
        }
        event.getDispatcher().register(
                Commands.literal("cast-off").executes(ctx -> cb_castOff(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("tyjtyj").executes(ctx -> cb_tyj(ctx.getSource().getPlayerOrException()))
        );
    }

    private static int cb_castOff(ServerPlayer player) {
        Inventory inv = player.getInventory();
        inv.dropAll();

        LZCommonForgeApi.sendSystemMessage(player, "Cast! Off !!!!!", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());

        return 0;
    }
    private static int cb_tyj(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(LZCommonForgeApi.playerGetLevel(player).getGameTime()) <= tag.MaxLevel);
        if(!castAble) {
            str.sendCoolDownInfoMsg();
            return -1;
        }
        tag.castOverload((float) Config.levelCostHome);

        ItemStack tItem = new ItemStack(Items.COOKED_CHICKEN, 1);
        player.drop(tItem, false);

        str.sendCoolDownInfoMsg();
        return 1;
    }
}
