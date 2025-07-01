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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MusicTracker {
    private static final int DEFAULT_TIME_UNTIL_NEXT_SONG = 100;
    private final Random random = Random.create();
    private final MinecraftClient client;
    @Nullable
    private SoundInstance current;
    private float volume = 1.0f;
    private int timeUntilNextSong = 100;

    public MusicTracker(MinecraftClient client) {
        this.client = client;
    }

    public void tick() {
        boolean bl;
        MusicInstance lv = this.client.getMusicInstance();
        float f = lv.volume();
        if (this.current != null && this.volume != f && !(bl = this.canFadeTowardsVolume(f))) {
            return;
        }
        MusicSound lv2 = lv.music();
        if (lv2 == null) {
            this.timeUntilNextSong = Math.max(this.timeUntilNextSong, 100);
            return;
        }
        if (this.current != null) {
            if (lv.shouldReplace(this.current)) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, lv2.getMinDelay() / 2);
            }
            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong, MathHelper.nextInt(this.random, lv2.getMinDelay(), lv2.getMaxDelay()));
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, lv2.getMaxDelay());
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            this.play(lv);
        }
    }

    public void play(MusicInstance music) {
        this.current = PositionedSoundInstance.music(music.music().getSound().value());
        if (this.current.getSound() != SoundManager.MISSING_SOUND) {
            this.client.getSoundManager().play(this.current);
            this.client.getSoundManager().setVolume(this.current, music.volume());
        }
        this.timeUntilNextSong = Integer.MAX_VALUE;
        this.volume = music.volume();
    }

    public void stop(MusicSound type) {
        if (this.isPlayingType(type)) {
            this.stop();
        }
    }

    public void stop() {
        if (this.current != null) {
            this.client.getSoundManager().stop(this.current);
            this.current = null;
        }
        this.timeUntilNextSong += 100;
    }

    private boolean canFadeTowardsVolume(float volume) {
        if (this.current == null) {
            return false;
        }
        if (this.volume == volume) {
            return true;
        }
        if (this.volume < volume) {
            this.volume += MathHelper.clamp(this.volume, 5.0E-4f, 0.005f);
            if (this.volume > volume) {
                this.volume = volume;
            }
        } else {
            this.volume = 0.03f * volume + 0.97f * this.volume;
            if (Math.abs(this.volume - volume) < 1.0E-4f || this.volume < volume) {
                this.volume = volume;
            }
        }
        this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
        if (this.volume <= 1.0E-4f) {
            this.stop();
            return false;
        }
        this.client.getSoundManager().setVolume(this.current, this.volume);
        return true;
    }

    public boolean isPlayingType(MusicSound type) {
        if (this.current == null) {
            return false;
        }
        return type.getSound().value().id().equals(this.current.getId());
    }
}

