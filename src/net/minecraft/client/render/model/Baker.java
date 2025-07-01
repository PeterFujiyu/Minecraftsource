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
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.Debug;

@Environment(value=EnvType.CLIENT)
public interface Baker {
    public BakedModel bake(Identifier var1, ModelBakeSettings var2);

    public SpriteGetter getSpriteGetter();

    @Debug
    public ModelNameSupplier getModelNameSupplier();
}

