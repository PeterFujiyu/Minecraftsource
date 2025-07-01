/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
    private final Map<Identifier, Entry> entries = Maps.newHashMap();
    private int tick;

    public boolean isCoolingDown(ItemStack stack) {
        return this.getCooldownProgress(stack, 0.0f) > 0.0f;
    }

    public float getCooldownProgress(ItemStack stack, float tickDelta) {
        Identifier lv = this.getGroup(stack);
        Entry lv2 = this.entries.get(lv);
        if (lv2 != null) {
            float g = lv2.endTick - lv2.startTick;
            float h = (float)lv2.endTick - ((float)this.tick + tickDelta);
            return MathHelper.clamp(h / g, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void update() {
        ++this.tick;
        if (!this.entries.isEmpty()) {
            Iterator<Map.Entry<Identifier, Entry>> iterator = this.entries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Entry> entry = iterator.next();
                if (entry.getValue().endTick > this.tick) continue;
                iterator.remove();
                this.onCooldownUpdate(entry.getKey());
            }
        }
    }

    public Identifier getGroup(ItemStack stack) {
        UseCooldownComponent lv = stack.get(DataComponentTypes.USE_COOLDOWN);
        Identifier lv2 = Registries.ITEM.getId(stack.getItem());
        if (lv == null) {
            return lv2;
        }
        return lv.cooldownGroup().orElse(lv2);
    }

    public void set(ItemStack stack, int duration) {
        this.set(this.getGroup(stack), duration);
    }

    public void set(Identifier groupId, int duration) {
        this.entries.put(groupId, new Entry(this.tick, this.tick + duration));
        this.onCooldownUpdate(groupId, duration);
    }

    public void remove(Identifier groupId) {
        this.entries.remove(groupId);
        this.onCooldownUpdate(groupId);
    }

    protected void onCooldownUpdate(Identifier groupId, int duration) {
    }

    protected void onCooldownUpdate(Identifier groupId) {
    }

    record Entry(int startTick, int endTick) {
    }
}

