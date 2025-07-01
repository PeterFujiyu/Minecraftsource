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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;

@Deprecated
@Environment(value=EnvType.CLIENT)
public abstract class AgeableMobEntityRenderer<T extends MobEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends MobEntityRenderer<T, S, M> {
    private final M adultModel;
    private final M babyModel;

    public AgeableMobEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float shadowRadius) {
        super(context, model, shadowRadius);
        this.adultModel = model;
        this.babyModel = babyModel;
    }

    @Override
    public void render(S arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        this.model = ((LivingEntityRenderState)arg).baby ? this.babyModel : this.adultModel;
        super.render(arg, arg2, arg3, i);
    }
}

