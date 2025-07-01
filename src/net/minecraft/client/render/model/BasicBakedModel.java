/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BasicBakedModel
implements BakedModel {
    public static final String PARTICLE_TEXTURE_ID = "particle";
    private final List<BakedQuad> quads;
    private final Map<Direction, List<BakedQuad>> faceQuads;
    private final boolean usesAo;
    private final boolean hasDepth;
    private final boolean isSideLit;
    private final Sprite sprite;
    private final ModelTransformation transformation;

    public BasicBakedModel(List<BakedQuad> quads, Map<Direction, List<BakedQuad>> faceQuads, boolean usesAo, boolean isSideLit, boolean hasDepth, Sprite sprite, ModelTransformation transformation) {
        this.quads = quads;
        this.faceQuads = faceQuads;
        this.usesAo = usesAo;
        this.hasDepth = hasDepth;
        this.isSideLit = isSideLit;
        this.sprite = sprite;
        this.transformation = transformation;
    }

    public static BakedModel bake(List<ModelElement> elements, ModelTextures textures, SpriteGetter spriteGetter, ModelBakeSettings settings, boolean ambientOcclusion, boolean isSideLit, boolean hasDepth, ModelTransformation transformation) {
        Sprite lv = BasicBakedModel.getSprite(spriteGetter, textures, PARTICLE_TEXTURE_ID);
        Builder lv2 = new Builder(ambientOcclusion, isSideLit, hasDepth, transformation).setParticle(lv);
        for (ModelElement lv3 : elements) {
            for (Direction lv4 : lv3.faces.keySet()) {
                ModelElementFace lv5 = lv3.faces.get(lv4);
                Sprite lv6 = BasicBakedModel.getSprite(spriteGetter, textures, lv5.textureId());
                if (lv5.cullFace() == null) {
                    lv2.addQuad(BasicBakedModel.bake(lv3, lv5, lv6, lv4, settings));
                    continue;
                }
                lv2.addQuad(Direction.transform(settings.getRotation().getMatrix(), lv5.cullFace()), BasicBakedModel.bake(lv3, lv5, lv6, lv4, settings));
            }
        }
        return lv2.build();
    }

    private static BakedQuad bake(ModelElement element, ModelElementFace face, Sprite sprite, Direction direction, ModelBakeSettings settings) {
        return BakedQuadFactory.bake(element.from, element.to, face, sprite, direction, settings, element.rotation, element.shade, element.lightEmission);
    }

    private static Sprite getSprite(SpriteGetter spriteGetter, ModelTextures textures, String textureId) {
        SpriteIdentifier lv = textures.get(textureId);
        return lv != null ? spriteGetter.get(lv) : spriteGetter.getMissing(textureId);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return face == null ? this.quads : this.faceQuads.get(face);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.usesAo;
    }

    @Override
    public boolean hasDepth() {
        return this.hasDepth;
    }

    @Override
    public boolean isSideLit() {
        return this.isSideLit;
    }

    @Override
    public Sprite getParticleSprite() {
        return this.sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.transformation;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        private final EnumMap<Direction, ImmutableList.Builder<BakedQuad>> faceQuads = Maps.newEnumMap(Direction.class);
        private final boolean usesAo;
        @Nullable
        private Sprite particleTexture;
        private final boolean isSideLit;
        private final boolean hasDepth;
        private final ModelTransformation transformation;

        public Builder(boolean usesAo, boolean isSideLit, boolean hasDepth, ModelTransformation transformation) {
            this.usesAo = usesAo;
            this.isSideLit = isSideLit;
            this.hasDepth = hasDepth;
            this.transformation = transformation;
            for (Direction lv : Direction.values()) {
                this.faceQuads.put(lv, ImmutableList.builder());
            }
        }

        public Builder addQuad(Direction side, BakedQuad quad) {
            this.faceQuads.get(side).add((Object)quad);
            return this;
        }

        public Builder addQuad(BakedQuad quad) {
            this.quads.add((Object)quad);
            return this;
        }

        public Builder setParticle(Sprite sprite) {
            this.particleTexture = sprite;
            return this;
        }

        public Builder method_35809() {
            return this;
        }

        public BakedModel build() {
            if (this.particleTexture == null) {
                throw new RuntimeException("Missing particle!");
            }
            Map map = Maps.transformValues(this.faceQuads, ImmutableList.Builder::build);
            return new BasicBakedModel((List<BakedQuad>)((Object)this.quads.build()), new EnumMap<Direction, List<BakedQuad>>(map), this.usesAo, this.isSideLit, this.hasDepth, this.particleTexture, this.transformation);
        }
    }
}

