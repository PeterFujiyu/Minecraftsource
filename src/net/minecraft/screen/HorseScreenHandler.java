/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class HorseScreenHandler
extends ScreenHandler {
    static final Identifier EMPTY_SADDLE_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/saddle");
    private static final Identifier EMPTY_LLAMA_ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/llama_armor");
    private static final Identifier EMPTY_HORSE_ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/horse_armor");
    private final Inventory inventory;
    private final Inventory horseArmorInventory;
    private final AbstractHorseEntity entity;
    private static final int field_48835 = 1;
    private static final int field_48836 = 2;

    public HorseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, final AbstractHorseEntity entity, int slotColumnCount) {
        super(null, syncId);
        this.inventory = inventory;
        this.horseArmorInventory = entity.getArmorInventory();
        this.entity = entity;
        inventory.onOpen(playerInventory.player);
        this.addSlot(new Slot(this, inventory, 0, 8, 18){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.SADDLE) && !this.hasStack() && entity.canBeSaddled();
            }

            @Override
            public boolean isEnabled() {
                return entity.canBeSaddled();
            }

            @Override
            public Identifier getBackgroundSprite() {
                return EMPTY_SADDLE_SLOT_TEXTURE;
            }
        });
        Identifier lv = entity instanceof LlamaEntity ? EMPTY_LLAMA_ARMOR_SLOT_TEXTURE : EMPTY_HORSE_ARMOR_SLOT_TEXTURE;
        this.addSlot(new ArmorSlot(this, this.horseArmorInventory, entity, EquipmentSlot.BODY, 0, 8, 36, lv){

            @Override
            public boolean canInsert(ItemStack stack) {
                return entity.canEquip(stack, EquipmentSlot.BODY);
            }

            @Override
            public boolean isEnabled() {
                return entity.canUseSlot(EquipmentSlot.BODY);
            }
        });
        if (slotColumnCount > 0) {
            for (int k = 0; k < 3; ++k) {
                for (int l = 0; l < slotColumnCount; ++l) {
                    this.addSlot(new Slot(inventory, 1 + l + k * slotColumnCount, 80 + l * 18, 18 + k * 18));
                }
            }
        }
        this.addPlayerSlots(playerInventory, 8, 84);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !this.entity.areInventoriesDifferent(this.inventory) && this.inventory.canPlayerUse(player) && this.horseArmorInventory.canPlayerUse(player) && this.entity.isAlive() && player.canInteractWithEntity(this.entity, 4.0);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(slot);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            int j = this.inventory.size() + 1;
            if (slot < j) {
                if (!this.insertItem(lv3, j, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(lv3) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(lv3, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(lv3)) {
                if (!this.insertItem(lv3, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (j <= 1 || !this.insertItem(lv3, 2, j, false)) {
                int l;
                int k = j;
                int m = l = k + 27;
                int n = m + 9;
                if (slot >= m && slot < n ? !this.insertItem(lv3, k, l, false) : (slot >= k && slot < l ? !this.insertItem(lv3, m, n, false) : !this.insertItem(lv3, m, l, false))) {
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            } else {
                lv2.markDirty();
            }
        }
        return lv;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}

