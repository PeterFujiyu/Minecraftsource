/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;

public record ResourceMetadataSerializer<T>(String name, Codec<T> codec) {
}

