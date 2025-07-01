/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public sealed interface ActionResult {
    public static final Success SUCCESS = new Success(SwingSource.CLIENT, ItemContext.KEEP_HAND_STACK);
    public static final Success SUCCESS_SERVER = new Success(SwingSource.SERVER, ItemContext.KEEP_HAND_STACK);
    public static final Success CONSUME = new Success(SwingSource.NONE, ItemContext.KEEP_HAND_STACK);
    public static final Fail FAIL = new Fail();
    public static final Pass PASS = new Pass();
    public static final PassToDefaultBlockAction PASS_TO_DEFAULT_BLOCK_ACTION = new PassToDefaultBlockAction();

    default public boolean isAccepted() {
        return false;
    }

    public record Success(SwingSource swingSource, ItemContext itemContext) implements ActionResult
    {
        @Override
        public boolean isAccepted() {
            return true;
        }

        public Success withNewHandStack(ItemStack newHandStack) {
            return new Success(this.swingSource, new ItemContext(true, newHandStack));
        }

        public Success noIncrementStat() {
            return new Success(this.swingSource, ItemContext.KEEP_HAND_STACK_NO_INCREMENT_STAT);
        }

        public boolean shouldIncrementStat() {
            return this.itemContext.incrementStat;
        }

        @Nullable
        public ItemStack getNewHandStack() {
            return this.itemContext.newHandStack;
        }
    }

    public static enum SwingSource {
        NONE,
        CLIENT,
        SERVER;

    }

    public record ItemContext(boolean incrementStat, @Nullable ItemStack newHandStack) {
        static ItemContext KEEP_HAND_STACK_NO_INCREMENT_STAT = new ItemContext(false, null);
        static ItemContext KEEP_HAND_STACK = new ItemContext(true, null);

        @Nullable
        public ItemStack newHandStack() {
            return this.newHandStack;
        }
    }

    public record Fail() implements ActionResult
    {
    }

    public record Pass() implements ActionResult
    {
    }

    public record PassToDefaultBlockAction() implements ActionResult
    {
    }
}

