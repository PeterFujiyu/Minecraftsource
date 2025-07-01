/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.map.MapState;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryPair;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Item
implements ToggleableFeature,
ItemConvertible {
    public static final Codec<RegistryEntry<Item>> ENTRY_CODEC = Registries.ITEM.getEntryCodec().validate(entry -> entry.matches(Items.AIR.getRegistryEntry()) ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(entry));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Block, Item> BLOCK_ITEMS = Maps.newHashMap();
    public static final Identifier BASE_ATTACK_DAMAGE_MODIFIER_ID = Identifier.ofVanilla("base_attack_damage");
    public static final Identifier BASE_ATTACK_SPEED_MODIFIER_ID = Identifier.ofVanilla("base_attack_speed");
    public static final int DEFAULT_MAX_COUNT = 64;
    public static final int MAX_MAX_COUNT = 99;
    public static final int ITEM_BAR_STEPS = 13;
    private final RegistryEntry.Reference<Item> registryEntry = Registries.ITEM.createEntry(this);
    private final ComponentMap components;
    @Nullable
    private final Item recipeRemainder;
    protected final String translationKey;
    private final FeatureSet requiredFeatures;

    public static int getRawId(Item item) {
        return item == null ? 0 : Registries.ITEM.getRawId(item);
    }

    public static Item byRawId(int id) {
        return Registries.ITEM.get(id);
    }

    @Deprecated
    public static Item fromBlock(Block block) {
        return BLOCK_ITEMS.getOrDefault(block, Items.AIR);
    }

    public Item(Settings settings) {
        String string;
        this.translationKey = settings.getTranslationKey();
        this.components = settings.getValidatedComponents(Text.translatable(this.translationKey), settings.getModelId());
        this.recipeRemainder = settings.recipeRemainder;
        this.requiredFeatures = settings.requiredFeatures;
        if (SharedConstants.isDevelopment && !(string = this.getClass().getSimpleName()).endsWith("Item")) {
            LOGGER.error("Item classes should end with Item and {} doesn't.", (Object)string);
        }
    }

    @Deprecated
    public RegistryEntry.Reference<Item> getRegistryEntry() {
        return this.registryEntry;
    }

    public ComponentMap getComponents() {
        return this.components;
    }

    public int getMaxCount() {
        return this.components.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    }

    public void onItemEntityDestroyed(ItemEntity entity) {
    }

    public void postProcessComponents(ItemStack stack) {
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    public float getMiningSpeed(ItemStack stack, BlockState state) {
        ToolComponent lv = stack.get(DataComponentTypes.TOOL);
        return lv != null ? lv.getSpeed(state) : 1.0f;
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        ConsumableComponent lv2 = lv.get(DataComponentTypes.CONSUMABLE);
        if (lv2 != null) {
            return lv2.consume(user, lv, hand);
        }
        EquippableComponent lv3 = lv.get(DataComponentTypes.EQUIPPABLE);
        if (lv3 != null && lv3.swappable()) {
            return lv3.equip(lv, user);
        }
        return ActionResult.PASS;
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ConsumableComponent lv = stack.get(DataComponentTypes.CONSUMABLE);
        if (lv != null) {
            return lv.finishConsumption(world, user, stack);
        }
        return stack;
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return stack.isDamaged();
    }

    public int getItemBarStep(ItemStack stack) {
        return MathHelper.clamp(Math.round(13.0f - (float)stack.getDamage() * 13.0f / (float)stack.getMaxDamage()), 0, 13);
    }

    public int getItemBarColor(ItemStack stack) {
        int i = stack.getMaxDamage();
        float f = Math.max(0.0f, ((float)i - (float)stack.getDamage()) / (float)i);
        return MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f);
    }

    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        return false;
    }

    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return false;
    }

    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        return 0.0f;
    }

    @Nullable
    public DamageSource getDamageSource(LivingEntity user) {
        return null;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        ToolComponent lv = stack.get(DataComponentTypes.TOOL);
        if (lv == null) {
            return false;
        }
        if (!world.isClient && state.getHardness(world, pos) != 0.0f && lv.damagePerBlock() > 0) {
            stack.damage(lv.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        ToolComponent lv = stack.get(DataComponentTypes.TOOL);
        return lv != null && lv.isCorrectForDrops(state);
    }

    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    public String toString() {
        return Registries.ITEM.getEntry(this).getIdAsString();
    }

    public final ItemStack getRecipeRemainder() {
        return this.recipeRemainder == null ? ItemStack.EMPTY : new ItemStack(this.recipeRemainder);
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }

    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        this.onCraft(stack, world);
    }

    public void onCraft(ItemStack stack, World world) {
    }

    public UseAction getUseAction(ItemStack stack) {
        ConsumableComponent lv = stack.get(DataComponentTypes.CONSUMABLE);
        if (lv != null) {
            return lv.useAction();
        }
        return UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        ConsumableComponent lv = stack.get(DataComponentTypes.CONSUMABLE);
        if (lv != null) {
            return lv.getConsumeTicks();
        }
        return 0;
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return false;
    }

    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    }

    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.empty();
    }

    @VisibleForTesting
    public final String getTranslationKey() {
        return this.translationKey;
    }

    public final Text getName() {
        return this.components.getOrDefault(DataComponentTypes.ITEM_NAME, ScreenTexts.EMPTY);
    }

    public Text getName(ItemStack stack) {
        return stack.getComponents().getOrDefault(DataComponentTypes.ITEM_NAME, ScreenTexts.EMPTY);
    }

    public boolean hasGlint(ItemStack stack) {
        return stack.hasEnchantments();
    }

    protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        Vec3d lv = player.getEyePos();
        Vec3d lv2 = lv.add(player.getRotationVector(player.getPitch(), player.getYaw()).multiply(player.getBlockInteractionRange()));
        return world.raycast(new RaycastContext(lv, lv2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return false;
    }

    public ItemStack getDefaultStack() {
        return new ItemStack(this);
    }

    public SoundEvent getBreakSound() {
        return SoundEvents.ENTITY_ITEM_BREAK;
    }

    public boolean canBeNested() {
        return true;
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.requiredFeatures;
    }

    public boolean shouldShowOperatorBlockWarnings(ItemStack stack, @Nullable PlayerEntity player) {
        return false;
    }

    public static class Settings {
        private static final RegistryKeyedValue<Item, String> BLOCK_PREFIXED_TRANSLATION_KEY = key -> Util.createTranslationKey("block", key.getValue());
        private static final RegistryKeyedValue<Item, String> ITEM_PREFIXED_TRANSLATION_KEY = key -> Util.createTranslationKey("item", key.getValue());
        private final ComponentMap.Builder components = ComponentMap.builder().addAll(DataComponentTypes.DEFAULT_ITEM_COMPONENTS);
        @Nullable
        Item recipeRemainder;
        FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
        @Nullable
        private RegistryKey<Item> registryKey;
        private RegistryKeyedValue<Item, String> translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
        private RegistryKeyedValue<Item, Identifier> modelId = RegistryKey::getValue;

        public Settings food(FoodComponent foodComponent) {
            return this.food(foodComponent, ConsumableComponents.FOOD);
        }

        public Settings food(FoodComponent foodComponent, ConsumableComponent consumableComponent) {
            return this.component(DataComponentTypes.FOOD, foodComponent).component(DataComponentTypes.CONSUMABLE, consumableComponent);
        }

        public Settings useRemainder(Item convertInto) {
            return this.component(DataComponentTypes.USE_REMAINDER, new UseRemainderComponent(new ItemStack(convertInto)));
        }

        public Settings useCooldown(float seconds) {
            return this.component(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(seconds));
        }

        public Settings maxCount(int maxCount) {
            return this.component(DataComponentTypes.MAX_STACK_SIZE, maxCount);
        }

        public Settings maxDamage(int maxDamage) {
            this.component(DataComponentTypes.MAX_DAMAGE, maxDamage);
            this.component(DataComponentTypes.MAX_STACK_SIZE, 1);
            this.component(DataComponentTypes.DAMAGE, 0);
            return this;
        }

        public Settings recipeRemainder(Item recipeRemainder) {
            this.recipeRemainder = recipeRemainder;
            return this;
        }

        public Settings rarity(Rarity rarity) {
            return this.component(DataComponentTypes.RARITY, rarity);
        }

        public Settings fireproof() {
            return this.component(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(DamageTypeTags.IS_FIRE));
        }

        public Settings jukeboxPlayable(RegistryKey<JukeboxSong> songKey) {
            return this.component(DataComponentTypes.JUKEBOX_PLAYABLE, new JukeboxPlayableComponent(new RegistryPair<JukeboxSong>(songKey), true));
        }

        public Settings enchantable(int enchantability) {
            return this.component(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(enchantability));
        }

        public Settings repairable(Item repairIngredient) {
            return this.component(DataComponentTypes.REPAIRABLE, new RepairableComponent(RegistryEntryList.of(repairIngredient.getRegistryEntry())));
        }

        public Settings repairable(TagKey<Item> repairIngredientsTag) {
            RegistryEntryLookup<Item> lv = Registries.createEntryLookup(Registries.ITEM);
            return this.component(DataComponentTypes.REPAIRABLE, new RepairableComponent(lv.getOrThrow(repairIngredientsTag)));
        }

        public Settings equippable(EquipmentSlot slot) {
            return this.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot).build());
        }

        public Settings equippableUnswappable(EquipmentSlot slot) {
            return this.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot).swappable(false).build());
        }

        public Settings requires(FeatureFlag ... features) {
            this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
            return this;
        }

        public Settings registryKey(RegistryKey<Item> registryKey) {
            this.registryKey = registryKey;
            return this;
        }

        public Settings translationKey(String translationKey) {
            this.translationKey = RegistryKeyedValue.fixed(translationKey);
            return this;
        }

        public Settings useBlockPrefixedTranslationKey() {
            this.translationKey = BLOCK_PREFIXED_TRANSLATION_KEY;
            return this;
        }

        public Settings useItemPrefixedTranslationKey() {
            this.translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
            return this;
        }

        protected String getTranslationKey() {
            return this.translationKey.get(Objects.requireNonNull(this.registryKey, "Item id not set"));
        }

        public Identifier getModelId() {
            return this.modelId.get(Objects.requireNonNull(this.registryKey, "Item id not set"));
        }

        public <T> Settings component(ComponentType<T> type, T value) {
            this.components.add(type, value);
            return this;
        }

        public Settings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent) {
            return this.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiersComponent);
        }

        ComponentMap getValidatedComponents(Text name, Identifier modelId) {
            ComponentMap lv = this.components.add(DataComponentTypes.ITEM_NAME, name).add(DataComponentTypes.ITEM_MODEL, modelId).build();
            if (lv.contains(DataComponentTypes.DAMAGE) && lv.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
                throw new IllegalStateException("Item cannot have both durability and be stackable");
            }
            return lv;
        }
    }

    public static interface TooltipContext {
        public static final TooltipContext DEFAULT = new TooltipContext(){

            @Override
            @Nullable
            public RegistryWrapper.WrapperLookup getRegistryLookup() {
                return null;
            }

            @Override
            public float getUpdateTickRate() {
                return 20.0f;
            }

            @Override
            @Nullable
            public MapState getMapState(MapIdComponent mapIdComponent) {
                return null;
            }
        };

        @Nullable
        public RegistryWrapper.WrapperLookup getRegistryLookup();

        public float getUpdateTickRate();

        @Nullable
        public MapState getMapState(MapIdComponent var1);

        public static TooltipContext create(final @Nullable World world) {
            if (world == null) {
                return DEFAULT;
            }
            return new TooltipContext(){

                @Override
                public RegistryWrapper.WrapperLookup getRegistryLookup() {
                    return world.getRegistryManager();
                }

                @Override
                public float getUpdateTickRate() {
                    return world.getTickManager().getTickRate();
                }

                @Override
                public MapState getMapState(MapIdComponent mapIdComponent) {
                    return world.getMapState(mapIdComponent);
                }
            };
        }

        public static TooltipContext create(final RegistryWrapper.WrapperLookup registries) {
            return new TooltipContext(){

                @Override
                public RegistryWrapper.WrapperLookup getRegistryLookup() {
                    return registries;
                }

                @Override
                public float getUpdateTickRate() {
                    return 20.0f;
                }

                @Override
                @Nullable
                public MapState getMapState(MapIdComponent mapIdComponent) {
                    return null;
                }
            };
        }
    }
}

