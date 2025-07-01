/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.GeneratedItemModel;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ReferencedModelsCollector {
    static final Logger LOGGER = LogUtils.getLogger();
    private final Map<Identifier, UnbakedModel> inputs;
    final UnbakedModel missingModel;
    private final List<ResolvableModel> topLevelModels = new ArrayList<ResolvableModel>();
    private final Map<Identifier, UnbakedModel> resolvedModels = new HashMap<Identifier, UnbakedModel>();

    public ReferencedModelsCollector(Map<Identifier, UnbakedModel> inputs, UnbakedModel missingModel) {
        this.inputs = inputs;
        this.missingModel = missingModel;
        this.resolvedModels.put(MissingModel.ID, missingModel);
    }

    public void addGenerated() {
        this.resolvedModels.put(GeneratedItemModel.GENERATED, new GeneratedItemModel());
    }

    public void add(ResolvableModel model) {
        this.topLevelModels.add(model);
    }

    public void resolveAll() {
        this.topLevelModels.forEach(model -> model.resolve(new ResolverImpl()));
    }

    public Map<Identifier, UnbakedModel> getResolvedModels() {
        return this.resolvedModels;
    }

    public Set<Identifier> getUnresolved() {
        return Sets.difference(this.inputs.keySet(), this.resolvedModels.keySet());
    }

    UnbakedModel computeResolvedModel(Identifier id) {
        return this.resolvedModels.computeIfAbsent(id, this::getModel);
    }

    private UnbakedModel getModel(Identifier id) {
        UnbakedModel lv = this.inputs.get(id);
        if (lv == null) {
            LOGGER.warn("Missing block model: '{}'", (Object)id);
            return this.missingModel;
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    class ResolverImpl
    implements ResolvableModel.Resolver {
        private final List<Identifier> stack = new ArrayList<Identifier>();
        private final Set<Identifier> visited = new HashSet<Identifier>();

        ResolverImpl() {
        }

        @Override
        public UnbakedModel resolve(Identifier id) {
            if (this.stack.contains(id)) {
                LOGGER.warn("Detected model loading loop: {}->{}", (Object)this.getPath(), (Object)id);
                return ReferencedModelsCollector.this.missingModel;
            }
            UnbakedModel lv = ReferencedModelsCollector.this.computeResolvedModel(id);
            if (this.visited.add(id)) {
                this.stack.add(id);
                lv.resolve(this);
                this.stack.remove(id);
            }
            return lv;
        }

        private String getPath() {
            return this.stack.stream().map(Identifier::toString).collect(Collectors.joining("->"));
        }
    }
}

