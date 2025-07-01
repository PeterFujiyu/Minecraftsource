/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MusicInstance(@Nullable MusicSound music, float volume) {
    public MusicInstance(MusicSound music) {
        this(music, 1.0f);
    }

    public boolean shouldReplace(SoundInstance sound) {
        if (this.music == null) {
            return false;
        }
        return this.music.shouldReplaceCurrentMusic() && !this.music.getSound().value().id().equals(sound.getId());
    }

    @Nullable
    public MusicSound music() {
        return this.music;
    }
}

