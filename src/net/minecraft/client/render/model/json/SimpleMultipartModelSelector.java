/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class SimpleMultipartModelSelector
implements MultipartModelSelector {
    private static final Splitter VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String valueString;

    public SimpleMultipartModelSelector(String key, String valueString) {
        this.key = key;
        this.valueString = valueString;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> arg) {
        List<String> list;
        boolean bl;
        Property<?> lv = arg.getProperty(this.key);
        if (lv == null) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", this.key, arg.getOwner()));
        }
        String string = this.valueString;
        boolean bl2 = bl = !string.isEmpty() && string.charAt(0) == '!';
        if (bl) {
            string = string.substring(1);
        }
        if ((list = VALUE_SPLITTER.splitToList(string)).isEmpty()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Empty value '%s' for property '%s' on '%s'", this.valueString, this.key, arg.getOwner()));
        }
        Predicate<BlockState> predicate = list.size() == 1 ? this.createPredicate(arg, lv, string) : Util.anyOf(list.stream().map(value -> this.createPredicate(arg, lv, (String)value)).toList());
        return bl ? predicate.negate() : predicate;
    }

    private Predicate<BlockState> createPredicate(StateManager<Block, BlockState> stateFactory, Property<?> property, String valueString) {
        Optional<?> optional = property.parse(valueString);
        if (optional.isEmpty()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", valueString, this.key, stateFactory.getOwner(), this.valueString));
        }
        return state -> state.get(property).equals(optional.get());
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.valueString).toString();
    }
}

