/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class IllusionerEntityRenderer
extends IllagerEntityRenderer<IllusionerEntity, IllusionerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/illusioner.png");

    public IllusionerEntityRenderer(EntityRendererFactory.Context arg) {
        super(arg, new IllagerEntityModel(arg.getPart(EntityModelLayers.ILLUSIONER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllusionerEntityRenderState, IllagerEntityModel<IllusionerEntityRenderState>>(this, (FeatureRendererContext)this){

            @Override
            public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, IllusionerEntityRenderState arg3, float f, float g) {
                if (arg3.spellcasting || arg3.attacking) {
                    super.render(arg, arg2, i, arg3, f, g);
                }
            }
        });
        ((IllagerEntityModel)this.model).getHat().visible = true;
    }

    @Override
    public Identifier getTexture(IllusionerEntityRenderState arg) {
        return TEXTURE;
    }

    @Override
    public IllusionerEntityRenderState createRenderState() {
        return new IllusionerEntityRenderState();
    }

    @Override
    public void updateRenderState(IllusionerEntity arg, IllusionerEntityRenderState arg2, float f) {
        super.updateRenderState(arg, arg2, f);
        Vec3d[] lvs = arg.getMirrorCopyOffsets(f);
        arg2.mirrorCopyOffsets = Arrays.copyOf(lvs, lvs.length);
        arg2.spellcasting = arg.isSpellcasting();
    }

    @Override
    public void render(IllusionerEntityRenderState arg, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        if (arg.invisible) {
            Vec3d[] lvs = arg.mirrorCopyOffsets;
            for (int j = 0; j < lvs.length; ++j) {
                arg2.push();
                arg2.translate(lvs[j].x + (double)MathHelper.cos((float)j + arg.age * 0.5f) * 0.025, lvs[j].y + (double)MathHelper.cos((float)j + arg.age * 0.75f) * 0.0125, lvs[j].z + (double)MathHelper.cos((float)j + arg.age * 0.7f) * 0.025);
                super.render(arg, arg2, arg3, i);
                arg2.pop();
            }
        } else {
            super.render(arg, arg2, arg3, i);
        }
    }

    @Override
    protected boolean isVisible(IllusionerEntityRenderState arg) {
        return true;
    }

    @Override
    protected Box getBoundingBox(IllusionerEntity arg) {
        return super.getBoundingBox(arg).expand(3.0, 0.0, 3.0);
    }

    @Override
    protected /* synthetic */ boolean isVisible(LivingEntityRenderState state) {
        return this.isVisible((IllusionerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IllusionerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

