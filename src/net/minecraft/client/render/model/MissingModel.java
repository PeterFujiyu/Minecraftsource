/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.EnumMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class MissingModel {
    private static final String KEY = "missing";
    private static final String TEXTURE_ID = "missingno";
    public static final Identifier ID = Identifier.ofVanilla("builtin/missing");
    public static final ModelIdentifier MODEL_ID = new ModelIdentifier(ID, "missing");

    public static UnbakedModel create() {
        ModelElementTexture lv = new ModelElementTexture(new float[]{0.0f, 0.0f, 16.0f, 16.0f}, 0);
        EnumMap<Direction, ModelElementFace> map = new EnumMap<Direction, ModelElementFace>(Direction.class);
        for (Direction lv2 : Direction.values()) {
            map.put(lv2, new ModelElementFace(lv2, -1, TEXTURE_ID, lv));
        }
        ModelElement lv3 = new ModelElement(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(16.0f, 16.0f, 16.0f), map);
        return new JsonUnbakedModel(null, List.of(lv3), new ModelTextures.Textures.Builder().addTextureReference("particle", TEXTURE_ID).addSprite(TEXTURE_ID, new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId())).build(), null, null, ModelTransformation.NONE);
    }
}

