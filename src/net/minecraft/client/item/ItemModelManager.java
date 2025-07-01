/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemModelManager {
    private final Function<Identifier, ItemModel> modelGetter = bakedModelManager::getItemModel;
    private final Function<Identifier, ItemAsset.Properties> propertiesGetter = bakedModelManager::getItemProperties;

    public ItemModelManager(BakedModelManager bakedModelManager) {
    }

    public void updateForLivingEntity(ItemRenderState renderState, ItemStack stack, ModelTransformationMode transformationMode, boolean leftHand, LivingEntity entity) {
        this.update(renderState, stack, transformationMode, leftHand, entity.getWorld(), entity, entity.getId() + transformationMode.ordinal());
    }

    public void updateForNonLivingEntity(ItemRenderState renderState, ItemStack stack, ModelTransformationMode transformationMode, Entity entity) {
        this.update(renderState, stack, transformationMode, false, entity.getWorld(), null, entity.getId());
    }

    public void update(ItemRenderState renderState, ItemStack stack, ModelTransformationMode transformationMode, boolean leftHand, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        renderState.clear();
        if (!stack.isEmpty()) {
            renderState.modelTransformationMode = transformationMode;
            renderState.leftHand = leftHand;
            this.update(renderState, stack, transformationMode, world, entity, seed);
        }
    }

    private static void resolveProfileComponent(ItemStack stack) {
        ProfileComponent lv2;
        BlockItem lv;
        Item item = stack.getItem();
        if (item instanceof BlockItem && (lv = (BlockItem)item).getBlock() instanceof AbstractSkullBlock && (lv2 = stack.get(DataComponentTypes.PROFILE)) != null && !lv2.isCompleted()) {
            stack.remove(DataComponentTypes.PROFILE);
            lv2.getFuture().thenAcceptAsync(profile -> stack.set(DataComponentTypes.PROFILE, profile), (Executor)MinecraftClient.getInstance());
        }
    }

    public void update(ItemRenderState renderState, ItemStack stack, ModelTransformationMode transformationMode, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        ClientWorld lv2;
        ItemModelManager.resolveProfileComponent(stack);
        Identifier lv = stack.get(DataComponentTypes.ITEM_MODEL);
        if (lv == null) {
            return;
        }
        this.modelGetter.apply(lv).update(renderState, stack, this, transformationMode, world instanceof ClientWorld ? (lv2 = (ClientWorld)world) : null, entity, seed);
    }

    public boolean hasHandAnimationOnSwap(ItemStack stack) {
        Identifier lv = stack.get(DataComponentTypes.ITEM_MODEL);
        if (lv == null) {
            return true;
        }
        return this.propertiesGetter.apply(lv).handAnimationOnSwap();
    }
}

