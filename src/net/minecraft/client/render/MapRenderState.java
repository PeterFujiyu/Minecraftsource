/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MapRenderState {
    @Nullable
    public Identifier texture;
    public final List<Decoration> decorations = new ArrayList<Decoration>();

    @Environment(value=EnvType.CLIENT)
    public static class Decoration {
        @Nullable
        public Sprite sprite;
        public byte x;
        public byte z;
        public byte rotation;
        public boolean alwaysRendered;
        @Nullable
        public Text name;
    }
}

