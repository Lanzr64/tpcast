package net.lanzr.tpCast.command;

import net.lanzr.tpCast.api.tpCastStr;
import net.lanzr.tpCast.api.tpCastTag;
import net.lanzr.tpCast.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;

public class TOYCommand {
    public static void register(RegisterCommandsEvent event) {
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
        player.sendMessage(new TextComponent("Cast! Off !!!!!")
                .withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
        return 0;
    }
    private static int cb_tyj(ServerPlayer player) {
        tpCastTag tag = new tpCastTag(player);
        tpCastStr str = new tpCastStr(player);
        boolean castAble = (tag.getCoolDownLevel(player.getLevel().getGameTime()) <= tag.MaxLevel);
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
