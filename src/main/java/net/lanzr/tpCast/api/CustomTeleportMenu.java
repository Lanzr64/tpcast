package net.lanzr.tpCast.api;

import net.lanzr.tpCast.config.Config;
import net.lanzr.tpCast.config.CustomTeleportRules;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** A server-side menu using the vanilla 9x3 chest screen, so clients need no mod. */
public class CustomTeleportMenu extends AbstractContainerMenu {
    private static final int MENU_SIZE = 27;
    private static final int X_TOGGLE = 1;
    private static final int Z_TOGGLE = 7;
    private static final int CONFIRM = 13;
    private static final int[] X_SLOTS = {0, 2, 9, 10, 11, 18, 19, 20};
    private static final int[] Z_SLOTS = {6, 8, 15, 16, 17, 24, 25, 26};

    private final Container container = new SimpleContainer(MENU_SIZE);
    private final ServerPlayer owner;
    private final Map<Item, Integer> xRules;
    private final Map<Item, Integer> zRules;
    private boolean negativeX;
    private boolean negativeZ;
    private boolean confirmed;

    public CustomTeleportMenu(int containerId, Inventory inventory, ServerPlayer owner) {
        super(MenuType.GENERIC_9x3, containerId);
        this.owner = owner;
        CustomTeleportRules configured = CustomTeleportRules.fromConfig();
        this.xRules = configured.x();
        this.zRules = configured.z();
        container.startOpen(owner);

        for (int index = 0; index < MENU_SIZE; index++) {
            final Map<Item, Integer> rules = rulesForSlot(index);
            Slot slot = new Slot(container, index, 8 + (index % 9) * 18, 18 + (index / 9) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return rules != null && rules.containsKey(stack.getItem());
                }

                @Override
                public boolean mayPickup(Player player) {
                    return rules != null;
                }
            };
            addSlot(slot);
            if (rules == null && index != X_TOGGLE && index != Z_TOGGLE && index != CONFIRM) {
                container.setItem(index, named(Items.PURPLE_STAINED_GLASS_PANE, "请做好着陆准备"));
            }
        }
        updateToggle(X_TOGGLE, false);
        updateToggle(Z_TOGGLE, false);
        container.setItem(CONFIRM, named(Items.ENDER_PEARL, "确认传送"));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    private Map<Item, Integer> rulesForSlot(int slot) {
        for (int index : X_SLOTS) {
            if (slot == index) return xRules;
        }
        for (int index : Z_SLOTS) {
            if (slot == index) return zRules;
        }
        return null;
    }

    private static ItemStack named(Item item, String title) {
        ItemStack stack = new ItemStack(item);
        stack.setHoverName(Component.literal(title));
        return stack;
    }

    private void updateToggle(int slot, boolean negative) {
        String axis = slot == X_TOGGLE ? "X" : "Z";
        container.setItem(slot, named(negative ? Items.FIRE_CHARGE : Items.SNOWBALL,
                "将" + axis + "坐标切换为" + (negative ? "正值" : "负值")));
        broadcastChanges();
    }

    @Override
    public void clicked(int slotId, int button, ClickType type, Player player) {
        if (player != owner || owner.containerMenu != this) return;
        if (slotId >= 0 && slotId < MENU_SIZE && rulesForSlot(slotId) == null) {
            if (type == ClickType.PICKUP && button >= 0 && button <= 1) {
                if (slotId == X_TOGGLE) {
                    negativeX = !negativeX;
                    updateToggle(X_TOGGLE, negativeX);
                } else if (slotId == Z_TOGGLE) {
                    negativeZ = !negativeZ;
                    updateToggle(Z_TOGGLE, negativeZ);
                } else if (slotId == CONFIRM) {
                    confirm();
                }
            }
            return;
        }
        super.clicked(slotId, button, type, player);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot.container != container || rulesForSlot(slot.getContainerSlot()) != null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotId) {
        if (player != owner || slotId < 0 || slotId >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(slotId);
        if (!slot.hasItem() || (slotId < MENU_SIZE && rulesForSlot(slotId) == null)) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean moved = slotId < MENU_SIZE
                ? moveItemStackTo(stack, MENU_SIZE, slots.size(), true)
                : (xRules.containsKey(stack.getItem()) || zRules.containsKey(stack.getItem()))
                    && moveItemStackTo(stack, 0, MENU_SIZE, false);
        if (!moved) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    private void confirm() {
        if (confirmed || !owner.isAlive() || owner.isRemoved()) return;
        if (!owner.hasPermissions(Config.CUSTP_PERMISSION_LEVEL.get())) {
            owner.sendSystemMessage(Component.literal("无权限使用该指令"));
            return;
        }
        if (Config.CUSTP_OVERWORLD_ONLY.get() && owner.level().dimension() != Level.OVERWORLD) {
            owner.sendSystemMessage(Component.literal("此传送只能在主世界使用"));
            return;
        }

        Long xDistance = distance(X_SLOTS, xRules);
        Long zDistance = distance(Z_SLOTS, zRules);
        if (xDistance == null || zDistance == null) {
            owner.sendSystemMessage(Component.literal("传送失败：槽位物品无效"));
            return;
        }
        long xPosition = negativeX ? -xDistance : xDistance;
        long zPosition = negativeZ ? -zDistance : zDistance;
        if (xPosition < Integer.MIN_VALUE || xPosition > Integer.MAX_VALUE
                || zPosition < Integer.MIN_VALUE || zPosition > Integer.MAX_VALUE) {
            owner.sendSystemMessage(Component.literal("传送失败：目标超出世界边界"));
            return;
        }
        int x = (int) xPosition;
        int z = (int) zPosition;
        ServerLevel level = owner.serverLevel();
        if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(x, 320, z))) {
            owner.sendSystemMessage(Component.literal("传送失败：目标超出世界边界"));
            return;
        }

        confirmed = true;
        List<ItemStack> payment = new ArrayList<>();
        if (Config.CUSTP_CONSUME_ITEMS.get()) {
            collect(X_SLOTS, payment);
            collect(Z_SLOTS, payment);
        }
        try {
            owner.closeContainer();
            owner.teleportTo(level, x, 320, z, owner.getYRot(), owner.getXRot());
            if (owner.serverLevel() != level || owner.getX() != x || owner.getY() != 320 || owner.getZ() != z) {
                throw new IllegalStateException("目标位置未生效");
            }
            owner.sendSystemMessage(Component.literal("已传送至 (" + x + ", 320, " + z + ")"));
        } catch (RuntimeException error) {
            for (ItemStack stack : payment) owner.getInventory().placeItemBackInInventory(stack);
            owner.sendSystemMessage(Component.literal("传送失败：" + error.getMessage() + "，物品已返还"));
        }
    }

    private Long distance(int[] indices, Map<Item, Integer> rules) {
        long total = 0;
        for (int index : indices) {
            ItemStack stack = container.getItem(index);
            if (!stack.isEmpty()) {
                Integer blocks = rules.get(stack.getItem());
                if (blocks == null) return null;
                total += (long) stack.getCount() * blocks;
            }
        }
        return total;
    }

    private void collect(int[] indices, List<ItemStack> payment) {
        for (int index : indices) {
            ItemStack stack = container.removeItemNoUpdate(index);
            if (!stack.isEmpty()) payment.add(stack);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int index : X_SLOTS) returnItem(index);
        for (int index : Z_SLOTS) returnItem(index);
        container.stopOpen(player);
    }

    private void returnItem(int index) {
        ItemStack stack = container.removeItemNoUpdate(index);
        if (!stack.isEmpty()) owner.getInventory().placeItemBackInInventory(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return player == owner && owner.isAlive();
    }
}
