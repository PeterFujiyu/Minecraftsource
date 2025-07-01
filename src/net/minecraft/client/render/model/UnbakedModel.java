/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelNameSupplier;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface UnbakedModel
extends ResolvableModel {
    public static final boolean DEFAULT_AMBIENT_OCCLUSION = true;
    public static final GuiLight DEFAULT_GUI_LIGHT = GuiLight.BLOCK;

    public BakedModel bake(ModelTextures var1, Baker var2, ModelBakeSettings var3, boolean var4, boolean var5, ModelTransformation var6);

    @Nullable
    default public Boolean getAmbientOcclusion() {
        return null;
    }

    @Nullable
    default public GuiLight getGuiLight() {
        return null;
    }

    @Nullable
    default public ModelTransformation getTransformation() {
        return null;
    }

    default public ModelTextures.Textures getTextures() {
        return ModelTextures.Textures.EMPTY;
    }

    @Nullable
    default public UnbakedModel getParent() {
        return null;
    }

    public static BakedModel bake(UnbakedModel model, Baker baker, ModelBakeSettings settings) {
        ModelTextures lv = UnbakedModel.buildTextures(model, baker.getModelNameSupplier());
        boolean bl = UnbakedModel.getAmbientOcclusion(model);
        boolean bl2 = UnbakedModel.getGuiLight(model).isSide();
        ModelTransformation lv2 = UnbakedModel.getTransformations(model);
        return model.bake(lv, baker, settings, bl, bl2, lv2);
    }

    public static ModelTextures buildTextures(UnbakedModel model, ModelNameSupplier modelNameSupplier) {
        ModelTextures.Builder lv = new ModelTextures.Builder();
        while (model != null) {
            lv.addLast(model.getTextures());
            model = model.getParent();
        }
        return lv.build(modelNameSupplier);
    }

    public static boolean getAmbientOcclusion(UnbakedModel model) {
        while (model != null) {
            Boolean boolean_ = model.getAmbientOcclusion();
            if (boolean_ != null) {
                return boolean_;
            }
            model = model.getParent();
        }
        return true;
    }

    public static GuiLight getGuiLight(UnbakedModel model) {
        while (model != null) {
            GuiLight lv = model.getGuiLight();
            if (lv != null) {
                return lv;
            }
            model = model.getParent();
        }
        return DEFAULT_GUI_LIGHT;
    }

    public static Transformation getTransformation(UnbakedModel model, ModelTransformationMode displayContext) {
        while (model != null) {
            Transformation lv2;
            ModelTransformation lv = model.getTransformation();
            if (lv != null && (lv2 = lv.getTransformation(displayContext)) != Transformation.IDENTITY) {
                return lv2;
            }
            model = model.getParent();
        }
        return Transformation.IDENTITY;
    }

    public static ModelTransformation getTransformations(UnbakedModel model) {
        Transformation lv = UnbakedModel.getTransformation(model, ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
        Transformation lv2 = UnbakedModel.getTransformation(model, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
        Transformation lv3 = UnbakedModel.getTransformation(model, ModelTransformationMode.FIRST_PERSON_LEFT_HAND);
        Transformation lv4 = UnbakedModel.getTransformation(model, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND);
        Transformation lv5 = UnbakedModel.getTransformation(model, ModelTransformationMode.HEAD);
        Transformation lv6 = UnbakedModel.getTransformation(model, ModelTransformationMode.GUI);
        Transformation lv7 = UnbakedModel.getTransformation(model, ModelTransformationMode.GROUND);
        Transformation lv8 = UnbakedModel.getTransformation(model, ModelTransformationMode.FIXED);
        return new ModelTransformation(lv, lv2, lv3, lv4, lv5, lv6, lv7, lv8);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GuiLight {
        ITEM("front"),
        BLOCK("side");

        private final String name;

        private GuiLight(String name) {
            this.name = name;
        }

        public static GuiLight byName(String value) {
            for (GuiLight lv : GuiLight.values()) {
                if (!lv.name.equals(value)) continue;
                return lv;
            }
            throw new IllegalArgumentException("Invalid gui light: " + value);
        }

        public boolean isSide() {
            return this == BLOCK;
        }
    }
}

