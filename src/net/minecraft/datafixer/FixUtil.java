/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public class FixUtil {
    public static Dynamic<?> fixBlockPos(Dynamic<?> dynamic) {
        Optional<Number> optional = dynamic.get("X").asNumber().result();
        Optional<Number> optional2 = dynamic.get("Y").asNumber().result();
        Optional<Number> optional3 = dynamic.get("Z").asNumber().result();
        if (optional.isEmpty() || optional2.isEmpty() || optional3.isEmpty()) {
            return dynamic;
        }
        return dynamic.createIntList(IntStream.of(optional.get().intValue(), optional2.get().intValue(), optional3.get().intValue()));
    }

    public static <T, R> Typed<R> withType(Type<R> type, Typed<T> typed) {
        return new Typed<R>(type, typed.getOps(), typed.getValue());
    }

    public static Type<?> withTypeChanged(Type<?> type, Type<?> oldType, Type<?> newType) {
        return type.all(FixUtil.typeChangingRule(oldType, newType), true, false).view().newType();
    }

    private static <A, B> TypeRewriteRule typeChangingRule(Type<A> oldType, Type<B> newType) {
        RewriteResult<A, B> rewriteResult = RewriteResult.create(View.create("Patcher", oldType, newType, dynamicOps -> object -> {
            throw new UnsupportedOperationException();
        }), new BitSet());
        return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(oldType, rewriteResult), PointFreeRule.nop(), true, true);
    }

    @SafeVarargs
    public static <T> Function<Typed<?>, Typed<?>> compose(Function<Typed<?>, Typed<?>> ... fixes) {
        return typed -> {
            for (Function function : fixes) {
                typed = (Typed)function.apply(typed);
            }
            return typed;
        };
    }

    public static Dynamic<?> createBlockState(String id, Map<String, String> properties) {
        Dynamic<NbtCompound> dynamic = new Dynamic<NbtCompound>(NbtOps.INSTANCE, new NbtCompound());
        Dynamic<NbtCompound> dynamic2 = dynamic.set("Name", dynamic.createString(id));
        if (!properties.isEmpty()) {
            dynamic2 = dynamic2.set("Properties", dynamic.createMap(properties.entrySet().stream().collect(Collectors.toMap(entry -> dynamic.createString((String)entry.getKey()), entry -> dynamic.createString((String)entry.getValue())))));
        }
        return dynamic2;
    }

    public static Dynamic<?> createBlockState(String id) {
        return FixUtil.createBlockState(id, Map.of());
    }

    public static Dynamic<?> apply(Dynamic<?> dynamic, String fieldName, UnaryOperator<String> applier) {
        return dynamic.update(fieldName, value -> DataFixUtils.orElse(value.asString().map(applier).map(dynamic::createString).result(), value));
    }
}

