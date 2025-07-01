/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FindTreeTutorialStepHandler
implements TutorialStepHandler {
    private static final int DELAY = 6000;
    private static final Text TITLE = Text.translatable("tutorial.find_tree.title");
    private static final Text DESCRIPTION = Text.translatable("tutorial.find_tree.description");
    private final TutorialManager manager;
    @Nullable
    private TutorialToast toast;
    private int ticks;

    public FindTreeTutorialStepHandler(TutorialManager manager) {
        this.manager = manager;
    }

    @Override
    public void tick() {
        ClientPlayerEntity lv2;
        ++this.ticks;
        if (!this.manager.isInSurvival()) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        MinecraftClient lv = this.manager.getClient();
        if (this.ticks == 1 && (lv2 = lv.player) != null && (FindTreeTutorialStepHandler.hasItem(lv2) || FindTreeTutorialStepHandler.hasBrokenTreeBlocks(lv2))) {
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(lv.textRenderer, TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
            lv.getToastManager().add(this.toast);
        }
    }

    @Override
    public void destroy() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onTarget(ClientWorld world, HitResult hitResult) {
        BlockState lv;
        if (hitResult.getType() == HitResult.Type.BLOCK && (lv = world.getBlockState(((BlockHitResult)hitResult).getBlockPos())).isIn(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            this.manager.setStep(TutorialStep.PUNCH_TREE);
        }
    }

    @Override
    public void onSlotUpdate(ItemStack stack) {
        if (stack.isIn(ItemTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
        }
    }

    private static boolean hasItem(ClientPlayerEntity player) {
        return player.getInventory().containsAny(stack -> stack.isIn(ItemTags.COMPLETES_FIND_TREE_TUTORIAL));
    }

    public static boolean hasBrokenTreeBlocks(ClientPlayerEntity player) {
        for (RegistryEntry<Block> lv : Registries.BLOCK.iterateEntries(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            Block lv2 = lv.value();
            if (player.getStatHandler().getStat(Stats.MINED.getOrCreateStat(lv2)) <= 0) continue;
            return true;
        }
        return false;
    }
}

