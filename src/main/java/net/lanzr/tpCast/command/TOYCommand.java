package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class TOYCommand {
    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("cast-off").executes(ctx -> cb_castOff(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("tyjtyj").executes(ctx -> cb_tyj(ctx.getSource().getPlayerOrException()))
        );
        event.getDispatcher().register(
                Commands.literal("hat").executes(ctx -> cb_hat(ctx.getSource().getPlayerOrException()))
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

    private static int cb_hat(ServerPlayer player) {
        ItemStack headItem = player.getInventory().armor.get(3);
        Iterable<ItemStack> handItems = player.getHandSlots();
        ItemStack selectHandItem = ItemStack.EMPTY;
        InteractionHand hand = InteractionHand.MAIN_HAND;
        for (ItemStack handItem : handItems) {
            if(handItem.getItem() != Items.AIR) {
                selectHandItem = handItem;
                break;
            }
            hand = InteractionHand.OFF_HAND;
        }
        if(selectHandItem == ItemStack.EMPTY) {
            hand = InteractionHand.MAIN_HAND;
        }
        player.setItemInHand(hand, headItem);
        player.getInventory().armor.set(3, selectHandItem);
        return 1;
//        if (headItem.isEmpty() && selectHandItem.isEmpty()) {
//            LZCommonForgeApi.sendSystemMessage(player, "��û��ͷ��������", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());

    }
}
