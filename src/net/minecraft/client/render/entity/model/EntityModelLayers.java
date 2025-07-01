/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EntityModelLayers {
    private static final String MAIN = "main";
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    public static final EntityModelLayer ACACIA_BOAT = EntityModelLayers.registerMain("boat/acacia");
    public static final EntityModelLayer ACACIA_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/acacia");
    public static final EntityModelLayer ALLAY = EntityModelLayers.registerMain("allay");
    public static final EntityModelLayer ARMADILLO = EntityModelLayers.registerMain("armadillo");
    public static final EntityModelLayer ARMADILLO_BABY = EntityModelLayers.registerMain("armadillo_baby");
    public static final EntityModelLayer ARMOR_STAND = EntityModelLayers.registerMain("armor_stand");
    public static final EntityModelLayer ARMOR_STAND_INNER_ARMOR = EntityModelLayers.createInnerArmor("armor_stand");
    public static final EntityModelLayer ARMOR_STAND_OUTER_ARMOR = EntityModelLayers.createOuterArmor("armor_stand");
    public static final EntityModelLayer ARMOR_STAND_SMALL = EntityModelLayers.registerMain("armor_stand_small");
    public static final EntityModelLayer ARMOR_STAND_SMALL_INNER_ARMOR = EntityModelLayers.createInnerArmor("armor_stand_small");
    public static final EntityModelLayer ARMOR_STAND_SMALL_OUTER_ARMOR = EntityModelLayers.createOuterArmor("armor_stand_small");
    public static final EntityModelLayer ARROW = EntityModelLayers.registerMain("arrow");
    public static final EntityModelLayer AXOLOTL = EntityModelLayers.registerMain("axolotl");
    public static final EntityModelLayer AXOLOTL_BABY = EntityModelLayers.registerMain("axolotl_baby");
    public static final EntityModelLayer BAMBOO_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/bamboo");
    public static final EntityModelLayer BAMBOO_BOAT = EntityModelLayers.registerMain("boat/bamboo");
    public static final EntityModelLayer STANDING_BANNER = EntityModelLayers.registerMain("standing_banner");
    public static final EntityModelLayer STANDING_BANNER_FLAG = EntityModelLayers.register("standing_banner", "flag");
    public static final EntityModelLayer WALL_BANNER = EntityModelLayers.registerMain("wall_banner");
    public static final EntityModelLayer WALL_BANNER_FLAG = EntityModelLayers.register("wall_banner", "flag");
    public static final EntityModelLayer BAT = EntityModelLayers.registerMain("bat");
    public static final EntityModelLayer BED_FOOT = EntityModelLayers.registerMain("bed_foot");
    public static final EntityModelLayer BED_HEAD = EntityModelLayers.registerMain("bed_head");
    public static final EntityModelLayer BEE = EntityModelLayers.registerMain("bee");
    public static final EntityModelLayer BEE_BABY = EntityModelLayers.registerMain("bee_baby");
    public static final EntityModelLayer BEE_STINGER = EntityModelLayers.registerMain("bee_stinger");
    public static final EntityModelLayer BELL = EntityModelLayers.registerMain("bell");
    public static final EntityModelLayer BIRCH_BOAT = EntityModelLayers.registerMain("boat/birch");
    public static final EntityModelLayer BIRCH_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/birch");
    public static final EntityModelLayer BLAZE = EntityModelLayers.registerMain("blaze");
    public static final EntityModelLayer BOAT = EntityModelLayers.register("boat", "water_patch");
    public static final EntityModelLayer BOGGED = EntityModelLayers.registerMain("bogged");
    public static final EntityModelLayer BOGGED_INNER_ARMOR = EntityModelLayers.createInnerArmor("bogged");
    public static final EntityModelLayer BOGGED_OUTER_ARMOR = EntityModelLayers.createOuterArmor("bogged");
    public static final EntityModelLayer BOGGED_OUTER = EntityModelLayers.register("bogged", "outer");
    public static final EntityModelLayer BOOK = EntityModelLayers.registerMain("book");
    public static final EntityModelLayer BREEZE = EntityModelLayers.registerMain("breeze");
    public static final EntityModelLayer BREEZE_WIND = EntityModelLayers.registerMain("breeze_wind");
    public static final EntityModelLayer CAMEL = EntityModelLayers.registerMain("camel");
    public static final EntityModelLayer CAMEL_BABY = EntityModelLayers.registerMain("camel_baby");
    public static final EntityModelLayer CAT = EntityModelLayers.registerMain("cat");
    public static final EntityModelLayer CAT_BABY = EntityModelLayers.registerMain("cat_baby");
    public static final EntityModelLayer CAT_BABY_COLLAR = EntityModelLayers.register("cat_baby", "collar");
    public static final EntityModelLayer CAT_COLLAR = EntityModelLayers.register("cat", "collar");
    public static final EntityModelLayer CAVE_SPIDER = EntityModelLayers.registerMain("cave_spider");
    public static final EntityModelLayer CHERRY_BOAT = EntityModelLayers.registerMain("boat/cherry");
    public static final EntityModelLayer CHERRY_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/cherry");
    public static final EntityModelLayer CHEST = EntityModelLayers.registerMain("chest");
    public static final EntityModelLayer CHEST_MINECART = EntityModelLayers.registerMain("chest_minecart");
    public static final EntityModelLayer CHICKEN = EntityModelLayers.registerMain("chicken");
    public static final EntityModelLayer CHICKEN_BABY = EntityModelLayers.registerMain("chicken_baby");
    public static final EntityModelLayer COD = EntityModelLayers.registerMain("cod");
    public static final EntityModelLayer COMMAND_BLOCK_MINECART = EntityModelLayers.registerMain("command_block_minecart");
    public static final EntityModelLayer CONDUIT = EntityModelLayers.register("conduit", "cage");
    public static final EntityModelLayer CONDUIT_EYE = EntityModelLayers.register("conduit", "eye");
    public static final EntityModelLayer CONDUIT_SHELL = EntityModelLayers.register("conduit", "shell");
    public static final EntityModelLayer CONDUIT_WIND = EntityModelLayers.register("conduit", "wind");
    public static final EntityModelLayer COW = EntityModelLayers.registerMain("cow");
    public static final EntityModelLayer COW_BABY = EntityModelLayers.registerMain("cow_baby");
    public static final EntityModelLayer CREAKING = EntityModelLayers.registerMain("creaking");
    public static final EntityModelLayer CREEPER = EntityModelLayers.registerMain("creeper");
    public static final EntityModelLayer CREEPER_ARMOR = EntityModelLayers.register("creeper", "armor");
    public static final EntityModelLayer CREEPER_HEAD = EntityModelLayers.registerMain("creeper_head");
    public static final EntityModelLayer DARK_OAK_BOAT = EntityModelLayers.registerMain("boat/dark_oak");
    public static final EntityModelLayer DARK_OAK_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/dark_oak");
    public static final EntityModelLayer DECORATED_POT_BASE = EntityModelLayers.registerMain("decorated_pot_base");
    public static final EntityModelLayer DECORATED_POT_SIDES = EntityModelLayers.registerMain("decorated_pot_sides");
    public static final EntityModelLayer DOLPHIN = EntityModelLayers.registerMain("dolphin");
    public static final EntityModelLayer DOLPHIN_BABY = EntityModelLayers.registerMain("dolphin_baby");
    public static final EntityModelLayer DONKEY = EntityModelLayers.registerMain("donkey");
    public static final EntityModelLayer DONKEY_BABY = EntityModelLayers.registerMain("donkey_baby");
    public static final EntityModelLayer DOUBLE_CHEST_LEFT = EntityModelLayers.registerMain("double_chest_left");
    public static final EntityModelLayer DOUBLE_CHEST_RIGHT = EntityModelLayers.registerMain("double_chest_right");
    public static final EntityModelLayer DRAGON_SKULL = EntityModelLayers.registerMain("dragon_skull");
    public static final EntityModelLayer DROWNED = EntityModelLayers.registerMain("drowned");
    public static final EntityModelLayer DROWNED_BABY = EntityModelLayers.registerMain("drowned_baby");
    public static final EntityModelLayer DROWNED_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("drowned_baby");
    public static final EntityModelLayer DROWNED_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("drowned_baby");
    public static final EntityModelLayer DROWNED_BABY_OUTER = EntityModelLayers.register("drowned_baby", "outer");
    public static final EntityModelLayer DROWNED_INNER_ARMOR = EntityModelLayers.createInnerArmor("drowned");
    public static final EntityModelLayer DROWNED_OUTER_ARMOR = EntityModelLayers.createOuterArmor("drowned");
    public static final EntityModelLayer DROWNED_OUTER = EntityModelLayers.register("drowned", "outer");
    public static final EntityModelLayer ELDER_GUARDIAN = EntityModelLayers.registerMain("elder_guardian");
    public static final EntityModelLayer ELYTRA = EntityModelLayers.registerMain("elytra");
    public static final EntityModelLayer ELYTRA_BABY = EntityModelLayers.registerMain("elytra_baby");
    public static final EntityModelLayer ENDERMAN = EntityModelLayers.registerMain("enderman");
    public static final EntityModelLayer ENDERMITE = EntityModelLayers.registerMain("endermite");
    public static final EntityModelLayer ENDER_DRAGON = EntityModelLayers.registerMain("ender_dragon");
    public static final EntityModelLayer END_CRYSTAL = EntityModelLayers.registerMain("end_crystal");
    public static final EntityModelLayer EVOKER = EntityModelLayers.registerMain("evoker");
    public static final EntityModelLayer EVOKER_FANGS = EntityModelLayers.registerMain("evoker_fangs");
    public static final EntityModelLayer FOX = EntityModelLayers.registerMain("fox");
    public static final EntityModelLayer FOX_BABY = EntityModelLayers.registerMain("fox_baby");
    public static final EntityModelLayer FROG = EntityModelLayers.registerMain("frog");
    public static final EntityModelLayer FURNACE_MINECART = EntityModelLayers.registerMain("furnace_minecart");
    public static final EntityModelLayer GHAST = EntityModelLayers.registerMain("ghast");
    public static final EntityModelLayer GIANT = EntityModelLayers.registerMain("giant");
    public static final EntityModelLayer GIANT_INNER_ARMOR = EntityModelLayers.createInnerArmor("giant");
    public static final EntityModelLayer GIANT_OUTER_ARMOR = EntityModelLayers.createOuterArmor("giant");
    public static final EntityModelLayer GLOW_SQUID = EntityModelLayers.registerMain("glow_squid");
    public static final EntityModelLayer GLOW_SQUID_BABY = EntityModelLayers.registerMain("glow_squid_baby");
    public static final EntityModelLayer GOAT = EntityModelLayers.registerMain("goat");
    public static final EntityModelLayer GOAT_BABY = EntityModelLayers.registerMain("goat_baby");
    public static final EntityModelLayer GUARDIAN = EntityModelLayers.registerMain("guardian");
    public static final EntityModelLayer HOGLIN = EntityModelLayers.registerMain("hoglin");
    public static final EntityModelLayer HOGLIN_BABY = EntityModelLayers.registerMain("hoglin_baby");
    public static final EntityModelLayer HOPPER_MINECART = EntityModelLayers.registerMain("hopper_minecart");
    public static final EntityModelLayer HORSE = EntityModelLayers.registerMain("horse");
    public static final EntityModelLayer HORSE_ARMOR = EntityModelLayers.registerMain("horse_armor");
    public static final EntityModelLayer HORSE_BABY = EntityModelLayers.registerMain("horse_baby");
    public static final EntityModelLayer HORSE_ARMOR_BABY = EntityModelLayers.registerMain("horse_armor_baby");
    public static final EntityModelLayer HUSK = EntityModelLayers.registerMain("husk");
    public static final EntityModelLayer HUSK_BABY = EntityModelLayers.registerMain("husk_baby");
    public static final EntityModelLayer HUSK_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("husk_baby");
    public static final EntityModelLayer HUSK_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("husk_baby");
    public static final EntityModelLayer HUSK_INNER_ARMOR = EntityModelLayers.createInnerArmor("husk");
    public static final EntityModelLayer HUSK_OUTER_ARMOR = EntityModelLayers.createOuterArmor("husk");
    public static final EntityModelLayer ILLUSIONER = EntityModelLayers.registerMain("illusioner");
    public static final EntityModelLayer IRON_GOLEM = EntityModelLayers.registerMain("iron_golem");
    public static final EntityModelLayer JUNGLE_BOAT = EntityModelLayers.registerMain("boat/jungle");
    public static final EntityModelLayer JUNGLE_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/jungle");
    public static final EntityModelLayer LEASH_KNOT = EntityModelLayers.registerMain("leash_knot");
    public static final EntityModelLayer LLAMA = EntityModelLayers.registerMain("llama");
    public static final EntityModelLayer LLAMA_BABY = EntityModelLayers.registerMain("llama_baby");
    public static final EntityModelLayer LLAMA_BABY_DECOR = EntityModelLayers.register("llama_baby", "decor");
    public static final EntityModelLayer LLAMA_DECOR = EntityModelLayers.register("llama", "decor");
    public static final EntityModelLayer LLAMA_SPIT = EntityModelLayers.registerMain("llama_spit");
    public static final EntityModelLayer MAGMA_CUBE = EntityModelLayers.registerMain("magma_cube");
    public static final EntityModelLayer MANGROVE_BOAT = EntityModelLayers.registerMain("boat/mangrove");
    public static final EntityModelLayer MANGROVE_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/mangrove");
    public static final EntityModelLayer MINECART = EntityModelLayers.registerMain("minecart");
    public static final EntityModelLayer MOOSHROOM = EntityModelLayers.registerMain("mooshroom");
    public static final EntityModelLayer MOOSHROOM_BABY = EntityModelLayers.registerMain("mooshroom_baby");
    public static final EntityModelLayer MULE = EntityModelLayers.registerMain("mule");
    public static final EntityModelLayer MULE_BABY = EntityModelLayers.registerMain("mule_baby");
    public static final EntityModelLayer OAK_BOAT = EntityModelLayers.registerMain("boat/oak");
    public static final EntityModelLayer OAK_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/oak");
    public static final EntityModelLayer OCELOT = EntityModelLayers.registerMain("ocelot");
    public static final EntityModelLayer OCELOT_BABY = EntityModelLayers.registerMain("ocelot_baby");
    public static final EntityModelLayer PALE_OAK_BOAT = EntityModelLayers.registerMain("boat/pale_oak");
    public static final EntityModelLayer PALE_OAK_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/pale_oak");
    public static final EntityModelLayer PANDA = EntityModelLayers.registerMain("panda");
    public static final EntityModelLayer PANDA_BABY = EntityModelLayers.registerMain("panda_baby");
    public static final EntityModelLayer PARROT = EntityModelLayers.registerMain("parrot");
    public static final EntityModelLayer PHANTOM = EntityModelLayers.registerMain("phantom");
    public static final EntityModelLayer PIG = EntityModelLayers.registerMain("pig");
    public static final EntityModelLayer PIGLIN = EntityModelLayers.registerMain("piglin");
    public static final EntityModelLayer PIGLIN_BABY = EntityModelLayers.registerMain("piglin_baby");
    public static final EntityModelLayer PIGLIN_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin_baby");
    public static final EntityModelLayer PIGLIN_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin_baby");
    public static final EntityModelLayer PIGLIN_BRUTE = EntityModelLayers.registerMain("piglin_brute");
    public static final EntityModelLayer PIGLIN_BRUTE_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin_brute");
    public static final EntityModelLayer PIGLIN_BRUTE_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin_brute");
    public static final EntityModelLayer PIGLIN_HEAD = EntityModelLayers.registerMain("piglin_head");
    public static final EntityModelLayer PIGLIN_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin");
    public static final EntityModelLayer PIGLIN_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin");
    public static final EntityModelLayer PIG_BABY = EntityModelLayers.registerMain("pig_baby");
    public static final EntityModelLayer PIG_BABY_SADDLE = EntityModelLayers.register("pig_baby", "saddle");
    public static final EntityModelLayer PIG_SADDLE = EntityModelLayers.register("pig", "saddle");
    public static final EntityModelLayer PILLAGER = EntityModelLayers.registerMain("pillager");
    public static final EntityModelLayer PLAYER = EntityModelLayers.registerMain("player");
    public static final EntityModelLayer PLAYER_CAPE = EntityModelLayers.register("player", "cape");
    public static final EntityModelLayer PLAYER_EARS = EntityModelLayers.register("player", "ears");
    public static final EntityModelLayer PLAYER_HEAD = EntityModelLayers.registerMain("player_head");
    public static final EntityModelLayer PLAYER_INNER_ARMOR = EntityModelLayers.createInnerArmor("player");
    public static final EntityModelLayer PLAYER_OUTER_ARMOR = EntityModelLayers.createOuterArmor("player");
    public static final EntityModelLayer PLAYER_SLIM = EntityModelLayers.registerMain("player_slim");
    public static final EntityModelLayer PLAYER_SLIM_INNER_ARMOR = EntityModelLayers.createInnerArmor("player_slim");
    public static final EntityModelLayer PLAYER_SLIM_OUTER_ARMOR = EntityModelLayers.createOuterArmor("player_slim");
    public static final EntityModelLayer SPIN_ATTACK = EntityModelLayers.registerMain("spin_attack");
    public static final EntityModelLayer POLAR_BEAR = EntityModelLayers.registerMain("polar_bear");
    public static final EntityModelLayer POLAR_BEAR_BABY = EntityModelLayers.registerMain("polar_bear_baby");
    public static final EntityModelLayer PUFFERFISH_BIG = EntityModelLayers.registerMain("pufferfish_big");
    public static final EntityModelLayer PUFFERFISH_MEDIUM = EntityModelLayers.registerMain("pufferfish_medium");
    public static final EntityModelLayer PUFFERFISH_SMALL = EntityModelLayers.registerMain("pufferfish_small");
    public static final EntityModelLayer RABBIT = EntityModelLayers.registerMain("rabbit");
    public static final EntityModelLayer RABBIT_BABY = EntityModelLayers.registerMain("rabbit_baby");
    public static final EntityModelLayer RAVAGER = EntityModelLayers.registerMain("ravager");
    public static final EntityModelLayer SALMON = EntityModelLayers.registerMain("salmon");
    public static final EntityModelLayer SALMON_LARGE = EntityModelLayers.registerMain("salmon_large");
    public static final EntityModelLayer SALMON_SMALL = EntityModelLayers.registerMain("salmon_small");
    public static final EntityModelLayer SHEEP = EntityModelLayers.registerMain("sheep");
    public static final EntityModelLayer SHEEP_BABY = EntityModelLayers.registerMain("sheep_baby");
    public static final EntityModelLayer SHEEP_BABY_WOOL = EntityModelLayers.register("sheep_baby", "wool");
    public static final EntityModelLayer SHEEP_WOOL = EntityModelLayers.register("sheep", "wool");
    public static final EntityModelLayer SHIELD = EntityModelLayers.registerMain("shield");
    public static final EntityModelLayer SHULKER = EntityModelLayers.registerMain("shulker");
    public static final EntityModelLayer SHULKER_BOX = EntityModelLayers.registerMain("shulker_box");
    public static final EntityModelLayer SHULKER_BULLET = EntityModelLayers.registerMain("shulker_bullet");
    public static final EntityModelLayer SILVERFISH = EntityModelLayers.registerMain("silverfish");
    public static final EntityModelLayer SKELETON = EntityModelLayers.registerMain("skeleton");
    public static final EntityModelLayer SKELETON_HORSE = EntityModelLayers.registerMain("skeleton_horse");
    public static final EntityModelLayer SKELETON_HORSE_BABY = EntityModelLayers.registerMain("skeleton_horse_baby");
    public static final EntityModelLayer SKELETON_INNER_ARMOR = EntityModelLayers.createInnerArmor("skeleton");
    public static final EntityModelLayer SKELETON_OUTER_ARMOR = EntityModelLayers.createOuterArmor("skeleton");
    public static final EntityModelLayer SKELETON_SKULL = EntityModelLayers.registerMain("skeleton_skull");
    public static final EntityModelLayer SLIME = EntityModelLayers.registerMain("slime");
    public static final EntityModelLayer SLIME_OUTER = EntityModelLayers.register("slime", "outer");
    public static final EntityModelLayer SNIFFER = EntityModelLayers.registerMain("sniffer");
    public static final EntityModelLayer SNIFFER_BABY = EntityModelLayers.registerMain("sniffer_baby");
    public static final EntityModelLayer SNOW_GOLEM = EntityModelLayers.registerMain("snow_golem");
    public static final EntityModelLayer SPAWNER_MINECART = EntityModelLayers.registerMain("spawner_minecart");
    public static final EntityModelLayer SPIDER = EntityModelLayers.registerMain("spider");
    public static final EntityModelLayer SPRUCE_BOAT = EntityModelLayers.registerMain("boat/spruce");
    public static final EntityModelLayer SPRUCE_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/spruce");
    public static final EntityModelLayer SQUID = EntityModelLayers.registerMain("squid");
    public static final EntityModelLayer SQUID_BABY = EntityModelLayers.registerMain("squid_baby");
    public static final EntityModelLayer STRAY = EntityModelLayers.registerMain("stray");
    public static final EntityModelLayer STRAY_INNER_ARMOR = EntityModelLayers.createInnerArmor("stray");
    public static final EntityModelLayer STRAY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("stray");
    public static final EntityModelLayer STRAY_OUTER = EntityModelLayers.register("stray", "outer");
    public static final EntityModelLayer STRIDER = EntityModelLayers.registerMain("strider");
    public static final EntityModelLayer STRIDER_SADDLE = EntityModelLayers.register("strider", "saddle");
    public static final EntityModelLayer STRIDER_BABY = EntityModelLayers.registerMain("strider_baby");
    public static final EntityModelLayer STRIDER_BABY_SADDLE = EntityModelLayers.register("strider_baby", "saddle");
    public static final EntityModelLayer TADPOLE = EntityModelLayers.registerMain("tadpole");
    public static final EntityModelLayer TNT_MINECART = EntityModelLayers.registerMain("tnt_minecart");
    public static final EntityModelLayer TRADER_LLAMA = EntityModelLayers.registerMain("trader_llama");
    public static final EntityModelLayer TRADER_LLAMA_BABY = EntityModelLayers.registerMain("trader_llama_baby");
    public static final EntityModelLayer TRIDENT = EntityModelLayers.registerMain("trident");
    public static final EntityModelLayer TROPICAL_FISH_LARGE = EntityModelLayers.registerMain("tropical_fish_large");
    public static final EntityModelLayer TROPICAL_FISH_LARGE_PATTERN = EntityModelLayers.register("tropical_fish_large", "pattern");
    public static final EntityModelLayer TROPICAL_FISH_SMALL = EntityModelLayers.registerMain("tropical_fish_small");
    public static final EntityModelLayer TROPICAL_FISH_SMALL_PATTERN = EntityModelLayers.register("tropical_fish_small", "pattern");
    public static final EntityModelLayer TURTLE = EntityModelLayers.registerMain("turtle");
    public static final EntityModelLayer TURTLE_BABY = EntityModelLayers.registerMain("turtle_baby");
    public static final EntityModelLayer VEX = EntityModelLayers.registerMain("vex");
    public static final EntityModelLayer VILLAGER = EntityModelLayers.registerMain("villager");
    public static final EntityModelLayer VILLAGER_BABY = EntityModelLayers.registerMain("villager_baby");
    public static final EntityModelLayer VINDICATOR = EntityModelLayers.registerMain("vindicator");
    public static final EntityModelLayer WANDERING_TRADER = EntityModelLayers.registerMain("wandering_trader");
    public static final EntityModelLayer WARDEN = EntityModelLayers.registerMain("warden");
    public static final EntityModelLayer WIND_CHARGE = EntityModelLayers.registerMain("wind_charge");
    public static final EntityModelLayer WITCH = EntityModelLayers.registerMain("witch");
    public static final EntityModelLayer WITHER = EntityModelLayers.registerMain("wither");
    public static final EntityModelLayer WITHER_ARMOR = EntityModelLayers.register("wither", "armor");
    public static final EntityModelLayer WITHER_SKELETON = EntityModelLayers.registerMain("wither_skeleton");
    public static final EntityModelLayer WITHER_SKELETON_INNER_ARMOR = EntityModelLayers.createInnerArmor("wither_skeleton");
    public static final EntityModelLayer WITHER_SKELETON_OUTER_ARMOR = EntityModelLayers.createOuterArmor("wither_skeleton");
    public static final EntityModelLayer WITHER_SKELETON_SKULL = EntityModelLayers.registerMain("wither_skeleton_skull");
    public static final EntityModelLayer WITHER_SKULL = EntityModelLayers.registerMain("wither_skull");
    public static final EntityModelLayer WOLF = EntityModelLayers.registerMain("wolf");
    public static final EntityModelLayer WOLF_ARMOR = EntityModelLayers.registerMain("wolf_armor");
    public static final EntityModelLayer WOLF_BABY = EntityModelLayers.registerMain("wolf_baby");
    public static final EntityModelLayer WOLF_BABY_ARMOR = EntityModelLayers.registerMain("wolf_baby_armor");
    public static final EntityModelLayer ZOGLIN = EntityModelLayers.registerMain("zoglin");
    public static final EntityModelLayer ZOGLIN_BABY = EntityModelLayers.registerMain("zoglin_baby");
    public static final EntityModelLayer ZOMBIE = EntityModelLayers.registerMain("zombie");
    public static final EntityModelLayer ZOMBIE_BABY = EntityModelLayers.registerMain("zombie_baby");
    public static final EntityModelLayer ZOMBIE_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_baby");
    public static final EntityModelLayer ZOMBIE_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_baby");
    public static final EntityModelLayer ZOMBIE_HEAD = EntityModelLayers.registerMain("zombie_head");
    public static final EntityModelLayer ZOMBIE_HORSE = EntityModelLayers.registerMain("zombie_horse");
    public static final EntityModelLayer ZOMBIE_HORSE_BABY = EntityModelLayers.registerMain("zombie_horse_baby");
    public static final EntityModelLayer ZOMBIE_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie");
    public static final EntityModelLayer ZOMBIE_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie");
    public static final EntityModelLayer ZOMBIE_VILLAGER = EntityModelLayers.registerMain("zombie_villager");
    public static final EntityModelLayer ZOMBIE_VILLAGER_BABY = EntityModelLayers.registerMain("zombie_villager_baby");
    public static final EntityModelLayer ZOMBIE_VILLAGER_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_villager_baby");
    public static final EntityModelLayer ZOMBIE_VILLAGER_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_villager_baby");
    public static final EntityModelLayer ZOMBIE_VILLAGER_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_villager");
    public static final EntityModelLayer ZOMBIE_VILLAGER_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_villager");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN = EntityModelLayers.registerMain("zombified_piglin");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY = EntityModelLayers.registerMain("zombified_piglin_baby");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombified_piglin_baby");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombified_piglin_baby");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombified_piglin");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombified_piglin");

    private static EntityModelLayer registerMain(String id) {
        return EntityModelLayers.register(id, MAIN);
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer lv = EntityModelLayers.create(id, layer);
        if (!LAYERS.add(lv)) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf(lv));
        }
        return lv;
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.ofVanilla(id), layer);
    }

    private static EntityModelLayer createInnerArmor(String id) {
        return EntityModelLayers.register(id, "inner_armor");
    }

    private static EntityModelLayer createOuterArmor(String id) {
        return EntityModelLayers.register(id, "outer_armor");
    }

    public static EntityModelLayer createStandingSign(WoodType type) {
        return EntityModelLayers.create("sign/standing/" + type.name(), MAIN);
    }

    public static EntityModelLayer createWallSign(WoodType type) {
        return EntityModelLayers.create("sign/wall/" + type.name(), MAIN);
    }

    public static EntityModelLayer createHangingSign(WoodType type, HangingSignBlockEntityRenderer.AttachmentType attachmentType) {
        return EntityModelLayers.create("hanging_sign/" + type.name() + "/" + attachmentType.asString(), MAIN);
    }

    public static Stream<EntityModelLayer> getLayers() {
        return LAYERS.stream();
    }
}

