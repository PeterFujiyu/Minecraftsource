/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ModelIdentifier(Identifier id, String variant) {
    public ModelIdentifier {
        variant = ModelIdentifier.toLowerCase(variant);
    }

    private static String toLowerCase(String string) {
        return string.toLowerCase(Locale.ROOT);
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id) + "#" + this.variant;
    }
}

