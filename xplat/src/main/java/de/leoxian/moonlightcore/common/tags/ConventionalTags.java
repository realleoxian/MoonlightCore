package de.leoxian.moonlightcore.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings("unused")
public final class ConventionalTags {
    public static final class Biomes {
        public static final TagKey<Biome> NO_DEFAULT_MONSTERS = register("no_default_monsters");

        public static final TagKey<Biome> HIDDEN_FROM_LOCATOR_SELECTION = register("hidden_from_locator_selection");

        public static final TagKey<Biome> IS_VOID = register("is_void");
        public static final TagKey<Biome> IS_OVERWORLD = register("is_overworld");
        public static final TagKey<Biome> IS_END = register("is_end");
        public static final TagKey<Biome> IS_NETHER = register("is_nether");

        public static final TagKey<Biome> IS_HOT = register("is_hot");
        public static final TagKey<Biome> IS_HOT_OVERWORLD = register("is_hot/overworld");
        public static final TagKey<Biome> IS_HOT_NETHER = register("is_hot/nether");
        public static final TagKey<Biome> IS_HOT_END = register("is_hot/end");

        public static final TagKey<Biome> IS_TEMPERATE = register("is_temperate");
        public static final TagKey<Biome> IS_TEMPERATE_OVERWORLD = register("is_temperate/overworld");
        public static final TagKey<Biome> IS_TEMPERATE_NETHER = register("is_temperate/nether");
        public static final TagKey<Biome> IS_TEMPERATE_END = register("is_temperate/end");

        public static final TagKey<Biome> IS_COLD = register("is_cold");
        public static final TagKey<Biome> IS_COLD_OVERWORLD = register("is_cold/overworld");
        public static final TagKey<Biome> IS_COLD_NETHER = register("is_cold/nether");
        public static final TagKey<Biome> IS_COLD_END = register("is_cold/end");

        public static final TagKey<Biome> IS_WET = register("is_wet");
        public static final TagKey<Biome> IS_WET_OVERWORLD = register("is_wet/overworld");
        public static final TagKey<Biome> IS_WET_NETHER = register("is_wet/nether");
        public static final TagKey<Biome> IS_WET_END = register("is_wet/end");

        public static final TagKey<Biome> IS_DRY = register("is_dry");
        public static final TagKey<Biome> IS_DRY_OVERWORLD = register("is_dry/overworld");
        public static final TagKey<Biome> IS_DRY_NETHER = register("is_dry/nether");
        public static final TagKey<Biome> IS_DRY_END = register("is_dry/end");

        public static final TagKey<Biome> IS_VEGETATION_SPARSE = register("is_sparse_vegetation");
        public static final TagKey<Biome> IS_VEGETATION_SPARSE_OVERWORLD = register("is_sparse_vegetation/overworld");
        public static final TagKey<Biome> IS_VEGETATION_SPARSE_NETHER = register("is_sparse_vegetation/nether");
        public static final TagKey<Biome> IS_VEGETATION_SPARSE_END = register("is_sparse_vegetation/end");

        public static final TagKey<Biome> IS_VEGETATION_DENSE = register("is_dense_vegetation");
        public static final TagKey<Biome> IS_VEGETATION_DENSE_OVERWORLD = register("is_dense_vegetation/overworld");
        public static final TagKey<Biome> IS_VEGETATION_DENSE_NETHER = register("is_dense_vegetation/nether");
        public static final TagKey<Biome> IS_VEGETATION_DENSE_END = register("is_dense_vegetation/end");

        public static final TagKey<Biome> PRIMARY_WOOD_TYPE = register("primary_wood_type");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_OAK = register("primary_wood_type/oak");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_BIRCH = register("primary_wood_type/birch");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_SPRUCE = register("primary_wood_type/spruce");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_JUNGLE = register("primary_wood_type/jungle");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_ACACIA = register("primary_wood_type/acacia");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_DARK_OAK = register("primary_wood_type/dark_oak");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_MANGROVE = register("primary_wood_type/mangrove");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_CHERRY = register("primary_wood_type/cherry");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_PALE_OAK = register("primary_wood_type/pale_oak");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_BAMBOO = register("primary_wood_type/bamboo");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_CRIMSON = register("primary_wood_type/crimson");
        public static final TagKey<Biome> PRIMARY_WOOD_TYPE_WARPED = register("primary_wood_type/warped");

        public static final TagKey<Biome> IS_CONIFEROUS_TREE = register("is_tree/coniferous");
        public static final TagKey<Biome> IS_SAVANNA_TREE = register("is_tree/savanna");
        public static final TagKey<Biome> IS_JUNGLE_TREE = register("is_tree/jungle");
        public static final TagKey<Biome> IS_DECIDUOUS_TREE = register("is_tree/deciduous");

        public static final TagKey<Biome> IS_PLAINS = register("is_plains");
        public static final TagKey<Biome> IS_SNOWY_PLAINS = register("is_snowy_plains");
        public static final TagKey<Biome> IS_FOREST = register("is_forest");
        public static final TagKey<Biome> IS_BIRCH_FOREST = register("is_birch_forest");
        public static final TagKey<Biome> IS_DARK_FOREST = register("is_dark_forest");
        public static final TagKey<Biome> IS_FLOWER_FOREST = register("is_flower_forest");
        public static final TagKey<Biome> IS_TAIGA = register("is_taiga");
        public static final TagKey<Biome> IS_OLD_GROWTH = register("is_old_growth");
        public static final TagKey<Biome> IS_HILL = register("is_hill");
        public static final TagKey<Biome> IS_WINDSWEPT = register("is_windswept");
        public static final TagKey<Biome> IS_JUNGLE = register("is_jungle");
        public static final TagKey<Biome> IS_SAVANNA = register("is_savanna");
        public static final TagKey<Biome> IS_SWAMP = register("is_swamp");
        public static final TagKey<Biome> IS_DESERT = register("is_desert");
        public static final TagKey<Biome> IS_BADLANDS = register("is_badlands");
        public static final TagKey<Biome> IS_BEACH = register("is_beach");
        public static final TagKey<Biome> IS_STONY_SHORES = register("is_stony_shores");
        public static final TagKey<Biome> IS_MUSHROOM = register("is_mushroom");

        public static final TagKey<Biome> IS_RIVER = register("is_river");
        public static final TagKey<Biome> IS_OCEAN = register("is_ocean");
        public static final TagKey<Biome> IS_DEEP_OCEAN = register("is_deep_ocean");
        public static final TagKey<Biome> IS_SHALLOW_OCEAN = register("is_shallow_ocean");

        public static final TagKey<Biome> IS_UNDERGROUND = register("is_underground");
        public static final TagKey<Biome> IS_CAVE = register("is_cave");

        public static final TagKey<Biome> IS_WASTELAND = register("is_wasteland");
        public static final TagKey<Biome> IS_DEAD = register("is_dead");
        public static final TagKey<Biome> IS_LUSH = register("is_lush");
        public static final TagKey<Biome> IS_MAGICAL = register("is_magical");
        public static final TagKey<Biome> IS_RARE = register("is_rare");
        public static final TagKey<Biome> IS_PLATEAU = register("is_plateau");
        public static final TagKey<Biome> IS_SPOOKY = register("is_spooky");
        public static final TagKey<Biome> IS_FLORAL = register("is_floral");
        public static final TagKey<Biome> IS_SANDY = register("is_sandy");
        public static final TagKey<Biome> IS_SNOWY = register("is_snowy");
        public static final TagKey<Biome> IS_ICY = register("is_icy");
        public static final TagKey<Biome> IS_AQUATIC = register("is_aquatic");
        public static final TagKey<Biome> IS_AQUATIC_ICY = register("is_aquatic_icy");

        public static final TagKey<Biome> IS_NETHER_FOREST = register("is_nether_forest");

        public static final TagKey<Biome> IS_OUTER_END_ISLAND = register("is_outer_end_island");

        private static TagKey<Biome> register(String key) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> STONES = register("stones");
        public static final TagKey<Block> COBBLESTONES = register("cobblestones");
        public static final TagKey<Block> DEEPSLATE_COBBLESTONES = register("cobblestones/deepslate");
        public static final TagKey<Block> INFESTED_COBBLESTONES = register("cobblestones/infested");
        public static final TagKey<Block> MOSSY_COBBLESTONES = register("cobblestones/mossy");
        public static final TagKey<Block> NORMAL_COBBLESTONES = register("cobblestones/normal");
        public static final TagKey<Block> NETHERRACKS = register("netherracks");
        public static final TagKey<Block> END_STONES = register("end_stones");
        public static final TagKey<Block> GRAVELS = register("gravels");
        public static final TagKey<Block> OBSIDIANS = register("obsidians");

        public static final TagKey<Block> NORMAL_OBSIDIANS = register("obsidians/normal");
        public static final TagKey<Block> CRYING_OBSIDIANS = register("obsidians/crying");

        public static final TagKey<Block> FROGLIGHTS = register("froglights");

        public static final TagKey<Block> ORES = register("ores");
        public static final TagKey<Block> COAL_ORES = register("ores/coal");
        public static final TagKey<Block> COPPER_ORES = register("ores/copper");
        public static final TagKey<Block> DIAMOND_ORES = register("ores/diamond");
        public static final TagKey<Block> EMERALD_ORES = register("ores/emerald");
        public static final TagKey<Block> GOLD_ORES = register("ores/gold");
        public static final TagKey<Block> IRON_ORES = register("ores/iron");
        public static final TagKey<Block> LAPIS_ORES = register("ores/lapis");
        public static final TagKey<Block> NETHERITE_SCRAP_ORES = register("ores/netherite_scrap");
        public static final TagKey<Block> QUARTZ_ORES = register("ores/quartz");
        public static final TagKey<Block> REDSTONE_ORES = register("ores/redstone");

        public static final TagKey<Block> BARRELS = register("barrels");
        public static final TagKey<Block> WOODEN_BARRELS = register("barrels/wooden");
        public static final TagKey<Block> BOOKSHELVES = register("bookshelves");
        public static final TagKey<Block> CHESTS = register("chests");
        public static final TagKey<Block> WOODEN_CHESTS = register("chests/wooden");
        public static final TagKey<Block> TRAPPED_CHESTS = register("chests/trapped");
        public static final TagKey<Block> ENDER_CHESTS = register("chests/ender");
        public static final TagKey<Block> GLASS_BLOCKS = register("glass_blocks");
        public static final TagKey<Block> GLASS_BLOCKS_COLORLESS = register("glass_blocks/colorless");

        public static final TagKey<Block> GLASS_BLOCKS_CHEAP = register("glass_blocks/cheap");
        public static final TagKey<Block> GLASS_BLOCKS_TINTED = register("glass_blocks/tinted");
        public static final TagKey<Block> GLASS_PANES = register("glass_panes");
        public static final TagKey<Block> GLASS_PANES_COLORLESS = register("glass_panes/colorless");
        public static final TagKey<Block> GLAZED_TERRACOTTAS = register("glazed_terracottas");
        public static final TagKey<Block> CONCRETES = register("concretes");

        public static final TagKey<Block> BUDDING_BLOCKS = register("budding_blocks");

        public static final TagKey<Block> BUDS = register("buds");

        public static final TagKey<Block> CLUSTERS = register("clusters");

        public static final TagKey<Block> VILLAGER_JOB_SITES = register("villager_job_sites");

        public static final TagKey<Block> SANDS = register("sands");
        public static final TagKey<Block> RED_SANDS = register("sands/red");
        public static final TagKey<Block> COLORLESS_SANDS = register("sands/colorless");

        public static final TagKey<Block> SMALL_FLOWERS = register("flowers/small");
        public static final TagKey<Block> TALL_FLOWERS = register("flowers/tall");
        public static final TagKey<Block> FLOWERS = register("flowers");

        public static final TagKey<Block> SANDSTONE_BLOCKS = register("sandstone/blocks");
        public static final TagKey<Block> SANDSTONE_SLABS = register("sandstone/slabs");
        public static final TagKey<Block> SANDSTONE_STAIRS = register("sandstone/stairs");
        public static final TagKey<Block> RED_SANDSTONE_BLOCKS = register("sandstone/red_blocks");
        public static final TagKey<Block> RED_SANDSTONE_SLABS = register("sandstone/red_slabs");
        public static final TagKey<Block> RED_SANDSTONE_STAIRS = register("sandstone/red_stairs");
        public static final TagKey<Block> UNCOLORED_SANDSTONE_BLOCKS = register("sandstone/uncolored_blocks");
        public static final TagKey<Block> UNCOLORED_SANDSTONE_SLABS = register("sandstone/uncolored_slabs");
        public static final TagKey<Block> UNCOLORED_SANDSTONE_STAIRS = register("sandstone/uncolored_stairs");

        public static final TagKey<Block> FENCES = register("fences");
        public static final TagKey<Block> WOODEN_FENCES = register("fences/wooden");
        public static final TagKey<Block> NETHER_BRICK_FENCES = register("fences/nether_brick");

        public static final TagKey<Block> FENCE_GATES = register("fence_gates");
        public static final TagKey<Block> WOODEN_FENCE_GATES = register("fence_gates/wooden");

        public static final TagKey<Block> BARS = register("bars");
        public static final TagKey<Block> IRON_BARS = register("bars/iron");
        public static final TagKey<Block> COPPER_BARS = register("bars/copper");

        public static final TagKey<Block> PUMPKINS = register("pumpkins");
        public static final TagKey<Block> NORMAL_PUMPKINS = register("pumpkins/normal");
        public static final TagKey<Block> CARVED_PUMPKINS = register("pumpkins/carved");
        public static final TagKey<Block> JACK_O_LANTERNS_PUMPKINS = register("pumpkins/jack_o_lanterns");

        public static final TagKey<Block> DYED = register("dyed");
        public static final TagKey<Block> BLACK_DYED = register("dyed/black");
        public static final TagKey<Block> BLUE_DYED = register("dyed/blue");
        public static final TagKey<Block> BROWN_DYED = register("dyed/brown");
        public static final TagKey<Block> CYAN_DYED = register("dyed/cyan");
        public static final TagKey<Block> GRAY_DYED = register("dyed/gray");
        public static final TagKey<Block> GREEN_DYED = register("dyed/green");
        public static final TagKey<Block> LIGHT_BLUE_DYED = register("dyed/light_blue");
        public static final TagKey<Block> LIGHT_GRAY_DYED = register("dyed/light_gray");
        public static final TagKey<Block> LIME_DYED = register("dyed/lime");
        public static final TagKey<Block> MAGENTA_DYED = register("dyed/magenta");
        public static final TagKey<Block> ORANGE_DYED = register("dyed/orange");
        public static final TagKey<Block> PINK_DYED = register("dyed/pink");
        public static final TagKey<Block> PURPLE_DYED = register("dyed/purple");
        public static final TagKey<Block> RED_DYED = register("dyed/red");
        public static final TagKey<Block> WHITE_DYED = register("dyed/white");
        public static final TagKey<Block> YELLOW_DYED = register("dyed/yellow");

        public static final TagKey<Block> STORAGE_BLOCKS = register("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_BONE_MEAL = register("storage_blocks/bone_meal");
        public static final TagKey<Block> STORAGE_BLOCKS_COAL = register("storage_blocks/coal");
        public static final TagKey<Block> STORAGE_BLOCKS_COPPER = register("storage_blocks/copper");
        public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = register("storage_blocks/diamond");
        public static final TagKey<Block> STORAGE_BLOCKS_DRIED_KELP = register("storage_blocks/dried_kelp");
        public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = register("storage_blocks/emerald");
        public static final TagKey<Block> STORAGE_BLOCKS_GOLD = register("storage_blocks/gold");
        public static final TagKey<Block> STORAGE_BLOCKS_IRON = register("storage_blocks/iron");
        public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = register("storage_blocks/lapis");
        public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = register("storage_blocks/netherite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = register("storage_blocks/raw_copper");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = register("storage_blocks/raw_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = register("storage_blocks/raw_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = register("storage_blocks/redstone");
        public static final TagKey<Block> STORAGE_BLOCKS_RESIN = register("storage_blocks/resin");
        public static final TagKey<Block> STORAGE_BLOCKS_SLIME = register("storage_blocks/slime");
        public static final TagKey<Block> STORAGE_BLOCKS_WHEAT = register("storage_blocks/wheat");

        public static final TagKey<Block> OVERWORLD_NATURAL_LOGS = register("natural_logs/overworld");
        public static final TagKey<Block> NETHER_NATURAL_LOGS = register("natural_logs/nether");
        public static final TagKey<Block> NATURAL_LOGS = register("natural_logs");

        public static final TagKey<Block> NATURAL_WOODS = register("natural_woods");

        public static final TagKey<Block> STRIPPED_LOGS = register("stripped_logs");
        public static final TagKey<Block> STRIPPED_WOODS = register("stripped_woods");

        public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = register("player_workstations/crafting_tables");
        public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = register("player_workstations/furnaces");

        public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = register("relocation_not_supported");

        public static final TagKey<Block> SKULLS = register("skulls");
        public static final TagKey<Block> ROPES = register("ropes");
        public static final TagKey<Block> CHAINS = register("chains");

        public static final TagKey<Block> HIDDEN_FROM_RECIPE_VIEWERS = register("hidden_from_recipe_viewers");

        public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = register("ore_bearing_ground/deepslate");
        public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = register("ore_bearing_ground/netherrack");
        public static final TagKey<Block> ORE_BEARING_GROUND_STONE = register("ore_bearing_ground/stone");

        public static final TagKey<Block> ORE_RATES_DENSE = register("ore_rates/dense");
        public static final TagKey<Block> ORE_RATES_SINGULAR = register("ore_rates/singular");
        public static final TagKey<Block> ORE_RATES_SPARSE = register("ore_rates/sparse");

        public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = register("ores_in_ground/deepslate");
        public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = register("ores_in_ground/netherrack");
        public static final TagKey<Block> ORES_IN_GROUND_STONE = register("ores_in_ground/stone");

        private static TagKey<Block> register(String key) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class Enchantments {
        public static final TagKey<Enchantment> INCREASE_BLOCK_DROPS = register("increase_block_drops");

        public static final TagKey<Enchantment> INCREASE_ENTITY_DROPS = register("increase_entity_drops");

        public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENTS = register("weapon_damage_enhancements");

        public static final TagKey<Enchantment> ENTITY_SPEED_ENHANCEMENTS = register("entity_speed_enhancements");

        public static final TagKey<Enchantment> ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS = register("entity_auxiliary_movement_enhancements");

        public static final TagKey<Enchantment> ENTITY_DEFENSE_ENHANCEMENTS = register("entity_defense_enhancements");

        public static final TagKey<Enchantment> HIDDEN_FROM_RECIPE_VIEWERS = register("hidden_from_recipe_viewers");

        private static TagKey<Enchantment> register(String key) {
            return TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class EntityTypes {
        public static final TagKey<EntityType<?>> BOSSES = register("bosses");

        public static final TagKey<EntityType<?>> MINECARTS = register("minecarts");
        public static final TagKey<EntityType<?>> BOATS = register("boats");

        public static final TagKey<EntityType<?>> ITEM_FRAMES = register("item_frames");

        public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = register("capturing_not_supported");

        public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = register("teleporting_not_supported");

        private static TagKey<EntityType<?>> register(String key) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class Fluids {
        public static final TagKey<Fluid> LAVA = register("lava");

        public static final TagKey<Fluid> WATER = register("water");

        public static final TagKey<Fluid> MILK = register("milk");

        public static final TagKey<Fluid> HONEY = register("honey");

        public static final TagKey<Fluid> GASEOUS = register("gaseous");

        public static final TagKey<Fluid> EXPERIENCE = register("experience");

        public static final TagKey<Fluid> POTION = register("potion");

        public static final TagKey<Fluid> SUSPICIOUS_STEW = register("suspicious_stew");

        public static final TagKey<Fluid> MUSHROOM_STEW = register("mushroom_stew");

        public static final TagKey<Fluid> RABBIT_STEW = register("rabbit_stew");

        public static final TagKey<Fluid> BEETROOT_SOUP = register("beetroot_soup");

        public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = register("hidden_from_recipe_viewers");

        private static TagKey<Fluid> register(String key) {
            return TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class Items {
        public static final TagKey<Item> STONES = register("stones");
        public static final TagKey<Item> COBBLESTONES = register("cobblestones");
        public static final TagKey<Item> DEEPSLATE_COBBLESTONES = register("cobblestones/deepslate");
        public static final TagKey<Item> INFESTED_COBBLESTONES = register("cobblestones/infested");
        public static final TagKey<Item> MOSSY_COBBLESTONES = register("cobblestones/mossy");
        public static final TagKey<Item> NORMAL_COBBLESTONES = register("cobblestones/normal");
        public static final TagKey<Item> NETHERRACKS = register("netherracks");
        public static final TagKey<Item> END_STONES = register("end_stones");
        public static final TagKey<Item> GRAVELS = register("gravels");
        public static final TagKey<Item> OBSIDIANS = register("obsidians");

        public static final TagKey<Item> NORMAL_OBSIDIANS = register("obsidians/normal");
        public static final TagKey<Item> CRYING_OBSIDIANS = register("obsidians/crying");

        public static final TagKey<Item> FROGLIGHTS = register("froglights");

        public static final TagKey<Item> TOOLS = register("tools");
        public static final TagKey<Item> SHEAR_TOOLS = register("tools/shear");
        public static final TagKey<Item> TRIDENT_TOOLS = register("tools/trident");
        public static final TagKey<Item> BOW_TOOLS = register("tools/bow");
        public static final TagKey<Item> CROSSBOW_TOOLS = register("tools/crossbow");
        public static final TagKey<Item> SHIELD_TOOLS = register("tools/shield");
        public static final TagKey<Item> FISHING_ROD_TOOLS = register("tools/fishing_rod");
        public static final TagKey<Item> BRUSH_TOOLS = register("tools/brush");
        public static final TagKey<Item> IGNITER_TOOLS = register("tools/igniter");
        public static final TagKey<Item> MACE_TOOLS = register("tools/mace");
        public static final TagKey<Item> WRENCH_TOOLS = register("tools/wrench");

        public static final TagKey<Item> ARMORS = register("armors");
        public static final TagKey<Item> HUMANOID_ARMORS = register("armors/humanoid");
        public static final TagKey<Item> HORSE_ARMORS = register("armors/horse");
        public static final TagKey<Item> NAUTILUS_ARMORS = register("armors/nautilus");
        public static final TagKey<Item> WOLF_ARMORS = register("armors/wolf");

        public static final TagKey<Item> ENCHANTABLES = register("enchantables");

        public static final TagKey<Item> BRICKS = register("bricks");
        public static final TagKey<Item> DUSTS = register("dusts");
        public static final TagKey<Item> CLUMPS = register("clumps");
        public static final TagKey<Item> GEMS = register("gems");
        public static final TagKey<Item> INGOTS = register("ingots");
        public static final TagKey<Item> NUGGETS = register("nuggets");
        public static final TagKey<Item> ORES = register("ores");
        public static final TagKey<Item> RAW_MATERIALS = register("raw_materials");

        public static final TagKey<Item> IRON_RAW_MATERIALS = register("raw_materials/iron");
        public static final TagKey<Item> GOLD_RAW_MATERIALS = register("raw_materials/gold");
        public static final TagKey<Item> COPPER_RAW_MATERIALS = register("raw_materials/copper");

        public static final TagKey<Item> NORMAL_BRICKS = register("bricks/normal");
        public static final TagKey<Item> NETHER_BRICKS = register("bricks/nether");
        public static final TagKey<Item> RESIN_BRICKS = register("bricks/resin");

        public static final TagKey<Item> IRON_INGOTS = register("ingots/iron");
        public static final TagKey<Item> GOLD_INGOTS = register("ingots/gold");
        public static final TagKey<Item> COPPER_INGOTS = register("ingots/copper");
        public static final TagKey<Item> NETHERITE_INGOTS = register("ingots/netherite");

        public static final TagKey<Item> COAL_ORES = register("ores/coal");
        public static final TagKey<Item> COPPER_ORES = register("ores/copper");
        public static final TagKey<Item> DIAMOND_ORES = register("ores/diamond");
        public static final TagKey<Item> EMERALD_ORES = register("ores/emerald");
        public static final TagKey<Item> GOLD_ORES = register("ores/gold");
        public static final TagKey<Item> IRON_ORES = register("ores/iron");
        public static final TagKey<Item> LAPIS_ORES = register("ores/lapis");
        public static final TagKey<Item> NETHERITE_SCRAP_ORES = register("ores/netherite_scrap");
        public static final TagKey<Item> QUARTZ_ORES = register("ores/quartz");
        public static final TagKey<Item> REDSTONE_ORES = register("ores/redstone");

        public static final TagKey<Item> QUARTZ_GEMS = register("gems/quartz");
        public static final TagKey<Item> LAPIS_GEMS = register("gems/lapis");
        public static final TagKey<Item> DIAMOND_GEMS = register("gems/diamond");
        public static final TagKey<Item> AMETHYST_GEMS = register("gems/amethyst");
        public static final TagKey<Item> EMERALD_GEMS = register("gems/emerald");
        public static final TagKey<Item> PRISMARINE_GEMS = register("gems/prismarine");

        public static final TagKey<Item> COPPER_NUGGETS = register("nuggets/copper");
        public static final TagKey<Item> IRON_NUGGETS = register("nuggets/iron");
        public static final TagKey<Item> GOLD_NUGGETS = register("nuggets/gold");

        public static final TagKey<Item> REDSTONE_DUSTS = register("dusts/redstone");
        public static final TagKey<Item> GLOWSTONE_DUSTS = register("dusts/glowstone");

        public static final TagKey<Item> RESIN_CLUMPS = register("clumps/resin");

        public static final TagKey<Item> POTIONS = register("potions");
        public static final TagKey<Item> BOTTLE_POTIONS = register("potions/bottle");

        public static final TagKey<Item> FOODS = register("foods");
        public static final TagKey<Item> ANIMAL_FOODS = register("animal_foods");
        public static final TagKey<Item> FRUIT_FOODS = register("foods/fruit");
        public static final TagKey<Item> VEGETABLE_FOODS = register("foods/vegetable");
        public static final TagKey<Item> BERRY_FOODS = register("foods/berry");
        public static final TagKey<Item> BREAD_FOODS = register("foods/bread");
        public static final TagKey<Item> COOKIE_FOODS = register("foods/cookie");
        public static final TagKey<Item> DOUGH_FOODS = register("foods/dough");
        public static final TagKey<Item> RAW_MEAT_FOODS = register("foods/raw_meat");
        public static final TagKey<Item> COOKED_MEAT_FOODS = register("foods/cooked_meat");
        public static final TagKey<Item> RAW_FISH_FOODS = register("foods/raw_fish");
        public static final TagKey<Item> COOKED_FISH_FOODS = register("foods/cooked_fish");
        public static final TagKey<Item> SOUP_FOODS = register("foods/soup");
        public static final TagKey<Item> CANDY_FOODS = register("foods/candy");
        public static final TagKey<Item> PIE_FOODS = register("foods/pie");
        public static final TagKey<Item> GOLDEN_FOODS = register("foods/golden");
        public static final TagKey<Item> EDIBLE_WHEN_PLACED_FOODS = register("foods/edible_when_placed");
        public static final TagKey<Item> FOOD_POISONING_FOODS = register("foods/food_poisoning");

        public static final TagKey<Item> DRINKS = register("drinks");
        public static final TagKey<Item> WATER_DRINKS = register("drinks/water");
        public static final TagKey<Item> WATERY_DRINKS = register("drinks/watery");
        public static final TagKey<Item> MILK_DRINKS = register("drinks/milk");
        public static final TagKey<Item> HONEY_DRINKS = register("drinks/honey");
        public static final TagKey<Item> MAGIC_DRINKS = register("drinks/magic");
        public static final TagKey<Item> OMINOUS_DRINKS = register("drinks/ominous");
        public static final TagKey<Item> JUICE_DRINKS = register("drinks/juice");

        public static final TagKey<Item> DRINK_CONTAINING_BUCKET = register("drink_containing/bucket");
        public static final TagKey<Item> DRINK_CONTAINING_BOTTLE = register("drink_containing/bottle");

        public static final TagKey<Item> BUCKETS = register("buckets");
        public static final TagKey<Item> EMPTY_BUCKETS = register("buckets/empty");
        public static final TagKey<Item> WATER_BUCKETS = register("buckets/water");
        public static final TagKey<Item> LAVA_BUCKETS = register("buckets/lava");
        public static final TagKey<Item> MILK_BUCKETS = register("buckets/milk");
        public static final TagKey<Item> POWDER_SNOW_BUCKETS = register("buckets/powder_snow");
        public static final TagKey<Item> ENTITY_WATER_BUCKETS = register("buckets/entity_water");

        public static final TagKey<Item> BARRELS = register("barrels");
        public static final TagKey<Item> WOODEN_BARRELS = register("barrels/wooden");

        public static final TagKey<Item> BOOKSHELVES = register("bookshelves");

        public static final TagKey<Item> CHESTS = register("chests");
        public static final TagKey<Item> WOODEN_CHESTS = register("chests/wooden");
        public static final TagKey<Item> TRAPPED_CHESTS = register("chests/trapped");
        public static final TagKey<Item> ENDER_CHESTS = register("chests/ender");

        public static final TagKey<Item> GLASS_BLOCKS = register("glass_blocks");
        public static final TagKey<Item> GLASS_BLOCKS_COLORLESS = register("glass_blocks/colorless");
        public static final TagKey<Item> GLASS_BLOCKS_CHEAP = register("glass_blocks/cheap");
        public static final TagKey<Item> GLASS_BLOCKS_TINTED = register("glass_blocks/tinted");

        public static final TagKey<Item> GLASS_PANES = register("glass_panes");
        public static final TagKey<Item> GLASS_PANES_COLORLESS = register("glass_panes/colorless");


        public static final TagKey<Item> SHULKER_BOXES = register("shulker_boxes");

        public static final TagKey<Item> GLAZED_TERRACOTTAS = register("glazed_terracottas");

        public static final TagKey<Item> CONCRETES = register("concretes");

        public static final TagKey<Item> CONCRETE_POWDERS = register("concrete_powders");

        public static final TagKey<Item> BUDDING_BLOCKS = register("budding_blocks");

        public static final TagKey<Item> BUDS = register("buds");

        public static final TagKey<Item> CLUSTERS = register("clusters");

        public static final TagKey<Item> VILLAGER_JOB_SITES = register("villager_job_sites");

        public static final TagKey<Item> SANDS = register("sands");
        public static final TagKey<Item> RED_SANDS = register("sands/red");
        public static final TagKey<Item> COLORLESS_SANDS = register("sands/colorless");

        public static final TagKey<Item> SANDSTONE_BLOCKS = register("sandstone/blocks");
        public static final TagKey<Item> SANDSTONE_SLABS = register("sandstone/slabs");
        public static final TagKey<Item> SANDSTONE_STAIRS = register("sandstone/stairs");
        public static final TagKey<Item> RED_SANDSTONE_BLOCKS = register("sandstone/red_blocks");
        public static final TagKey<Item> RED_SANDSTONE_SLABS = register("sandstone/red_slabs");
        public static final TagKey<Item> RED_SANDSTONE_STAIRS = register("sandstone/red_stairs");
        public static final TagKey<Item> UNCOLORED_SANDSTONE_BLOCKS = register("sandstone/uncolored_blocks");
        public static final TagKey<Item> UNCOLORED_SANDSTONE_SLABS = register("sandstone/uncolored_slabs");
        public static final TagKey<Item> UNCOLORED_SANDSTONE_STAIRS = register("sandstone/uncolored_stairs");

        public static final TagKey<Item> SMALL_FLOWERS = register("flowers/small");
        public static final TagKey<Item> TALL_FLOWERS = register("flowers/tall");
        public static final TagKey<Item> FLOWERS = register("flowers");

        public static final TagKey<Item> FENCES = register("fences");

        public static final TagKey<Item> WOODEN_FENCES = register("fences/wooden");
        public static final TagKey<Item> NETHER_BRICK_FENCES = register("fences/nether_brick");

        public static final TagKey<Item> FENCE_GATES = register("fence_gates");
        public static final TagKey<Item> WOODEN_FENCE_GATES = register("fence_gates/wooden");

        public static final TagKey<Item> BARS = register("bars");
        public static final TagKey<Item> IRON_BARS = register("bars/iron");
        public static final TagKey<Item> COPPER_BARS = register("bars/copper");

        public static final TagKey<Item> PUMPKINS = register("pumpkins");
        public static final TagKey<Item> NORMAL_PUMPKINS = register("pumpkins/normal");
        public static final TagKey<Item> CARVED_PUMPKINS = register("pumpkins/carved");
        public static final TagKey<Item> JACK_O_LANTERNS_PUMPKINS = register("pumpkins/jack_o_lanterns");

        public static final TagKey<Item> DYES = register("dyes");
        public static final TagKey<Item> BLACK_DYES = register("dyes/black");
        public static final TagKey<Item> BLUE_DYES = register("dyes/blue");
        public static final TagKey<Item> BROWN_DYES = register("dyes/brown");
        public static final TagKey<Item> CYAN_DYES = register("dyes/cyan");
        public static final TagKey<Item> GRAY_DYES = register("dyes/gray");
        public static final TagKey<Item> GREEN_DYES = register("dyes/green");
        public static final TagKey<Item> LIGHT_BLUE_DYES = register("dyes/light_blue");
        public static final TagKey<Item> LIGHT_GRAY_DYES = register("dyes/light_gray");
        public static final TagKey<Item> LIME_DYES = register("dyes/lime");
        public static final TagKey<Item> MAGENTA_DYES = register("dyes/magenta");
        public static final TagKey<Item> ORANGE_DYES = register("dyes/orange");
        public static final TagKey<Item> PINK_DYES = register("dyes/pink");
        public static final TagKey<Item> PURPLE_DYES = register("dyes/purple");
        public static final TagKey<Item> RED_DYES = register("dyes/red");
        public static final TagKey<Item> WHITE_DYES = register("dyes/white");
        public static final TagKey<Item> YELLOW_DYES = register("dyes/yellow");

        public static final TagKey<Item> DYED = register("dyed");
        public static final TagKey<Item> BLACK_DYED = register("dyed/black");
        public static final TagKey<Item> BLUE_DYED = register("dyed/blue");
        public static final TagKey<Item> BROWN_DYED = register("dyed/brown");
        public static final TagKey<Item> CYAN_DYED = register("dyed/cyan");
        public static final TagKey<Item> GRAY_DYED = register("dyed/gray");
        public static final TagKey<Item> GREEN_DYED = register("dyed/green");
        public static final TagKey<Item> LIGHT_BLUE_DYED = register("dyed/light_blue");
        public static final TagKey<Item> LIGHT_GRAY_DYED = register("dyed/light_gray");
        public static final TagKey<Item> LIME_DYED = register("dyed/lime");
        public static final TagKey<Item> MAGENTA_DYED = register("dyed/magenta");
        public static final TagKey<Item> ORANGE_DYED = register("dyed/orange");
        public static final TagKey<Item> PINK_DYED = register("dyed/pink");
        public static final TagKey<Item> PURPLE_DYED = register("dyed/purple");
        public static final TagKey<Item> RED_DYED = register("dyed/red");
        public static final TagKey<Item> WHITE_DYED = register("dyed/white");
        public static final TagKey<Item> YELLOW_DYED = register("dyed/yellow");

        public static final TagKey<Item> STORAGE_BLOCKS = register("storage_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_BONE_MEAL = register("storage_blocks/bone_meal");
        public static final TagKey<Item> STORAGE_BLOCKS_COAL = register("storage_blocks/coal");
        public static final TagKey<Item> STORAGE_BLOCKS_COPPER = register("storage_blocks/copper");
        public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = register("storage_blocks/diamond");
        public static final TagKey<Item> STORAGE_BLOCKS_DRIED_KELP = register("storage_blocks/dried_kelp");
        public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = register("storage_blocks/emerald");
        public static final TagKey<Item> STORAGE_BLOCKS_GOLD = register("storage_blocks/gold");
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = register("storage_blocks/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = register("storage_blocks/lapis");
        public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = register("storage_blocks/netherite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = register("storage_blocks/raw_copper");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = register("storage_blocks/raw_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = register("storage_blocks/raw_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = register("storage_blocks/redstone");
        public static final TagKey<Item> STORAGE_BLOCKS_RESIN = register("storage_blocks/resin");
        public static final TagKey<Item> STORAGE_BLOCKS_SLIME = register("storage_blocks/slime");
        public static final TagKey<Item> STORAGE_BLOCKS_WHEAT = register("storage_blocks/wheat");

        public static final TagKey<Item> OVERWORLD_NATURAL_LOGS = register("natural_logs/overworld");
        public static final TagKey<Item> NETHER_NATURAL_LOGS = register("natural_logs/nether");
        public static final TagKey<Item> NATURAL_LOGS = register("natural_logs");
        public static final TagKey<Item> NATURAL_WOODS = register("natural_woods");
        public static final TagKey<Item> STRIPPED_LOGS = register("stripped_logs");
        public static final TagKey<Item> STRIPPED_WOODS = register("stripped_woods");

        public static final TagKey<Item> CROPS = register("crops");
        public static final TagKey<Item> BEETROOT_CROPS = register("crops/beetroot");
        public static final TagKey<Item> CACTUS_CROPS = register("crops/cactus");
        public static final TagKey<Item> CARROT_CROPS = register("crops/carrot");
        public static final TagKey<Item> COCOA_BEAN_CROPS = register("crops/cocoa_bean");
        public static final TagKey<Item> MELON_CROPS = register("crops/melon");
        public static final TagKey<Item> NETHER_WART_CROPS = register("crops/nether_wart");
        public static final TagKey<Item> POTATO_CROPS = register("crops/potato");
        public static final TagKey<Item> PUMPKIN_CROPS = register("crops/pumpkin");
        public static final TagKey<Item> SUGAR_CANE_CROPS = register("crops/sugar_cane");
        public static final TagKey<Item> WHEAT_CROPS = register("crops/wheat");

        public static final TagKey<Item> SEEDS = register("seeds");
        public static final TagKey<Item> BEETROOT_SEEDS = register("seeds/beetroot");
        public static final TagKey<Item> MELON_SEEDS = register("seeds/melon");
        public static final TagKey<Item> PUMPKIN_SEEDS = register("seeds/pumpkin");
        public static final TagKey<Item> TORCHFLOWER_SEEDS = register("seeds/torchflower");
        public static final TagKey<Item> PITCHER_PLANT_SEEDS = register("seeds/pitcher_plant");
        public static final TagKey<Item> WHEAT_SEEDS = register("seeds/wheat");


        public static final TagKey<Item> PLAYER_WORKSTATIONS_CRAFTING_TABLES = register("player_workstations/crafting_tables");
        public static final TagKey<Item> PLAYER_WORKSTATIONS_FURNACES = register("player_workstations/furnaces");

        public static final TagKey<Item> STRINGS = register("strings");

        public static final TagKey<Item> LEATHERS = register("leathers");

        public static final TagKey<Item> BONES = register("bones");

        public static final TagKey<Item> EGGS = register("eggs");

        public static final TagKey<Item> FEATHERS = register("feathers");

        public static final TagKey<Item> GUNPOWDERS = register("gunpowders");

        public static final TagKey<Item> MUSHROOMS = register("mushrooms");

        public static final TagKey<Item> NETHER_STARS = register("nether_stars");

        public static final TagKey<Item> MUSIC_DISCS = register("music_discs");

        public static final TagKey<Item> RODS = register("rods");

        public static final TagKey<Item> WOODEN_RODS = register("rods/wooden");
        public static final TagKey<Item> BLAZE_RODS = register("rods/blaze");
        public static final TagKey<Item> BREEZE_RODS = register("rods/breeze");

        public static final TagKey<Item> ROPES = register("ropes");

        public static final TagKey<Item> CHAINS = register("chains");

        public static final TagKey<Item> ENDER_PEARLS = register("ender_pearls");

        public static final TagKey<Item> SLIME_BALLS = register("slime_balls");

        public static final TagKey<Item> FERTILIZERS = register("fertilizers");

        public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = register("hidden_from_recipe_viewers");

        public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE = register("ore_bearing_ground/deepslate");
        public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK = register("ore_bearing_ground/netherrack");
        public static final TagKey<Item> ORE_BEARING_GROUND_STONE = register("ore_bearing_ground/stone");

        public static final TagKey<Item> ORE_RATES_DENSE = register("ore_rates/dense");
        public static final TagKey<Item> ORE_RATES_SINGULAR = register("ore_rates/singular");
        public static final TagKey<Item> ORE_RATES_SPARSE = register("ore_rates/sparse");

        public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = register("ores_in_ground/deepslate");
        public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = register("ores_in_ground/netherrack");
        public static final TagKey<Item> ORES_IN_GROUND_STONE = register("ores_in_ground/stone");

        private static TagKey<Item> register(String key) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class Structures {
        public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = register("hidden_from_displayers");

        public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = register("hidden_from_locator_selection");

        private static TagKey<Structure> register(String key) {
            return TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    public static final class Potions {
        public static final TagKey<Potion> HIDDEN_FROM_RECIPE_VIEWERS = register("hidden_from_recipe_viewers");

        private static TagKey<Potion> register(String key) {
            return TagKey.create(Registries.POTION, Identifier.fromNamespaceAndPath("c", key));
        }
    }

    private ConventionalTags() {}
}
