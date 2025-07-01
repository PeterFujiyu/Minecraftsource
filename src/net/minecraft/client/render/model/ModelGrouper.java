/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public class ModelGrouper {
    static final int field_53673 = -1;
    private static final int field_53674 = 0;

    public static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.BlockStateDefinition definition) {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        definition.models().forEach((modelId, model) -> {
            List list = map.computeIfAbsent(model.state().getBlock(), block -> List.copyOf(colors.getProperties((Block)block)));
            GroupKey lv = GroupKey.of(model.state(), model.model(), list);
            map2.computeIfAbsent(lv, key -> Sets.newIdentityHashSet()).add(model.state());
        });
        int i = 1;
        Object2IntOpenHashMap<BlockState> object2IntMap = new Object2IntOpenHashMap<BlockState>();
        object2IntMap.defaultReturnValue(-1);
        for (Set set : map2.values()) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                BlockState lv = (BlockState)iterator.next();
                if (lv.getRenderType() == BlockRenderType.MODEL) continue;
                iterator.remove();
                object2IntMap.put(lv, 0);
            }
            if (set.size() <= 1) continue;
            int j = i++;
            set.forEach(state -> object2IntMap.put((BlockState)state, j));
        }
        return object2IntMap;
    }

    @Environment(value=EnvType.CLIENT)
    record GroupKey(Object equalityGroup, List<Object> coloringValues) {
        public static GroupKey of(BlockState state, GroupableModel model, List<Property<?>> properties) {
            List<Object> list2 = GroupKey.getColoringValues(state, properties);
            Object object = model.getEqualityGroup(state);
            return new GroupKey(object, list2);
        }

        private static List<Object> getColoringValues(BlockState state, List<Property<?>> properties) {
            Object[] objects = new Object[properties.size()];
            for (int i = 0; i < properties.size(); ++i) {
                objects[i] = state.get(properties.get(i));
            }
            return List.of(objects);
        }
    }
}

