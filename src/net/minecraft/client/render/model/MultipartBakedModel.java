/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.WrapperBakedModel;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultipartBakedModel
extends WrapperBakedModel {
    private final List<Selector> selectors;
    private final Map<BlockState, BitSet> stateCache = new Reference2ObjectOpenHashMap<BlockState, BitSet>();

    private static BakedModel getFirst(List<Selector> selectors) {
        if (selectors.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
        }
        return selectors.getFirst().model();
    }

    public MultipartBakedModel(List<Selector> selectors) {
        super(MultipartBakedModel.getFirst(selectors));
        this.selectors = selectors;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        if (state == null) {
            return Collections.emptyList();
        }
        BitSet bitSet = this.stateCache.get(state);
        if (bitSet == null) {
            bitSet = new BitSet();
            for (int i = 0; i < this.selectors.size(); ++i) {
                if (!this.selectors.get((int)i).condition.test(state)) continue;
                bitSet.set(i);
            }
            this.stateCache.put(state, bitSet);
        }
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        long l = random.nextLong();
        for (int j = 0; j < bitSet.length(); ++j) {
            if (!bitSet.get(j)) continue;
            random.setSeed(l);
            list.addAll(this.selectors.get((int)j).model.getQuads(state, face, random));
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public record Selector(Predicate<BlockState> condition, BakedModel model) {
    }
}

