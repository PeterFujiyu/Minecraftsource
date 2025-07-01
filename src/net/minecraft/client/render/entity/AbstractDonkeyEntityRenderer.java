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
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AbstractDonkeyEntityRenderer<T extends AbstractDonkeyEntity>
extends AbstractHorseEntityRenderer<T, DonkeyEntityRenderState, DonkeyEntityModel> {
    public static final Identifier DONKEY_TEXTURE = Identifier.ofVanilla("textures/entity/horse/donkey.png");
    public static final Identifier MULE_TEXTURE = Identifier.ofVanilla("textures/entity/horse/mule.png");
    private final Identifier texture;

    public AbstractDonkeyEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer, boolean mule) {
        super(context, new DonkeyEntityModel(context.getPart(layer)), new DonkeyEntityModel(context.getPart(babyLayer)));
        this.texture = mule ? MULE_TEXTURE : DONKEY_TEXTURE;
    }

    @Override
    public Identifier getTexture(DonkeyEntityRenderState arg) {
        return this.texture;
    }

    @Override
    public DonkeyEntityRenderState createRenderState() {
        return new DonkeyEntityRenderState();
    }

    @Override
    public void updateRenderState(T arg, DonkeyEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        arg2.hasChest = ((AbstractDonkeyEntity)arg).hasChest();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((DonkeyEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

