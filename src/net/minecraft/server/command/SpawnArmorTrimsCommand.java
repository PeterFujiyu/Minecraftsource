/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnArmorTrimsCommand {
    private static final List<RegistryKey<ArmorTrimPattern>> PATTERNS = List.of(ArmorTrimPatterns.SENTRY, ArmorTrimPatterns.DUNE, ArmorTrimPatterns.COAST, ArmorTrimPatterns.WILD, ArmorTrimPatterns.WARD, ArmorTrimPatterns.EYE, ArmorTrimPatterns.VEX, ArmorTrimPatterns.TIDE, ArmorTrimPatterns.SNOUT, ArmorTrimPatterns.RIB, ArmorTrimPatterns.SPIRE, ArmorTrimPatterns.WAYFINDER, ArmorTrimPatterns.SHAPER, ArmorTrimPatterns.SILENCE, ArmorTrimPatterns.RAISER, ArmorTrimPatterns.HOST, ArmorTrimPatterns.FLOW, ArmorTrimPatterns.BOLT);
    private static final List<RegistryKey<ArmorTrimMaterial>> MATERIALS = List.of(ArmorTrimMaterials.QUARTZ, ArmorTrimMaterials.IRON, ArmorTrimMaterials.NETHERITE, ArmorTrimMaterials.REDSTONE, ArmorTrimMaterials.COPPER, ArmorTrimMaterials.GOLD, ArmorTrimMaterials.EMERALD, ArmorTrimMaterials.DIAMOND, ArmorTrimMaterials.LAPIS, ArmorTrimMaterials.AMETHYST, ArmorTrimMaterials.RESIN);
    private static final ToIntFunction<RegistryKey<ArmorTrimPattern>> PATTERN_INDEX_GETTER = Util.lastIndexGetter(PATTERNS);
    private static final ToIntFunction<RegistryKey<ArmorTrimMaterial>> MATERIAL_INDEX_GETTER = Util.lastIndexGetter(MATERIALS);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spawn_armor_trims").requires(source -> source.hasPermissionLevel(2))).executes(context -> SpawnArmorTrimsCommand.execute((ServerCommandSource)context.getSource(), ((ServerCommandSource)context.getSource()).getPlayerOrThrow())));
    }

    private static int execute(ServerCommandSource source, PlayerEntity player) {
        World lv = player.getWorld();
        DefaultedList<ArmorTrim> lv2 = DefaultedList.of();
        RegistryWrapper.Impl lv3 = lv.getRegistryManager().getOrThrow(RegistryKeys.TRIM_PATTERN);
        RegistryWrapper.Impl lv4 = lv.getRegistryManager().getOrThrow(RegistryKeys.TRIM_MATERIAL);
        RegistryWrapper<Item> lv5 = lv.createCommandRegistryWrapper(RegistryKeys.ITEM);
        Map<RegistryKey, List<Item>> map = lv5.streamEntries().map(RegistryEntry.Reference::value).filter(arg -> {
            EquippableComponent lv = arg.getComponents().get(DataComponentTypes.EQUIPPABLE);
            return lv != null && lv.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR && lv.assetId().isPresent();
        }).collect(Collectors.groupingBy(arg -> arg.getComponents().get(DataComponentTypes.EQUIPPABLE).assetId().get()));
        lv3.stream().sorted(Comparator.comparing(pattern -> PATTERN_INDEX_GETTER.applyAsInt(((Registry)lv3).getKey(pattern).orElse(null)))).forEachOrdered(pattern -> ((Registry)lv4).stream().sorted(Comparator.comparing(material -> MATERIAL_INDEX_GETTER.applyAsInt(((Registry)lv4).getKey(material).orElse(null)))).forEachOrdered(material -> lv2.add(new ArmorTrim(((Registry)lv4).getEntry(material), ((Registry)lv3).getEntry(pattern)))));
        BlockPos lv6 = player.getBlockPos().offset(player.getHorizontalFacing(), 5);
        int i = map.size() - 1;
        double d = 3.0;
        int j = 0;
        int k = 0;
        for (ArmorTrim lv7 : lv2) {
            for (List<Item> list : map.values()) {
                double e = (double)lv6.getX() + 0.5 - (double)(j % lv4.size()) * 3.0;
                double f = (double)lv6.getY() + 0.5 + (double)(k % i) * 3.0;
                double g = (double)lv6.getZ() + 0.5 + (double)(j / lv4.size() * 10);
                ArmorStandEntity lv8 = new ArmorStandEntity(lv, e, f, g);
                lv8.setYaw(180.0f);
                lv8.setNoGravity(true);
                for (Item lv9 : list) {
                    EquippableComponent lv10 = Objects.requireNonNull(lv9.getComponents().get(DataComponentTypes.EQUIPPABLE));
                    ItemStack lv11 = new ItemStack(lv9);
                    lv11.set(DataComponentTypes.TRIM, lv7);
                    lv8.equipStack(lv10.slot(), lv11);
                    if (lv11.isOf(Items.TURTLE_HELMET)) {
                        lv8.setCustomName(lv7.pattern().value().getDescription(lv7.material()).copy().append(" ").append(lv7.material().value().description()));
                        lv8.setCustomNameVisible(true);
                        continue;
                    }
                    lv8.setInvisible(true);
                }
                lv.spawnEntity(lv8);
                ++k;
            }
            ++j;
        }
        source.sendFeedback(() -> Text.literal("Armorstands with trimmed armor spawned around you"), true);
        return 1;
    }
}

