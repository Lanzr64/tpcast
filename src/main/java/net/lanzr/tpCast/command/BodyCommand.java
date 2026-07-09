package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.command.tools.CommandTools;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;

public class BodyCommand {
    public static void register(RegisterCommandsEvent event) {
        CommandTools.registerWithPrefix(event,
                Commands.literal("hat").executes(COMMAND_HAT));
    }

    private static final TpCommand COMMAND_HAT = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            tryChangeEquipSlot(tpPlayer.player,3);
            return 1;
        }
    };
    private static void tryChangeEquipSlot(ServerPlayer player, int armorSlot) {
        ItemStack headItem = player.getInventory().armor.get(armorSlot);
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
        player.getInventory().armor.set(armorSlot, selectHandItem);
        return;
    }


}
