// TODO
//package de.leoxian.moonlightcore.common.server.dimension;
//
//import com.mojang.serialization.Lifecycle;
//import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
//import de.leoxian.moonlightcore.mixin.dimension.MinecraftServerAccessor;
//import de.leoxian.moonlightcore.mixin.dimension.WorldGenSettingsAccessor;
//import net.minecraft.core.MappedRegistry;
//import net.minecraft.core.RegistrationInfo;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.biome.BiomeManager;
//import net.minecraft.world.level.dimension.LevelStem;
//import net.minecraft.world.level.levelgen.WorldDimensions;
//import net.minecraft.world.level.storage.DerivedLevelData;
//
//import java.util.*;
//import java.util.function.Supplier;
//
//public class DynamicDimensionRegistry {
//    private static final RegistrationInfo REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.stable());
//
//    private static final Set<ResourceKey<Level>> VANILLA_KEYS = Set.of(
//            Level.END,
//            Level.OVERWORLD,
//            Level.NETHER);
//
//    private static final Set<ResourceKey<Level>> deferredLevelRemovals = new HashSet<>();
//
//    public static ServerLevel getOrCreate(final MinecraftServer server, final ResourceKey<Level> levelKey, final Supplier<LevelStem> dimensionFactory) {
//        Map<ResourceKey<Level>, ServerLevel> map = ((MinecraftServerAccessor) server).getLevels();
//        var level = map.get(levelKey);
//        return level == null ? createLevel(server, map, levelKey, dimensionFactory) : level;
//    }
//
//    private static ServerLevel createLevel(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> levelsMap, final ResourceKey<Level> levelKey, Supplier<LevelStem> dimensionFactory) {
//        final var dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, levelKey.identifier());
//        final var dimension = dimensionFactory.get();
//
//        final var executor = ((MinecraftServerAccessor) server).getExecutor();
//        final var levelStorageSource = ((MinecraftServerAccessor) server).getStorageSource();
//        final var worldData = server.getWorldData();
//        final var derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());
//
//        var worldGenSettings = server.getWorldGenSettings();
//        long serverSeed = worldGenSettings.options().seed();
//
//        var dimensionRegistry = server.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
//        if (!(dimensionRegistry instanceof MappedRegistry<LevelStem> mappedRegistry)) {
//            throw new IllegalStateException("Cannot register dimension '" + dimensionKey.identifier() + "': Dimension registry isn't writable");
//        }
//        XplatAbstraction.INSTANCE.getInternals().unfreezeRegistry(mappedRegistry);
//        mappedRegistry.register(dimensionKey,dimension, REGISTRATION_INFO);
//
//        var oldDimensions = worldGenSettings.dimensions();
//        Map<ResourceKey<LevelStem>, LevelStem> dimensionMap = new HashMap<>(oldDimensions.dimensions());
//        dimensionMap.put(dimensionKey, dimension);
//        var newDimensions = new WorldDimensions(dimensionMap);
//        ((WorldGenSettingsAccessor) (Object) worldGenSettings).setDimensions(newDimensions);
//        worldGenSettings.setDirty();
//
//        final var level = new ServerLevel(server, executor, levelStorageSource, derivedLevelData, levelKey, dimension, worldData.isDebugWorld(), BiomeManager.obfuscateSeed(serverSeed), List.of(), false);
//        level.getWorldBorder().setAbsoluteMaxSize(server.getAbsoluteMaxWorldSize());
//        server.getPlayerList().addWorldborderListener(level);
//        levelsMap.put(levelKey, level);
//        return level;
//    }
//}
