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
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class UndeadHorseEntityRenderer
extends AbstractHorseEntityRenderer<AbstractHorseEntity, LivingHorseEntityRenderState, AbstractHorseEntityModel<LivingHorseEntityRenderState>> {
    private static final Identifier ZOMBIE_HORSE_TEXTURE = Identifier.ofVanilla("textures/entity/horse/horse_zombie.png");
    private static final Identifier SKELETON_HORSE_TEXTURE = Identifier.ofVanilla("textures/entity/horse/horse_skeleton.png");
    private final Identifier texture;

    public UndeadHorseEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer babyLayer, boolean skeleton) {
        super(ctx, new HorseEntityModel(ctx.getPart(layer)), new HorseEntityModel(ctx.getPart(babyLayer)));
        this.texture = skeleton ? SKELETON_HORSE_TEXTURE : ZOMBIE_HORSE_TEXTURE;
    }

    @Override
    public Identifier getTexture(LivingHorseEntityRenderState arg) {
        return this.texture;
    }

    @Override
    public LivingHorseEntityRenderState createRenderState() {
        return new LivingHorseEntityRenderState();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((LivingHorseEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

