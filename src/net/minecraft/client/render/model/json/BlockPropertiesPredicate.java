/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockPropertiesPredicate {
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);

    public static <O, S extends State<O, S>> Predicate<State<O, S>> parse(StateManager<O, S> stateManager, String string) {
        HashMap map = new HashMap();
        for (String string2 : COMMA_SPLITTER.split(string)) {
            Iterator<String> iterator = EQUAL_SIGN_SPLITTER.split(string2).iterator();
            if (!iterator.hasNext()) continue;
            String string3 = iterator.next();
            Property<?> lv = stateManager.getProperty(string3);
            if (lv != null && iterator.hasNext()) {
                String string4 = iterator.next();
                Object comparable = BlockPropertiesPredicate.parse(lv, string4);
                if (comparable != null) {
                    map.put(lv, comparable);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + String.valueOf(lv.getValues()));
            }
            if (string3.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
        }
        return state -> {
            for (Map.Entry entry : map.entrySet()) {
                if (Objects.equals(state.get((Property)entry.getKey()), entry.getValue())) continue;
                return false;
            }
            return true;
        };
    }

    @Nullable
    private static <T extends Comparable<T>> T parse(Property<T> property, String value) {
        return (T)((Comparable)property.parse(value).orElse(null));
    }
}

