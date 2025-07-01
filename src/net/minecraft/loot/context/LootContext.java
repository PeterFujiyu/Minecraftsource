/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.loot.context;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class LootContext {
    private final LootWorldContext worldContext;
    private final Random random;
    private final RegistryEntryLookup.RegistryLookup lookup;
    private final Set<Entry<?>> activeEntries = Sets.newLinkedHashSet();

    LootContext(LootWorldContext worldContext, Random random, RegistryEntryLookup.RegistryLookup lookup) {
        this.worldContext = worldContext;
        this.random = random;
        this.lookup = lookup;
    }

    public boolean hasParameter(ContextParameter<?> parameter) {
        return this.worldContext.getParameters().contains(parameter);
    }

    public <T> T getOrThrow(ContextParameter<T> parameter) {
        return this.worldContext.getParameters().getOrThrow(parameter);
    }

    @Nullable
    public <T> T get(ContextParameter<T> parameter) {
        return this.worldContext.getParameters().getNullable(parameter);
    }

    public void drop(Identifier id, Consumer<ItemStack> lootConsumer) {
        this.worldContext.addDynamicDrops(id, lootConsumer);
    }

    public boolean isActive(Entry<?> entry) {
        return this.activeEntries.contains(entry);
    }

    public boolean markActive(Entry<?> entry) {
        return this.activeEntries.add(entry);
    }

    public void markInactive(Entry<?> entry) {
        this.activeEntries.remove(entry);
    }

    public RegistryEntryLookup.RegistryLookup getLookup() {
        return this.lookup;
    }

    public Random getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.worldContext.getLuck();
    }

    public ServerWorld getWorld() {
        return this.worldContext.getWorld();
    }

    public static Entry<LootTable> table(LootTable table) {
        return new Entry<LootTable>(LootDataType.LOOT_TABLES, table);
    }

    public static Entry<LootCondition> predicate(LootCondition predicate) {
        return new Entry<LootCondition>(LootDataType.PREDICATES, predicate);
    }

    public static Entry<LootFunction> itemModifier(LootFunction itemModifier) {
        return new Entry<LootFunction>(LootDataType.ITEM_MODIFIERS, itemModifier);
    }

    public record Entry<T>(LootDataType<T> type, T value) {
    }

    public static enum EntityTarget implements StringIdentifiable
    {
        THIS("this", LootContextParameters.THIS_ENTITY),
        ATTACKER("attacker", LootContextParameters.ATTACKING_ENTITY),
        DIRECT_ATTACKER("direct_attacker", LootContextParameters.DIRECT_ATTACKING_ENTITY),
        ATTACKING_PLAYER("attacking_player", LootContextParameters.LAST_DAMAGE_PLAYER);

        public static final StringIdentifiable.EnumCodec<EntityTarget> CODEC;
        private final String type;
        private final ContextParameter<? extends Entity> parameter;

        private EntityTarget(String type, ContextParameter<? extends Entity> parameter) {
            this.type = type;
            this.parameter = parameter;
        }

        public ContextParameter<? extends Entity> getParameter() {
            return this.parameter;
        }

        public static EntityTarget fromString(String type) {
            EntityTarget lv = CODEC.byId(type);
            if (lv != null) {
                return lv;
            }
            throw new IllegalArgumentException("Invalid entity target " + type);
        }

        @Override
        public String asString() {
            return this.type;
        }

        static {
            CODEC = StringIdentifiable.createCodec(EntityTarget::values);
        }
    }

    public static class Builder {
        private final LootWorldContext worldContext;
        @Nullable
        private Random random;

        public Builder(LootWorldContext worldContext) {
            this.worldContext = worldContext;
        }

        public Builder random(long seed) {
            if (seed != 0L) {
                this.random = Random.create(seed);
            }
            return this;
        }

        public Builder random(Random random) {
            this.random = random;
            return this;
        }

        public ServerWorld getWorld() {
            return this.worldContext.getWorld();
        }

        public LootContext build(Optional<Identifier> randomId) {
            ServerWorld lv = this.getWorld();
            MinecraftServer minecraftServer = lv.getServer();
            Random lv2 = Optional.ofNullable(this.random).or(() -> randomId.map(lv::getOrCreateRandom)).orElseGet(lv::getRandom);
            return new LootContext(this.worldContext, lv2, minecraftServer.getReloadableRegistries().createRegistryLookup());
        }
    }
}

