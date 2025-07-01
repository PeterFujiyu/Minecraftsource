/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MapColor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class MapTextureManager
implements AutoCloseable {
    private final Int2ObjectMap<MapTexture> texturesByMapId = new Int2ObjectOpenHashMap<MapTexture>();
    final TextureManager textureManager;

    public MapTextureManager(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void setNeedsUpdate(MapIdComponent mapIdComponent, MapState mapState) {
        this.getMapTexture(mapIdComponent, mapState).setNeedsUpdate();
    }

    public Identifier getTextureId(MapIdComponent mapIdComponent, MapState mapState) {
        MapTexture lv = this.getMapTexture(mapIdComponent, mapState);
        lv.updateTexture();
        return lv.textureId;
    }

    public void clear() {
        for (MapTexture lv : this.texturesByMapId.values()) {
            lv.close();
        }
        this.texturesByMapId.clear();
    }

    private MapTexture getMapTexture(MapIdComponent mapIdComponent, MapState MapState2) {
        return this.texturesByMapId.compute(mapIdComponent.id(), (id, mapTexture) -> {
            if (mapTexture == null) {
                return new MapTexture(this, (int)id, MapState2);
            }
            mapTexture.setState(MapState2);
            return mapTexture;
        });
    }

    @Override
    public void close() {
        this.clear();
    }

    @Environment(value=EnvType.CLIENT)
    class MapTexture
    implements AutoCloseable {
        private MapState state;
        private final NativeImageBackedTexture texture;
        private boolean needsUpdate = true;
        final Identifier textureId;

        MapTexture(MapTextureManager arg, int id, MapState state) {
            this.state = state;
            this.texture = new NativeImageBackedTexture(128, 128, true);
            this.textureId = Identifier.ofVanilla("map/" + id);
            arg.textureManager.registerTexture(this.textureId, this.texture);
        }

        void setState(MapState state) {
            boolean bl = this.state != state;
            this.state = state;
            this.needsUpdate |= bl;
        }

        public void setNeedsUpdate() {
            this.needsUpdate = true;
        }

        void updateTexture() {
            if (this.needsUpdate) {
                NativeImage lv = this.texture.getImage();
                if (lv != null) {
                    for (int i = 0; i < 128; ++i) {
                        for (int j = 0; j < 128; ++j) {
                            int k = j + i * 128;
                            lv.setColorArgb(j, i, MapColor.getRenderColor(this.state.colors[k]));
                        }
                    }
                }
                this.texture.upload();
                this.needsUpdate = false;
            }
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }
}

