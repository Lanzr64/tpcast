package net.lanzr.tpCast.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lanzr.tpCast.api.LZCommonForgeApi;
import net.lanzr.tpCast.api.TpCastPlayer;
import net.lanzr.tpCast.api.TrashCanMenu;
import net.lanzr.tpCast.command.tools.CommandTools;import net.lanzr.tpCast.config.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class TOYCommand {
    public static void register(RegisterCommandsEvent event) {

        CommandTools.registerWithPrefix(event,
                Commands.literal("cast-off").executes(COMMAND_CAST_OFF));
        CommandTools.registerWithPrefix(event,
                Commands.literal("tyjtyj").executes(COMMAND_TYJ));
        CommandTools.registerWithPrefix(event,
                Commands.literal("repair").executes(COMMAND_REPAIR));
        CommandTools.registerWithPrefix(event,
                Commands.literal("trashcan").executes(COMMAND_TRASHCAN));
    }
    
    // 掉落�?上所有物�?
    // TODO curios 的饰品之类的现在还不�?�?
    private static final TpCommand COMMAND_CAST_OFF = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            player.getInventory().dropAll();
            LZCommonForgeApi.sendSystemMessage(player, "Cast! Off !!!!!", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
            return 0;
        }
    };

    //  吐一�?土窑�?
    private static final TpCommand COMMAND_TYJ = new TpCommand() {
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            tpPlayer.tag.castOverload(Config.LEVEL_COST_HOME.get().floatValue());

            ItemStack tItem = new ItemStack(Items.COOKED_CHICKEN, 1);
            player.drop(tItem, false);

            tpPlayer.sendCoolDownInfoMsg();
            return 1;
        }
    };

    private static final TpCommand COMMAND_REPAIR = new TpCommand() {
        
        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;


            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            int repairedCount = 0;

            if (!mainHand.isEmpty() && mainHand.isDamageableItem()) {
                mainHand.setDamageValue(0);
                repairedCount++;
            }
            if (!offHand.isEmpty() && offHand.isDamageableItem()) {
                offHand.setDamageValue(0);
                repairedCount++;
            }

            if (repairedCount > 0) {
                LZCommonForgeApi.sendSystemMessage(player, "已修�? " + repairedCount + " 件物品的耐久�?", LZCommonForgeApi.MsgTypes.NORMAL.getmFmt());
                tpPlayer.tag.castOverload(Config.LEVEL_COST_REPAIR.get().floatValue());
            } else {
                LZCommonForgeApi.sendSystemMessage(player, "手上没有需要修复的物品�?", LZCommonForgeApi.MsgTypes.ALERT.getmFmt());
            }

            return repairedCount;
        }
    };

    private static final TpCommand COMMAND_TRASHCAN = new TpCommand() {
        @Override
        protected boolean requiresCooldownCheck() {
            return false;
        }

        @Override
        protected int execute(CommandContext<CommandSourceStack> ctx, TpCastPlayer tpPlayer) throws CommandSyntaxException {
            ServerPlayer player = tpPlayer.player;
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new TrashCanMenu(containerId, playerInventory),
                    Component.literal("垃圾�?")
            ));
            return 1;
        }
    };
}
