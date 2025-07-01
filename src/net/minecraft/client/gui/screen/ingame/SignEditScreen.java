/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends AbstractSignEditScreen {
    public static final float BACKGROUND_SCALE = 62.500004f;
    public static final float TEXT_SCALE_MULTIPLIER = 0.9765628f;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628f, 0.9765628f, 0.9765628f);
    @Nullable
    private Model model;

    public SignEditScreen(SignBlockEntity sign, boolean filtered, boolean bl2) {
        super(sign, filtered, bl2);
    }

    @Override
    protected void init() {
        super.init();
        boolean bl = this.blockEntity.getCachedState().getBlock() instanceof SignBlock;
        this.model = SignBlockEntityRenderer.createSignModel(this.client.getLoadedEntityModels(), this.signType, bl);
    }

    @Override
    protected void translateForRender(DrawContext context, BlockState state) {
        super.translateForRender(context, state);
        boolean bl = state.getBlock() instanceof SignBlock;
        if (!bl) {
            context.getMatrices().translate(0.0f, 35.0f, 0.0f);
        }
    }

    @Override
    protected void renderSignBackground(DrawContext context) {
        if (this.model == null) {
            return;
        }
        context.getMatrices().translate(0.0f, 31.0f, 0.0f);
        context.getMatrices().scale(62.500004f, 62.500004f, -62.500004f);
        context.draw(vertexConsumers -> {
            SpriteIdentifier lv = TexturedRenderLayers.getSignTextureId(this.signType);
            VertexConsumer lv2 = lv.getVertexConsumer((VertexConsumerProvider)vertexConsumers, this.model::getLayer);
            this.model.render(context.getMatrices(), lv2, 0xF000F0, OverlayTexture.DEFAULT_UV);
        });
    }

    @Override
    protected Vector3f getTextScale() {
        return TEXT_SCALE;
    }
}

