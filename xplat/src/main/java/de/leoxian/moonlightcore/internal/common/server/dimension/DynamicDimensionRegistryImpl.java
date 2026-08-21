package de.leoxian.moonlightcore.internal.common.server.dimension;

import de.leoxian.moonlightcore.common.network.PacketDistributor;
import de.leoxian.moonlightcore.common.server.dimension.DynamicDimensionRegistry;
import de.leoxian.moonlightcore.common.server.dimension.PlayerRemover;
import de.leoxian.moonlightcore.common.util.DynamicRegistryUtils;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CCreateDimension;
import de.leoxian.moonlightcore.mixin.accessor.ChunkMapAccessor;
import de.leoxian.moonlightcore.mixin.accessor.DistanceManagerAccessor;
import de.leoxian.moonlightcore.mixin.accessor.MinecraftServerAccessor;
import de.leoxian.moonlightcore.mixin.accessor.ServerChunkCacheAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DynamicDimensionRegistryImpl implements DynamicDimensionRegistry {
    private final MinecraftServer server;
    private final DynamicDimensionProvider dynamicDimensionProvider;
    private final Registry<DimensionType> dimensionTypeRegistry;
    private final Registry<LevelStem> stemRegistry;

    public DynamicDimensionRegistryImpl(MinecraftServer server) {
        this.server = server;
        this.dynamicDimensionProvider = (DynamicDimensionProvider) server;
        this.dimensionTypeRegistry = server.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE);
        this.stemRegistry = server.registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
    }

    @Override
    public ServerLevel createDynamicDimension(Identifier id, ChunkGenerator chunkGenerator, DimensionType dimensionType) {
        ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, id);
        if (!canCreateDimension(id)) return null;

        if (this.dimensionTypeRegistry.stream().anyMatch(t -> t == dimensionType)) {
            return null;
        }

        Holder.Reference<DimensionType> typeHolder = DynamicRegistryUtils.register(this.dimensionTypeRegistry, id, () -> dimensionType);
        LevelStem stem = new LevelStem(typeHolder, chunkGenerator);
        DynamicRegistryUtils.register(this.stemRegistry, id, () -> stem);

        final ServerLevel overworld = this.server.overworld();

        final MinecraftServerAccessor accessor = (MinecraftServerAccessor) server;
        final WorldData worldData = this.server.getWorldData();
        final DerivedLevelData data = new DerivedLevelData(worldData, worldData.overworldData());
        final ServerLevel level = new ServerLevel(
                this.server,
                accessor.getExecutor(),
                accessor.getStorageSource(),
                data,
                levelKey,
                stem,
                worldData.isDebugWorld(),
                BiomeManager.obfuscateSeed(this.server.getWorldGenSettings().options().seed()),
                List.of(),
                false
        );

        level.getChunkSource().setSimulationDistance(((DistanceManagerAccessor) ((ServerChunkCacheAccessor) overworld.getChunkSource()).getDistanceManager()).getSimulationDistance());
        level.getChunkSource().setViewDistance(((ChunkMapAccessor) overworld.getChunkSource().chunkMap).getServerViewDistance());
        level.setSpawnSettings(overworld.isSpawningMonsters());
        this.dynamicDimensionProvider.moonlightcore$registerLevel(level);
        PacketDistributor.sendToAllPlayers(new S2CCreateDimension(id, dimensionType));
        return level;
    }

    @Override
    public void unloadDynamicDimension(ServerLevel level, @Nullable PlayerRemover playerRemover) {
        this.dynamicDimensionProvider.moonlightcore$removeLevel(level.dimension(), playerRemover, false);
    }

    @Override
    public void deleteDynamicDimension(ServerLevel level, @Nullable PlayerRemover playerRemover) {
        this.dynamicDimensionProvider.moonlightcore$removeLevel(level.dimension(), playerRemover, true);
    }

    @Override
    public boolean anyDimensionExists(Identifier identifier) {
        return this.server.getLevel(ResourceKey.create(Registries.DIMENSION, identifier)) != null;
    }

    @Override
    public boolean canCreateDimension(Identifier identifier) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, identifier);
        return this.server.getLevel(key) == null;
    }

    @Override
    public boolean canDeleteDimension(Identifier identifier) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, identifier);
        ServerLevel level = this.server.getLevel(key);
        return level != null && key != Level.OVERWORLD && key != Level.NETHER && key != Level.END;
    }
}
