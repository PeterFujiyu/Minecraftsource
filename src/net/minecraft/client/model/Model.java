/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public abstract class Model {
    private static final Vector3f ANIMATION_VEC = new Vector3f();
    protected final ModelPart root;
    protected final Function<Identifier, RenderLayer> layerFactory;
    private final List<ModelPart> parts;

    public Model(ModelPart root, Function<Identifier, RenderLayer> layerFactory) {
        this.root = root;
        this.layerFactory = layerFactory;
        this.parts = root.traverse().toList();
    }

    public final RenderLayer getLayer(Identifier texture) {
        return this.layerFactory.apply(texture);
    }

    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.getRootPart().render(matrices, vertices, light, overlay, color);
    }

    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, -1);
    }

    public final ModelPart getRootPart() {
        return this.root;
    }

    public Optional<ModelPart> getPart(String name) {
        if (name.equals("root")) {
            return Optional.of(this.getRootPart());
        }
        return this.getRootPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(partx -> partx.getChild(name));
    }

    public final List<ModelPart> getParts() {
        return this.parts;
    }

    public final void resetTransforms() {
        for (ModelPart lv : this.parts) {
            lv.resetTransform();
        }
    }

    protected void animate(AnimationState animationState, Animation animation, float age) {
        this.animate(animationState, animation, age, 1.0f);
    }

    protected void animateWalking(Animation animation, float limbFrequency, float limbAmplitudeModifier, float h, float i) {
        long l = (long)(limbFrequency * 50.0f * h);
        float j = Math.min(limbAmplitudeModifier * i, 1.0f);
        AnimationHelper.animate(this, animation, l, j, ANIMATION_VEC);
    }

    protected void animate(AnimationState animationState, Animation animation, float age, float speedMultiplier) {
        animationState.run(state -> AnimationHelper.animate(this, animation, (long)((float)state.getTimeInMilliseconds(age) * speedMultiplier), 1.0f, ANIMATION_VEC));
    }

    protected void animate(Animation animation) {
        AnimationHelper.animate(this, animation, 0L, 1.0f, ANIMATION_VEC);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SinglePartModel
    extends Model {
        public SinglePartModel(ModelPart part, Function<Identifier, RenderLayer> layerFactory) {
            super(part, layerFactory);
        }
    }
}

