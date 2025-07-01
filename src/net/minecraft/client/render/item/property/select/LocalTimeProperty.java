/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.select;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LocalTimeProperty
implements SelectProperty<String> {
    public static final String DEFAULT_FORMATTED_TIME = "";
    private static final long MILLIS_PER_SECOND = TimeUnit.SECONDS.toMillis(1L);
    private static final Codec<TimeZone> TIME_ZONE_CODEC = Codec.STRING.comapFlatMap(timeZone -> {
        TimeZone timeZone2 = TimeZone.getTimeZone(timeZone);
        if (timeZone2.equals(TimeZone.UNKNOWN_ZONE)) {
            return DataResult.error(() -> "Unknown timezone: " + timeZone);
        }
        return DataResult.success(timeZone2);
    }, TimeZone::getID);
    private static final MapCodec<Data> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("pattern")).forGetter(data -> data.format), Codec.STRING.optionalFieldOf("locale", DEFAULT_FORMATTED_TIME).forGetter(data -> data.localeId), TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter(data -> data.timeZone)).apply((Applicative<Data, ?>)instance, Data::new));
    public static final SelectProperty.Type<LocalTimeProperty, String> TYPE = SelectProperty.Type.create(DATA_CODEC.flatXmap(LocalTimeProperty::validate, property -> DataResult.success(property.data)), Codec.STRING);
    private final Data data;
    private final DateFormat dateFormat;
    private long nextUpdateTime;
    private String currentTimeFormatted = "";

    private LocalTimeProperty(Data data, DateFormat dateFormat) {
        this.data = data;
        this.dateFormat = dateFormat;
    }

    public static LocalTimeProperty create(String pattern, String locale, Optional<TimeZone> timeZone) {
        return LocalTimeProperty.validate(new Data(pattern, locale, timeZone)).getOrThrow(format -> new IllegalStateException("Failed to validate format: " + format));
    }

    private static DataResult<LocalTimeProperty> validate(Data data) {
        ULocale uLocale = new ULocale(data.localeId);
        Calendar calendar = data.timeZone.map(timeZone -> Calendar.getInstance(timeZone, uLocale)).orElseGet(() -> Calendar.getInstance(uLocale));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(data.format, uLocale);
        simpleDateFormat.setCalendar(calendar);
        try {
            simpleDateFormat.format(new Date());
        } catch (Exception exception) {
            return DataResult.error(() -> "Invalid time format '" + String.valueOf(simpleDateFormat) + "': " + exception.getMessage());
        }
        return DataResult.success(new LocalTimeProperty(data, simpleDateFormat));
    }

    @Override
    @Nullable
    public String getValue(ItemStack arg, @Nullable ClientWorld arg2, @Nullable LivingEntity arg3, int i, ModelTransformationMode arg4) {
        long l = Util.getMeasuringTimeMs();
        if (l > this.nextUpdateTime) {
            this.currentTimeFormatted = this.formatCurrentTime();
            this.nextUpdateTime = l + MILLIS_PER_SECOND;
        }
        return this.currentTimeFormatted;
    }

    private String formatCurrentTime() {
        return this.dateFormat.format(new Date());
    }

    @Override
    public SelectProperty.Type<LocalTimeProperty, String> getType() {
        return TYPE;
    }

    @Override
    @Nullable
    public /* synthetic */ Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return this.getValue(stack, world, user, seed, modelTransformationMode);
    }

    @Environment(value=EnvType.CLIENT)
    record Data(String format, String localeId, Optional<TimeZone> timeZone) {
    }
}

