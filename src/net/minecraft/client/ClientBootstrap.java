/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.tint.TintSourceTypes;

@Environment(value=EnvType.CLIENT)
public class ClientBootstrap {
    private static volatile boolean initialized;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        ItemModelTypes.bootstrap();
        SpecialModelTypes.bootstrap();
        TintSourceTypes.bootstrap();
        SelectProperties.bootstrap();
        BooleanProperties.bootstrap();
        NumericProperties.bootstrap();
    }
}

