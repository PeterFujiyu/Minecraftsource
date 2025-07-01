/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PufferfishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PufferfishEntityRenderer
extends MobEntityRenderer<PufferfishEntity, PufferfishEntityRenderState, EntityModel<EntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/pufferfish.png");
    private final EntityModel<EntityRenderState> smallModel;
    private final EntityModel<EntityRenderState> mediumModel;
    private final EntityModel<EntityRenderState> largeModel = this.getModel();

    public PufferfishEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new LargePufferfishEntityModel(arg.getPart(EntityModelLayers.PUFFERFISH_BIG)), 0.2f);
        this.mediumModel = new MediumPufferfishEntityModel(arg.getPart(EntityModelLayers.PUFFERFISH_MEDIUM));
        this.smallModel = new SmallPufferfishEntityModel(arg.getPart(EntityModelLayers.PUFFERFISH_SMALL));
    }

    @Override
    public Identifier getTexture(PufferfishEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public PufferfishEntityRenderState createRenderState() {
        return new PufferfishEntityRenderState();
    }

    @Override
    protected float getShadowRadius(PufferfishEntityRenderState arg) {
        return 0.1f + 0.1f * (float)arg.puffState;
    }

    @Override
    public void render(PufferfishEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.model = switch (arg.puffState) {
            case 0 -> this.smallModel;
            case 1 -> this.mediumModel;
            default -> this.largeModel;
        };
        super.render(arg, arg2, arg3, i);
    }

    @Override
    public void updateRenderState(PufferfishEntity arg, PufferfishEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.puffState = arg.getPuffState();
    }

    @Override
    protected void setupTransforms(PufferfishEntityRenderState arg, MatrixStack arg2, float f, float g) {
        arg2.translate(0.0f, MathHelper.cos(arg.age * 0.05f) * 0.08f, 0.0f);
        super.setupTransforms(arg, arg2, f, g);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState arg) {
        return this.getShadowRadius((PufferfishEntityRenderState)arg);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PufferfishEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((PufferfishEntityRenderState)state);
    }
}

