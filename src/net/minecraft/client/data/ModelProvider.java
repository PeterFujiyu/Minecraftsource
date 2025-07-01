/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateSupplier;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ModelProvider
implements DataProvider {
    private final DataOutput.PathResolver blockstatesPathResolver;
    private final DataOutput.PathResolver itemsPathResolver;
    private final DataOutput.PathResolver modelsPathResolver;

    public ModelProvider(DataOutput output) {
        this.blockstatesPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "blockstates");
        this.itemsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "items");
        this.modelsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        ItemAssets lv = new ItemAssets();
        BlockStateSuppliers lv2 = new BlockStateSuppliers();
        ModelSuppliers lv3 = new ModelSuppliers();
        new BlockStateModelGenerator(lv2, lv, lv3).register();
        new ItemModelGenerator(lv, lv3).register();
        lv2.validate();
        lv.resolveAndValidate();
        return CompletableFuture.allOf(lv2.writeAllToPath(writer, this.blockstatesPathResolver), lv3.writeAllToPath(writer, this.modelsPathResolver), lv.writeAllToPath(writer, this.itemsPathResolver));
    }

    static <T> CompletableFuture<?> writeAllToPath(DataWriter writer, Function<T, Path> pathResolver, Map<T, ? extends Supplier<JsonElement>> idsToValues) {
        return DataProvider.writeAllToPath(writer, Supplier::get, pathResolver, idsToValues);
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    @Environment(value=EnvType.CLIENT)
    static class ItemAssets
    implements ItemModelOutput {
        private final Map<Item, ItemAsset> itemAssets = new HashMap<Item, ItemAsset>();
        private final Map<Item, Item> aliasedAssets = new HashMap<Item, Item>();

        ItemAssets() {
        }

        @Override
        public void accept(Item item, ItemModel.Unbaked model) {
            this.accept(item, new ItemAsset(model, ItemAsset.Properties.DEFAULT));
        }

        private void accept(Item item, ItemAsset asset) {
            ItemAsset lv = this.itemAssets.put(item, asset);
            if (lv != null) {
                throw new IllegalStateException("Duplicate item model definition for " + String.valueOf(item));
            }
        }

        @Override
        public void acceptAlias(Item base, Item alias) {
            this.aliasedAssets.put(alias, base);
        }

        public void resolveAndValidate() {
            Registries.ITEM.forEach(item -> {
                BlockItem lv;
                if (this.aliasedAssets.containsKey(item)) {
                    return;
                }
                if (item instanceof BlockItem && !this.itemAssets.containsKey(lv = (BlockItem)item)) {
                    Identifier lv2 = ModelIds.getBlockModelId(lv.getBlock());
                    this.accept((Item)lv, ItemModels.basic(lv2));
                }
            });
            this.aliasedAssets.forEach((base, alias) -> {
                ItemAsset lv = this.itemAssets.get(alias);
                if (lv == null) {
                    throw new IllegalStateException("Missing donor: " + String.valueOf(alias) + " -> " + String.valueOf(base));
                }
                this.accept((Item)base, lv);
            });
            List<Identifier> list = Registries.ITEM.streamEntries().filter(entry -> !this.itemAssets.containsKey(entry.value())).map(entryx -> entryx.registryKey().getValue()).toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + String.valueOf(list));
            }
        }

        public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
            return DataProvider.writeAllToPath(writer, ItemAsset.CODEC, item -> pathResolver.resolveJson(item.getRegistryEntry().registryKey().getValue()), this.itemAssets);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlockStateSuppliers
    implements Consumer<BlockStateSupplier> {
        private final Map<Block, BlockStateSupplier> blockStateSuppliers = new HashMap<Block, BlockStateSupplier>();

        BlockStateSuppliers() {
        }

        @Override
        public void accept(BlockStateSupplier arg) {
            Block lv = arg.getBlock();
            BlockStateSupplier lv2 = this.blockStateSuppliers.put(lv, arg);
            if (lv2 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf(lv));
            }
        }

        public void validate() {
            Stream<RegistryEntry.Reference> stream = Registries.BLOCK.streamEntries().filter(entry -> true);
            List<Identifier> list = stream.filter(entry -> !this.blockStateSuppliers.containsKey(entry.value())).map(entryx -> entryx.registryKey().getValue()).toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf(list));
            }
        }

        public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
            return ModelProvider.writeAllToPath(writer, block -> pathResolver.resolveJson(block.getRegistryEntry().registryKey().getValue()), this.blockStateSuppliers);
        }

        @Override
        public /* synthetic */ void accept(Object blockStateSupplier) {
            this.accept((BlockStateSupplier)blockStateSupplier);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelSuppliers
    implements BiConsumer<Identifier, ModelSupplier> {
        private final Map<Identifier, ModelSupplier> modelSuppliers = new HashMap<Identifier, ModelSupplier>();

        ModelSuppliers() {
        }

        @Override
        public void accept(Identifier arg, ModelSupplier arg2) {
            Supplier supplier = this.modelSuppliers.put(arg, arg2);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + String.valueOf(arg));
            }
        }

        public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
            return ModelProvider.writeAllToPath(writer, pathResolver::resolveJson, this.modelSuppliers);
        }

        @Override
        public /* synthetic */ void accept(Object id, Object modelSupplier) {
            this.accept((Identifier)id, (ModelSupplier)modelSupplier);
        }
    }
}

